package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "用户周期信息")
@Data
public class GetVisitorBaseChurnAndRemainData {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "统计时间")
    private String statTime;

    @Schema(description = "新用户")
    private int newCount;
    
//    @Schema(description = "老用户")
//    private Integer historyCount;

    @Schema(description = "活跃用户数")
    private int activeCount;
    
    @Schema(description = "用户数")
    private int uvCount;
    
    @Schema(description = "流失用户")
    private int churnCount;
    
    @Schema(description = "留存用户")
    private int remainCount;
    
    @Schema(description = "流失率")
    private float churnRate;
    
    @Schema(description = "留存率")
    private float remainRate;
}
