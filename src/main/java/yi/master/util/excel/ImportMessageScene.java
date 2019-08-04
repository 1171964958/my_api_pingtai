package yi.master.util.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.SceneValidateRule;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.SceneValidateRuleService;
import yi.master.business.message.service.TestDataService;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.util.FrameworkUtil;

/**
 * 从指定excel中读取对象内容
 * @author xuwangcheng
 * @version 2017.12.05,1.0.0.0
 *
 */
public class ImportMessageScene {
	
	private static final Logger LOGGER = Logger.getLogger(ImportMessage.class);
	
	
	/**
	 * 从excel导入scene到数据库
	 * @param path
	 * @return
	 */
	public static Map<String, Object> importToDB (String path, Message message) {
		
		MessageSceneService messageSceneService = (MessageSceneService) FrameworkUtil.getSpringBean("messageSceneService");
		TestDataService testDataService = (TestDataService) FrameworkUtil.getSpringBean("testDataService");
		SceneValidateRuleService sceneValidateRuleService = (SceneValidateRuleService) FrameworkUtil.getSpringBean("sceneValidateRuleService");
		
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<MessageScene> infos = importValue(path);
		
		int totalCount = infos.size();
		int successCount = 0;
		int failCount = 0;
		StringBuilder msg = new StringBuilder();
		
		for (MessageScene info:infos) {
			//排除名称为空的情况
			if (StringUtils.isBlank(info.getSceneName())) {
				totalCount--;
				continue;
			}			
			//验证是否合法
			Object[] objs = validateInfo(info, message);
			msg.append(objs[1].toString());
			
			try {
				info.setCreateTime(new Timestamp(System.currentTimeMillis()));
				info.setMessage(message);
				//保存场景信息 到数据库
				info.setMessageSceneId(messageSceneService.save(info));
				
				//设置一条默认数据
				TestData defaultData = new TestData();
				defaultData.setDataDiscr("默认数据");
				defaultData.setStatus("0");
				defaultData.setMessageScene(info);
				defaultData.setParamsData("");	
				testDataService.save(defaultData);
				
				//判断是否需要创建默认关联验证
				if (objs[2] != null) {
					SceneValidateRule rule = (SceneValidateRule) objs[2];
					rule.setMessageScene(info);
					sceneValidateRuleService.save(rule);
				}
				
				msg.append("<span class=\"c-green\">导入该条信息成功.</span><br>");
				successCount++;				
				
			} catch (Exception e) {
				
				LOGGER.error("在进行Excel批量导入场景信息时发生了错误：" + info.toString(), e);
				msg.append("<span class=\"c-red\">导入过程中发生了错误：" + e.getMessage() + "！导入该条信息失败.</span><br>");
				failCount++;
			}
		}
		
		result.put(PoiExcelUtil.RESULT_MAP_TOTAL_COUNT, totalCount);
		result.put(PoiExcelUtil.RESULT_MAP_SUCCESS_COUNT, successCount);
		result.put(PoiExcelUtil.RESULT_MAP_FAIL_COUNT, failCount);
		result.put(PoiExcelUtil.RESULT_MAP_MSG, msg.toString());
		return result;
	}
	
	/**
	 * 验证传入的Message是否合法<br>
	 * @param info 
	 * @return 返回  数组第一位修改之后的实体对象 数组第二位字符串标识警告信息或者错误信息 数组第三位为默认验证规则
	 */
	public static Object[] validateInfo (MessageScene info, Message message) {
		StringBuilder tigs = new StringBuilder("<span class=\"label label-default radius\">" + message.getInterfaceName() 
					+ "-" + message.getMessageName() + "-" + info.getSceneName() +":" + "</span>&nbsp;");
		
		SceneValidateRule rule = null;
		
		Object[] obj = new Object[3];
				
		
		//设置默认验证规则
		if (StringUtils.isNotBlank(info.getValidateMethodStr()) && info.getValidateMethodStr().length() > 4) {
			GlobalVariableService service = (GlobalVariableService) FrameworkUtil.getSpringBean("globalVariableService");
			String variableKey = info.getValidateMethodStr().substring(4, info.getValidateMethodStr().length() - 1);
			GlobalVariable variable = service.findByKey(variableKey);
			if (variable == null) {
				tigs.append("没有找到Key名称为<strong>" + info.getValidateMethodStr() + "</strong>的关联验证模板,不设置默认验证规则;");				
			} else {
				try {
					rule = (SceneValidateRule) variable.createSettingValue();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
			}
		}
		
		
		obj[0] = info;
		obj[1] = tigs.toString();
		obj[2] = rule;
		return obj;
	}
	
	/**
	 * 从excel读取数据
	 * @param path
	 * @return
	 */
	public static List<MessageScene> importValue (String path) {
		if (path != null && !path.equals("")) {
            String ext = PoiExcelUtil.getExt(path);
            if (ext!=null && !ext.equals("")) {
                if (ext.equals("xls")) {
                    return readXls(path);
                } else if (ext.equals("xlsx")) {
                    return readXlsx(path);
                }
            }
        }
		return new ArrayList<MessageScene>();
	}
	
	/**
	 * 从xls文件读取MessageScene
	 * @param path
	 * @return
	 */
	public static List<MessageScene> readXls (String path) {
		HSSFWorkbook hssfWorkbook = null;
		try {
			 InputStream is = new FileInputStream(path);
			 hssfWorkbook = new HSSFWorkbook(is);			 
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		MessageScene info = null;
		List<MessageScene> infos = new ArrayList<MessageScene>();
		
		if (hssfWorkbook != null) {
			// Read the Sheet.只读取第一页
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
			// Read the Row
			 for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                 HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                 if (hssfRow != null) {
                     info = new MessageScene();
                     HSSFCell sceneName = hssfRow.getCell(0);
                     HSSFCell defaultValidate = hssfRow.getCell(1);
                     HSSFCell mark = hssfRow.getCell(2);
                     
                     info.setSceneName(PoiExcelUtil.getValue(sceneName));
                     info.setValidateMethodStr(PoiExcelUtil.getValue(defaultValidate));
                     info.setMark(PoiExcelUtil.getValue(mark));
                     
                     infos.add(info);
                 }
             }
		}
		
		return infos;
		
	}
	
	/**
	 * 从xlsx文件读取MessageScene
	 * @param path
	 * @return
	 */
	public static List<MessageScene> readXlsx (String path) {
		XSSFWorkbook xssfWorkbook = null;
        try {
            InputStream is = new FileInputStream(path);
            xssfWorkbook = new XSSFWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageScene info = null;
		List<MessageScene> infos = new ArrayList<MessageScene>();
		
		if (xssfWorkbook != null) {
			// Read the Sheet.只读取第一页
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
			// Read the Row
			 for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                 XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                 if (xssfRow != null) {
                     info = new MessageScene();
                     XSSFCell sceneName = xssfRow.getCell(0);
                     XSSFCell defaultValidate = xssfRow.getCell(1);
                     XSSFCell mark = xssfRow.getCell(2);

                     info.setSceneName(PoiExcelUtil.getValue(sceneName));
                     info.setValidateMethodStr(PoiExcelUtil.getValue(defaultValidate));
                     info.setMark(PoiExcelUtil.getValue(mark));
                     
                     infos.add(info);
                 }
             }
		}
		
		return infos;
        
	}
}	
