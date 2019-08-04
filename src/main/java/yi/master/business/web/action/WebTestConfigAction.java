package yi.master.business.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.web.bean.WebTestConfig;
import yi.master.business.web.service.WebTestConfigService;

@Controller
@Scope("prototype")
public class WebTestConfigAction extends BaseAction<WebTestConfig>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(WebTestConfigAction.class);
	
	private WebTestConfigService webTestConfigService;
	
	@Autowired
	public void setWebTestConfigService(WebTestConfigService webTestConfigService) {
		super.setBaseService(webTestConfigService);
		this.webTestConfigService = webTestConfigService;
	}

}
