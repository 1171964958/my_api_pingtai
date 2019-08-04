package yi.master.business.advanced.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.dao.InterfaceProbeDao;
import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.TestResult;
import yi.master.statement.vo.ProbeResultSynopsisView;
import yi.master.util.PracticalUtils;

@Repository("interfaceProbeDao")
public class InterfaceProbeDaoImpl extends BaseDaoImpl<InterfaceProbe> implements InterfaceProbeDao {

	@Override
	public void updateConfig(Integer probeId, String configJson) {
		
		String hql = "update InterfaceProbe i set i.probeConfigJson=:configJson where i.probeId=:probeId";
		getSession().createQuery(hql).setString("configJson", configJson).setInteger("probeId", probeId).executeUpdate();
	}

	@Override
	public List<InterfaceProbe> findRunTasks() {
		
		String hql = "from InterfaceProbe i where i.status!='0'";
		
		return getSession().createQuery(hql).setCacheable(true).list();
	}

	@Override
	public TestResult getLastResult(Integer probeId, Timestamp lastRunTime) {
		
		String hql = "from TestResult t where t.interfaceProbe.probeId=:probeId and t.opTime>=:lastRunTime order by t.opTime desc";
		return (TestResult) getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("lastRunTime", lastRunTime)
				.setMaxResults(1).uniqueResult();
	}

	@Override
	public List<TestResult> findProbeResults(Integer probeId,
			Timestamp startTime) {
		
		String hql = "from TestResult t where t.interfaceProbe.probeId=:probeId and t.opTime>=:startTime";
		return getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("startTime", startTime).setCacheable(true).list();
	}

	@Override
	public List<TestResult> listProbeResultsByTimeRange(Integer probeId,
			Timestamp startTime, Timestamp endTime) {
		
		String hql = "from TestResult t where t.interfaceProbe.probeId=:probeId and t.opTime>=:startTime and t.opTime<=:endTime";
		return getSession().createQuery(hql).setInteger("probeId", probeId).setTimestamp("startTime", startTime)
				.setTimestamp("endTime", endTime).setCacheable(true).list();
	}

	@Override
	public List<ProbeResultSynopsisView> listProbeBeforeResultInfo(int dateNum) {
		
		String hql = "select t.opTime as opTime,t.qualityLevel as qualityLevel,t.messageInfo as messageInfo,"
				+ "t.interfaceProbe.system.systemId as systemId,t.interfaceProbe.system.systemName as systemName,"
				+ "t.interfaceProbe.probeId as probeId from TestResult t where "
				+ "t.interfaceProbe!=null and t.interfaceProbe.status!='0' and t.opTime>=:beforeTime order by t.opTime";
		Query query = getSession().createQuery(hql).setTimestamp("beforeTime", PracticalUtils.getSevenDayTimeBefore(dateNum))
				.setResultTransformer(new AliasToBeanResultTransformer(ProbeResultSynopsisView.class));
		return query.list();
	}

}
