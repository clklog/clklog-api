package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "下载设备分析的请求")
@Data
public class DownloadDeviceRequest extends DownloadBaseRequest {

    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"device\",\"pv\",\"pvRate\",\"visitCount\",\"uv\",\"newUv\",\"newUvRate\",\"ipCount\",\"bounceRate\",\"avgVisitTime\",\"avgPv\"]")
    private List<String> cols;
}
