package yi.master.business.web.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.user.bean.User;
import yi.master.business.web.bean.WebTestStep;
import yi.master.business.web.service.WebTestStepService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class WebTestStepAction extends BaseAction<WebTestStep> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(WebTestStepAction.class);
	
	private WebTestStepService webTestStepService;
	
	private Integer caseId;

	@Autowired
	public void setWebTestStepService(WebTestStepService webTestStepService) {
		super.setBaseService(webTestStepService);
		this.webTestStepService = webTestStepService;
	}
	
	@Override
	public String[] prepareList() {
		
		List<String> conditions = new ArrayList<String>();
		if (caseId != null) {
			conditions.add("webTestCase.caseId=" + caseId);
		}	
		
		this.filterCondition = conditions.toArray(new String[0]);
		return this.filterCondition;
	}
	
	
	/**
	 * 更新配置信息
	 * @return
	 */
	public String updateConfig(){
		String configJson = model.getConfigJson();
		model = webTestStepService.get(model.getStepId());
		
		if (model == null && StringUtils.isBlank(configJson)) {
			setReturnInfo(ReturnCodeConsts.NO_RESULT_CODE, "入参不完整或者不正确,请检查!");
			return SUCCESS;
		} 
		
		model.setConfigJson(configJson);
		webTestStepService.edit(model);
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	
	@Override
	public String edit() {
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		if (model.getElement().getElementId() == null) model.setElement(null);
		if (model.getSnippetCase().getCaseId() == null) model.setSnippetCase(null);
		if (model.getStepId() == null) {
			model.setCreateUser(user);
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));		
			model.getStepConfig();
			model.updateConfigJson();
		} else {
			model.logModify(user.getRealName(), webTestStepService.get(model.getStepId()));
		}
		super.edit();
		return SUCCESS;
	}
	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}
}
