package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.cfg.ClklogApiSetting;
import com.zcunsoft.clklog.api.constant.Constants;
import com.zcunsoft.clklog.api.entity.clickhouse.*;
import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;
import com.zcunsoft.clklog.api.models.TimeFrame;
import com.zcunsoft.clklog.api.models.channel.GetChannelDetailResponse;
import com.zcunsoft.clklog.api.models.device.GetDeviceDetailResponse;
import com.zcunsoft.clklog.api.models.enums.LibType;
import com.zcunsoft.clklog.api.models.enums.SortType;
import com.zcunsoft.clklog.api.models.enums.VisitorType;
import com.zcunsoft.clklog.api.models.os.GetOsDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponse;
import com.zcunsoft.clklog.api.models.searchword.GetSearchWordDetailResponseData;
import com.zcunsoft.clklog.api.models.searchword.SearchWordDetail;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponse;
import com.zcunsoft.clklog.api.models.sourcewebsite.GetSourceWebsiteDetailPageResponseData;
import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.FlowSummary;
import com.zcunsoft.clklog.api.models.summary.GetFlowRequest;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponse;
import com.zcunsoft.clklog.api.models.trend.GetFlowTrendDetailResponseData;
import com.zcunsoft.clklog.api.models.uservisit.BaseUserVisit;
import com.zcunsoft.clklog.api.models.uservisit.GetUserLatestTimebydateResponse;
import com.zcunsoft.clklog.api.models.uservisit.GetUserVisitUribydateResponse;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorDetailResponse;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriEntryDetailPageResponse;
import com.zcunsoft.clklog.api.models.visituri.GetVisitUriEntryDetailPageResponseData;
import com.zcunsoft.clklog.api.models.visituri.VisitUriEntryDetail;
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
import com.zcunsoft.clklog.api.utils.MathUtils;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExportReportServiceImpl implements IExportReportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final NamedParameterJdbcTemplate clickHouseJdbcTemplate;


    private final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final ThreadLocal<DateFormat> yMdHmsFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final ClklogApiSetting clklogApiSetting;

    private final ConstsDataHolder constsDataHolder;

    public ExportReportServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate, ClklogApiSetting clklogApiSetting, ConstsDataHolder constsDataHolder) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.clklogApiSetting = clklogApiSetting;
        this.constsDataHolder = constsDataHolder;
    }

    private static final ThreadLocal<DecimalFormat> decimalFormat =
            new ThreadLocal<DecimalFormat>() {
                @Override
                protected DecimalFormat initialValue() {
                    return new DecimalFormat("0.####");
                }
            };


    @Override
    public GetFlowTrendDetailResponse getFlowTrendDetail(DownloadFlowTrendRequest downloadFlowTrendRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select * from flow_trend_bydate t";
        String where = "";
        where = buildChannelFilter(downloadFlowTrendRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadFlowTrendRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadFlowTrendRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadFlowTrendRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadFlowTrendRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadFlowTrendRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadFlowTrendRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " order by t.stat_date";

        List<FlowTrendbydate> flowTrendbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbydate>(FlowTrendbydate.class));

        GetFlowTrendDetailResponse response = new GetFlowTrendDetailResponse();
        List<FlowDetail> flowDetailList = new ArrayList<>();

        FlowDetail totalFlowDetail = null;
        if (!flowTrendbydateList.isEmpty()) {
            totalFlowDetail = new FlowDetail();
            totalFlowDetail.setAvgPv(0);
            totalFlowDetail.setAvgVisitTime(0);
            totalFlowDetail.setBounceRate(0);
        }
        int totalVisitTime = 0;
        int totalBounceCount = 0;

        for (FlowTrendbydate flowTrendbydate : flowTrendbydateList) {
            // 合计
            totalFlowDetail.setPv(totalFlowDetail.getPv() + flowTrendbydate.getPv());
            totalFlowDetail.setIpCount(totalFlowDetail.getIpCount() + flowTrendbydate.getIpCount());
            totalFlowDetail.setVisitCount(totalFlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
            totalFlowDetail.setUv(totalFlowDetail.getUv() + flowTrendbydate.getUv());
            totalFlowDetail.setNewUv(totalFlowDetail.getNewUv() + flowTrendbydate.getNewUv());
            totalFlowDetail.setVisitTime(totalFlowDetail.getVisitTime() + flowTrendbydate.getVisitTime());
            totalFlowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
            totalVisitTime += flowTrendbydate.getVisitTime();
            totalBounceCount += flowTrendbydate.getBounceCount();
        }
        if (totalFlowDetail != null && totalFlowDetail.getVisitCount() > 0) {
            float avgPv = totalFlowDetail.getPv() * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = totalVisitTime * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = totalBounceCount * 1.0f / totalFlowDetail.getVisitCount();
            totalFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
        }
        Timestamp startTime = transformFilterTime(downloadFlowTrendRequest.getStartTime(), true, downloadFlowTrendRequest.getTimeType());
        Timestamp endTime = transformFilterTime(downloadFlowTrendRequest.getEndTime(), false, downloadFlowTrendRequest.getTimeType());
        if ("hour".equalsIgnoreCase(downloadFlowTrendRequest.getTimeType())) {
            flowDetailList = getFlowTrendByHour(paramMap, totalFlowDetail, where);
        } else if ("day".equalsIgnoreCase(downloadFlowTrendRequest.getTimeType())) {
            flowDetailList = getFlowTrendByDate(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("week".equalsIgnoreCase(downloadFlowTrendRequest.getTimeType())) {
            flowDetailList = getFlowTrendByWeek(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        } else if ("month".equalsIgnoreCase(downloadFlowTrendRequest.getTimeType())) {
            flowDetailList = getFlowTrendByMonth(flowTrendbydateList, totalFlowDetail, startTime, endTime);
        }
        GetFlowTrendDetailResponseData responseData = new GetFlowTrendDetailResponseData();
        responseData.setDetail(flowDetailList);
        responseData.setTotal(totalFlowDetail);
        response.setData(responseData);
        return response;
    }

    @Override
    public GetSearchWordDetailResponse getSearchWordDetail(DownloadSearchWordRequest downloadSearchWordRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "* from searchword_detail_bydate t";
        String getListSql = "select " + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadSearchWordRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadSearchWordRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadSearchWordRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadSearchWordRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadSearchWordRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadSearchWordRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadSearchWordRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.searchword<>'all' and " + where;
            getSummarySql += " where t.searchword='all' and " + where;
        }
        getListSql += "order by pv desc";
        List<SearchWordDetail> searchWordDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SearchWordDetail>(SearchWordDetail.class));

        List<SearchWordDetail> summarySearchWordDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<SearchWordDetail>(SearchWordDetail.class));

        SearchWordDetail totalSearchWordDetailbydate = null;
        if (summarySearchWordDetailbydate.size() > 0) {
            totalSearchWordDetailbydate = summarySearchWordDetailbydate.get(0);
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetSearchWordDetailResponse response = new GetSearchWordDetailResponse();
        GetSearchWordDetailResponseData responseData = new GetSearchWordDetailResponseData();
        for (SearchWordDetail searchWordDetailbydate : searchWordDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(searchWordDetailbydate, totalSearchWordDetailbydate);
            flowDetail.setSearchword(searchWordDetailbydate.getSearchword());
            flowDetailList.add(flowDetail);
        }
	    
        responseData.setRows(flowDetailList);
        responseData.setTotal(0);
        response.setData(responseData);
        return response;
    }
    
    @Override
    public List<FlowDetail> getAreaDetailList(DownloadAreaRequest downloadAreaRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getListSql = "select t.province as province,t.country as country," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadAreaRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadAreaRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadAreaRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadAreaRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadAreaRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.province<>'all' and t.country<>'all' and t.city<> 'all' and t.country='中国' and " + where;
            getSummarySql += " where t.province='all' and t.country='all' and t.city<> 'all' and t.country='中国' and " + where;
        }
        
        getListSql += " group by t.province,t.country ";
        getListSql += " order by pv desc ";
        List<AreaDetailbydate> areaDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }

        List<FlowDetail> areaDetailList = new ArrayList<>();

        for (AreaDetailbydate areaDetailbydate : areaDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(areaDetailbydate, totalAreaDetailbydate);
            String province = StringUtils.equalsIgnoreCase("未知省份", areaDetailbydate.getProvince()) ? Constants.DEFAULT_PROVICE : areaDetailbydate.getProvince();
            flowDetail.setProvince(StringUtils.equals(Constants.DEFAULT_COUNTRY, areaDetailbydate.getCountry()) ? province : (areaDetailbydate.getCountry()+"-"+province));
            areaDetailList.add(flowDetail);
        }
        return areaDetailList;
    }

    @Override
    public FlowDetail getAreaDetailTotal(DownloadAreaRequest downloadAreaRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from area_detail_bydate t";
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadAreaRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadAreaRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadAreaRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadAreaRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadAreaRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getSummarySql += " where t.province='all' and t.country='all' and t.city='all' and " + where;
        }
        List<AreaDetailbydate> summaryAreaDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<AreaDetailbydate>(AreaDetailbydate.class));

        AreaDetailbydate totalAreaDetailbydate = null;
        if (summaryAreaDetailbydate.size() > 0) {
            totalAreaDetailbydate = summaryAreaDetailbydate.get(0);
        }
        return assemblyFlowDetail(totalAreaDetailbydate, totalAreaDetailbydate);
    }
    
    @Override
    public FlowDetail getVisitUriTotal(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitUriSql = "select sum(pv) as pv,sum(uv) as uv,sum(new_uv) as newUv,sum(down_pv_count) as downPvCount,sum(exit_count) as exitCount,sum(visit_count) as visitCount,sum(visit_time) as visitTime,sum(bounce_count) as bounceCount,sum(ip_count) as ipCount from visituri_detail_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getVisitUriSql += " where uri = 'all' and uri_path='all' and title='all' and " + where.substring(4);
        }

        VisituriDetailbydate visituriDetailbydate = clickHouseJdbcTemplate.queryForObject(getVisitUriSql, paramMap,
                new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        if (visituriDetailbydate != null) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            flowDetail.setExitCount(visituriDetailbydate.getExitCount());
            flowDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            return flowDetail;
        }
        return new FlowDetail();
    }


    @Override
    public List<FlowDetail> getVisitUriDetailList(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(down_pv_count) as downPvCount,sum(exit_count) as exitCount,sum(entry_count) as entryCount from visituri_detail_bydate t";
        String getListSql = "select t.uri as uri,t.uri_path as uri_path,t.title as title, " + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and t.uri_path <> 'all' and t.title<> 'all' and t.pv>0 and " + where;
            getSummarySql += " where t.uri = 'all' and t.uri_path = 'all' and t.title = 'all' and t.pv > 0 and " + where;
        }
        getListSql += " group by t.uri,t.uri_path,t.title "; 
        getListSql += " order by pv desc "; 
        List<VisituriDetailbydate> visitUriDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        List<VisituriDetailbydate> summartVisitUriDetailbydateList = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        VisituriDetailbydate totalVisituriDetailbydate = null;
        if(summartVisitUriDetailbydateList.size() > 0) {
        	totalVisituriDetailbydate = summartVisitUriDetailbydateList.get(0);
        }
        List<FlowDetail> visitUriDetailList = new ArrayList<>();

        for (VisituriDetailbydate visituriDetailbydate : visitUriDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, totalVisituriDetailbydate);
            flowDetail.setEntryCount(visituriDetailbydate.getEntryCount());
            flowDetail.setExitCount(visituriDetailbydate.getExitCount());
            if (visituriDetailbydate.getVisitCount() > 0) {
                float exitRate = visituriDetailbydate.getExitCount() * 1.0f / visituriDetailbydate.getVisitCount();
                flowDetail.setExitRate(Float.parseFloat(decimalFormat.get().format(exitRate*100)));
            }
            flowDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            flowDetail.setUri(visituriDetailbydate.getUri());
            flowDetail.setTitle(visituriDetailbydate.getTitle());
            flowDetail.setUriPath(visituriDetailbydate.getUriPath());
            visitUriDetailList.add(flowDetail);
        }
        return visitUriDetailList;
    }
    
    @Override
    public FlowDetail getVisitUriEntryTotal(DownloadVisitUriEntryRequest downloadVisitUriEntryRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitUriSql = "select sum(pv) as pv,sum(uv) as uv,sum(new_uv) as newUv,sum(entry_count) as entryCount,sum(visit_count) as visitCount,sum(visit_time) as visitTime,sum(bounce_count) as bounceCount,sum(ip_count) as ipCount from visituri_detail_entry_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadVisitUriEntryRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitUriEntryRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitUriEntryRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitUriEntryRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitUriEntryRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitUriEntryRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadVisitUriEntryRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getVisitUriSql += " where uri = 'all' and uri_path='all' and title='all' and " + where.substring(4);
        }

        VisituriDetailbydate visituriDetailbydate = clickHouseJdbcTemplate.queryForObject(getVisitUriSql, paramMap,
                new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        if (visituriDetailbydate != null) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            flowDetail.setEntryCount(visituriDetailbydate.getEntryCount());
            return flowDetail;
        }
        return new FlowDetail();
    }
    
    
    @Override
	public List<FlowDetail> getVisitUriEntryDetailList(DownloadVisitUriEntryRequest downloadVisitUriEntryRequest) {
    	MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(entry_count) as entry_count from visituri_detail_entry_bydate t ";
        String getListSql = "select t.uri as uri,t.uri_path as uri_path,t.title as title," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadVisitUriEntryRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitUriEntryRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitUriEntryRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitUriEntryRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitUriEntryRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitUriEntryRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadVisitUriEntryRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(downloadVisitUriEntryRequest.getUriPath())) {
            if (downloadVisitUriEntryRequest.isNeedFuzzySearchUriPath()) {
                where += " and t.uri_path like :uri_path||'%'";
            } else {
                where += " and t.uri_path=:uri_path";
            }

            String host = extractHost(downloadVisitUriEntryRequest.getUriPath());
            if (StringUtils.isNotBlank(host)) {
                paramMap.addValue("host", host);
                where += " and t.host=:host";
            }
            paramMap.addValue("uri_path", downloadVisitUriEntryRequest.getUriPath().substring(host.length()));
        }
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and t.uri_path <> 'all' and t.title <> 'all' and t.entry_count > 0 and " + where;
            getSummarySql += " where t.uri = 'all' and t.uri_path = 'all' and t.title = 'all' and t.entry_count > 0 and " + where;
        }
        getListSql += " group by t.uri,t.title,t.uri_path";
        getListSql += " order by pv desc "; 

        List<VisituriEntryDetailbydate> visitUriEntryDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriEntryDetailbydate>(VisituriEntryDetailbydate.class));
        List<VisituriEntryDetailbydate> summartVisitUriEntryDetailbydateList = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<VisituriEntryDetailbydate>(VisituriEntryDetailbydate.class));
        VisituriEntryDetailbydate totalVisituriEntryDetailbydate = null;
        if(summartVisitUriEntryDetailbydateList.size() > 0) {
        	totalVisituriEntryDetailbydate = summartVisitUriEntryDetailbydateList.get(0);
        }
        List<FlowDetail> visitUriDetailList = new ArrayList<>();

        for (VisituriEntryDetailbydate visituriEntryDetailbydate : visitUriEntryDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriEntryDetailbydate, totalVisituriEntryDetailbydate);
            flowDetail.setEntryCount(visituriEntryDetailbydate.getEntryCount());
            if (visituriEntryDetailbydate.getVisitCount() > 0) {
                float entryRate = visituriEntryDetailbydate.getEntryCount() * 1.0f / visituriEntryDetailbydate.getVisitCount();
                flowDetail.setEntryRate(Float.parseFloat(decimalFormat.get().format(entryRate*100)));
            }
            flowDetail.setUri(visituriEntryDetailbydate.getUri());
            flowDetail.setTitle(visituriEntryDetailbydate.getTitle());
            flowDetail.setUriPath(visituriEntryDetailbydate.getUriPath());
            visitUriDetailList.add(flowDetail);
        }
        return visitUriDetailList;
	}
    
    

	@Override
	public List<FlowDetail> getVisitUriExitDetailList(DownloadVisitUriExitRequest downloadVisitUriExitRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(exit_count) as exit_count from visituri_detail_exit_bydate t ";
        String getListSql = "select t.uri as uri,t.uri_path as uri_path,t.title as title," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadVisitUriExitRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitUriExitRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitUriExitRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitUriExitRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitUriExitRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitUriExitRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadVisitUriExitRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(downloadVisitUriExitRequest.getUriPath())) {
            if (downloadVisitUriExitRequest.isNeedFuzzySearchUriPath()) {
                where += " and t.uri_path like :uri_path||'%'";
            } else {
                where += " and t.uri_path=:uri_path";
            }

            String host = extractHost(downloadVisitUriExitRequest.getUriPath());
            if (StringUtils.isNotBlank(host)) {
                paramMap.addValue("host", host);
                where += " and t.host=:host";
            }
            paramMap.addValue("uri_path", downloadVisitUriExitRequest.getUriPath().substring(host.length()));
        }
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and t.uri_path <> 'all' and t.title <> 'all' and t.exit_count > 0 and " + where;
            getSummarySql += " where t.uri = 'all' and t.uri_path = 'all' and t.title = 'all' and t.exit_count > 0 and " + where;
            
        }
        getListSql += " group by t.uri,t.title,t.uri_path";
        getListSql += " order by pv desc "; 

        List<VisituriExitDetailbydate> visitUriExitDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriExitDetailbydate>(VisituriExitDetailbydate.class));
        List<VisituriExitDetailbydate> summartVisitUriExitDetailbydateList = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<VisituriExitDetailbydate>(VisituriExitDetailbydate.class));
        VisituriExitDetailbydate totalVisituriExitDetailbydate = null;
        if(summartVisitUriExitDetailbydateList.size() > 0) {
        	totalVisituriExitDetailbydate = summartVisitUriExitDetailbydateList.get(0);
        }
        List<FlowDetail> visitUriDetailList = new ArrayList<>();

        for (VisituriExitDetailbydate visituriExitDetailbydate : visitUriExitDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriExitDetailbydate, totalVisituriExitDetailbydate);
            flowDetail.setExitCount(visituriExitDetailbydate.getExitCount());
            if (visituriExitDetailbydate.getVisitCount() > 0) {
                float exitRate = visituriExitDetailbydate.getExitCount() * 1.0f / visituriExitDetailbydate.getVisitCount();
                flowDetail.setExitRate(Float.parseFloat(decimalFormat.get().format(exitRate*100)));
            }
            flowDetail.setUri(visituriExitDetailbydate.getUri());
            flowDetail.setTitle(visituriExitDetailbydate.getTitle());
            flowDetail.setUriPath(visituriExitDetailbydate.getUriPath());
            visitUriDetailList.add(flowDetail);
        }
        return visitUriDetailList;
	}

	@Override
	public FlowDetail getVisitUriExitTotal(DownloadVisitUriExitRequest downloadVisitUriExitRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitUriSql = "select sum(pv) as pv,sum(uv) as uv,sum(new_uv) as newUv,sum(exit_count) as exitCount,sum(visit_count) as visitCount,sum(visit_time) as visitTime,sum(bounce_count) as bounceCount,sum(ip_count) as ipCount from visituri_detail_exit_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadVisitUriExitRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitUriExitRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitUriExitRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitUriExitRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitUriExitRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitUriExitRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadVisitUriExitRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getVisitUriSql += " where uri = 'all' and uri_path='all' and title='all' and " + where.substring(4);
        }

        VisituriDetailbydate visituriDetailbydate = clickHouseJdbcTemplate.queryForObject(getVisitUriSql, paramMap,
                new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        if (visituriDetailbydate != null) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            flowDetail.setExitCount(visituriDetailbydate.getExitCount());
            return flowDetail;
        }
        return new FlowDetail();
	}

	@Override
	public List<FlowDetail> getVisitUriDownpvDetailList(DownloadVisitUriDownpvRequest downloadVisitUriDownpvRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,sum(down_pv_count) as down_pv_count from visituri_detail_downpv_bydate t ";
        String getListSql = "select t.uri as uri,t.uri_path as uri_path,t.title as title," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadVisitUriDownpvRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitUriDownpvRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitUriDownpvRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitUriDownpvRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitUriDownpvRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitUriDownpvRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadVisitUriDownpvRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(downloadVisitUriDownpvRequest.getUriPath())) {
            if (downloadVisitUriDownpvRequest.isNeedFuzzySearchUriPath()) {
                where += " and t.uri_path like :uri_path||'%'";
            } else {
                where += " and t.uri_path=:uri_path";
            }

            String host = extractHost(downloadVisitUriDownpvRequest.getUriPath());
            if (StringUtils.isNotBlank(host)) {
                paramMap.addValue("host", host);
                where += " and t.host=:host";
            }
            paramMap.addValue("uri_path", downloadVisitUriDownpvRequest.getUriPath().substring(host.length()));
        }
        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.uri <> 'all' and t.uri_path <> 'all' and t.title <> 'all' and t.down_pv_count > 0 and " + where;
            getSummarySql += " where t.uri = 'all' and t.uri_path = 'all' and t.title = 'all' and t.down_pv_count > 0 and " + where;
        }
        getListSql += " group by t.uri,t.title,t.uri_path";
        getListSql += " order by pv desc "; 

        List<VisituriDownpvDetailbydate> visitUriDownpvDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisituriDownpvDetailbydate>(VisituriDownpvDetailbydate.class));
        List<VisituriDownpvDetailbydate> summartVisitUriDownpvDetailbydateList = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<VisituriDownpvDetailbydate>(VisituriDownpvDetailbydate.class));
        VisituriDownpvDetailbydate totalVisituriDownpvDetailbydate = null;
        if(summartVisitUriDownpvDetailbydateList.size() > 0) {
        	totalVisituriDownpvDetailbydate = summartVisitUriDownpvDetailbydateList.get(0);
        }
        List<FlowDetail> visitUriDetailList = new ArrayList<>();

        for (VisituriDownpvDetailbydate visituriDownpvDetailbydate : visitUriDownpvDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDownpvDetailbydate, totalVisituriDownpvDetailbydate);
            flowDetail.setDownPvCount(visituriDownpvDetailbydate.getDownPvCount());
            if (visituriDownpvDetailbydate.getVisitCount() > 0) {
                float downPvRate = visituriDownpvDetailbydate.getDownPvCount() * 1.0f / visituriDownpvDetailbydate.getVisitCount();
                flowDetail.setEntryRate(Float.parseFloat(decimalFormat.get().format(downPvRate*100)));
            }
            flowDetail.setUri(visituriDownpvDetailbydate.getUri());
            flowDetail.setTitle(visituriDownpvDetailbydate.getTitle());
            flowDetail.setUriPath(visituriDownpvDetailbydate.getUriPath());
            visitUriDetailList.add(flowDetail);
        }
        return visitUriDetailList;
	}

	@Override
	public FlowDetail getVisitUriDownpvTotal(DownloadVisitUriDownpvRequest downloadVisitUriDownpvRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getVisitUriSql = "select sum(pv) as pv,sum(uv) as uv,sum(new_uv) as newUv,sum(down_pv_count) as downPvCount,sum(visit_count) as visitCount,sum(visit_time) as visitTime,sum(bounce_count) as bounceCount,sum(ip_count) as ipCount from visituri_detail_downpv_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadVisitUriDownpvRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitUriDownpvRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitUriDownpvRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitUriDownpvRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitUriDownpvRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitUriDownpvRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeFilter(downloadVisitUriDownpvRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getVisitUriSql += " where uri = 'all' and uri_path='all' and title='all' and " + where.substring(4);
        }

        VisituriDetailbydate visituriDetailbydate = clickHouseJdbcTemplate.queryForObject(getVisitUriSql, paramMap,
                new BeanPropertyRowMapper<VisituriDetailbydate>(VisituriDetailbydate.class));
        if (visituriDetailbydate != null) {
            FlowDetail flowDetail = assemblyFlowDetail(visituriDetailbydate, visituriDetailbydate);
            flowDetail.setDownPvCount(visituriDetailbydate.getDownPvCount());
            return flowDetail;
        }
        return new FlowDetail();
	}

	@Override
    public GetSourceWebsiteDetailPageResponse getSourceWebSiteDetail(DownloadSourceWebsiteRequest downloadSourceWebsiteRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from sourcesite_detail_bydate_test t";
        String getListSql = "select sourcesite as sourcesite,latest_referrer as latest_referrer," + selectSql;
        String getSummarySql = "select " + selectSql;
        String where = "";

        where = buildChannelFilter(downloadSourceWebsiteRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadSourceWebsiteRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadSourceWebsiteRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadSourceWebsiteRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadSourceWebsiteRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadSourceWebsiteRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadSourceWebsiteRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.sourcesite<>'all' and t.latest_referrer<>'all' and " + where;
            getSummarySql += " where t.sourcesite='all' and t.latest_referrer='all' and " + where;
        }
        getListSql += " group by t.sourcesite,t.latest_referrer"; 
        getListSql += " order by pv desc "; 
        
        List<SourcesiteDetailbydate> sourcesiteDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));

        List<SourcesiteDetailbydate> summarySourcesiteDetailbydate = clickHouseJdbcTemplate.query(getSummarySql, paramMap, new BeanPropertyRowMapper<SourcesiteDetailbydate>(SourcesiteDetailbydate.class));
        SourcesiteDetailbydate totalSourcesiteDetailbydate = null;
        FlowDetail totalFlowDetail = null;
        if (summarySourcesiteDetailbydate.size() > 0) {
            totalSourcesiteDetailbydate = summarySourcesiteDetailbydate.get(0);
            totalFlowDetail = assemblyFlowDetail(totalSourcesiteDetailbydate, totalSourcesiteDetailbydate);
            totalFlowDetail.setSourcesite(totalSourcesiteDetailbydate.getSourcesite());
        }

        List<FlowDetail> flowDetailList = new ArrayList<>();

        GetSourceWebsiteDetailPageResponse response = new GetSourceWebsiteDetailPageResponse();
        GetSourceWebsiteDetailPageResponseData responseData = new GetSourceWebsiteDetailPageResponseData();
        for (SourcesiteDetailbydate sourcesiteDetailbydate : sourcesiteDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(sourcesiteDetailbydate, totalSourcesiteDetailbydate);
            flowDetail.setSourcesite(sourcesiteDetailbydate.getSourcesite());
            flowDetail.setLatestReferrer(sourcesiteDetailbydate.getLatestReferrer());
            flowDetailList.add(flowDetail);
        }
        responseData.setRows(flowDetailList);
        responseData.setTotal(0);
        responseData.setSummary(totalFlowDetail);
        response.setData(responseData);
        return response;
    }
    
    @Override
    public GetVisitorDetailResponse getVisitorDetail(DownloadVisitorRequest downloadVisitorRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select t.is_first_day as is_first_day,sum(pv) as pv,sum(uv) as uv,sum(ip_count) as ip_count,sum(visit_time) as visit_time,sum(visit_count) as visit_count,sum(new_uv) as new_uv,sum(bounce_count) as bounce_count from visitor_detail_bydate t";
        String where = "";
        where = buildChannelFilter(downloadVisitorRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitorRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitorRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitorRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadVisitorRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadVisitorRequest.getProvince(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.is_first_day ";
        List<VisitorDetailbydate> visitorDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        GetVisitorDetailResponse response = new GetVisitorDetailResponse();
        VisitorDetailbydate totalVisitorDetailbydate = null;
        Optional<VisitorDetailbydate> optionalVisitorDetailbydate = visitorDetailbydateList.stream().filter(f -> f.getIsFirstDay().equalsIgnoreCase("all")).findAny();
        if (optionalVisitorDetailbydate.isPresent()) {
            totalVisitorDetailbydate = optionalVisitorDetailbydate.get();
        }

        List<FlowDetail> visitorDetailList = new ArrayList<FlowDetail>();
        for (VisitorDetailbydate visitorDetailbydate : visitorDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(visitorDetailbydate, totalVisitorDetailbydate);
            flowDetail.setVisitorType(VisitorType.getName(visitorDetailbydate.getIsFirstDay()));
            visitorDetailList.add(flowDetail);
        }
        response.setData(visitorDetailList);
        return response;
    }

    @Override
    public FlowDetail getVisitorTotal(DownloadRequest downloadRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListVisitorDetailSql = "select sum(uv) as uv,sum(new_uv) as newUv from visitor_detail_bydate t ";
        String getListVisitorLifeSql = "select sum(revisit_uv) as revisitUv,sum(silent_uv) as silentUv,sum(churn_uv) as churnUv from visitor_life_bydate t ";
        String where = "";
        where = buildChannelFilter(downloadRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadRequest.getProjectName(), paramMap, where);
        where = buildCountryFilter(downloadRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadRequest.getProvince(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListVisitorLifeSql += " where  " + where.substring(4);
        }
        where = buildVisitorTypeFilter(downloadRequest.getVisitorType(), paramMap, where);
        if (StringUtils.isNotBlank(where)) {
//            getListVisitorDetailSql += " where t.is_first_day='all' and " + where.substring(4);
            getListVisitorDetailSql += " where " + where.substring(4);
        }

        VisitorDetailbydate visitorDetailbydate = clickHouseJdbcTemplate.queryForObject(getListVisitorDetailSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        VisitorLifebydate visitorLifebydate = clickHouseJdbcTemplate.queryForObject(getListVisitorLifeSql, paramMap,
                new BeanPropertyRowMapper<VisitorLifebydate>(VisitorLifebydate.class));
        FlowDetail flowDetail = new FlowDetail();;
        if (visitorDetailbydate != null) {
        	flowDetail.setUv(visitorDetailbydate.getUv());
        	flowDetail.setNewUv(visitorDetailbydate.getNewUv());
        	if (visitorDetailbydate.getUv() > 0) {
                float newUvRate = visitorDetailbydate.getNewUv() * 1.0f / visitorDetailbydate.getUv();
                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
        }
        if (!"新访客".equalsIgnoreCase(downloadRequest.getVisitorType())) {
        	flowDetail.setChurn(visitorLifebydate.getChurnUv());
        	flowDetail.setRevisit(visitorLifebydate.getRevisitUv());
        	flowDetail.setSilent(visitorLifebydate.getSilentUv());
        }
        return getVisitorTotal(null, where, getListVisitorDetailSql, where, null, null, getListVisitorLifeSql);
    }
    
    @Override
    public FlowDetail getVisitorTotal(List<String> channel,String startTime,String endTime,String projectName,List<String> country,List<String> province,String visitorType) {
    	MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListVisitorDetailSql = "select sum(uv) as uv,sum(new_uv) as newUv from visitor_detail_bydate t ";
        String getListVisitorLifeSql = "select sum(revisit_uv) as revisitUv,sum(silent_uv) as silentUv,sum(churn_uv) as churnUv from visitor_life_bydate t ";
        String where = "";
        where = buildChannelFilter(channel, paramMap, where);
        where = buildStatDateStartFilter(startTime, paramMap, where);
        where = buildStatDateEndFilter(endTime, paramMap, where);
        where = buildProjectNameFilter(projectName, paramMap, where);
        where = buildCountryFilter(country, paramMap, where);
        where = buildProvinceFilter(province, paramMap, where);
        if (StringUtils.isNotBlank(where)) {
            getListVisitorLifeSql += " where  " + where.substring(4);
        }
        where = buildVisitorTypeFilter(visitorType, paramMap, where);
        if (StringUtils.isNotBlank(where)) {
//            getListVisitorDetailSql += " where t.is_first_day='all' and " + where.substring(4);
            getListVisitorDetailSql += " where " + where.substring(4);
        }

        VisitorDetailbydate visitorDetailbydate = clickHouseJdbcTemplate.queryForObject(getListVisitorDetailSql, paramMap,
                new BeanPropertyRowMapper<VisitorDetailbydate>(VisitorDetailbydate.class));
        VisitorLifebydate visitorLifebydate = clickHouseJdbcTemplate.queryForObject(getListVisitorLifeSql, paramMap,
                new BeanPropertyRowMapper<VisitorLifebydate>(VisitorLifebydate.class));
        FlowDetail flowDetail = new FlowDetail();;
        if (visitorDetailbydate != null) {
        	flowDetail.setUv(visitorDetailbydate.getUv());
        	flowDetail.setNewUv(visitorDetailbydate.getNewUv());
        	if (visitorDetailbydate.getUv() > 0) {
                float newUvRate = visitorDetailbydate.getNewUv() * 1.0f / visitorDetailbydate.getUv();
                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
        }
        if (!"新访客".equalsIgnoreCase(visitorType)) {
        	flowDetail.setChurn(visitorLifebydate.getChurnUv());
        	flowDetail.setRevisit(visitorLifebydate.getRevisitUv());
        	flowDetail.setSilent(visitorLifebydate.getSilentUv());
        }
        return flowDetail;
    }
    
    @Override
    public GetDeviceDetailResponse getDeviceDetail(DownloadDeviceRequest downloadDeviceRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select device,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from device_detail_bydate t";
        String where = "";

        where = buildChannelFilter(downloadDeviceRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadDeviceRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadDeviceRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadDeviceRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadDeviceRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadDeviceRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadDeviceRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.device";
        getListSql += " order by pv desc ";
        List<DeviceDetailbydate> deviceDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<DeviceDetailbydate>(DeviceDetailbydate.class));

        List<FlowDetail> flowDetailList = new ArrayList<>();

        DeviceDetailbydate totalDeviceDetailbydate = null;

        Optional<DeviceDetailbydate> optionalDeviceDetailbydate = deviceDetailbydateList.stream().filter(f -> f.getDevice().equalsIgnoreCase("all")).findAny();
        if (optionalDeviceDetailbydate.isPresent()) {
            totalDeviceDetailbydate = optionalDeviceDetailbydate.get();
        }

        GetDeviceDetailResponse response = new GetDeviceDetailResponse();
        for (DeviceDetailbydate deviceDetailbydate : deviceDetailbydateList) {
        	if (deviceDetailbydate.getDevice().equalsIgnoreCase("all")) {
        		continue;
        	}
            FlowDetail flowDetail = assemblyFlowDetail(deviceDetailbydate, totalDeviceDetailbydate);
            flowDetail.setDevice(StringUtils.isEmpty(deviceDetailbydate.getDevice()) ? Constants.DEFAULT_DEVICE : deviceDetailbydate.getDevice());
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
    }
    
    @Override
    public GetChannelDetailResponse getChannelDetail(DownloadChannelRequest downloadChannelRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select lib,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from channel_detail_bydate t";
        String where = "";

        where = buildStatDateStartFilter(downloadChannelRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadChannelRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadChannelRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadChannelRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadChannelRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadChannelRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.lib";

        List<ChannelDetailbydate> channelDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<ChannelDetailbydate>(ChannelDetailbydate.class));

        List<FlowDetail> flowDetailList = new ArrayList<>();

        ChannelDetailbydate totalChannelDetailbydate = null;

        Optional<ChannelDetailbydate> optionalChannelDetailbydate = channelDetailbydateList.stream().filter(f -> f.getLib().equalsIgnoreCase("all")).findAny();
        if (optionalChannelDetailbydate.isPresent()) {
            totalChannelDetailbydate = optionalChannelDetailbydate.get();
        }

        GetChannelDetailResponse response = new GetChannelDetailResponse();
        for (ChannelDetailbydate channelDetailbydate : channelDetailbydateList) {
            FlowDetail flowDetail = assemblyFlowDetail(channelDetailbydate, totalChannelDetailbydate);
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
    }
    
    @Override
	public List<FlowDetail> getVisitorList(DownloadVisitorListRequest downloadVisitorListRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String selectSql = "sum(pv) as pv,sum(visit_count) as visit_count,sum(visit_time) as visit_time,max(latest_time) as latest_time from visitor_summary_byvisitor t";
        String getListSql = "select t.distinct_id as distinct_id,t.is_first_day as is_first_day," + selectSql;
        String where = "";

        where = buildChannelByAllFilter(downloadVisitorListRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadVisitorListRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadVisitorListRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadVisitorListRequest.getProjectName(), paramMap, where);
        where = buildCountryByAllFilter(downloadVisitorListRequest.getCountry(), paramMap, where);
        where = buildProvinceByAllFilter(downloadVisitorListRequest.getProvince(), paramMap, where);
        where = buildVisitorTypeByAllFilter(downloadVisitorListRequest.getVisitorType(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            where = where.substring(4);
            getListSql += " where t.distinct_id <> 'all' and " + where;
        }
        getListSql += " group by t.distinct_id,t.is_first_day";
        getListSql += " order by pv desc ";
        List<VisitorSummarybyvisitor> visitorSummarybyvisitorList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<VisitorSummarybyvisitor>(VisitorSummarybyvisitor.class));

        List<FlowDetail> visitorListList = new ArrayList<>();

        for (VisitorSummarybyvisitor visitorSummarybyvisitor : visitorSummarybyvisitorList) {
        	FlowDetail flowDetail = new FlowDetail();
           
        	flowDetail.setDistinctId(visitorSummarybyvisitor.getDistinctId());
        	flowDetail.setLatestTime(visitorSummarybyvisitor.getLatestTime());
        	flowDetail.setVisitCount(visitorSummarybyvisitor.getVisitCount());
        	flowDetail.setPv(visitorSummarybyvisitor.getPv());
        	flowDetail.setVisitTime(visitorSummarybyvisitor.getVisitTime());
        	flowDetail.setVisitorType(VisitorType.getName(visitorSummarybyvisitor.getIsFirstDay()));
        	if (visitorSummarybyvisitor.getVisitCount() > 0) {
                float avgPv = visitorSummarybyvisitor.getPv() * 1.0f / visitorSummarybyvisitor.getVisitCount();
                flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));
        	} else {
        		flowDetail.setAvgPv(0.0f);
        	}
            visitorListList.add(flowDetail);
        }
		return visitorListList;
	}
    
    @Override
    public List<BaseUserVisit> getUserVisit(DownloadBaseRequest downloadBaseRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(v1_uv) as v1Uv,sum(v2_uv) as v2Uv,sum(v3_uv) as v3Uv,sum(v4_uv) as v4Uv,sum(v5_uv) as v5Uv,sum(v6_uv) as v6Uv,sum(v7_uv) as v7Uv,sum(v8_uv) as v8Uv,sum(v9_uv) as v9Uv,sum(v10_uv) as v10Uv,sum(v11_15_uv) as v11_15Uv,sum(v16_50_uv) as v16_50Uv,sum(v51_100_uv) as v51_100Uv,sum(v101_200_uv) as v101_200Uv,sum(v201_300_uv) as v201_300Uv,sum(v300_uv) as v300Uv from user_visit_bydate t";
        String where = "";

        where = buildChannelFilter(downloadBaseRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadBaseRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadBaseRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadBaseRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadBaseRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadBaseRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadBaseRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisitbydate> userVisitbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisitbydate>(UserVisitbydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userVisitbydateList.size() > 0) {
            UserVisitbydate userVisitbydate = userVisitbydateList.get(0);
            
            total = userVisitbydate.getV1Uv() + userVisitbydate.getV2Uv() + userVisitbydate.getV3Uv() + userVisitbydate.getV4Uv() + userVisitbydate.getV5Uv() + userVisitbydate.getV6Uv()
            + userVisitbydate.getV7Uv() + userVisitbydate.getV8Uv() + userVisitbydate.getV9Uv() + userVisitbydate.getV10Uv() + userVisitbydate.getV11_15Uv() + userVisitbydate.getV16_50Uv()
            + userVisitbydate.getV51_100Uv() + userVisitbydate.getV101_200Uv() + userVisitbydate.getV201_300Uv() + userVisitbydate.getV300Uv();
            
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1次");
            baseUserVisit.setValue(userVisitbydate.getV1Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV1Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2次");
            baseUserVisit.setValue(userVisitbydate.getV2Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV2Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3次");
            baseUserVisit.setValue(userVisitbydate.getV3Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV3Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4次");
            baseUserVisit.setValue(userVisitbydate.getV4Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV4Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5次");
            baseUserVisit.setValue(userVisitbydate.getV5Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV5Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6次");
            baseUserVisit.setValue(userVisitbydate.getV6Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV6Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("7次");
            baseUserVisit.setValue(userVisitbydate.getV7Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV7Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("8次");
            baseUserVisit.setValue(userVisitbydate.getV8Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV8Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("9次");
            baseUserVisit.setValue(userVisitbydate.getV9Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV9Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10次");
            baseUserVisit.setValue(userVisitbydate.getV10Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV10Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-15次");
            baseUserVisit.setValue(userVisitbydate.getV11_15Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV11_15Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("16-50次");
            baseUserVisit.setValue(userVisitbydate.getV16_50Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV16_50Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("51-100次");
            baseUserVisit.setValue(userVisitbydate.getV51_100Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV51_100Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("101-200次");
            baseUserVisit.setValue(userVisitbydate.getV101_200Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV101_200Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("201-300次");
            baseUserVisit.setValue(userVisitbydate.getV201_300Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV201_300Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("300次以上");
            baseUserVisit.setValue(userVisitbydate.getV300Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisitbydate.getV300Uv()));
            baseUserVisitList.add(baseUserVisit);
        }

        return baseUserVisitList;
    }

    @Override
    public List<BaseUserVisit> getUserPv(DownloadBaseRequest downloadBaseRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv1_uv) as pv1Uv,sum(pv2_5_uv) as pv2_5Uv,sum(pv6_10_uv) as pv6_10Uv,sum(pv11_20_uv) as pv11_20Uv,sum(pv21_30_uv) as pv21_30Uv,sum(pv31_40_uv) as pv31_40Uv,sum(pv41_50_uv) as pv41_50Uv,sum(pv51_100_uv) as pv51_100Uv,sum(pv101_uv) as pv101Uv from user_pv_bydate t";
        String where = "";

        where = buildChannelFilter(downloadBaseRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadBaseRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadBaseRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadBaseRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadBaseRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadBaseRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadBaseRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserPvbydate> userPvbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserPvbydate>(UserPvbydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userPvbydateList.size() > 0) {
            UserPvbydate userPvbydate = userPvbydateList.get(0);
            
            total = userPvbydate.getPv1Uv()+userPvbydate.getPv2_5Uv()+userPvbydate.getPv6_10Uv()+userPvbydate.getPv11_20Uv()+userPvbydate.getPv21_30Uv()
            +userPvbydate.getPv31_40Uv()+userPvbydate.getPv41_50Uv()+userPvbydate.getPv51_100Uv()+userPvbydate.getPv101Uv();
            
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1页");
            baseUserVisit.setValue(userPvbydate.getPv1Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv1Uv()));;
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2-5页");
            baseUserVisit.setValue(userPvbydate.getPv2_5Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv2_5Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6-10页");
            baseUserVisit.setValue(userPvbydate.getPv6_10Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv6_10Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-20页");
            baseUserVisit.setValue(userPvbydate.getPv11_20Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv11_20Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("21-30页");
            baseUserVisit.setValue(userPvbydate.getPv21_30Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv21_30Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("31-40页");
            baseUserVisit.setValue(userPvbydate.getPv31_40Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv31_40Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("41-50页");
            baseUserVisit.setValue(userPvbydate.getPv41_50Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv41_50Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("51-100页");
            baseUserVisit.setValue(userPvbydate.getPv51_100Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv51_100Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("100页以上");
            baseUserVisit.setValue(userPvbydate.getPv101Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userPvbydate.getPv101Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        return baseUserVisitList;
    }

    @Override
    public List<BaseUserVisit> getUserVisitTime(DownloadBaseRequest downloadBaseRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(vt0_10_uv) as vt0_10Uv,sum(vt10_30_uv) as vt10_30Uv,sum(vt30_60_uv) as vt30_60Uv,sum(vt60_120_uv) as vt60_120Uv,sum(vt120_180_uv) as vt120_180Uv,sum(vt180_240_uv) as vt180_240Uv,sum(vt240_300_uv) as vt240_300Uv,sum(vt300_600_uv) as vt300_600Uv,sum(vt600_1800_uv) as vt600_1800Uv,sum(vt1800_3600_uv) as vt1800_3600Uv,sum(vt3600_uv) as vt3600Uv from user_visittime_bydate t";
        String where = "";

        where = buildChannelFilter(downloadBaseRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadBaseRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadBaseRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadBaseRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadBaseRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadBaseRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadBaseRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisittimebydate> userVisitTimebydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisittimebydate>(UserVisittimebydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userVisitTimebydateList.size() > 0) {
            UserVisittimebydate userVisittimebydate = userVisitTimebydateList.get(0);

            total = userVisittimebydate.getVt0_10Uv() + userVisittimebydate.getVt10_30Uv() + userVisittimebydate.getVt30_60Uv() + userVisittimebydate.getVt60_120Uv() + userVisittimebydate.getVt120_180Uv()
            + userVisittimebydate.getVt180_240Uv() + userVisittimebydate.getVt240_300Uv() + userVisittimebydate.getVt300_600Uv() + userVisittimebydate.getVt600_1800Uv() + userVisittimebydate.getVt1800_3600Uv()
            + userVisittimebydate.getVt3600Uv();
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("0-10秒");
            baseUserVisit.setValue(userVisittimebydate.getVt0_10Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt0_10Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10-30秒");
            baseUserVisit.setValue(userVisittimebydate.getVt10_30Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt10_30Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("30-60秒");
            baseUserVisit.setValue(userVisittimebydate.getVt30_60Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt30_60Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1-2分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt60_120Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt60_120Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2-3分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt120_180Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt120_180Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3-4分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt180_240Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt180_240Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4-5分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt240_300Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt240_300Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5-10分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt300_600Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt300_600Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10-30分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt600_1800Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt600_1800Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("30-60分钟");
            baseUserVisit.setValue(userVisittimebydate.getVt1800_3600Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt1800_3600Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1小时以上");
            baseUserVisit.setValue(userVisittimebydate.getVt3600Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisittimebydate.getVt3600Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        return baseUserVisitList;
    }
    
    

    @Override
	public List<BaseUserVisit> getUserVisitUri(DownloadBaseRequest downloadBaseRequest) {
    	MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv1_uri) as pv1_uri,sum(pv2_uri) as pv2_uri,sum(pv3_uri) as pv3_uri,sum(pv4_uri) as pv4_uri,sum(pv5_uri) as pv5_uri,sum(pv6_uri) as pv6_uri,sum(pv7_uri) as pv7_uri,sum(pv8_uri) as pv8_uri,sum(pv9_uri) as pv9_uri,sum(pv10_uri) as pv10_uri,sum(pv11_15_uri) as pv11_15_uri,sum(pv16_20_uri) as pv16_20_uri,sum(pv21_uri) as pv21_uri from user_visituri_bydate t";
        String where = "";

        where = buildChannelFilter(downloadBaseRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadBaseRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadBaseRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadBaseRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadBaseRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadBaseRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadBaseRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        List<UserVisituribydate> userVisitTimebydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserVisituribydate>(UserVisituribydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userVisitTimebydateList.size() > 0) {
        	UserVisituribydate userVisituribydate = userVisitTimebydateList.get(0);

            total = userVisituribydate.getPv1Uri()+ userVisituribydate.getPv2Uri() + userVisituribydate.getPv3Uri() + userVisituribydate.getPv4Uri() + userVisituribydate.getPv5Uri()
            + userVisituribydate.getPv6Uri() + userVisituribydate.getPv7Uri() + userVisituribydate.getPv8Uri() + userVisituribydate.getPv9Uri() + userVisituribydate.getPv10Uri()
            + userVisituribydate.getPv11_15Uri() + userVisituribydate.getPv16_20Uri() + userVisituribydate.getPv21Uri();
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1页");
            baseUserVisit.setValue(userVisituribydate.getPv1Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv1Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2页");
            baseUserVisit.setValue(userVisituribydate.getPv2Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv2Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3页");
            baseUserVisit.setValue(userVisituribydate.getPv3Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv3Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("4页");
            baseUserVisit.setValue(userVisituribydate.getPv4Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv4Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("5页");
            baseUserVisit.setValue(userVisituribydate.getPv5Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv5Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("6页");
            baseUserVisit.setValue(userVisituribydate.getPv6Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv6Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("7页");
            baseUserVisit.setValue(userVisituribydate.getPv7Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv7Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("8页");
            baseUserVisit.setValue(userVisituribydate.getPv8Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv8Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("9页");
            baseUserVisit.setValue(userVisituribydate.getPv9Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv9Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("10页");
            baseUserVisit.setValue(userVisituribydate.getPv10Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv10Uri()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("11-15页");
            baseUserVisit.setValue(userVisituribydate.getPv11_15Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv11_15Uri()));
            baseUserVisitList.add(baseUserVisit);
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("16-20页");
            baseUserVisit.setValue(userVisituribydate.getPv16_20Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv16_20Uri()));
            baseUserVisitList.add(baseUserVisit);
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("21以上页");
            baseUserVisit.setValue(userVisituribydate.getPv21Uri());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userVisituribydate.getPv21Uri()));
            baseUserVisitList.add(baseUserVisit);
            
        }
        return baseUserVisitList;
	}

	@Override
	public List<BaseUserVisit> getUserLatestTime(DownloadBaseRequest downloadBaseRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select countDistinct(if(p3.days_diff = 0, p3.distinct_id, NULL)) AS lt0_uv,countDistinct(if(p3.days_diff = 1, p3.distinct_id, NULL)) AS lt1_uv,countDistinct(if(p3.days_diff = 2, p3.distinct_id, NULL)) AS lt2_uv,"
        		+ "countDistinct(if(p3.days_diff > 2 AND p3.days_diff <=7, p3.distinct_id, NULL)) AS lt3_7_uv,countDistinct(if(p3.days_diff > 7 AND p3.days_diff <=15, p3.distinct_id, NULL)) AS lt8_15_uv,"
        		+ "countDistinct(if(p3.days_diff > 15 AND p3.days_diff <=30, p3.distinct_id, NULL)) AS lt16_30_uv,countDistinct(if(p3.days_diff > 30 AND p3.days_diff <=90, p3.distinct_id, NULL)) AS lt31_90_uv,"
        		+ "countDistinct(if(p3.days_diff > 90, p3.distinct_id, NULL)) AS lt90_uv from "
        		+ "(select p1.distinct_id as distinct_id,dateDiff('day',toDate(p2.max_time),if(p2.distinct_id ='',toDate(p2.max_time),toDate(p1.max_time))) as days_diff from ";
        String getCurrentSql = "(SELECT t.distinct_id AS distinct_id, max(t.stat_date) AS max_time FROM visitor_detail_byinfo t ";
        String getLetastTimeSql = "(SELECT t.distinct_id AS distinct_id, max(t.stat_date) AS max_time FROM visitor_detail_byinfo t ";
        String where = "";
        String beforeWhere = "";
        where = buildChannelByAllFilter(downloadBaseRequest.getChannel(), paramMap, where);
        where = buildProjectNameFilter(downloadBaseRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeByAllFilter(downloadBaseRequest.getVisitorType(), paramMap, where);
        where = buildCountryByAllFilter(downloadBaseRequest.getCountry(), paramMap, where);
        where = buildProvinceByAllFilter(downloadBaseRequest.getProvince(), paramMap, where);
        beforeWhere += where;
        beforeWhere = buildStatDateStartLessThanFilter(downloadBaseRequest.getStartTime(), paramMap, beforeWhere);
        where = buildStatDateStartFilter(downloadBaseRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadBaseRequest.getEndTime(), paramMap, where);
        
        if (StringUtils.isNotBlank(where)) {
        	getCurrentSql += " where " + where.substring(4);
        	getCurrentSql += " group by distinct_id) p1 ";
            getLetastTimeSql += " where " + beforeWhere.substring(4);
            getLetastTimeSql += " group by distinct_id) p2  ";
        }
        getListSql += getCurrentSql +" left join " + getLetastTimeSql +" on p1.distinct_id=p2.distinct_id ";
        getListSql += ") p3";
        List<UserLatesttimebydate> userLatesttimebydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<UserLatesttimebydate>(UserLatesttimebydate.class));
        List<BaseUserVisit> baseUserVisitList = new ArrayList<BaseUserVisit>();
        BaseUserVisit baseUserVisit = null;
        int total = 0;
        if (userLatesttimebydateList.size() > 0) {
        	UserLatesttimebydate userLatesttimebydate = userLatesttimebydateList.get(0);
            
            total = userLatesttimebydate.getLt0Uv()+userLatesttimebydate.getLt1Uv()+userLatesttimebydate.getLt2Uv()+userLatesttimebydate.getLt3_7Uv()+userLatesttimebydate.getLt8_15Uv()
            +userLatesttimebydate.getLt16_30Uv()+userLatesttimebydate.getLt31_90Uv()+userLatesttimebydate.getLt90Uv();
            
            
            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1天内");
            baseUserVisit.setValue(userLatesttimebydate.getLt0Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt0Uv()));;
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("1天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt1Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt1Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("2天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt2Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt2Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("3-7天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt3_7Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt3_7Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("8-15天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt8_15Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt8_15Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("16-30天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt16_30Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt16_30Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("31-90天前");
            baseUserVisit.setValue(userLatesttimebydate.getLt31_90Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt31_90Uv()));
            baseUserVisitList.add(baseUserVisit);

            baseUserVisit = new BaseUserVisit();
            baseUserVisit.setKey("90天以上前");
            baseUserVisit.setValue(userLatesttimebydate.getLt90Uv());
            baseUserVisit.setRate(MathUtils.getRateByMultip100(total, userLatesttimebydate.getLt90Uv()));
            baseUserVisitList.add(baseUserVisit);
        }
        return baseUserVisitList;
	}
	
	

	@Override
	public GetOsDetailResponse getOsDetail(DownloadOsRequest downloadOsRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select os,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv,sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from os_detail_bydate t";
        String where = "";

        where = buildChannelFilter(downloadOsRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(downloadOsRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(downloadOsRequest.getEndTime(), paramMap, where);
        where = buildProjectNameFilter(downloadOsRequest.getProjectName(), paramMap, where);
        where = buildVisitorTypeFilter(downloadOsRequest.getVisitorType(), paramMap, where);
        where = buildCountryFilter(downloadOsRequest.getCountry(), paramMap, where);
        where = buildProvinceFilter(downloadOsRequest.getProvince(), paramMap, where);

        if (StringUtils.isNotBlank(where)) {
            getListSql += " where " + where.substring(4);
        }
        getListSql += " group by t.os";
        getListSql += " order by pv desc ";
        List<OsDetailbydate> osDetailbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap, new BeanPropertyRowMapper<OsDetailbydate>(OsDetailbydate.class));

        List<FlowDetail> flowDetailList = new ArrayList<>();

        OsDetailbydate totalOsDetailbydate = null;

        Optional<OsDetailbydate> optionalOsDetailbydate = osDetailbydateList.stream().filter(f -> f.getOs().equalsIgnoreCase("all")).findAny();
        if (optionalOsDetailbydate.isPresent()) {
            totalOsDetailbydate = optionalOsDetailbydate.get();
        }

        GetOsDetailResponse response = new GetOsDetailResponse();
        for (OsDetailbydate osDetailbydate : osDetailbydateList) {
        	if (osDetailbydate.getOs().equalsIgnoreCase("all")) {
        		continue;
        	}
            FlowDetail flowDetail = assemblyFlowDetail(osDetailbydate, totalOsDetailbydate);
            flowDetail.setOs(StringUtils.isEmpty(osDetailbydate.getOs()) ? Constants.DEFAULT_OS : osDetailbydate.getOs());
            flowDetailList.add(flowDetail);
        }
        response.setData(flowDetailList);
        return response;
	}

	private FlowSummary getFlowByTimeframe(GetFlowRequest getFlowRequest) {
        MapSqlParameterSource paramMap = new MapSqlParameterSource();
        String getListSql = "select sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv, sum(visit_time) as visit_time,sum(bounce_count) as bounce_count from flow_trend_bydate t";

        BaseSummaryRequest request = (BaseSummaryRequest) getFlowRequest;

        String summaryWhere = buildSummaryWhere(request, paramMap);

        if (StringUtils.isNotBlank(summaryWhere)) {
            getListSql += " where " + summaryWhere.substring(4);
        }

        FlowSummarybydate flowSummaryList = clickHouseJdbcTemplate.queryForObject(getListSql, paramMap, new BeanPropertyRowMapper<FlowSummarybydate>(FlowSummarybydate.class));

        FlowSummary flowSummary = assemblyFlowSummary(flowSummaryList);

        flowSummary.setStatTime(paramMap.getValue("starttime") + " - " + paramMap.getValue("endtime"));
        return flowSummary;
    }

    private List<FlowDetail> getFlowTrendByHour(MapSqlParameterSource paramMap, FlowDetail totalFlowDetail, String where) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        String getHourListSql = "select stat_hour,sum(pv) as pv,sum(ip_count) as ip_count,sum(visit_count) as visit_count,sum(uv) as uv, sum(new_uv) as new_uv,sum(visit_time) as visit_time,sum(bounce_count) as bounce_count,max(lib) as lib from flow_trend_byhour t where " + where.substring(4) + " group by stat_hour order by stat_hour";
        List<FlowTrendbyhour> flowTrendbyhourList = clickHouseJdbcTemplate.query(getHourListSql, paramMap, new BeanPropertyRowMapper<FlowTrendbyhour>(FlowTrendbyhour.class));

        for (int i = 0; i < 24; i++) {
            String hour = String.valueOf(100 + i).substring(1);
            FlowDetail flowDetail = new FlowDetail();
            flowDetail.setStatTime(hour);
            Optional<FlowTrendbyhour> opFlowTrendbyhour = flowTrendbyhourList.stream().filter(f -> f.getStatHour().equalsIgnoreCase(hour)).findAny();
            if (opFlowTrendbyhour.isPresent()) {
                FlowTrendbyhour flowTrendbyhour = opFlowTrendbyhour.get();
                flowDetail.setVisitTime(flowTrendbyhour.getVisitTime());
                flowDetail.setPv(flowTrendbyhour.getPv());
                flowDetail.setIpCount(flowTrendbyhour.getIpCount());
                flowDetail.setVisitCount(flowTrendbyhour.getVisitCount());
                flowDetail.setUv(flowTrendbyhour.getUv());
                flowDetail.setNewUv(flowTrendbyhour.getNewUv());
                flowDetail.setChannel(LibType.getName(flowTrendbyhour.getLib()));
                flowDetail.setAvgPv(0);
                flowDetail.setAvgVisitTime(0);
                flowDetail.setBounceRate(0);
                flowDetail.setNewUvRate(0.0f);
                flowDetail.setPvRate(0.0f);

                if (flowDetail.getVisitCount() > 0) {
                    float avgPv = flowTrendbyhour.getPv() * 1.0f / flowTrendbyhour.getVisitCount();
                    flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                    float avgVisitTime = flowTrendbyhour.getVisitTime() * 1.0f / flowTrendbyhour.getVisitCount();
                    flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                    float bounceRate = flowTrendbyhour.getBounceCount() * 1.0f / flowTrendbyhour.getVisitCount();
                    flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
                }
                if (flowDetail.getUv() > 0) {
                    float newUvRate = flowTrendbyhour.getNewUv() * 1.0f / flowTrendbyhour.getUv();
                    flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
                }
                if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                    float pvRate = flowTrendbyhour.getPv() * 1.0f / totalFlowDetail.getPv();
                    flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
                }
            }
            flowDetailList.add(flowDetail);
        }
        return flowDetailList;
    }

    private List<FlowDetail> getFlowTrendByDate(List<FlowTrendbydate> flowTrendbydateList, FlowDetail totalFlowDetail, Timestamp startTime, Timestamp endTime) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        do {
            FlowDetail flowDetail = new FlowDetail();
            flowDetail.setStatTime(this.yMdFORMAT.get().format(tmpTime));
            Timestamp statDate = tmpTime;

            Optional<FlowTrendbydate> optionalFlowTrendbydate = flowTrendbydateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalFlowTrendbydate.isPresent()) {
                FlowTrendbydate flowTrendbydate = optionalFlowTrendbydate.get();
                flowDetail.setVisitTime(flowTrendbydate.getVisitTime());
                flowDetail.setPv(flowTrendbydate.getPv());
                flowDetail.setIpCount(flowTrendbydate.getIpCount());
                flowDetail.setVisitCount(flowTrendbydate.getVisitCount());
                flowDetail.setUv(flowTrendbydate.getUv());
                flowDetail.setNewUv(flowTrendbydate.getNewUv());
                flowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
                flowDetail.setAvgPv(0);
                flowDetail.setAvgVisitTime(0);
                flowDetail.setBounceRate(0);
                flowDetail.setNewUvRate(0.0f);
                flowDetail.setPvRate(0.0f);
                if (flowDetail.getVisitCount() > 0) {

                    float avgPv = flowTrendbydate.getPv() * 1.0f / flowTrendbydate.getVisitCount();
                    flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                    float avgVisitTime = flowTrendbydate.getVisitTime() * 1.0f / flowTrendbydate.getVisitCount();
                    flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                    float bounceRate = flowTrendbydate.getBounceCount() * 1.0f / flowTrendbydate.getVisitCount();
                    flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
                }
                if (flowDetail.getUv() > 0) {
                    float newUvRate = flowTrendbydate.getNewUv() * 1.0f / flowTrendbydate.getUv();
                    flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
                }
                if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                    float pvRate = flowTrendbydate.getPv() * 1.0f / totalFlowDetail.getPv();
                    flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
                }
            }
            flowDetailList.add(flowDetail);
            tmpTime = new Timestamp(tmpTime.getTime() + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return flowDetailList;
    }

    private List<FlowDetail> getFlowTrendByWeek(List<FlowTrendbydate> flowTrendbydateList, FlowDetail totalFlowDetail, Timestamp startTime, Timestamp endTime) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        do {
            long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(tmpTime);
            if (weekframe[0] < startTime.getTime()) {
                weekframe[0] = startTime.getTime();
            }
            if (weekframe[1] > endTime.getTime()) {
                weekframe[1] = endTime.getTime();
            }

            FlowDetail weekFlowDetail = new FlowDetail();
            String statTime = this.yMdFORMAT.get().format(new Timestamp(weekframe[0]));
            if (weekframe[1] != weekframe[0]) {
                statTime += " - " + this.yMdFORMAT.get().format(new Timestamp(weekframe[1]));
            }
            weekFlowDetail.setStatTime(statTime);
            weekFlowDetail.setAvgPv(0);
            weekFlowDetail.setAvgVisitTime(0);
            weekFlowDetail.setBounceRate(0);
            weekFlowDetail.setNewUvRate(0.0f);
            weekFlowDetail.setPvRate(0.0f);
            int totalVisitTime = 0;
            int totalBounceCount = 0;

            for (int i = j; i < flowTrendbydateList.size(); i++) {
                FlowTrendbydate flowTrendbydate = flowTrendbydateList.get(i);
                Timestamp statDate = flowTrendbydate.getStatDate();
                long lStatDate = statDate.getTime();
                if (lStatDate >= weekframe[0] && lStatDate <= weekframe[1]) {
                    weekFlowDetail.setPv(weekFlowDetail.getPv() + flowTrendbydate.getPv());
                    weekFlowDetail.setIpCount(weekFlowDetail.getIpCount() + flowTrendbydate.getIpCount());
                    weekFlowDetail.setVisitCount(weekFlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
                    weekFlowDetail.setUv(weekFlowDetail.getUv() + flowTrendbydate.getUv());
                    weekFlowDetail.setNewUv(weekFlowDetail.getNewUv() + flowTrendbydate.getNewUv());
                    weekFlowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
                    weekFlowDetail.setVisitTime(weekFlowDetail.getVisitTime() + flowTrendbydate.getVisitTime());
                    totalVisitTime += flowTrendbydate.getVisitTime();
                    totalBounceCount += flowTrendbydate.getBounceCount();
                    j++;
                }
                if (lStatDate > weekframe[1]) {
                    break;
                }
            }

            if (weekFlowDetail.getVisitCount() > 0) {
                float avgPv = weekFlowDetail.getPv() * 1.0f / weekFlowDetail.getVisitCount();
                weekFlowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                float avgVisitTime = totalVisitTime * 1.0f / weekFlowDetail.getVisitCount();
                weekFlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                float bounceRate = totalBounceCount * 1.0f / weekFlowDetail.getVisitCount();
                weekFlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
            }
            if (weekFlowDetail.getUv() > 0) {
                float newUvRate = weekFlowDetail.getNewUv() * 1.0f / weekFlowDetail.getUv();
                weekFlowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
            if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                float pvRate = weekFlowDetail.getPv() * 1.0f / totalFlowDetail.getPv();
                weekFlowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
            }
            flowDetailList.add(weekFlowDetail);
            tmpTime = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return flowDetailList;
    }

    private List<FlowDetail> getFlowTrendByMonth(List<FlowTrendbydate> flowTrendbydateList, FlowDetail totalFlowDetail, Timestamp startTime, Timestamp endTime) {
        List<FlowDetail> flowDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        do {
            long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(tmpTime);
            if (monthframe[0] < startTime.getTime()) {
                monthframe[0] = startTime.getTime();
            }
            if (monthframe[1] > endTime.getTime()) {
                monthframe[1] = endTime.getTime();
            }

            FlowDetail monthlowDetail = new FlowDetail();
            String statTime = this.yMdFORMAT.get().format(new Timestamp(monthframe[0]));
            if (monthframe[1] != monthframe[0]) {
                statTime += " - " + this.yMdFORMAT.get().format(new Timestamp(monthframe[1]));
            }
            monthlowDetail.setStatTime(statTime);
            monthlowDetail.setAvgPv(0);
            monthlowDetail.setAvgVisitTime(0);
            monthlowDetail.setBounceRate(0);
            monthlowDetail.setNewUvRate(0.0f);
            monthlowDetail.setPvRate(0.0f);
            int totalVisitTime = 0;
            int totalBounceCount = 0;

            for (int i = j; i < flowTrendbydateList.size(); i++) {
                FlowTrendbydate flowTrendbydate = flowTrendbydateList.get(i);
                Timestamp statDate = flowTrendbydate.getStatDate();
                long lStatDate = statDate.getTime();
                if (lStatDate >= monthframe[0] && lStatDate <= monthframe[1]) {
                    monthlowDetail.setPv(monthlowDetail.getPv() + flowTrendbydate.getPv());
                    monthlowDetail.setIpCount(monthlowDetail.getIpCount() + flowTrendbydate.getIpCount());
                    monthlowDetail.setVisitCount(monthlowDetail.getVisitCount() + flowTrendbydate.getVisitCount());
                    monthlowDetail.setUv(monthlowDetail.getUv() + flowTrendbydate.getUv());
                    monthlowDetail.setNewUv(monthlowDetail.getNewUv() + flowTrendbydate.getNewUv());
                    monthlowDetail.setChannel(LibType.getName(flowTrendbydate.getLib()));
                    monthlowDetail.setVisitTime(monthlowDetail.getVisitTime() + flowTrendbydate.getVisitTime());
                    totalVisitTime += flowTrendbydate.getVisitTime();
                    totalBounceCount += flowTrendbydate.getBounceCount();
                    j++;
                }
                if (lStatDate > monthframe[1]) {
                    break;
                }
            }

            if (monthlowDetail.getVisitCount() > 0) {
                float avgPv = monthlowDetail.getPv() * 1.0f / monthlowDetail.getVisitCount();
                monthlowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

                float avgVisitTime = totalVisitTime * 1.0f / monthlowDetail.getVisitCount();
                monthlowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

                float bounceRate = totalBounceCount * 1.0f / monthlowDetail.getVisitCount();
                monthlowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
            }
            if (monthlowDetail.getUv() > 0) {
                float newUvRate = monthlowDetail.getNewUv() * 1.0f / monthlowDetail.getUv();
                monthlowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUvRate*100)));
            }
            if (totalFlowDetail != null && totalFlowDetail.getPv() > 0) {
                float pvRate = monthlowDetail.getPv() * 1.0f / totalFlowDetail.getPv();
                monthlowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
            }
            flowDetailList.add(monthlowDetail);
            tmpTime = new Timestamp(monthframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return flowDetailList;
    }

    private String buildSummaryWhere(BaseSummaryRequest baseSummaryRequest, MapSqlParameterSource paramMap) {
        return buildSummaryWhere(baseSummaryRequest, paramMap, true, true);
    }

    private String buildSummaryWhere(BaseSummaryRequest baseSummaryRequest, MapSqlParameterSource paramMap, boolean needFilterVisitorType) {
        return buildSummaryWhere(baseSummaryRequest, paramMap, needFilterVisitorType, true);
    }

    private String buildSummaryWhere(BaseSummaryRequest baseSummaryRequest, MapSqlParameterSource paramMap, boolean needFilterVisitorType, boolean needFilterArea) {
        String where = "";
        where = buildChannelFilter(baseSummaryRequest.getChannel(), paramMap, where);
        where = buildStatDateStartFilter(baseSummaryRequest.getStartTime(), paramMap, where, baseSummaryRequest.getTimeType());
        where = buildStatDateEndFilter(baseSummaryRequest.getEndTime(), paramMap, where, baseSummaryRequest.getTimeType());
        where = buildProjectNameFilter(baseSummaryRequest.getProjectName(), paramMap, where);
        if (needFilterArea) {
            where = buildCountryFilter(new ArrayList<>(), paramMap, where);
            where = buildProvinceFilter(new ArrayList<>(), paramMap, where);
        }
        if (needFilterVisitorType) {
            where = buildVisitorTypeFilter("", paramMap, where);
        }
        return where;
    }

    private FlowDetail assemblyFlowDetail(BaseDetailbydate baseDetailbydate, BaseDetailbydate totalBaseDetailbydate) {
        FlowDetail flowDetail = new FlowDetail();
        flowDetail.setVisitTime(baseDetailbydate.getVisitTime());
        flowDetail.setPv(baseDetailbydate.getPv());
        flowDetail.setIpCount(baseDetailbydate.getIpCount());
        flowDetail.setVisitCount(baseDetailbydate.getVisitCount());
        flowDetail.setUv(baseDetailbydate.getUv());
        flowDetail.setNewUv(baseDetailbydate.getNewUv());
        flowDetail.setChannel(LibType.getName(baseDetailbydate.getLib()));
        flowDetail.setAvgPv(0);
        flowDetail.setAvgVisitTime(0);
        flowDetail.setBounceRate(0);
        flowDetail.setPvRate(0.0f);
        flowDetail.setVisitCountRate(0.0f);
        flowDetail.setUvRate(0.0f);
        flowDetail.setNewUvRate(0.0f);
        flowDetail.setIpCountRate(0.0f);
        flowDetail.setVisitTimeRate(0.0f);
        flowDetail.setExitRate(0.23f);
        
        if (flowDetail.getVisitCount() > 0) {

            float avgPv = baseDetailbydate.getPv() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv)));

            float avgVisitTime = baseDetailbydate.getVisitTime() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime)));

            float bounceRate = baseDetailbydate.getBounceCount() * 1.0f / baseDetailbydate.getVisitCount();
            flowDetail.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));

        }
        if (totalBaseDetailbydate != null) {
            if (totalBaseDetailbydate.getPv() > 0) {
                float pvRate = baseDetailbydate.getPv() * 1.0f / totalBaseDetailbydate.getPv();
                flowDetail.setPvRate(Float.parseFloat(decimalFormat.get().format(pvRate*100)));
            }
            if (totalBaseDetailbydate.getVisitCount() > 0) {
                float visitCountRate = baseDetailbydate.getVisitCount() * 1.0f / totalBaseDetailbydate.getVisitCount();
                flowDetail.setVisitCountRate(Float.parseFloat(decimalFormat.get().format(visitCountRate*100)));
            }
            if (totalBaseDetailbydate.getUv() > 0) {
                float uvRate = baseDetailbydate.getUv() * 1.0f / totalBaseDetailbydate.getUv();
                flowDetail.setUvRate(Float.parseFloat(decimalFormat.get().format(uvRate*100)));
            }
            if (totalBaseDetailbydate.getNewUv() > 0) {
                float newUrRate = baseDetailbydate.getNewUv() * 1.0f / totalBaseDetailbydate.getUv();
                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUrRate*100)));
            }
            if (totalBaseDetailbydate.getIpCount() > 0) {
                float ipCountRate = baseDetailbydate.getIpCount() * 1.0f / totalBaseDetailbydate.getIpCount();
                flowDetail.setIpCountRate(Float.parseFloat(decimalFormat.get().format(ipCountRate*100)));
            }
            if (totalBaseDetailbydate.getVisitTime() > 0) {
                float visitTimeRate = baseDetailbydate.getVisitTime() * 1.0f / totalBaseDetailbydate.getVisitTime();
                flowDetail.setVisitTimeRate(Float.parseFloat(decimalFormat.get().format(visitTimeRate*100)));
            }
        }
        return flowDetail;
    }

    private FlowSummary assemblyFlowSummary(FlowSummarybydate flowSummarybydate) {
        FlowSummary flowSummary = new FlowSummary();
        flowSummary.setPv(flowSummarybydate.getPv());
        flowSummary.setIpCount(flowSummarybydate.getIpCount());
        flowSummary.setVisitCount(flowSummarybydate.getVisitCount());
        flowSummary.setUv(flowSummarybydate.getUv());
        flowSummary.setAvgPv(0F);
        flowSummary.setAvgVisitTime(0F);
        flowSummary.setBounceRate(0F);
        flowSummary.setChannel(flowSummarybydate.getLib());
        if (flowSummarybydate.getVisitCount() > 0) {
            float avgPv = flowSummarybydate.getPv() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv*100)));

            float avgVisitTime = flowSummarybydate.getVisitTime() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime*100)));

            float bounceRate = flowSummarybydate.getBounceCount() * 1.0f / flowSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
        }
        return flowSummary;
    }

    private FlowSummary assemblyFlowSummary(VisitorSummarybydate visitorSummarybydate) {
        FlowSummary flowSummary = new FlowSummary();
        flowSummary.setPv(visitorSummarybydate.getPv());
        flowSummary.setIpCount(visitorSummarybydate.getIpCount());
        flowSummary.setVisitCount(visitorSummarybydate.getVisitCount());
        flowSummary.setUv(visitorSummarybydate.getUv());
        flowSummary.setAvgPv(0F);
        flowSummary.setAvgVisitTime(0F);
        flowSummary.setBounceRate(0F);
        flowSummary.setChannel(visitorSummarybydate.getLib());
        if (visitorSummarybydate.getVisitCount() > 0) {
            float avgPv = visitorSummarybydate.getPv() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgPv(Float.parseFloat(decimalFormat.get().format(avgPv*100)));

            float avgVisitTime = visitorSummarybydate.getVisitTime() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setAvgVisitTime(Float.parseFloat(decimalFormat.get().format(avgVisitTime*100)));

            float bounceRate = visitorSummarybydate.getBounceCount() * 1.0f / visitorSummarybydate.getVisitCount();
            flowSummary.setBounceRate(Float.parseFloat(decimalFormat.get().format(bounceRate*100)));
        }
        return flowSummary;
    }

    private String buildStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateEndFilter(_endTime, paramMap, where, "day");
    }

    private String buildStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp endTime = transformFilterTime(_endTime, false, timeType);
        where += " and t.stat_date<=:endtime";
        paramMap.addValue("endtime", this.yMdFORMAT.get().format(endTime));
        return where;
    }

    private String buildStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateStartFilter(_startTime, paramMap, where, "day");
    }

    private String buildStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t.stat_date>=:starttime";
        paramMap.addValue("starttime", this.yMdFORMAT.get().format(startTime));
        return where;
    }
    
    private String buildStatDateStartLessThanFilter(String _startTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateStartLessThanFilter(_startTime, paramMap, where, "day");
    }

    private String buildStatDateStartLessThanFilter(String _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t.stat_date<:starttimeLessThan";
        paramMap.addValue("starttimeLessThan", this.yMdFORMAT.get().format(startTime));
        return where;
    }

    private String buildProvinceFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
        if (provinceList == null) {
            provinceList = new ArrayList<>();
        }

        if (provinceList.isEmpty()) {
            provinceList.add("all");
        }
        where += " and t.province in (:province)";
        paramMap.addValue("province", provinceList);
        return where;
    }
    
    private String buildProvinceByAllFilter(List<String> provinceList, MapSqlParameterSource paramMap, String where) {
    	if (provinceList != null && !provinceList.isEmpty()) {
    		where += " and t.province in (:province)";
            paramMap.addValue("province", provinceList);
        }
        return where;
    }

    private String buildCountryFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList == null) {
            countryList = new ArrayList<>();
        }

        if (countryList.isEmpty()) {
            countryList.add("all");
        }
        where += " and t.country in (:country)";
        paramMap.addValue("country", countryList);
        return where;
    }
    
    private String buildCountryByAllFilter(List<String> countryList, MapSqlParameterSource paramMap, String where) {
        if (countryList != null && !countryList.isEmpty()) {
        	where += " and t.country in (:country)";
            paramMap.addValue("country", countryList);
        }
        return where;
    }

    private String buildVisitorTypeFilter(String visitorType, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(visitorType)) {
            if ("老访客".equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if ("新访客".equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }

        } else {
            visitorType = "all";
        }
        where += " and t.is_first_day=:is_first_day";
        paramMap.addValue("is_first_day", visitorType);
        return where;
    }
    
    private String buildVisitorTypeByAllFilter(String visitorType, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isNotBlank(visitorType)) {
            if ("老访客".equalsIgnoreCase(visitorType)) {
                visitorType = "false";
            } else if ("新访客".equalsIgnoreCase(visitorType)) {
                visitorType = "true";
            }
            where += " and t.is_first_day=:is_first_day";
            paramMap.addValue("is_first_day", visitorType);
        }
        return where;
    }

    private String buildProjectNameFilter(String projectName, MapSqlParameterSource paramMap, String where) {
        if (StringUtils.isBlank(projectName)) {
            projectName = clklogApiSetting.getProjectName();
        }
        where += " and t.project_name=:project";
        paramMap.addValue("project", projectName);
        return where;
    }

    private String buildChannelFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = new ArrayList<>();
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
        }
        if (channelList.isEmpty()) {
            channelList.add("all");
        }
        where += " and t.lib in (:channel)";
        paramMap.addValue("channel", channelList);
        return where;
    }
    
    private String buildChannelByAllFilter(List<String> channels, MapSqlParameterSource paramMap, String where) {
        List<String> channelList = new ArrayList<>();
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                LibType libType = LibType.parse(channel);
                if (libType != null) {
                    channelList.add(libType.getValue());
                }
            }
            where += " and t.lib in (:channel)";
            paramMap.addValue("channel", channelList);
        }
        return where;
    }

    private Timestamp transformFilterTime(String time, boolean isStart, String timeType) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        now = Timestamp.valueOf(this.yMdFORMAT.get().format(now) + " 00:00:00");
        Timestamp timestamp = now;
        if ("week".equalsIgnoreCase(timeType)) {
            long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(weekframe[0]);
            } else {
                timestamp = new Timestamp(weekframe[1]);
            }
        } else if ("month".equalsIgnoreCase(timeType)) {

            long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(monthframe[0]);
            } else {
                timestamp = new Timestamp(monthframe[1]);
            }
        } else if ("year".equalsIgnoreCase(timeType)) {
            long[] monthframe = TimeUtils.getCurrentYearTimeFrame(now);
            if (isStart) {
                timestamp = new Timestamp(monthframe[0]);
            } else {
                timestamp = new Timestamp(monthframe[1]);
            }
        }

        if (timestamp.getTime() > now.getTime()) {
            timestamp = now;
        }

        if (StringUtils.isNotBlank(time)) {
            timestamp = Timestamp.valueOf(time + " 00:00:00");
            if (timestamp.getTime() > now.getTime()) {
                timestamp = now;
            }
//            if (timestamp.getTime() <= constsDataHolder.getStartStatDate().getTime()) {
//                timestamp = constsDataHolder.getStartStatDate();
//            }
        }
        return timestamp;
    }

    private TimeFrame getPreviousTimeframe(String startTime, String timeType) {
        Timestamp start = transformFilterTime(startTime, true, timeType);
        start = new Timestamp(start.getTime() - DateUtils.MILLIS_PER_DAY);
        Timestamp end = null;
        if ("day".equalsIgnoreCase(timeType)) {
            end = start;
        } else if ("week".equalsIgnoreCase(timeType)) {
            long[] prev = TimeUtils.getCurrentWeekTimeFrame(start);
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        } else if ("month".equalsIgnoreCase(timeType)) {
            long[] prev = TimeUtils.getCurrentMonthTimeFrame(start);
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        } else if ("year".equalsIgnoreCase(timeType)) {
            long[] prev = TimeUtils.getCurrentYearTimeFrame(start);
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        }

        TimeFrame timeFrame = new TimeFrame();
        timeFrame.setStartTime(start);
        timeFrame.setEndTime(end);

        return timeFrame;
    }

    private TimeFrame getSamePeriodTimeframe(String startTime, String timeType) {
        TimeFrame timeFrame = null;
        Timestamp start = transformFilterTime(startTime, true, timeType);
        Timestamp end = null;
        if ("day".equalsIgnoreCase(timeType)) {
            start = new Timestamp(start.getTime() - DateUtils.MILLIS_PER_DAY * 7);
            end = start;
        } else if ("month".equalsIgnoreCase(timeType)) {
            Calendar month = Calendar.getInstance();
            month.setTime(start);
            Calendar first = Calendar.getInstance();
            first.set(Calendar.YEAR, month.get(Calendar.YEAR) - 1);
            first.set(Calendar.MONTH, month.get(Calendar.MONTH));
            first.set(Calendar.DATE, 1);
            first.set(Calendar.HOUR_OF_DAY, 0);
            first.set(Calendar.MINUTE, 0);
            first.set(Calendar.SECOND, 0);
            first.set(Calendar.MILLISECOND, 0);
            long[] prev = TimeUtils.getCurrentMonthTimeFrame(new Timestamp(first.getTimeInMillis()));
            start = new Timestamp(prev[0]);
            end = new Timestamp(prev[1]);
        }
        if (end != null) {
            timeFrame = new TimeFrame();
            timeFrame.setStartTime(start);
            timeFrame.setEndTime(end);
        }
        return timeFrame;
    }
    
    private String isFirstDayToConvert(String isFirstDay) {
    	if("true".equalsIgnoreCase(isFirstDay)){
    		return "新访客";
    	} else if("false".equalsIgnoreCase(isFirstDay)){
    		return "老访客";
    	} else if("all".equalsIgnoreCase(isFirstDay)){
    		return "全部";
    	} else {
    		return isFirstDay;
    	}
    }
    
    private String extractHost(String url) {
        Pattern pattern = Pattern.compile("(http|https)://(www.)?(\\w+(\\.)?)+");
        String host = "";
        Matcher hostMatcher = pattern.matcher(url);
        if (hostMatcher.find()) {
            host = hostMatcher.group();
        }
        return host;
    }
}
