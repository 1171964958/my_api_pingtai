package yi.master.business.localize.shanxi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.localize.shanxi.bean.WebScriptTask;
import yi.master.business.localize.shanxi.dao.WebScriptTaskDao;
import yi.master.business.localize.shanxi.service.WebScriptTaskService;

@Service("webScriptTaskService")
public class WebScriptTaskServiceImpl extends BaseServiceImpl<WebScriptTask> implements WebScriptTaskService {
	
	private WebScriptTaskDao webScriptTaskDao;
	
	@Autowired
	public void setWebScriptTaskDao(WebScriptTaskDao webScriptTaskDao) {
		super.setBaseDao(webScriptTaskDao);
		this.webScriptTaskDao = webScriptTaskDao;
	}

	@Override
	public WebScriptTask findByGuid(String guid) {
		
		return webScriptTaskDao.findByGuid(guid);
	}
}
