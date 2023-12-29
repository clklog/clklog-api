package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载来源网站分析的请求")
@Data
public class DownloadSourceWebsiteRequest extends DownloadBaseRequest {

    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"sourcesite\",\"latestReferrer\",\"pv\",\"pvRate\",\"visitCount\",\"uv\",\"newUv\",\"newUvRate\",\"ipCount\",\"bounceRate\",\"avgVisitTime\",\"avgPv\"]")
    private List<String> cols;
}
