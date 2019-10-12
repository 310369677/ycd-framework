package com.ycd.common.util.excel.interfaces;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * 描述:
 * 作者:杨川东
 * 时间:19:03
 */
public interface WriteRowMapper {

    void handleCell(int rowIndex, short columnIndex, Cell cell, Object cellValue, CellStyle cellStyle);
}
