package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.ComplexScene;
import yi.master.business.message.dao.ComplexSceneDao;
import yi.master.business.message.service.ComplexSceneService;

/**
 * 
 * @author xuwangcheng
 * @version 2017.11.23,1.0.0.0
 *
 */
@Repository("complexSceneService")
public class ComplexSceneServiceImpl extends BaseServiceImpl<ComplexScene> implements ComplexSceneService {
	
	private ComplexSceneDao complexSceneDao;
	
	@Autowired
	public void setcomplexSceneDao(ComplexSceneDao complexSceneDao) {
		super.setBaseDao(complexSceneDao);
		this.complexSceneDao = complexSceneDao;
	}

	@Override
	public List<ComplexScene> listComplexScenesBySetId(Integer setId) {
		
		return complexSceneDao.listComplexScenesBySetId(setId);
	}

	@Override
	public void editComplexScene(Integer id, String complexSceneName,
			String mark) {
		
		complexSceneDao.editComplexScene(id, complexSceneName, mark);
	}

	@Override
	public void editComplexSceneVariables(Integer id, String scenes,
			String saveVariables, String useVariables) {
		
		complexSceneDao.editComplexSceneVariables(id, scenes, saveVariables, useVariables);
		
	}

	@Override
	public ComplexScene findAssignScene(Integer id, Integer setId) {
		
		return complexSceneDao.findAssignScene(id, setId);
	}

	@Override
	public void addToSet(Integer id, Integer setId) {
		
		complexSceneDao.addToSet(id, setId);
	}

	@Override
	public void delFromSet(Integer id, Integer setId) {
		
		complexSceneDao.delFromSet(id, setId);
	}

	@Override
	public void updateConfigInfo(Integer id, String configJson) {
		
		complexSceneDao.updateConfigInfo(id, configJson);
	}
}	
