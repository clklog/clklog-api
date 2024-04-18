package com.zcunsoft.clklog.api.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zcunsoft.clklog.api.models.ResponseBase;
import com.zcunsoft.clklog.api.models.os.GetOsDetailRequest;
import com.zcunsoft.clklog.api.models.os.GetOsDetailResponse;
import com.zcunsoft.clklog.api.services.IReportService;
import com.zcunsoft.clklog.api.services.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "profile")
@Tag(name = "profile查询", description = "profile查询")
public class ProfileController {

	@Resource
    private ProfileService profileService;

    @Operation(summary = "获取服务的id")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseBase<String> getOsDetail() {
        return new ResponseBase<>(200, "获取成功", profileService.loadKey());
    }
}
