package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.AutoTask;
import yi.master.business.message.dao.AutoTaskDao;
import yi.master.business.message.service.AutoTaskService;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170724
 *
 */
@Service("autoTaskService")
public class AutoTaskServiceImpl extends BaseServiceImpl<AutoTask> implements AutoTaskService {
	
	private AutoTaskDao autoTaskDao;
	
	@Autowired
	public void setAutoTaskDao(AutoTaskDao autoTaskDao) {
		super.setBaseDao(autoTaskDao);
		this.autoTaskDao = autoTaskDao;
	}
	

	@Override
	public List<AutoTask> findRunTasks() {
		
		return autoTaskDao.findRunTasks();
	}

	@Override
	public void updateStatus(Integer taskId, String status) {
		
		autoTaskDao.updateStatus(taskId, status);
	}

	@Override
	public void updateExpression(Integer taskId, String expression) {
		
		autoTaskDao.updateExpression(taskId, expression);
	}

	@Override
	public void updateCount(Integer taskId, Integer mode) {
		
		autoTaskDao.updateCount(taskId, mode);
	}


	@Override
	public AutoTask findByName(String taskName) {
		
		return autoTaskDao.findByName(taskName);
	}

}
