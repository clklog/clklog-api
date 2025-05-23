package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.channel.GetChannelDetailRequest;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.channel.GetChannelRequest;
import com.zcunsoft.clklog.api.models.channel.GetChannelResponse;
import com.zcunsoft.clklog.api.services.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "channel")
@Tag(name = "渠道分析", description = "渠道分析")
public class ChannelController {

    @Resource
    IReportService reportService;



    @Operation(summary = "获取渠道访问统计数据")
    @RequestMapping(path = "/getChannelDetail", method = RequestMethod.POST)
    public GetChannelDetailResponse getChannelDetail(@RequestBody GetChannelDetailRequest getChannelDetailRequest, HttpServletRequest request) {
        return reportService.getChannelDetail(getChannelDetailRequest);
    }


    @Operation(summary = "获取项目渠道列表")
    @RequestMapping(path = "/getChannelList", method = RequestMethod.POST)
    public GetChannelResponse getChannelList(@Valid @RequestBody GetChannelRequest getChannelRequest, HttpServletRequest request) {
        return reportService.getChannelList(getChannelRequest);
    }
}
