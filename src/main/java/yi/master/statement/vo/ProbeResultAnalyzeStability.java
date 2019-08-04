package yi.master.statement.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 探测结果统计-每个时间段内各状态的结果数量
 * @author xuwangcheng
 * @version 1.0.0.0, 2018.1.29
 *
 */
public class ProbeResultAnalyzeStability {
	
	private List<String> timeBucket = new ArrayList<String>(); 
	
	private List<Integer> successCount = new ArrayList<Integer>();
	
	private List<Integer> failCount = new ArrayList<Integer>();
	
	private List<Integer> stopCount = new ArrayList<Integer>();

	public ProbeResultAnalyzeStability() {
		super();
		
	}

	

	public List<String> getTimeBucket() {
		return timeBucket;
	}

	public void setTimeBucket(List<String> timeBucket) {
		this.timeBucket = timeBucket;
	}

	public List<Integer> getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(List<Integer> successCount) {
		this.successCount = successCount;
	}

	public List<Integer> getFailCount() {
		return failCount;
	}

	public void setFailCount(List<Integer> failCount) {
		this.failCount = failCount;
	}

	public List<Integer> getStopCount() {
		return stopCount;
	}

	public void setStopCount(List<Integer> stopCount) {
		this.stopCount = stopCount;
	}
	
	
	
}
