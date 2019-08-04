package yi.master.coretest.message.test.performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.json.annotations.JSON;

import yi.master.business.advanced.bean.PerformanceTestConfig;
import yi.master.business.advanced.bean.PerformanceTestResult;
import yi.master.business.advanced.bean.config.performancetest.PerformanceTestAnalyzeResult;
import yi.master.business.advanced.service.PerformanceTestResultService;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.testconfig.service.TestConfigService;
import yi.master.business.user.bean.User;
import yi.master.constant.MessageKeys;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.coretest.message.protocol.TestClient;
import yi.master.coretest.message.test.MessageAutoTest;
import yi.master.coretest.message.test.TestMessageScene;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;
import yi.master.util.excel.PoiExcelUtil;
import yi.master.util.jsonlib.JsonDateValueProcessor;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * 性能测试  测试对象<br>
 * 一次性能测试中  包含所有的对象、方法、数据等<br>
 * <strong>待优化：</strong><br>
 * 1、Tps计算的方式不太准确,因为程序本身还需要消耗时间；<br>
 * 2、每个线程应该有自己单独的存储TestResult结果的队列，防止时间消耗在同步操作上
 * @author xuwangcheng
 * @version 20180709
 *
 */
public class PerformanceTestObject {
	
	private static final Logger logger = Logger.getLogger(PerformanceTestObject.class);
	
	/**
	 * 统计相关数据的时间间隔：秒
	 */
	private static final Integer STATISTICS_TIME_INTERVAL = 8;
	
	private static Integer object_id = 0;
	
	private Integer objectId;
	
	private User testUser;
	
	private PerformanceTestConfig config;
	private PerformanceTestResult result = new PerformanceTestResult();;

	private List<String> errorMsg = new ArrayList<String>();
	private List<String> infoMsg = new ArrayList<String>();
	private List<String> resultMarkMsg = new ArrayList<String>();
	
	private List<Map<String, String>> parameters;
	private String[] titles;
	/**
	 * 单场景测试结果集<br>
	 * linkedList是线程不安全的，参考<a href="http://yizhilong28.iteye.com/blog/717158">LinkedList多线程不安全的解决办法</a>
	 */
	private Queue<TestResult> testResults = new ConcurrentLinkedQueue<TestResult>();
	/**
	 * 测试对象
	 */
	private TestMessageScene testScene;
	
	/**
	 * 全局停止标记，该标记为true时将会停止所有当前正在测试的线程
	 */
	private boolean globalStoped = false;
	/**
	 * 是否需要保存测试结果的标记,在删除状态时启用
	 */
	private boolean enableSave = true;
	
	/**
	 * 已创建/正在初始化/初始化完成/正在测试中/正在收集整理数据
	 */
	private String currentStatus = "已创建";
	
	/**
	 * 标记是否已经开始了测试
	 */
	private boolean running = false;
	/**
	 * 当前正在运行测试的线程名称
	 */
	private List<String> threads = new ArrayList<String>();
	
	/**
	 * 和本次测试场景相关的对象
	 */
	private InterfaceInfo info;
	private Message msg;
	private MessageAutoTest autoTest;
	
	/**
	 * 视图结果保存的对象
	 */
	private PerformanceTestAnalyzeResult analyzeResult = result.getAnalyzeResult();
	
	/**
	 * 请求报文,可能已被格式化或者处理过了
	 */
	private String requestMessage;
	
	
	private PerformanceTestResultService performanceTestResultService = (PerformanceTestResultService) FrameworkUtil.getSpringBean(PerformanceTestResultService.class);
	
	public PerformanceTestObject(PerformanceTestConfig config, User testUser) {
		super();
		this.config = config;
		this.testUser = testUser;
		this.objectId = ++object_id;
		CacheUtil.addPtObject(testUser.getUserId(), this);
	}


	public PerformanceTestObject() {
		super();
		
	}


	private String timeTag() {
		return PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss - ", new Date());
	}
	
	/**
	 * 初始化相关配置
	 * @return
	 */
	public boolean init() {
		currentStatus = "正在初始化";
		infoMsg.add(timeTag() + "正在初始化测试配置...");
		if (config == null) {
			errorMsg.add(timeTag() + "没有找到相关的测试配置,请检查!");
			return false;
		}
		infoMsg.add(timeTag() + "正在初始化测试数据...");
		//判断是否存在参数化文件并解析
		if (StringUtils.isNotBlank(config.getParameterizedFilePath())) {
			File dataFile = new File(FrameworkUtil.getProjectPath() + File.separator + config.getParameterizedFilePath());
			if (dataFile.exists()) {
				parseParameterizedFile(dataFile);
			}			
		}
		infoMsg.add(timeTag() + "正在创建测试对象...");
		//获取入参报文和请求地址
		MessageSceneService messageSceneService = (MessageSceneService)FrameworkUtil.getSpringBean("messageSceneService");
		info = messageSceneService.getInterfaceOfScene(config.getMessageScene().getMessageSceneId());
		msg = messageSceneService.getMessageOfScene(config.getMessageScene().getMessageSceneId());
		autoTest = (MessageAutoTest) FrameworkUtil.getSpringBean("messageAutoTest");
		
		//判断是否需要替换参数
		Map<String, String> paramsData = new HashMap<String, String>();
		if (parameters != null && parameters.size() > 0) {
			for (String t:titles) {
				if (StringUtils.isNotBlank(t)) {
					paramsData.put(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + "." + t, "{" + t + "}");
				}
			}
		}
		
		//拼装入参报文
		MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
		requestMessage = parseUtil.depacketizeMessageToString(msg.getComplexParameter(), JSONObject.fromObject(paramsData).toString());
		
		//获取请求地址
		String requestUrl = "";
		if (StringUtils.isNotBlank(config.getMessageScene().getRequestUrl())) requestUrl = config.getMessageScene().getRequestUrl();
		if (StringUtils.isBlank(requestUrl) && StringUtils.isNotBlank(msg.getRequestUrl())) requestUrl = msg.getRequestUrl();
		if (StringUtils.isBlank(requestUrl)) requestUrl = info.getRequestUrlReal();			
		
		requestUrl = config.getBusinessSystem().getReuqestUrl(requestUrl, config.getBusinessSystem().getDefaultPath(), info.getInterfaceName());
		
		//手动组装场景测试对象
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		TestConfigService testConfigService = (TestConfigService) FrameworkUtil.getSpringBean("testConfigService");
		TestConfig testConfig = testConfigService.getConfigByUserId(user.getUserId());
		if (testConfig == null) {
			testConfig = testConfigService.getConfigByUserId(0);
		}
		testScene = new TestMessageScene(config.getMessageScene(), requestUrl, new String(requestMessage), 0, false, testConfig, PracticalUtils.jsonToMap(msg.getCallParameter()));
		testScene.setBusinessSystem(config.getBusinessSystem());
		testScene.setParseUtil(parseUtil);		
		
		infoMsg.add(timeTag() + "测试对象创建完成!");
		
		infoMsg.add(timeTag() + "正在检查测试配置选项...");
		//设置最大的超时时间,防止无限循环
		if (config.getMaxTime() == null || config.getMaxTime() < 1 || config.getMaxTime() > PerformanceTestConfig.MAX_TEST_TIME) {
			config.setMaxTime(PerformanceTestConfig.MAX_TEST_TIME);//最长时间8小时
		} 
		
		//设置最大线程数
		if (config.getThreadCount() == null) config.setThreadCount(1);
		if (config.getThreadCount() > PerformanceTestConfig.MAX_THREAD_COUNT) {
			config.setThreadCount(PerformanceTestConfig.MAX_THREAD_COUNT);
		}
		
		
		//创建结果对象		
		result.setCreateTime(new Timestamp(System.currentTimeMillis()));
		result.setFinishFlag("N");
		result.setInterfaceName(info.getInterfaceName() + "-" + msg.getMessageName() + "-" + config.getMessageScene().getSceneName());
		result.setParameterizedFilePath(config.getParameterizedFilePath());
		result.setPerformanceTestConfig(config);
		result.setSystemName(config.getBusinessSystem().getSystemName());
		result.setThreadCount(config.getThreadCount());
		result.setUser(testUser);
		result.setAnalyzeResult(new PerformanceTestAnalyzeResult());
		infoMsg.add(timeTag() + "接口性能测试初始化已完成!");
		currentStatus = "初始化完成";
		return true;
	}

	/**
	 * 执行测试,启动测试线程
	 * @return
	 */
	public boolean action() {
		if (!running && globalStoped) setGlobalStoped(false); 
		currentStatus = "正在测试中";
		infoMsg.add(timeTag() + "正在做最后的测试准备...");
		//切分测试数据和测试次数到每个线程中
		List<List<Map<String, String>>> threadParameters = null;
		if (parameters != null && parameters.size() > 0) {
			//设置数据
			if (parameters.size() < config.getThreadCount() && "0".equals(config.getParameterReuse())) {
				errorMsg.add(timeTag() + "测试数据不足!!!!");
				return false;
			}			
			int countAvg = parameters.size() / ("0".equals(config.getParameterReuse()) ? config.getThreadCount() : 1);
			//切分数据
			threadParameters = PracticalUtils.splitList(parameters, countAvg);			
		}
		int maxCount;
		if (config.getMaxCount() == null || config.getMaxCount() < 1) {
			maxCount = Integer.MAX_VALUE;
		} else {
			maxCount = config.getMaxCount() / config.getThreadCount();
			if (maxCount == 0) maxCount = 1; 
		}
		result.setStartTime(new Timestamp(System.currentTimeMillis()));
		for (int i = 0;i < config.getThreadCount();i++) {
			Thread thread = new Thread(new ThreadPerformanceTest(threadParameters == null ? null : threadParameters.get(i), maxCount));
			threads.add(thread.getName());
			thread.start();
		}
		
		//启动监控处理数据的线程
		new Timer().schedule(new PerformanceTestMonitorTask(), 5000, (long)STATISTICS_TIME_INTERVAL * 1000);
		running = true;
		return true;
	}
	

	/**
	 * 结束工作，包括写结果到文件，更新数据库内容等
	 * @return
	 */
	private boolean end() {
		if (enableSave) {
			currentStatus = "正在收集整理数据";
			infoMsg.add(timeTag() + "正在保存结果到文件...");
			String filePath = null;
			FileOutputStream fos = null;
			ObjectOutputStream out = null;
			try {
				filePath = FrameworkUtil.getProjectPath() + "/tmp/ptr/" + PracticalUtils.getUUID("") + ".txt";
				fos = new FileOutputStream(filePath);
				out = new ObjectOutputStream(fos);
				out.writeObject(result.getTestResults());			
			} catch (Exception e) {
				
				logger.error("写测试结果到文件中出错!", e);
				errorMsg.add(timeTag() + "写测试结果到文件中出错!");
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						logger.warn("IOException", e);
					}
				}				
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						logger.warn("IOException", e);
					}
				}
			}
			infoMsg.add(timeTag() + "正在保存结果到数据库...");
			result.setFinishFlag("Y");
			result.setFinishTime(new Timestamp(System.currentTimeMillis()));
			result.setRequestCount(analyzeResult.getTotalCount());
			result.setTestTime(STATISTICS_TIME_INTERVAL * analyzeResult.getTime().size());
			result.setDetailsResultFilePath(filePath);
			
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor("yyyy-MM-dd HH:mm:ss"));	
			
			infoMsg.add(timeTag() + "数据整理完成,本次测试已结束!");
			
			analyzeResult.setInfoMsg(infoMsg);
			analyzeResult.setErrorMsg(errorMsg);
			analyzeResult.setResultMark(resultMarkMsg);
			result.setAnalyzeResultJson(JSONObject.fromObject(analyzeResult, jsonConfig).toString());
			
			performanceTestResultService.edit(result);
			
		}	
				
		CacheUtil.getPtObjectsByUserId(this.testUser.getUserId()).remove(this.objectId);
		return true;
	}
	
	public void setEnableSave(boolean enableSave) {
		this.enableSave = enableSave;
	}
	
	public boolean isEnableSave() {
		return enableSave;
	}	
	
	public void setGlobalStoped(boolean globalStoped) {
		this.globalStoped = globalStoped;
	}
	
	public boolean isGlobalStoped() {
		return globalStoped;
	}
	
	public PerformanceTestAnalyzeResult getAnalyzeResult() {
		return analyzeResult;
	}
	
	public void setAnalyzeResult(PerformanceTestAnalyzeResult analyzeResult) {
		this.analyzeResult = analyzeResult;
	}
	
	@JSON(serialize=false)
	public PerformanceTestConfig getConfig() {
		return config;
	}

	public void setConfig(PerformanceTestConfig config) {
		this.config = config;
	}

	@JSON(serialize=false)
	public PerformanceTestResult getResult() {
		return result;
	}

	public void setResult(PerformanceTestResult result) {
		this.result = result;
	}
	
	public List<String> getErrorMsg() {
		return errorMsg;
	}
	
	public List<String> getInfoMsg() {
		return infoMsg;
	}
	
	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}
	
	public Integer getObjectId() {
		return objectId;
	}

	public List<String> getThreads() {
		return threads;
	}

	public void setThreads(List<String> threads) {
		this.threads = threads;
	}

	public void setTestUser(User testUser) {
		this.testUser = testUser;
	}
	
	@JSON(serialize=false)
	public User getTestUser() {
		return testUser;
	}
	
	public void setResultMarkMsg(List<String> resultMarkMsg) {
		this.resultMarkMsg = resultMarkMsg;
	}
	
	public List<String> getResultMarkMsg() {
		return resultMarkMsg;
	}
	
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	public String getCurrentStatus() {
		return currentStatus;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	
	/**
	 * 解析参数化文件
	 * @param parameterizedFile txt格式化的参数化文件，并且指定了分隔符
	 */
	private void parseParameterizedFile(File parameterizedFile) {
		infoMsg.add(timeTag() + "正在解析参数化文件...");
		if (parameterizedFile == null || !parameterizedFile.exists()) {
			return;
		}
		
		List<String> infos = null;
		try {
			infos = FileUtils.readLines(parameterizedFile);
		} catch (Exception e) {
			
			errorMsg.add(timeTag() + "读取参数化文件:" + parameterizedFile.getPath() + "失败，本次测试不进行参数化操作!");
			logger.error("读取参数化文件:" + parameterizedFile.getPath() + "失败!", e);
			return;
		}
		
		if (infos != null && infos.size() > 1) {
			//读取第一行为参数化的路径
			parameters = new ArrayList<Map<String, String>>();
			String formatCharacter = config.getFormatCharacter() == null ? "," : config.getFormatCharacter();
			
			titles = infos.get(0).split(formatCharacter);
			infos.remove(0);
			
			for (String info:infos) {
				if (StringUtils.isBlank(info)) {
					continue;
				}
				
				String[] infoArray = info.split(formatCharacter);
				if (infoArray.length != titles.length) {
					continue;
				}
				Map<String, String> parameter = new HashMap<String, String>();
				for (int i = 0;i < infoArray.length;i++) {
					parameter.put("{" + titles[i] + "}", infoArray[i]);
				}
				
				if (parameter.size() > 0) {
					parameters.add(parameter);
				}
			}
			infoMsg.add(timeTag() + "参数化文件解析完成,数据总共为：" + parameters.size() + "条!");			
		}		
	}
	
	
	/**
	 * 解析参数化文件<br>
	 * @see PerformanceTestObject#parseParameterizedFile(File)
	 * @param parameterizedFile xlsx格式的文件
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void parseParameterizedFile2(File parameterizedFile) {
		infoMsg.add(timeTag() + "正在解析参数化文件...");
		if (parameterizedFile == null || !parameterizedFile.exists()) {
			return;
		}
		XSSFWorkbook xssfWorkbook = null;
		try {
			InputStream is = new FileInputStream(parameterizedFile);
			xssfWorkbook = new XSSFWorkbook(is);
		} catch (IOException e) {
			errorMsg.add(timeTag() + "读取参数化文件:" + parameterizedFile.getPath() + "失败，本次测试不进行参数化操作!");
			logger.error("读取参数化文件:" + parameterizedFile.getPath() + "失败!", e);
			return;
		}
		
		if (xssfWorkbook != null) {
			parameters = new ArrayList<Map<String, String>>();
			// Read the Sheet.只读取第一页
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
			//获取标题即节点路径名称
			titles = new String[xssfSheet.getRow(0).getLastCellNum() + 1];
						
			 for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                 XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                 Map<String, String> parameter = new HashMap<String, String>();
                 if (xssfRow == null) {
                	continue;               	                	 
                 }
                 for (int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++) {
                	 if (xssfRow.getCell(cellNum) == null) continue;
                	 if (rowNum == 0) {
                		 //获取title
                		 titles[cellNum] = PoiExcelUtil.getValue(xssfRow.getCell(cellNum));
                	 } else {
                		 parameter.put("{" + titles[cellNum] + "}", PoiExcelUtil.getValue(xssfRow.getCell(cellNum)));
                	 }
                 }
                 if (parameter.size() > 0) {
                	 parameters.add(parameter);
                 }
			 }			
		}
		
	}
	
	private String repalceParameter(String requestMessage, Map<String, String> parameters) {
		for (String key:parameters.keySet()) {
			requestMessage = requestMessage.replace(key, parameters.get(key));
		}
		return requestMessage;
	}
	
	/**
	 * 创建简要视图
	 * @return
	 */
	public JSONObject createSummaryView() {
		JSONObject obj = new JSONObject();
		obj.put("objectId", objectId);
		obj.put("interfaceName", result.getInterfaceName());
		obj.put("systemName", result.getSystemName());
		obj.put("responseTime", analyzeResult.getResponseTimeAvg());
		obj.put("tps", analyzeResult.getTpsAvg());
		obj.put("currentStatus", currentStatus);
		obj.put("startTime", PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss", result.getStartTime()));
		obj.put("errorMsg", errorMsg);
		obj.put("infoMsg", infoMsg);
		obj.put("resultMark", resultMarkMsg);
		obj.put("totalCount", analyzeResult.getTotalCount());
		obj.put("successCount", analyzeResult.getSuccessCount());
		obj.put("failCount", analyzeResult.getFailCount());
		
		return obj;
	}
	
	/**
	 * 统计相关数据以及及时停止
	 * @author xuwangcheng
	 *
	 */
	class PerformanceTestMonitorTask extends TimerTask {
		@Override
		public void run() {
			Date now = new Date();			
			
			int count = testResults.size();
			List<TestResult> thisTestResults = new ArrayList<TestResult>();
			for (int i = 1;i <= count;i++) {
				thisTestResults.add(testResults.poll());
			}
			
			result.getTestResults().addAll(thisTestResults);
			
			//数据计算
			
			double totalResTime = 0.00;
			int successCount = 0;
			int failCount = 0;
			int totalCount = 0;
			for (TestResult r:thisTestResults) {
				if (r == null) continue; 
				//计算请求总数,成功数，失败数
				totalCount++;
				switch (r.getRunStatus()) {
				case "0":					
					//响应时间相关
					if (analyzeResult.getResponseTimeMax() < r.getUseTime()) {
						analyzeResult.setResponseTimeMax((double) r.getUseTime());
					}
					if (analyzeResult.getResponseTimeMin() > r.getUseTime() || analyzeResult.getResponseTimeMin() == 0.00) {
						analyzeResult.setResponseTimeMin((double) r.getUseTime());
					}
					totalResTime += r.getUseTime();	
					successCount++;
					break;
				default:
					analyzeResult.setFailCount(analyzeResult.getFailCount() + 1);
					resultMarkMsg.add(timeTag() + r.getMessageInfo() + " 测试失败:\n" + r.getMark().replaceAll("\\n+", "") + "\n");
					failCount++;
					break;
				}			
			}
			analyzeResult.setTotalCount(analyzeResult.getTotalCount() + totalCount);
			analyzeResult.getResponseTime().add(successCount > 0 ? (double) Math.round(totalResTime / successCount * 100) / 100 : 0.00);
			totalResTime += (analyzeResult.getResponseTimeAvg() * analyzeResult.getSuccessCount());
			
			analyzeResult.setSuccessCount(analyzeResult.getSuccessCount() + successCount);
			analyzeResult.setResponseTimeAvg((double) Math.round((totalResTime / analyzeResult.getSuccessCount()) * 100) / 100);
			
			//TPS
			long lastTime = result.getStartTime().getTime();
			if (analyzeResult.getTime().size() > 0) {
				lastTime = analyzeResult.getTime().get(analyzeResult.getTime().size() - 1).getTime();
			}
			double thisTps = (double) Math.round(((double)successCount / ((now.getTime() - lastTime) / 1000.00)) * 100) / 100;
			if (analyzeResult.getTpsMax() < thisTps) {
				analyzeResult.setTpsMax(thisTps);
			}
			if (analyzeResult.getTpsMin() > thisTps || analyzeResult.getTpsMin() == 0.00) {
				analyzeResult.setTpsMin(thisTps);
			}
			analyzeResult.getTps().add(thisTps);			
			analyzeResult.setTpsAvg((double) Math.round(((double)analyzeResult.getSuccessCount() / ((now.getTime() - result.getStartTime().getTime()) / 1000)) * 100) / 100);
			analyzeResult.getTime().add(now);
			
			analyzeResult.getEveryTimeDetails().add(Arrays.asList(new Integer[]{totalCount, successCount, failCount}));
			
			//压力机资源情况本机资源
			try {
				analyzeResult.getPressCpu().add((double) Math.round((PracticalUtils.sigar.getCpuPerc().getCombined() * 100) * 100) / 100);				
				analyzeResult.getPressMemory().add((double) Math.round((PracticalUtils.sigar.getMem().getUsedPercent()) * 100) / 100);
			} catch (Exception e) {
				
				logger.error("获取压力机资源消耗失败!", e);
				errorMsg.add(timeTag() + "获取压力机的资源消耗情况失败!");
				analyzeResult.getPressCpu().add(0.00);
				analyzeResult.getPressMemory().add(0.00);
			}
			//判断是否测试完成：所有测试线程是否都已经退出
			if (threads.size() == 0 && testResults.size() == 0) {
				infoMsg.add(timeTag() + "所有线程均已退出,准备数据统计并存储!");
				end();
				this.cancel(); //终止监听器同时调用end()方法				
			}
		}	
	}
	
	
	class ThreadPerformanceTest implements Runnable {
		private List<Map<String, String>> threadParameters;
		private int maxCount;
		
		
		public ThreadPerformanceTest(List<Map<String, String>> threadParameters, int maxCount) {
			
			this.threadParameters = threadParameters;
			this.maxCount = maxCount;
		}
		
		@Override
		public void run() {
			
			infoMsg.add(timeTag() + Thread.currentThread().getName() + " - 开始进行测试...");
			//复制一份测试对象,注意此时操作是浅复制
			TestMessageScene threadTestScene = (TestMessageScene) testScene.clone();
			
			//获取一个新的测试客户端
			Object procotolClient = TestClient.getTestClientInstance(info.getInterfaceProtocol()).getTestClient();

			//停止条件
			int testTime = 0;
			int testCount = 0;
			long beginTime = System.currentTimeMillis();
			int index = 0;

			while (!globalStoped && testTime <= config.getMaxTime() && testCount <= this.maxCount) {
				if (threadParameters != null) {
					if (threadParameters.isEmpty()) {
						errorMsg.add(timeTag() + Thread.currentThread().getName() + " - 当前线程没有可用的测试数据,线程已退出!");
						break;
					}
					//替换入参							
					//按顺序还是随机
					if ("1".equals(config.getParameterPickType())) {
						if (threadParameters.size() == 1) {
							index = 0;
						} else {
							index = PracticalUtils.getRandomNum(threadParameters.size() - 1, 0);
						}						
					}
					//可重复还是不可重复
					Map<String, String> replaceParameter = threadParameters.get(index);
					if ("0".equals(config.getParameterReuse())) {
						threadParameters.remove(index);//不可复用
					} else {
						//可复用
						index++;
						if (index > threadParameters.size() - 1) {
							index = 0;
						}
					}
					
					threadTestScene.setRequestMessage(repalceParameter(requestMessage, replaceParameter));						
				}
				//开始进行测试
				TestResult testResult = autoTest.singleTest(threadTestScene, procotolClient);
				testResults.add(testResult);
				
				testCount++;
				testTime = (int) ((System.currentTimeMillis() - beginTime) / 1000);
			}
			threads.remove(Thread.currentThread().getName());			
			
			TestClient.getTestClientInstance(info.getInterfaceProtocol()).putBackTestClient(procotolClient);
			infoMsg.add(timeTag() + Thread.currentThread().getName() + " - 已停止测试!");
		}
		
	}
	
}
