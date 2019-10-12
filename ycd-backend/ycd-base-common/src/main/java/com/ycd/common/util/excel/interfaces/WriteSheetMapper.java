package com.ycd.common.util.excel.interfaces;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * 描述:
 * 作者:杨川东
 * 时间:18:59
 */
public interface WriteSheetMapper {
    /**
     * @return 行索引对应的行匹配器
     */
    Map<Integer, WriteRowMapper> obtainWriteRowMappers();

    /**
     * 预处理sheet表例如可以为sheet表加密等等
     *
     * @param sheet sheet表
     */
    void preHandleSheet(Sheet sheet, Map<Integer, Map<Short, Object>> data, Object extraData);
}
