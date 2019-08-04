package yi.master.business.advanced.action;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.advanced.bean.InterfaceMock;
import yi.master.business.advanced.bean.config.mock.MockRequestValidateConfig;
import yi.master.business.advanced.bean.config.mock.MockResponseConfig;
import yi.master.business.advanced.bean.config.mock.MockValidateRuleConfig;
import yi.master.business.advanced.service.InterfaceMockService;
import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.Parameter;
import yi.master.business.user.bean.User;
import yi.master.constant.MessageKeys;
import yi.master.constant.ReturnCodeConsts;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.coretest.message.test.mock.MockSocketServer;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@Scope("prototype")
public class InterfaceMockAction extends BaseAction<InterfaceMock> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(InterfaceMockAction.class);
	
	private InterfaceMockService interfaceMockService;
	
	private String message;
	
	@Autowired
	public void setInterfaceMockService(
			InterfaceMockService interfaceMockService) {
		super.setBaseService(interfaceMockService);
		this.interfaceMockService = interfaceMockService;
	}
	
	
	public String updateStatus() {
		interfaceMockService.updateStatus(model.getMockId(), model.getStatus());
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	@Override
	public String edit(){
		if (model.getMockId() == null) {
			model.setUser((User)FrameworkUtil.getSessionMap().get("user"));
			model.setRequestValidate(JSONObject.fromObject(new MockRequestValidateConfig()).toString());
			model.setResponseMock(JSONObject.fromObject(new MockResponseConfig()).toString());
			model.setMockId(interfaceMockService.save(model));
		} else {
			interfaceMockService.edit(model);
		}
		
		//开启Socket模拟服务
		MockSocketServer server = CacheUtil.getSocketServers().get(model.getMockId());
		
		if (server != null && ("1".equals(model.getStatus()) || !"socket".equalsIgnoreCase(model.getProtocolType()))) {
			server.stop();
		}
		
		if (server == null && "0".equals(model.getStatus()) && "socket".equalsIgnoreCase(model.getProtocolType())) {
			try {
				new MockSocketServer(model.getMockId());
			} catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
			}
		}
		
		jsonMap.put("object", model);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
				
		return SUCCESS;
	}
	
	/**
	 * 更新配置内容
	 * @return
	 */
	public String updateSetting (){
		String settingType = "";
		String settingValue = "";
		if (StringUtils.isNotBlank(model.getResponseMock())) {
			settingType = "responseMock";
			settingValue = model.getResponseMock();
		}
		if (StringUtils.isNotBlank(model.getRequestValidate())) {
			settingType = "requestValidate";
			settingValue = model.getRequestValidate();
		}
		interfaceMockService.updateSetting(model.getMockId(), settingType, settingValue);
		MockSocketServer thisSocket = CacheUtil.getSocketServers().get(model.getMockId());
		if (thisSocket != null) thisSocket.updateConfig(settingType, settingValue);
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	
	/**
	 * 解析入参报文成默认的验证规则
	 * @return
	 */
	public String parseMessageToConfig() {
		MessageParse parseUtil = MessageParse.getParseInstance(MessageParse.judgeType(message));
		Set<Parameter> parameters = parseUtil.importMessageToParameter(message, null);
		JSONArray rules = new JSONArray();
		for (Parameter param:parameters) {
			if (Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING + "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER 
					+ "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA
					, param.getType().toUpperCase())) {
				MockValidateRuleConfig rule = new MockValidateRuleConfig();
				rule.setName(param.getParameterIdentify());
				rule.setPath(param.getPath().replaceAll("TopRoot\\.*", ""));
				rule.setType(param.getType());
				rule.setValidateValue(param.getDefaultValue());
				rules.add(rule);
			}
		}
		
		setData("rules", rules.toString()).setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	/**
	 * 解析指定的示例报文成适合ztree的tree node
	 * @return
	 */
	public String parseMessageToNodes() {
		MessageParse parseUtil = MessageParse.getParseInstance(MessageParse.judgeType(message));
		Set<Parameter> params = parseUtil.importMessageToParameter(message, new HashSet<Parameter>());
		if (params == null) {
			setReturnInfo(ReturnCodeConsts.NO_RESULT_CODE, "尚不支持此类型的报文格式，请检查出报文格式!");
			return SUCCESS;
		}
		//自定义parmeterId
		int count = 1;
		for (Parameter p:params) {
			p.setParameterId(count++);
		}
		
		Object[] os = PracticalUtils.getParameterZtreeMap(params);
		
		if (os == null) {
			setReturnInfo(ReturnCodeConsts.NO_RESULT_CODE, "没有可用的参数,请检查!");
			return SUCCESS;
		}
		setData("data", os[0]).setData("rootPid", Integer.parseInt(os[1].toString())).setData("error", os[2].toString())
			.setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	@Override
	public void checkObjectName() {
		InterfaceMock mock = interfaceMockService.findByMockUrl(model.getMockUrl());
		checkNameFlag = (mock != null && !mock.getMockId().equals(model.getMockId())) ? "请求路径重复" : "true";
		
		if (model.getMockId() == null) {
			checkNameFlag = (mock == null) ? "true" : "请求路径重复";
		}
	}
	
	
	public void setMessage(String message) {
		this.message = message;
	}
	
}	
