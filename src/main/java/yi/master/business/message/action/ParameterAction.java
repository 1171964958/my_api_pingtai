package yi.master.business.message.action;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.service.InterfaceInfoService;
import yi.master.business.message.service.ParameterService;
import yi.master.constant.MessageKeys;
import yi.master.constant.ReturnCodeConsts;
import yi.master.coretest.message.parse.MessageParse;

/**
 * 接口自动化
 * 接口参数管理Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 *
 */

@Controller
@Scope("prototype")
public class ParameterAction extends BaseAction<Parameter> {

	private static final long serialVersionUID = 1L;
	
	private ParameterService parameterService;
	
	@Autowired
	public void setParameterService(ParameterService parameterService) {
		super.setBaseService(parameterService);
		this.parameterService = parameterService;
	}
	@Autowired
	private InterfaceInfoService interfaceInfoService;
	
	/**
	 * 通过入参json报文{paramJson}批量导入接口参数
	 */
	private String paramsJson;
	
	/**
	 * 参数对应的接口id
	 */
	private Integer interfaceId;
	
	/**
	 * 传入的报文类型
	 */
	private String messageType;
	
	/**
	 * 根据指定的interfaceId接口id来获取下面的所有参数
	 * @return
	 */
	public String getParams() {
		List<Parameter> ps = parameterService.findByInterfaceId(interfaceId);		
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("data", ps);
		
		return SUCCESS;
	}
	
	/**
	 * 删除指定接口下的入参接口信息
	 * @return
	 */
	public String delInterfaceParams() {
		parameterService.delByInterfaceId(interfaceId);
				
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
/*	*//**
	 * 根据传入的参数属性名称和属性值来更新指定参数的指定属性
	 *//*
	@Override
	public String edit() {
		parameterService.editProperty(id, attrName, attrValue);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}*/
	
	
	
	
	/**
	 * 根据传入的接口入参报文批量处理导入参数
	 * @return
	 */
	public String batchImportParams() {
		InterfaceInfo info = interfaceInfoService.get(interfaceId);	
		
		if (info == null) {
			jsonMap.put("msg", "不存在的接口信息，可能已被删除!");
			jsonMap.put("returnCode", ReturnCodeConsts.MISS_PARAM_CODE);
			return SUCCESS;
		}
		
		MessageParse parseUtil = MessageParse.getParseInstance(messageType);
		
		if (parseUtil == null) {
			jsonMap.put("msg", "无法解析此格式报文!");
			jsonMap.put("returnCode", ReturnCodeConsts.MISS_PARAM_CODE);
			return SUCCESS;
		}
		
		Set<Parameter> params = parseUtil.importMessageToParameter(paramsJson, info.getParameters());
		
		if (params == null) {
			jsonMap.put("returnCode", ReturnCodeConsts.INTERFACE_ILLEGAL_TYPE_CODE);
			jsonMap.put("msg", "不是指定格式的合法报文!");
			return SUCCESS;
		}		

		for (Parameter p:params) {
			p.setInterfaceInfo(info);
			parameterService.edit(p);
		}
		
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 添加或者修改之前，检查是否已存在
	 */
	@Override
	public String edit() {
		

		model.setPath(StringUtils.isEmpty(model.getPath()) ? MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH 
				: MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + "." + model.getPath());
		if (parameterService.checkRepeatParameter(model.getParameterId(), model.getParameterIdentify(), model.getPath(), 
				model.getType(), model.getInterfaceInfo().getInterfaceId()) != null) {
			jsonMap.put("msg", "该参数已存在!");
			jsonMap.put("returnCode", ReturnCodeConsts.NAME_EXIST_CODE);
			return SUCCESS;
		}

		return super.edit();
	}

	/***************************************GET-SET****************************************************/
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public void setParamsJson(String paramsJson) {
		this.paramsJson = paramsJson;
	}

	public void setInterfaceId(Integer interfaceId) {
		this.interfaceId = interfaceId;
	}

}
