package yi.master.business.localize.shanxi.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.localize.shanxi.bean.WebScriptTask;
import yi.master.business.localize.shanxi.dao.WebScriptTaskDao;

@Repository("webScriptTaskDao")
public class WebScriptTaskDaoImpl extends BaseDaoImpl<WebScriptTask> implements WebScriptTaskDao{

	@Override
	public WebScriptTask findByGuid(String guid) {
		
		String hql = "from WebScriptTask w where w.guid=:guid";
		return (WebScriptTask) getSession().createQuery(hql).setString("guid", guid).uniqueResult();
	}

}
