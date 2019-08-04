package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.dao.TestSetDao;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.TestConfig;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170518
 *
 */

@Service("testSetService")
public class TestSetServiceImpl extends BaseServiceImpl<TestSet> implements TestSetService {
	
	private TestSetDao testSetDao;
	
	@Autowired
	public void setTestSetDao(TestSetDao testSetDao) {
		setBaseDao(testSetDao);
		this.testSetDao = testSetDao;
	}

	@Override
	public List<MessageScene> getEnableAddScenes(Integer setId) {
		
		return testSetDao.getEnableAddScenes(setId);
	}

	@Override
	public void addSceneToSet(Integer setId, Integer messageSceneId) {
		
		testSetDao.addSceneToSet(setId, messageSceneId);
	}

	@Override
	public void delSceneToSet(Integer setId, Integer messageSceneId) {
		
		testSetDao.delSceneToSet(setId, messageSceneId);
	}

	@Override
	public List<TestSet> getUserSets(Integer userId) {
		
		return testSetDao.getUserSets(userId);
	}

	@Override
	public void updateSettingConfig(Integer setId, TestConfig config) {
		
		testSetDao.updateSettingConfig(setId, config);
	}

	@Override
	public List<TestSet> getRootSet() {
		
		return testSetDao.getRootSet();
	}

	@Override
	public void delete(int id) {
		
		TestSet set = get(id);
		if (set == null) {
			return;
		}
		
		if ("0".equals(set.getParented())) {//测试集目录
			TestSet parentSet = set.getParentSet();
			
			for (TestSet s:set.getChildrenSets()) {
				s.setParentSet(parentSet);
				edit(s);
			}
		}
		set.setChildrenSets(null);		
		testSetDao.delete(id);
		
	}

	@Override
	public void moveFolder(Integer setId, Integer parentId) {
		
		testSetDao.moveFolder(setId, parentId);
	}

	@Override
	public PageModel<MessageScene> listSetMessageScene(Integer setId,
			int dataNo, int pageSize, String orderDataName, String orderType,
			String searchValue, List<List<String>> dataParams, int mode,
			String... filterCondition) {
		
		return testSetDao.listSetMessageScene(setId, dataNo, pageSize, orderDataName, orderType,
				searchValue, dataParams, mode, filterCondition);
	}
}
