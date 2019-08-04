package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestStep;
import yi.master.business.web.dao.WebTestStepDao;
import yi.master.business.web.service.WebTestStepService;

@Service("webTestStepService")
public class WebTestStepServiceImpl extends BaseServiceImpl<WebTestStep> implements WebTestStepService {

	private WebTestStepDao webTestStepDao;
	
	@Autowired
	public void setWebTestStepDao(WebTestStepDao webTestStepDao) {
		super.setBaseDao(webTestStepDao);
		this.webTestStepDao = webTestStepDao;
	}
}
