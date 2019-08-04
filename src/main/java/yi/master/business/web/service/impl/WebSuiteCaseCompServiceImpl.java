package yi.master.business.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.web.bean.WebSuiteCaseComp;
import yi.master.business.web.dao.WebSuiteCaseCompDao;
import yi.master.business.web.service.WebSuiteCaseCompService;

@Service("webSuiteCaseCompService")
public class WebSuiteCaseCompServiceImpl extends BaseServiceImpl<WebSuiteCaseComp> implements WebSuiteCaseCompService {

	private WebSuiteCaseCompDao webSuiteCaseCompDao;
	
	@Autowired
	public void setWebSuiteCaseCompDao(WebSuiteCaseCompDao webSuiteCaseCompDao) {
		super.setBaseDao(webSuiteCaseCompDao);
		this.webSuiteCaseCompDao = webSuiteCaseCompDao;
	}
}
