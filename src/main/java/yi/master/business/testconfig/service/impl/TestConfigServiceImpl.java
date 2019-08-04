package yi.master.business.testconfig.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.testconfig.dao.TestConfigDao;
import yi.master.business.testconfig.service.TestConfigService;

@Service("testConfigService")
public class TestConfigServiceImpl extends BaseServiceImpl<TestConfig> implements TestConfigService{
	
	private TestConfigDao testConfigDao;
	
	@Autowired
	public void setTestConfigDao(TestConfigDao testConfigDao) {
		super.setBaseDao(testConfigDao);
		this.testConfigDao = testConfigDao;
	}
	
	@Override
	public TestConfig getConfigByUserId(Integer userId) {
		
		return testConfigDao.getConfigByUserId(userId);
	}

}
