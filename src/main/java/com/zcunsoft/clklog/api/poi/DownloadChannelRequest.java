package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "下载渠道分析的请求")
@Data
public class DownloadChannelRequest extends DownloadBaseRequest {

    
    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"channel\",\"pv\",\"pvRate\",\"visitCount\",\"visitCountRate\",\"uv\",\"uvRate\",\"newUv\",\"newUvRate\",\"ipCount\",\"ipCountRate\",\"bounceRate\",\"avgVisitTime\",\"visitTimeRate\",\"avgPv\"]")
    private List<String> cols;
}
