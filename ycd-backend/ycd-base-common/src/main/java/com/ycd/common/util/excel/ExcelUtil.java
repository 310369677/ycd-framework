package com.ycd.common.util.excel;


import com.ycd.common.util.SimpleUtil;
import com.ycd.common.util.excel.adapter.ReadSheetMapperAdapter;
import com.ycd.common.util.excel.adapter.WriteSheetMapperAdapter;
import com.ycd.common.util.excel.adapter.WriteWorkBookMapperAdapter;
import com.ycd.common.util.excel.enums.ExcelType;
import com.ycd.common.util.excel.interfaces.ReadSheetMapper;
import com.ycd.common.util.excel.interfaces.WriteRowMapper;
import com.ycd.common.util.excel.interfaces.WriteSheetMapper;
import com.ycd.common.util.excel.interfaces.WriteWorkBookMapper;
import io.swagger.models.auth.In;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * 描述:
 * 作者:杨川东
 * 时间:10:48
 */
public class ExcelUtil {
    /**
     * 解析excel文件
     *
     * @param file            要解析的文件
     * @param readSheetMapper 单元格匹配器
     * @return 解析的结果
     * @throws IOException 异常
     */
    public static Map<String, List> readExcel(File file, ReadSheetMapper readSheetMapper) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ExcelType excelType = parseExcelType(file.getName());
        return readExcel(fileInputStream, readSheetMapper, excelType);
    }


    public static Map<String, List> readExcel(InputStream inputStream, ReadSheetMapper readSheetMapper, ExcelType excelType) throws IOException {
        ReadSheetMapper defaultReadSheetMapper = new ReadSheetMapperAdapter();
        if (readSheetMapper != null) {
            defaultReadSheetMapper = readSheetMapper;
        }
        //POIFSFileSystem poifsFileSystem = new POIFSFileSystem(inputStream);
        //创建工作簿对象
        Workbook workbook = createWorkBook(excelType, inputStream);
        //得到工作表的数量
        int sheetNum = workbook.getNumberOfSheets();
        Map<String, List> workbookResult = new HashMap<>();
        int startSheetIndex = 0;  //开始解析sheet表的索引
        if (defaultReadSheetMapper.startParseSheetIndex() >= 0) {
            startSheetIndex = defaultReadSheetMapper.startParseSheetIndex();
        }
        int endSheetIndex = sheetNum;  //结束解析sheet表的索引
        if (defaultReadSheetMapper.endParseSheetIndex() >= 0) {
            endSheetIndex = (defaultReadSheetMapper.endParseSheetIndex()) > endSheetIndex ? endSheetIndex : (defaultReadSheetMapper.endParseSheetIndex());
        }

        for (int i = startSheetIndex; i < endSheetIndex; i++) {
            Sheet sheet = workbook.getSheetAt(i);  //得到sheet表
            List<Object> sheetResult = parseSheet(sheet, defaultReadSheetMapper);
            workbookResult.put(sheet.getSheetName(), sheetResult);
        }
        return workbookResult;
    }


    /**
     * 写入sheet表
     *
     * @param outputStream        写入的流
     * @param data                数据
     * @param extraData           额外的数据
     * @param writeWorkBookMapper 写入匹配器
     * @param excelType           excel的类型
     * @throws IOException 读取文件的异常
     */
    public static void writeExcel(OutputStream outputStream, Map<String, Map<Integer, Map<Short, Object>>> data, Object extraData, WriteWorkBookMapper writeWorkBookMapper, ExcelType excelType) throws IOException {
        WriteWorkBookMapper defaultWriteWorkBookMapper = new WriteWorkBookMapperAdapter();
        if (writeWorkBookMapper != null) {  //外部注入的writeSheetMapper
            defaultWriteWorkBookMapper = writeWorkBookMapper;
        }
        //创建工作簿
        Workbook workbook = createWorkBook(excelType, null);
        //写入工作表
        for (Map.Entry<String, Map<Integer, Map<Short, Object>>> entry : data.entrySet()) {
            String sheetName = entry.getKey();  //sheet表的名字
            Sheet sheet = workbook.createSheet(sheetName);
            //得到当前sheet表所有需要合并的区域
            List<CellRangeAddress> cellRangeAddressList = defaultWriteWorkBookMapper.sheetsCellRangeAddresses().get(sheetName);
            if (cellRangeAddressList == null) {
                cellRangeAddressList = Collections.emptyList();  //空的list
            }
            //得到当前sheet表的样式
            Map<Integer, Map<Short, CellStyle>> sheetCellStyles = defaultWriteWorkBookMapper.sheetsCellStyles(workbook).get(sheetName);
            if (sheetCellStyles == null) {
                sheetCellStyles = Collections.emptyMap();
            }
            //得到当前sheet表sheetMapper
            WriteSheetMapper writeSheetMapper = defaultWriteWorkBookMapper.obtainWriteSheetMappers().get(sheetName);
            //当前sheet表的数据
            Map<Integer, Map<Short, Object>> sheetData = data.get(sheetName);
            writeSingleSheet(sheet, sheetData, extraData, sheetCellStyles, cellRangeAddressList, writeSheetMapper);
        }
        workbook.write(outputStream);
    }

    public static void writeSingleSheet(Sheet sheet, Map<Integer, Map<Short, Object>> sheetData, Object extraData, Map<Integer, Map<Short, CellStyle>> sheetCellStyles, List<CellRangeAddress> cellRangeAddressList, WriteSheetMapper writeSheetMapper) {
        WriteSheetMapper defaultWriteSheetMapper = new WriteSheetMapperAdapter();
        if (writeSheetMapper != null) {
            defaultWriteSheetMapper = writeSheetMapper;
        }
        defaultWriteSheetMapper.preHandleSheet(sheet, sheetData, extraData);  //预处理
        for (CellRangeAddress cellRangeAddress : cellRangeAddressList) {  //添加需要混合的样式
            sheet.addMergedRegion(cellRangeAddress);
        }

        //开始填充数据
        for (Map.Entry<Integer, Map<Short, Object>> rowEntry : sheetData.entrySet()) {
            Integer rowIndex = rowEntry.getKey();  //行的索引
            Map<Short, Object> rowData = rowEntry.getValue();  //行的数据
            //得到对应行的样式
            Map<Short, CellStyle> rowCellStyle = sheetCellStyles.get(rowIndex);
            if (rowCellStyle == null) {
                rowCellStyle = Collections.emptyMap();
            }
            //创建行
            Row row = sheet.createRow(rowIndex);
            WriteRowMapper writeRowMapper = defaultWriteSheetMapper.obtainWriteRowMappers().get(rowIndex);
            writeSingleRowData(row, rowData, rowCellStyle, writeRowMapper);
        }

    }

    private static void writeSingleRowData(Row row, Map<Short, Object> rowData, Map<Short, CellStyle> rowCellStyle, WriteRowMapper writeRowMapper) {
        WriteRowMapper defaultWriteRowMapper = new WriteRowMapper() {

            @Override
            public void handleCell(int rowIndex, short columnIndex, Cell cell, Object cellValue, CellStyle cellStyle) {
                if (cellStyle != null) {
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellValue(cellValue.toString());
            }
        };
        if (writeRowMapper != null) {
            defaultWriteRowMapper = writeRowMapper;
        }
        //开始写行的数据
        for (Map.Entry<Short, Object> rowDataEntry : rowData.entrySet()) {
            Short columnIndex = rowDataEntry.getKey();  //列的索引
            Object cellData = rowDataEntry.getValue();  //单元格的数据
            CellStyle cellStyle = rowCellStyle.get(columnIndex); //单元格的样式
            //创建单元格
            Cell cell = row.createCell(columnIndex);
            defaultWriteRowMapper.handleCell(row.getRowNum(), columnIndex, cell, cellData, cellStyle);
        }
    }

    /**
     * 解析sheet表的具体逻辑
     *
     * @param sheet 表
     */
    private static List<Object> parseSheet(Sheet sheet, ReadSheetMapper readSheetMapper) {
        Map<Integer, Map<Short, Object>> resultData = new HashMap<>();
        Map<Integer, Map<Short, String>> errorInfo = new HashMap<>();
        List<Object> resultInfo = new ArrayList<>();
        int startRowIndex = sheet.getFirstRowNum();  //默认的起始行
        if (readSheetMapper.startRowIndex() >= 0) {
            startRowIndex = readSheetMapper.startRowIndex();
        }
        int endRowIndex = sheet.getLastRowNum();
        if (readSheetMapper.endRowIndex() >= 0) {
            endRowIndex = (readSheetMapper.endRowIndex() - 1) > endRowIndex ? endRowIndex : (readSheetMapper.endRowIndex() - 1);
        }
        for (int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++) {
            boolean currentRowIgnore = false;
            int[] ignoreRows = readSheetMapper.ignoreRowIndex();  //忽略行的索引
            if (Arrays.binarySearch(ignoreRows, rowIndex) >= 0) {
                currentRowIgnore = true;
            }
            if (currentRowIgnore) {
                continue;
            }
            Row row = sheet.getRow(rowIndex);
            Map<Short, Object> rowDataMap = new LinkedHashMap<>();
            Map<Short, String> rowErrorInfo = new HashMap<>();
            //得到有多少列
            boolean parseError = false;
            short startColumnIndex = row.getFirstCellNum();
            if (readSheetMapper.startColumnIndex() >= 0) {
                startColumnIndex = readSheetMapper.startColumnIndex();
            }
            short endColumnIndex = row.getLastCellNum();
            if (readSheetMapper.endColumnIndex() >= 0) {
                endColumnIndex = (readSheetMapper.endColumnIndex()) > endColumnIndex ? endColumnIndex : (readSheetMapper.endColumnIndex());
            }
            for (short columnIndex = startColumnIndex; columnIndex < endColumnIndex; columnIndex++) {
                boolean currentColumnIgnore = false;
                short[] ignoreColumns = readSheetMapper.ignoreColumnIndex();
                if (Arrays.binarySearch(ignoreColumns, columnIndex) >= 0) {
                    currentColumnIgnore = true;
                }
                if (currentColumnIgnore) {
                    continue;
                }

                Cell cell = row.getCell(columnIndex);  //得到单元格
                try {
                    rowDataMap.put(columnIndex, readSheetMapper.handleCell(cell, rowIndex, columnIndex));
                } catch (RuntimeException e) {  //这一列发生错误
                    rowErrorInfo.put(columnIndex, e.getMessage());
                    parseError = true;
                }
            }
            if (parseError) {  //这一行解析出错了
                errorInfo.put(rowIndex, rowErrorInfo);
                continue;
            }
            resultData.put(rowIndex, rowDataMap);

        }
        resultInfo.add(resultData);
        resultInfo.add(errorInfo);
        return resultInfo;
    }

    /**
     * 创建工作簿
     *
     * @param excelType 文件的类型
     * @return 工作簿的类型
     */
    private static Workbook createWorkBook(ExcelType excelType, InputStream inputStream) throws IOException {
        switch (excelType) {
            case XLS:
                return inputStream == null ? new HSSFWorkbook() : new HSSFWorkbook(inputStream);
            case XLSX:
                return inputStream == null ? new XSSFWorkbook() : new XSSFWorkbook(inputStream);
        }
        throw new RuntimeException("无法解析的excel的表格类型");
    }

    public static ExcelType parseExcelType(String fileName) {
        if (fileName.endsWith(".xls")) {
            return ExcelType.XLS;
        }
        if (fileName.endsWith(".xlsx")) {
            return ExcelType.XLSX;
        }
        return ExcelType.UNKNOWN;
    }

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\Administrator\\Desktop\\2019.9行政区划代码.xlsx");
        Map<String, String> area = new HashMap<>();
        Map<String, List> result = ExcelUtil.readExcel(file, new ReadSheetMapperAdapter());
        Map<Integer, Map<Short, String>> data = new HashMap<>();
        for (Map.Entry<String, List> entry : result.entrySet()) {
            String sheetName = entry.getKey();
            if ("工作表1".equals(sheetName)) {
                data = (Map<Integer, Map<Short, String>>) entry.getValue().get(0);
                break;
            }
        }
        int i = 0;
        for (Map<Short, String> row : data.values()) {
            if (i == 0) {
                i++;
                continue;
            }
            i++;
            int j = 0;
            while (j <= 4) {
                area.putIfAbsent(row.get((short) (j + 1)), row.get((short) j));
                j += 2;
            }
        }
        System.out.println(area);
        /*    File file=new File("d:/testNew.xls");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        Map<String,Map<Integer,Map<Short,Object>>> map=new HashMap<>();
        Map<Integer,Map<Short,Object>> sheetData=new HashMap<>();
        map.put("test",sheetData);
        Map<Short,Object> data=new HashMap<>();
        data.put((short)0,"学号");
        data.put((short)1,"姓名");
        sheetData.put(0,data);
        data=new HashMap<>();
        data.put((short)0,1);
        data.put((short)1,"张三");
        sheetData.put(1,data);
        writeExcel(fileOutputStream,map,null,null,ExcelType.XLS);*/
    }
}
