package yi.master.business.localize.shanxi.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldRealSearch;

/**
 * web自动化测试任务
 * @author xuwangcheng
 * @version 1.0.0.0,2018.3.31
 *
 */
public class WebScriptTask implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer taskId;
	/**
	 * 关联模块
	 */
	private WebScriptModule module;
	/**
	 * 类型,0 - 内部调用 1 - 外部接口调用
	 */
	@FieldRealSearch(names = {"内部任务", "外部任务"}, values = {"0", "1"})
	private String taskType;
	/**
	 * 开始执行时间
	 */
	private Timestamp startTime;
	/**
	 * 结束时间
	 */
	private Timestamp finishTime;
	/**
	 * 结束标识 Y - 已结束  N - 未结束
	 */
	@FieldRealSearch(names = {"已完成", "未完成"}, values = {"Y", "N"})
	private String finishFlag;
	/**
	 * 该次报告路径
	 */
	private String reportPath;
	/**
	 * 命令行执行的返回内容
	 */
	private String mark;
	/**
	 * 详细结果json
	 */
	private String detailJson;
	
	/**
	 * 唯一全局标识
	 */
	private String guid;
	

	public WebScriptTask(Integer taskId, WebScriptModule module,
			String taskType, Timestamp startTime, Timestamp finishTime,
			String finishFlag, String reportPath, String mark) {
		super();
		this.taskId = taskId;
		this.module = module;
		this.taskType = taskType;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.finishFlag = finishFlag;
		this.reportPath = reportPath;
		this.mark = mark;
	}

	public WebScriptTask() {
		super();
		
	}

	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public void setDetailJson(String detailJson) {
		this.detailJson = detailJson;
	}
	
	public String getDetailJson() {
		return detailJson;
	}
	
	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public WebScriptModule getModule() {
		return module;
	}

	public void setModule(WebScriptModule module) {
		this.module = module;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
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

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public String getMark() {
		return mark;
	}

	@Override
	public String toString() {
		return "WebScriptTask [taskId=" + taskId + ", module=" + module
				+ ", taskType=" + taskType + ", startTime=" + startTime
				+ ", finishTime=" + finishTime + ", finishFlag=" + finishFlag
				+ ", reportPath=" + reportPath + ", mark=" + mark + "]";
	}

}
