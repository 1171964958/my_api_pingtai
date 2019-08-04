package yi.master.business.message.service;

import java.sql.Timestamp;
import java.util.List;

import yi.master.business.base.service.BaseService;
import yi.master.business.message.bean.TestResult;

public interface TestResultService extends BaseService<TestResult> {
	/**
	 * 查找指定测试报告的测试详情结果集
	 * @param reportId
	 * @return
	 */
	List<TestResult> listByReportId(Integer reportId);
	
	/**
	 * 获取指定探测结果集指定时间段内的各质量等级的结果数量
	 * @param probeId
	 * @param startTime
	 * @param lastTime
	 * @return
	 */
	int[] countProbeResultQuality(Integer probeId, Timestamp startTime, Timestamp lastTime);
	
	/**
	 * 指定时间内指定探测结果集,按日统计不同测试结果状态的数量
	 * @param probeId
	 * @param startTime
	 * @param lastTime
	 * @return List&lt;List&gt;[]  该数组依次分别为成功、失败、异常, list包含为time,count
	 */
	Object[] countProbeResultStatusByDay(Integer probeId, Timestamp startTime, Timestamp lastTime);
	
	/**
	 * 指定时间内指定探测结果集,按小时统计不同测试结果状态的数量
	 * @param probeId
	 * @param startTime
	 * @param lastTime
	 * @return List&lt;List&gt;[]  该数组依次分别为成功、失败、异常, list包含为time,count
	 */
	Object[] countProbeResultStatusByHour(Integer probeId, Timestamp startTime, Timestamp lastTime);
	
	/**
	 * 指定时间内指定探测结果集,统计随时间的探测响应时间,排除stop状态的0ms情况
	 * @param probeId
	 * @param startTime
	 * @param lastTime
	 * @return List数组，list[0]为探测时间列表  list[1]为相对应的响应时间ms列表
	 */
	List[] countProbeResultResponseTime(Integer probeId, Timestamp startTime, Timestamp lastTime);
	
	/**
	 * 通过reportId查找当前测试报告包含的测试环境
	 * @param reportId
	 * @return 测试环境名称 数组
	 */
	String[] getReportSystemNames(Integer reportId);
	
}
