package yi.master.business.testconfig.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.testconfig.dao.TestConfigDao;

@Repository("testConfigDao")
public class TestConfigDaoImpl extends BaseDaoImpl<TestConfig> implements TestConfigDao{

	@Override
	public TestConfig getConfigByUserId(Integer userId) {
		String hql="From TestConfig t where t.userId= :userId";
		return (TestConfig) getSession().createQuery(hql).setInteger("userId",userId).uniqueResult();
	}

}
