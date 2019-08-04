package yi.master.business.message.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.Message;
import yi.master.business.message.dao.MessageDao;

/**
 * 接口报文Dao接口实现
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.17
 */

@Repository("messageDao")
public class MessageDaoImpl extends BaseDaoImpl<Message> implements MessageDao {

}
