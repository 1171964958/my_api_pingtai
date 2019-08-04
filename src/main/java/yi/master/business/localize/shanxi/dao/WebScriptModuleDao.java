package yi.master.business.localize.shanxi.dao;

import yi.master.business.base.dao.BaseDao;
import yi.master.business.localize.shanxi.bean.WebScriptModule;

public interface WebScriptModuleDao extends BaseDao<WebScriptModule> {
	
	WebScriptModule findByModuleCode(String moduleCode);
}
