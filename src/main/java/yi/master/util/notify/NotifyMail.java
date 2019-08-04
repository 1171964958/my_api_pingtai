package yi.master.util.notify;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import yi.master.constant.SystemConsts;
import yi.master.util.cache.CacheUtil;

public class NotifyMail {
	
	private static final Logger LOGGER = Logger.getLogger(NotifyMail.class);
	
	/**
	 * 发送邮件入口
	 * @param message
	 * @return
	 */
	public static String sendEmail (EmailCreator emailCreator, String receiveAddress_s, String copyAddress_s) {
		String sendSuccess = "true";
		Properties props = new Properties(); 
		
		final String smtpPort = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_SERVER_PORT);
		
		props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_SERVER_HOST));   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        props.setProperty("mail.smtp.port", smtpPort);
        
        /**
         * SSL连接
         */
		
        if ("0".equals(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_SSL_FLAG))) {       	
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        }
        
        Session session = Session.getInstance(props);
        
        Transport transport = null;
        String sendAddress = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_SEND_ADDRESS);
        if (StringUtils.isBlank(receiveAddress_s)) {
        	receiveAddress_s = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_RECEIVE_ADDRESS);
        	copyAddress_s = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_COPY_ADDRESS);
        }

        Address[] receiveAddresses = createAddresses(receiveAddress_s.split("[,，]"));
        if (receiveAddresses.length < 1) {
        	return "邮件发送失败：缺少收件人或者邮件地址不正确,请检查!";
        }
        Address[] copyAddresses = createAddresses(copyAddress_s.split("[,，]"));
        
        try {
			transport = session.getTransport();
		
			transport.connect(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_AUTH_USERNAME),
					CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MAIL_AUTH_PASSWORD));
			
			MimeMessage message = emailCreator.createMessage(session, sendAddress, receiveAddresses, copyAddresses);			
			
			LOGGER.info("发送邮件详情：\n发件人-" + sendAddress + ",\n收件人列表-" + receiveAddress_s + ",\n抄送列表-" + copyAddress_s
					+ "\n邮件内容为\n" + message.getContent().toString());
			
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
		} catch (Exception e) {
			LOGGER.error("发送邮件失败", e);
			return "发送邮件失败:" + e.getMessage();
		}
        
        LOGGER.info("邮件发送成功!");
		return sendSuccess;
	}
	
	
	/**
	 * 创建地址对象
	 * @param addresses
	 * @return
	 */
	public static Address[] createAddresses (String[] addresses) {
		List<InternetAddress> addressObjs = new ArrayList<InternetAddress>();
		InternetAddress addr = null;
		for (int i = 0; i < addresses.length; i++) {
			try {
				addr = new InternetAddress(addresses[i]);
				addressObjs.add(addr);
			} catch (AddressException e) {
				//e.printStackTrace();
				LOGGER.info("邮件地址:" + addresses[i] + "不正确!");
			}
		}
		
		return addressObjs.toArray(new Address[0]);
	}
	
}
