package yi.master.coretest.message.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;
import yi.master.business.message.bean.ComplexScene;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.bean.TestReport;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.service.ComplexSceneService;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.TestDataService;
import yi.master.business.message.service.TestReportService;
import yi.master.business.message.service.TestResultService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.testconfig.service.BusinessSystemService;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.business.testconfig.service.TestConfigService;
import yi.master.business.user.bean.User;
import yi.master.business.user.service.UserService;
import yi.master.constant.MessageKeys;
import yi.master.constant.SystemConsts;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.coretest.message.process.MessageProcess;
import yi.master.coretest.message.protocol.TestClient;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;



/**
 * 
 * 接口自动化<br>
 * 测试工具类
 * @author xuwangcheng
 * @version 1.0.0.0,20170502
 *
 */

@Service
public class MessageAutoTest {
	
	private static final Logger LOGGER = Logger.getLogger(MessageAutoTest.class);
	
	@Autowired
	private MessageSceneService messageSceneService;
	@Autowired
	private TestDataService testDataService;
	@Autowired
	private TestResultService testResultService;
	@Autowired
	private TestConfigService testConfigService;
	@Autowired
	private TestSetService testSetService;
	@Autowired
	private UserService userService;
	@Autowired
	private TestReportService testReportService;
	@Autowired
	private MessageValidateResponse validateUtil;
	@Autowired
	private ComplexSceneService complexSceneService;
	@Autowired
	private GlobalVariableService globalVariableService;
	@Autowired
	private BusinessSystemService businessSystemService;
	
	/**
	 * 通过接口场景动态获取指定的值
	 * @param sceneId
	 * @param systemId
	 * @param valueExpression
	 * @return
	 * @throws Exception 
	 */
	public String dynamicInterfaceGetValue(String sceneId, String systemId, String valueExpression) throws Exception {
		
		//执行测试
		TestConfig config = testConfigService.getConfigByUserId(FrameworkUtil.getLoginUser().getUserId());
		if (config == null) {
			config = testConfigService.getConfigByUserId(0);
		}
		MessageScene scene = messageSceneService.get(Integer.valueOf(sceneId));
		if (scene == null) {
			throw new Exception("指定的测试场景不存在或者已被删除,请检查!");
		}
		
		BusinessSystem system = businessSystemService.get(Integer.valueOf(systemId));
		if (system == null) {
			throw new Exception("指定的测试环境不存在或者已被删除,请检查!");
		}
		
		Set<TestMessageScene> testObjects = packageRequestObject(scene, config, system);
		
		if (testObjects.size() == 0) {				
			throw new Exception("测试环境[" + system.getSystemName() + "]被禁用,请检查!");
		}
		TestMessageScene testObject = new ArrayList<TestMessageScene>(testObjects).get(0);
		
		TestResult result = singleTest(testObject, null);
		
		//获取出参结果
		String v = null;
		if (MessageKeys.MESSAGE_TYPE_JSON.equals(MessageParse.judgeType(valueExpression))) {
			//左右边界关联
			v = PracticalUtils.getValueByRelationKeyWord(PracticalUtils.jsonToMap(valueExpression), result.getResponseMessage());
		} else {
			//节点路径获取
			MessageParse parseUtil = MessageParse.judgeMessageType(result.getResponseMessage());
			v = parseUtil.getObjectByPath(parseUtil.parseMessageToSingleRow(result.getResponseMessage()), valueExpression);
		}
		
		if (v == null) {
			throw new Exception("出参路径或者关联配置有误，无法获取到指定的值！");
		}
		
		return v;
	}
	
	/**
	 * 单场景测试
	 * @param testScene 测试要素对象
	 * @param config 当前测试配置
	 * @return TestResult 测试结果详情
	 */
	public TestResult singleTest(TestMessageScene testScene, Object procotolClient) {
		TestResult result = new TestResult();
		MessageScene scene = testScene.getScene();
		Message msg = messageSceneService.getMessageOfScene(scene.getMessageSceneId());
		InterfaceInfo info = messageSceneService.getInterfaceOfScene(scene.getMessageSceneId());
		
		String messageInfo = info.getInterfaceName() + "," + msg.getMessageName() + "," + scene.getSceneName();		
		
		result.setMessageInfo(messageInfo);
		result.setOpTime(new Timestamp(System.currentTimeMillis()));
		result.setMessageScene(scene);
		result.setRequestUrl(testScene.getRequestUrl());
		
		result.setProtocolType(info.getInterfaceProtocol());
		result.setBusinessSystemName(testScene.getBusinessSystem().getSystemName());
		result.setMessageType(msg.getMessageType());
		
		LOGGER.debug("当前请求报文为：" + testScene.getRequestMessage());
		if (!PracticalUtils.isNormalString(testScene.getRequestMessage())) {
			result.setMark(MessageKeys.NO_ENOUGH_TEST_DATA_RESULT_MARK);
			result.setUseTime(0);
			result.setStatusCode("000");
			result.setRunStatus(MessageKeys.TEST_RUN_STATUS_STOP);
			return result;
		}
		
		//是否需要进行特殊处理
		MessageProcess processUtil = MessageProcess.getProcessInstance(msg.getProcessType());
		if (processUtil != null) testScene.setRequestMessage(processUtil.processRequestMessage(testScene.getRequestMessage(), msg.getProcessParameter()));
		
		result.setRequestMessage(testScene.getRequestMessage());
		//获取指定测试客户端
		TestClient client = TestClient.getTestClientInstance(info.getInterfaceProtocol().trim().toLowerCase());
		
		Map<String, String> responseMap = client.sendRequest(testScene, procotolClient);		
		
		String responseMessage = responseMap.get(MessageKeys.RESPONSE_MAP_PARAMETER_MESSAGE);
		if (processUtil != null) responseMessage = processUtil.processResponseMessage(responseMessage, msg.getProcessParameter());
		
		result.setUseTime(Integer.parseInt(responseMap.get(MessageKeys.RESPONSE_MAP_PARAMETER_USE_TIME)));
		result.setHeaders(responseMap.get(MessageKeys.RESPONSE_MAP_PARAMETER_HEADERS));
		
		MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
		
		result.setResponseMessage(parseUtil.messageFormatBeautify(responseMessage));		
		result.setStatusCode(responseMap.get(MessageKeys.RESPONSE_MAP_PARAMETER_STATUS_CODE));
		
		if ("false".equals(result.getStatusCode()) || !PracticalUtils.isNormalString(result.getResponseMessage())) {
			result.setRunStatus(MessageKeys.TEST_RUN_STATUS_STOP);						
			result.setMark(responseMap.get(MessageKeys.RESPONSE_MAP_PARAMETER_TEST_MARK));
			//解除数据预占
			CacheUtil.removeLockedTestData(testScene.getDataId());
			return result;
		}
				
		
		Map<String,String> map = validateUtil.validate(result.getResponseMessage(), testScene.getRequestMessage(), scene, msg.getMessageType());
		
		//变更数据状态
		if ("0".equals(map.get(MessageValidateResponse.VALIDATE_MAP_STATUS_KEY))) {
			result.setRunStatus(MessageKeys.TEST_RUN_STATUS_SUCCESS);	
			if (testScene.getDataId() != 0) {				
				testDataService.updateDataValue(testScene.getDataId(), "status", "1");
			}
		} else {
			result.setRunStatus(MessageKeys.TEST_RUN_STATUS_FAIL);
		}
		
		//解除数据预占
		CacheUtil.removeLockedTestData(testScene.getDataId());
		
		result.setMark(map.get(MessageValidateResponse.VALIDATE_MAP_MSG_KEY));
		
		return result;
	}
	
	
	
	
	/**
	 * 测试组合场景
	 * @param testScene
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object singleTestComplexScene (TestMessageScene testScene, TestReport report) {
		//该组合场景测试上下文保存的变量
		Map<String, String> saveVariables = new HashMap<String, String>();
		List<TestResult> results = new ArrayList<>();
		//组合场景测试备注
		StringBuilder complexMark = new StringBuilder();
				
		MessageParse parseUtil = null;
		boolean stopFlag = false; //停止标记
		boolean lastTestFlag = false;//测试最后一个的标记
		boolean allSuccessFlag = true;
		
		int lastSeqNum = 1;
		
		//获取httpclient,其他协议的暂时也走这个，但是不影响，后期需要针对不同协议的客户端做改动
		DefaultHttpClient procotolClient = null;
		if (testScene.isNewClient()) {
			procotolClient = (DefaultHttpClient) testScene.getTestClient().getTestClient();
		}
		
		for (TestMessageScene scene:testScene.getScenes()) {						
			if (stopFlag || scene == null || (lastTestFlag && !scene.getScene().getSequenceNum().equals(testScene.getScenes().size()))) { 
				complexMark.append("测试序号为[" +  ++lastSeqNum + "]" + ",跳过测试该场景!\n");
				continue;
			}
			
			lastSeqNum = scene.getScene().getSequenceNum();
			parseUtil = scene.getParseUtil();
			//替换上下文变量			
			for (Map.Entry<String, String> entry:scene.getScene().getConfig().getUseVariables().entrySet()) {
				String value = null;
				if (saveVariables.containsKey(entry.getValue())) {
					//如果有对应上下文替换变量的就替换掉,否则使用常量
					value = saveVariables.get(entry.getValue());
				} else {
					value = entry.getValue();					
				}	
				
				if (scene.getCallParameter() == null) {
					scene.setCallParameter(new HashMap<String, Object>());
					scene.getCallParameter().put(MessageKeys.HTTP_PARAMETER_HEADER, new HashMap<String, String>());										
				}
				if (scene.getCallParameter().get(MessageKeys.HTTP_PARAMETER_QUERYS) == null) scene.getCallParameter().put(MessageKeys.HTTP_PARAMETER_QUERYS, new HashMap<String, String>());
				
				//根据变量名来判断是替换请求头还是请求体还是query参数
				if (entry.getKey().startsWith("RequestHeader.")) {
					((Map<String, String>) scene.getCallParameter().get(MessageKeys.HTTP_PARAMETER_HEADER)).put(entry.getKey().substring(entry.getKey().indexOf(".") + 1)
							, value);
				} else if (entry.getKey().startsWith("Querys.")) {
					((Map<String, String>) scene.getCallParameter().get(MessageKeys.HTTP_PARAMETER_QUERYS)).put(entry.getKey().substring(entry.getKey().indexOf(".") + 1)
							, value);					
				} else {
					scene.setRequestMessage(scene.getRequestMessage().replace(MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_LEFT + 
							entry.getValue() + MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_RIGHT, value));
				}
				
			}
			boolean successFlag = false;
			int requestCount = 0;
			TestResult result = null;
			int maxRetryCount = scene.getScene().getConfig().getRetryCount();
			while (!successFlag && maxRetryCount >= requestCount ++) {

				result = singleTest(scene, procotolClient);
				
				//如果场景测试成功
				if (MessageKeys.TEST_RUN_STATUS_SUCCESS.equals(result.getRunStatus())) {
					successFlag = true;
					continue;
				}
				try {
					Thread.sleep(scene.getScene().getConfig().getIntervalTime());
				} catch (InterruptedException e) {
					e.printStackTrace();
					
				}
			}
			result.setMark("组合场景名 [" + testScene.getComplexScene().getComplexSceneName() + "] ,执行序号 [" + scene.getScene().getSequenceNum() + "] \n\n" + result.getMark());
			results.add(result);
			//测试成功要获取保存变量并设置数据状态
			if (successFlag) {
				//保存上下文变量
				for (Map.Entry<String, String> entry:scene.getScene().getConfig().getSaveVariables().entrySet()) {
					String str = null;
					//保存响应头中的变量
					if (entry.getKey().startsWith("ResponseHeader.") && StringUtils.isNotBlank(result.getHeaders())) {
						JSONObject header = JSONObject.fromObject(result.getHeaders());
						String headerKey = entry.getKey().substring(entry.getKey().indexOf(".") + 1);
						if (header.getJSONObject("ResponseHeader").has(headerKey)) {
							str = header.getJSONObject("ResponseHeader").getString(headerKey);
						}						
					} else {
						//保存body体中的变量
						str = parseUtil.judgeMessageType(result.getResponseMessage()).getObjectByPath(result.getResponseMessage(), entry.getKey());
					}
									
					if (StringUtils.isNotEmpty(str)) {
						saveVariables.put(entry.getValue(), str);
					}
				}								
			}			
			//测试不成功的处理
			if (!successFlag) {
				allSuccessFlag = false;
				switch (scene.getScene().getConfig().getErrorExecFlag()) {
				case "0"://退出组合场景的测试
					stopFlag = true;
					break;
				case "1"://继续执行下一个场景					
					break;
				case "2"://直接执行最后一个场景
					lastTestFlag = true;
					break;
				default:
					break;
				}
			}
			
			//一定时间间隔后执行下一个场景
			if (!stopFlag) {
				try {
					Thread.sleep(scene.getScene().getConfig().getIntervalTime());
				} catch (InterruptedException e) {
					LOGGER.warn("InterruptedException", e);
				}
			}									
		}	
		
		if(testScene.getTestClient() != null) testScene.getTestClient().putBackTestClient(procotolClient);
		
		//如果缺少场景(结果和场景数量不等)，表明有某些场景设定的测试环境在原本的测试场景中已经被删除了或者直接执行了最好一个场景，此时，组合场景必须为测试失败状态并添加备注
		if (results.size() != testScene.getScenes().size()) {
			allSuccessFlag = false;
		}
		
		//SuccessFlag=0代表要求所有场景必须测试成功	
		//SuccessFlag=2代表将每一个测试场景作单独处理
		if ("0".equals(testScene.getComplexScene().getSuccessFlag())) {
			TestResult complexResult = new TestResult();
			complexResult.setMessageInfo(testScene.getComplexScene().getComplexSceneName() + ",组合场景,组合场景");
			complexResult.setTestReport(report);
			complexResult.setOpTime(new Timestamp(System.currentTimeMillis()));	
			complexResult.setMark(complexMark.toString());
			complexResult.setUseTime(0);
			complexResult.setProtocolType("FIXED");
			complexResult.setStatusCode("000");			
			complexResult.setRunStatus(allSuccessFlag ? MessageKeys.TEST_RUN_STATUS_SUCCESS : MessageKeys.TEST_RUN_STATUS_FAIL);
			complexResult.setComplexSceneResults(new TreeSet<TestResult>(results));
			complexResult.setRequestUrl("");
			complexResult.setRequestMessage("");
			complexResult.setResponseMessage("");
			testResultService.save(complexResult);
			
			return complexResult;
		}
		
		if ("2".equals(testScene.getComplexScene().getSuccessFlag())) {
			for (TestResult result:results) {
				result.setTestReport(report);				
				testResultService.save(result);
			}
			return results;
		}
		
		return null;
	}
	
	/**
	 * 批量测试
	 * @param user
	 * @param setId
	 * @param autoTestFlag 是否为系统自动化测试
	 * @return
	 */
	public int[] batchTest (User user, Integer setId, String testMark, String guid) {		
		
		List<MessageScene> scenes = null;
		List<ComplexScene> complexScenes  = null;
		TestSet set = null;
				
		//全量
		if (setId == 0) {
			scenes = messageSceneService.findAll();
			complexScenes = complexSceneService.findAll();
		//测试集	
		} else {
			scenes = messageSceneService.getBySetId(setId);
			complexScenes = complexSceneService.listComplexScenesBySetId(setId);
			set = testSetService.get(setId);			
		}				
		
		if (scenes.size() == 0 && complexScenes.size() == 0) {
			return null;
		}
		
		//选择测试配置
		TestConfig config1 = testConfigService.getConfigByUserId(user.getUserId());
		
		if (set != null && set.getConfig() != null) {
			config1 = set.getConfig();
		}
		
		
		final TestConfig config = config1;
		
		//测试报告
		final TestReport report = new TestReport();
		report.setUser(user);
		report.setFinishFlag("N");
		report.setTestMode(String.valueOf(setId));
		report.setCreateTime(new Timestamp(System.currentTimeMillis()));		
		report.setMark(testMark);
		report.setGuid(guid);
		report.setReportId(testReportService.save(report));
		
		final Object lock = new Object();
		
		/**
		 * 所有装配好的测试场景列表，包含组合场景
		 */
		final List<TestMessageScene> testObjects = new ArrayList<TestMessageScene>();
		
		//组装单独的测试场景元素对象
		for (MessageScene scene:scenes) {							
			testObjects.addAll(packageRequestObject(scene, config, null));
		}
		//场景数量 分别为finishCount和totalCount
		final int[] count = new int[]{0, testObjects.size()};
		
		//组装组合场景中的测试场景				
		for (ComplexScene s:complexScenes) {
			TestMessageScene testScene = packageComplexRequestObject(s, config);
			count[1] = count[1] + testScene.getTestCount();
			testObjects.add(testScene);
		}
		
		//筛选出置顶测试场景
		if (StringUtils.isNotBlank(config.getTopScenes())) {
			String[] topSceneIds = config.getTopScenes().split(",|，");
			int countNum = 1;		
			for (int i = topSceneIds.length - 1;i >= 0;i-- ) {
				if (StringUtils.isNumeric(topSceneIds[i].trim())) {
					countNum = findTestObjectBySceneId(Integer.valueOf(topSceneIds[i].trim()), testObjects, countNum);
				}
			}
		}
		
		//排序
		Collections.sort(testObjects, new Comparator<TestMessageScene>() {
			@Override
			public int compare(TestMessageScene o1, TestMessageScene o2) {
								
				return o2.getPriority() - o1.getPriority();
			}
		});
		final boolean[] finishFlag = new boolean[]{false};
		new Thread(){
			public void run() {
				for (final TestMessageScene testSceneT:testObjects) {
					Thread testThread = new Thread(){
						public void run() {
							//组合场景
							if (testSceneT.getComplexFlag()) {
								singleTestComplexScene(testSceneT, report);
							} else {
								TestResult result = singleTest(testSceneT, null);
								result.setTestReport(report);
								testResultService.save(result);
							}
							
							synchronized (lock) {
								count[0] += testSceneT.getTestCount();
								//判断是否完成
								if (count[0] == count[1]) {	
									report.setFinishTime(new Timestamp(System.currentTimeMillis()));
									report.setFinishFlag("Y");
									testReportService.edit(report);
									
									//提前生成静态报告
									String createReportUrl = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME) + "/" 
											+ SystemConsts.CREATE_STATIC_REPORT_HTML_RMI_URL + "?reportId=" + report.getReportId()
											+ "&token=" + SystemConsts.REQUEST_ALLOW_TOKEN;
									PracticalUtils.doGetHttpRequest(createReportUrl);
																		
									finishFlag[0] = true;
								}
							}													
						}
					};
					testThread.start();
					try {
						if (testSceneT.getPriority() > 0|| "1".equals(config.getRunType())) {
							testThread.join();
						}
					} catch (Exception e) {
						LOGGER.warn(e.getMessage(), e);
					}
				}
			};
		}.start();

		if (StringUtils.isNotBlank(guid)) {
			while (!finishFlag[0]) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.warn("InterruptedException", e);
				}
			}
		}		
		
		return new int[]{report.getReportId(), count[1]};		
	}
	
	/**
	 * 组装组合场景的测试要素对象
	 * @param complexScene 对应组合场景
	 * @param config 对应测试配置
	 * @return
	 */
	public TestMessageScene packageComplexRequestObject(ComplexScene complexScene, TestConfig config){
		complexScene.setComplexSceneConfigs();
		TestMessageScene testScene = new TestMessageScene();
		testScene.setComplexFlag(true);
		testScene.setComplexScene(complexScene);
		testScene.setNewClient("0".equals(complexScene.getNewClient()) ? true : false);
		int sceneCount = 0;
		for (MessageScene scene:complexScene.setScenes(messageSceneService)) {
			Set<TestMessageScene> tss = packageRequestObject(scene, config, businessSystemService.get(Integer.valueOf(scene.getConfig().getSystemId())));
			if (tss.size() == 1) {
				testScene.getScenes().addAll(tss);
				if ("2".equals(complexScene.getSuccessFlag())) {
					sceneCount++;
				}
			} else {
				testScene.getScenes().add(null);
			}
		}	
		if(testScene.getScenes().size() > 0) testScene.setTestClient(testScene.getScenes().get(0).getTestClient());
		testScene.setTestCount("2".equals(complexScene.getSuccessFlag()) ? sceneCount : 1);
		return testScene;		
	}
	
	
	/**
	 * 组装测试要素对象
	 * @param scene 对应的测试场景
	 * @param config 需要用到的测试配置
	 * @param systemId 指定的测试系统,如果传入null,默认组装所有的测试环境
	 * @return
	 */
	@SuppressWarnings("static-access")
	public Set<TestMessageScene> packageRequestObject (MessageScene scene, TestConfig config, BusinessSystem singleSystem) {				
		InterfaceInfo info = messageSceneService.getInterfaceOfScene(scene.getMessageSceneId());
		Message msg = messageSceneService.getMessageOfScene(scene.getMessageSceneId());
		
		Set<TestMessageScene> testObjects = new HashSet<TestMessageScene>();
		
		Set<BusinessSystem> systems = msg.checkSystems(scene.getSystems());
		
		//配置为空时默认测试全部的
		Set<BusinessSystem> enableTestSystems = config.getBusinessSystems();
		
		boolean allSystemsTestFlag = false;
		if (StringUtils.isBlank(config.getSystems())) {
			allSystemsTestFlag = true;
		} 
		
		if (singleSystem != null) {
			systems.add(singleSystem);
		}
		
		for (BusinessSystem system:systems) {				
			
			if ((!allSystemsTestFlag && !enableTestSystems.contains(system)) || "1".equals(system.getStatus()) ) {
				continue;
			}
			if (singleSystem == null || system.getSystemId().equals(singleSystem.getSystemId())) {
				TestMessageScene testScene = new TestMessageScene();
				
				//获取测试地址
				String requestUrl = "";
				if (StringUtils.isNotBlank(scene.getRequestUrl())) requestUrl = scene.getRequestUrl();
				if (StringUtils.isBlank(requestUrl) && StringUtils.isNotBlank(msg.getRequestUrl())) requestUrl = msg.getRequestUrl();
				if (StringUtils.isBlank(requestUrl)) requestUrl = info.getRequestUrlReal();			
				
				requestUrl = system.getReuqestUrl(requestUrl, system.getDefaultPath(), info.getInterfaceName());
				
				//获取测试数据
				MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
				String requestMessage = "";
				List<TestData> datas= scene.getEnabledTestDatas(1, String.valueOf(system.getSystemId()));
				TestData d = null;
				if (datas.size() != 0) {
					d = datas.get(0);
				}
				if (d != null && !CacheUtil.checkLockedTestData(d.getDataId())) {				
					if (info.getInterfaceType().equalsIgnoreCase(MessageKeys.INTERFACE_TYPE_SL) && d.getStatus().equals("0")) {
						//预占测试数据
						CacheUtil.addLockedTestData(d.getDataId());
						testScene.setDataId(d.getDataId());
					}
					
					JSONObject paramsData = new JSONObject().fromObject(StringUtils.isNotBlank(d.getParamsData()) ? d.getParamsData() : "{}");
					//有单独的测试配置说明是组合场景下的场景
					if (scene.getConfig() != null) {
						for (Map.Entry<String, String> entry:scene.getConfig().getUseVariables().entrySet()) {
							String replaceVariable = entry.getValue();
							if (!Pattern.matches("\\$\\{__(.*?)\\}", replaceVariable)) {
								//如果不是全局变量说明是常量或者上下文保存的测试结果变量
								replaceVariable = MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_LEFT + replaceVariable + MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_RIGHT;
							}
							paramsData.put(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + "." + entry.getKey(), replaceVariable);
						}
					} 
					//替换入参报文中的全局变量
					requestMessage = PracticalUtils.replaceGlobalVariable(parseUtil.depacketizeMessageToString(msg.getComplexParameter(),
							paramsData.toString()), globalVariableService);	
									
				}
				testScene.setTestClient(TestClient.getTestClientInstance(info.getInterfaceProtocol()));
				testScene.setRequestMessage(requestMessage);				
				testScene.setComplexFlag(false);
				testScene.setScene(scene);
				testScene.setRequestUrl(PracticalUtils.replaceGlobalVariable(requestUrl, globalVariableService));		
				testScene.setParseUtil(parseUtil);
				testScene.setBusinessSystem(system);
				testScene.setConfig(config);
				testScene.setCallParameter(PracticalUtils.jsonToMap(msg.getCallParameter()));
				testObjects.add(testScene);
			}					
		}
		
		return testObjects;
	}
	
	private int findTestObjectBySceneId(Integer sceneId, List<TestMessageScene> testObjects, int priority) {
		for (TestMessageScene scene:testObjects) {
			if (!scene.getComplexFlag() && sceneId.equals(scene.getScene().getMessageSceneId())) {
				scene.setPriority(priority++);
			}
		}
		
		return priority;
	}

}
