package yi.master.business.message.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.service.InterfaceProbeService;
import yi.master.business.message.bean.ComplexScene;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.service.ComplexSceneService;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.TestDataService;
import yi.master.business.message.service.TestResultService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.testconfig.service.BusinessSystemService;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.business.testconfig.service.TestConfigService;
import yi.master.business.user.bean.User;
import yi.master.business.user.service.UserService;
import yi.master.constant.MessageKeys;
import yi.master.constant.ReturnCodeConsts;
import yi.master.coretest.message.test.MessageAutoTest;
import yi.master.coretest.message.test.TestMessageScene;
import yi.master.util.PracticalUtils;
import yi.master.util.FrameworkUtil;
import yi.master.util.cache.CacheUtil;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * 接口自动化
 * 接口测试Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
@Scope("prototype")
public class AutoTestAction extends ActionSupport implements ModelDriven<TestConfig> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,Object> jsonMap = new HashMap<String,Object>();
	
	@Autowired
	private MessageSceneService messageSceneService;
	@Autowired
	private TestDataService testDataService;
	@Autowired
	private TestResultService testResultService;
	@Autowired
	private TestConfigService testConfigService;
	@Autowired
	private UserService userService;
	@Autowired
	private TestSetService testSetService;
	@Autowired
	private MessageAutoTest autoTest;
	@Autowired
	private InterfaceProbeService interfaceProbeService;
	@Autowired
	private BusinessSystemService businessSystemService;
	@Autowired
	private ComplexSceneService complexSceneService;
	@Autowired
	private GlobalVariableService globalVariableService;
	
	private Integer messageSceneId;
	
	private Integer dataId;
	
	private String requestUrl;
	
	private String requestMessage;
	
	private Integer setId;
	
	private TestConfig config = new TestConfig();	
	
	private Boolean autoTestFlag;
	
	private Integer probeId;
	
	private Integer systemId;
	
	private Integer id;
	
	
	/**
	 * 组合场景测试
	 * @return
	 */
	public String complexSceneTest(){
		ComplexScene complexScene = complexSceneService.get(id);
		
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		TestConfig config = testConfigService.getConfigByUserId(user.getUserId());
		if (config == null) {
			config = testConfigService.getConfigByUserId(0);
		}
		
		if (complexScene.getSceneNum() == 0) {
			jsonMap.put("returnCode", ReturnCodeConsts.AUTO_TEST_NO_SCENE_CODE);
			jsonMap.put("msg", "该组合场景没有可测试场景");
			return SUCCESS;
		}
		
		Object results = autoTest.singleTestComplexScene(autoTest.packageComplexRequestObject(complexScene, config), null);
		
		jsonMap.put("result", results);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 探测接口测试
	 * @return
	 */
	public String probeTest () {
		InterfaceProbe task = interfaceProbeService.get(probeId);
		Integer resultId = null;
		if (task != null) {
			//执行测试
			TestConfig config = testConfigService.getConfigByUserId(task.getUser().getUserId());
			if (config == null) {
				config = testConfigService.getConfigByUserId(0);
			}
			Set<TestMessageScene> testObjects = autoTest.packageRequestObject(task.getScene(), config, task.getSystem());
			
			if (testObjects.size() == 0) {				
				jsonMap.put("msg", "测试环境[" + task.getSystem().getSystemName() + "]不存在或者被禁用,请检查!");
				jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
				return SUCCESS;
			}
			TestMessageScene testObject = new ArrayList<TestMessageScene>(testObjects).get(0);
			
			TestResult result = autoTest.singleTest(testObject, null);
			
			result.setQualityLevel(task.getConfig().judgeLevel(result));
			//保存测试结果
			result.setInterfaceProbe(task);
			resultId = testResultService.save(result);
		}
		
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		jsonMap.put("resultId", resultId);
		return SUCCESS;
	}
	
	
	/**
	 * 单场景测试
	 * @return
	 */
	public String sceneTest() {
		TestData d = testDataService.get(dataId);	
		
		if (CacheUtil.checkLockedTestData(dataId)) {
			
			jsonMap.put("msg", "该条测试数据正在被使用，请稍后再操作!");
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			return SUCCESS;
		}
		
		if (d == null || "1".equals(d.getStatus())) {	
			
			jsonMap.put("msg", "测试数据不可用,请更换!");
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);			
			return SUCCESS;
		}
		
		
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		
		MessageScene scene = messageSceneService.get(messageSceneId);
		
		TestConfig config = testConfigService.getConfigByUserId(user.getUserId());
		
		if (config == null) {
			config = testConfigService.getConfigByUserId(0);
		}
		
		TestMessageScene testObject = new TestMessageScene(scene, requestUrl, PracticalUtils.replaceGlobalVariable(requestMessage, globalVariableService)
					, 0, false, config, PracticalUtils.jsonToMap(scene.getMessage().getCallParameter()));
		testObject.setBusinessSystem(businessSystemService.get(systemId));
		if ( MessageKeys.INTERFACE_TYPE_SL.equalsIgnoreCase(scene.getMessage().getInterfaceInfo().getInterfaceType())
					&& "0".equals(d.getStatus())) {
			//改变预占数据			
			CacheUtil.addLockedTestData(dataId);
			testObject.setDataId(dataId);
		}
				
		TestResult result = autoTest.singleTest(testObject, null);
		
		testResultService.save(result);		
		
		jsonMap.put("result", result);	
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 批量场景测试
	 * @return
	 */
	public String scenesTest() {
		
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		
		if (user == null) {
			user = userService.get(config.getUserId());
		}
		String mark = "";
		if (autoTestFlag != null) {
			mark = MessageKeys.QUARTZ_AUTO_TEST_REPORT_MARK;
		}
		
		int[] result = autoTest.batchTest(user, setId, mark, null);
		
		if (result == null) {
			jsonMap.put("msg", "没有可用的测试场景");
			jsonMap.put("returnCode", ReturnCodeConsts.AUTO_TEST_NO_SCENE_CODE);
			return SUCCESS;
		}
		
		jsonMap.put("reportId", result[0]);
		jsonMap.put("count", result[1]);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 获取当前用户的自动化测试配置
	 * <br>如果没有就按照全局配置复制一个
	 * @return
	 */
	public String getConfig () {
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		TestConfig config = testConfigService.getConfigByUserId(user.getUserId());
		
		if (config == null) {
			config = (TestConfig) testConfigService.getConfigByUserId(0).clone();
			config.setConfigId(null);
			config.setUserId(user.getUserId());
			testConfigService.save(config);
		}
		jsonMap.put("config", config);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 更新测试配置
	 * @return
	 */
	public String updateConfig () {
		testConfigService.edit(config);
		
		jsonMap.put("config", config);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 测试前检查测试场景数据是否足够
	 * @return
	 */
	public String checkHasData () {		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);

		List<MessageScene> scenes = null;
		//全量
		if (setId == 0) {
			scenes = messageSceneService.findAll();
		//测试集	
		} else {
			scenes = messageSceneService.getBySetId(setId);
		}				
		
		if (scenes.size() == 0) {
			jsonMap.put("count", 0);
			return SUCCESS;
		}
				
		int noDataCount = 0;
		
		for(MessageScene ms:scenes){
			if(!ms.hasEnoughData(null)){
				noDataCount++;
			}								
		}
		
		jsonMap.put("count", noDataCount);
		//jsonMap.put("data", noDataScenes);
		
		return SUCCESS;
	}
	
	/*********************************GET-SET**************************************************/
	public void setConfig(TestConfig config) {
		this.config = config;
	}
	
	public void setSetId(Integer setId) {
		this.setId = setId;
	}
	
	public void setMessageSceneId(Integer messageSceneId) {
		this.messageSceneId = messageSceneId;
	}
	
	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}
	
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}
	
	public Map<String, Object> getJsonMap() {
		return jsonMap;
	}

	@Override
	public TestConfig getModel() {
		
		return this.config;
	}
	
	public void setAutoTestFlag(Boolean autoTestFlag) {
		this.autoTestFlag = autoTestFlag;
	}
	
	public void setProbeId(Integer probeId) {
		this.probeId = probeId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
}
