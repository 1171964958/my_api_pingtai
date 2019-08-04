package yi.master.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.bean.config.probe.ProbeConfig;
import yi.master.business.base.service.BaseService;
import yi.master.business.message.service.TestResultService;
import yi.master.statement.vo.ProbeResultAnalyzeQuality;
import yi.master.statement.vo.ProbeResultAnalyzeStability;
import yi.master.statement.vo.ProbeResultAnalyzeView;
import yi.master.statement.vo.ProbeResultSynopsisView;
import yi.master.statement.vo.StatisticalQuantity;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;

/**
 * 数据报表分析
 * @author Administrator
 *
 */
public class AnalyzeUtil {
	
	public static final String STATISTICAL_QUANTITY_INTERFACE_NAME = "interfaceInfo";
	
	public static final String STATISTICAL_QUANTITY_MESSAGE_NAME = "message";
	
	public static final String STATISTICAL_QUANTITY_SCENE_NAME = "messageScene";
	
	public static final String STATISTICAL_QUANTITY_SET_NAME = "testSet";
	
	public static final String STATISTICAL_QUANTITY_REPORT_NAME = "testReport";
	
	/**
	 * 测试统计
	 */
	private static Map<String, StatisticalQuantity> statistics = new HashMap<String, StatisticalQuantity>();
	
	static {
		statistics.put(STATISTICAL_QUANTITY_INTERFACE_NAME, new StatisticalQuantity(STATISTICAL_QUANTITY_INTERFACE_NAME));
		statistics.put(STATISTICAL_QUANTITY_MESSAGE_NAME, new StatisticalQuantity(STATISTICAL_QUANTITY_MESSAGE_NAME));
		statistics.put(STATISTICAL_QUANTITY_SCENE_NAME, new StatisticalQuantity(STATISTICAL_QUANTITY_SCENE_NAME));
		statistics.put(STATISTICAL_QUANTITY_SET_NAME, new StatisticalQuantity(STATISTICAL_QUANTITY_SET_NAME));
		statistics.put(STATISTICAL_QUANTITY_REPORT_NAME, new StatisticalQuantity(STATISTICAL_QUANTITY_REPORT_NAME));
	}
	
	/**
	 * 统计测试接口、测试报文、测试场景、测试集、测试报告的报表数据<br>
	 * 包含本日、昨日、本周、本月、总共
	 * @return Map<String, StatisticalQuantity>
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, StatisticalQuantity> countStatistics () {
		for (StatisticalQuantity sl:statistics.values()) {
			BaseService service = (BaseService) FrameworkUtil.getSpringBean(sl.getItemName() + "Service");
			sl.setTotalCount(service.totalCount());
			sl.setTodayCount(service.countByTime("createTime", PracticalUtils.getTodayZeroTime()));
			sl.setYesterdayCount(service.countByTime("createTime", PracticalUtils.getYesterdayZeroTime(), PracticalUtils.getTodayZeroTime()));
			sl.setThisWeekCount(service.countByTime("createTime", PracticalUtils.getThisWeekFirstDayZeroTime()));
			sl.setThisMonthCount(service.countByTime("createTime", PracticalUtils.getThisMonthFirstDayZeroTime()));
		}
		
		return statistics;
	}
	
	
	/**
	 * 分析探测结果集并返回报表
	 * @param probe
	 * @param results
	 * @return
	 */
	public static ProbeResultAnalyzeView analyzeProbeResults (InterfaceProbe probe) {
		TestResultService service = (TestResultService) FrameworkUtil.getSpringBean(TestResultService.class);
		
		ProbeResultAnalyzeView view = new ProbeResultAnalyzeView(probe.getFirstCallTime(), probe.getLastCallTime(),
				0, probe.getScene().getInterfaceName() 
				+ "-" + probe.getScene().getMessageName() + "-" + probe.getScene().getSceneName() + " " 
				+ probe.getSystem().getSystemName());
		
		int[] qualityCount = service.countProbeResultQuality(probe.getProbeId(), probe.getFirstCallTime(), probe.getLastCallTime());
		view.setQualityCount(new ProbeResultAnalyzeQuality(qualityCount[0], qualityCount[1], qualityCount[2], qualityCount[3]));
		
		if (view.getTotalCount() < 1) {
			return null;
		}
				
		List[] responseTime = service.countProbeResultResponseTime(probe.getProbeId(), probe.getFirstCallTime(), probe.getLastCallTime());

		view.setResponseTime(responseTime[1]);
		view.setOpTime(responseTime[0]);

		Object[] runStatusCount = null; 
		//统计周期,根据startTime和lastTime以及intervalTime来选择是按天还是按小时
		if (((view.getLastTime().getTime() - view.getStartTime().getTime()) / (1000 * 60 * 60 * 24)) >= 15) {
			runStatusCount = service.countProbeResultStatusByDay(probe.getProbeId(), probe.getFirstCallTime(), probe.getLastCallTime());
		} else {
			runStatusCount = service.countProbeResultStatusByHour(probe.getProbeId(), probe.getFirstCallTime(), probe.getLastCallTime());
		}		
		ProbeResultAnalyzeStability runStatusView = new ProbeResultAnalyzeStability();	
		for (List list:(List<List>)runStatusCount[3]) {
			runStatusView.getTimeBucket().add(list.get(0).toString());
		}
		
		runStatusView.setSuccessCount(countRunStatus(runStatusView.getTimeBucket(), (List<List>)runStatusCount[0]));
		runStatusView.setFailCount(countRunStatus(runStatusView.getTimeBucket(), (List<List>)runStatusCount[1]));
		runStatusView.setStopCount(countRunStatus(runStatusView.getTimeBucket(), (List<List>)runStatusCount[2]));
		view.setStabilityCount(runStatusView);
	
		
		return view;
	}
	
	private static List<Integer> countRunStatus(List<String> timeBucket, List<List> status) {
		List<Integer> list = new ArrayList<Integer>();
		loop:
		for (String time:timeBucket) {		
			for (List l:status) {
				if (time.equals(l.get(0).toString())) {
					list.add(Integer.valueOf(l.get(1).toString()));
					continue loop;
				}
			}
			list.add(0);
		}
		return list;
	}
	
	/**
	 * 当前所有正在运行中的探测任务总览视图
	 * @param views
	 * @return
	 */
	public static Object analyzeProbeResultSynopsisView(List<ProbeResultSynopsisView> views) {
		Map<String, List<Object[]>> returnObj = new HashMap<String, List<Object[]>>();		
		Object[] info = null;
		for (ProbeResultSynopsisView view:views) {
			info = new Object[5];
			info[0] = PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, view.getOpTime());
			info[1] = probeQualityLevelName(view.getQualityLevel());
			info[2] = view.getMessageInfo().split(",")[0];
			info[3] = view.getSystemName();
			info[4] = view.getProbeId();
			if (returnObj.get(String.valueOf(view.getSystemId())) == null) {
				returnObj.put(String.valueOf(view.getSystemId()), new ArrayList<Object[]>());
			}
			returnObj.get(String.valueOf(view.getSystemId())).add(info);
		}
		return returnObj;
		
	}
	
	private static String probeQualityLevelName(Integer level) {
		if (ProbeConfig.PROBE_EXCELLENT_LEVEL.equals(level)) {
			return "ExcellentLevel(优秀)";
		}
		if (ProbeConfig.PROBE_NORMAL_LEVEL.equals(level)) {
			return "NormalLevel(正常)";
		}
		if (ProbeConfig.PROBE_PROBLEMATIC_LEVEL.equals(level)) {
			return "ProblematicLevel(有问题)";
		}
		if (ProbeConfig.PROBE_SERIOUS_LEVEL.equals(level)) {
			return "SeriousLevel(严重)";
		}
		return "NoDate(缺少数据)";
	}
}
