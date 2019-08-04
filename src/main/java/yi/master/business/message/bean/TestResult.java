package yi.master.business.message.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldNameMapper;
import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.bean.config.probe.ProbeConfig;

/**
 * 测试详情结果
 * @author xuwangcheng
 * @version 1.0.0.0,2017.07.12
 *
 */
public class TestResult implements Serializable, Comparable<TestResult> {
	
	/**
	 * 测试结果运行状态的说明字典
	 */
	public static final Map<String, String> RUN_STATUS_DICT = new HashMap<String, String>();
	/**
	 * 测试结果的最终质量等级说明字典
	 */
	public static final Map<Integer, String> QUALITY_LEVEL_DICT = new HashMap<Integer, String>();
	
	static {
		RUN_STATUS_DICT.put("0", "正常");
		RUN_STATUS_DICT.put("1", "失败");
		RUN_STATUS_DICT.put("2", "异常");
		QUALITY_LEVEL_DICT.put(ProbeConfig.PROBE_EXCELLENT_LEVEL, "优秀");
		QUALITY_LEVEL_DICT.put(ProbeConfig.PROBE_NORMAL_LEVEL, "正常");
		QUALITY_LEVEL_DICT.put(ProbeConfig.PROBE_PROBLEMATIC_LEVEL, "有问题");
		QUALITY_LEVEL_DICT.put(ProbeConfig.PROBE_SERIOUS_LEVEL, "严重");
	}
	
	private static final long serialVersionUID = 1L;
	private Integer resultId;
	
	/**
	 * 对应测试场景
	 */
	private MessageScene messageScene;
	
	/**
	 * 所属测试报告
	 */
	private TestReport testReport;
	
	/**
	 * 实际使用的URL
	 */
	private String requestUrl;
	
	/**
	 *实际用到的请求入参报文
	 */
	private String requestMessage;
	
	/**
	 * 实际返回的报文
	 */
	private String responseMessage;
	
	/**
	 * response headers，使用json串保存
	 */
	private String headers;
	
	/**
	 * 测试耗时ms
	 */
	private Integer useTime;
	/**
	 * 测试结果状态，必须要保证验证也成功<br>
	 * 0 - SUCCESS 成功
	 * 1 - FAIL 失败，如返回验证不成功、没有返回等
	 * 2 - STOP 异常停止或者未完成，请求地址不通、缺少测试数据等
	 */
	@FieldNameMapper(ifSearch=false)
	private String runStatus;
	
	/**
	 * 返回状态码，自定义
	 * <br>比如http协议中，就标识htpp状态码如200,302等
	 */
	private String statusCode;
	
	/**
	 * 发送请求的时间
	 */
	private Timestamp opTime;
	/**
	 * 格式：接口名,报文名,场景名
	 * <br>记录的是测试当时的状态
	 */
	private String messageInfo;
	
	/**
	 * 请求协议类型
	 */
	private String protocolType;
	
	/**
	 * 验证说明、失败说明等
	 */
	private String mark;
	
	/**
	 * 测试环境名称
	 */
	private String businessSystemName;
	
	/**
	 * 包含的组合场景多个测试结果
	 */
	private Set<TestResult> complexSceneResults = new TreeSet<TestResult>();
	
	/**
	 * 对应组合场景的测试详情
	 */
	private TestResult complexResult;
	
	/**
	 * 接口探测任务
	 */
	private InterfaceProbe interfaceProbe;
	
	/**
	 * 测试的质量等级<br>
	 * 接口探测任务生成的测试结果才会去评定
	 */
	private Integer qualityLevel;
	
	/**
	 * 格式类型
	 */
	private String messageType;
	
	private String threadName;
	
	public TestResult() {
		super();
		
	}

	public TestResult(MessageScene messageScene,String messageInfo,
			String requestUrl, String requestMessage, String responseMessage,
			Integer useTime, String runStatus, String statusCode,
			Timestamp opTime, String protocolType,String mark) {
		super();
		this.messageScene = messageScene;
		this.messageInfo = messageInfo;
		this.requestUrl = requestUrl;
		this.requestMessage = requestMessage;
		this.responseMessage = responseMessage;
		this.useTime = useTime;
		this.runStatus = runStatus;
		this.statusCode = statusCode;
		this.opTime = opTime;
		this.protocolType = protocolType;
		this.mark = mark;
	}

	
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	public void setHeaders(String headers) {
		this.headers = headers;
	}
	
	public String getHeaders() {
		return headers;
	}
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public String getMessageType() {
		return messageType;
	}
	
	public void setQualityLevel(Integer qualityLevel) {
		this.qualityLevel = qualityLevel;
	}
	
	public Integer getQualityLevel() {
		return qualityLevel;
	}
	
	public void setInterfaceProbe(InterfaceProbe interfaceProbe) {
		this.interfaceProbe = interfaceProbe;
	}
	
	@JSON(serialize=false)
	public InterfaceProbe getInterfaceProbe() {
		return interfaceProbe;
	}
	
	public void setBusinessSystemName(String businessSystemName) {
		this.businessSystemName = businessSystemName;
	}
	
	public String getBusinessSystemName() {
		if (businessSystemName == null) {
			return "";
		}
		return businessSystemName;
	}
	
	public void setComplexResult(TestResult complexResult) {
		this.complexResult = complexResult;
	}
	
	@JSON(serialize=false)
	public TestResult getComplexResult() {
		return complexResult;
	}
	
	public void setComplexSceneResults(Set<TestResult> complexSceneResults) {
		this.complexSceneResults = complexSceneResults;
	}
	
	public Set<TestResult> getComplexSceneResults() {
		
		return complexSceneResults;
	}
	
	public String getMark() {
		return mark;
	}
	
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}

	public Integer getResultId() {
		return resultId;
	}

	public void setResultId(Integer resultId) {
		this.resultId = resultId;
	}
	
	@JSON(serialize=false)
	public MessageScene getMessageScene() {
		return messageScene;
	}

	public void setMessageScene(MessageScene messageScene) {
		this.messageScene = messageScene;
	}

	@JSON(serialize=false)
	public TestReport getTestReport() {
		return testReport;
	}

	public void setTestReport(TestReport testReport) {
		this.testReport = testReport;
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

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Integer getUseTime() {
		return useTime;
	}

	public void setUseTime(Integer useTime) {
		this.useTime = useTime;
	}

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getOpTime() {
		return opTime;
	}

	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}
	
	public String getProtocolType() {
		return protocolType;
	}
	
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	@Override
	public int compareTo(TestResult o) {
		
		//return -(o.getOpTime().compareTo(this.opTime));
		return this.opTime.compareTo(o.getOpTime());
	}

	@Override
	public boolean equals(Object obj) {
		
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "TestResult [resultId=" + resultId + ", requestUrl="
				+ requestUrl + ",headers=" + headers + ", useTime="
				+ useTime + ", runStatus=" + runStatus + ", statusCode="
				+ statusCode + ", opTime=" + opTime + ", messageInfo="
				+ messageInfo + ", protocolType=" + protocolType + ", mark="
				+ mark + ", businessSystemName=" + businessSystemName
				+ ", complexSceneResults=" + complexSceneResults
				+ ", complexResult=" + complexResult + ", qualityLevel="
				+ qualityLevel + ", messageType=" + messageType + "]";
	}
	
}
