package yi.master.business.base.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.dao.BaseDao;

/**
 * 通用DAO接口的实现类
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class BaseDaoImpl<T> implements BaseDao<T> {
	
	protected static final Logger LOGGER = Logger.getLogger(BaseDaoImpl.class);
	
	/**
	 * Hibernate sessionFactory
	 */
	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	private Class<T> clazz;
	
	/**
	 * 通过反射获取传入的类
	 */
	@SuppressWarnings("rawtypes")
	public BaseDaoImpl() {		
		ParameterizedType type=(ParameterizedType)this.getClass().getGenericSuperclass();
		this.clazz = (Class)type.getActualTypeArguments()[0];	
	}
	
	public Integer save(T entity) {
		
		Integer id = (Integer) getSession().save(entity);
		return id;
	}

	public void delete(int id) {
		
		getSession().delete(get(id));
	}

	public void edit(T entity) {
		
		getSession().merge(entity);
	}

	
	public T get(Integer id) {
		
		
		return (T)getSession().get(clazz, id);
	}

	public T load(Integer id) {
		
		return (T)getSession().load(clazz, id);
	}

	public List<T> findAll(String ...filterCondition) {
		
		String hsql = "select t from " + clazz.getSimpleName() + " t";
		
		if (filterCondition != null && filterCondition.length > 0) {
			hsql += " where ";
			int i = 1;
			for (String s : filterCondition) {
				hsql += s;
				i++;
				if (i <= filterCondition.length) {
					hsql += " and ";
				}
			}
		}
		LOGGER.info("The query HQL String: \n" + hsql);
		return getSession().createQuery(hsql).setCacheable(true).list();
	}

	public int totalCount(String ...filterCondition) {
		
		int count = 0;
		StringBuilder hql = new StringBuilder("select count(t) from " + clazz.getSimpleName() + " t");
		
		if (filterCondition != null && filterCondition.length > 0) {
			hql.append(" where ");
			int i = 1;
			for (String s : filterCondition) {
				hql.append(s);
				i++;
				if (i <= filterCondition.length) {
					hql.append(" and ");
				}
			}
		}
		
		Long temp = (Long)getSession().createQuery(hql.toString()).uniqueResult();
		if (temp != null) {
			count = temp.intValue();
		}
		return count;
	}

	public PageModel<T> findByPager(int dataNo, int pageSize, String orderDataName, String orderType, String searchValue, List<List<String>> dataParams, String ...filterCondition) {
		
		PageModel<T> pm = new PageModel<T>(orderDataName, orderType, searchValue, dataParams, dataNo, pageSize);
		
		StringBuilder hql = new StringBuilder("from " + clazz.getSimpleName() + " t");
		
		if (searchValue != "" || filterCondition != null && filterCondition.length > 0) {
			hql.append(" where ");
		}
		
		//增加搜索条件
		if (searchValue != "") {	
			hql.append("(");
			int i = 1;
			for (List<String> ss : dataParams) {
				i++;
				String columnName = ss.get(0);
				
				if (ss.size() == 1) {
					hql.append(columnName + " like '%" + searchValue + "%'");
				}
				
				if (ss.size() > 1) {
					for (int m = 1;m < ss.size();m ++) {
						hql.append(columnName + " like '%" + ss.get(m) + "%'");
						if (m + 1 < ss.size()) {
							hql.append(" or ");
						}
					}
				}
												
				if (i <= dataParams.size()) {
					hql.append(" or ");
				}
			}
			hql.append(")");
		}
		//增加自定义的条件
		if (filterCondition != null && filterCondition.length > 0) {
			if (searchValue != "") {
				hql.append(" and ");
			}
			
			
			int i = 1;
			for (String s : filterCondition) {
				hql.append(s);
				i++;
				if (i <= filterCondition.length) {
					hql.append(" and ");
				}
			}
		}
		
		pm.setFilteredCount(getHqlCount("select count(t) " + hql.toString()));
		
		//增加排序
		if (!orderDataName.isEmpty()) {
			hql.append(" order by " + orderDataName + " " + orderType);
		}
		
		LOGGER.info("The query HQL String: \n" + hql.toString());
	
		pm.setDatas(getSession().createQuery(hql.toString())
				.setFirstResult(dataNo)
				.setMaxResults(pageSize)
				.setCacheable(true).list());
		pm.setRecordCount(totalCount(filterCondition));
		return pm;
	}

	public void update(String sql) {
		
		getSession().createQuery(sql).executeUpdate();
	}

	public T findUnique(String sql) {
			
		return (T)getSession().createQuery(sql).uniqueResult();
	}

	@Override
	public int countByTime(String fieldName, Date ...time) {
		
		int count = 0;
		String hql = "select count(t) from " + clazz.getSimpleName() + " t where t." + fieldName + ">:endTime1";			
		if (time.length > 1) {
			hql += " and t." + fieldName + "<:endTime2";
		}
		Query query = getSession().createQuery(hql).setTimestamp("endTime1", time[0]);
		if (time.length > 1) {
			query.setTimestamp("endTime2", time[1]);
		}
		Long temp = (Long)query.uniqueResult();
		if (temp != null) {
			count = temp.intValue();
		}
		return count;
	}

	@Override
	public int getHqlCount(String hql) {
		
		Query query = getSession().createQuery(hql);
		int count = 0;
		Long temp = (Long)query.uniqueResult();
		if (temp != null) {
			count = temp.intValue();
		}
		return count;
	}
}
