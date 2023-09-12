package com.zcunsoft.clklog.api.models.accesslog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "日志分析请求")
@Data
public class GetAccesslogRequest {

    @Schema(description = "Host", requiredMode = Schema.RequiredMode.REQUIRED, example = "huoqingqing.com")
    private String host;
    
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    private String endTime;
}
