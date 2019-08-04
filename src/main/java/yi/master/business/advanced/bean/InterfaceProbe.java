package yi.master.business.advanced.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.advanced.bean.config.probe.ProbeConfig;
import yi.master.business.advanced.service.InterfaceProbeService;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestResult;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.user.bean.User;
import yi.master.util.FrameworkUtil;

/**
 * 接口探测信息表
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.01.24
 *
 */
public class InterfaceProbe implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(InterfaceProbe.class);

	private Integer probeId;
	
	@FieldRealSearch(names = {"测试场景", "组合场景"}, values = {"1", "2"})
	@CustomConditionSetting(operator="=")
	private String probeType;
	
	@FieldNameMapper(fieldPath="scene.message.interfaceInfo.interfaceName")
	private MessageScene scene;
	@FieldNameMapper(fieldPath="system.systemName")
	private BusinessSystem system;
	
	private Timestamp createTime;
	
	@FieldNameMapper(fieldPath="user.realName")
	private User user;
	
	@FieldRealSearch(names = {"已停止", "正在运行", "缺少数据", "执行出错"}, values = {"0", "1", "2", "3"})
	@CustomConditionSetting(operator="=")
	private String status;
	
	@FieldNameMapper(ifSearch=false)
	@CustomConditionSetting(conditionType="")
	private Integer callCount = 0;
	
	private Timestamp firstCallTime;
	
	private Timestamp lastCallTime;
	
	private String cycleAnalysisData;
	
	private String mark;
	
	private ProbeConfig config;
	
	private String probeConfigJson;
	
	private Set<TestResult> results = new HashSet<TestResult>();
	
	/**
	 * 最近一次的测试结果
	 */
	@FieldNameMapper(ifOrder=false,ifSearch=false)
	@CustomConditionSetting(conditionType="")
	private TestResult lastResult;
	
	
	/*****************************************/
	@FieldNameMapper(fieldPath="createTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String createTimeText;
	
	@FieldNameMapper(fieldPath="lastCallTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String lastCallTimeText;
	
	@FieldNameMapper(fieldPath="firstCallTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)	
	private String firstCallTimeText;
	
	@FieldNameMapper(fieldPath="user.realName")
	private String createUserName;
	
	@FieldNameMapper(fieldPath="scene.message.interfaceInfo.interfaceName")
	private String interfaceName;
	
	@FieldNameMapper(fieldPath="system.systemId")
	@CustomConditionSetting(operator="=")
	private Integer systemId;
	
	/******************************************/

	public InterfaceProbe() {
		super();
	}
	
	
	public Integer getSystemId() {
		return systemId;
	}
	
	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public String getCreateTimeText() {
		return createTimeText;
	}

	public void setCreateTimeText(String createTimeText) {
		this.createTimeText = createTimeText;
	}

	public String getLastCallTimeText() {
		return lastCallTimeText;
	}

	public void setLastCallTimeText(String lastCallTimeText) {
		this.lastCallTimeText = lastCallTimeText;
	}

	public String getFirstCallTimeText() {
		return firstCallTimeText;
	}

	public void setFirstCallTimeText(String firstCallTimeText) {
		this.firstCallTimeText = firstCallTimeText;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Integer getProbeId() {
		return probeId;
	}

	public void setProbeId(Integer probeId) {
		this.probeId = probeId;
	}
	
	public void setResults(Set<TestResult> results) {
		this.results = results;
	}
	
	/**
	 * 对应的测试结果集合
	 * @return
	 */
	@JSON(serialize=false)
	public Set<TestResult> getResults() {
		/*InterfaceProbeService service = (InterfaceProbeService) SettingUtil.getSpringBean(InterfaceProbeService.class);	
		return new HashSet<TestResult>(service.findProbeResults(this.probeId, this.firstCallTime));*/
		return results;
	}

	/**
	 * 探测类型<br>
	 * 1 - 单个场景探测<br>
	 * 2 - 组合场景探测<br>
	 * 目前只能使用单个场景探测
	 * @return
	 */
	public String getProbeType() {
		return probeType;
	}

	public void setProbeType(String probeType) {
		this.probeType = probeType;
	}

	/**
	 * 关联的测试场景
	 * @return
	 */
	public MessageScene getScene() {
		return scene;
	}

	public void setScene(MessageScene scene) {
		this.scene = scene;
	}

	/**
	 * 关联测试环境<br>
	 * 探测时将会忽略测试场景中配置的测试环境
	 * @return
	 */
	public BusinessSystem getSystem() {
		return system;
	}

	public void setSystem(BusinessSystem system) {
		this.system = system;
	}

	/**
	 * 任务创建时间
	 * @return
	 */
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * 任务创建用户
	 * @return
	 */
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 任务状态<br>
	 * 0 - 已停止,未开启<br>
	 * 1 - 正在正常运行<br>
	 * 2 - 已启动，但是缺少数据导致无法运行测试
	 * 3 - 暂停
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 探测次数<br>
	 * 当任务被重新开启时将会重置
	 * @return
	 */
	public Integer getCallCount() {
		return callCount;
	}

	public void setCallCount(Integer callCount) {
		this.callCount = callCount;
	}

	public void setFirstCallTime(Timestamp firstCallTime) {
		this.firstCallTime = firstCallTime;
	}
	
	/**
	 * 
	 * 第一次探测时间<br>
	 * 重启开启任务将会重置
	 * @return
	 */
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getFirstCallTime() {
		return firstCallTime;
	}
	
	/**
	 * 最后一次调用时间<br>
	 * 重新开启任务将会重置
	 * @return
	 */
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getLastCallTime() {
		return lastCallTime;
	}

	public void setLastCallTime(Timestamp lastCallTime) {
		this.lastCallTime = lastCallTime;
	}

	/**
	 * 指定周期的数据分析结果<br>
	 * json格式保存,方便前台查看
	 * @return
	 */
	@JSON(serialize=false)
	public String getCycleAnalysisData() {
		return cycleAnalysisData;
	}

	public void setCycleAnalysisData(String cycleAnalysisData) {
		this.cycleAnalysisData = cycleAnalysisData;
	}

	/**
	 * 探测任务备注
	 * @return
	 */
	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * 由probeConfigJson转换的对象,方便调用
	 * @return
	 */
	public ProbeConfig getConfig() {
		if (config == null && StringUtils.isNotEmpty(probeConfigJson)) {
			JSONObject jsonObject = JSONObject.fromObject(probeConfigJson);
			config = (ProbeConfig) JSONObject.toBean(jsonObject, ProbeConfig.class);
		}
		return config;
	}

	public void setConfig(ProbeConfig config) {
		this.config = config;
	}
	
	public void setProbeConfigJson(String probeConfigJson) {
		this.probeConfigJson = probeConfigJson;
	}

	public String setProbeConfigJson() {
		if (config == null) {
			config = new ProbeConfig();
		}
		probeConfigJson = JSONObject.fromObject(config).toString();
		
		return probeConfigJson;
	}
	
	
	public void setLastResult(TestResult lastResult) {
		this.lastResult = lastResult;
	}
	
	/**
	 * 获取最近一次的测试结果,只有在探测任务运行状态时才会有
	 * @return
	 */
	public TestResult getLastResult() {
		InterfaceProbeService service = (InterfaceProbeService) FrameworkUtil.getSpringBean(InterfaceProbeService.class);		
		return service.getLastResult(this.probeId, this.lastCallTime);
	}
	
	/**
	 * 探测任务的详细配置
	 * @return
	 */
	@JSON(serialize=false)
	public String getProbeConfigJson() {
		return probeConfigJson;
	}
	
	
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		Object o = null;
		try {
			o = super.clone();
		} catch (Exception e) {
			
			LOGGER.warn(e.getMessage(), e);
		}
		return o;
	}


	@Override
	public String toString() {
		return "InterfaceProbe [probeId=" + probeId + ", probeType="
				+ probeType + ", scene=" + scene + ", system=" + system
				+ ", createTime=" + createTime + ", user=" + user + ", status="
				+ status + ", callCount=" + callCount + ", lastCallTime="
				+ lastCallTime + ", cycleAnalysisData=" + cycleAnalysisData
				+ ", mark=" + mark + ", config=" + config + "]";
	}
}
