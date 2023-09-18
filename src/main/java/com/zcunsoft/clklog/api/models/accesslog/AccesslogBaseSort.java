/**
 * 
 */
package com.zcunsoft.clklog.api.models.accesslog;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "排序")
@Data
public class AccesslogBaseSort  {
	 @Schema(description = "属性", requiredMode = Schema.RequiredMode.REQUIRED, example = "pv,ipCount,logTime,requestLength,bodyBytesSent,ipCount,slowPv,maxVisitTime,avgVisitTime,uriCount,latestTime")
	 private String sortName;
	 
	 @Schema(description = "顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "desc,asc")
	 private String sortOrder;
}
