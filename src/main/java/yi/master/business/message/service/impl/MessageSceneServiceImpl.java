package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.dao.MessageSceneDao;
import yi.master.business.message.service.MessageSceneService;

/**
 * 报文场景service实现
 * @author xuwangcheng
 * @version 1.0.0,2017.3.6
 */
@Service("messageSceneService")
public class MessageSceneServiceImpl extends BaseServiceImpl<MessageScene> implements MessageSceneService{

	private MessageSceneDao messageSceneDao;
	
	@Autowired
	public void setMessageSceneDao(MessageSceneDao messageSceneDao) {
		super.setBaseDao(messageSceneDao);
		this.messageSceneDao = messageSceneDao;
	}
	
	@Override
	public void updateValidateFlag(Integer messageSceneId, String validateRuleFlag) {
		
		messageSceneDao.updateValidateFlag(messageSceneId, validateRuleFlag);
	}

	@Override
	public List<MessageScene> getBySetId(Integer setId) {
		
		return messageSceneDao.getBySetId(setId);
	}

	@Override
	public InterfaceInfo getInterfaceOfScene(Integer messageSceneId) {
		
		return messageSceneDao.getInterfaceOfScene(messageSceneId);
	}

	@Override
	public Message getMessageOfScene(Integer messageSceneId) {
		
		return messageSceneDao.getMessageOfScene(messageSceneId);
	}

	@Override
	public void updateResponseExample(Integer messageSceneId, String response) {
		
		messageSceneDao.updateResponseExample(messageSceneId, response);
	}

}
