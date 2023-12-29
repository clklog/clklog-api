package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载新老访客分析的请求")
@Data
public class DownloadVisitorRequest extends DownloadBaseRequest {

    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"visitorType\",\"pv\",\"pvRate\",\"visitCount\",\"uv\",\"uvRate\",\"ipCount\",\"bounceRate\",\"avgVisitTime\",\"avgPv\"]")
    private List<String> cols;
}
