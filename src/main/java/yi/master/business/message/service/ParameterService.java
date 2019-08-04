package yi.master.business.message.service;

import java.util.List;

import yi.master.business.base.service.BaseService;
import yi.master.business.message.bean.Parameter;

/**
 * 接口参数Service接口
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public interface ParameterService extends BaseService<Parameter> {
	
	/**
	 * 查找指定接口下所有参数信息
	 * @param interfaceId
	 * @return
	 */
	List<Parameter> findByInterfaceId(int interfaceId);
	
	/**
	 * 删除指定接口下的所有参数信息
	 * @param interfaceId
	 */
	void delByInterfaceId(int interfaceId);
	
	
	/**
	 * 编辑参数的属性
	 * @param parameterId
	 * @param attrName 属性名
	 * @param attrValue 值
	 */
	void editProperty(int parameterId,String attrName,String attrValue);
	
	/**
	 * 查找是否已有重复的参数
	 * @param parameterId
	 * @param parameterIdentify
	 * @param path
	 * @param type
	 * @param interfaceId
	 * @return
	 */
	Parameter checkRepeatParameter (Integer parameterId, String parameterIdentify, String path, String type, Integer interfaceId);
}
