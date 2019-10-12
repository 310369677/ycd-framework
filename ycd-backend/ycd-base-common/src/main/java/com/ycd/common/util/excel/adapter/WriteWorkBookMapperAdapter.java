package com.ycd.common.util.excel.adapter;


import com.ycd.common.util.excel.interfaces.WriteSheetMapper;
import com.ycd.common.util.excel.interfaces.WriteWorkBookMapper;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 作者:杨川东
 * 时间:17:06
 */
public class WriteWorkBookMapperAdapter implements WriteWorkBookMapper {

    @Override
    public Map<String, List<CellRangeAddress>> sheetsCellRangeAddresses() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Map<Integer, Map<Short, CellStyle>>> sheetsCellStyles(Workbook workbook) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, WriteSheetMapper> obtainWriteSheetMappers() {
        return Collections.emptyMap();
    }
}
