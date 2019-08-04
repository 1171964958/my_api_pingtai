package yi.master.business.advanced.service;

import java.sql.Timestamp;
import java.util.List;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.base.service.BaseService;
import yi.master.business.message.bean.TestResult;
import yi.master.statement.vo.ProbeResultSynopsisView;

public interface InterfaceProbeService extends BaseService<InterfaceProbe> {
	/**
	 * 更新探测配置
	 * @param probeId
	 * @param configJson
	 */
	void updateConfig(Integer probeId, String configJson);
	
	/**
	 * 获取所有运行状态的探测任务
	 * @return
	 */
	List<InterfaceProbe> findRunTasks();
	
	/**
	 * 获取最近一次的测试结果
	 * @param resultId
	 * @param lastRunTime
	 * @return
	 */
	TestResult getLastResult(Integer probeId, Timestamp lastRunTime);
	
	/**
	 * 获取当前运行时段的测试结果集
	 * @param resultId
	 * @param firstRunTime
	 * @return
	 */
	List<TestResult> findProbeResults(Integer probeId, Timestamp startTime);
	
	/**
	 * 获取指定时间范围的探测结果集
	 * @param probeId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<TestResult> listProbeResultsByTimeRange(Integer probeId, Timestamp startTime, Timestamp endTime);
	
	/**
	 * 获取所有当前正在运行中的探测任务N天之内结果
	 * @param dateNum
	 * @return
	 */
	List<ProbeResultSynopsisView> listProbeBeforeResultInfo(int dateNum);
}
