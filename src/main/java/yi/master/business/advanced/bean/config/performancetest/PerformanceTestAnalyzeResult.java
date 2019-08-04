package yi.master.business.advanced.bean.config.performancetest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yi.master.util.PracticalUtils;

/**
 * 性能测试结果详细内容，包含TPS、响应时间、错误率、时间、压力机（本机）资源CPU内存
 * @author xuwangcheng
 * @version 20180628
 *
 */
public class PerformanceTestAnalyzeResult {
	
	private List<Double> tps = new ArrayList<Double>();
	private List<Double> responseTime = new ArrayList<Double>();
	private List<Date> time = new ArrayList<Date>();
	private List<Double> pressCpu = new ArrayList<Double>();
	private List<Double> pressMemory = new ArrayList<Double>();
	private Double tpsMax = 0.00;
	private Double tpsMin = 0.00;
	private Double tpsAvg = 0.00;
	private Double responseTimeMax = 0.00;
	private Double responseTimeMin = 0.00;
	private Double responseTimeAvg = 0.00;
	
	private Integer totalCount = 0;
	private Integer successCount = 0;
	private Integer failCount = 0;
	
	private List<String> errorMsg = new ArrayList<String>();
	private List<String> infoMsg = new ArrayList<String>();
	private List<String> resultMark = new ArrayList<String>();
	
	/**
	 * 记录每段时间内的数据详情,包含总事务，成功数量，失败数量<br>
	 * 0 - 总数<br>
	 * 1 - 成功数<br>
	 * 2 - 失败数
	 */
	private List<List<Integer>> everyTimeDetails = new ArrayList<List<Integer>>();
	
	/**
	 * 根据最大最小来筛选并重新处理获取数据
	 * @param maxTime
	 * @param minTime
	 */
	public PerformanceTestAnalyzeResult reAnaylzeByTime(Date maxTime, Date minTime) {
		if (maxTime == null || minTime == null) {
			return this;
		}
		if (minTime.getTime() > maxTime.getTime()) {
			return this;
		}

		int minIndex = 0;
		int maxIndex = time.size() - 1;
		for (int i = 0;i< time.size();i++) {
			if (minTime.getTime() < time.get(i).getTime()) {
				break;
			} else {
				minIndex = i;
			}
		}
		for (int i = (time.size() - 1);i > minIndex;i--) {
			if (maxTime.getTime() > time.get(i).getTime()) {
				break;
			} else {
				maxIndex = i;
			}			
		}
		
		if ((minIndex == 0) && (maxIndex == time.size() - 1)) {
			return this;
		}
		
		//截取对应时间范围内的数据
		tps = tps.subList(minIndex, maxIndex + 1);
		responseTime = responseTime.subList(minIndex, maxIndex + 1);
		time = time.subList(minIndex, maxIndex + 1);
		pressCpu = pressCpu.subList(minIndex, maxIndex + 1);
		pressMemory = pressMemory.subList(minIndex, maxIndex + 1);
		everyTimeDetails = everyTimeDetails.subList(minIndex, maxIndex + 1);
		
		
		//计算整体最大/最小/平均数据
		totalCount = 0;
		successCount = 0;
		failCount = 0;
		
		tpsMax = 0.00;
		tpsMin = 0.00;
		tpsAvg = 0.00;
		responseTimeMax = 0.00;
		responseTimeMin = 0.00;
		responseTimeAvg = 0.00;
		
		double responseTimeTotal = 0.00;
		
		for (int i = 0;i < time.size();i++) {
			List<Integer> counts = everyTimeDetails.get(i);
			totalCount += counts.get(0);
			successCount += counts.get(1);
			failCount += counts.get(2);
			
			if (tpsMax < tps.get(i)) {
				tpsMax = tps.get(i);
			}
			if (tpsMin > tps.get(i) || tpsMin == 0.00) {
				tpsMin = tps.get(i);
			}
			
			responseTimeTotal += (responseTime.get(i) * counts.get(1));
			if (responseTimeMax < responseTime.get(i)) {
				responseTimeMax = responseTime.get(i);
			}
			if (responseTimeMin > responseTime.get(i) || responseTimeMin == 0.00) {
				responseTimeMin = responseTime.get(i);
			}
			
		}
		double differTime = (time.get(time.size() - 1).getTime() - time.get(0).getTime()) / 1000.00;
		tpsAvg = (double) Math.round((successCount / differTime) * 100) / 100;
		responseTimeAvg = (double) Math.round((responseTimeTotal / successCount) * 100) / 100;	
		
		return this;
	}
	
	
	public List<List<Integer>> getEveryTimeDetails() {
		return everyTimeDetails;
	}


	public void setEveryTimeDetails(List<List<Integer>> everyTimeDetails) {
		this.everyTimeDetails = everyTimeDetails;
	}


	public List<Double> getTps() {
		return tps;
	}
	public void setTps(List<Double> tps) {
		this.tps = tps;
	}
	public List<Double> getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(List<Double> responseTime) {
		this.responseTime = responseTime;
	}
	public List<Date> getTime() {
		return time;
	}
	public void setTime(List<Date> time) {
		this.time = time;
	}
	public List<Double> getPressCpu() {
		return pressCpu;
	}
	public void setPressCpu(List<Double> pressCpu) {
		this.pressCpu = pressCpu;
	}
	public List<Double> getPressMemory() {
		return pressMemory;
	}
	public void setPressMemory(List<Double> pressMemory) {
		this.pressMemory = pressMemory;
	}
	public Double getTpsMax() {
		return tpsMax;
	}
	public void setTpsMax(Double tpsMax) {
		this.tpsMax = tpsMax;
	}
	public Double getTpsMin() {
		return tpsMin;
	}
	public void setTpsMin(Double tpsMin) {
		this.tpsMin = tpsMin;
	}
	public Double getTpsAvg() {
		return tpsAvg;
	}
	public void setTpsAvg(Double tpsAvg) {
		this.tpsAvg = tpsAvg;
	}
	public Double getResponseTimeMax() {
		return responseTimeMax;
	}
	public void setResponseTimeMax(Double responseTimeMax) {
		this.responseTimeMax = responseTimeMax;
	}
	public Double getResponseTimeMin() {
		return responseTimeMin;
	}
	public void setResponseTimeMin(Double responseTimeMin) {
		this.responseTimeMin = responseTimeMin;
	}
	public Double getResponseTimeAvg() {
		return responseTimeAvg;
	}
	public void setResponseTimeAvg(Double responseTimeAvg) {
		this.responseTimeAvg = responseTimeAvg;
	}
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}
	public Integer getFailCount() {
		return failCount;
	}
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	public List<String> getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(List<String> errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List<String> getInfoMsg() {
		return infoMsg;
	}
	public void setInfoMsg(List<String> infoMsg) {
		this.infoMsg = infoMsg;
	}
	public List<String> getResultMark() {
		return resultMark;
	}
	public void setResultMark(List<String> resultMark) {
		this.resultMark = resultMark;
	}
}
