package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "受访页面路径统计详情")
public class VisitUriTreeStatData {
    @Schema(description = "页面路径")
    private String uri;
    @Schema(description = "域名")
    private String host;
    @Schema(description = "相对路径")
    private String path;
    @Schema(description = "页面路径最后地址段")
    private String segment;
    @Schema(description = "子页面路径统计详情")
    private List<VisitUriTreeStatData> leafUri;

    @Schema(description = "统计信息")
    private VisitUriPathDetail detail;
}
