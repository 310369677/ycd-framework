package com.ycd.common.util.excel.adapter;


import com.ycd.common.util.excel.interfaces.ReadSheetMapper;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 描述:
 * 作者:杨川东
 * 时间:17:00
 */
public class ReadSheetMapperAdapter implements ReadSheetMapper {

    @Override
    public int startParseSheetIndex() {
        return 0;
    }

    @Override
    public int endParseSheetIndex() {
        return -1;
    }

    @Override
    public int startRowIndex() {
        return -1;
    }

    @Override
    public int endRowIndex() {
        return -1;
    }

    @Override
    public short startColumnIndex() {
        return -1;
    }

    @Override
    public short endColumnIndex() {
        return -1;
    }

    @Override
    public int[] ignoreRowIndex() {
        return new int[0];
    }

    @Override
    public short[] ignoreColumnIndex() {
        return new short[0];
    }

    //默认的处理
    @Override
    public Object handleCell(Cell cell, int rowIndex, int columnIndex) throws RuntimeException {
        if (cell == null) {
            return "";
        }
        cell.setCellType(Cell.CELL_TYPE_STRING); //默认处理成字符串
        return cell.getStringCellValue();
    }
}
