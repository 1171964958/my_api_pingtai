package yi.master.business.testconfig.dao;

import yi.master.business.base.dao.BaseDao;
import yi.master.business.testconfig.bean.DataDB;

/**
 * 查询用数据Dao接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public interface DataDBDao extends BaseDao<DataDB> {
	
	/**
	 * 查询id最大的对象实体 
	 * @return DataDB id最大的对象实体
	 */
	DataDB getMaxDBId();
}
