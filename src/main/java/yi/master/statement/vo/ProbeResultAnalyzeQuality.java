package yi.master.statement.vo;

/**
 * 探测结果分析-质量等级统计
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.29
 *
 */
public class ProbeResultAnalyzeQuality {
	
	private Integer excellentLevelCount;
	private Integer normalLevelCount;
	private Integer problematicLevelCount;
	private Integer seriousLevelCount;
	
	public ProbeResultAnalyzeQuality(Integer excellentLevelCount,
			Integer normalLevelCount, Integer problematicLevelCount,
			Integer seriousLevelCount) {
		super();
		this.excellentLevelCount = excellentLevelCount;
		this.normalLevelCount = normalLevelCount;
		this.problematicLevelCount = problematicLevelCount;
		this.seriousLevelCount = seriousLevelCount;
	}
	
	public ProbeResultAnalyzeQuality() {
		super();
		
	}
	public Integer getExcellentLevelCount() {
		return excellentLevelCount;
	}
	public void setExcellentLevelCount(Integer excellentLevelCount) {
		this.excellentLevelCount = excellentLevelCount;
	}
	public Integer getNormalLevelCount() {
		return normalLevelCount;
	}
	public void setNormalLevelCount(Integer normalLevelCount) {
		this.normalLevelCount = normalLevelCount;
	}
	public Integer getProblematicLevelCount() {
		return problematicLevelCount;
	}
	public void setProblematicLevelCount(Integer problematicLevelCount) {
		this.problematicLevelCount = problematicLevelCount;
	}
	public Integer getSeriousLevelCount() {
		return seriousLevelCount;
	}
	public void setSeriousLevelCount(Integer seriousLevelCount) {
		this.seriousLevelCount = seriousLevelCount;
	}
	
	
}
