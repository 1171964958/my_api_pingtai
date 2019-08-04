package yi.master.business.message.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.bean.SceneValidateRule;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.MessageService;
import yi.master.business.message.service.SceneValidateRuleService;
import yi.master.business.message.service.TestDataService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.util.PracticalUtils;
import yi.master.util.excel.ImportMessageScene;

/**
 * 报文场景Action
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.3.6
 */

@Controller
@Scope("prototype")
public class MessageSceneAction extends BaseAction<MessageScene>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer messageId;

	private MessageSceneService messageSceneService;
	@Autowired
	private TestSetService testSetService;
	@Autowired
	private TestDataService testDataService;
	@Autowired
	private GlobalVariableService globalVariableService;
	@Autowired
	private SceneValidateRuleService sceneValidateRuleService;
	@Autowired
	private MessageService messageService;
	
	private String path;
	private Integer variableId;
	private Integer setId;
	
	@SuppressWarnings("unused")
	private String mode;

	@Autowired
	public void setMessageSceneService(MessageSceneService messageSceneService) {
		super.setBaseService(messageSceneService);
		this.messageSceneService = messageSceneService;
	}
	
	
	/**
	 * 获取指定报文的入参节点树
	 * @return
	 */
	public String getRequestMsgJsonTree() {
		Message msg = messageSceneService.get(model.getMessageSceneId()).getMessage();
		
		MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
		String paramsJson = parseUtil.depacketizeMessageToString(msg.getComplexParameter(), null);
		Set<Parameter> params = parseUtil.importMessageToParameter(paramsJson, new HashSet<Parameter>());
		
		if (params == null) {
			jsonMap.put("msg", "报文格式不正确，请检查出入参报文!");
			jsonMap.put("returnCode", ReturnCodeConsts.NO_RESULT_CODE);
			return SUCCESS;
		}
		
		//自定义parmeterId
		int count = 1;
		for (Parameter p:params) {
			p.setParameterId(count++);
		}
		
		Object[] os = PracticalUtils.getParameterZtreeMap(params);
		
		if (os == null) {
			jsonMap.put("msg", "没有可用的参数,请检查!");
			jsonMap.put("returnCode", ReturnCodeConsts.NO_RESULT_CODE);
			return SUCCESS;
		}
		
		jsonMap.put("data", os[0]);
		jsonMap.put("rootPid", Integer.parseInt(os[1].toString()));
		jsonMap.put("error", os[2].toString());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);	
		return SUCCESS;
	}
	
	/**
	 * 获取指定报文的出参节点树
	 * @return
	 */
	public String getResponseMsgJsonTree() {
		String responseMsg = messageSceneService.get(model.getMessageSceneId()).getResponseExample();
		
		if (StringUtils.isBlank(responseMsg)) {
			jsonMap.put("msg", "该测试场景没有返回示例报文,请先设置!");
			jsonMap.put("returnCode", ReturnCodeConsts.NO_RESULT_CODE);
			return SUCCESS;
		}	
		
		MessageParse parseUtil = MessageParse.getParseInstance(messageSceneService.get(model.getMessageSceneId()).getMessage().getMessageType());
			
		Set<Parameter> params = parseUtil.judgeMessageType(responseMsg).importMessageToParameter(responseMsg, new HashSet<Parameter>());
		if (params == null) {
			jsonMap.put("msg", "尚不支持此类型的报文格式，请检查出报文格式!");
			jsonMap.put("returnCode", ReturnCodeConsts.NO_RESULT_CODE);
			return SUCCESS;
		}
		
		//自定义parmeterId
		int count = 1;
		for (Parameter p:params) {
			p.setParameterId(count++);
		}
		
		Object[] os = PracticalUtils.getParameterZtreeMap(params);
		
		if (os == null) {
			jsonMap.put("msg", "没有可用的参数,请检查!");
			jsonMap.put("returnCode", ReturnCodeConsts.NO_RESULT_CODE);
			return SUCCESS;
		}
		
		jsonMap.put("data", os[0]);
		jsonMap.put("rootPid", Integer.parseInt(os[1].toString()));
		jsonMap.put("error", os[2].toString());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);	
		return SUCCESS;
	}
	
	
	
	/**
	 * 更新返回报文示例
	 * @return
	 */
	public String updateResponseExample () {
		
		messageSceneService.updateResponseExample(model.getMessageSceneId(), model.getResponseExample());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	@Override
	public String[] prepareList() {
		
		if (messageId != null) {
			this.filterCondition = new String[]{"message.messageId=" + messageId};
		}
		return this.filterCondition;
	}	
	
	/**
	 * 从上传的Excel导入场景信息到数据库
	 * @return
	 */
	public String importFromExcel () {
		
		Message message = messageService.get(messageId);
		
		if (message == null) {
			jsonMap.put("msg", "报文信息不存在!");
			jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
			return SUCCESS;
		}
		
		Map<String, Object> result = ImportMessageScene.importToDB(path, message);
		
		jsonMap.put("result", result);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	@Override
	public String edit() {
		if (model.getMessageSceneId() == null) { //新增
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			model.setMessageSceneId(messageSceneService.save(model));
			//新增时默认该该场景添加一条默认数据		
			TestData defaultData = new TestData();
			defaultData.setDataDiscr("默认数据");
			defaultData.setStatus("0");
			defaultData.setMessageScene(model);
			defaultData.setParamsData("");	
			//defaultData.setSystems(model.getSystems());
			defaultData.setDefaultData("0");
			testDataService.edit(defaultData);
			
			//是否配置关联验证模板
			if (variableId != null) {
				GlobalVariable v = globalVariableService.get(variableId);
				SceneValidateRule rule = (SceneValidateRule) v.createSettingValue();
				rule.setMessageScene(model);
				sceneValidateRuleService.save(rule);								
			}			
		} else {
			messageSceneService.edit(model);
		}
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 变更验证规则
	 * @return
	 */
	public String changeValidateRule() {		
		messageSceneService.updateValidateFlag(model.getMessageSceneId(), model.getValidateRuleFlag());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 获取测试中需要用到的url和所有可用测试数据
	 * @return
	 */
	public String getTestObject () {
		
		model = messageSceneService.get(model.getMessageSceneId());		
		Message msg = model.getMessage();
		InterfaceInfo info = msg.getInterfaceInfo();
		//检查是否缺少关联测试环境信息
		boolean normal = true;
		String s = "";
		if (info.getSystems().size() == 0) {
			normal = false;
			s += "接口信息[" + info.getInterfaceName() + "]缺少关联的测试环境<br>";
		}
		
		if (StringUtils.isBlank(msg.getSystems())) {
			normal = false;
			s += "报文信息[" + msg.getMessageName() + "]缺少关联的测试环境<br>";
		}
		
		if (StringUtils.isBlank(model.getSystems())) {
			normal = false;
			s += "测试场景[" + model.getSceneName() + "]缺少关联的测试环境";
		}
		
		if (!normal) {
			jsonMap.put("msg", s);
			jsonMap.put("returnCode", ReturnCodeConsts.MISS_PARAM_CODE);
			return SUCCESS;
		}
		
		Map<String, Object> testObject = new HashMap<String, Object>();
		
		MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
		for (BusinessSystem system:msg.checkSystems(model.getSystems())) {
			Map<String, Object> object = new HashMap<String, Object>();	
			
			String requestUrl = "";
			if (StringUtils.isNotBlank(model.getRequestUrl())) requestUrl = model.getRequestUrl();
			if (StringUtils.isBlank(requestUrl) && StringUtils.isNotBlank(msg.getRequestUrl())) requestUrl = msg.getRequestUrl();
			if (StringUtils.isBlank(requestUrl)) requestUrl = info.getRequestUrlReal();	
			
			object.put("requestUrl", system.getReuqestUrl(requestUrl, system.getDefaultPath(), info.getInterfaceName()));
			List<TestData> datas = model.getEnabledTestDatas(5, String.valueOf(system.getSystemId()));
			for (TestData data:datas) {
				if (data.getDataJson() == null) {
					data.setDataJson(parseUtil.depacketizeMessageToString(msg.getComplexParameter(), data.getParamsData()));
				}				
			}
			object.put("requestData", datas);
			object.put("system", system);
			testObject.put(String.valueOf(system.getSystemId()), object);
		}
		
		
		jsonMap.put("testObject", testObject);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
		
	/**
	 * 获取指定测试集中没有测试数据的测试场景列表
	 * @return
	 */
	public String listNoDataScenes() {
		List<MessageScene> noDataScenes = new ArrayList<MessageScene>();		
		List<MessageScene> scenes = null;
		
		//全量
		if (setId == 0) {
			scenes = messageSceneService.findAll();
		//测试集	
		} else {
			scenes = messageSceneService.getBySetId(setId);
		}
		
		for(MessageScene ms:scenes){
			if(!ms.hasEnoughData(null)){
				noDataScenes.add(ms);
			}								
		}	
		
		jsonMap.put("data", noDataScenes);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/***************************************GET-SET************************************************/
	
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public void setSetId(Integer setId) {
		this.setId = setId;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
}
