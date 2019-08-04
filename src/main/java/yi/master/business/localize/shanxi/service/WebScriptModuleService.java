package yi.master.business.localize.shanxi.service;

import yi.master.business.base.service.BaseService;
import yi.master.business.localize.shanxi.bean.WebScriptModule;

public interface WebScriptModuleService extends BaseService<WebScriptModule> {
	WebScriptModule findByModuleCode(String moduleCode);
}
