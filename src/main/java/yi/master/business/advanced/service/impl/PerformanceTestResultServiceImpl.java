package yi.master.business.advanced.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.advanced.bean.PerformanceTestResult;
import yi.master.business.advanced.dao.PerformanceTestResultDao;
import yi.master.business.advanced.service.PerformanceTestResultService;
import yi.master.business.base.service.impl.BaseServiceImpl;

@Service("performanceTestResultService")
public class PerformanceTestResultServiceImpl extends BaseServiceImpl<PerformanceTestResult> implements PerformanceTestResultService {
	
	private PerformanceTestResultDao performanceTestResultDao;
	
	@Autowired
	public void setPerformanceTestResultDao(
			PerformanceTestResultDao performanceTestResultDao) {
		super.setBaseDao(performanceTestResultDao);
		this.performanceTestResultDao = performanceTestResultDao;
	}
}
