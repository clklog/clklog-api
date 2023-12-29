package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载访客列表分析的请求")
@Data
public class DownloadVisitorListRequest extends DownloadBaseRequest {

    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"distinctId\",\"visitorType\",\"pv\",\"visitCount\",\"visitTime\",\"avgPv\",\"latestTime\"]")
    private List<String> cols;
}
