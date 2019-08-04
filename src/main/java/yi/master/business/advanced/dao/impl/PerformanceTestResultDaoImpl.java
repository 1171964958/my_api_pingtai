package yi.master.business.advanced.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.advanced.bean.PerformanceTestResult;
import yi.master.business.advanced.dao.PerformanceTestResultDao;
import yi.master.business.base.dao.impl.BaseDaoImpl;

@Repository("performanceTestResultDao")
public class PerformanceTestResultDaoImpl extends BaseDaoImpl<PerformanceTestResult> implements PerformanceTestResultDao {

}
