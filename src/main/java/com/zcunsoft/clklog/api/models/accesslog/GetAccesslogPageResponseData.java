package com.zcunsoft.clklog.api.models.accesslog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Schema(description = "日志详情的响应")
@Data
public class GetAccesslogPageResponseData {

	private int total;
	
    private List<AccesslogFlowDetail> rows;
}