package yi.master.util.notify;

import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

import yi.master.business.message.bean.TestReport;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

import net.sf.json.JSONObject;

/**
 * 定时任务推送邮件
 * @author xuwangcheng
 * @version 2018.08.16
 *
 */
public class ReportEmailCreator implements EmailCreator {
	
	private TestReport report;

	public ReportEmailCreator(TestReport report) {
		super();
		this.report = report;
	}

	public ReportEmailCreator() {
		super();
		
	}

	public TestReport getReport() {
		return report;
	}

	public void setReport(TestReport report) {
		this.report = report;
	}

	@Override
	public MimeMessage createMessage(Session session, String sendAddress,
			Address[] receiveAddresses, Address[] copyAddresses) throws Exception {
		
		MimeMessage message = new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress(sendAddress, CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_PERSONAL_NAME), "UTF-8"));		
			message.setRecipients(RecipientType.TO, receiveAddresses);
			message.setRecipients(RecipientType.CC, copyAddresses);
			
			message.setSubject("接口自动化定时任务_测试报告", "UTF-8");
			
			//添加附件
			MimeBodyPart attachment = new MimeBodyPart();
			DataHandler dh2 = new DataHandler(new FileDataSource(FrameworkUtil.getProjectPath() + "/" + report.getReportHtmlPath()));
			attachment.setDataHandler(dh2);
			attachment.setFileName(MimeUtility.encodeText(dh2.getName()));
			
			//邮件内容
			MimeBodyPart text = new MimeBodyPart();
			report.countSceneNum();
			String content = null;
			JSONObject obj = JSONObject.fromObject(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MESSAGE_MAIL_STYLE));
			if (StringUtils.isNotBlank(obj.getString(SystemConsts.GLOBAL_SETTING_MESSAGE_MAIL_STYLE_TASK_REPORT))) {				
				content = obj.getString(SystemConsts.GLOBAL_SETTING_MESSAGE_MAIL_STYLE_TASK_REPORT);
				content = content.replaceAll("\\$\\{time\\}", PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, report.getFinishTime()))
						.replaceAll("\\$\\{totalCount\\}", String.valueOf(report.getSceneNum())).replaceAll("\\$\\{successCount\\}", String.valueOf(report.getSuccessNum()))
						.replaceAll("\\$\\{failCount\\}", String.valueOf(report.getFailNum())).replaceAll("\\$\\{stopCount\\}", String.valueOf(report.getStopNum()));
			} else {
				content = "你好：<br>接口自动化平台在" + PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, report.getFinishTime()) 
				+ "完成一次定时测试任务。<br>本次共执行测试场景<span style=\"color:#0000FF;\">" + report.getSceneNum() + "</span>个,"
				+ "其中测试成功<span style=\"color:green;\">" + report.getSuccessNum() + "</span>个,"
				+ "测试失败<span style=\"color:red;\">" + report.getFailNum() + "</span>个,"
				+ "异常中断<span style=\"color:#848484;\">" + report.getStopNum() + "</span>个。"
				+ "<br>详情请参考附件中的离线测试报告!(请先从邮箱中下载在本地打开查看，否则会出现样式错误!)";
			}
			
			text.setContent(content, "text/html;charset=UTF-8");	

			MimeMultipart mm = new MimeMultipart();
			mm.addBodyPart(attachment);
			mm.addBodyPart(text);
			mm.setSubType("mixed");
			
			message.setContent(mm);
			message.setSentDate(new Date());
			message.saveChanges();
		} catch (Exception e) {
			
			//创建失败做二次处理
			//do something
			
			throw e;			
		}

		return message;
	}

}
