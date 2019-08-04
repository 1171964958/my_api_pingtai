package yi.master.business.message.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.AutoTask;
import yi.master.business.message.dao.AutoTaskDao;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170724
 *
 */
@Repository("autoTaskDao")
public class AutoTaskDaoImpl extends BaseDaoImpl<AutoTask> implements AutoTaskDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<AutoTask> findRunTasks() {
		
		String hql = "From AutoTask t where t.status='0'";
		return getSession().createQuery(hql).setCacheable(true).list();
	}

	@Override
	public void updateStatus(Integer taskId, String status) {
		
		String hql = "update AutoTask t set t.status=:status where t.taskId=:taskId";
		getSession().createQuery(hql).setString("status", status).setInteger("taskId", taskId).executeUpdate();
	}

	@Override
	public void updateExpression(Integer taskId, String expression) {
		
		String hql = "update AutoTask t set t.taskCronExpression=:expression where t.taskId=:taskId";
		getSession().createQuery(hql).setString("expression", expression).setInteger("taskId",taskId).executeUpdate();		
	}

	@Override
	public void updateCount(Integer taskId, Integer mode) {
		
		String hql = "update AutoTask t set t.runCount=";
		if (mode == 0) {
			hql += "0";
		} else {
			hql += "t.runCount+1";
		}
		hql+=" where t.taskId=:taskId";
		getSession().createQuery(hql).setInteger("taskId", taskId).executeUpdate();
	}

	@Override
	public AutoTask findByName(String taskName) {
		
		String hql = "from AutoTask t where t.taskName=:taskName";
		return (AutoTask) getSession().createQuery(hql).setString("taskName", taskName).uniqueResult();
	}

}
