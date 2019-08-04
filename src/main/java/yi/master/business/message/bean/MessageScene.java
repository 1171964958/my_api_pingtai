package yi.master.business.message.bean;
// default package

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldNameMapper;
import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.bean.PerformanceTestConfig;
import yi.master.business.message.service.TestDataService;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.constant.MessageKeys;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;


/**
 * 接口自动化测试场景
 * @author xuwangcheng
 * @version 1.0.0.0,2017.3.6
 */

public class MessageScene implements Serializable, Cloneable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(MessageScene.class);
	
	/**
	 * 场景id
	 */
	private Integer messageSceneId;
	/**
	 * 所属报文
	 */
	private Message message;
	/**
	 * 场景名
	 */
	private String sceneName;
	/**
	 * 验证规则标志
	 * 0  根据左右边界取关键字并验证,这是默认验证
	 * 1  使用自定义的验证配置,需要验证多个参数具体的值
	 * 2  使用自定义的验证规则,直接验证整个返回串
	 */
	@Deprecated
	private String validateRuleFlag;
	/**
	 * 备注
	 */
	private String mark;
	/**
	 * 该场景的返回报文示例
	 */
	private String responseExample;
	
	private Timestamp createTime;
	/**
	 * 该测试场景对应的测试数据
	 */
	private Set<TestData> testDatas = new HashSet<TestData>();
	/**
	 * 对应测试集
	 */
	private Set<TestSet> testSets = new HashSet<TestSet>();
	 /**
	  * 测试结果
	  */
	private Set<TestResult> testResults = new HashSet<TestResult>();
	 /**
	  * 验证规则
	  */
	private Set<SceneValidateRule> rules = new HashSet<SceneValidateRule>();
	
	private String validateMethodStr;
	
	/**
	 * 请求路径，他的优先级高于报文和接口中配置的
	 */
	private String requestUrl;
	
	@FieldNameMapper(fieldPath="size(testDatas)",ifSearch=false)
	private Integer testDataNum = getTestDataNum();
	
	@FieldNameMapper(fieldPath="size(rules)",ifSearch=false)
	private Integer rulesNum;
	
	@FieldNameMapper(fieldPath="message.messageName")
	private String messageName;

	@FieldNameMapper(fieldPath="message.interfaceInfo.interfaceName")
	private String interfaceName;
	
	@FieldNameMapper(fieldPath="message.interfaceInfo.interfaceProtocol")
	private String protocolType;
	
	@FieldNameMapper(fieldPath="message.messageType")
	private String messageType;
	
	/**
	 * 可用测试数据
	 */
	private List<TestData> enabledTestDatas;
	
	/**
	 * 组合场景中的序号
	 */
	@FieldNameMapper(ifOrder = false, ifSearch = false)
	private Integer sequenceNum = -1;
	
	/**
	 * 组合场景中的配置信息
	 */
	private ComplexSceneConfig config;
	
	/**
     * 所属测试环境
     */
    private String systems;
    
    /**
     * 所属测试环境
     */
    private Set<BusinessSystem> businessSystems;
    
    /**
     * 关联的性能测试
     */
    private Set<PerformanceTestConfig> ptsConfigs = new HashSet<>();
    
    /**
     * 关联的接口探测
     */
    private Set<InterfaceProbe> probes = new HashSet<>();
    
    
    
     
    // Constructors

    /** default constructor */
    public MessageScene() {
    }

    public MessageScene(Integer messageSceneId) {
    	this.messageSceneId = messageSceneId;
    }
    
    /** full constructor */
    public MessageScene(Message message, String sceneName, String validateRuleFlag,String mark) {
        this.message = message;
        this.sceneName = sceneName;
        this.validateRuleFlag = validateRuleFlag;
        this.mark = mark;
    }       
    
    
    
    
    // Property accessors   
    @JSON(serialize = false)
    public Set<PerformanceTestConfig> getPtsConfigs() {
		return ptsConfigs;
	}

	public void setPtsConfigs(Set<PerformanceTestConfig> ptsConfigs) {
		this.ptsConfigs = ptsConfigs;
	}

	@JSON(serialize = false)
	public Set<InterfaceProbe> getProbes() {
		return probes;
	}


	public void setProbes(Set<InterfaceProbe> probes) {
		this.probes = probes;
	}
    
    
    public String getSystems() {
		return systems;
	}
    
    public void setSystems(String systems) {
		this.systems = systems;
	}
    
    public Set<BusinessSystem> getBusinessSystems() {
		return PracticalUtils.getSystems(this.systems);
	}
    
    public String getProtocolType() {
    	if (this.message != null) {
    		return this.message.getInterfaceInfo().getInterfaceProtocol();
    	}
		return this.protocolType;
	}
    
    public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
    
    public String getMessageType() {
    	if (this.message != null) {
    		return this.message.getMessageType();
    	}
		return "";
	}
    
    public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
    
    public void setConfig(ComplexSceneConfig config) {
		this.config = config;
	}
    
    public ComplexSceneConfig getConfig() {
		return config;
	}
    
    public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
    
    public Integer getSequenceNum() {
		return sequenceNum;
	}
    
    public void setResponseExample(String responseExample) {
		this.responseExample = responseExample;
	}
    
    
    public String getResponseExample() {
    	if (responseExample == null) {
    		return "";
    	}
		return responseExample;
	}
    
    public Integer getRulesNum() {
		return this.rules.size();
	}
    
    public void setRulesNum(Integer rulesNum) {
		this.rulesNum = rulesNum;
	}
    
    
    public String getMessageName() {
    	if (this.message != null) {
    		return this.getMessage().getMessageName();
    	}
		return null;
	}
    @JSON(serialize=false)
    public String getMessageName2 () {
    	return this.messageName;
    }
    
	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getInterfaceName() {
		if (this.message != null) {
			return this.getMessage().getInterfaceName();
		}
		return this.interfaceName;
	}

	@JSON(serialize=false)
	public String getInterfaceName2 () {
		return this.interfaceName;
	}
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public void setTestSets(Set<TestSet> testSets) {
		this.testSets = testSets;
	}
    
    public Integer getTestDataNum() {
		return this.testDatas.size();
	}


	public void setTestDataNum(Integer testDataNum) {
		this.testDataNum = testDataNum;
	}


	public String getValidateMethodStr() {
		return validateMethodStr;
	}


	public void setValidateMethodStr(String validateMethodStr) {
		this.validateMethodStr = validateMethodStr;
	}	


	@JSON(serialize=false)
    public Set<SceneValidateRule> getRules() {
		return rules;
	}


	public void setRules(Set<SceneValidateRule> rules) {
		this.rules = rules;
	}


	public String getValidateRuleFlag() {
		return validateRuleFlag;
	}


	public void setValidateRuleFlag(String validateRuleFlag) {
		this.validateRuleFlag = validateRuleFlag;
	}


	@JSON(format="yyyy-MM-dd HH:mm:ss")
    public Timestamp getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
	
	@JSON(serialize=false)
    public Set<TestResult> getTestResults() {
		return testResults;
	}


	public void setTestResults(Set<TestResult> testResults) {
		this.testResults = testResults;
	}

	@JSON(serialize=false)
    public Set<TestSet> getTestSets() {
		return testSets;
	}

    public Integer getMessageSceneId() {
        return this.messageSceneId;
    }
    
    public void setMessageSceneId(Integer messageSceneId) {
        this.messageSceneId = messageSceneId;
    }

    @JSON(serialize=false)
    public Message getMessage() {
        return this.message;
    }
    
    public void setMessage(Message message) {
        this.message = message;
    }

    public String getSceneName() {
        return this.sceneName;
    }
    
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getMark() {
    	if (this.mark == null) {
    		return "";
    	}
        return this.mark;
    }
    
    public void setMark(String mark) {
        this.mark = mark;
    }

    @JSON(serialize=false)
    public Set<TestData> getTestDatas() {   	
        return testDatas;
    }
       
    public void setTestDatas(Set<TestData> testDatas) {
        this.testDatas = testDatas;
    }

    public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
    
    public String getRequestUrl() {
		return requestUrl;
	}
    
    @JSON(serialize=false)
	public List<TestData> getEnabledTestDatas(Integer count, String systemId) {
    	if (this.enabledTestDatas == null) {
    		TestDataService testDataService = (TestDataService) FrameworkUtil.getSpringBean("testDataService");
    		this.enabledTestDatas = testDataService.getDatasByScene(this.messageSceneId, 0);
    	}
    	
		if (count == null) {
			return this.enabledTestDatas;
		}
		
		List<TestData> systemDatas = new ArrayList<TestData>();
		List<TestData> defaultDatas = new ArrayList<TestData>();
		for (TestData data:enabledTestDatas) {
			if ("1".equals(data.getDefaultData()) && data.checkSystem(systemId)) {
				systemDatas.add(data);
			}	
			if (StringUtils.isBlank(data.getDefaultData()) || "0".equals(data.getDefaultData())) {
				defaultDatas.add(data);
			}
			if (count.equals(systemDatas.size())) {
				break;
			}
		}
		
		if (count > systemDatas.size()) {//数据不够的话就将默认数据添加进去
			systemDatas.addAll(defaultDatas);
		}
		return systemDatas;
	}

    /**
     * 检查是否有足够测试数据
     * @param systemId 传入systemId指定检查的测试环境,传入null则检查全部的测试环境(测试场景关联的有效测试环境)
     * @return
     */
    public boolean hasEnoughData (String systemId) {
    	boolean flag = true;
    	this.businessSystems = getMessage().checkSystems(this.systems);
    	
    	if (this.enabledTestDatas == null) {
    		TestDataService testDataService = (TestDataService) FrameworkUtil.getSpringBean("testDataService");
    		this.enabledTestDatas = testDataService.getDatasByScene(this.messageSceneId, 0);
    	}
    	
    	Set<Integer> hasUse = new HashSet<Integer>();
    	
    	label:
    	for (BusinessSystem sys:this.businessSystems) {
    		if (systemId != null && !systemId.equals(String.valueOf(sys.getSystemId()))) {
    			continue;
    		}
    		for (TestData data:this.enabledTestDatas) {
    			if (data.checkSystem(String.valueOf(sys.getSystemId())) && !hasUse.contains(data.getDataId())) {
    				if (MessageKeys.INTERFACE_TYPE_SL.equalsIgnoreCase(this.getMessage().getInterfaceInfo().getInterfaceType())
    							&& "0".equals(data.getStatus())) {
    					hasUse.add(data.getDataId());
    				}  				
    				continue label;
    			}
    		}
    		flag = false;
    		break;
    	}
    	
    	return flag;
    }

	@Override
	public String toString() {
		return "MessageScene [messageSceneId=" + messageSceneId
				+ ", sceneName=" + sceneName + ", mark=" + mark
				+ ", createTime=" + createTime + "]";
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
}