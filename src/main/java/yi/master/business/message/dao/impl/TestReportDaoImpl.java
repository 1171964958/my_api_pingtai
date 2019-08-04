package yi.master.business.message.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.TestReport;
import yi.master.business.message.dao.TestReportDao;

@Repository("testReportDao")
public class TestReportDaoImpl extends BaseDaoImpl<TestReport> implements TestReportDao{

	@Override
	public String isFinished(Integer reportId) {
		
		String hql = "select t.finishFlag from TestReport t where t.reportId=:reportId";
		return getSession().createQuery(hql).setInteger("reportId", reportId).uniqueResult().toString();
	}

	@Override
	public TestReport findByGuid(String guid) {
		String hql = "from TestReport t where t.guid=:guid";
		return (TestReport) getSession().createQuery(hql).setString("guid", guid).uniqueResult();
	}

	@Override
	public String getDetailsJson(Integer reportId) {
		
		String hql = "select t.detailsJson from TestReport t where t.reportId=:reportId";
		return (String) getSession().createQuery(hql).setInteger("reportId", reportId).uniqueResult();
	}

}
