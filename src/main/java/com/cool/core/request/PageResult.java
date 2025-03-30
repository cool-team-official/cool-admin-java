package com.cool.core.request;

import java.util.List;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema( title = "分页数据模型" )
public class PageResult<T> {
    @Schema( title = "分页数据" )
    private List<T> list;
    private Pagination pagination = new Pagination();

    @Data
    public static class Pagination {
        @Schema( title = "页码" )
        private Long page;
        @Schema( title = "本页数量" )
        private Long size;
        @Schema( title = "总页数" )
        private Long total;
    }
    
    static public <B> PageResult<B> of(Page<B> page ){
        PageResult<B> result = new PageResult<B>();
        result.setList(page.getRecords());
        result.pagination.setPage( page.getPageNumber() );
        result.pagination.setSize( page.getPageSize() );
        result.pagination.setTotal( page.getTotalRow() );
        return result;
    }
}