package yi.master.business.advanced.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.advanced.bean.PerformanceTestConfig;
import yi.master.business.advanced.dao.PerformanceTestConfigDao;
import yi.master.business.advanced.service.PerformanceTestConfigService;
import yi.master.business.base.service.impl.BaseServiceImpl;

@Service("performanceTestConfigService")
public class PerformanceTestConfigServiceImpl extends BaseServiceImpl<PerformanceTestConfig> implements PerformanceTestConfigService {
	
	private PerformanceTestConfigDao performanceTestConfigDao;
	
	@Autowired
	public void setPerformanceTestConfigDao(
			PerformanceTestConfigDao performanceTestConfigDao) {
		super.setBaseDao(performanceTestConfigDao);
		this.performanceTestConfigDao = performanceTestConfigDao;
	}
}
