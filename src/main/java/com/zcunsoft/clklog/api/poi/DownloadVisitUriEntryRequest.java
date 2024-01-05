package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载受访页面入口页的请求")
@Data
public class DownloadVisitUriEntryRequest extends DownloadBaseRequest {
	
	@Schema(description = "路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String uriPath;

    @Schema(description = "是否模糊搜索路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "true:模糊搜索、false:精准搜索")
    private boolean needFuzzySearchUriPath;
    
    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"uri\",\"pv\",\"pvRate\",\"visitCount\",\"uv\",\"newUv\",\"newUvRate\",\"ipCount\",\"bounceRate\",\"title\",\"avgVisitTime\",\"avgPv\",\"entryCount\",\"entryRate\"]")
    private List<String> cols;
}
