package yi.master.business.localize.shanxi.service;

import yi.master.business.base.service.BaseService;
import yi.master.business.localize.shanxi.bean.WebScriptTask;

public interface WebScriptTaskService extends BaseService<WebScriptTask> {
	/**
	 * 根据guid查询测试任务
	 * @param guid
	 * @return
	 */
	WebScriptTask findByGuid(String guid);

}
