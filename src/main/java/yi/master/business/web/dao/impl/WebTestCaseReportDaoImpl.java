package yi.master.business.web.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.web.bean.WebTestCaseReport;
import yi.master.business.web.dao.WebTestCaseReportDao;

@Repository("webTestCaseReportDao")
public class WebTestCaseReportDaoImpl extends BaseDaoImpl<WebTestCaseReport> implements WebTestCaseReportDao {

}
