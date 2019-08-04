package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestCaseReport;
import yi.master.business.web.dao.WebTestCaseReportDao;
import yi.master.business.web.service.WebTestCaseReportService;

@Service("webTestCaseReportService")
public class WebTestCaseReportServiceImpl extends BaseServiceImpl<WebTestCaseReport> implements WebTestCaseReportService {

	private WebTestCaseReportDao webTestCaseReportDao;
	
	@Autowired
	public void setWebTestCaseReportDao(WebTestCaseReportDao webTestCaseReportDao) {
		super.setBaseDao(webTestCaseReportDao);
		this.webTestCaseReportDao = webTestCaseReportDao;
	}
}
