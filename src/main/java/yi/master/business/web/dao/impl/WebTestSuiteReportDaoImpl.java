package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestSuiteReport;
import yi.master.business.web.dao.WebTestSuiteReportDao;

@Repository("webTestSuiteReportDao")
public class WebTestSuiteReportDaoImpl extends BaseDaoImpl<WebTestSuiteReport> implements WebTestSuiteReportDao {

}
