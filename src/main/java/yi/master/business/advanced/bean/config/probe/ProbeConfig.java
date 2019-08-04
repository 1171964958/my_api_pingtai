package yi.master.business.advanced.bean.config.probe;

import java.io.Serializable;

import yi.master.business.message.bean.TestResult;
import yi.master.constant.MessageKeys;

/**
 * 探测任务的详细配置
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.24
 *
 */
public class ProbeConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	public static final Integer PROBE_EXCELLENT_LEVEL = 1;
	
	public static final Integer PROBE_NORMAL_LEVEL = 2;
	
	public static final Integer PROBE_PROBLEMATIC_LEVEL = 3;
	
	public static final Integer PROBE_SERIOUS_LEVEL = 4;
	
	private Integer intervalTime = 3600;
	
	private Integer maxCallTime = -1;
	
	private Integer maxDurationTime = -1;
	
	private String addHeaderFlag = "0";
	
	private String notifyType = "0";
	
	private Integer analysisCycleTime = 24;
	
	private String notifyLevel = "3";
	
	private Integer maxResponseTime = 5000;
	
	/**
	 * 自定义的邮件推送收信人地址
	 */
	private String receiveAddress;
	
	/**
	 * 自定义的邮件推送抄送人地址
	 */
	private String copyAddress;
	

	public ProbeConfig() {
		super();
		
	}
	
	/**
	 * 根据相关配置判定当前测试结果的质量等级
	 * @param result
	 * @return
	 */
	public Integer judgeLevel (TestResult result) {
		//是否为缺少数据
		if (MessageKeys.NO_ENOUGH_TEST_DATA_RESULT_MARK.equals(result.getMark())) {
			return 0;
		}
		
		//判断是否验证正确?
		if (MessageKeys.TEST_RUN_STATUS_SUCCESS.equals(result.getRunStatus())) {
			return (result.getUseTime() > this.maxResponseTime) ? PROBE_NORMAL_LEVEL : PROBE_EXCELLENT_LEVEL;
		} 
		
		if (MessageKeys.TEST_RUN_STATUS_FAIL.equals(result.getRunStatus())) {
			return PROBE_PROBLEMATIC_LEVEL;
		}
		
		return PROBE_SERIOUS_LEVEL;
		
		
	}
	
	/**
	 * 探测间隔时间<br>
	 * 单位秒,指定范围为：10s - 86400s
	 * @return
	 */
	public Integer getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(Integer intervalTime) {
		this.intervalTime = intervalTime;
	}

	/**
	 * 最大调用次数<br>
	 * 为空代表不限
	 * @return
	 */
	public Integer getMaxCallTime() {
		return maxCallTime;
	}

	public void setMaxCallTime(Integer maxCallTime) {
		this.maxCallTime = maxCallTime;
	}

	/**
	 * 最大持续时间<br>
	 * 单位小时,为空代表不限
	 * @return
	 */
	public Integer getMaxDurationTime() {
		return maxDurationTime;
	}

	public void setMaxDurationTime(Integer maxDurationTime) {
		this.maxDurationTime = maxDurationTime;
	}

	/**
	 * 对于探测任务，是否在请求的时候加上指定的header<br>
	 * 0 - 不添加  1 -添加
	 * 仅http/https协议有效
	 * @return
	 */
	public String getAddHeaderFlag() {
		return addHeaderFlag;
	}

	public void setAddHeaderFlag(String addHeaderFlag) {
		this.addHeaderFlag = addHeaderFlag;
	}

	/**
	 * 通知方式<br>
	 * 0 - 不通知<br>
	 * 1 - 站内信通知<br>
	 * 2 - 站内信和邮件通知
	 * @return
	 */
	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	/**
	 * 统计并分析探测任务的周期<br>
	 * 时间 小时单位
	 * @return
	 */
	public Integer getAnalysisCycleTime() {
		return analysisCycleTime;
	}
	
	public void setAnalysisCycleTime(Integer analysisCycleTime) {
		this.analysisCycleTime = analysisCycleTime;
	}
	
	/**
	 * 达到何种质量等级的时候进行通知<br>
	 * 质量等级分为：1 - ExcellentLevel(优秀的) 2 - NormalLevel(正常的) 3 - ProblematicLevel(有问题的) 4 - SeriousLevel(严重的)
	 * <hr>
	 * <strong>ExcellentLevel</strong>:有返回且验证正确,响应时间不大于maxResponseTime<br>
	 * <strong>NormalLevel</strong>:有返回且验证正确,响应时间大于maxResponseTime<br>
	 * <strong>ProblematicLevel</strong>:有返回但是验证不正确<br>
	 * <strong>SeriousLevel</strong>:无法连接，没有返回<br>
	 * 排除没有测试数据的情况
	 * @return
	 */
	public String getNotifyLevel() {
		return notifyLevel;
	}

	public void setNotifyLevel(String notifyLevel) {
		this.notifyLevel = notifyLevel;
	}

	
	public void setMaxResponseTime(Integer maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}
	
	/**
	 * 最大响应时间值,单位ms
	 * @return
	 */
	public Integer getMaxResponseTime() {
		return maxResponseTime;
	}
	
	
	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}
	
	public String getReceiveAddress() {
		return receiveAddress;
	}
	
	public void setCopyAddress(String copyAddress) {
		this.copyAddress = copyAddress;
	}
	
	public String getCopyAddress() {
		return copyAddress;
	}

	@Override
	public String toString() {
		return "ProbeConfig [intervalTime=" + intervalTime + ", maxCallTime="
				+ maxCallTime + ", maxDurationTime=" + maxDurationTime
				+ ", addHeaderFlag=" + addHeaderFlag + ", notifyType="
				+ notifyType + ", analysisCycleTime=" + analysisCycleTime
				+ ", notifyLevel=" + notifyLevel + ", maxResponseTime="
				+ maxResponseTime + "]";
	}

	
}
