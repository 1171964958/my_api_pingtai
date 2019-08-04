package yi.master.business.localize.shanxi.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.localize.shanxi.bean.WebScriptModule;
import yi.master.business.localize.shanxi.service.WebScriptModuleService;
import yi.master.business.user.bean.User;
import yi.master.constant.ReturnCodeConsts;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.cache.CacheUtil;

@Controller
@Scope("prototype")
public class WebScriptModuleAction extends BaseAction<WebScriptModule> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WebScriptModuleService webScriptModuleService;
	
	@Autowired
	public void setWebScriptModuleService(
			WebScriptModuleService webScriptModuleService) {
		super.setBaseService(webScriptModuleService);
		this.webScriptModuleService = webScriptModuleService;
	}

	@Override
	public String edit() {
		
		//检查是否存在指定的文件夹
		File f = new File(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_WEB_SCRIPT_MODULE_PATH) + File.separator + model.getFolderName());
		if (!f.exists() || !f.isDirectory()) {
			setReturnInfo(ReturnCodeConsts.ILLEGAL_HANDLE_CODE, "指定的模块文件夹不存在：" + f.getAbsolutePath());
			return SUCCESS;
		}
		
		if (StringUtils.isEmpty(model.getCreateUser())) {
			model.setCreateUser(((User) FrameworkUtil.getSessionMap().get("user")).getRealName());
		}
		return super.edit();
	}

	@Override
	public String del() {
		
		//删除也会同时删除文件夹
		model = webScriptModuleService.get(id);
		File f = new File(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_WEB_SCRIPT_MODULE_PATH) + File.separator + model.getFolderName());
		FileUtils.deleteQuietly(f);
		return super.del();
	}
	
	@Override
	public void checkObjectName() {
		WebScriptModule info = webScriptModuleService.findByModuleCode(model.getModuleCode());
		checkNameFlag = (info != null && !info.getModuleId().equals(model.getModuleId())) ? "重复的业务编码" : "true";
		
		if (model.getModuleId() == null) {
			checkNameFlag = (info == null) ? "true" : "重复的业务编码";
		}
	}
	
}
