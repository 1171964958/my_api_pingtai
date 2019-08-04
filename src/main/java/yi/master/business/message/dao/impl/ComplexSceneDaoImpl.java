package yi.master.business.message.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.message.bean.ComplexScene;
import yi.master.business.message.dao.ComplexSceneDao;

/**
 * 
 * @author xuwangcheng
 * @version 2017.11.23,1.0.0.0
 *
 */
@Repository("complexScene")
public class ComplexSceneDaoImpl extends BaseDaoImpl<ComplexScene> implements ComplexSceneDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ComplexScene> listComplexScenesBySetId(Integer setId) {
		String hql = "select c from ComplexScene c join c.testSets s where s.setId=:setId";		
		return getSession().createQuery(hql).setInteger("setId", setId).list();
	}

	@Override
	public void editComplexScene(Integer id, String complexSceneName,
			String mark) {
		
		String hql = "update ComplexScene c set c.complexSceneName=:complexSceneName "
				+ ",c.mark=:mark where c.id=:id";
		getSession().createQuery(hql).setString("complexSceneName", complexSceneName)
			.setString("mark", mark).setInteger("id", id).executeUpdate();
	}

	@Override
	public void editComplexSceneVariables(Integer id, String scenes,
			String saveVariables, String useVariables) {
		
		String hql = "update ComplexScene c set c.scenes=:scenes,c.saveVariables=:saveVariables "
				+ ",c.useVariables=:useVariables where c.id=:id";
		getSession().createQuery(hql).setString("scenes", scenes).setString("saveVariables", saveVariables)
			.setString("useVariables", useVariables).setInteger("id", id).executeUpdate();
		
	}

	@Override
	public ComplexScene findAssignScene(Integer id, Integer setId) {
		
		String hql = "select c from ComplexScene c join c.testSets s where s.setId=:setId and c.id=:id";
		return (ComplexScene) getSession().createQuery(hql).setInteger("id", id).setInteger("setId", setId).uniqueResult();
	}

	@Override
	public void addToSet(Integer id, Integer setId) {
		
		String sql = "insert into at_set_complex_scene (set_id, complex_scene_id) values (:setId, :id)";
		getSession().createSQLQuery(sql).setInteger("setId", setId).setInteger("id", id).executeUpdate();
	}

	@Override
	public void delFromSet(Integer id, Integer setId) {
		
		String sql = "delete from at_set_complex_scene where set_id=:setId and complex_scene_id=:id";
		getSession().createSQLQuery(sql).setInteger("setId", setId).setInteger("id", id).executeUpdate();
	}

	@Override
	public void updateConfigInfo(Integer id, String configJson) {
		
		String hql = "update ComplexScene c set c.configInfo=:configJson where c.id=:id";
		getSession().createQuery(hql).setInteger("id", id).setString("configJson", configJson).executeUpdate();
	}
	
}
