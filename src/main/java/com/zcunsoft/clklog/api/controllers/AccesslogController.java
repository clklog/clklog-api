package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogHostResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogOverviewResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageRequest;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogRequest;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogResponse;
import com.zcunsoft.clklog.api.services.AccesslogIReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "accesslog")
@Tag(name = "日志分析", description = "日志分析")
public class AccesslogController {

    @Resource
    AccesslogIReportService accesslogIReportService;

   
//    @Operation(summary = "分页测试")
//    @RequestMapping(path = "/getAccesslogPageTest", method = RequestMethod.POST)
//    public GetAccesslogPageResponse getAccesslogPageTest(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest, HttpServletRequest request) {
//        return accesslogIReportService.getAccesslogPageTest(getAccesslogPageRequest);
//    }
//    
//    @Operation(summary = "测试不分页")
//    @RequestMapping(path = "/getAccesslogTest", method = RequestMethod.POST)
//    public GetAccesslogResponse getAccesslogTest(@RequestBody GetAccesslogRequest getAccesslogRequest, HttpServletRequest request) {
//        return accesslogIReportService.getAccesslogTest(getAccesslogRequest);
//    }
    
    @Operation(summary = "获取应用列表")
    @RequestMapping(path = "/getHost", method = RequestMethod.POST)
    public GetAccesslogHostResponse getHost(HttpServletRequest request) {
        return accesslogIReportService.getHost();
    }
    
    @Operation(summary = "指标概览")
    @RequestMapping(path = "/getOverview", method = RequestMethod.POST)
    public GetAccesslogOverviewResponse getOverview(@RequestBody GetAccesslogRequest getAccesslogRequest,HttpServletRequest request) {
        return accesslogIReportService.getOverview(getAccesslogRequest);
    }
    
    @Operation(summary = "按应用流量指标概览")
    @RequestMapping(path = "/getHostOverview", method = RequestMethod.POST)
    public GetAccesslogResponse getHostOverview(@RequestBody GetAccesslogRequest getAccesslogRequest,HttpServletRequest request) {
        return accesslogIReportService.getHostOverview(getAccesslogRequest);
    }
    
    @Operation(summary = "性能分析")
    @RequestMapping(path = "/getPerformanceDetail", method = RequestMethod.POST)
    public GetAccesslogPageResponse getPerformanceDetail(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest,HttpServletRequest request) {
        return accesslogIReportService.getPerformanceDetail(getAccesslogPageRequest);
    }
    
    @Operation(summary = "状态码分析")
    @RequestMapping(path = "/getStatus", method = RequestMethod.POST)
    public GetAccesslogResponse getStatus(@RequestBody GetAccesslogRequest getAccesslogRequest,HttpServletRequest request) {
        return accesslogIReportService.getStatus(getAccesslogRequest);
    }
    
    @Operation(summary = "状态码详情")
    @RequestMapping(path = "/getStatusDetail", method = RequestMethod.POST)
    public GetAccesslogPageResponse getStatusDetail(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest,HttpServletRequest request) {
        return accesslogIReportService.getStatusDetail(getAccesslogPageRequest);
    }
    
    @Operation(summary = "IP分析")
    @RequestMapping(path = "/getIpDetail", method = RequestMethod.POST)
    public GetAccesslogPageResponse getIpDetail(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest,HttpServletRequest request) {
        return accesslogIReportService.getIpDetail(getAccesslogPageRequest);
    }
    
    @Operation(summary = "根据IP获取URI信息")
    @RequestMapping(path = "/getUriDetailByIp", method = RequestMethod.POST)
    public GetAccesslogPageResponse getUriDetailByIp(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest,HttpServletRequest request) {
        return accesslogIReportService.getUriDetailByIp(getAccesslogPageRequest);
    }
    
    @Operation(summary = "获取来源网站信息")
    @RequestMapping(path = "/getSourceWebsiteDetail", method = RequestMethod.POST)
    public GetAccesslogPageResponse getSourceWebsiteDetail(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest,HttpServletRequest request) {
        return accesslogIReportService.getSourceWebsiteDetail(getAccesslogPageRequest);
    }
}
