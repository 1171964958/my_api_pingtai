package yi.master.business.message.service;

import java.util.List;

import yi.master.business.base.service.BaseService;
import yi.master.business.message.bean.TestData;

public interface TestDataService extends BaseService<TestData>{
	
	/**
	 * 更新某个属性
	 * @param dataId
	 * @param dataName 属性名
	 * @param dataValue 要更新的值
	 */
	void updateDataValue(Integer dataId, String dataName, String dataValue);
	
	/**
	 * 通过数据标记来查找测试数据
	 * @param dataDisrc
	 * @param messageSceneId
	 * @return
	 */
	TestData findByDisrc(String dataDiscr, Integer messageSceneId);
	
	/**
	 * 根据场景获取一定数量的测试数据
	 * @param messageSceneId
	 * @param count
	 * @return
	 */
	List<TestData> getDatasByScene(Integer messageSceneId, int count);
	
	/**
	 * 更新指定数据的设置内容
	 * @param dataId
	 * @param paramsData
	 */
	void updateParamsData (Integer dataId, String paramsData);
}
