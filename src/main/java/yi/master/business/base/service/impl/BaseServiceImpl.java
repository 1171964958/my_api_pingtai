package yi.master.business.base.service.impl;

import java.util.Date;
import java.util.List;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.dao.BaseDao;
import yi.master.business.base.service.BaseService;

/**
 * 通用service实现类
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 * @param <T>
 */
public class BaseServiceImpl<T> implements BaseService<T>{
	
	private BaseDao<T> baseDao;
	
	public void setBaseDao(BaseDao<T> baseDao) {
		this.baseDao = baseDao;
	}
	
	
	@Override
	public Integer save(T entity) {
				
		return baseDao.save(entity);
	}

	@Override
	public void delete(int id) {
		
		baseDao.delete(id);		
	}

	@Override
	public void edit(T entity) {
		
		baseDao.edit(entity);		
	}

	@Override
	public T get(Integer id) {
		
		return baseDao.get(id);
	}

	@Override
	public T load(Integer id) {
		
		return baseDao.load(id);
	}

	@Override
	public List<T> findAll(String ...filterCondition) {
		
		return baseDao.findAll(filterCondition);
	}

	@Override
	public int totalCount(String ...filterCondition) {
		
		return baseDao.totalCount(filterCondition);
	}

	@Override
	public PageModel<T> findByPager(int dataNo, int pageSize, String orderDataName, String orderType, String searchValue, List<List<String>> dataParams,String ...filterCondition) {
		
		return baseDao.findByPager(dataNo, pageSize, orderDataName, orderType, searchValue, dataParams, filterCondition);
	}


	@Override
	public int countByTime(String fieldName, Date ...time) {
		
		return baseDao.countByTime(fieldName, time);
	}


	@Override
	public int getHqlCount(String hql) {
		
		return baseDao.getHqlCount(hql);
	}
	
	
}
