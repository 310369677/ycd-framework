package com.ycd.common.dto;

import java.util.List;

public class Page<T> {

    /**
     * 总的记录数
     */
    private long total;

    /**
     * 每页的大小
     */
    private int pageSize;

    /**
     * 页的数量
     */
    private int pageNum;


    private List<T> data;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }


    public int firstResult() {
        return (pageNum - 1) * pageSize;
    }

    public int maxResults() {
        return pageSize;
    }
}
