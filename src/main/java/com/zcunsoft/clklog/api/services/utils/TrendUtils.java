package com.zcunsoft.clklog.api.services.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;

import com.zcunsoft.clklog.api.entity.clickhouse.VisitorBaseSummarybydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisitorBasebydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriDetailDownpvbydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriDetailEntrybydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriDetailExitbydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriDetailbydate;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseChurnAndRemainData;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseTrend;
import com.zcunsoft.clklog.api.models.visituri.VisitUriDetail;
import com.zcunsoft.clklog.api.models.visituri.VisitUriDetailTrend;
import com.zcunsoft.clklog.api.utils.TimeUtils;

public class TrendUtils {
	
	private static final ThreadLocal<DateFormat> yMdFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    
    private static final ThreadLocal<DecimalFormat> decimalFormat =
            new ThreadLocal<DecimalFormat>() {
                @Override
                protected DecimalFormat initialValue() {
                    return new DecimalFormat("0.####");
                }
            };

	public static List<GetVisitorBaseChurnAndRemainData> getVisitorChunrAndRemainTrendByDate(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebydateList, Timestamp startTime, Timestamp endTime) {
        List<GetVisitorBaseChurnAndRemainData> visitorDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        do {
        	GetVisitorBaseChurnAndRemainData visitorTrendDetail = new GetVisitorBaseChurnAndRemainData();
        	visitorTrendDetail.setStatTime(yMdFORMAT.get().format(tmpTime));
            Timestamp statDate = tmpTime;
            int uvCount = 0;
            for(VisitorBasebydate visitorActivebydate : visitorActivebydateList) {
            	if(visitorActivebydate.getStatDate().getTime() == TimeUtils.getCurrentYesterday(statDate)) {
            		uvCount = visitorActivebydate.getActiveCount();
            	}
            	if(visitorActivebydate.getStatDate().equals(statDate)) {
            		int remain = visitorActivebydate.getActiveCount()-visitorActivebydate.getNewCount();
                	int churn = 0;
                	float remainRate =  0;
                	float churnRate = 0;
                	
//                	for(VisitorBaseSummarybydate visitorBaseSummarybydate : visitorBaseSummaryDateList) {
//                		if(visitorBaseSummarybydate.getStatDate().getTime() == statDate.getTime()) {
//                			uvCount = visitorBaseSummarybydate.getUv();
//                		}
//                	}
                	if(uvCount > 0) {
                		churn = uvCount-remain;
                		remainRate = remain * 1.0f / uvCount;
                		churnRate = churn * 1.0f / uvCount;
                	}
                	visitorTrendDetail.setRemainRate(Float.parseFloat(decimalFormat.get().format(remainRate)));
                	visitorTrendDetail.setChurnRate(Float.parseFloat(decimalFormat.get().format(churnRate)));
                	visitorTrendDetail.setRemainCount(remain);
                	visitorTrendDetail.setChurnCount(churn);
                	visitorTrendDetail.setNewCount(visitorActivebydate.getNewCount());
                	visitorTrendDetail.setActiveCount(visitorActivebydate.getActiveCount());
//                	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
                	visitorTrendDetail.setUvCount(uvCount);
            	}
            	
            }
            /**
            Optional<VisitorBasebydate> optionalVisitorActivebydate = visitorActivebydateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalVisitorActivebydate.isPresent()) {
            	VisitorBasebydate visitorActivebydate = optionalVisitorActivebydate.get();
            	int remain = visitorActivebydate.getActiveCount()-visitorActivebydate.getNewCount();
            	int churn = 0;
            	float remainRate =  0;
            	float churnRate = 0;
            	
//            	for(VisitorBaseSummarybydate visitorBaseSummarybydate : visitorBaseSummaryDateList) {
//            		if(visitorBaseSummarybydate.getStatDate().getTime() == statDate.getTime()) {
//            			uvCount = visitorBaseSummarybydate.getUv();
//            		}
//            	}
            	if(uvCount > 0) {
            		churn = uvCount-remain;
            		remainRate = remain * 1.0f / uvCount;
            		churnRate = churn * 1.0f / uvCount;
            	}
            	visitorTrendDetail.setRemainRate(Float.parseFloat(decimalFormat.get().format(remainRate)));
            	visitorTrendDetail.setChurnRate(Float.parseFloat(decimalFormat.get().format(churnRate)));
            	visitorTrendDetail.setRemainCount(remain);
            	visitorTrendDetail.setChurnCount(churn);
            	visitorTrendDetail.setNewCount(visitorActivebydate.getNewCount());
            	visitorTrendDetail.setActiveCount(visitorActivebydate.getActiveCount());
//            	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
            	visitorTrendDetail.setUvCount(uvCount);
            }
            */
            visitorDetailList.add(visitorTrendDetail);
            tmpTime = new Timestamp(tmpTime.getTime() + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
	
	public static List<GetVisitorBaseChurnAndRemainData>  getVisitorChunrAndRemainTrendByWeek(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyWeekList, Timestamp startTime, Timestamp endTime) {
    	List<GetVisitorBaseChurnAndRemainData> visitorDetailList = new ArrayList<>();

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

            GetVisitorBaseChurnAndRemainData weekVisitorTrendDetail = new GetVisitorBaseChurnAndRemainData();
            String statTime = yMdFORMAT.get().format(new Timestamp(weekframe[0]));
            if (weekframe[1] != weekframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(weekframe[1]));
            }
            weekVisitorTrendDetail.setStatTime(statTime);
            int uvCount = 0;
            for (int i = 0; i < visitorActivebyWeekList.size(); i++) {
            	VisitorBasebydate visitorActivebyMonth = visitorActivebyWeekList.get(i);
                Timestamp statDate = visitorActivebyMonth.getStatDate();
                long lStatDate = statDate.getTime();
                long oneMonthAgoDate = TimeUtils.getGetOneWeekAgoDateByTimestamp(new Timestamp(weekframe[0]));
                if(lStatDate == oneMonthAgoDate) {
                	uvCount = visitorActivebyMonth.getActiveCount();
                }
            }
            for (int i = j; i < visitorActivebyWeekList.size(); i++) {
            	VisitorBasebydate visitorActivebyWeek = visitorActivebyWeekList.get(i);
                Timestamp statDate = visitorActivebyWeek.getStatDate();
                long lStatDate = statDate.getTime();
//                long oneWeekAgoDate = TimeUtils.getGetOneWeekAgoDateByTimestamp(new Timestamp(weekframe[0]));
//                if(lStatDate == oneWeekAgoDate) {
//                	uvCount = visitorActivebyWeek.getActiveCount();
//                }
                if (lStatDate == weekframe[0]) {
                	int remain = visitorActivebyWeek.getActiveCount()-visitorActivebyWeek.getNewCount();
                	int churn = 0;
                	float remainRate =  0;
                	float churnRate = 0;
//                	for(VisitorBaseSummarybydate visitorBaseSummarybydate : visitorBaseSummaryDateList) {
//                		if(visitorBaseSummarybydate.getStatDate().getTime() == weekframe[1]) {
//                			uvCount = visitorBaseSummarybydate.getUv();
//                		}
//                	}
                	if(uvCount > 0) {
                		churn = uvCount-remain;
                		remainRate = remain * 1.0f / uvCount;
                		churnRate = churn * 1.0f / uvCount;
                	}
                	weekVisitorTrendDetail.setRemainRate(Float.parseFloat(decimalFormat.get().format(remainRate)));
                	weekVisitorTrendDetail.setChurnRate(Float.parseFloat(decimalFormat.get().format(churnRate)));
                	weekVisitorTrendDetail.setRemainCount(remain);
                	weekVisitorTrendDetail.setChurnCount(churn);
                	weekVisitorTrendDetail.setNewCount(visitorActivebyWeek.getNewCount());
                	weekVisitorTrendDetail.setActiveCount(visitorActivebyWeek.getActiveCount());
//                	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
                	weekVisitorTrendDetail.setUvCount(uvCount);
                    j++;
                }
                if (lStatDate > weekframe[1]) {
                    break;
                }
            }
            if(weekVisitorTrendDetail.getNewCount() == 0) {
            	weekVisitorTrendDetail.setChurnCount(uvCount);
            	weekVisitorTrendDetail.setRemainRate(Float.parseFloat(decimalFormat.get().format(0)));
            	weekVisitorTrendDetail.setChurnRate(Float.parseFloat(decimalFormat.get().format(1)));
            	weekVisitorTrendDetail.setUvCount(uvCount);
            }
            visitorDetailList.add(weekVisitorTrendDetail);
            tmpTime = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
	
	public static List<GetVisitorBaseChurnAndRemainData> getVisitorChunrAndRemainTrendByMonth(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyMonthList, Timestamp startTime, Timestamp endTime) {
        List<GetVisitorBaseChurnAndRemainData> visitorDetailList = new ArrayList<>();

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

            GetVisitorBaseChurnAndRemainData monthVisitorTrendDetail = new GetVisitorBaseChurnAndRemainData();
            String statTime = yMdFORMAT.get().format(new Timestamp(monthframe[0]));
            if (monthframe[1] != monthframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(monthframe[1]));
            }
            monthVisitorTrendDetail.setStatTime(statTime);
            
            int uvCount = 0;
            for (int i = 0; i < visitorActivebyMonthList.size(); i++) {
            	VisitorBasebydate visitorActivebyMonth = visitorActivebyMonthList.get(i);
                Timestamp statDate = visitorActivebyMonth.getStatDate();
                long lStatDate = statDate.getTime();
                long oneMonthAgoDate = TimeUtils.getCurrentPreviousMonthFirstDay(new Timestamp(monthframe[0]));
                if(lStatDate == oneMonthAgoDate) {
                	uvCount = visitorActivebyMonth.getActiveCount();
                }
            }
            for (int i = j; i < visitorActivebyMonthList.size(); i++) {
            	VisitorBasebydate visitorActivebyMonth = visitorActivebyMonthList.get(i);
                Timestamp statDate = visitorActivebyMonth.getStatDate();
                long lStatDate = statDate.getTime();
//                long oneMonthAgoDate = TimeUtils.getCurrentPreviousMonthFirstDay(new Timestamp(monthframe[0]));
//                if(lStatDate == oneMonthAgoDate) {
//                	uvCount = visitorActivebyMonth.getActiveCount();
//                }
                if (lStatDate == monthframe[0]) {
                	int remain = visitorActivebyMonth.getActiveCount()-visitorActivebyMonth.getNewCount();
                	int churn = 0;
                	float remainRate =  0;
                	float churnRate = 0;
//                	for(VisitorBaseSummarybydate visitorBaseSummarybydate : visitorBaseSummaryDateList) {
//                		if(visitorBaseSummarybydate.getStatDate().getTime() == monthframe[1]) {
//                			uvCount = visitorBaseSummarybydate.getUv();
//                		}
//                	}
                	if(uvCount > 0) {
                		churn = uvCount-remain;
                		remainRate = remain * 1.0f / uvCount;
                		churnRate = churn * 1.0f / uvCount;
                	}
                	monthVisitorTrendDetail.setRemainRate(Float.parseFloat(decimalFormat.get().format(remainRate)));
                	monthVisitorTrendDetail.setChurnRate(Float.parseFloat(decimalFormat.get().format(churnRate)));
                	monthVisitorTrendDetail.setRemainCount(remain);
                	monthVisitorTrendDetail.setChurnCount(churn);
                	monthVisitorTrendDetail.setNewCount(visitorActivebyMonth.getNewCount());
                	monthVisitorTrendDetail.setActiveCount(visitorActivebyMonth.getActiveCount());
//                	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
                	monthVisitorTrendDetail.setUvCount(uvCount);
                    j++;
                }
                if (lStatDate > monthframe[1]) {
                    break;
                }
            }
            
            if(monthVisitorTrendDetail.getNewCount() == 0) {
            	monthVisitorTrendDetail.setChurnCount(uvCount);
            	monthVisitorTrendDetail.setRemainRate(Float.parseFloat(decimalFormat.get().format(0)));
            	monthVisitorTrendDetail.setChurnRate(Float.parseFloat(decimalFormat.get().format(1)));
            	monthVisitorTrendDetail.setUvCount(uvCount);
            }
            
            visitorDetailList.add(monthVisitorTrendDetail);
            tmpTime = new Timestamp(monthframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
	
	public static List<GetVisitorBaseTrend> getVisitorBaseTrendByDate(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyDateList,List<VisitorBasebydate> visitorRevisitByDateList,List<VisitorBasebydate> visitorContinuousActiveByDateList, Timestamp startTime, Timestamp endTime) {
        List<GetVisitorBaseTrend> visitorDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        do {
        	GetVisitorBaseTrend visitorTrendDetail = new GetVisitorBaseTrend();
        	visitorTrendDetail.setStatTime(yMdFORMAT.get().format(tmpTime));
            Timestamp statDate = tmpTime;

            Optional<VisitorBasebydate> optionalVisitorActivebydate = visitorActivebyDateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalVisitorActivebydate.isPresent()) {
            	VisitorBasebydate visitorLifeSummarybydate = optionalVisitorActivebydate.get();
//            	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
            	visitorTrendDetail.setActiveCount(visitorLifeSummarybydate.getActiveCount());
            	visitorTrendDetail.setNewCount(visitorLifeSummarybydate.getNewCount());
            	visitorTrendDetail.setHistoryCount(visitorLifeSummarybydate.getActiveCount()-visitorLifeSummarybydate.getNewCount());;
//            	visitorTrendDetail.setUvCount(distinctCount);
            }
            
            Optional<VisitorBaseSummarybydate> optionalVisitorBaseSummarybydate = visitorBaseSummaryDateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalVisitorBaseSummarybydate.isPresent()) {
            	VisitorBaseSummarybydate visitorBaseSummarybydate = optionalVisitorBaseSummarybydate.get();
            	visitorTrendDetail.setUvCount(visitorBaseSummarybydate.getUv());
            }
            
            Optional<VisitorBasebydate> optionalVisitorRevisitbydate = visitorRevisitByDateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalVisitorRevisitbydate.isPresent()) {
            	VisitorBasebydate visitorRevisitbydate = optionalVisitorRevisitbydate.get();
            	visitorTrendDetail.setRevisitCount(visitorRevisitbydate.getRevisitCount());
            }
            
            Optional<VisitorBasebydate> optionalVisitorContinuousActivebydate = visitorContinuousActiveByDateList.stream().filter(f -> f.getStatDate().equals(statDate)).findAny();
            if (optionalVisitorContinuousActivebydate.isPresent()) {
            	VisitorBasebydate visitorContinuousActivebydate = optionalVisitorContinuousActivebydate.get();
            	long lYesterday = TimeUtils.getCurrentYesterday(statDate);
            	Integer lYesterdayDistinctCount = 0;
            	for(VisitorBasebydate visitorActivebydate : visitorActivebyDateList) {
            		if(visitorActivebydate.getStatDate().getTime() == lYesterday) {
            			lYesterdayDistinctCount = visitorActivebydate.getActiveCount();
            		}           
            	}
            	Integer continuousActiveCount = visitorContinuousActivebydate.getContinuousActiveCount() == null ? 0 : visitorContinuousActivebydate.getContinuousActiveCount();
            	visitorTrendDetail.setSilentCount(lYesterdayDistinctCount == null ? 0 : lYesterdayDistinctCount-continuousActiveCount);
            }
            
            visitorDetailList.add(visitorTrendDetail);
            tmpTime = new Timestamp(tmpTime.getTime() + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
    
	public static List<GetVisitorBaseTrend>  getVisitorBaseTrendByWeek(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyWeekList,List<VisitorBasebydate> visitorRevisitByWeekList,List<VisitorBasebydate> visitorContinuousActiveByWeekList, Timestamp startTime, Timestamp endTime) {
    	List<GetVisitorBaseTrend> visitorDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        int k = 0;
        int m = 0;
        do {
            long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(tmpTime);
            if (weekframe[0] < startTime.getTime()) {
                weekframe[0] = startTime.getTime();
            }
            if (weekframe[1] > endTime.getTime()) {
                weekframe[1] = endTime.getTime();
            }

            GetVisitorBaseTrend weekVisitorTrendDetail = new GetVisitorBaseTrend();
            String statTime = yMdFORMAT.get().format(new Timestamp(weekframe[0]));
            if (weekframe[1] != weekframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(weekframe[1]));
            }
            weekVisitorTrendDetail.setStatTime(statTime);
            for (int i = j; i < visitorActivebyWeekList.size(); i++) {
            	VisitorBasebydate visitorActivebyWeek = visitorActivebyWeekList.get(i);
                Timestamp statDate = visitorActivebyWeek.getStatDate();
                long lStatDate = statDate.getTime();
                if (lStatDate == weekframe[0]) {
//                	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
                	weekVisitorTrendDetail.setActiveCount(visitorActivebyWeek.getActiveCount());
                	weekVisitorTrendDetail.setNewCount(visitorActivebyWeek.getNewCount());
                	weekVisitorTrendDetail.setHistoryCount(visitorActivebyWeek.getActiveCount()-visitorActivebyWeek.getNewCount());
//                	weekVisitorTrendDetail.setUvCount(distinctCount);
                	for(VisitorBaseSummarybydate visitorBaseSummarybydate : visitorBaseSummaryDateList) {
                		if(visitorBaseSummarybydate.getStatDate().getTime() == weekframe[1]) {
                			weekVisitorTrendDetail.setUvCount(visitorBaseSummarybydate.getUv());
                		}
                	}
                    j++;
                }
                if (lStatDate > weekframe[1]) {
                    break;
                }
            }
            if(visitorRevisitByWeekList != null) {
            	for (int i = k; i < visitorRevisitByWeekList.size(); i++) {
                	VisitorBasebydate vvisitorRevisitByWeek = visitorRevisitByWeekList.get(i);
                    Timestamp statDate = vvisitorRevisitByWeek.getStatDate();
                    long lStatDate = statDate.getTime();
                    if (lStatDate == weekframe[0]) {
                    	weekVisitorTrendDetail.setRevisitCount(vvisitorRevisitByWeek.getRevisitCount());
                        k++;
                    }
                    if (lStatDate > weekframe[1]) {
                        break;
                    }
                }
            }
            if(visitorContinuousActiveByWeekList != null) {
            	for (int i = m; i < visitorContinuousActiveByWeekList.size(); i++) {
                	VisitorBasebydate visitorContinuousActiveByWeek = visitorContinuousActiveByWeekList.get(i);
                    Timestamp statDate = visitorContinuousActiveByWeek.getStatDate();
                    long lStatDate = statDate.getTime();
                    if (lStatDate == weekframe[0]) {
                    	long oneWeekAgo = TimeUtils.getGetOneWeekAgoDateByTimestamp(statDate);
                    	Integer oneAgoActiveCount = 0;
                    	for(VisitorBasebydate visitorActivebyWeek : visitorActivebyWeekList) {
                    		if(visitorActivebyWeek.getStatDate().getTime() == oneWeekAgo) {
                    			oneAgoActiveCount = visitorActivebyWeek.getActiveCount();
                    		}           
                    	}
                    	Integer continuousActiveCount = visitorContinuousActiveByWeek.getContinuousActiveCount() == null ? 0 : visitorContinuousActiveByWeek.getContinuousActiveCount();
                    	weekVisitorTrendDetail.setSilentCount(oneAgoActiveCount == null ? 0 : oneAgoActiveCount-continuousActiveCount);
                        m++;
                    }
                    if (lStatDate > weekframe[1]) {
                        break;
                    }
                }
            }
            visitorDetailList.add(weekVisitorTrendDetail);
            tmpTime = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
    
    public static List<GetVisitorBaseTrend> getVisitorBaseTrendByMonth(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyMonthList,List<VisitorBasebydate> visitorRevisitByMonthList,List<VisitorBasebydate> visitorContinuousActiveByMonthList, Timestamp startTime, Timestamp endTime) {
        List<GetVisitorBaseTrend> visitorDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        int j = 0;
        int k = 0;
        int m = 0;
        do {
            long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(tmpTime);
            if (monthframe[0] < startTime.getTime()) {
                monthframe[0] = startTime.getTime();
            }
            if (monthframe[1] > endTime.getTime()) {
                monthframe[1] = endTime.getTime();
            }

            GetVisitorBaseTrend monthVisitorTrendDetail = new GetVisitorBaseTrend();
            String statTime = yMdFORMAT.get().format(new Timestamp(monthframe[0]));
            if (monthframe[1] != monthframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(monthframe[1]));
            }
            monthVisitorTrendDetail.setStatTime(statTime);
            

            for (int i = j; i < visitorActivebyMonthList.size(); i++) {
            	VisitorBasebydate visitorActivebyMonth = visitorActivebyMonthList.get(i);
                Timestamp statDate = visitorActivebyMonth.getStatDate();
                long lStatDate = statDate.getTime();
                if (lStatDate == monthframe[0]) {
//                	distinctCount = distinctCount + visitorLifeSummarybydate.getNewCount();
                	monthVisitorTrendDetail.setActiveCount(visitorActivebyMonth.getActiveCount());
                	monthVisitorTrendDetail.setNewCount(visitorActivebyMonth.getNewCount());
                	monthVisitorTrendDetail.setHistoryCount(visitorActivebyMonth.getActiveCount()-visitorActivebyMonth.getNewCount());
//                	monthVisitorTrendDetail.setUvCount(distinctCount);
                	for(VisitorBaseSummarybydate visitorBaseSummarybydate : visitorBaseSummaryDateList) {
                		if(visitorBaseSummarybydate.getStatDate().getTime() == monthframe[1]) {
                			monthVisitorTrendDetail.setUvCount(visitorBaseSummarybydate.getUv());
                		}
                	}
                    j++;
                }
                if (lStatDate > monthframe[1]) {
                    break;
                }
            }
            if(visitorRevisitByMonthList != null) {
            	for (int i = k; i < visitorRevisitByMonthList.size(); i++) {
                	VisitorBasebydate visitorRevisitByMonth = visitorRevisitByMonthList.get(i);
                    Timestamp statDate = visitorRevisitByMonth.getStatDate();
                    long lStatDate = statDate.getTime();
                    if (lStatDate == monthframe[0]) {
                    	monthVisitorTrendDetail.setRevisitCount(visitorRevisitByMonth.getRevisitCount());
                        k++;
                    }
                    if (lStatDate > monthframe[1]) {
                        break;
                    }
                }
            }
            
            if(visitorContinuousActiveByMonthList != null) {
            	for (int i = m; i < visitorContinuousActiveByMonthList.size(); i++) {
                	VisitorBasebydate visitorContinuousActiveByMonth = visitorContinuousActiveByMonthList.get(i);
                    Timestamp statDate = visitorContinuousActiveByMonth.getStatDate();
                    long lStatDate = statDate.getTime();
                    if (lStatDate == monthframe[0]) {
                    	long oneMonthAgo = TimeUtils.getCurrentPreviousMonthFirstDay(statDate);
                    	Integer oneAgoActiveCount = 0;
                    	for(VisitorBasebydate visitorActivebyMonth : visitorActivebyMonthList) {
                    		if(visitorActivebyMonth.getStatDate().getTime() == oneMonthAgo) {
                    			oneAgoActiveCount = visitorActivebyMonth.getActiveCount();
                    		}           
                    	}
                    	Integer continuousActiveCount = visitorContinuousActiveByMonth.getContinuousActiveCount() == null ? 0 : visitorContinuousActiveByMonth.getContinuousActiveCount();
                    	monthVisitorTrendDetail.setSilentCount(oneAgoActiveCount == null ? 0 : oneAgoActiveCount-continuousActiveCount);
                        m++;
                    }
                    if (lStatDate > monthframe[1]) {
                        break;
                    }
                }
            }
            
            
            visitorDetailList.add(monthVisitorTrendDetail);
            tmpTime = new Timestamp(monthframe[1] + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
    
	
}
