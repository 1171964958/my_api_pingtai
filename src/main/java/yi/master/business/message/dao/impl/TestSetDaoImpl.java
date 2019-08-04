package yi.master.business.message.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.dao.TestSetDao;
import yi.master.business.testconfig.bean.TestConfig;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170518
 *
 */

@Repository("testSetDao")
public class TestSetDaoImpl extends BaseDaoImpl<TestSet> implements TestSetDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<MessageScene> getEnableAddScenes(Integer setId) {
		
		String hql = "from MessageScene m1 where not exists (select 1 from MessageScene m2 "
				+ "join m2.testSets s where s.setId=:setId and m1.messageSceneId=m2.messageSceneId) "
				+ "and m1.message is not null";		
		
		return getSession().createQuery(hql).setInteger("setId", setId).setCacheable(true).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageModel<MessageScene> listSetMessageScene (Integer setId, int dataNo, int pageSize, String orderDataName, String orderType
				, String searchValue, List<List<String>> dataParams, int mode, String ...filterCondition) {
		PageModel<MessageScene> pm = new PageModel<MessageScene>(orderDataName, orderType, searchValue, dataParams, dataNo, pageSize);
		StringBuilder hql = new StringBuilder("from MessageScene m1 where");
		//mode=0 查询存在的 mode=1查询不存在的
		hql.append(mode == 1 ? " not" : "");
		
		hql.append(" exists (select 1 from MessageScene m2 join m2.testSets s "
				+ "where s.setId=" + setId + " and m1.messageSceneId=m2.messageSceneId) "
				+ "and m1.message is not null");
		LOGGER.info("The query HQL String: \n" + hql.toString());		
		pm.setRecordCount(getHqlCount("select count(m1) " + hql.toString()));

		
		//增加搜索条件
		if (searchValue != "") {
			hql.append(" and (");
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
			hql.append(" and ");
						
			int i = 1;
			for (String s : filterCondition) {
				hql.append(s);
				i++;
				if (i <= filterCondition.length) {
					hql.append(" and ");
				}
			}
		}
		
		LOGGER.info("The query HQL String: \n" + hql.toString());
		pm.setFilteredCount(getHqlCount("select count(m1) " + hql.toString()));
		
		//增加排序
		if (!orderDataName.isEmpty()) {
			hql.append(" order by " + orderDataName + " " + orderType);
		}
		
		LOGGER.info("The query HQL String: \n" + hql.toString());
		
		pm.setDatas(getSession().createQuery(hql.toString())
				.setFirstResult(dataNo)
				.setMaxResults(pageSize)
				.setCacheable(true).list());
		
		return pm;
		
	}
	
	@Override
	public void addSceneToSet(Integer setId, Integer messageSceneId) {
		
		String sql = "insert into at_set_scene(set_id,message_scene_id) values(:setId,:messageSceneId)";
		getSession().createSQLQuery(sql).setInteger("setId" ,setId).setInteger("messageSceneId", messageSceneId).executeUpdate();
	}

	@Override
	public void delSceneToSet(Integer setId, Integer messageSceneId) {
		
		String sql = "delete from at_set_scene where set_id=:setId and message_scene_id=:messageSceneId";
		getSession().createSQLQuery(sql).setInteger("setId" ,setId).setInteger("messageSceneId", messageSceneId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TestSet> getUserSets(Integer userId) {
		
		String hql = "from TestSet t where t.status='0'";
		return getSession().createQuery(hql).setCacheable(true).list();
	}

	@Override
	public void updateSettingConfig(Integer setId, TestConfig config) {
		
		String hql = "update TestSet t set t.config.configId=:configId where t.setId=:setId";	
		getSession().createQuery(hql).setInteger("configId", config.getConfigId()).setInteger("setId", setId).executeUpdate();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TestSet> getRootSet() {
		
		String hql = "from TestSet t where t.parentSet=null";
		return getSession().createQuery(hql).setCacheable(true).list();
	}

	@Override
	public void moveFolder(Integer setId, Integer parentId) {
		
		String sql = "update at_test_set s set s.parent_id=:parentId where s.set_id=:setId";
		getSession().createSQLQuery(sql).setInteger("setId", setId).setInteger("parentId", parentId).executeUpdate();
	}

}
