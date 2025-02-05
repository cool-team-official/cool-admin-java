package com.cool.core.request;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class PageResult {
    private List<?> list;
    private Map<String, Object> pagination;
}