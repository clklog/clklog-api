package com.zcunsoft.clklog.api.models.accesslog;

import com.zcunsoft.clklog.api.models.BaseSort;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "日志分页")
@Data
public class GetAccesslogPageRequest  extends AccesslogBaseSort {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum;

    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private int pageSize;

    @Schema(description = "Host", requiredMode = Schema.RequiredMode.REQUIRED, example = "huoqingqing.com")
    private String host;
    
    @Schema(description = "状态码", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private String status;
    
    @Schema(description = "IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    private String ip;
    
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    private String endTime;
}
