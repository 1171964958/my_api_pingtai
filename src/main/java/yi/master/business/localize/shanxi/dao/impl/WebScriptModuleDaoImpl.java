package yi.master.business.localize.shanxi.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.localize.shanxi.bean.WebScriptModule;
import yi.master.business.localize.shanxi.dao.WebScriptModuleDao;

@Repository("webScriptModuleDao")
public class WebScriptModuleDaoImpl extends BaseDaoImpl<WebScriptModule> implements WebScriptModuleDao {

	@Override
	public WebScriptModule findByModuleCode(String moduleCode) {
		
		String hql = "from WebScriptModule m where m.moduleCode=:moduleCode";
		return (WebScriptModule) getSession().createQuery(hql).setString("moduleCode", moduleCode).uniqueResult();
	}

}
