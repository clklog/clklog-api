package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogHostResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogOverviewResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageRequest;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogRequest;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogResponse;


public interface AccesslogIReportService {

	GetAccesslogPageResponse getAccesslogPageTest(GetAccesslogPageRequest getAccesslogPageRequest);

	GetAccesslogResponse getAccesslogTest(GetAccesslogRequest getAccesslogRequest);
	
	GetAccesslogHostResponse getHost();
	
	GetAccesslogResponse getHostOverview(GetAccesslogRequest getAccesslogRequest);
	
	GetAccesslogOverviewResponse getOverview(GetAccesslogRequest getAccesslogRequest);
	
	GetAccesslogPageResponse getPerformance(GetAccesslogPageRequest getAccesslogPageRequest);
}
