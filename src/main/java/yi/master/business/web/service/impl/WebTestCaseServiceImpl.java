package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestCase;
import yi.master.business.web.dao.WebTestCaseDao;
import yi.master.business.web.service.WebTestCaseService;

@Service("webTestCaseService")
public class WebTestCaseServiceImpl extends BaseServiceImpl<WebTestCase> implements WebTestCaseService {

	private WebTestCaseDao webTestCaseDao;
	
	@Autowired
	public void setWebTestCaseDao(WebTestCaseDao webTestCaseDao) {
		super.setBaseDao(webTestCaseDao);
		this.webTestCaseDao = webTestCaseDao;
	}
}
