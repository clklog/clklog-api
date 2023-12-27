package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "获取单个受访页面趋势详情的请求")
@Data
public class GetVisitUriDetailTrendRequest extends GetVisitUriDetailTotalRequest{

    @Schema(description = "时间类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "day/week/month")
    private String timeType;

}
