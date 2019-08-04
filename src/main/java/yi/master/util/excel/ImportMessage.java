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

import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.MessageService;
import yi.master.business.message.service.TestDataService;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.business.user.bean.User;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.util.FrameworkUtil;

/**
 * 从指定excel中读取对象内容
 * @author xuwangcheng
 * @version 2017.12.05,1.0.0.0
 *
 */
public class ImportMessage {
	
	private static final Logger LOGGER = Logger.getLogger(ImportMessage.class);
	
	
	/**
	 * 从excel导入message到数据库
	 * @param path
	 * @return
	 */
	public static Map<String, Object> importToDB (String path, InterfaceInfo interfaceInfo) {
		
		MessageService messageService = (MessageService) FrameworkUtil.getSpringBean("messageService");
		MessageSceneService messageSceneService = (MessageSceneService) FrameworkUtil.getSpringBean("messageSceneService");
		TestDataService testDataService = (TestDataService) FrameworkUtil.getSpringBean("testDataService");
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<Message> infos = importValue(path);
		
		int totalCount = infos.size();
		int successCount = 0;
		int failCount = 0;
		StringBuilder msg = new StringBuilder();
		
		for (Message info:infos) {
			//排除名称为空的情况
			if (StringUtils.isBlank(info.getMessageName())) {
				totalCount--;
				continue;
			}			
			//验证是否合法
			Object[] objs = validateInfo(info, interfaceInfo);
			msg.append(objs[1].toString());
			if (!(boolean) objs[2]) {
				failCount++;
				continue;
			}
			
			try {
				User user = (User)FrameworkUtil.getSessionMap().get("user");
				info.setCreateTime(new Timestamp(System.currentTimeMillis()));
				info.setUser(user);
				info.setLastModifyUser(user.getRealName());				
				info.setInterfaceInfo(interfaceInfo);
				
				MessageParse parse = MessageParse.getParseInstance(info.getMessageType());
				info.setComplexParameter(parse.parseMessageToObject(info.getParameterJson(), new ArrayList<Parameter>(interfaceInfo.getParameters())));
				
				//保存报文信息 到数据库
				info.setMessageId(messageService.save(info));
				
				MessageScene scene = null;
				//判断是否需要创建默认测试场景
				if (StringUtils.isNotBlank(info.getCreateMessageScene())) {
					scene = new MessageScene();
					scene.setCreateTime(new Timestamp(System.currentTimeMillis()));
					scene.setSceneName(info.getCreateMessageScene());
					scene.setMessage(info);
					scene.setMark("");
					
					scene.setMessageSceneId(messageSceneService.save(scene));
					//设置一条默认数据
					TestData defaultData = new TestData();
					defaultData.setDataDiscr("默认数据");
					defaultData.setStatus("0");
					defaultData.setMessageScene(scene);
					defaultData.setParamsData("");	
					testDataService.save(defaultData);
				}
				
				msg.append("<span class=\"c-green\">导入该条信息成功.</span><br>");
				successCount++;				
				
			} catch (Exception e) {
				
				LOGGER.error("在进行Excel批量导入报文信息时发生了错误：" + info.toString(), e);
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
	 * @return 返回  数组第一位修改之后的实体对象 数组第二位字符串标识警告信息或者错误信息 数组第三位为boolean标识该信息是否可以入库
	 */
	public static Object[] validateInfo (Message info, InterfaceInfo interfaceInfo) {
		StringBuilder tigs = new StringBuilder("<span class=\"label label-default radius\">" + interfaceInfo.getInterfaceName() + "-" + info.getMessageName() + ":" + "</span>&nbsp;");
		boolean flag = true;
		
		Object[] obj = new Object[3];
		
		try {
			MessageParse parse = MessageParse.getParseInstance(info.getMessageType());
			if (parse == null || !parse.messageFormatValidation(info.getParameterJson())) {
				tigs.append("<span class=\"c-red\">报文格式类型与入参报文不一致或者不正确！导入该条信息失败.</span><br>");
				flag = false;
				obj[0] = info;
				obj[1] = tigs.toString();
				obj[2] = flag;
				return obj;
			}
			String validateFlag = parse.checkParameterValidity(new ArrayList<Parameter>(interfaceInfo.getParameters()), info.getParameterJson());
			if (!"true".equals(validateFlag)) {
				tigs.append("<span class=\"c-red\">在验证该报文的入参报文时出错：<br>" + validateFlag + "<br>导入该条信息失败！</span><br>");
				flag = false;
				obj[0] = info;
				obj[1] = tigs.toString();
				obj[2] = flag;
				return obj;
			}
			
			//设置调用参数
			if (StringUtils.isNotBlank(info.getCallParameter())) {
				GlobalVariableService service = (GlobalVariableService) FrameworkUtil.getSpringBean("globalVariableService");
				String variableKey = info.getCallParameter().substring(4, info.getCallParameter().length() - 1);
				GlobalVariable variable = service.findByKey(variableKey);
				if (variable == null) {
					tigs.append("没有找到Key名称为<strong>" + info.getCallParameter() + "</strong>的调用参数模板,默认不设置该报文的调用参数;");
					info.setCallParameter("");
				} else {
					info.setCallParameter(variable.getValue());
				}
			}
			
			
			if (!"0".equals(info.getStatus()) && !"1".equals(info.getStatus())) {
				tigs.append("接口状态设置不正确,默认设置为正常(0);");
				info.setStatus("0");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			flag = false;
			tigs.append("<span class=\"c-red\">数据异常,请检查上传文档格式.导入该条信息失败!</span><br>");
		}
		
		obj[0] = info;
		obj[1] = tigs.toString();
		obj[2] = flag;
		return obj;
	}
	
	/**
	 * 从excel读取数据
	 * @param path
	 * @return
	 */
	public static List<Message> importValue (String path) {
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
		return new ArrayList<Message>();
	}
	
	/**
	 * 从xls文件读取interfaceInfo
	 * @param path
	 * @return
	 */
	public static List<Message> readXls (String path) {
		HSSFWorkbook hssfWorkbook = null;
		try {
			 InputStream is = new FileInputStream(path);
			 hssfWorkbook = new HSSFWorkbook(is);			 
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		Message info = null;
		List<Message> infos = new ArrayList<Message>();
		
		if (hssfWorkbook != null) {
			// Read the Sheet.只读取第一页
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
			// Read the Row
			 for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                 HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                 if (hssfRow != null) {
                     info = new Message();
                     HSSFCell messageName = hssfRow.getCell(0);
                     HSSFCell messageType = hssfRow.getCell(1);
                     HSSFCell paramsJson = hssfRow.getCell(2);
                     HSSFCell requestUrl = hssfRow.getCell(3);
                     HSSFCell callParameter = hssfRow.getCell(4);
                     HSSFCell status = hssfRow.getCell(5);
                     HSSFCell createMessageScene = hssfRow.getCell(6);
                     
                     info.setMessageName(PoiExcelUtil.getValue(messageName));
                     info.setMessageType(PoiExcelUtil.getValue(messageType).toUpperCase());
                     info.setParameterJson(PoiExcelUtil.getValue(paramsJson));
                     info.setRequestUrl(PoiExcelUtil.getValue(requestUrl));
                     info.setCallParameter(PoiExcelUtil.getValue(callParameter));
                     info.setStatus(PoiExcelUtil.getValue(status));
                     info.setCreateMessageScene(PoiExcelUtil.getValue(createMessageScene));
                     
                     infos.add(info);
                 }
             }
		}
		
		return infos;
		
	}
	
	/**
	 * 从xlsx文件读取interfaceInfo
	 * @param path
	 * @return
	 */
	public static List<Message> readXlsx (String path) {
		XSSFWorkbook xssfWorkbook = null;
        try {
            InputStream is = new FileInputStream(path);
            xssfWorkbook = new XSSFWorkbook(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message info = null;
		List<Message> infos = new ArrayList<Message>();
		
		if (xssfWorkbook != null) {
			// Read the Sheet.只读取第一页
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
			// Read the Row
			 for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                 XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                 if (xssfRow != null) {
                     info = new Message();
                     XSSFCell messageName = xssfRow.getCell(0);
                     XSSFCell messageType = xssfRow.getCell(1);
                     XSSFCell paramsJson = xssfRow.getCell(2);
                     XSSFCell requestUrl = xssfRow.getCell(3);
                     XSSFCell callParameter = xssfRow.getCell(4);
                     XSSFCell status = xssfRow.getCell(5);
                     XSSFCell createMessageScene = xssfRow.getCell(6);
                     
                     info.setMessageName(PoiExcelUtil.getValue(messageName));
                     info.setMessageType(PoiExcelUtil.getValue(messageType).toUpperCase());
                     info.setParameterJson(PoiExcelUtil.getValue(paramsJson));
                     info.setRequestUrl(PoiExcelUtil.getValue(requestUrl));
                     info.setCallParameter(PoiExcelUtil.getValue(callParameter));
                     info.setStatus(PoiExcelUtil.getValue(status));
                     info.setCreateMessageScene(PoiExcelUtil.getValue(createMessageScene));
                     
                     infos.add(info);
                 }
             }
		}
		
		return infos;
        
	}
	
	
}
