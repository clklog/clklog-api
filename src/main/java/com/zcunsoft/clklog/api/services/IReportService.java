package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.models.area.GetAreaDetailCityRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailCityResponse;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailComparePageRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailComparePageResponse;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailPageRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailPageResponse;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailRequest;
import com.zcunsoft.clklog.api.models.area.GetAreaDetailTotalResponse;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailRequest;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailPageResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailRequest;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.os.GetOsDetailRequest;
import com.zcunsoft.clklog.api.models.os.GetOsDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailRequest;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.*;
import com.zcunsoft.clklog.api.models.summary.*;
import com.zcunsoft.clklog.api.models.trend.GetFlowDetailResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowPreviousMinTotalRequest;
import com.zcunsoft.clklog.api.models.trend.GetFlowTotalResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailCompareRequest;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailCompareResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailRequest;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserPvbydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserLatestTimebydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitRequest;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitTimebydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitUribydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitbydateResponse;
import com.zcunsoft.clklog.api.models.visitor.*;
import com.zcunsoft.clklog.api.models.visituri.*;

import java.sql.Timestamp;

public interface IReportService {

    GetFlowResponse getFlow(GetFlowRequest getFlowRequest);

    GetFlowTrendResponse getFlowTrend(GetFlowTrendRequest getFlowTrendRequest);

    GetVisitorSummaryResponse getVisitor(GetVisitorSummaryRequest getVisitorRequest);

    GetVisitUriResponse getVisitUri(GetVisitUriRequest getVisitUriRequest);
    
    //获取受访页面统计
    GetVisitUriTotalResponse getVisitUriTotal(GetVisitUriDetailRequest getVisitUriDetailRequest);
    
    //获取受访页面列表
    GetVisitUriDetailPageResponse getVisitUriDetailList(GetVisitUriDetailPageRequest getVisitUriDetailPageRequest);

    GetSearchWordResponse getSearchWord(GetSearchWordRequest getSearchWordRequest);

    GetSourceWebsiteResponse getSourceWebsite(GetSourceWebsiteRequest getSourceWebsiteRequest);

    GetAreaResponse getArea(GetAreaRequest getAreaRequest);
    
    GetAreaDetailPageResponse getAreaDetailList(GetAreaDetailPageRequest getAreaDetailPageRequest);
    
    GetAreaDetailPageResponse getCountryDetailList(GetAreaDetailPageRequest getAreaDetailPageRequest);
    
    GetAreaDetailComparePageResponse getCountryDetailListByCompare(GetAreaDetailComparePageRequest getAreaDetailComparePageRequest);
    
    GetAreaDetailComparePageResponse getProvinceDetailListByCompare(GetAreaDetailComparePageRequest getAreaDetailComparePageRequest);
    
    GetAreaDetailCityResponse getAreaDetailCityList(GetAreaDetailCityRequest getAreaDetailCityRequest);
    
    GetAreaDetailTotalResponse getAreaDetailTotal(GetAreaDetailRequest getAreaDetailRequest);

    GetAreaResponse getAreaDetailTop10(GetAreaDetailRequest getAreaDetailRequest);

    GetFlowTrendDetailResponse getFlowTrendDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest);

    GetSearchWordDetailResponse getSearchWordDetail(GetSearchWordDetailRequest getSearchWordDetailRequest);

    GetChannelDetailResponse getChannelDetail(GetChannelDetailRequest getChannelDetailRequest);

    GetDeviceDetailResponse getDeviceDetail(GetDeviceDetailRequest getDeviceDetailRequest);

    GetSourceWebsiteDetailResponse getSourceSiteTop10(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest);

    GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(GetSourceWebsiteDetailPageRequest getSourceWebsiteDetailPageRequest);

    GetVisitorTotalResponse getVisitorTotal(GetVisitorDetailRequest getVisitorDetailRequest);
    
    GetVisitorDetailResponse getVisitorDetail(GetVisitorDetailRequest getVisitorDetailRequest);
    
    //获取访客渠道指标
    GetVisitorChannelResponse getVisitorChannel(GetVisitorChannelRequest getVisitorChannelRequest);
    
    //获取用户列表
    GetVisitorListPageResponse getVisitorList(GetVisitorListPageRequest getVisitorListPageRequest);
    
    //获取用户详情访问列表
    GetVisitorSessionListPageResponse getGetVisitorSessionList(GetVisitorSessionListPageRequest getVisitorSessionListPageRequest);
    
    //获取用户详情访问页面列表
    GetVisitorSessionUriListPageResponse getGetVisitorSessionUriList(GetVisitorSessionUriListPageRequest getVisitorSessionUriListPageRequest);
    
   //获取用户详情基本信息
    GetVisitorDetailinfoResponse getVisitorDetailinfo(GetVisitorDetailinfoRequest getVisitorDetailinfoRequest);
    
    GetUserVisitTimebydateResponse getUserVisitTime(GetUserVisitRequest getUserVisitRequest);
    
    GetUserVisitbydateResponse getUserVisit(GetUserVisitRequest getUserVisitRequest);
    
    GetUserPvbydateResponse getUserPv(GetUserVisitRequest getUserVisitRequest);
    
    GetUserLatestTimebydateResponse getUserLatestTime(GetUserVisitRequest getUserVisitRequest);

    Timestamp getProjectNameStartStatDate();

    GetSourceWebsiteTop10Response getSourceWebSiteTop10(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest);

    GetSourceWebsiteTotalResponse getSourceWebSiteTotal(GetSourceWebsiteDetailRequest getSourceWebsiteDetailRequest);

    GetDeviceDetailPageResponse getDeviceDetailPageList(GetDeviceDetailPageRequest getDeviceDetailPageRequest);

    GetFlowTotalResponse getFlowTotal(GetFlowTrendDetailRequest getFlowTrendDetailRequest);

    GetFlowDetailResponse getFlowDetail(GetFlowTrendDetailRequest getFlowTrendDetailRequest);
    
    GetFlowDetailResponse getFlowDetailByPreviousHour(GetFlowTrendDetailRequest getFlowTrendDetailRequest);

    GetVisitUriPathTreeTotalResponse getVisitUriPathTreeTotal(GetVisitUriDetailRequest getVisitUriDetailRequest);

    GetVisitUriListOfUriPathResponse getVisitUriListOfUriPath(GetVisitUriListOfUriPathRequest getVisitUriListOfUriPathRequest);
    
    GetFlowTrendDetailCompareResponse getFlowDetailByCompare(GetFlowTrendDetailCompareRequest getFlowTrendDetailCompareRequest);
    
    GetLogAnalysisListPageResponse getLogAnalysisList(GetLogAnalysisListPageRequest getLogAnalysisListPageRequest);
    
    /**
     * 操作系统分析
     * @param getOsDetailRequest
     * @return
     */
    GetOsDetailResponse getOsDetail(GetOsDetailRequest getOsDetailRequest);
    
    /**
     * 获取活跃用户周期日-周-月趋势
     * @param getVisitorActiveTrend
     * @return
     */
    GetVisitorBaseTrendResponse getVisitorActiveTrend(GetVisitorBaseTrendRequest getVisitorBaseTrendRequest);
    
    /**
     * 获取活跃用户周期日-周-月趋势
     * @param getVisitorRevisitAndSilentTrend
     * @return
     */
    GetVisitorBaseTrendResponse getVisitorRevisitAndSilentTrend(GetVisitorBaseTrendRequest getVisitorBaseTrendRequest);
    
    /**
     * 获取流失/留存周期日-周-月趋势
     * @param getVisitorChunrAndRemainTrend
     * @return
     */
    GetVisitorBaseChurnAndRemainTrendResponse getVisitorChunrAndRemainTrend(GetVisitorBaseTrendRequest getVisitorBaseTrendRequest);
    
    /**
     * 获取退出页页面列表
     * @param getVisitUriDetailPageRequest
     * @return
     */
    GetVisitUriExitDetailPageResponse getVisitUriExitDetail(GetVisitUriDetailPageRequest getVisitUriDetailPageRequest);
    
    /**
     * 获取入口页页面列表
     * @param getVisitUriDetailPageRequest
     * @return
     */
    GetVisitUriEntryDetailPageResponse getVisitUriEntryDetail(GetVisitUriDetailPageRequest getVisitUriDetailPageRequest);
    
    /**
     * 获取贡献下游浏览量页面列表
     * @param getVisitUriDetailPageRequest
     * @return
     */
    GetVisitUriDownpvDetailPageResponse getVisitUriDownpvDetail(GetVisitUriDetailPageRequest getVisitUriDetailPageRequest);

    /**
     * 获取近n分钟流量统计
     * @param getFlowPreviousMinTotalRequest
     * @return
     */
    GetFlowTotalResponse getFlowPreviousMinTotal(GetFlowPreviousMinTotalRequest getFlowPreviousMinTotalRequest);
    
    /**
     * 获取访问深度
     * @param getUserVisitRequest
     * @return
     */
    GetUserVisitUribydateResponse getUserVisitUri(GetUserVisitRequest getUserVisitRequest);
    
    /**
     * 获取单个受访页面访问统计数据
     * @param getVisitUriTrendRequest
     * @return
     */
    GetVisitUriTotalResponse getVisitUriDetailTotal(GetVisitUriDetailTotalRequest getVisitUriTrendRequest);
    
  	/**
  	 * 获取单个受访页面访问趋势数据
  	 * @param getVisitUriDetailPageRequest
  	 * @return
  	 */
    GetVisitUriDetailTrendResponse getVisitUriDetailTrend(GetVisitUriDetailTrendRequest getVisitUriTrendDetailRequest);
}
