package yi.master.coretest.task.probe;

import java.sql.Timestamp;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.service.InterfaceProbeService;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.service.TestResultService;
import yi.master.business.user.service.MailService;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;
import yi.master.util.notify.EmailCreator;
import yi.master.util.notify.NotifyMail;
import yi.master.util.notify.ProbeEmailCreator;

/**
 * 探测任务执行工作类
 * @author xuwangcheng
 * @version 1.0.0.0,2018.1.26
 *
 */
public class ProbeTaskJobAction implements Job {

	@Autowired
	private TestResultService testResultService;
	@Autowired
	private InterfaceProbeService interfaceProbeService;
	@Autowired
	private MailService mailService;
	
	private static final Logger LOGGER = Logger.getLogger(ProbeTaskJobAction.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		InterfaceProbe task = (InterfaceProbe) dataMap.get(context.getJobDetail().getKey().getName());
		
		//获取请求地址
		String testUrl = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_HOME) + "/" + SystemConsts.PROBE_TASK_TEST_RMI_URL
				+ "?probeId=" + task.getProbeId() + "&token=" + SystemConsts.REQUEST_ALLOW_TOKEN;
		
		LOGGER.info("[接口探测任务]执行探测任务:url=" + testUrl);
		
		String returnJson = PracticalUtils.doGetHttpRequest(testUrl);
		
		LOGGER.info("[接口探测任务]请求返回内容：" + returnJson);
		Integer resultId = null;
		try {
			Map maps = new ObjectMapper().readValue(returnJson, Map.class);
			if ("0".equals(maps.get("returnCode").toString())) {
				resultId = Integer.valueOf(maps.get("resultId").toString());
			} else {
				task.setMark(maps.get("msg").toString());
			}
		} catch (Exception e) {
			
			LOGGER.error("[接口探测任务]探测任务执行出错:" + returnJson, e);
		}
		Timestamp lastCallTime = null;
		TestResult result = null;
		if (resultId == null) {
			task.setStatus("3");//变更状态为“执行出错”
		} else {
			result = testResultService.get(resultId);
			task.setMark("");
			if (result.getQualityLevel() == 0) {//变更状态为“缺少数据”
				task.setStatus("2");
			}
			lastCallTime = result.getOpTime();
		}
		
		task.setLastCallTime(lastCallTime == null ?new Timestamp(System.currentTimeMillis()) : lastCallTime);
		//更新探测详情
		if (task.getFirstCallTime() == null) {
			task.setFirstCallTime(task.getLastCallTime());
		}
										
		task.setCallCount(task.getCallCount() + 1);
		interfaceProbeService.edit(task);
		
		//结果通知
		if ("0".equals(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_IF_SEND_REPORT_MAIL)) 
				&& !"0".equals(task.getConfig().getNotifyType()) && result != null 
				&& result.getQualityLevel() >= Integer.valueOf(task.getConfig().getNotifyLevel())) {
			String sendEmailFlag = "true";
			//邮件通知
			if ("2".equals(task.getConfig().getNotifyType())) {
				final String emailInfo = "接口任务[Id=" + task.getProbeId() + "] " + result.getMessageInfo() + " 在" + result.getOpTime()
						+ "的探测结果级别为 [" + TestResult.QUALITY_LEVEL_DICT.get(result.getQualityLevel()) + "] ,详细结果如下,请关注!\n\n探测结果:" + TestResult.RUN_STATUS_DICT.get(result.getRunStatus()) 
						+ "\n耗时:" + result.getUseTime() + "\n状态码:" + result.getStatusCode();
								
				sendEmailFlag = NotifyMail.sendEmail(new ProbeEmailCreator(task, result), task.getConfig().getReceiveAddress()
						, task.getConfig().getCopyAddress());
			}
			
			
			//站内信通知
			String mailInfo = "接口探测任务<br><span class=\"label label-primary radius\">[任务Id]</span> = " 
					+ task.getProbeId() + "<br><span class=\"label label-primary radius\">[接口信息]</span> = " 
					+ result.getMessageInfo() + "<br><span class=\"label label-primary radius\">[探测时间]</span> = " 
					+ result.getOpTime() + "<br><span class=\"label label-primary radius\">[探测结果]</span> = "
					+ PracticalUtils.getProbeResultQualityLevelHtml(result.getQualityLevel()) + "<br>请在接口探测模块关注详情!";
			
			if (!"true".equalsIgnoreCase(sendEmailFlag)) {
				mailInfo += "<br><br><span class=\"label label-danger radius\">!! 由于以下原因,本次邮件通知失败,请检查!</span><br><br><code>"
						 +  sendEmailFlag + "</code>";
			}
			
			mailService.sendSystemMail("接口探测任务警告", mailInfo, task.getUser().getUserId());					
			
		}
	}

}
