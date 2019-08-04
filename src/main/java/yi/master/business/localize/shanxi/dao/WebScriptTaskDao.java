package yi.master.business.localize.shanxi.dao;

import yi.master.business.base.dao.BaseDao;
import yi.master.business.localize.shanxi.bean.WebScriptTask;

public interface WebScriptTaskDao extends BaseDao<WebScriptTask> {
	/**
	 * 根据guid查询测试任务
	 * @param guid
	 * @return
	 */
	WebScriptTask findByGuid(String guid);
}
