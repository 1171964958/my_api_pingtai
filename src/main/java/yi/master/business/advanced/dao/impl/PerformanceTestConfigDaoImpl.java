package yi.master.business.advanced.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.advanced.bean.PerformanceTestConfig;
import yi.master.business.advanced.dao.PerformanceTestConfigDao;
import yi.master.business.base.dao.impl.BaseDaoImpl;

@Repository("performanceTestConfigDao")
public class PerformanceTestConfigDaoImpl extends BaseDaoImpl<PerformanceTestConfig> implements PerformanceTestConfigDao {

}
