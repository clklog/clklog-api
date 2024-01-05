package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "受访页面详情")
@Data
public class VisitUriExitDetail {
    @Schema(description = "页面")
    private String uri;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "退出页次数")
    private int exitCount;

//    @Schema(description = "平均停留时长")
//    private float avgDuration;

    @Schema(description = "退出率")
    private float exitRate;

    
    @Schema(description = "平均访问时长")
    private float avgVisitTime;
    
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "路径")
    private String uriPath;
    
    @Schema(description = "浏览量占比")
    private float pvRate;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "新访客数")
    private int newUv;

    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "跳出率")
    private float bounceRate;
    
    @Schema(description = "新访客比率")
    private float newUvRate;
}
