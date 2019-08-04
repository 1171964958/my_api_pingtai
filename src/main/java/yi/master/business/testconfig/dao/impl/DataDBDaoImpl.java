package yi.master.business.testconfig.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.testconfig.bean.DataDB;
import yi.master.business.testconfig.dao.DataDBDao;

/**
 * 查询用数据库信息Dao实现
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Repository("dataDBDao")
public class DataDBDaoImpl extends BaseDaoImpl<DataDB> implements DataDBDao {
	
	@Override
	public DataDB getMaxDBId() {
		String hql = "From DataDB d order by d.dbId desc";
		
		return (DataDB) getSession().createQuery(hql)
				.setFirstResult(0).setMaxResults(1)
				.setCacheable(true).uniqueResult();
	}
}
