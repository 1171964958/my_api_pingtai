package yi.master.business.message.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.TestDataService;
import yi.master.constant.MessageKeys;
import yi.master.constant.ReturnCodeConsts;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;
import yi.master.util.message.JsonUtil;


/**
 * 测试数据Action
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.5.5
 */

@Controller
@Scope("prototype")
public class TestDataAction extends BaseAction<TestData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(TestDataAction.class);
	
	/**对应的测试场景*/
	private Integer messageSceneId;
	
	private TestDataService testDataService;
	@Autowired
	public void setTestDataService(TestDataService testDataService) {
		super.setBaseService(testDataService);
		this.testDataService = testDataService;
	}
	
	@Autowired
	private MessageSceneService messageSceneService;

	/**列对应的path,不带顶级节点名TopRoot,需要自己加*/
	private String paths;
	/**批量数据内容,逗号分隔*/
	private String datas;
	
	@Override
	public String[] prepareList() {
		
		if (this.messageSceneId != null) {
			this.filterCondition = new String[]{"messageScene.messageSceneId=" + messageSceneId};
		}
		
		return this.filterCondition;
	}
	
	/**
	 * 创建数据状态
	 * @return
	 */
	public String changeStatus () {
		TestData data = testDataService.get(model.getDataId());
		
		if (data != null && !data.getStatus().equals(model.getStatus()) && !CacheUtil.checkLockedTestData(data.getDataId())) {
			data.setStatus(model.getStatus());
			testDataService.edit(data);
		}
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 判断标记名重复性
	 * 新增或者修改状态下均可用
	 */
	@Override
	public void checkObjectName() {
		TestData data = testDataService.findByDisrc(model.getDataDiscr(), messageSceneId);
		checkNameFlag = (data != null && !data.getDataId().equals(model.getDataId())) ? "重复的标记名" : "true";
		
		if (model.getDataId() == null) {
			checkNameFlag = (data == null) ? "true" : "重复的标记名";
		}
	}

	/**
	 * 更新数据详细内容
	 * @return
	 */
	public String updateParamsData () {
		
		if (CacheUtil.checkLockedTestData(model.getDataId())) {
			
			jsonMap.put("msg", "该条测试数据正在被使用，请稍后再更新!");
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			return SUCCESS;
		}
		
		testDataService.updateParamsData(model.getDataId(), model.getParamsData());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	@Override
	public String get() {
		
		if (model.getDataId() == null) {
			model.setDataId(id);
		}
		model = testDataService.get(model.getDataId());
		
		MessageParse parseUtil = MessageParse.getParseInstance(model.getMessageScene().getMessage().getMessageType());
		
		model.setDataJson(parseUtil.depacketizeMessageToString(model.getMessageScene().getMessage().getComplexParameter(), model.getParamsData()));
		
		jsonMap.put("object", model);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 获取设置参数数据时需要用到的相关数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getSettingData() {
		
		Message msg = messageSceneService.get(messageSceneId).getMessage();
		
		MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
		
		List<Parameter> params = null;
		try {
			params = msg.getComplexParameter().getEnableSettingDataParameter(null);
		} catch (Exception e) {
			
			LOGGER.warn(msg.getInterfaceName() + "-" + msg.getMessageName() + ":" + e.getMessage(), e);
			
			jsonMap.put("msg", "接口参数有改动,请检查或者重新设置该场景所属报文的入参规则!");
			jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
			return SUCCESS;
		}
		
		
		String paramsData = "";
		if (model.getDataId() != null ) {	
			paramsData = testDataService.get(model.getDataId()).getParamsData();
		} 
		
		Map<String, Object> messageData = PracticalUtils.jsonToMap(paramsData);
		
		List<Map<String, String>> paramsMap = new ArrayList<Map<String, String>>();
		Map<String, String> paramValues = null;
		String key = "";
		for (Parameter param:params) {
			key = param.getPath() + "." + param.getParameterIdentify();
			paramValues = new HashMap<String, String>();
			paramValues.put("path", key.replaceFirst(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + "\\.", ""));
			paramValues.put("settingValue", parseUtil.findParameterValue2(param, messageData));
			paramValues.put("defaultValue", param.getDefaultValue());
			paramValues.put("parameterId", String.valueOf(param.getParameterId()));
			paramValues.put("parameterIdentify", param.getParameterIdentify());
			paramValues.put("parameterName", param.getParameterName());
			paramValues.put("type", param.getType());
			
			paramsMap.add(paramValues);
		}					
		
		String dataMsg = parseUtil.depacketizeMessageToString(msg.getComplexParameter(), paramsData);	
		
		jsonMap.put("dataMsg", dataMsg);
		jsonMap.put("params", paramsMap);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 根据传入的json数据字符串和场景id生成新得带有数据的报文
	 * @return
	 */
	public String createDataMsg() {
		Message msg = messageSceneService.get(messageSceneId).getMessage();
		MessageParse parseUtil = MessageParse.getParseInstance(msg.getMessageType());
		
		String dataMsg = parseUtil.depacketizeMessageToString(msg.getComplexParameter(), model.getParamsData());
		
		jsonMap.put("dataMsg", dataMsg);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}

	/**
	 * 批量导入数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String importDatas() {
		MessageScene scene = messageSceneService.get(messageSceneId);
		
		String[] pathArray = paths.split(",");
		
		String[] dataArray = null;	
		TestData testData = null;
		Map<String, Object> paramsData = new HashMap<String, Object>();
		
		int totalCount = 0;
		int successCount = 0;
		int failCount = 0;
		for (String data:datas.split("\\n+")) {
			totalCount++;
			dataArray = data.split(",");
			
			if (dataArray.length != (pathArray.length + 2)) {
				failCount++;
				continue;
			}
			
			if (!"0".equals(dataArray[1]) && !"1".equals(dataArray[1])) {
				failCount++;
				continue;
			}
			
			if (testDataService.findByDisrc(dataArray[0], messageSceneId) != null) {
				failCount++;
				continue;
			}
			
			testData = new TestData();
			testData.setDataDiscr(dataArray[0]);
			testData.setMessageScene(scene);
			testData.setStatus(dataArray[1]);
			
			paramsData.clear();
			
			for (int i = 0;i < pathArray.length;i++) {
				String path = MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + "." + pathArray[i];
				
				if (paramsData.get(path) != null) {
					if (paramsData.get(path) instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String)paramsData.get(path));
						paramsData.put(path, values);
					} 
					
					((List<String>)paramsData.get(path)).add(dataArray[i + 2]);
				} else {
					paramsData.put(path, dataArray[i + 2]);
				}
			}
			
			try {
				testData.setParamsData(JsonUtil.getObjectByJson(paramsData));
				testDataService.edit(testData);
			} catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
				failCount++;
				continue;
			}
			
			successCount++;
		}
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		if (successCount < 1) {
			jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
			jsonMap.put("msg", "未能成功导入任何数据,请检查!");
		}
		jsonMap.put("totalCount", totalCount);
		jsonMap.put("successCount", successCount);
		jsonMap.put("failCount", failCount);
		return SUCCESS;
	}	
	
	
	@Override
	public String del() {
		
		if (CacheUtil.checkLockedTestData(model.getDataId())) {
			
			jsonMap.put("msg", "该条测试数据正在被使用，请稍后再更新!");
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			return SUCCESS;
		}

		return super.del();
	}

	@Override
	public String edit() {
		
		if (CacheUtil.checkLockedTestData(model.getDataId())) {
			
			jsonMap.put("msg", "该条测试数据正在被使用，请稍后再更新!");
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			return SUCCESS;
		}
		
		return super.edit();
	}

	/******************************************GET-SET********************************************/
	
	public void setPaths(String paths) {
		this.paths = paths;
	}
	
	public void setDatas(String datas) {
		this.datas = datas;
	}
	
	public void setMessageSceneId(Integer messageSceneId) {
		this.messageSceneId = messageSceneId;
	}
}
