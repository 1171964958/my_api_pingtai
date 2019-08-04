package yi.master.statement.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;

/**
 * 
 * 探测结果分析报表视图
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.29
 *
 */
public class ProbeResultAnalyzeView {
	
	private List<String> responseTime = new ArrayList<String>();
	
	private List<String> opTime = new ArrayList<String>();
	
	private Timestamp startTime;
	
	private Timestamp lastTime;
	
	private Integer totalCount;
	
	private String probeName;
	
	private ProbeResultAnalyzeQuality qualityCount;
	
	private ProbeResultAnalyzeStability stabilityCount;

	public ProbeResultAnalyzeView() {
		super();
		
	}
	
	public ProbeResultAnalyzeView(Timestamp startTime, Timestamp lastTime,
			Integer totalCount, String probeName) {
		super();
		this.startTime = startTime;
		this.lastTime = lastTime;
		this.totalCount = totalCount;
		this.probeName = probeName;
	}


	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getLastTime() {
		return lastTime;
	}

	public void setLastTime(Timestamp lastTime) {
		this.lastTime = lastTime;
	}

	public Integer getTotalCount() {
		return qualityCount.getExcellentLevelCount() + qualityCount.getNormalLevelCount()
				+ qualityCount.getProblematicLevelCount() + qualityCount.getSeriousLevelCount();
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public String getProbeName() {
		return probeName;
	}

	public void setProbeName(String probeName) {
		this.probeName = probeName;
	}

	public List<String> getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(List<String> responseTime) {
		this.responseTime = responseTime;
	}

	public List<String> getOpTime() {
		return opTime;
	}

	public void setOpTime(List<String> opTime) {
		this.opTime = opTime;
	}

	public ProbeResultAnalyzeQuality getQualityCount() {
		return qualityCount;
	}

	public void setQualityCount(ProbeResultAnalyzeQuality qualityCount) {
		this.qualityCount = qualityCount;
	}

	public ProbeResultAnalyzeStability getStabilityCount() {
		return stabilityCount;
	}

	public void setStabilityCount(ProbeResultAnalyzeStability stabilityCount) {
		this.stabilityCount = stabilityCount;
	}

}
