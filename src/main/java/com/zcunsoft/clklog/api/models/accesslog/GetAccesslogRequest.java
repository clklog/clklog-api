package com.zcunsoft.clklog.api.models.accesslog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "测试日志")
@Data
public class GetAccesslogRequest {

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String projectName;
}
