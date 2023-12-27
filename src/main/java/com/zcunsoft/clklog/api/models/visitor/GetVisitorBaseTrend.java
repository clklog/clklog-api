package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "用户周期信息")
@Data
public class GetVisitorBaseTrend {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "统计时间")
    private String statTime;

    @Schema(description = "新用户")
    private Integer newCount;
    
    @Schema(description = "老用户")
    private Integer historyCount;

    @Schema(description = "用户数")
    private Integer uvCount;
    
    @Schema(description = "回流访客")
    private Integer revisitCount;


    @Schema(description = "沉默访客")
    private Integer silentCount;

    
    @Schema(description = "活跃用户")
    private Integer activeCount;
}
