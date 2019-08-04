package yi.master.util.excel;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PoiExcelUtil {

	public static final String RESULT_MAP_TOTAL_COUNT = "totalCount";
	public static final String RESULT_MAP_SUCCESS_COUNT = "successCount";
	public static final String RESULT_MAP_FAIL_COUNT = "failCount";
	public static final String RESULT_MAP_MSG = "msg";
	public final static int XLS = 97;  
    public final static int XLSX = 2007; 

	/**
	 * 获取文件扩展名
	 * 
	 * @param path
	 * @return String
	 * @author zhang 2015-08-17 23:26
	 */
	public static String getExt(String path) {
		if (path == null || path.equals("") || !path.contains(".")) {
			return null;
		} else {
			return path.substring(path.lastIndexOf(".") + 1, path.length());
		}
	}

	/**
	 * 判断后缀为xlsx的excel文件的数据类型
	 * 
	 * @param xssfRow
	 * @return String
	 * @author zhang 2015-08-18 00:12
	 */
	@SuppressWarnings("static-access")
	public static String getValue(XSSFCell xssfRow) {
		if (xssfRow == null) {
			return "";
		}
		if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfRow.getBooleanCellValue());
		} else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
			return String.valueOf(xssfRow.getNumericCellValue());
		} else {
			return String.valueOf(xssfRow.getStringCellValue());
		}
	}

	/**
	 * 判断后缀为xls的excel文件的数据类型
	 * 
	 * @param hssfCell
	 * @return String
	 * @author zhang 2015-08-18 00:12
	 */
	@SuppressWarnings("static-access")
	public static String getValue(HSSFCell hssfCell) {
		if (hssfCell == null) {
			return "";
		}
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}

	/**
	 * 创建Workbook
	 * 
	 * @param type
	 *            Excel类型, 97-2003或2007
	 * @return
	 * @throws IOException
	 */
	public static Workbook createWorkBook(int type) throws IOException {
		Workbook wb = null;
		if (type == XLSX) {
			wb = new XSSFWorkbook();
		} else {
			wb = new HSSFWorkbook();
		}
		return wb;
	}

	/**
	 * 合并单元格，可以根据设置的值来合并行和列
	 * 
	 * @param sheet
	 * @param firstRow
	 * @param lastRow
	 * @param firstColumn
	 * @param lastColumn
	 */
	public static void MergingCells(Sheet sheet, int firstRow, int lastRow,
			int firstColumn, int lastColumn) {
		sheet.addMergedRegion(new CellRangeAddress(firstRow, // first row
																// (0-based)
				lastRow, // last row (0-based)
				firstColumn, // first column (0-based)
				lastColumn // last column (0-based)
		));
	}

	/**
	 * 创建头部样式
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle createHeadCellStyle(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		addAlignStyle(cellStyle, CellStyle.ALIGN_CENTER,
				CellStyle.VERTICAL_CENTER);
		addBorderStyle(cellStyle, CellStyle.BORDER_MEDIUM,
				IndexedColors.BLACK.getIndex());
		addColor(cellStyle, IndexedColors.GREY_25_PERCENT.getIndex(),
				CellStyle.SOLID_FOREGROUND);
		return cellStyle;
	}

	/**
	 * 创建普通单元格样式
	 * 
	 * @param wb
	 * @return
	 */
	public static CellStyle createDefaultCellStyle(Workbook wb) {
		CellStyle cellStyle = wb.createCellStyle();
		addAlignStyle(cellStyle, CellStyle.ALIGN_CENTER,
				CellStyle.VERTICAL_CENTER);
		addBorderStyle(cellStyle, CellStyle.BORDER_THIN,
				IndexedColors.BLACK.getIndex());
		return cellStyle;
	}

	/**
	 * cell文本位置样式
	 * 
	 * @param cellStyle
	 * @param halign
	 * @param valign
	 * @return
	 */
	public static CellStyle addAlignStyle(CellStyle cellStyle, short halign,
			short valign) {
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		return cellStyle;
	}

	/**
	 * cell边框样式
	 * 
	 * @param cellStyle
	 * @return
	 */
	public static CellStyle addBorderStyle(CellStyle cellStyle,
			short borderSize, short colorIndex) {
		cellStyle.setBorderBottom(borderSize);
		cellStyle.setBottomBorderColor(colorIndex);
		cellStyle.setBorderLeft(borderSize);
		cellStyle.setLeftBorderColor(colorIndex);
		cellStyle.setBorderRight(borderSize);
		cellStyle.setRightBorderColor(colorIndex);
		cellStyle.setBorderTop(borderSize);
		cellStyle.setTopBorderColor(colorIndex);
		return cellStyle;
	}

	/**
	 * 给cell设置颜色
	 * 
	 * @param cellStyle
	 * @param backgroundColor
	 * @param fillPattern
	 * @return
	 */
	public static CellStyle addColor(CellStyle cellStyle,
			short backgroundColor, short fillPattern) {
		cellStyle.setFillForegroundColor(backgroundColor);
		cellStyle.setFillPattern(fillPattern);
		return cellStyle;
	}

	/**
	 * 创建标签页
	 * @param wb
	 * @param sheetName
	 * @return
	 */
	public static Sheet createSheet(Workbook wb, String sheetName) {
		return wb.createSheet(sheetName);
	}

	/**
	 * 创建行
	 * @param sheet
	 * @param rownum
	 * @return
	 */
	public static Row createRow(Sheet sheet, int rownum) {
		return sheet.createRow(rownum);
	}

	/**
	 * 创建单元格
	 * @param row 行对象
	 * @param column 列数
	 * @return
	 */
	public static Cell createCell(Row row, int column) {
		return row.createCell(column);
	}
}
