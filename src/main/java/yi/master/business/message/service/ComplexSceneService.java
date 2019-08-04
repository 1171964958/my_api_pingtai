package yi.master.business.message.service;

import java.util.List;

import yi.master.business.base.service.BaseService;
import yi.master.business.message.bean.ComplexScene;

/**
 * 
 * @author xuwangcheng
 * @version 2017.11.23,1.0.0.0
 *
 */
public interface ComplexSceneService extends BaseService<ComplexScene> {
	
	/**
	 * 展示指定测试拥有的组合场景
	 * @param setId
	 * @return
	 */
	List<ComplexScene> listComplexScenesBySetId(Integer setId);
	
	/**
	 * 编辑组合场景的名称和备注
	 * @param id
	 * @param complexSceneName
	 * @param mark
	 */
	void editComplexScene(Integer id, String complexSceneName, String mark);
	
	/**
	 * 更新组合场景中变量相关信息
	 * @param id
	 * @param scenes
	 * @param saveVariables
	 * @param useVariables
	 */
	void editComplexSceneVariables(Integer id, String scenes, String saveVariables, String useVariables);
	
	/**
	 * 操作该组合场景是否已经存在于指定的测试集中
	 * @param id
	 * @param setId
	 * @return
	 */
	ComplexScene findAssignScene (Integer id, Integer setId);
	
	/**
	 * 添加到指定测试集
	 * @param id
	 * @param setId
	 */
	void addToSet (Integer id, Integer setId);
	
	/**
	 * 从测试集中删除
	 * @param id
	 * @param setId
	 */
	void delFromSet (Integer id, Integer setId);
	
	/**
	 * 更新配置信息
	 * @param id
	 * @param configJson
	 */
	void updateConfigInfo (Integer id, String configJson);
}
