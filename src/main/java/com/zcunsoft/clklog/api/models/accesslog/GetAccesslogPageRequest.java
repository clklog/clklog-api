package com.zcunsoft.clklog.api.models.accesslog;

import com.zcunsoft.clklog.api.models.BaseSort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "测试日志分页")
@Data
public class GetAccesslogPageRequest extends BaseSort {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum;

    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private int pageSize;

}
