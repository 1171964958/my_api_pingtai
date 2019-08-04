package yi.master.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import yi.master.business.advanced.bean.PerformanceTestResult;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;

public class ExportPerformanceTestResult {
	private static final Logger LOGGER = Logger.getLogger(ExportPerformanceTestResult.class);
	
	public static String exportDocuments(List<PerformanceTestResult> results) throws Exception {
		String filePath = null;
		OutputStream outputStream = null; 
		String fileName = PracticalUtils.getUUID("") + ".xlsx";
		
		try {
			//创建excel
			Workbook wb = PoiExcelUtil.createWorkBook(2007);
			
			writeData(wb, results);
			filePath = SystemConsts.EXCEL_FILE_FOLDER + File.separator + fileName;					
			outputStream = new FileOutputStream(new File(FrameworkUtil.getProjectPath() + File.separator + filePath));
			
			wb.write(outputStream); 			
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
		
		return filePath;
	}
	
	
	private static void writeData (Workbook wb, List<PerformanceTestResult> results) {
		Sheet sheet = PoiExcelUtil.createSheet(wb, "汇总结果");
		CellStyle headerStyle = PoiExcelUtil.createHeadCellStyle(wb);
		
		//创建标题
		String[] titles = new String[]{
			"接口场景", "测试环境", "总事务数", "成功事务数", "失败事务数", "平均TPS", "平均响应时间", "开始时间", "结束时间"	
		};
		Row titleRow = sheet.createRow(0);		
		for (int i = 0; i < titles.length; i++) {
			Cell cell = titleRow.createCell(i);
			cell.setCellValue(titles[i]);
			cell.setCellStyle(headerStyle);
		}
		
		//填充内容
		for (int i = 0;i < results.size();i++) {
			Row row = sheet.createRow(i + 1);
			PerformanceTestResult ptr = results.get(i);
			row.createCell(0).setCellValue(ptr.getInterfaceName());
			row.createCell(1).setCellValue(ptr.getSystemName());
			row.createCell(2).setCellValue(ptr.getAnalyzeResult().getTotalCount());
			row.createCell(3).setCellValue(ptr.getAnalyzeResult().getSuccessCount());
			row.createCell(4).setCellValue(ptr.getAnalyzeResult().getFailCount());
			row.createCell(5).setCellValue(ptr.getAnalyzeResult().getTpsAvg());
			row.createCell(6).setCellValue(ptr.getAnalyzeResult().getResponseTimeAvg());
			row.createCell(7).setCellValue(PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss", ptr.getStartTime()));
			row.createCell(8).setCellValue(PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss", ptr.getFinishTime()));
		}
	}
}
