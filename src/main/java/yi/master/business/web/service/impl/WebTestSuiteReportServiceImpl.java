package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestSuiteReport;
import yi.master.business.web.dao.WebTestSuiteReportDao;
import yi.master.business.web.service.WebTestSuiteReportService;

@Service("webTestSuiteReportService")
public class WebTestSuiteReportServiceImpl extends BaseServiceImpl<WebTestSuiteReport> implements WebTestSuiteReportService {
	
	private WebTestSuiteReportDao webTestSuiteReportDao;
	
	@Autowired
	public void setWebTestSuiteReportDao(WebTestSuiteReportDao webTestSuiteReportDao) {
		super.setBaseDao(webTestSuiteReportDao);
		this.webTestSuiteReportDao = webTestSuiteReportDao;
	}
}
