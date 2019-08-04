package yi.master.business.message.dao;

import yi.master.business.base.dao.BaseDao;
import yi.master.business.message.bean.TestReport;

public interface TestReportDao extends BaseDao<TestReport>{
	
	/**
	 * 查看指定得测试任务是否执行完成
	 * @param reportId
	 * @return
	 */
	String isFinished(Integer reportId);
	/**
	 * 根据guid查询
	 * @param guid
	 * @return
	 */
	TestReport findByGuid(String guid);
	
	String getDetailsJson(Integer reportId);

}
