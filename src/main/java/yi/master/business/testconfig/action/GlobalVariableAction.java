package yi.master.business.testconfig.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.business.user.bean.User;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20171129
 *
 */
@Controller("globalVariableAction")
@Scope("prototype")
public class GlobalVariableAction extends BaseAction<GlobalVariable> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GlobalVariableService globalVariableService;
	
	private Boolean foreceCreate;
	
	@Autowired
	public void setGlobalVariableService(
			GlobalVariableService globalVariableService) {
		super.setBaseService(globalVariableService);
		this.globalVariableService = globalVariableService;
	}


	@Override
	public String edit() {
		
		
		User user = (User)FrameworkUtil.getSessionMap().get("user");
		if (model.getVariableId() == null) {
			//新增
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			model.setUser(new User(user.getUserId()));
			model.setExpiryDate(new Timestamp(System.currentTimeMillis()));
			model.setUniqueScope("0");
			if (model.getValidityPeriod() == null) {
				model.setValidityPeriod(0);
			}
		}
		
		//验证key的唯一性
		if (GlobalVariable.ifHasKey(model.getVariableType())) {
			checkObjectName();
			if (StringUtils.isBlank(model.getKey()) || !"true".equals(checkNameFlag)) {
				
				jsonMap.put("msg", "无效或者" + checkNameFlag + ",请重试!");
				jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
				
				return SUCCESS;
			}
		}
		
		globalVariableService.edit(model);		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}

	@Override
	public String listAll () {
		List<GlobalVariable> variables = new ArrayList<GlobalVariable>();
		
		if (model.getVariableType() == null || "all".equalsIgnoreCase(model.getVariableType())) {
			variables = globalVariableService.findAll();
		} else {
			variables = globalVariableService.findByVariableType(model.getVariableType());
		}
		
		jsonMap.put("data", variables);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 更新指定全局变量的value值，不包括constant和timestamp类型的
	 * @return
	 */
	public String updateValue() {
		
		globalVariableService.updateValue(model.getVariableId(), model.getValue());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	

	/**
	 * 根据variableType和value生成返回变量
	 * @return
	 */
	public String createVariable() {
		model = globalVariableService.get(model.getVariableId());
		if (model == null) {
			setReturnInfo(ReturnCodeConsts.NO_RESULT_CODE, "该全局变量不存在！");
			return SUCCESS;
		}
		
		Object str = model.createSettingValue(foreceCreate == null ? false : foreceCreate);
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("msg", str);
		if (str == null) {
			jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
			jsonMap.put("msg", model.getCreateErrorInfo());
		}
		
		
		return SUCCESS;
	}
	
	/**
	 * 在同种类型中判断key值是否重复<br>
	 * 新增或者修改状态下均可用
	 */
	@Override
	public void checkObjectName() {
		GlobalVariable info = globalVariableService.findByKey(model.getKey());
		checkNameFlag = (info != null && !info.getVariableId().equals(model.getVariableId())) ? "重复的key" : "true";
		
		if (model.getVariableId() == null) {
			checkNameFlag = (info == null) ? "true" : "重复的key";
		}
	}
	
	public void setForeceCreate(Boolean foreceCreate) {
		this.foreceCreate = foreceCreate;
	}

}
