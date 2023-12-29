package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
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
import com.zcunsoft.clklog.api.poi.DownloadRequest;
import com.zcunsoft.clklog.api.poi.DownloadSearchWordRequest;
import com.zcunsoft.clklog.api.poi.DownloadSourceWebsiteRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitorListRequest;
import com.zcunsoft.clklog.api.poi.DownloadVisitorRequest;

import java.util.List;

public interface IExportReportService {

    
	/**
	 * 获取流量趋势分析数据
	 * @param downloadFlowTrendRequest
	 * @return
	 */
    GetFlowTrendDetailResponse getFlowTrendDetail(DownloadFlowTrendRequest downloadFlowTrendRequest);

    /**
     * 获取站外搜索词分析数据
     * @param downloadSearchWordRequest
     * @return
     */
    GetSearchWordDetailResponse getSearchWordDetail(DownloadSearchWordRequest downloadSearchWordRequest);
    
    /**
     * 获取地域分析数据
     * @param downloadAreaRequest
     * @return
     */
    List<FlowDetail> getAreaDetailList(DownloadAreaRequest downloadAreaRequest);
    
    /**
     * 获取地域统计分析数据
     * @param downloadAreaRequest
     * @return
     */
    FlowDetail getAreaDetailTotal(DownloadAreaRequest downloadAreaRequest);
    
  //获取受访页面统计
    FlowDetail getVisitUriTotal(DownloadRequest downloadRequest);
    
    //获取受访页面列表
    List<FlowDetail> getVisitUriDetailList(DownloadRequest downloadRequest);
    
    /**
     * 获取来源网站分析数据
     * @param downloadSourceWebsiteRequest
     * @return
     */
    GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(DownloadSourceWebsiteRequest downloadSourceWebsiteRequest);

    /**
     * 获取新老访客统计数据
     * @param downloadVisitorRequest
     * @return
     */
    FlowDetail getVisitorTotal(DownloadRequest downloadRequest);
    
    /**
     * 获取新老访客统计数据
     * @param channel 渠道
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param projectName 项目名称
     * @param country 国家
     * @param province 省份
     * @param visitorType 访客类型
     * @return
     */
    FlowDetail getVisitorTotal(List<String> channel,String startTime,String endTime,String projectName,List<String> country,List<String> province,String visitorType);
    
    /**
     * 获取新老访客分析数据
     * @param downloadVisitorRequest
     * @return
     */
    GetVisitorDetailResponse getVisitorDetail(DownloadVisitorRequest downloadVisitorRequest);
    
    /**
     * 获取设备分析数据
     * @param downloadRequest
     * @return
     */
    GetDeviceDetailResponse getDeviceDetail(DownloadDeviceRequest downloadDeviceRequest);

    /**
     * 获取渠道分析数据
     * @param downloadChannelRequest
     * @return
     */
    GetChannelDetailResponse getChannelDetail(DownloadChannelRequest downloadChannelRequest);
    
    /**
     * 获取访客列表分析数据
     * @param downloadVisitorListRequest
     * @return
     */
    List<FlowDetail> getVisitorList(DownloadVisitorListRequest downloadVisitorListRequest);
    
    /**
     * 获取用户忠诚度-访问时长分析数据
     * @param downloadBaseRequest
     * @return
     */
    List<BaseUserVisit> getUserVisitTime(DownloadBaseRequest downloadBaseRequest);
    
    /**
     * 获取用户忠诚度-访问页数分析数据
     * @param downloadBaseRequest
     * @return
     */
    List<BaseUserVisit> getUserVisit(DownloadBaseRequest downloadBaseRequest);
    
    /**
     * 获取用户忠诚度-访问次数分析数据
     * @param downloadBaseRequest
     * @return
     */
    List<BaseUserVisit> getUserPv(DownloadBaseRequest downloadBaseRequest);
}
