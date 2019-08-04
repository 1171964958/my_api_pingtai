package yi.master.business.message.dao;

import java.util.List;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.dao.BaseDao;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestSet;
import yi.master.business.testconfig.bean.TestConfig;

/**
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170518
 *
 */

public interface TestSetDao extends BaseDao<TestSet> {
	
	/**
	 * 获取不在指定测试集下的测试场景<br>
	 * 同时该场景对应的测试接口和测试报文状态均为0(正常)
	 * @param setId
	 * @return
	 */
	List<MessageScene> getEnableAddScenes(Integer setId);
	
	/**
	 * 添加测试场景到测试集
	 * @param setId
	 * @param messageSceneId
	 */
	void addSceneToSet(Integer setId, Integer messageSceneId);
	
	/**
	 * 从测试集中删除测试场景
	 * @param setId
	 * @param messageSceneId
	 */
	void delSceneToSet(Integer setId, Integer messageSceneId);
	
	/**
	 * 获取指定用户的测试集列表
	 * @param userId
	 * @return
	 */
	List<TestSet> getUserSets (Integer userId);
	
	/**
	 * 更新测试集的运行时配置
	 * @param setId
	 * @param config
	 */
	void updateSettingConfig(Integer setId, TestConfig config);
	
	/**
	 * 获取测试集目录树根节点
	 * @return
	 */
	List<TestSet> getRootSet ();
	
	/**
	 * 移动测试集到指定文件夹下
	 * @param setId
	 * @param parentId
	 */
	void moveFolder(Integer setId, Integer parentId);
	
	/**
	 * 分页查询存在或者不存在指定测试集中
	 * @param setId
	 * @param dataNo
	 * @param pageSize
	 * @param orderDataName
	 * @param orderType
	 * @param searchValue
	 * @param dataParams
	 * @param mode
	 * @param filterCondition
	 * @return
	 */
	PageModel<MessageScene> listSetMessageScene (Integer setId, int dataNo, int pageSize, String orderDataName, String orderType
			, String searchValue, List<List<String>> dataParams, int mode, String ...filterCondition);
}
