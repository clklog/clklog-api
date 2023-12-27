package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "分页获取受访页面详情的响应")
@Data
public class GetVisitUriDownpvDetailPageResponseData {

	private int total;
	
    private List<VisitUriDownpvDetail> rows;
}