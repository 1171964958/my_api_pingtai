package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebTestElement;
import yi.master.business.web.dao.WebTestElementDao;
import yi.master.business.web.service.WebTestElementService;

@Service("webTestElementService")
public class WebTestElementServiceImpl extends BaseServiceImpl<WebTestElement> implements WebTestElementService {
	
	private WebTestElementDao webTestElementDao;
	
	@Autowired
	public void setWebTestElementDao(WebTestElementDao webTestElementDao) {
		super.setBaseDao(webTestElementDao);
		this.webTestElementDao = webTestElementDao;
	}
}
