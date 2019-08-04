package yi.master.business.user.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.user.bean.Mail;
import yi.master.business.user.bean.User;
import yi.master.business.user.dao.MailDao;
import yi.master.business.user.service.MailService;
import yi.master.constant.SystemConsts;

/**
 * 用户邮件Service接口实现
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

@Service("mailService")
public class MailServiceImpl extends BaseServiceImpl<Mail> implements MailService {
	
	private MailDao mailDao;
	
	@Autowired
	public void setMailDao(MailDao mailDao) {
		super.setBaseDao(mailDao);
		this.mailDao = mailDao;
	}

	@Override
	public int getNoReadNum(Integer receiveUserId) {
		
		return mailDao.getNoReadNum(receiveUserId);
	}

	@Override
	public List<Mail> findReadMails(Integer receiveUserId) {
		
		return mailDao.findReadMails(receiveUserId);
	}

	@Override
	public List<Mail> findSendMails(Integer sendUserId) {
		
		return mailDao.findSendMails(sendUserId);
	}

	@Override
	public void changeStatus(Integer mailId, String statusName, String status) {
		
		mailDao.changeStatus(mailId, statusName, status);
	}

	@Override
	public void sendSystemMail(String title, String messageInfo, Integer receiveUserId) {
		
		Mail mail = new Mail(new User(SystemConsts.ADMIN_USER_ID), null, "1", messageInfo, "0", "1", new Timestamp(System.currentTimeMillis()), "", "", title);
		edit(mail);		
	}

}
