package yi.master.business.testconfig.service;

import java.util.List;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.service.BaseService;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.testconfig.bean.BusinessSystem;


public interface BusinessSystemService extends BaseService<BusinessSystem> {
	/**
	 * 查询指定测试环境中包含的或者不包含的接口信息
	 * @param systemId
	 * @param dataNo
	 * @param pageSize
	 * @param orderDataName
	 * @param orderType
	 * @param searchValue
	 * @param dataParams
	 * @param mode 0 - 查询包含的 ,1 - 查询不包含的
	 * @param filterCondition
	 * @return
	 */
	PageModel<InterfaceInfo> listSystemInterface (Integer systemId, int dataNo, int pageSize, String orderDataName, String orderType
			, String searchValue, List<List<String>> dataParams, int mode, String procotolType, String ...filterCondition);
	
	/**
	 * 添加接口到测试环境
	 * @param systemId
	 * @param interfaceId
	 */
	void addInterfaceToSystem(Integer systemId, Integer interfaceId);
	
	/**
	 * 从测试环境中删除接口信息
	 * @param systemId
	 * @param interfaceId
	 */
	void delInterfaceFromSystem(Integer systemId, Integer interfaceId);
}
