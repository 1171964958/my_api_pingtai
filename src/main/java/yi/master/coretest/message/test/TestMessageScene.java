
package yi.master.coretest.message.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import yi.master.business.message.bean.ComplexScene;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.coretest.message.protocol.TestClient;

/**
 * 
 * 临时组装的测试对象，包含单个场景或者组合场景测试需要的所有信息
 * @author xuwangcheng
 * @version 20171231,1.0.0.0
 *
 */
public class TestMessageScene implements Cloneable {
	
	private static final Logger LOGGER = Logger.getLogger(TestMessageScene.class);
	/**
	 * 对应的测试场景
	 */
	private MessageScene scene;
	
	/**
	 * 最终的测试地址
	 */
	private String requestUrl;
	
	/**
	 * 最终拼装的测试报文
	 */
	private String requestMessage;
	
	/**
	 * 使用的数据id,测试成功之后将数据置为已使用<br>为0代表不需要不需要改变数据状态
	 */
	private Integer dataId = 0;
	
	/**
	 * 是否为组合场景？
	 */
	private Boolean complexFlag = false;
	
	/**
	 * 如果为组合场景则对应包含的测试数据
	 */
	private List<TestMessageScene> scenes = new ArrayList<TestMessageScene>();
	
	/**
	 * 包含的组合场景
	 */
	private ComplexScene complexScene;
	
	/**
	 * 所属测试环境
	 */
	private BusinessSystem businessSystem;
	
	private MessageParse parseUtil;
	private TestClient testClient;
	
	/**
	 * 最终产生的测试报告数量
	 */
	private int testCount = 1;
	
	/**
	 * 是否启用新的httpclient去测试
	 */
	private boolean newClient = false;
	
	/**
	 * 测试配置
	 */
	private TestConfig config;
	/**
	 * 调用参数map
	 */
	private Map<String, Object> callParameter;
	
	/**
	 * 优先级
	 */
	private Integer priority = 0;

	public TestMessageScene(MessageScene scene, String requestUrl,
			String requestMessage, Integer dataId, Boolean complexFlag) {
		super();
		this.scene = scene;
		this.requestUrl = requestUrl;
		this.requestMessage = requestMessage;
		this.dataId = dataId;
		this.complexFlag = complexFlag;
	}

	
	
	public TestMessageScene(MessageScene scene, String requestUrl,
			String requestMessage, Integer dataId, Boolean complexFlag, TestConfig config,
			Map<String, Object> callParameter) {
		super();
		this.scene = scene;
		this.requestUrl = requestUrl;
		this.requestMessage = requestMessage;
		this.dataId = dataId;
		this.complexFlag = complexFlag;
		this.config = config;
		this.callParameter = callParameter;
	}



	public TestMessageScene() {
		super();
		
	}

	
	public void setTestClient(TestClient testClient) {
		this.testClient = testClient;
	}
	
	public TestClient getTestClient() {
		return testClient;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Integer getPriority() {
		return priority;
	}

	public void setConfig(TestConfig config) {
		this.config = config;
	}
	
	public TestConfig getConfig() {
		return config;
	}
	
	public void setCallParameter(Map<String, Object> callParameter) {
		this.callParameter = callParameter;
	}
	
	public Map<String, Object> getCallParameter() {
		return callParameter;
	}
	
	public void setNewClient(boolean newClient) {
		this.newClient = newClient;
	}
	
	public boolean isNewClient() {
		return newClient;
	}
	
	public void setBusinessSystem(BusinessSystem businessSystem) {
		this.businessSystem = businessSystem;
	}
	
	public BusinessSystem getBusinessSystem() {
		return businessSystem;
	}
	
	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}
	
	public int getTestCount() {
		return testCount;
	}
	
	public void setParseUtil(MessageParse parseUtil) {
		this.parseUtil = parseUtil;
	}
	
	public MessageParse getParseUtil() {
		return parseUtil;
	}
	
	public void setComplexScene(ComplexScene complexScene) {
		this.complexScene = complexScene;
	}
	
	public ComplexScene getComplexScene() {
		return complexScene;
	}
	
	public MessageScene getScene() {
		return scene;
	}

	public void setScene(MessageScene scene) {
		this.scene = scene;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

	public Boolean getComplexFlag() {
		return complexFlag;
	}

	public void setComplexFlag(Boolean complexFlag) {
		this.complexFlag = complexFlag;
	}

	public List<TestMessageScene> getScenes() {
		return scenes;
	}

	public void setScenes(List<TestMessageScene> scenes) {
		this.scenes = scenes;
	}

	
	@Override
	public Object clone() {
		
		Object o = null;
		try {
			o = super.clone();
		} catch (Exception e) {
			
			LOGGER.warn("clone exception!", e);
		}
		return o;
	}
 
	
	@Override
	public String toString() {
		return "TestMessageScene [priority=" + priority + "]";
	}
}
