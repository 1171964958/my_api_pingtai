package yi.master.statement.vo;

import java.sql.Timestamp;

import org.apache.struts2.json.annotations.JSON;

/**
 * 探测任务总览相关<br>
 * HQL组装属性实体
 * @author xuwangcheng
 * @version 1.0.0.0,2018.2.2
 *
 */
public class ProbeResultSynopsisView {
	
	private Timestamp opTime;
	private Integer qualityLevel;
	private String messageInfo;
	private Integer systemId;
	private String systemName;
	private Integer probeId;
	
	public ProbeResultSynopsisView(Timestamp opTime, Integer qualityLevel,
			String messageInfo, Integer systemId, String systemName,
			Integer probeId) {
		super();
		this.opTime = opTime;
		this.qualityLevel = qualityLevel;
		this.messageInfo = messageInfo;
		this.systemId = systemId;
		this.systemName = systemName;
		this.probeId = probeId;
	}

	public ProbeResultSynopsisView() {
		super();
		
	}
	
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getOpTime() {
		return opTime;
	}

	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}

	public Integer getQualityLevel() {
		return qualityLevel;
	}

	public void setQualityLevel(Integer qualityLevel) {
		this.qualityLevel = qualityLevel;
	}

	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public Integer getProbeId() {
		return probeId;
	}

	public void setProbeId(Integer probeId) {
		this.probeId = probeId;
	}
	
	
	
}
