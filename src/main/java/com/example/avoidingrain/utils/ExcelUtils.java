package com.example.avoidingrain.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yuanqixu
 */
@Slf4j
public class ExcelUtils {
    private static final String EXCEL_CONTENT_TYPE          = "application/msexcel";
    private static final String EXCEL_CONTENT_LENGTH        = "Content-Length";
    private static final String EXCEL_HEAD_TYPE             = "Content-Disposition";
    private static final String EXCEL_HEAD_FILE_NAME        = "attchement;filename=%s";
    private static final String EXCEL_HEAD_FILE_NAME_FORMAT = "ISO8859-1";
    private static final String GB2312                      = "gb2312";

    public static List<List<String>> head(List<String> array) {
        List<List<String>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(array)) {
            for (String s : array) {
                List<String> head = new ArrayList<>();
                head.add(s);
                list.add(head);
            }
        }
        return list;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    public static String getStringCellValue(Cell cell) {
        String strCell = "";
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                strCell = cell.getStringCellValue().trim();
                break;
            case NUMERIC:
                //解决大数科学计数法、小数带.0问题
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 不显示千位分割符，否则显示结果会变成类似1,234,567,890
                numberFormat.setGroupingUsed(false);
                numberFormat.setMaximumFractionDigits(1000);
                strCell = numberFormat.format(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            default:
                strCell = "";
                break;
        }
        return strCell;
    }

    /**
     * get excel sheet by sheet index
     *
     * @param fileBytes
     * @param sheetIndex
     * @return
     */
    public static Sheet getSheet(byte[] fileBytes, int sheetIndex) {
        Sheet sheet = null;
        if (fileBytes != null && fileBytes.length > 0) {
            InputStream inputStream = new ByteArrayInputStream(fileBytes);
            try {
                Workbook workbook = WorkbookFactory.create(inputStream);// 获得工作簿
                sheet = getSheet(workbook, sheetIndex);
            } catch (Exception ex) {
                log.error("ExcelUtils.getSheet", ex);
            }
        }
        return sheet;
    }

    /**
     * 取workBook中的Sheet
     *
     * @param workbook
     * @param sheetIndex
     * @return
     */
    private static Sheet getSheet(Workbook workbook, int sheetIndex) {
        Sheet sheet = null;
        if (workbook != null) {
            int sheetCount = workbook.getNumberOfSheets();// 获得工作表个数
            if (sheetCount > sheetIndex) {
                sheet = workbook.getSheetAt(sheetIndex);
            }
        }
        return sheet;
    }

    /**
     * 根据数据导出Excel字节流xlsx
     *
     * @param sheetName：文件名
     * @param rowNameList：文件头
     * @param list：文件体
     * @return
     */
    public static byte[] excelExport(String sheetName, List<String> rowNameList, List<Map<String, String>> list) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth(25);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THICK);
        cellStyle.setBorderLeft(BorderStyle.THICK);
        cellStyle.setBorderTop(BorderStyle.THICK);
        cellStyle.setBorderRight(BorderStyle.THICK);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle headCellStyle = wb.createCellStyle();
        headCellStyle.cloneStyleFrom(cellStyle);
        headCellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());

        TreeMap<String, Integer> headMap = new TreeMap<>();
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < rowNameList.size(); i++) {
            row.setHeight((short) 450);
            XSSFCell cell = row.createCell(i);
            String headName = rowNameList.get(i);
            cell.setCellValue(headName);
            headMap.put(headName, i);
            cell.setCellStyle(headCellStyle);
        }

        int index = 1;
        for (Map<String, String> map : list) {
            XSSFRow dataRow = sheet.createRow(index++);
            for (Map.Entry<String, Integer> m : headMap.entrySet()) {
                String name = m.getKey();
                String value = map.get(name);
                XSSFCell cell = dataRow.createCell(m.getValue());
                if (value != null) {
                    cell.setCellValue(value);
                }
                cell.setCellStyle(cellStyle);
            }
        }
        byte[] excelBytes = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            wb.write(byteStream);
            excelBytes = byteStream.toByteArray();
        } catch (Exception ex) {
            log.warn("ExcelUtils.excelExport", ex);
        } finally {
                byteStream.close();
                wb.close();
        }
        return excelBytes;
    }

    /**
     * 根据文件名和数据，导出excel
     *
     * @param excelBytes
     * @param response
     * @param fileName
     */
    public static void exportExcel(byte[] excelBytes, HttpServletResponse response, String fileName) {
        try {
            if (response != null && !StringUtils.isEmpty(fileName)) {
                if (excelBytes != null) {
                    OutputStream outputStream = response.getOutputStream();
                    response.reset();
                    String fileSet = String.format(EXCEL_HEAD_FILE_NAME, fileName);
                    String fileSetInfo = new String(fileSet.getBytes(GB2312), EXCEL_HEAD_FILE_NAME_FORMAT);
                    response.setHeader(EXCEL_HEAD_TYPE, fileSetInfo);
                    //修正 Excel在“xxx.xlsx”中发现不可读取的内容。是否恢复此工作薄的内容？如果信任此工作簿的来源，请点击"是"
                    response.setHeader(EXCEL_CONTENT_LENGTH, String.valueOf(excelBytes.length));
                    response.setContentType(EXCEL_CONTENT_TYPE);
                    outputStream.write(excelBytes);
                } else {
                    log.warn("ExcelUtils.exportExcel", "excelBytes null");
                }
            }
        } catch (Exception ex) {
            log.warn("ExcelUtils.exportExcel", ex);
        }
    }

    /**
     * 根据数据导出Excel字节流xlsx
     *
     * @param sheetName：文件名
     * @param rowNameList：文件头
     * @param list：文件体
     * @return
     */
    public static byte[] getExcelDataDefault(String sheetName, List<String> rowNameList, List<Map<String, String>> list) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth(25);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle headCellStyle = wb.createCellStyle();
        headCellStyle.cloneStyleFrom(cellStyle);
        headCellStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());

        TreeMap<String, Integer> headMap = new TreeMap<>();
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < rowNameList.size(); i++) {
            row.setHeight((short) 450);
            XSSFCell cell = row.createCell(i);
            String headName = rowNameList.get(i);
            cell.setCellValue(headName);
            headMap.put(headName, i);
            cell.setCellStyle(headCellStyle);
        }

        int index = 1;
        for (Map<String, String> map : list) {
            XSSFRow dataRow = sheet.createRow(index++);
            for (Map.Entry<String, Integer> m : headMap.entrySet()) {
                String name = m.getKey();
                String value = map.get(name);
                XSSFCell cell = dataRow.createCell(m.getValue());
                if (value != null) {
                    cell.setCellValue(value);
                }
                cell.setCellStyle(cellStyle);
            }
        }
        byte[] excelBytes = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            wb.write(byteStream);
            excelBytes = byteStream.toByteArray();
        } catch (Exception ex) {
            log.warn("ExcelUtils.excelExport", ex);
        } finally {
                byteStream.close();
                wb.close();
        }
        return excelBytes;
    }


}
