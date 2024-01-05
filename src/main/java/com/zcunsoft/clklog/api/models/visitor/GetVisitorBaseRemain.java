package com.zcunsoft.clklog.api.models.visitor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "用户周期信息")
@Data
public class GetVisitorBaseRemain {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "统计时间")
    private String statTime;
    
    @Schema(description = "原始用户")
    private int rawUvCount;
    
    @Schema(description = "原始日期")
    private String rawStatTime;

    @Schema(description = "详情")
    List<GetVisitorBaseRemainData> rows;
}
