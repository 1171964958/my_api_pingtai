package yi.master.business.advanced.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.advanced.bean.config.performancetest.PerformanceTestAnalyzeResult;
import yi.master.business.message.bean.TestResult;
import yi.master.business.user.bean.User;

/**
 * 性能测试结果详情
 * @author xuwangcheng
 * @version 20180627
 *
 */
public class PerformanceTestResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Map<String, Class<?>> map = new HashMap<>();
		
	static {
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[]{"yyyy-MM-dd HH:mm:ss"}), true);		
		map.put("time", java.util.Date.class);
		map.put("tps", java.lang.Double.class);
		map.put("responseTime", java.lang.Double.class);
		map.put("pressCpu", java.lang.Double.class);
		map.put("pressMemory", java.lang.Double.class);
		map.put("everyTimeDetails", java.lang.Integer.class);
	}
	
	private Integer ptResultId;
	/**
	 * 关联配置
	 */
	private PerformanceTestConfig performanceTestConfig;
	/**
	 * 接口名称：包含报文名和场景名称
	 */
	private String interfaceName;
	/**
	 * 测试环境名称
	 */
	private String systemName;
	/**
	 * 实际启用的最大线程数
	 */
	@CustomConditionSetting(operator=">")
	private Integer threadCount;
	/**
	 * 参数化文件路径（快照文件，与配置中的文件可能会不一样）
	 */
	private String parameterizedFilePath;
	/**
	 * 测试开始时间
	 */
	private Timestamp startTime;
	/**
	 * 测试结束时间
	 */
	private Timestamp finishTime;
	/**
	 * 结束标志:<br>
	 * Y-已结束  N-未结束
	 */
	@FieldRealSearch(names = {"已完成", "未完成"}, values = {"Y", "N"})
	private String finishFlag;
	/**
	 * 实际请求总次数
	 */
	private Integer requestCount;
	/**
	 * 实际测试时间,从第一个线程启动开始计算到最后一个线程关闭
	 */
	private Integer testTime;
	/**
	 * 结果详情文件，序列化,包含为TestResult对象
	 */
	private String detailsResultFilePath;
	/**
	 * 详细分析结果内容,json串保存,可转换为PerformanceTestAnalyzeResult对象
	 */
	private String analyzeResultJson;
	/**
	 * 测试用户
	 */
	private User user;
	/**
	 * 备注
	 */
	private String mark;
	/**
	 * 该测试结果的创建时间
	 */
	private Timestamp createTime;
	
	private PerformanceTestAnalyzeResult analyzeResult;

	private List<TestResult> testResults = new ArrayList<TestResult>();
	
	@FieldNameMapper(fieldPath="performanceTestConfig.ptName")
	private String ptName;
	
	@FieldNameMapper(ifSearch=false, ifOrder=false)
	@CustomConditionSetting(conditionType="")
	private String tps;
	
	@FieldNameMapper(ifSearch=false, ifOrder=false)
	@CustomConditionSetting(conditionType="")
	private String responseTime;
	
	@FieldNameMapper(fieldPath="user.realName")
	private String testUserName;
	
	@FieldNameMapper(fieldPath="startTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String startTimeText;

	
	public void setAnalyzeResult(PerformanceTestAnalyzeResult analyzeResult) {
		this.analyzeResult = analyzeResult;
	}
	
	@JSON(serialize=false)
	public PerformanceTestAnalyzeResult getAnalyzeResult() {
		if (this.analyzeResult == null) {
			if (StringUtils.isEmpty(this.analyzeResultJson)) {
				this.analyzeResult = new PerformanceTestAnalyzeResult();
			} else {				
				this.analyzeResult = (PerformanceTestAnalyzeResult) JSONObject.toBean(JSONObject.fromObject(this.analyzeResultJson), PerformanceTestAnalyzeResult.class, map);
			}
		}
		return analyzeResult;
	}
	
	
	
	public String getTestUserName() {
		return testUserName;
	}

	public void setTestUserName(String testUserName) {
		this.testUserName = testUserName;
	}

	public String getStartTimeText() {
		return startTimeText;
	}

	public void setStartTimeText(String startTimeText) {
		this.startTimeText = startTimeText;
	}

	public void setTestResults(List<TestResult> testResults) {
		this.testResults = testResults;
	}
	
	@JSON(serialize=false)
	public List<TestResult> getTestResults() {
		return testResults;
	}
	
	public void setTps(String tps) {
		this.tps = tps;
	}
	
	public String getTps() {
		return String.valueOf(getAnalyzeResult().getTpsAvg());
	}
	
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	
	public String getResponseTime() {
		return String.valueOf(getAnalyzeResult().getResponseTimeAvg());
	}
	
	public Integer getPtResultId() {
		return ptResultId;
	}

	public void setPtResultId(Integer ptResultId) {
		this.ptResultId = ptResultId;
	}

	@JSON(serialize=false)
	public PerformanceTestConfig getPerformanceTestConfig() {
		return performanceTestConfig;
	}


	public void setPerformanceTestConfig(PerformanceTestConfig performanceTestConfig) {
		this.performanceTestConfig = performanceTestConfig;
	}


	public String getInterfaceName() {
		return interfaceName;
	}


	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}


	public String getSystemName() {
		return systemName;
	}


	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}


	public Integer getThreadCount() {
		return threadCount;
	}


	public void setThreadCount(Integer threadCount) {
		this.threadCount = threadCount;
	}


	public String getParameterizedFilePath() {
		return parameterizedFilePath;
	}


	public void setParameterizedFilePath(String parameterizedFilePath) {
		this.parameterizedFilePath = parameterizedFilePath;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getStartTime() {
		return startTime;
	}


	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getFinishTime() {
		return finishTime;
	}


	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}


	public String getFinishFlag() {
		return finishFlag;
	}


	public void setFinishFlag(String finishFlag) {
		this.finishFlag = finishFlag;
	}


	public Integer getRequestCount() {
		return requestCount;
	}


	public void setRequestCount(Integer requestCount) {
		this.requestCount = requestCount;
	}


	public Integer getTestTime() {
		return testTime;
	}


	public void setTestTime(Integer testTime) {
		this.testTime = testTime;
	}


	public String getDetailsResultFilePath() {
		return detailsResultFilePath;
	}


	public void setDetailsResultFilePath(String detailsResultFilePath) {
		this.detailsResultFilePath = detailsResultFilePath;
	}

	@JSON(serialize=false)
	public String getAnalyzeResultJson() {
		return analyzeResultJson;
	}


	public void setAnalyzeResultJson(String analyzeResultJson) {
		this.analyzeResultJson = analyzeResultJson;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getMark() {
		return mark;
	}


	public void setMark(String mark) {
		this.mark = mark;
	}


	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public void setPtName(String ptName) {
		this.ptName = ptName;
	}
	
	public String getPtName() {
		if (StringUtils.isBlank(ptName)) {
			return getPerformanceTestConfig().getPtName();
		}
		return ptName;
	}
	
	@Override
	public String toString() {
		return "PerformanceTestResult [ptResultId=" + ptResultId
				+ ", performanceTestConfig=" + performanceTestConfig
				+ ", interfaceName=" + interfaceName + ", systemName="
				+ systemName + ", threadCount=" + threadCount
				+ ", parameterizedFilePath=" + parameterizedFilePath
				+ ", startTime=" + startTime + ", finishTime=" + finishTime
				+ ", finishFlag=" + finishFlag + ", requestCount="
				+ requestCount + ", testTime=" + testTime
				+ ", detailsResultFilePath=" + detailsResultFilePath
				+ ", analyzeResultJson=" + analyzeResultJson + ", user=" + user
				+ ", mark=" + mark + ", createTime=" + createTime + "]";
	}	
	
}
