package com.ycd.common.util;



import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ycd.common.context.PageContext;
import com.ycd.common.dto.Page;

import java.util.List;

public class PageUtil {

    private PageUtil() {
        throw new UnsupportedOperationException("this is a util");
    }

    public static void start() {
        Page page = PageContext.get();
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
    }

    public static <T> void end(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        Page<T> result = new Page<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setData(pageInfo.getList());
        PageContext.setPage(result);
    }

    public static void close() {
        PageHelper.clearPage();
    }

    public static <T> Page<T> getLocalPage() {
        Page<T> page = PageContext.get();
        PageContext.clear();
        return page;
    }
}
