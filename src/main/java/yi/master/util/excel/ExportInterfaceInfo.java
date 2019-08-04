package yi.master.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Parameter;
import yi.master.constant.MessageKeys;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;
import yi.master.util.FrameworkUtil;

/**
 * 从数据库导出接口详细文档
 * @author xuwangcheng
 * @version 20171214, 1.0.0.0
 *
 */
public class ExportInterfaceInfo {
	
	private static final Logger LOGGER = Logger.getLogger(ExportInterfaceInfo.class);
	
	public static String exportDocuments (List<InterfaceInfo> infos, int type) throws Exception {
		String path = null;
		OutputStream outputStream = null; 
		String fileName = "接口文档_" + System.currentTimeMillis() + "." + (PoiExcelUtil.XLSX == type ? "xlsx" : "xls");
		try {
			//创建excel
			Workbook wb = PoiExcelUtil.createWorkBook(type);
			
			for (InterfaceInfo info:infos) {
				writeSheet(info, wb);
			}
								
			outputStream = new FileOutputStream(new File(FrameworkUtil.getProjectPath() + File.separator + SystemConsts.EXCEL_FILE_FOLDER 
					+ File.separator + fileName));
			
			wb.write(outputStream); 
			path = SystemConsts.EXCEL_FILE_FOLDER + File.separator + fileName;
		} catch (Exception e) {
			
			LOGGER.error("写Excel文件失败:文件路径" + FrameworkUtil.getProjectPath() + File.separator + SystemConsts.EXCEL_FILE_FOLDER  + File.separator + fileName, e);
			throw e;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}				
		
		return path;
	}
	
	public static void writeSheet (InterfaceInfo info,  Workbook wb) {
		Sheet sheet = PoiExcelUtil.createSheet(wb, info.getInterfaceName() + "_" + info.getInterfaceCnName());
		CellStyle headerStyle = PoiExcelUtil.createHeadCellStyle(wb);
		//创建标题和基本信息
		String[][] titles = new String[][]{
				new String[]{"接口名", info.getInterfaceName()},
				new String[]{"中文名", info.getInterfaceCnName()},
				new String[]{"类型", "CX".equalsIgnoreCase(info.getInterfaceType()) ? "查询类" : "办理类"},
				new String[]{"协议", info.getInterfaceProtocol()},
				new String[]{"当前状态", "0".equals(info.getStatus()) ? "正常" : "禁用"},
				new String[]{"请求路径", info.getRequestUrlReal()},
				new String[]{"创建用户", info.getUser().getRealName()},
				new String[]{"最后修改", info.getLastModifyUser()},
				new String[]{"备注说明", info.getMark() == null ? "" : info.getMark()},
				new String[]{"导出时间", PracticalUtils.formatDate(PracticalUtils.FULL_DATE_PATTERN, new Date())},				
		};
		
		for (int i = 0; i < titles.length; i++) {
			Row row = sheet.createRow(i);
			for (int j = 0; j < titles[i].length; j++) {				
				Cell cell = row.createCell(j);
				cell.setCellValue(titles[i][j]);
				if (j == 0) {
					cell.setCellStyle(headerStyle);
				} 
			}
		}	
		
		//创建参数说明的部分
		sheet.createRow(titles.length); //添加一个空行
		//创建参数说明的标题
		int count = titles.length;
		
		Row row_params_mark = sheet.createRow(++count);
		Cell c = PoiExcelUtil.createCell(row_params_mark, 0);
		c.setCellStyle(headerStyle);
		c.setCellValue("入参节点");
		PoiExcelUtil.MergingCells(sheet, count, count, 0, 5);		
		
		Row row_t = sheet.createRow(++count);
		String[] paramTitles = new String[]{"标识", "名称", "默认值", "路径", "类型", "备注"};
		
		for (int i = 0; i < paramTitles.length; i++) {
			Cell cell = row_t.createCell(i);
			cell.setCellValue(paramTitles[i]);
			cell.setCellStyle(headerStyle);
		}
		
		//参数信息
		for (Parameter p:info.getParameters()) {
			Row row_params = sheet.createRow(++count);
			row_params.createCell(0).setCellValue(p.getParameterIdentify());
			row_params.createCell(1).setCellValue(p.getParameterName());
			row_params.createCell(2).setCellValue(p.getDefaultValue());
			row_params.createCell(3).setCellValue(p.getPath().replace(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + ".", "")
					.replace(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH, ""));
			row_params.createCell(4).setCellValue(p.getType());
			row_params.createCell(5).setCellValue(p.getMark());
		}
	}

}
