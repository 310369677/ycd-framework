package com.ycd.common.util.excel.interfaces;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 描述:
 * 作者:杨川东
 * 时间:16:59
 */
public interface ReadSheetMapper {
    int startParseSheetIndex();   //开始解析sheet表时的索引

    int endParseSheetIndex();     //结束解析sheet表的索引 "-1"表示不提前结束

    int startRowIndex();         //从多少行开始解析

    int endRowIndex();          //解析到多少行结束

    short startColumnIndex();      //从多少列开始解析

    short endColumnIndex();        //解析到多少列结束

    int[] ignoreRowIndex();      //忽略行的索引

    short[] ignoreColumnIndex();  //忽略的列的索引

    Object handleCell(Cell cell, int rowIndex, int columnIndex) throws RuntimeException; //自定义处理单元格的值信息
}
