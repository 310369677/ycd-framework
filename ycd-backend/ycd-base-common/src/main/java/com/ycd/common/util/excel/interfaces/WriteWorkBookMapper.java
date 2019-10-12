package com.ycd.common.util.excel.interfaces;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 作者:杨川东
 * 时间:17:05
 */
public interface WriteWorkBookMapper {
    /**
     * 需要合并单元格区域的列表
     *
     * @return 对应sheet表和其相对应的单元格需要合并的区域
     */
    Map<String, List<CellRangeAddress>> sheetsCellRangeAddresses();

    /**
     * 返回单元格的样式
     *
     * @return sheet表对应的单元格的样式
     */
    Map<String, Map<Integer, Map<Short, CellStyle>>> sheetsCellStyles(Workbook workbook);

    Map<String, WriteSheetMapper> obtainWriteSheetMappers();

}
