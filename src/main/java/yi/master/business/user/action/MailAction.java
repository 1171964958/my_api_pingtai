package yi.master.business.user.action;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.user.bean.Mail;
import yi.master.business.user.bean.User;
import yi.master.business.user.service.MailService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;

/**
 * 用户邮件Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
@Scope("prototype")
public class MailAction extends BaseAction<Mail> {

	private static final long serialVersionUID = 1L;
		
/*	private Integer mailType;	
	private Integer receiveUserId;*/
	
	private String statusName;
	
	private String status;
	
	private MailService mailService;
	@Autowired
	public void setMailService(MailService mailService) {
		super.setBaseService(mailService);
		this.mailService = mailService;
	}
	
	@Override
	public String[] prepareList() {
		
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		if (user != null) {
			this.filterCondition = new String[]{"receiveUser.userId=" + user.getUserId()};
		}		
		return this.filterCondition;
	}

	//获取未读邮件数量
	public String getNoReadMailNum() {
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		int num = 0;
		if (user != null) {
			num = mailService.getNoReadNum(user.getUserId());
		}
		
		jsonMap.put("mailNum", num);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	
	
	//获取收件箱列表或者发件箱列表
	//mailType=1收件箱列表   mailType=2 发件箱列表
	/*public String listMails() {
		User user = (User) StrutsUtils.getSessionMap().get("user");
		List<Mail> mails = new ArrayList<Mail>();
		if (mailType == 1) {
			mails = mailService.findReadMails(user.getUserId());
		} else {
			mails = mailService.findSendMails(user.getUserId());
		}
		for (Mail mail : mails) {
			mail.setSendUserName();
			mail.setReceiveUserName();
			if (mail.getIfValidate().equals("0") && mailType == 1) {
				mail.setMailInfo("");
			}
		}
		jsonMap.put("data", mails);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}*/
	
	//改变邮件状态
	public String changeStatus() {
		if (statusName.equals("sendStatus") || statusName.equals("readStatus") || statusName.equals("ifValidate")) {			
			if (statusName.equals("sendStatus")) {
				Mail mail1 = mailService.get(model.getMailId());
				if (mail1.getReceiveUser() == null) {
					jsonMap.put("returnCode", ReturnCodeConsts.MAIL_MISS_RECEIVER_CODE);
					jsonMap.put("msg", "需要选定一个收件用户才能发送!");
					return SUCCESS;
				}
				mail1.setSendTime(new Timestamp(System.currentTimeMillis()));
				mailService.edit(mail1);
			}
			mailService.changeStatus(model.getMailId(), statusName, status);
			jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		} else {
			jsonMap.put("returnCode", ReturnCodeConsts.MISS_PARAM_CODE);
			jsonMap.put("msg", "参数不正确!");
		}		
		return SUCCESS;
	}
	
	//获取指定mail
	/*public String get() {
		model = mailService.get(model.getMailId());
		model.setReceiveUserName();
		model.setSendUserName();
		if (model.getReceiveUser() != null) {
			jsonMap.put("receiveUserId",model.getReceiveUser().getUserId());
		}		
		jsonMap.put("mail", model);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);		
		return SUCCESS;
	}*/

	//保存新的邮件信息或者更新信息
	/*public String save() {
		User user = (User) StrutsUtils.getSessionMap().get("user");
		
		model.setReadStatus("1");
		model.setSendStatus("1");
		model.setSendUser(user);
		
		if (receiveUserId != null) {
			User receiveUser = new User();
			receiveUser.setUserId(receiveUserId);
			model.setReceiveUser(receiveUser);
		}
		
		if (model.getMailId() == null) {
			Integer id = mailService.save(model);
			jsonMap.put("mailId", id);
		} else {
			mailService.edit(model);
		}
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}*/
	
	/****************************************************************************************************************/
/*	
	public void setMailType(Integer mailType) {
		this.mailType = mailType;
	}
	
	public void setReceiveUserId(Integer receiveUserId) {
		this.receiveUserId = receiveUserId;
	}
	*/
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

}
