package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestSuite;
import yi.master.business.web.dao.WebTestSuiteDao;
import yi.master.business.web.service.WebTestSuiteService;

@Service("webTestSuiteService")
public class WebTestSuiteServiceImpl extends BaseServiceImpl<WebTestSuite> implements WebTestSuiteService {

	private WebTestSuiteDao webTestSuiteDao;
	
	@Autowired
	public void setWebTestSuiteDao(WebTestSuiteDao webTestSuiteDao) {
		super.setBaseDao(webTestSuiteDao);
		this.webTestSuiteDao = webTestSuiteDao;
	}
}
