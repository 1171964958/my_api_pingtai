package yi.master.business.testconfig.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.dao.GlobalVariableDao;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20171129
 *
 */
@Repository("globalVariableDao")
public class GlobalVariableDaoImpl extends BaseDaoImpl<GlobalVariable> implements GlobalVariableDao {

	@Override
	public GlobalVariable findByKey(String key) {
		
		String hql = "From GlobalVariable g where g.key=:key";
		return (GlobalVariable) getSession().createQuery(hql)
				.setString("key", key).uniqueResult();
	}

	@Override
	public void updateValue(Integer variableId, String value) {
		
		String hql = "update GlobalVariable g set g.value=:value where g.variableId=:variableId";
		getSession().createQuery(hql).setString("value", value).setInteger("variableId", variableId)
			.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GlobalVariable> findByVariableType(String variableType) {
		
		String hql = "From GlobalVariable g where g.variableType=:variableType";
		return getSession().createQuery(hql).setString("variableType", variableType).list();
	}	
}
