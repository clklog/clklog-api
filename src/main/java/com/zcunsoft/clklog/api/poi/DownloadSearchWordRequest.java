package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载站外搜索词的请求")
@Data
public class DownloadSearchWordRequest extends DownloadBaseRequest {

    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"searchword\",\"pv\",\"pvRate\",\"visitCount\",\"visitCountRate\",\"uv\",\"newUv\",\"ipCount\",\"ipCountRate\",\"bounceRate\",\"avgVisitTime\",\"avgPv\"]")
    private List<String> cols;
}
