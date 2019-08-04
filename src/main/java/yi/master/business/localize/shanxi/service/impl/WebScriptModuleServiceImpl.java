package yi.master.business.localize.shanxi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.localize.shanxi.bean.WebScriptModule;
import yi.master.business.localize.shanxi.dao.WebScriptModuleDao;
import yi.master.business.localize.shanxi.service.WebScriptModuleService;

@Service("webScriptModuleService")
public class WebScriptModuleServiceImpl extends BaseServiceImpl<WebScriptModule> implements WebScriptModuleService {
	
	private WebScriptModuleDao webScriptModuleDao;
	
	@Autowired
	public void setWebScriptModuleDao(WebScriptModuleDao webScriptModuleDao) {
		super.setBaseDao(webScriptModuleDao);
		this.webScriptModuleDao = webScriptModuleDao;
	}

	@Override
	public WebScriptModule findByModuleCode(String moduleCode) {
		
		return webScriptModuleDao.findByModuleCode(moduleCode);
	}
}
