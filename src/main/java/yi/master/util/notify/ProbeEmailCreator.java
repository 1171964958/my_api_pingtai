package yi.master.util.notify;

import java.util.Date;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.lang3.StringUtils;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.message.bean.TestResult;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

import net.sf.json.JSONObject;

public class ProbeEmailCreator implements EmailCreator {
	
	private InterfaceProbe probe;
	private TestResult result;
	

	public ProbeEmailCreator(InterfaceProbe probe, TestResult result) {
		super();
		this.probe = probe;
		this.result = result;
	}


	@Override
	public MimeMessage createMessage(Session session, String sendAddress, Address[] receiveAddresses,
			Address[] copyAddresses) throws Exception {
		
		String content = null;
		JSONObject obj = JSONObject.fromObject(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MESSAGE_MAIL_STYLE));
		if (StringUtils.isNotBlank(obj.getString(SystemConsts.GLOBAL_SETTING_MESSAGE_MAIL_STYLE_PROBE_REPORT))) {
			content = obj.getString(SystemConsts.GLOBAL_SETTING_MESSAGE_MAIL_STYLE_PROBE_REPORT);
			content = content.replaceAll("\\$\\{id\\}", String.valueOf(probe.getProbeId())).replaceAll("\\$\\{interfaceScene\\}", result.getMessageInfo())
					.replaceAll("\\$\\{level\\}", TestResult.QUALITY_LEVEL_DICT.get(result.getQualityLevel()))
					.replaceAll("\\$\\{time\\}", PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss", result.getOpTime()))
					.replaceAll("\\$\\{status\\}", TestResult.RUN_STATUS_DICT.get(result.getRunStatus()))
					.replaceAll("\\$\\{useTime\\} ", String.valueOf(result.getUseTime())).replaceAll("\\$\\{code\\}", result.getStatusCode());						
		} else {
			content = "接口任务[Id=" + probe.getProbeId() + "] " + result.getMessageInfo() + " 在" + result.getOpTime()
			+ "的探测结果级别为 [" + TestResult.QUALITY_LEVEL_DICT.get(result.getQualityLevel()) + "] ,详细结果如下,请关注!\n\n探测结果:" + TestResult.RUN_STATUS_DICT.get(result.getRunStatus()) 
			+ "\n耗时:" + result.getUseTime() + "\n状态码:" + result.getStatusCode();
		}
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sendAddress, CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_PERSONAL_NAME), "UTF-8"));		
		message.setRecipients(RecipientType.TO, receiveAddresses);
		message.setRecipients(RecipientType.CC, copyAddresses);
		
		message.setSubject("接口探测任务警告", "UTF-8");
		
		MimeMultipart mm = new MimeMultipart();
		MimeBodyPart text = new MimeBodyPart();
		text.setContent(content, "text/html;charset=UTF-8");
		mm.addBodyPart(text);			
		message.setContent(mm);
		message.setSentDate(new Date());
		message.saveChanges();
		
		return message;
	}


	public InterfaceProbe getProbe() {
		return probe;
	}


	public void setProbe(InterfaceProbe probe) {
		this.probe = probe;
	}


	public TestResult getResult() {
		return result;
	}


	public void setResult(TestResult result) {
		this.result = result;
	}
	
}
