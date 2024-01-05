package com.zcunsoft.clklog.api.controllers;

import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.os.GetOsDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponse;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.uservisit.BaseUserVisit;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorDetailResponse;
import com.zcunsoft.clklog.api.poi.DownloadAreaRequest;
import com.zcunsoft.clklog.api.poi.DownloadBaseRequest;
import com.zcunsoft.clklog.api.poi.DownloadChannelRequest;
import com.zcunsoft.clklog.api.poi.DownloadDeviceRequest;
import com.zcunsoft.clklog.api.poi.DownloadFlowTrendRequest;
import com.zcunsoft.clklog.api.poi.DownloadOsRequest;
import com.zcunsoft.clklog.api.poi.DownloadRequest;
import com.zcunsoft.clklog.api.poi.DownloadSearchWordRequest;
import com.zcunsoft.clklog.api.poi.DownloadSourceWebsiteRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitUriDownpvRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitUriEntryRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitUriExitRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitorListRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitorRequest;
import com.zcunsoft.clklog.api.poi.ExcelExportUtils;
import com.zcunsoft.clklog.api.services.IExportReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(path = "download")
@Tag(name = "下载", description = "下载")
public class DownloadController {

    @Resource
    IExportReportService exportReportService;
    
    @Operation(summary = "下载流量趋势分析")
    @RequestMapping(path = "/exportFlowTrendDetail", method = RequestMethod.POST)
    public void exportFlowTrendDetail(@RequestBody DownloadFlowTrendRequest downloadFlowTrendRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetFlowTrendDetailResponse getFlowTrendDetailResponse = exportReportService.getFlowTrendDetail(downloadFlowTrendRequest);
    	try {
    		ExcelExportUtils.exportFlowTrendDetail(getFlowTrendDetailResponse.getData().getDetail(),getFlowTrendDetailResponse.getData().getTotal(),downloadFlowTrendRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载搜索词分析")
    @RequestMapping(path = "/exportSearchWordDetail", method = RequestMethod.POST)
    public void exportSearchWordDetail(@RequestBody DownloadSearchWordRequest downloadSearchWordRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetSearchWordDetailResponse getSearchWordDetailResponse = exportReportService.getSearchWordDetail(downloadSearchWordRequest);
    	try {
    		ExcelExportUtils.exportSearchWordDetail(getSearchWordDetailResponse.getData().getRows(),downloadSearchWordRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载地域分析")
    @RequestMapping(path = "/exportAreaDetail", method = RequestMethod.POST)
    public void exportAreaDetail(@RequestBody DownloadAreaRequest downloadAreaRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> areaDetailList = exportReportService.getAreaDetailList(downloadAreaRequest);
    	FlowDetail totalAreaDetail = exportReportService.getAreaDetailTotal(downloadAreaRequest);
    	try {
    		ExcelExportUtils.exportAreaDetail(areaDetailList,totalAreaDetail,downloadAreaRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载受访页面分析")
    @RequestMapping(path = "/exportVisitUriDetail", method = RequestMethod.POST)
    public void exportVisitUriDetail(@RequestBody DownloadRequest downloadRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> dataList = exportReportService.getVisitUriDetailList(downloadRequest);
    	FlowDetail totalData = exportReportService.getVisitUriTotal(downloadRequest);
    	try {
    		ExcelExportUtils.exportVisitUriDetail(dataList,totalData,downloadRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载受访页面-入口页分析")
    @RequestMapping(path = "/exportVisitUriEntryDetail", method = RequestMethod.POST)
    public void exportVisitUriEntryDetail(@RequestBody DownloadVisitUriEntryRequest downloadVisitUriEntryRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> dataList = exportReportService.getVisitUriEntryDetailList(downloadVisitUriEntryRequest);
    	FlowDetail totalData = exportReportService.getVisitUriEntryTotal(downloadVisitUriEntryRequest);
    	try {
    		ExcelExportUtils.exportVisitUriEntryDetail(dataList,totalData,downloadVisitUriEntryRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载受访页面-退出页页分析")
    @RequestMapping(path = "/exportVisitUriExitDetail", method = RequestMethod.POST)
    public void exportVisitUriExitDetail(@RequestBody DownloadVisitUriExitRequest downloadVisitUriExitRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> dataList = exportReportService.getVisitUriExitDetailList(downloadVisitUriExitRequest);
    	FlowDetail totalData = exportReportService.getVisitUriExitTotal(downloadVisitUriExitRequest);
    	try {
    		ExcelExportUtils.exportVisitUriExitDetail(dataList,totalData,downloadVisitUriExitRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载受访页面-贡献下游浏览量页分析")
    @RequestMapping(path = "/exportVisitUriDownpvDetail", method = RequestMethod.POST)
    public void exportVisitUriDownpvDetail(@RequestBody DownloadVisitUriDownpvRequest downloadVisitUriDownpvRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> dataList = exportReportService.getVisitUriDownpvDetailList(downloadVisitUriDownpvRequest);
    	FlowDetail totalData = exportReportService.getVisitUriDownpvTotal(downloadVisitUriDownpvRequest);
    	try {
    		ExcelExportUtils.exportVisitUriDownpvDetail(dataList,totalData,downloadVisitUriDownpvRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载来源网站分析")
    @RequestMapping(path = "/exportSourceWebsiteDetail", method = RequestMethod.POST)
    public void exportSourceWebSiteDetail(@RequestBody DownloadSourceWebsiteRequest downloadSourceWebsiteRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetSourceWebsiteDetailPageResponse getSourceWebsiteDetailPageResponse = exportReportService.getSourceWebSiteDetail(downloadSourceWebsiteRequest);
    	try {
    		ExcelExportUtils.exportSourceWebSiteDetail(getSourceWebsiteDetailPageResponse.getData().getRows(),getSourceWebsiteDetailPageResponse.getData().getSummary(),downloadSourceWebsiteRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载新老访客分析")
    @RequestMapping(path = "/exportVisitorDetail", method = RequestMethod.POST)
    public void exportVisitorDetail(@RequestBody DownloadVisitorRequest downloadVisitorRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetVisitorDetailResponse getVisitorDetailResponse = exportReportService.getVisitorDetail(downloadVisitorRequest);
    	FlowDetail totalData = exportReportService.getVisitorTotal(downloadVisitorRequest.getChannel(),downloadVisitorRequest.getStartTime(),downloadVisitorRequest.getEndTime(),downloadVisitorRequest.getProjectName(),downloadVisitorRequest.getCountry(),downloadVisitorRequest.getProvince(),downloadVisitorRequest.getVisitorType());
    	try {
    		ExcelExportUtils.exportVisitorDetail(getVisitorDetailResponse.getData(),totalData,downloadVisitorRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载设备分析")
    @RequestMapping(path = "/exportDeviceDetail", method = RequestMethod.POST)
    public void exportDeviceDetail(@RequestBody DownloadDeviceRequest downloadDeviceRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetDeviceDetailResponse getDeviceDetailResponse = exportReportService.getDeviceDetail(downloadDeviceRequest);
    	try {
    		ExcelExportUtils.exportDeviceDetail(getDeviceDetailResponse.getData(),downloadDeviceRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载渠道分析")
    @RequestMapping(path = "/exportChannelDetail", method = RequestMethod.POST)
    public void exportChannelDetail(@RequestBody DownloadChannelRequest downloadChannelRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetChannelDetailResponse getChannelDetailResponse = exportReportService.getChannelDetail(downloadChannelRequest);
    	try {
    		ExcelExportUtils.exportChannelDetail(getChannelDetailResponse.getData(),downloadChannelRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载用户行为分析")
    @RequestMapping(path = "/exportVisitorList", method = RequestMethod.POST)
    public void exportVisitorList(@RequestBody DownloadVisitorListRequest downloadVisitorListRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<FlowDetail> detailList =  exportReportService.getVisitorList(downloadVisitorListRequest);
    	FlowDetail total = exportReportService.getVisitorTotal(downloadVisitorListRequest.getChannel(),downloadVisitorListRequest.getStartTime(),downloadVisitorListRequest.getEndTime(),downloadVisitorListRequest.getProjectName(),downloadVisitorListRequest.getCountry(),downloadVisitorListRequest.getProvince(),downloadVisitorListRequest.getVisitorType());
    	try {
    		ExcelExportUtils.exportVisitorList(detailList,total, downloadVisitorListRequest.getCols(),request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Operation(summary = "下载用户忠诚度分析")
    @RequestMapping(path = "/exportVisitor", method = RequestMethod.POST)
    public void exportVisitor(@RequestBody DownloadBaseRequest downloadBaseRequest, HttpServletRequest request,HttpServletResponse respones) {
    	List<BaseUserVisit> pvList =  exportReportService.getUserPv(downloadBaseRequest);
    	List<BaseUserVisit> visitList =  exportReportService.getUserVisit(downloadBaseRequest);
    	List<BaseUserVisit> visitTimeList =  exportReportService.getUserVisitTime(downloadBaseRequest);
    	List<BaseUserVisit> visitUriList = exportReportService.getUserVisitUri(downloadBaseRequest);
    	List<BaseUserVisit> latestTimeList = exportReportService.getUserLatestTime(downloadBaseRequest);
    	try {
    		ExcelExportUtils.exportVisitorList1(pvList,visitList,visitTimeList,visitUriList,latestTimeList,request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Operation(summary = "下载操作系统分析")
    @RequestMapping(path = "/exportOsDetail", method = RequestMethod.POST)
    public void exportOsDetail(@RequestBody DownloadOsRequest downloadOsRequest, HttpServletRequest request,HttpServletResponse respones) {
    	GetOsDetailResponse getOsDetailResponse = exportReportService.getOsDetail(downloadOsRequest);
    	try {
    		ExcelExportUtils.exportOsDetail(getOsDetailResponse.getData(),downloadOsRequest.getCols(), request,respones);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
