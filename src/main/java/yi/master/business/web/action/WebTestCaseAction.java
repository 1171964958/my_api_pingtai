package yi.master.business.web.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.user.bean.User;
import yi.master.business.web.bean.WebTestCase;
import yi.master.business.web.service.WebTestCaseService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.constant.WebTestKeys;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class WebTestCaseAction extends BaseAction<WebTestCase> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(WebTestCaseAction.class);
	
	private WebTestCaseService webTestCaseService;
	
	@Autowired
	public void setWebTestCaseService(WebTestCaseService webTestCaseService) {
		super.setBaseService(webTestCaseService);
		this.webTestCaseService = webTestCaseService;
	}
	
	@Override
	public String[] prepareList() {
		
		List<String> conditions = new ArrayList<String>();
		if (StringUtils.isNotEmpty(model.getCaseType())) {
			conditions.add("caseType='" + model.getCaseType() + "'");			
		}
		if (model.getCaseId() != null) {
			conditions.add("caseId<>" + model.getCaseId());//用例片段不能为自身
		}
		
		this.filterCondition = conditions.toArray(new String[0]);
		return this.filterCondition;
	}
	
	
	@Override
	public String edit() {
		if (model.getCaseId() == null) {
			model.getCaseConfig();
			model.updateConfigJson();
			model.setCreateUser((User) FrameworkUtil.getSessionMap().get("user"));
		}		
		super.edit();
		return SUCCESS;
	}
	
	/**
	 * 变更浏览器类型
	 * @return
	 */
	public String changeBroswerType() {
		String broswerType = model.getBrowserType();
		model = webTestCaseService.get(id);
		if (model != null && !StringUtils.equalsIgnoreCase(broswerType, model.getBrowserType())) {
			model.setBrowserType(broswerType);
			webTestCaseService.edit(model);
		}
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}	
	
	/**
	 * 更新配置信息
	 * @return
	 */
	public String updateConfig() {
		String configJson = model.getConfigJson();
		model = webTestCaseService.get(model.getCaseId());
		if (model != null && !StringUtils.equalsIgnoreCase(configJson, model.getConfigJson())) {
			model.setConfigJson(configJson);
			webTestCaseService.edit(model);
		}
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
}
