package com.zcunsoft.clklog.api.models.accesslog;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "应用流量信息")
@Data
public class AccesslogFlowDetail {
    @Schema(description = "主机")
    private String host;
    
    @Schema(description = "URL")
    private String uri;
    
    @Schema(description = "日志总数(PV)")
    private BigDecimal pv;
    
    @Schema(description = "超过1秒次数")
    private BigDecimal slowPv;

    @Schema(description = "流入流量")
    private BigDecimal requestLength;

    @Schema(description = "流出流量")
    private BigDecimal bodyBytesSent;

    @Schema(description = "访问IP数")
    private BigDecimal ipCount;

    @Schema(description = "访问时长")
    private BigDecimal visitTime;
    
    @Schema(description = "最大访问时长")
    private BigDecimal maxVisitTime;
    
    @Schema(description = "平均访问时长")
    private BigDecimal avgVisitTime;
    
    @Schema(description = "日志占比")
    private BigDecimal pvRate;
    
    @Schema(description = "流入流量占比")
    private BigDecimal requestLengthRate;

    @Schema(description = "流出流量占比")
    private BigDecimal bodyBytesSentRate;
    
    @Schema(description = "访问IP数占比")
    private BigDecimal ipCountRate;
    
}
