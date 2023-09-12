package com.zcunsoft.clklog.api.controllers;

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

   
    @Operation(summary = "分页测试")
    @RequestMapping(path = "/getAccesslogPageTest", method = RequestMethod.POST)
    public GetAccesslogPageResponse getAccesslogPageTest(@RequestBody GetAccesslogPageRequest getAccesslogPageRequest, HttpServletRequest request) {
        return accesslogIReportService.getAccesslogPageTest(getAccesslogPageRequest);
    }
    
    @Operation(summary = "测试不分页")
    @RequestMapping(path = "/getAccesslogTest", method = RequestMethod.POST)
    public GetAccesslogResponse getAccesslogTest(@RequestBody GetAccesslogRequest getAccesslogRequest, HttpServletRequest request) {
        return accesslogIReportService.getAccesslogTest(getAccesslogRequest);
    }
}
