package yi.master.business.testconfig.service;

import yi.master.business.base.service.BaseService;
import yi.master.business.testconfig.bean.DataDB;

/**
 * 查询用数据库信息Service接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public interface DataDBService extends BaseService<DataDB> {
	
	/**
	 * 获取当前自定义id值
	 * @return Integer 最大id值加1  如果没有指定实体对象，默认第一个id值为9999990
	 */
	Integer getMaxDBId();
	
}
