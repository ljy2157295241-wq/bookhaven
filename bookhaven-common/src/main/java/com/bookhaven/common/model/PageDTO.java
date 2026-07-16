package com.bookhaven.common.model;

import lombok.Data;
import java.util.List;

@Data
public class PageDTO<T> {
    private List<T> records;
    private long total;
    private int page;
    private int size;

    public PageDTO(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
