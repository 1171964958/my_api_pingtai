package yi.master.business.message.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.dao.TestResultDao;
import yi.master.business.message.service.TestResultService;

@Service("testResultService")
public class TestResultServiceImpl extends BaseServiceImpl<TestResult> implements TestResultService{
	
	private TestResultDao testResultDao;
	
	@Autowired
	public void setTestResultDao(TestResultDao testResultDao) {
		super.setBaseDao(testResultDao);
		this.testResultDao = testResultDao;
	}

	@Override
	public List<TestResult> listByReportId(Integer reportId) {
		
		return testResultDao.listByReportId(reportId);
	}

	@Override
	public int[] countProbeResultQuality(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		return testResultDao.countProbeResultQuality(probeId, startTime, lastTime);
	}

	@Override
	public Object[] countProbeResultStatusByDay(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		return testResultDao.countProbeResultStatusByDay(probeId, startTime, lastTime);
	}

	@Override
	public Object[] countProbeResultStatusByHour(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		return testResultDao.countProbeResultStatusByHour(probeId, startTime, lastTime);
	}

	@Override
	public List[] countProbeResultResponseTime(Integer probeId,
			Timestamp startTime, Timestamp lastTime) {
		
		return testResultDao.countProbeResultResponseTime(probeId, startTime, lastTime);
	}

	@Override
	public String[] getReportSystemNames(Integer reportId) {
		
		return testResultDao.getReportSystemNames(reportId);
	}

}
