package com.zcunsoft.clklog.api.services;

import com.zcunsoft.clklog.api.cfg.ClklogApiSetting;
import com.zcunsoft.clklog.api.entity.clickhouse.*;
import com.zcunsoft.clklog.api.entity.clickhouse.accesslog.Accesslogbydate;
import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;
import com.zcunsoft.clklog.api.models.TimeFrame;
import com.zcunsoft.clklog.api.models.accesslog.AccesslogFlowDetail;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogHostResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogOverviewResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageRequest;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageResponse;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogPageResponseData;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogRequest;
import com.zcunsoft.clklog.api.models.accesslog.GetAccesslogResponse;
import com.zcunsoft.clklog.api.models.enums.LibType;
import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import com.zcunsoft.clklog.api.models.summary.FlowSummary;
import com.zcunsoft.clklog.api.models.summary.GetFlowRequest;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AccesslogReportServiceImpl implements AccesslogIReportService {

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
    
    private static final List<String> hostList;
    static {
    	hostList = new ArrayList<String>() {
 			{
 				add("huoqingqing.com");
 				add("h5.huoqingqing.com");
 				add("app.huoqingqing.com");
 				add("group.huoqingqing.com");
 			}
 		};
    }

    private final ClklogApiSetting clklogApiSetting;

    private final ConstsDataHolder constsDataHolder;

    public AccesslogReportServiceImpl(NamedParameterJdbcTemplate clickHouseJdbcTemplate, ClklogApiSetting clklogApiSetting, ConstsDataHolder constsDataHolder) {
        this.clickHouseJdbcTemplate = clickHouseJdbcTemplate;
        this.clklogApiSetting = clklogApiSetting;
        this.constsDataHolder = constsDataHolder;
    }

	private static final ThreadLocal<DecimalFormat> decimalFormat = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			return new DecimalFormat("0.####");
		}
	};

	
	
	@Override
	public GetAccesslogHostResponse getHost() {
		GetAccesslogHostResponse response = new GetAccesslogHostResponse();
		response.setData(hostList);
		return response;
	}
	
	

	@Override
	public GetAccesslogResponse getHostOverview(GetAccesslogRequest getAccesslogRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "select host as host,count(1) as pv,sum(request_length) as request_length,sum(body_bytes_sent) as body_bytes_sent,countDistinct(x_forward_for) as ip_count,sum(request_time) as visit_time from nginx_access t ";
		String getSummarySql ="select sum(t1.pv) as pv,sum(t1.request_length) as request_length,sum(t1.body_bytes_sent) as body_bytes_sent,sum(t1.ip_count) as ip_count,sum(t1.visit_time) as visit_time  from (";
		String where = "";
        where = buildHostFilter(getAccesslogRequest.getHost(), paramMap, where);
        where = buildStatDateStartFilter(getAccesslogRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogRequest.getEndTime(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getListSql += " where " + where.substring(4);
		}
//		getListSql += " group by t.host order by pv desc ";
		getListSql += " group by host " + getSortSqlFormat(getAccesslogRequest.getSortName(), getAccesslogRequest.getSortOrder(), "pv");
		getSummarySql += getListSql +") t1";
		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		
		Accesslogbydate totalAccesslogbydate = clickHouseJdbcTemplate.queryForObject(getSummarySql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		List<AccesslogFlowDetail> accesslogDetailList = new ArrayList<>();
//		Timestamp startTime = transformFilterTime(getAccesslogRequest.getStartTime(), true, getAccesslogRequest.getTimeType());
//        Timestamp endTime = transformFilterTime(getAccesslogRequest.getEndTime(), false, getAccesslogRequest.getTimeType());
//        if ("hour".equalsIgnoreCase(getAccesslogRequest.getTimeType())) {
//            flowDetailList = getFlowTrendByHour(paramMap, totalFlowDetail, where);
//        } else if ("day".equalsIgnoreCase(getAccesslogRequest.getTimeType())) {
//            flowDetailList = getFlowTrendByDate(flowTrendbydateList, totalFlowDetail, startTime, endTime);
//        } else if ("week".equalsIgnoreCase(getAccesslogRequest.getTimeType())) {
//            flowDetailList = getFlowTrendByWeek(flowTrendbydateList, totalFlowDetail, startTime, endTime);
//        } else if ("month".equalsIgnoreCase(getAccesslogRequest.getTimeType())) {
//            flowDetailList = getFlowTrendByMonth(flowTrendbydateList, totalFlowDetail, startTime, endTime);
//        }
		
		GetAccesslogResponse response = new GetAccesslogResponse();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			AccesslogFlowDetail accesslogFlowDetail = assemblyFlowDetail(accesslogbydate, totalAccesslogbydate);
			accesslogFlowDetail.setHost(accesslogbydate.getHost());
			accesslogDetailList.add(accesslogFlowDetail);
		}
		response.setData(accesslogDetailList);
		return response;
	}
	
	

	@Override
	public GetAccesslogOverviewResponse getOverview(GetAccesslogRequest getAccesslogRequest) {
		Accesslogbydate accesslogbydate = getTotalAccesslogbydate(getAccesslogRequest);
		GetAccesslogOverviewResponse response = new GetAccesslogOverviewResponse();
		AccesslogFlowDetail accesslogFlowDetail = assemblyFlowDetail(accesslogbydate, null);
		response.setData(accesslogFlowDetail);
		return response;
	}


	private Accesslogbydate getTotalAccesslogbydate(GetAccesslogRequest getAccesslogRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "select count(1) as pv,sum(request_length) as request_length,sum(body_bytes_sent) as body_bytes_sent,countDistinct(x_forward_for) as ip_count,sum(request_time) as visit_time from nginx_access t ";
		String where = "";
		where = buildDefaultHostFilter(paramMap, where);
        where = buildStatDateStartFilter(getAccesslogRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogRequest.getEndTime(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getListSql += " where " + where.substring(4);
		}
		Accesslogbydate accesslogbydate = clickHouseJdbcTemplate.queryForObject(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		return accesslogbydate;
	}
	
	
	

	@Override
	public GetAccesslogPageResponse getPerformanceDetail(GetAccesslogPageRequest getAccesslogPageRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "SELECT t2.host as host,t2.uri as uri, t2.pv as pv, t2.slow_pv as slow_pv, t2.max_visit_time as max_visit_time, t2.avg_visit_time as avg_visit_time FROM ( ";
		String getSonSql1 = "SELECT host as host,url2 as uri, sum(if(request_time >= 1, 1, 0)) AS slow_pv,count(1) AS pv,avg(request_time) AS avg_visit_time , max(request_time) AS max_visit_time FROM nginx_access t ";
		String getCountSql = "SELECT count(1) FROM ( ";
		String where = "";
        where = buildHostFilter(getAccesslogPageRequest.getHost(), paramMap, where);
        where = buildStatDateStartFilter(getAccesslogPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogPageRequest.getEndTime(), paramMap, where);
        where = buildStatusFilter(getAccesslogPageRequest.getStatus(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getSonSql1 += " where " + where.substring(4);
		}
//		getSonSql3 += " GROUP BY host,url2 ORDER BY slow_pv DESC";
		getSonSql1 += " GROUP BY host,url2 " + getSortSqlFormat(getAccesslogPageRequest.getSortName(), getAccesslogPageRequest.getSortOrder(), "slow_pv");
		getListSql += getSonSql1  + ") t2 WHERE t2.slow_pv>0 ";
		getCountSql += getSonSql1 + ") t2 WHERE t2.slow_pv>0 ";
		getListSql += " limit " + (getAccesslogPageRequest.getPageNum() - 1) * getAccesslogPageRequest.getPageSize() + "," + getAccesslogPageRequest.getPageSize();
		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
		List<AccesslogFlowDetail> accesslogDetailList = new ArrayList<>();

		GetAccesslogPageResponse response = new GetAccesslogPageResponse();
		GetAccesslogPageResponseData responseData = new GetAccesslogPageResponseData();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			AccesslogFlowDetail accesslogFlowDetail = assemblyFlowDetail(accesslogbydate, null);
			accesslogFlowDetail.setHost(accesslogbydate.getHost());
			accesslogFlowDetail.setUri(accesslogbydate.getUri());
			accesslogFlowDetail.setAvgVisitTime(accesslogbydate.getAvgVisitTime().setScale(5,RoundingMode.DOWN));
			if (accesslogbydate.getPv() != null && accesslogbydate.getPv().compareTo(BigDecimal.ZERO)>0) {
                BigDecimal pvRate = accesslogbydate.getSlowPv().divide(accesslogbydate.getPv(),5,RoundingMode.DOWN);
                accesslogFlowDetail.setPvRate(pvRate);
            }
			accesslogDetailList.add(accesslogFlowDetail);
		}
		responseData.setRows(accesslogDetailList);
		responseData.setTotal(total);
		response.setData(responseData);
		return response;
	}

	

	@Override
	public GetAccesslogResponse getStatus(GetAccesslogRequest getAccesslogRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "select status as status,count(1) as pv ,countDistinct(url2) as uri_count from nginx_access t ";
		String where = "";
        where = buildHostFilter(getAccesslogRequest.getHost(), paramMap, where);
        where = buildStatDateStartFilter(getAccesslogRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogRequest.getEndTime(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getListSql += " where " + where.substring(4);
		}
		getListSql += " group by status " + getSortSqlFormat(getAccesslogRequest.getSortName(), getAccesslogRequest.getSortOrder(), "pv");;

		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		Accesslogbydate totalAccesslogbydate = getTotalAccesslogbydate(getAccesslogRequest);
		
		List<AccesslogFlowDetail> accesslogDetailList = new ArrayList<>();

		GetAccesslogResponse response = new GetAccesslogResponse();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			AccesslogFlowDetail accesslogFlowDetail = assemblyFlowDetail(accesslogbydate, totalAccesslogbydate);
			accesslogFlowDetail.setStatus(accesslogbydate.getStatus());
			accesslogDetailList.add(accesslogFlowDetail);
		}
		response.setData(accesslogDetailList);
		return response;
	}

	@Override
	public GetAccesslogPageResponse getStatusDetail(GetAccesslogPageRequest getAccesslogPageRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "SELECT t2.host as host,t2.uri as uri, t2.pv as pv, t2.slow_pv as slow_pv, t2.max_visit_time as max_visit_time, t2.avg_visit_time as avg_visit_time FROM ( ";
		String getSonSql1 = "SELECT host as host,url2 as uri, sum(if(request_time >= 1, 1, 0)) AS slow_pv,count(1) AS pv,avg(request_time) AS avg_visit_time , max(request_time) AS max_visit_time FROM nginx_access t ";
		String getCountSql = "SELECT count(1) FROM ( ";
		String where = "";
        where = buildHostFilter(getAccesslogPageRequest.getHost(), paramMap, where);
        where = buildStatDateStartFilter(getAccesslogPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogPageRequest.getEndTime(), paramMap, where);
        where = buildStatusFilter(getAccesslogPageRequest.getStatus(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getSonSql1 += " where " + where.substring(4);
		}
		getSonSql1 += " GROUP BY host,url2 " + getSortSqlFormat(getAccesslogPageRequest.getSortName(), getAccesslogPageRequest.getSortOrder(), "pv");
		getListSql += getSonSql1 +") t2 ";
		getCountSql += getSonSql1 + ") t2 ";
		getListSql += " limit " + (getAccesslogPageRequest.getPageNum() - 1) * getAccesslogPageRequest.getPageSize() + "," + getAccesslogPageRequest.getPageSize();
		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
		List<AccesslogFlowDetail> accesslogDetailList = new ArrayList<>();

		GetAccesslogPageResponse response = new GetAccesslogPageResponse();
		GetAccesslogPageResponseData responseData = new GetAccesslogPageResponseData();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			AccesslogFlowDetail accesslogFlowDetail = assemblyFlowDetail(accesslogbydate, null);
			accesslogFlowDetail.setHost(accesslogbydate.getHost());
			accesslogFlowDetail.setUri(accesslogbydate.getUri());
			accesslogFlowDetail.setAvgVisitTime(accesslogbydate.getAvgVisitTime().setScale(5,RoundingMode.DOWN));
			if (accesslogbydate.getPv() != null && accesslogbydate.getPv().compareTo(BigDecimal.ZERO)>0) {
                BigDecimal pvRate = accesslogbydate.getSlowPv().divide(accesslogbydate.getPv(),5,RoundingMode.DOWN);
                accesslogFlowDetail.setPvRate(pvRate);
            }
			accesslogDetailList.add(accesslogFlowDetail);
		}
		responseData.setRows(accesslogDetailList);
		responseData.setTotal(total);
		response.setData(responseData);
		return response;
	}
	
	@Override
	public GetAccesslogPageResponse getIpDetail(GetAccesslogPageRequest getAccesslogPageRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "SELECT x_forward_for as ip ,count(1) AS pv,avg(request_time) AS avg_visit_time , sum(request_time) AS visit_time,max(_time_datepart) latest_time,countDistinct(url2) as uri_count FROM nginx_access t ";
		String getCountSql = "SELECT count(1) from (SELECT x_forward_for as ip FROM nginx_access t ";
		String where = "";
        where = buildHostFilter(getAccesslogPageRequest.getHost(), paramMap, where);
        where = buildStatDateStartFilter(getAccesslogPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogPageRequest.getEndTime(), paramMap, where);
        where = buildStatusFilter(getAccesslogPageRequest.getStatus(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getListSql += " where " + where.substring(4);
			getCountSql += " where " + where.substring(4);
		}
		getCountSql += " GROUP BY t.x_forward_for) t";
		getListSql += " GROUP BY x_forward_for " + getSortSqlFormat(getAccesslogPageRequest.getSortName(), getAccesslogPageRequest.getSortOrder(), "latest_time");
		getListSql += " limit " + (getAccesslogPageRequest.getPageNum() - 1) * getAccesslogPageRequest.getPageSize() + "," + getAccesslogPageRequest.getPageSize();
		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
		
		GetAccesslogRequest getAccesslogRequest = new GetAccesslogRequest();
		BeanUtils.copyProperties(getAccesslogPageRequest, getAccesslogRequest);
		Accesslogbydate totalAccesslogbydate = getTotalAccesslogbydate(getAccesslogRequest);
		List<AccesslogFlowDetail> accesslogDetailList = new ArrayList<>();

		GetAccesslogPageResponse response = new GetAccesslogPageResponse();
		GetAccesslogPageResponseData responseData = new GetAccesslogPageResponseData();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			AccesslogFlowDetail accesslogFlowDetail = assemblyFlowDetail(accesslogbydate, totalAccesslogbydate);
			accesslogFlowDetail.setIp(accesslogbydate.getIp());
			accesslogFlowDetail.setLatestTime(accesslogbydate.getLatestTime());
			accesslogFlowDetail.setAvgVisitTime(accesslogbydate.getAvgVisitTime().setScale(5,RoundingMode.DOWN));
			accesslogDetailList.add(accesslogFlowDetail);
		}
		responseData.setRows(accesslogDetailList);
		responseData.setTotal(total);
		response.setData(responseData);
		return response;
	}
	
	@Override
	public GetAccesslogPageResponse getUriDetailByIp(GetAccesslogPageRequest getAccesslogPageRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String getListSql = "SELECT host as host,x_forward_for as ip ,url2 as uri,request_time AS visit_time , _time_ as log_time FROM nginx_access t ";
		String getCountSql = "SELECT count(1) FROM nginx_access t ";
		String where = "";
        where = buildHostFilter(getAccesslogPageRequest.getHost(), paramMap, where);
        where = buildStatDateStartFilter(getAccesslogPageRequest.getStartTime(), paramMap, where);
        where = buildStatDateEndFilter(getAccesslogPageRequest.getEndTime(), paramMap, where);
        where = buildStatusFilter(getAccesslogPageRequest.getStatus(), paramMap, where);
        where = buildIpFilter(getAccesslogPageRequest.getIp(), paramMap, where);
		if (StringUtils.isNotBlank(where)) {
			getListSql += " where " + where.substring(4);
			getCountSql += " where " + where.substring(4);
		}
//		getListSql += " ORDER BY _time_ ASC";
		getListSql += getSortSqlFormat(getAccesslogPageRequest.getSortName(), getAccesslogPageRequest.getSortOrder(), "_time_");
		getListSql += " limit " + (getAccesslogPageRequest.getPageNum() - 1) * getAccesslogPageRequest.getPageSize() + "," + getAccesslogPageRequest.getPageSize();
		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));
		Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);
		
		List<AccesslogFlowDetail> accesslogDetailList = new ArrayList<>();

		GetAccesslogPageResponse response = new GetAccesslogPageResponse();
		GetAccesslogPageResponseData responseData = new GetAccesslogPageResponseData();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			AccesslogFlowDetail accesslogFlowDetail = new AccesslogFlowDetail();
			accesslogFlowDetail.setIp(accesslogbydate.getIp());
			accesslogFlowDetail.setUri(accesslogbydate.getUri());
			accesslogFlowDetail.setVisitTime(accesslogbydate.getVisitTime());
			accesslogFlowDetail.setLogTime(accesslogbydate.getLogTime());
			accesslogFlowDetail.setHost(accesslogbydate.getHost());
			accesslogDetailList.add(accesslogFlowDetail);
		}
		responseData.setRows(accesslogDetailList);
		responseData.setTotal(total);
		response.setData(responseData);
		return response;
	}
	
	private static String getSortSqlFormat(String sortName,String sortOrder,String defalutSortName) {
    	
    	if("p".equals(sortName)) {
    		return " ORDER BY if(visit_count == 0 , 0 ,pv/visit_count) "+ sortOrder;
    	}
    	if("logTime".equals(sortName)) {
    		return " ORDER BY _time_ "+ sortOrder;
    	}
    	if("visitTime".equals(sortName)) {
    		return " ORDER BY visit_time "+ sortOrder;
    	}
    	if("pv".equals(sortName)) {
    		return " ORDER BY pv "+ sortOrder;
    	}
    	if("requestLength".equals(sortName)) {
    		return " ORDER BY request_length "+ sortOrder;
    	}
    	if("bodyBytesSent".equals(sortName)) {
    		return " ORDER BY body_bytes_sent "+ sortOrder;
    	}
		if("ipCount".equals(sortName)) {
			return " ORDER BY ip_count "+ sortOrder;
		}
		if("slowPv".equals(sortName)) {
			return " ORDER BY slow_pv "+ sortOrder;
		}
		if("maxVisitTime".equals(sortName)) {
			return " ORDER BY max_visit_time "+ sortOrder;
		}
		if("avgVisitTime".equals(sortName)) {
			return " ORDER BY avg_visit_time "+ sortOrder;
		}
		if("uriCount".equals(sortName)) {
			return " ORDER BY uri_count "+ sortOrder;
		}
		if("latestTime".equals(sortName)) {
			return "ORDER BY latest_time " + sortOrder;
		}
    	return " ORDER BY " +defalutSortName + " desc "; 
	}

	private AccesslogFlowDetail assemblyFlowDetail(Accesslogbydate baseDetailbydate, Accesslogbydate totalBaseDetailbydate) {
		AccesslogFlowDetail accesslogFlowDetail = new AccesslogFlowDetail();
		accesslogFlowDetail.setVisitTime(baseDetailbydate.getVisitTime() != null ? baseDetailbydate.getVisitTime().setScale(5,RoundingMode.DOWN) : BigDecimal.ZERO);
		accesslogFlowDetail.setPv(baseDetailbydate.getPv());
		accesslogFlowDetail.setIpCount(baseDetailbydate.getIpCount());
		accesslogFlowDetail.setRequestLength(baseDetailbydate.getRequestLength());
		accesslogFlowDetail.setBodyBytesSent(baseDetailbydate.getBodyBytesSent());
		accesslogFlowDetail.setSlowPv(baseDetailbydate.getSlowPv());
		accesslogFlowDetail.setMaxVisitTime(baseDetailbydate.getMaxVisitTime());
		accesslogFlowDetail.setUriCount(baseDetailbydate.getUriCount());
        if (baseDetailbydate.getIpCount() != null && baseDetailbydate.getIpCount().compareTo(BigDecimal.ZERO)>0) {
            BigDecimal avgVisitTime = baseDetailbydate.getVisitTime().divide(baseDetailbydate.getIpCount(),5,RoundingMode.DOWN);
            accesslogFlowDetail.setAvgVisitTime(avgVisitTime);
        }
        if (totalBaseDetailbydate != null) {
            if (totalBaseDetailbydate.getPv() != null && totalBaseDetailbydate.getPv().compareTo(BigDecimal.ZERO)>0) {
                BigDecimal pvRate = baseDetailbydate.getPv() == null ?  BigDecimal.ZERO : baseDetailbydate.getPv().divide(totalBaseDetailbydate.getPv(),5,RoundingMode.DOWN);
                accesslogFlowDetail.setPvRate(pvRate);
            }
            if (totalBaseDetailbydate.getRequestLength() != null && totalBaseDetailbydate.getRequestLength().compareTo(BigDecimal.ZERO)>0) {
                BigDecimal requestLengthRate = baseDetailbydate.getRequestLength() == null ? BigDecimal.ZERO : baseDetailbydate.getRequestLength().divide(totalBaseDetailbydate.getRequestLength(),5,RoundingMode.DOWN);
                accesslogFlowDetail.setRequestLengthRate(requestLengthRate);
            }
            if (totalBaseDetailbydate.getBodyBytesSent() != null && totalBaseDetailbydate.getBodyBytesSent().compareTo(BigDecimal.ZERO)>0) {
                BigDecimal bodyBytesSentRate = baseDetailbydate.getBodyBytesSent() == null ? BigDecimal.ZERO : baseDetailbydate.getBodyBytesSent().divide(totalBaseDetailbydate.getBodyBytesSent(),5,RoundingMode.DOWN);
                accesslogFlowDetail.setBodyBytesSentRate(bodyBytesSentRate);
            }
            if (totalBaseDetailbydate.getIpCount() != null && totalBaseDetailbydate.getIpCount().compareTo(BigDecimal.ZERO)>0) {
                BigDecimal ipCountRate = baseDetailbydate.getIpCount() == null ? BigDecimal.ZERO : baseDetailbydate.getIpCount().divide(totalBaseDetailbydate.getIpCount(),5,RoundingMode.DOWN);
                accesslogFlowDetail.setIpCountRate(ipCountRate);
            }
//            if (totalBaseDetailbydate.getVisitCount() > 0) {
//                float visitCountRate = baseDetailbydate.getVisitCount() * 1.0f / totalBaseDetailbydate.getVisitCount();
//                flowDetail.setVisitCountRate(Float.parseFloat(decimalFormat.get().format(visitCountRate)));
//            }
//            if (totalBaseDetailbydate.getUv() > 0) {
//                float uvRate = baseDetailbydate.getUv() * 1.0f / totalBaseDetailbydate.getUv();
//                flowDetail.setUvRate(Float.parseFloat(decimalFormat.get().format(uvRate)));
//            }
//            if (totalBaseDetailbydate.getNewUv() > 0) {
//                float newUrRate = baseDetailbydate.getNewUv() * 1.0f / totalBaseDetailbydate.getUv();
//                flowDetail.setNewUvRate(Float.parseFloat(decimalFormat.get().format(newUrRate)));
//            }
//            if (totalBaseDetailbydate.getIpCount() > 0) {
//                float ipCountRate = baseDetailbydate.getIpCount() * 1.0f / totalBaseDetailbydate.getIpCount();
//                flowDetail.setIpCountRate(Float.parseFloat(decimalFormat.get().format(ipCountRate)));
//            }
//            if (totalBaseDetailbydate.getVisitTime() > 0) {
//                float visitTimeRate = baseDetailbydate.getVisitTime() * 1.0f / totalBaseDetailbydate.getVisitTime();
//                flowDetail.setVisitTimeRate(Float.parseFloat(decimalFormat.get().format(visitTimeRate)));
//            }
        }
        return accesslogFlowDetail;
    }

	@Override
	public GetAccesslogPageResponse getAccesslogPageTest(GetAccesslogPageRequest getAccesslogPageRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String selectSql = "select t._time_datepart as time_datepart,count(1) visit_count from nginx_access t ";
		String getListSql = "" + selectSql;
//                String getSummarySql = "select " + selectSql;
		String getCountSql = "select count(1) from (select t._time_datepart as time_datepart from nginx_access t group by t._time_datepart) ";
		String where = "";
		if (StringUtils.isNotBlank(where)) {

		}
		getListSql += " group by t._time_datepart order by t._time_datepart asc ";
		getListSql += " limit " + (getAccesslogPageRequest.getPageNum() - 1) * getAccesslogPageRequest.getPageSize()
				+ "," + getAccesslogPageRequest.getPageSize();

		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));

		Integer total = clickHouseJdbcTemplate.queryForObject(getCountSql, paramMap, Integer.class);

		List<Map<String, Object>> accesslogDetailList = new ArrayList<>();

		GetAccesslogPageResponse response = new GetAccesslogPageResponse();
		GetAccesslogPageResponseData responseData = new GetAccesslogPageResponseData();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", yMdFORMAT.get().format(accesslogbydate.getTimeDatepart()));
//			map.put("visitCount", accesslogbydate.getVisitCount());
			accesslogDetailList.add(map);
		}
		responseData.setRows(null);
		responseData.setTotal(total);
		response.setData(responseData);
		return response;
	}

	@Override
	public GetAccesslogResponse getAccesslogTest(GetAccesslogRequest getAccesslogRequest) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		String selectSql = "select t._time_datepart as time_datepart,count(1) visit_count from nginx_access t ";
		String getListSql = "" + selectSql;
		String where = "";
		if (StringUtils.isNotBlank(where)) {

		}
		getListSql += " group by t._time_datepart order by t._time_datepart asc ";

		List<Accesslogbydate> accesslogbydateList = clickHouseJdbcTemplate.query(getListSql, paramMap,
				new BeanPropertyRowMapper<Accesslogbydate>(Accesslogbydate.class));

		List<Map<String, Object>> accesslogDetailList = new ArrayList<>();

		GetAccesslogResponse response = new GetAccesslogResponse();
		for (Accesslogbydate accesslogbydate : accesslogbydateList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", yMdFORMAT.get().format(accesslogbydate.getTimeDatepart()));
//			map.put("visitCount", accesslogbydate.getVisitCount());
			accesslogDetailList.add(map);
		}
		response.setData(null);
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
        flowSummary.setAvgPv(0);
        flowSummary.setAvgVisitTime(0);
        flowSummary.setBounceRate(0);
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
        flowSummary.setAvgPv(0);
        flowSummary.setAvgVisitTime(0);
        flowSummary.setBounceRate(0);
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
    
    private String buildHostFilter(String host, MapSqlParameterSource paramMap, String where) {
        where += " and t.host in (:host)";
        if(StringUtils.isNotEmpty(host)) {
        	paramMap.addValue("host", Arrays.asList(host));
    	} else {
    		paramMap.addValue("host", hostList);
    	}
        
        return where;
    }
    
    private String buildDefaultHostFilter(MapSqlParameterSource paramMap, String where) {
        where += " and t.host in (:host)";
    	paramMap.addValue("host", hostList);
        
        return where;
    }
    
    private String buildStatusFilter(String status, MapSqlParameterSource paramMap, String where) {
        if(StringUtils.isNotEmpty(status)) {
        	where += " and t.status = :status";
        	paramMap.addValue("status", status);
    	}
        return where;
    }
    
    private String buildIpFilter(String ip, MapSqlParameterSource paramMap, String where) {
        if(StringUtils.isNotEmpty(ip)) {
        	where += " and t.x_forward_for = :ip";
        	paramMap.addValue("ip", ip);
    	}
        return where;
    }

    private String buildStatDateEndFilter(String _endTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp endTime = transformFilterTime(_endTime, false, timeType);
        where += " and t._time_datepart <=:endtime";
        paramMap.addValue("endtime", this.yMdFORMAT.get().format(endTime));
        return where;
    }

    private String buildStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where) {
        return buildStatDateStartFilter(_startTime, paramMap, where, "day");
    }

    private String buildStatDateStartFilter(String _startTime, MapSqlParameterSource paramMap, String where, String timeType) {
        Timestamp startTime = transformFilterTime(_startTime, true, timeType);
        where += " and t._time_datepart >=:starttime";
        paramMap.addValue("starttime", this.yMdFORMAT.get().format(startTime));
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
    
  
    
}
