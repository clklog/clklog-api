package com.zcunsoft.clklog.api.models.accesslog;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "应用流量信息")
@Data
public class AccesslogFlowDetail {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "统计时间")
    private String statTime;
	
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
    
    @Schema(description = "状态码")
	private String status;
	
    @Schema(description = "uri数")
	private BigDecimal uriCount;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "上次访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp latestTime;
    
    @Schema(description = "IP")
	private String ip;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp logTime;
    
    @Schema(description = "来源网站")
    private String httpReferer;
    
}
