package com.zcunsoft.clklog.api.models.trend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "获取流量近n分钟实时统计请求")
@Data
public class GetFlowPreviousMinTotalRequest {

    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"安卓\",\"苹果\",\"网站\",\"微信小程序\"]")
    private List<String> channel = new ArrayList<>();

    @Schema(description = "访客类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "新访客/老访客")
    private String visitorType;

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String projectName;
    
    @Schema(description = "近n分钟", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer previousMin;
}
