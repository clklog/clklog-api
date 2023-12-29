package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载趋势分析的请求")
@Data
public class DownloadFlowTrendRequest extends DownloadBaseRequest {

    @Schema(description = "时间类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "hour/day/week/month")
    private String timeType;
    
    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"statTime\",\"pv\",\"pvRate\",\"visitCount\",\"uv\",\"newUv\",\"newUvRate\",\"ipCount\",\"bounceRate\",\"avgVisitTime\",\"avgPv\"]")
    private List<String> cols;
}
