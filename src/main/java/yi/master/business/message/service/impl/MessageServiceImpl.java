package yi.master.business.message.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.Message;
import yi.master.business.message.dao.MessageDao;
import yi.master.business.message.service.MessageService;

/**
 * 接口报文Service接口实现
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.17
 */

@Service("messageService")
public class MessageServiceImpl extends BaseServiceImpl<Message> implements MessageService{
	
	private MessageDao messageDao;
	
	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		super.setBaseDao(messageDao);
		this.messageDao = messageDao;
	}
	

}
