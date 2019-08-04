package yi.master.business.message.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.dao.TestResultDao;
import yi.master.constant.MessageKeys;
import yi.master.util.PracticalUtils;

@Repository("testResultDao")
public class TestResultDaoImpl extends BaseDaoImpl<TestResult> implements TestResultDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<TestResult> listByReportId(Integer reportId) {
		
		String hql = "from TestResult t where t.testReport.reportId=:reportId";
		
		return getSession().createQuery(hql).setInteger("reportId", reportId).setCacheable(true).list();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int[] countProbeResultQuality(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		String hql = "select new list(count(*),t.qualityLevel) from TestResult t where t.interfaceProbe.probeId=:probeId"
				+ " and t.opTime between :startTime and :lastTime group by t.qualityLevel";
		List<List> list1 = getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("startTime", startTime).
				setTimestamp("lastTime", lastTime).setCacheable(true).list();
		int[] counts = new int[4];
		for (int i = 1;i < 5;i++) {
			for (List list:list1) {
				if (Integer.parseInt(list.get(1).toString()) == i) {
					counts[i - 1] = Integer.parseInt(list.get(0).toString());
					continue;
				}
			}
		}
		
		return counts;
	}
	
	@Override
	public Object[] countProbeResultStatusByDay(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		String hql = "select new list(year(t.opTime)||'-'||month(t.opTime)||'-'||day(t.opTime)||' 00:00:00',count(*)) from TestResult t where t.interfaceProbe.probeId=:probeId"
				+ " and t.opTime between :startTime and :lastTime and t.runStatus=:runStatus group by year(t.opTime),month(t.opTime),day(t.opTime) order by t.opTime";
		Query query = getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("startTime", startTime)
				.setTimestamp("lastTime", lastTime);
		Object[] counts = new Object[4];
		counts[0] = query.setString("runStatus", MessageKeys.TEST_RUN_STATUS_SUCCESS).setCacheable(true).list();
		counts[1] = query.setString("runStatus", MessageKeys.TEST_RUN_STATUS_FAIL).setCacheable(true).list();
		counts[2] = query.setString("runStatus", MessageKeys.TEST_RUN_STATUS_STOP).setCacheable(true).list();
		counts[3] = getSession().createQuery(hql.replace("and t.runStatus=:runStatus", "")).setInteger("probeId", probeId).setTimestamp("startTime", startTime)
				.setTimestamp("lastTime", lastTime).setCacheable(true).list();
		return counts;
	}

	@Override
	public Object[] countProbeResultStatusByHour(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		String hql = "select new list(year(t.opTime)||'-'||month(t.opTime)||'-'||day(t.opTime)||' '||hour(t.opTime)||':00:00',count(*)) from"
				+ " TestResult t where t.interfaceProbe.probeId=:probeId and t.opTime between :startTime and :lastTime and"
				+ " t.runStatus=:runStatus group by year(t.opTime),month(t.opTime),day(t.opTime),hour(t.opTime) order by t.opTime";
		Query query = getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("startTime", startTime)
				.setTimestamp("lastTime", lastTime);
		Object[] counts = new Object[4];
		counts[0] = query.setString("runStatus", MessageKeys.TEST_RUN_STATUS_SUCCESS).setCacheable(true).list();
		counts[1] = query.setString("runStatus", MessageKeys.TEST_RUN_STATUS_FAIL).setCacheable(true).list();
		counts[2] = query.setString("runStatus", MessageKeys.TEST_RUN_STATUS_STOP).setCacheable(true).list();
		counts[3] = getSession().createQuery(hql.replace("and t.runStatus=:runStatus", "")).setInteger("probeId", probeId).setTimestamp("startTime", startTime)
				.setTimestamp("lastTime", lastTime).setCacheable(true).list();
		return counts;
	}

	@Override
	public List[] countProbeResultResponseTime(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		String hql = "select new list(t.opTime,t.useTime) from TestResult t where t.interfaceProbe.probeId=:probeId and"
				+ " t.opTime between :startTime and :lastTime and t.runStatus!='2' order by t.opTime";
		List<List> list1 = getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("startTime", startTime).
				setTimestamp("lastTime", lastTime).setCacheable(true).list();
		ArrayList<String> datetimes = new ArrayList<String>();
		ArrayList<String> responseTimes = new ArrayList<String>();
		for (List list:list1) {
			datetimes.add(PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, (Timestamp)list.get(0)));
			responseTimes.add(list.get(1).toString());
		}
		return new ArrayList[]{datetimes, responseTimes};
	}

	@Override
	public String[] getReportSystemNames(Integer reportId) {
		
		String hql = "select new list(t.businessSystemName) from TestResult t where t.testReport.reportId=:reportId group by t.businessSystemName";
		List<List> list = getSession().createQuery(hql).setInteger("reportId", reportId).list();
		String[] names = new String[list.size()];
		for (int i = 0;i < names.length;i++) {
			names[i] = list.get(i).get(0).toString();
		}
		return names;
	}

}
