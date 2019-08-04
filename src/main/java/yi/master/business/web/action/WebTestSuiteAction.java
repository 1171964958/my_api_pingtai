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
import yi.master.business.web.bean.WebSuiteCaseComp;
import yi.master.business.web.bean.WebTestCase;
import yi.master.business.web.bean.WebTestConfig;
import yi.master.business.web.bean.WebTestSuite;
import yi.master.business.web.service.WebSuiteCaseCompService;
import yi.master.business.web.service.WebTestCaseService;
import yi.master.business.web.service.WebTestConfigService;
import yi.master.business.web.service.WebTestSuiteService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class WebTestSuiteAction extends BaseAction<WebTestSuite> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(WebTestSuiteAction.class);
	
	private WebTestSuiteService webTestSuiteService;
	@Autowired
	private WebTestConfigService webTestConfigService;
	@Autowired
	private WebTestCaseService webTestCaseService;
	
	@Autowired
	private WebSuiteCaseCompService compService;
	
	//0-删除  1-添加
	private String mode;	
	private Integer compId;
	private Integer caseId;
	
	//测试集用例的配置选项
	private String groupName;
	private String skipFlag;
	private Integer execSeq;
	
	@Autowired
	public void setWebTestSuiteService(WebTestSuiteService webTestSuiteService) {
		super.setBaseService(webTestSuiteService);
		this.webTestSuiteService = webTestSuiteService;
	}
	
	@Override
	public String edit() {
		if (model.getSuiteId() == null) {
			WebTestConfig config = new WebTestConfig();
			config.setConfigId(webTestConfigService.save(config));
			model.getSuiteConfig();
			model.updateConfigJson();
			model.setCreateUser((User) FrameworkUtil.getSessionMap().get("user"));
			model.setTestConfig(config);			
		}
		super.edit();
		return SUCCESS;
	}
	
	/**
	 * 获取当前测试集下的所有测试用例
	 * @return
	 */
	public String listCase() {
		List<WebSuiteCaseComp> suiteCases = compService.findAll("testSuite.suiteId=" + model.getSuiteId());		
		List<WebTestCase> cases = new ArrayList<WebTestCase>();
		
		for (WebSuiteCaseComp c:suiteCases) {
			WebTestCase webcase = (WebTestCase) c.getTestCase().clone();
			webcase.setGroupName(c.getGroupName());
			webcase.setSkipFlag(c.getSkipFlag());
			webcase.setExecSeq(c.getExecSeq());
			webcase.setCompId(c.getCompId());
			cases.add(webcase);
		}		
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "").setData("data", cases);
		return SUCCESS;
	}
	
	/**
	 * 从测试用例集中删除用例或者增加用例
	 * @return
	 */
	public String opCase() {
		if ("0".equals(mode)) { //删除
			compService.delete(compId);
		}		
		if ("1".equals(mode)) {//添加
			WebSuiteCaseComp comp = new WebSuiteCaseComp();
			comp.setTestCase(webTestCaseService.get(caseId));
			comp.setTestSuite(model);
			compService.edit(comp);
		}
		
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}

	/**
	 * 更新测试集用例的配置
	 * @return
	 */
	public String updateCaseSetting() {
		WebSuiteCaseComp c = compService.get(compId);
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		if (c != null) {
			c.setExecSeq(execSeq);
			c.setSkipFlag(skipFlag);
			c.setGroupName(groupName);
			compService.edit(c);
		} else {
			setReturnInfo(ReturnCodeConsts.MISS_PARAM_CODE, "该对象不存在!");
		}
		
		return SUCCESS;
	}
	
	/**
	 * 变更浏览器类型
	 * @return
	 */
	public String changeBroswerType() {
		String broswerType = model.getBrowserType();
		model = webTestSuiteService.get(id);
		if (model != null && !StringUtils.equalsIgnoreCase(broswerType, model.getBrowserType())) {
			model.setBrowserType(broswerType);			
			webTestSuiteService.edit(model);
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
		model = webTestSuiteService.get(model.getSuiteId());
		if (model != null && !StringUtils.equalsIgnoreCase(configJson, model.getConfigJson())) {
			model.setConfigJson(configJson);
			webTestSuiteService.edit(model);
		}
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	/***************************************************************************************/
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void setCompId(Integer compId) {
		this.compId = compId;
	}
	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}
	
	public void setExecSeq(Integer execSeq) {
		this.execSeq = execSeq;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public void setSkipFlag(String skipFlag) {
		this.skipFlag = skipFlag;
	}
}
