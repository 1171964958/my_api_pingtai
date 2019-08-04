package yi.master.coretest.task.time;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;

import yi.master.business.message.bean.AutoTask;
import yi.master.business.message.bean.TestReport;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.service.AutoTaskService;
import yi.master.business.message.service.TestReportService;
import yi.master.business.message.service.TestResultService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.user.service.MailService;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;
import yi.master.util.notify.NotifyMail;
import yi.master.util.notify.ReportEmailCreator;

/**
 * 定时任务监听器
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.26
 *
 */
public class TimeTaskJobListener implements JobListener {

	public static final String LISTENER_NAME = SystemConsts.QUARTZ_TIME_TASK_NAME_PREFIX_KEY + "-autoTest";
	
	@Autowired
	private MailService mailService;
	@Autowired
	private AutoTaskService taskService;
	@Autowired
	private TestReportService reportService;
	@Autowired
	private TestResultService resultService;
	@Autowired
	private TestSetService testSetService;
	
	private static final Logger LOGGER = Logger.getLogger(TimeTaskJobListener.class);
	
	@Override
	public String getName() {
		
		return LISTENER_NAME;
	}

	/**
	 * 准备执行
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
	}	

	/**
	 * 执行完成后
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		AutoTask task = (AutoTask)dataMap.get(context.getJobDetail().getKey().getName());
		
		String[] result = (String[]) context.getResult();
		StringBuilder tip = new StringBuilder();
		if (StringUtils.isEmpty(result[0])) {
			tip.append("接口自动化测试定时任务<br><span class=\"label label-primary radius\">[任务Id]</span> = ") 
				.append(task.getTaskId() + "<br><span class=\"label label-primary radius\">[任务名称]</span> = ") 
				.append(task.getTaskName() + "<br><span class=\"label label-primary radius\">[任务类型]</span> = ") 
				.append(getTaskType(task.getTaskType()) + "<br><span class=\"label label-primary radius\">[任务状态]</span> = <span class=\"c-red\"><strong>失败</strong></span><br><pre class=\"prettyprint linenums\">") 
				.append(result[1] + "</pre>");
			mailService.sendSystemMail("接口自动化定时任务失败提醒", tip.toString(), task.getUser().getUserId());
			return;
		}
		
		if ("0".equals(task.getTaskType())) {
			String finishFlag = "N";
			
			while ("N".equalsIgnoreCase(finishFlag)) {
				finishFlag = reportService.isFinished(Integer.parseInt(result[0]));	
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					LOGGER.warn("InterruptedException", e);
				}
			}
			
			tip.append("接口自动化测试定时任务<br><span class=\"label label-primary radius\">[任务Id]</span> = ") 
				.append(task.getTaskId() + "<br><span class=\"label label-primary radius\">[任务名称]</span> = ") 
				.append(task.getTaskName() + "<br><span class=\"label label-primary radius\">[任务类型]</span> = ") 
				.append(getTaskType(task.getTaskType()) + "<br><span class=\"label label-primary radius\">[测试报告ID]</span> = ") 
				.append(result[0] + "<br><a href=\"../message/reportView.html?reportId="+ result[0] +"\" target=\"_blank\">详情请查看本次测试报告!</a>");
		}		
		
		task.setRunCount(task.getRunCount() + 1);
		task.setLastFinishTime(new Timestamp(System.currentTimeMillis()));
		taskService.edit(task);
		
		//发送推送邮件
		if ("0".equals(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_IF_SEND_REPORT_MAIL)) && "1".equals(task.getMailNotify())) {
			String createReportUrl = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME) + "/" 
					+ SystemConsts.CREATE_STATIC_REPORT_HTML_RMI_URL + "?reportId=" + result[0] 
					+ "&token=" + SystemConsts.REQUEST_ALLOW_TOKEN;
			String returnJson = PracticalUtils.doGetHttpRequest(createReportUrl);
			try {
				Map maps = new ObjectMapper().readValue(returnJson, Map.class);
				if (!"0".equals(maps.get("returnCode").toString())) {
					throw new Exception(returnJson);
				}
				
				TestReport report = reportService.get(Integer.valueOf(result[0]));
				report.setTrs(new HashSet<TestResult>(resultService.listByReportId(Integer.valueOf(result[0]))));
				
				TestConfig config = testSetService.get(task.getRelatedId()).getConfig();
				String sendMailSuccessFlag = NotifyMail.sendEmail(new ReportEmailCreator(report), config.getMailReceiveAddress(), config.getMailCopyAddress());
				
				if ("true".equalsIgnoreCase(sendMailSuccessFlag)) {
					tip.append("<p class=\"c-green\">本次测试结果及报告已通过邮件推送!</p>");
				} else {
					tip.append("<p class=\"c-red\">发送推送邮件失败,原因：</p><p>" + sendMailSuccessFlag + "</p>");
				}
				
				
			} catch (Exception e) {
				tip.append("<p class=\"c-red\">发送推送邮件失败，原因：" + e.getMessage() + "。ReportId=" + result[0] + "</p>");
			}
		} else {
			tip.append("<p class=\"c-red\">!!!没有开启测试报告邮件推送或者全局配置中的推送开关被关闭!!!</p>");
		}
		
		mailService.sendSystemMail("接口自动化定时任务完成提醒", tip.toString(), task.getUser().getUserId());
		
		
	}
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		
		
	}
	
	private static String getTaskType (String type) {
		switch (type) {
		case "0":
			return "接口自动化";
		case "1":
			return "Web自动化";
		default:
			return "未知类型";
		}
	}

}
