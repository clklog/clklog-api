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
import com.zcunsoft.clklog.api.entity.clickhouse.VisitorChunrAndRemainModel;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriDownpvDetailbydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriEntryDetailbydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriExitDetailbydate;
import com.zcunsoft.clklog.api.entity.clickhouse.VisituriDetailbydate;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseChurn;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseChurnData;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseRemain;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseRemainData;
import com.zcunsoft.clklog.api.models.visitor.GetVisitorBaseTrend;
import com.zcunsoft.clklog.api.models.visituri.VisitUriDetail;
import com.zcunsoft.clklog.api.models.visituri.VisitUriDetailTrend;
import com.zcunsoft.clklog.api.utils.TimeUtils;

import io.micrometer.core.instrument.util.StringUtils;

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
            
            
    /**
	public static List<GetVisitorBaseRemainData> getVisitorChunrAndRemainTrendByDate(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebydateList, Timestamp startTime, Timestamp endTime) {
        List<GetVisitorBaseRemainData> visitorDetailList = new ArrayList<>();

        Timestamp tmpTime = startTime;
        do {
        	GetVisitorBaseRemainData visitorTrendDetail = new GetVisitorBaseRemainData();
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
            visitorDetailList.add(visitorTrendDetail);
            tmpTime = new Timestamp(tmpTime.getTime() + DateUtils.MILLIS_PER_DAY);
        }
        while (tmpTime.getTime() <= endTime.getTime());
        return visitorDetailList;
    }
	
	public static List<GetVisitorBaseRemainData>  getVisitorChunrAndRemainTrendByWeek(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyWeekList, Timestamp startTime, Timestamp endTime) {
    	List<GetVisitorBaseRemainData> visitorDetailList = new ArrayList<>();

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

            GetVisitorBaseRemainData weekVisitorTrendDetail = new GetVisitorBaseRemainData();
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
                if (lStatDate == weekframe[0]) {
                	int remain = visitorActivebyWeek.getActiveCount()-visitorActivebyWeek.getNewCount();
                	int churn = 0;
                	float remainRate =  0;
                	float churnRate = 0;
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
	
	public static List<GetVisitorBaseRemainData> getVisitorChunrAndRemainTrendByMonth(List<VisitorBaseSummarybydate> visitorBaseSummaryDateList,List<VisitorBasebydate> visitorActivebyMonthList, Timestamp startTime, Timestamp endTime) {
        List<GetVisitorBaseRemainData> visitorDetailList = new ArrayList<>();

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

            GetVisitorBaseRemainData monthVisitorTrendDetail = new GetVisitorBaseRemainData();
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
                if (lStatDate == monthframe[0]) {
                	int remain = visitorActivebyMonth.getActiveCount()-visitorActivebyMonth.getNewCount();
                	int churn = 0;
                	float remainRate =  0;
                	float churnRate = 0;
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
	*/
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
    
	public static List<GetVisitorBaseRemain> getVisitorBaseRemainList(VisitorChunrAndRemainModel visitorChunrAndRemainModel,String timeType) {
		
		List<GetVisitorBaseRemain> getVisitorRemainList = new ArrayList<GetVisitorBaseRemain>();
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol0(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol1(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol2(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol3(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol4(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol5(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol6(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol7(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol8(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol9(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol10(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol11(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol12(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol13(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol14(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol15(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol16(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol17(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol18(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol19(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol20(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol21(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol22(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol23(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol24(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol25(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol26(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol27(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol28(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol29(),timeType);
		getVisitorRemainList = getVisitorBaseRemain(getVisitorRemainList,visitorChunrAndRemainModel.getCol30(),timeType);
//		if (!"day".equalsIgnoreCase(timeType)) {
//			getVisitorRemainList = getVisitorBaseChurnAndRemain(getVisitorRemainList,visitorChunrAndRemainModel.getLatestCol());
//        }
		return getVisitorRemainList;
	}
	
	public static List<GetVisitorBaseRemain> getVisitorBaseRemain(List<GetVisitorBaseRemain> getVisitorRemainList,String col,String timeType) {
		GetVisitorBaseRemain getVisitorRemain = new GetVisitorBaseRemain();
		if(StringUtils.isBlank(col)) {
			return getVisitorRemainList;
		}
		System.out.println(col);
		String[] datas = col.substring(1, col.length()-1).split(",");
		if(datas.length <2) {
			return getVisitorRemainList;
		}
		Timestamp time = null;
		String statTime = datas[0];
		if("day".equals(timeType)) {
			time = new Timestamp(Timestamp.valueOf((datas[0]+" 00:00:00")).getTime());
		} else if("week".equals(timeType)) {
			long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(Timestamp.valueOf(statTime + " 00:00:00"));
			if (weekframe[1] != weekframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(weekframe[1]));
            }
			time = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
		} else if("month".equals(timeType)) {
			long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(Timestamp.valueOf(statTime + " 00:00:00"));
			if (monthframe[1] != monthframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(monthframe[1]));
            }
			time = new Timestamp(monthframe[1] + + DateUtils.MILLIS_PER_DAY);
		}
		int uvCount = Integer.valueOf(datas[1].trim());
		getVisitorRemain.setRawStatTime(statTime);
		getVisitorRemain.setRawUvCount(uvCount);
		List<GetVisitorBaseRemainData> rows = new ArrayList<>();
		for(int i=2;i<datas.length;i++) {
			if(StringUtils.isEmpty(datas[i]) || datas[i].trim().length() == 0) {
				continue;
			}
			int remain = Integer.valueOf(datas[i].trim());
        	float remainRate =  0;
        	if(uvCount > 0) {
        		remainRate = remain * 1.0f / uvCount;
        	}
        	GetVisitorBaseRemainData getVisitorBaseRemainData = new GetVisitorBaseRemainData();
        	getVisitorBaseRemainData.setRemainRate(Float.parseFloat(decimalFormat.get().format(remainRate)));
        	getVisitorBaseRemainData.setRemainCount(remain);
        	if("day".equals(timeType)) {
        		time = new Timestamp(time.getTime() + DateUtils.MILLIS_PER_DAY);
        		getVisitorBaseRemainData.setStatTime(yMdFORMAT.get().format(time));
        	} else if("week".equals(timeType)) {
        		long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(time);
        		getVisitorBaseRemainData.setStatTime(yMdFORMAT.get().format(new Timestamp(weekframe[0])) + "~" + yMdFORMAT.get().format(new Timestamp(weekframe[1])));
        		time = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
        	} else if("month".equals(timeType)) {
        		long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(time);
        		getVisitorBaseRemainData.setStatTime(yMdFORMAT.get().format(new Timestamp(monthframe[0])) + "~" + yMdFORMAT.get().format(new Timestamp(monthframe[1])));
        		time = new Timestamp(monthframe[1] + DateUtils.MILLIS_PER_DAY);
        	}
        	rows.add(getVisitorBaseRemainData);
		}
		getVisitorRemain.setRows(rows);
//		getVisitorChurnAndRemain.setStatTime(statTime);
		getVisitorRemainList.add(getVisitorRemain);
		return getVisitorRemainList;
	}
	
	
	public static List<GetVisitorBaseChurn> getVisitorBaseChurnList(VisitorChunrAndRemainModel visitorChunrAndRemainModel,String timeType) {
		
		List<GetVisitorBaseChurn> getVisitorChurnList = new ArrayList<GetVisitorBaseChurn>();
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol0(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol1(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol2(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol3(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol4(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol5(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol6(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol7(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol8(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol9(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol10(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol11(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol12(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol13(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol14(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol15(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol16(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol17(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol18(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol19(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol20(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol21(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol22(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol23(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol24(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol25(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol26(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol27(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol28(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol29(),timeType);
		getVisitorChurnList = getVisitorBaseChrun(getVisitorChurnList,visitorChunrAndRemainModel.getCol30(),timeType);
	//	if (!"day".equalsIgnoreCase(timeType)) {
	//		getVisitorChurnList = getVisitorBaseChurnAndRemain(getVisitorChurnList,visitorChunrAndRemainModel.getLatestCol());
	//    }
		return getVisitorChurnList;
	}
	
	public static List<GetVisitorBaseChurn> getVisitorBaseChrun(List<GetVisitorBaseChurn> getVisitorChurnList,String col,String timeType) {
		GetVisitorBaseChurn getVisitorChurn = new GetVisitorBaseChurn();
		if(StringUtils.isBlank(col)) {
			return getVisitorChurnList;
		}
		System.out.println(col);
		String[] datas = col.substring(1, col.length()-1).split(",");
		if(datas.length <2) {
			return getVisitorChurnList;
		}
		Timestamp time = null;
		String statTime = datas[0];
		if("day".equals(timeType)) {
			time = new Timestamp(Timestamp.valueOf((datas[0]+" 00:00:00")).getTime());
		} else if("week".equals(timeType)) {
			long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(Timestamp.valueOf(statTime + " 00:00:00"));
			if (weekframe[1] != weekframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(weekframe[1]));
            }
			time = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
		} else if("month".equals(timeType)) {
			long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(Timestamp.valueOf(statTime + " 00:00:00"));
			if (monthframe[1] != monthframe[0]) {
                statTime += " ~ " + yMdFORMAT.get().format(new Timestamp(monthframe[1]));
            }
			time = new Timestamp(monthframe[1] + + DateUtils.MILLIS_PER_DAY);
		}
		
		int uvCount = Integer.valueOf(datas[1].trim());
		getVisitorChurn.setRawStatTime(statTime);
		getVisitorChurn.setRawUvCount(uvCount);
		List<GetVisitorBaseChurnData> rows = new ArrayList<>();
		for(int i=2;i<datas.length;i++) {
			if(StringUtils.isEmpty(datas[i]) || datas[i].trim().length() == 0) {
				continue;
			}
			int churn = Integer.valueOf(datas[i].trim());
        	float churnRate = 0;
        	if(uvCount > 0) {
        		churnRate = churn * 1.0f / uvCount;
        	}
        	GetVisitorBaseChurnData getVisitorBaseChurnData = new GetVisitorBaseChurnData();
        	getVisitorBaseChurnData.setChurnRate(Float.parseFloat(decimalFormat.get().format(churnRate)));
        	getVisitorBaseChurnData.setChurnCount(churn);
        	if("day".equals(timeType)) {
        		time = new Timestamp(time.getTime() + DateUtils.MILLIS_PER_DAY);
        		getVisitorBaseChurnData.setStatTime(yMdFORMAT.get().format(time));
        	} else if("week".equals(timeType)) {
        		long[] weekframe = TimeUtils.getCurrentWeekTimeFrame(time);
        		getVisitorBaseChurnData.setStatTime(yMdFORMAT.get().format(new Timestamp(weekframe[0])) + "~" + yMdFORMAT.get().format(new Timestamp(weekframe[1])));
        		time = new Timestamp(weekframe[1] + DateUtils.MILLIS_PER_DAY);
        	} else if("month".equals(timeType)) {
        		long[] monthframe = TimeUtils.getCurrentMonthTimeFrame(time);
        		getVisitorBaseChurnData.setStatTime(yMdFORMAT.get().format(new Timestamp(monthframe[0])) + "~" + yMdFORMAT.get().format(new Timestamp(monthframe[1])));
        		time = new Timestamp(monthframe[1] + DateUtils.MILLIS_PER_DAY);
        	}
        	rows.add(getVisitorBaseChurnData);
		}
		getVisitorChurn.setRows(rows);
		getVisitorChurnList.add(getVisitorChurn);
		return getVisitorChurnList;
	}
}
