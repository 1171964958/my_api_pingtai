package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestConfig;
import yi.master.business.web.dao.WebTestConfigDao;
import yi.master.business.web.service.WebTestConfigService;

@Service("webTestConfigService")
public class WebTestConfigServiceImpl extends BaseServiceImpl<WebTestConfig> implements WebTestConfigService { 

	private WebTestConfigDao webTestConfigDao;
	
	@Autowired
	public void setWebTestConfigDao(WebTestConfigDao webTestConfigDao) {
		super.setBaseDao(webTestConfigDao);
		this.webTestConfigDao = webTestConfigDao;
	}
}
