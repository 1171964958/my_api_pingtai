package yi.master.business.message.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.dao.TestDataDao;

@Repository("testDataDao")
public class TestDataDaoImpl extends BaseDaoImpl<TestData> implements TestDataDao{

	@Override
	public void updateDataValue(Integer dataId, String dataName,
			String dataValue) {
		
		String hql = "update TestData set " + dataName + "= :dataValue where dataId= :dataId";
		getSession().createQuery(hql).setString("dataValue", dataValue).setInteger("dataId", dataId).executeUpdate();
	}

	@Override
	public TestData findByDisrc(String dataDiscr, Integer messageSceneId) {
		
		String hql = "from TestData t where messageScene.messageSceneId=:messageSceneId and dataDiscr=:dataDiscr";
		
		return (TestData) getSession().createQuery(hql).setInteger("messageSceneId", messageSceneId).setString("dataDiscr", dataDiscr).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TestData> getDatasByScene(Integer messageSceneId, int count) {
		
		String hql = "from TestData t where t.messageScene.messageSceneId=:messageSceneId and t.status != '1'";
		Query query = getSession().createQuery(hql).setInteger("messageSceneId", messageSceneId);
		if (count != 0) {
			query.setMaxResults(count);
		}
		return query.list();
	}
}
