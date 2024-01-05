package com.zcunsoft.clklog.api.poi;

import com.zcunsoft.clklog.api.models.enums.DownloadColType;
import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import com.zcunsoft.clklog.api.models.uservisit.BaseUserVisit;
import com.zcunsoft.clklog.api.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 
 */
@Slf4j
@Service
public class ExcelExportUtils  {

    private static final int DETAIL_COL_INDEX = 0;//明细列索引
    private static final int DETAIL_ROW_INDEX = 0;//明细行索引
    
    private static final int TOTAL_POSITION_ROW = 1;//概括标题行索引
    private static final int TOTAL_POSITION_COL = 0;//概括列索引
    
    private static final int TOTAL_FIRST_ROW_INDEX = 0;//概括合并起始行索引
    private static final int TOTAL_LAST_ROW_INDEX = 0;//概括合并结束行索引
    private static final int TOTAL_FIRST_COL_INDEX = 0;//概括合并起始列索引
    private static final int TOTAL_ROW_INDEX = 0;//概括合并行索引
    private static final int TOTAL_COL_INDEX = 0;//概括合并列索引
    
    
    private static final ThreadLocal<DateFormat> yMdHmsFORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    
    private static final ThreadLocal<DecimalFormat> intDecimalFormat =
            new ThreadLocal<DecimalFormat>() {
                @Override
                protected DecimalFormat initialValue() {
                	DecimalFormat decimalFormat = new DecimalFormat("0");
                	decimalFormat.setRoundingMode(RoundingMode.UP);
                    return decimalFormat;
                }
            };
       
   private static final List<String> initCols;
   static {
	   initCols = new ArrayList<String>() {
			{
				add("index");
				add("sourcesite");
				add("latestReferrer");
				add("distinctId");
				add("visitorType");
				add("searchword");
				add("statTime");
				add("device");
				add("os");
				add("channel");
				add("province");
				add("uri");
				add("uriPath");
				add("title");
				add("pv");
				add("pvRate");
				add("visitCount");
				add("visitCountRate");
				add("uv");
				add("uvRate");
				add("newUv");
				add("newUvRate");
				add("ipCount");
				add("ipCountRate");
				add("bounceRate");
				add("entryCount");
				add("downPvCount");
				add("exitCount");
				add("exitRate");
				add("visitTime");
				add("avgVisitTime");
				add("visitTimeRate");
				add("avgPv");
				add("avgPvRate");
				add("latestTime");
				add("pvCount");
				add("visitTimeDesc");
				add("visitUri");
				add("rate");
			}
		};
   }
    
    /**
     * @return SXSSFWorkbook
     * @throws IOException 
     */
    public static void exportFlowTrendDetail(List<FlowDetail> detailList, FlowDetail total, List<String> cols, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","visitCount","uv","ipCount","avgPv","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"statTime");
        createDetailSheet(workbook, detailList, cols, "趋势分析","趋势流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    /**
     * @return SXSSFWorkbook
     * @throws IOException 
     */
    public static void exportSearchWordDetail(List<FlowDetail> detailList,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
    	if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"searchword");
        createDetailSheet(workbook, detailList, cols, "搜索词分析","搜索词流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportAreaDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","visitCount","uv","ipCount","avgPv","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"province");
        createDetailSheet(workbook, detailList, cols, "地域分析","地域流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportVisitUriDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","uv","downPvCount","exitCount","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        
      //detailSheet
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"uri");
        createDetailSheet(workbook, detailList, cols, "受访页面分析","受访页面流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportVisitUriEntryDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","uv","entryCount","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        
      //detailSheet
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"uri");
        createDetailSheet(workbook, detailList, cols, "受访页面入口页分析","受访页面入口页流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportVisitUriExitDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","uv","exitCount","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        
      //detailSheet
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"uri");
        createDetailSheet(workbook, detailList, cols, "受访页面退出页分析","受访页面退出页流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportVisitUriDownpvDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","uv","downPvCount","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        
      //detailSheet
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"uri");
        createDetailSheet(workbook, detailList, cols, "受访页面贡献下游页分析","受访页面贡献下游页流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportSourceWebSiteDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
    	SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("pv","visitCount","uv","ipCount","avgPv","avgVisitTime","bounceRate");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"sourcesite");
        createDetailSheet(workbook, detailList, cols, "来源网站分析","来源网站流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportVisitorDetail(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
    	SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        List<String> totalCols = Arrays.asList("uv","newUv","newUvRate","revisit","silent","churn");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
      //detailSheet
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"visitorType");
        createDetailSheet(workbook, detailList, cols, "新老访客分析","新老访客流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    /**
     * @return SXSSFWorkbook
     * @throws IOException 
     */
    public static void exportDeviceDetail(List<FlowDetail> detailList,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"device");
        createDetailSheet(workbook, detailList, cols, "设备分析","设备分流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    /**
     * @return SXSSFWorkbook
     * @throws IOException 
     */
    public static void exportChannelDetail(List<FlowDetail> detailList,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"index");
//        cols.add(1,"channel");
        createDetailSheet(workbook, detailList, cols, "渠道分析","渠道流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    public static void exportVisitorList(List<FlowDetail> detailList,FlowDetail total,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
    	SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        List<String> totalCols = Arrays.asList("uv","newUv","newUvRate","revisit","silent","churn");
        createTotalSheet(workbook, totalCols, total, "流量概览", headStyle, bodyStyle, floatStyle);
        
        //detailSheet
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
//        cols.add(0,"distinctId");
//        cols.add(1,"visitorType");
        createDetailSheet(workbook, detailList, cols, "用户行为分析","用户行为流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    
    public static void exportVisitorList(List<BaseUserVisit> pvList, List<BaseUserVisit> visitList, List<BaseUserVisit> visitTimeList,List<BaseUserVisit> visitUriList,List<BaseUserVisit> latestTimeList, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        
        //用户忠诚度-访问次数
        Sheet visitSheet = workbook.createSheet("访问次数流量指标分析");
    	String[] visitColumns = new String[visitList.size()];
        int[] visitColWidths = new int[visitList.size()];
        for(int i=0;i<visitList.size();i++) {
        	visitColumns[i] = visitList.get(i).getKey();
        	visitColWidths[i] =  3000;
        }
        int visitRowIndex = DETAIL_ROW_INDEX;
        int visitHeadColIndex =DETAIL_COL_INDEX;
        Row visitHead = visitSheet.createRow(visitRowIndex++);
        for (int i = 0; i < visitColumns.length; ++i) {
        	visitSheet.setColumnWidth(visitHeadColIndex, visitColWidths[i]);
            addCellWithStyle(visitHead, visitHeadColIndex++, headStyle).setCellValue(visitColumns[i]);
        }
        int visitColIndex = DETAIL_COL_INDEX;
        Row visitRow = visitSheet.createRow(visitRowIndex++);
        for (BaseUserVisit baseUserVisit : visitList) {
            addCellWithStyle(visitRow, visitColIndex++, bodyStyle).setCellValue(baseUserVisit.getValue());
        }
        
        //用户忠诚度-访问页数
        Sheet pvSheet = workbook.createSheet("访问页数流量指标分析");
    	String[] pvColumns = new String[pvList.size()];
        int[] pvColWidths = new int[pvList.size()];
        for(int i=0;i<pvList.size();i++) {
        	pvColumns[i] = pvList.get(i).getKey();
        	pvColWidths[i] =  3000;
        }
        int pvRowIndex = DETAIL_ROW_INDEX;
        int pvHeadColIndex =DETAIL_COL_INDEX;
        Row pvHead = pvSheet.createRow(pvRowIndex++);
        for (int i = 0; i < pvColumns.length; ++i) {
        	pvSheet.setColumnWidth(pvHeadColIndex, pvColWidths[i]);
            addCellWithStyle(pvHead, pvHeadColIndex++, headStyle).setCellValue(pvColumns[i]);
        }
        int pvColIndex = DETAIL_COL_INDEX;
        Row pvRow = pvSheet.createRow(pvRowIndex++);
        for (BaseUserVisit baseUserVisit : pvList) {
            addCellWithStyle(pvRow, pvColIndex++, bodyStyle).setCellValue(baseUserVisit.getValue());
        }
        
        //用户忠诚度-访问时长
        Sheet visitTimeSheet = workbook.createSheet("访问时长流量指标分析");
    	String[] visitTimeColumns = new String[visitTimeList.size()];
        int[] visitTimeColWidths = new int[visitTimeList.size()];
        for(int i=0;i<visitTimeList.size();i++) {
        	visitTimeColumns[i] = visitTimeList.get(i).getKey();
        	visitTimeColWidths[i] =  3000;
        }
        int visitTimeRowIndex = DETAIL_ROW_INDEX;
        int visitTimeHeadColIndex =DETAIL_COL_INDEX;
        Row visitTimeHead = visitTimeSheet.createRow(visitTimeRowIndex++);
        for (int i = 0; i < visitTimeColumns.length; ++i) {
        	visitTimeSheet.setColumnWidth(visitTimeHeadColIndex, visitTimeColWidths[i]);
            addCellWithStyle(visitTimeHead, visitTimeHeadColIndex++, headStyle).setCellValue(visitTimeColumns[i]);
        }
        int visitTimeColIndex = DETAIL_COL_INDEX;
        Row visitTimeRow = visitTimeSheet.createRow(visitTimeRowIndex++);
        for (BaseUserVisit baseUserVisit : visitTimeList) {
            addCellWithStyle(visitTimeRow, visitTimeColIndex++, bodyStyle).setCellValue(baseUserVisit.getValue());
        }
        
        //用户忠诚度-访问深度
        Sheet visitUriSheet = workbook.createSheet("访问深度流量指标分析");
    	String[] visitUriColumns = new String[visitUriList.size()];
        int[] visitUriColWidths = new int[visitUriList.size()];
        for(int i=0;i<visitUriList.size();i++) {
        	visitUriColumns[i] = visitUriList.get(i).getKey();
        	visitUriColWidths[i] =  3000;
        }
        int visitUriRowIndex = DETAIL_ROW_INDEX;
        int visitUriHeadColIndex =DETAIL_COL_INDEX;
        Row visitUriHead = visitUriSheet.createRow(visitUriRowIndex++);
        for (int i = 0; i < visitUriColumns.length; ++i) {
        	visitUriSheet.setColumnWidth(visitUriHeadColIndex, visitUriColWidths[i]);
            addCellWithStyle(visitUriHead, visitUriHeadColIndex++, headStyle).setCellValue(visitUriColumns[i]);
        }
        int visitUriColIndex = DETAIL_COL_INDEX;
        Row visitUriRow = visitUriSheet.createRow(visitUriRowIndex++);
        for (BaseUserVisit baseUserVisit : visitUriList) {
            addCellWithStyle(visitUriRow, visitUriColIndex++, bodyStyle).setCellValue(baseUserVisit.getValue());
        }
        
        //用户忠诚度-上次访问时间
        Sheet latestTimeSheet = workbook.createSheet("上次访问时间流量指标分析");
    	String[] latestTimeColumns = new String[latestTimeList.size()];
        int[] latestTimeColWidths = new int[latestTimeList.size()];
        for(int i=0;i<latestTimeList.size();i++) {
        	latestTimeColumns[i] = latestTimeList.get(i).getKey();
        	latestTimeColWidths[i] =  3000;
        }
        int latestTimeRowIndex = DETAIL_ROW_INDEX;
        int latestTimeHeadColIndex =DETAIL_COL_INDEX;
        Row latestTimeHead = latestTimeSheet.createRow(latestTimeRowIndex++);
        for (int i = 0; i < latestTimeColumns.length; ++i) {
        	latestTimeSheet.setColumnWidth(latestTimeHeadColIndex, latestTimeColWidths[i]);
            addCellWithStyle(latestTimeHead, latestTimeHeadColIndex++, headStyle).setCellValue(latestTimeColumns[i]);
        }
        int latestTimeColIndex = DETAIL_COL_INDEX;
        Row latestTimeRow = latestTimeSheet.createRow(latestTimeRowIndex++);
        for (BaseUserVisit baseUserVisit : latestTimeList) {
            addCellWithStyle(latestTimeRow, latestTimeColIndex++, bodyStyle).setCellValue(baseUserVisit.getValue());
        }
    
        
        downLoadExcel("用户忠诚度分析", workbook, request,response);
    }
    
    
    public static void createHeadRow(List<String> cols,Sheet sheet,CellStyle headStyle) {
    	cols = resetColsSort(cols);
    	String[] detailColumns = new String[cols.size()];
        int[] detailColWidths = new int[cols.size()];
        for(int i=0;i<cols.size();i++) {
        	DownloadColType colType = DownloadColType.parse(cols.get(i));
        	if(colType == null) {
        		continue;
        	}
        	detailColumns[i] = colType.getName();
        	detailColWidths[i] =  colType.getWidth();
        }

        int detailHeadColIndex =DETAIL_COL_INDEX;
        Row head = sheet.createRow(0);
        for (int i = 0; i < detailColumns.length; ++i) {
        	sheet.setColumnWidth(detailHeadColIndex, detailColWidths[i]);
            addCellWithStyle(head, detailHeadColIndex++, headStyle).setCellValue(detailColumns[i]);
        }
    }
    
    public static void exportVisitorList1(List<BaseUserVisit> pvList, List<BaseUserVisit> visitList, List<BaseUserVisit> visitTimeList,List<BaseUserVisit> visitUriList,List<BaseUserVisit> latestTimeList, HttpServletRequest request, HttpServletResponse response) throws IOException {
    	SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        
        //用户忠诚度-访问次数
        List<String> visitCols = Arrays.asList("index","visitCount","uv","rate");
        Sheet visitSheet = workbook.createSheet("访问次数流量指标分析");
        createHeadRow(visitCols, visitSheet, headStyle);
        for (int i=0;i<visitList.size();i++) {
        	BaseUserVisit baseUserVisit = visitList.get(i);
            Row row = visitSheet.createRow((i+1));
            addCellWithStyle(row, 0, bodyStyle).setCellValue((i+1));
            addCellWithStyle(row, 1, bodyStyle).setCellValue(baseUserVisit.getKey());
            addCellWithStyle(row, 2, bodyStyle).setCellValue(baseUserVisit.getValue());
            addCellWithStyle(row, 3, bodyStyle).setCellValue(String.valueOf(baseUserVisit.getRate()));
        }
        
        //用户忠诚度-访问页数
        List<String> pvCols = Arrays.asList("index","pvCount","uv","rate");
        Sheet pvSheet = workbook.createSheet("访问页数流量指标分析");
        createHeadRow(pvCols, pvSheet, headStyle);
        for (int i=0;i<pvList.size();i++) {
        	BaseUserVisit baseUserVisit = pvList.get(i);
            Row row = pvSheet.createRow((i+1));
            addCellWithStyle(row, 0, bodyStyle).setCellValue((i+1));
            addCellWithStyle(row, 1, bodyStyle).setCellValue(baseUserVisit.getKey());
            addCellWithStyle(row, 2, bodyStyle).setCellValue(baseUserVisit.getValue());
            addCellWithStyle(row, 3, bodyStyle).setCellValue(String.valueOf(baseUserVisit.getRate()));
        }
        
      //用户忠诚度-访问时长
        List<String> visitTimeCols = Arrays.asList("index","visitTimeDesc","uv","rate");
        Sheet visitTimeSheet = workbook.createSheet("访问时长流量指标分析");
        createHeadRow(visitTimeCols, visitTimeSheet, headStyle);
        for (int i=0;i<visitTimeList.size();i++) {
        	BaseUserVisit baseUserVisit = visitTimeList.get(i);
            Row row = visitTimeSheet.createRow((i+1));
            addCellWithStyle(row, 0, bodyStyle).setCellValue((i+1));
            addCellWithStyle(row, 1, bodyStyle).setCellValue(baseUserVisit.getKey());
            addCellWithStyle(row, 2, bodyStyle).setCellValue(baseUserVisit.getValue());
            addCellWithStyle(row, 3, bodyStyle).setCellValue(String.valueOf(baseUserVisit.getRate()));
        }
        
      //用户忠诚度-访问深度
        List<String> visitUriCols = Arrays.asList("index","visitUriDesc","uv","rate");
        Sheet visitUriSheet = workbook.createSheet("访问深度流量指标分析");
        createHeadRow(visitUriCols, visitUriSheet, headStyle);
        for (int i=0;i<visitUriList.size();i++) {
        	BaseUserVisit baseUserVisit = visitUriList.get(i);
            Row row = visitUriSheet.createRow((i+1));
            addCellWithStyle(row, 0, bodyStyle).setCellValue((i+1));
            addCellWithStyle(row, 1, bodyStyle).setCellValue(baseUserVisit.getKey());
            addCellWithStyle(row, 2, bodyStyle).setCellValue(baseUserVisit.getValue());
            addCellWithStyle(row, 3, bodyStyle).setCellValue(String.valueOf(baseUserVisit.getRate()));
        }
        
      //用户忠诚度-上次访问时间
        List<String> latestTimeCols = Arrays.asList("index","latestTime","uv","rate");
        Sheet latestTimeSheet = workbook.createSheet("上次访问时间流量指标分析");
        createHeadRow(latestTimeCols, latestTimeSheet, headStyle);
        for (int i=0;i<latestTimeList.size();i++) {
        	BaseUserVisit baseUserVisit = latestTimeList.get(i);
            Row row = latestTimeSheet.createRow((i+1));
            addCellWithStyle(row, 0, bodyStyle).setCellValue((i+1));
            addCellWithStyle(row, 1, bodyStyle).setCellValue(baseUserVisit.getKey());
            addCellWithStyle(row, 2, bodyStyle).setCellValue(baseUserVisit.getValue());
            addCellWithStyle(row, 3, bodyStyle).setCellValue(String.valueOf(baseUserVisit.getRate()));
        }
        
        downLoadExcel("用户忠诚度分析", workbook, request,response);
    }
    
    public static void exportOsDetail(List<FlowDetail> detailList,List<String> cols,HttpServletRequest request,HttpServletResponse response) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CellStyle headStyle = getHeadCellStyle(workbook);
        CellStyle bodyStyle = getBodyCellStyle(workbook);
        CellStyle floatStyle = getFloatCellStyle(workbook);
        if(cols == null || cols.size() ==0) {
        	cols = new ArrayList<String>();
        }
        createDetailSheet(workbook, detailList, cols, "操作系统分析","操作系统分流量指标分析", headStyle, bodyStyle, floatStyle, request, response);
    }
    
    private static void createTotalSheet(SXSSFWorkbook workbook,List<String> totalCols,FlowDetail total,String sheetName,CellStyle headStyle,CellStyle bodyStyle, CellStyle floatStyle) {
    	Sheet sheet = workbook.createSheet(sheetName);
    	//统计title
        addMergedRegion(sheet, TOTAL_FIRST_ROW_INDEX, TOTAL_LAST_ROW_INDEX, TOTAL_FIRST_COL_INDEX, totalCols.size()-1,TOTAL_ROW_INDEX,TOTAL_COL_INDEX,headStyle,sheetName);
        
        String[] totalColumns = new String[totalCols.size()];
        int[] totalColWidths = new int[totalCols.size()];
        for(int i=0;i<totalCols.size();i++) {
        	DownloadColType colType = DownloadColType.parse(totalCols.get(i));
        	if(colType == null) {
        		continue;
        	}
        	totalColumns[i] = colType.getName();
        	totalColWidths[i] =  colType.getWidth();
        }
        // 表头
        int totalRowIndex = TOTAL_POSITION_ROW;
        int totalColIndex = TOTAL_POSITION_COL;
        
        Row totalHead = sheet.createRow(totalRowIndex++);
        for (int i = 0; i < totalColumns.length; ++i) {
            sheet.setColumnWidth(totalColIndex, totalColWidths[i]);
            addCellWithStyle(totalHead, totalColIndex++, headStyle).setCellValue(totalColumns[i]);
        }
        Row totalRow = sheet.createRow(totalRowIndex++);
        totalColIndex = TOTAL_POSITION_COL;
        if(totalCols.contains("pv")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getPv());
        }
        if(totalCols.contains("visitCount")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getVisitCount());
        }
        if(totalCols.contains("uv")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getUv());
        }
        if(totalCols.contains("newUv")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getNewUv());
        }
        if(totalCols.contains("newUvRate")) {
        	addCellWithStyle(totalRow, totalColIndex++, floatStyle).setCellValue(total.getNewUvRate());
        }
        if(totalCols.contains("ipCount")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getIpCount());
        }
        
        if(totalCols.contains("downPvCount")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getDownPvCount());
        }
        
        if(totalCols.contains("exitCount")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getExitCount());
        }
        
        if(totalCols.contains("entryCount")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getEntryCount());
        }
        
        if(totalCols.contains("avgPv")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(intDecimalFormat.get().format(total.getAvgPv()));
        }
        if(totalCols.contains("avgVisitTime")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(TimeUtils.getTimeString(((long)total.getAvgVisitTime())));
        }
        if(totalCols.contains("bounceRate")) {
        	addCellWithStyle(totalRow, totalColIndex++, floatStyle).setCellValue(total.getBounceRate());
        }
        
        if(totalCols.contains("revisit")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getRevisit());
        }
        if(totalCols.contains("silent")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getSilent());
        }
        if(totalCols.contains("churn")) {
        	addCellWithStyle(totalRow, totalColIndex++, bodyStyle).setCellValue(total.getChurn());
        }
        
    }

    private static void createDetailSheet(SXSSFWorkbook workbook, List<FlowDetail> rows, List<String> cols,String fileName,String sheetName,CellStyle headStyle,CellStyle bodyStyle, CellStyle floatStyle,HttpServletRequest request,HttpServletResponse response) throws IOException {
    	cols = resetColsSort(cols);
    	Sheet detailSheet = workbook.createSheet(sheetName);
    	String[] detailColumns = new String[cols.size()];
        int[] detailColWidths = new int[cols.size()];
        
        
        for(int i=0;i<cols.size();i++) {
        	DownloadColType colType = DownloadColType.parse(cols.get(i));
        	if(colType == null) {
        		continue;
        	}
        	detailColumns[i] = colType.getName();
        	detailColWidths[i] =  colType.getWidth();
        	if("搜索词分析".equals(fileName)) {
        		if("pv".equals(cols.get(i))) {
            		detailColumns[i] = "搜索次数";
            	} 
            	if("pvRate".equals(cols.get(i))) {
            		detailColumns[i] = "搜索次数占比(%)";
            	} 
        	}
        }
        int detailRowIndex = DETAIL_ROW_INDEX;
        int detailHeadColIndex =DETAIL_COL_INDEX;
        Row head = detailSheet.createRow(detailRowIndex++);
        for (int i = 0; i < detailColumns.length; ++i) {
        	detailSheet.setColumnWidth(detailHeadColIndex, detailColWidths[i]);
            addCellWithStyle(head, detailHeadColIndex++, headStyle).setCellValue(detailColumns[i]);
        }
        int index = 1;
        for (FlowDetail flowDetail : rows) {
            Row row = detailSheet.createRow(detailRowIndex++);
            addCell(cols, row, bodyStyle, floatStyle, flowDetail, index++);
        }
        downLoadExcel(fileName, workbook, request,response);
    }
    
    private static Cell addCellWithStyle(Row row, int colPosition, CellStyle cellStyle) {
        Cell cell = row.createCell(colPosition);
        cell.setCellStyle(cellStyle);
        return cell;
    }
    
    private static void addCell(List<String> cols,Row row, CellStyle bodyStyle, CellStyle floatStyle,FlowDetail flowDetail,int index) {
    	 int detailColIndex = DETAIL_COL_INDEX;
    	 if(cols.contains("index")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(index);
    	 }
    	 if(cols.contains("sourcesite")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getSourcesite());
    	 }
    	 if(cols.contains("latestReferrer")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getLatestReferrer());
    	 }
    	 if(cols.contains("distinctId")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getDistinctId());
    	 }
    	 if(cols.contains("visitorType")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getVisitorType());
    	 }
    	 if(cols.contains("searchword")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getSearchword());
    	 }
    	 if(cols.contains("statTime")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getStatTime());
    	 }
    	 if(cols.contains("device")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getDevice());
    	 }
    	 if(cols.contains("os")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getOs());
    	 }
    	 if(cols.contains("channel")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getChannel());
    	 }
    	 if(cols.contains("province")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getProvince());
    	 }
    	 if(cols.contains("uri")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getUri());
    	 }
    	 if(cols.contains("uriPath")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getUriPath());
    	 }
    	 if(cols.contains("title")) {
    		 addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getTitle());
    	 }
    	 if(cols.contains("pv")) {
         	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getPv());
         }
         if(cols.contains("pvRate")) {
         	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getPvRate());
         }
         if(cols.contains("visitCount")) {
         	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getVisitCount());
         }
         if(cols.contains("visitCountRate")) {
        	 addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getVisitCountRate());
         }
         if(cols.contains("uv")) {
         	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getUv());
         }
         if(cols.contains("uvRate")) {
          	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getUvRate());
          }
         if(cols.contains("newUv")) {
         	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getNewUv());
         }
         if(cols.contains("newUvRate")) {
         	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getNewUvRate());
         }
         if(cols.contains("ipCount")) {
         	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getIpCount());
         }
         if(cols.contains("ipCountRate")) {
          	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getIpCountRate());
         }
         if(cols.contains("bounceRate")) {
         	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getBounceRate());
         }
         if(cols.contains("entryCount")) {
            	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getEntryCount());
           }
         if(cols.contains("downPvCount")) {
            	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getDownPvCount());
           }
         
         if(cols.contains("exitCount")) {
           	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(flowDetail.getExitCount());
         }
         
         if(cols.contains("exitRate")) {
           	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getExitRate());
          }
         
         
         if(cols.contains("visitTime")) {
           	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(TimeUtils.getTimeString(((long)flowDetail.getVisitTime())));
           }
         if(cols.contains("avgVisitTime")) {
          	addCellWithStyle(row, detailColIndex++, bodyStyle).setCellValue(TimeUtils.getTimeString(((long)flowDetail.getAvgVisitTime())));
         }
         if(cols.contains("visitTimeRate")) {
           	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(flowDetail.getVisitTimeRate());
          }
         
         if(cols.contains("avgPv")) {
         	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(intDecimalFormat.get().format(flowDetail.getAvgPv()));
         }
         if(cols.contains("avgPvRate")) {
          	addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue("");
         }
         if(cols.contains("latestTime")) {
        	 addCellWithStyle(row, detailColIndex++, floatStyle).setCellValue(yMdHmsFORMAT.get().format(flowDetail.getLatestTime()));
         }

    }
    
    private static void addMergedRegion(Sheet sheet,int firstRow,int lastRow,int firstCol,int lastCol,int rowIndex,int colIndex,CellStyle headStyle,String value) {
    	CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		sheet.addMergedRegion(cra);
		 Row row = sheet.getRow(rowIndex);
		 if(row == null) {
			 row = sheet.createRow(rowIndex);
		 }
		 addCellWithStyle(row, colIndex, headStyle).setCellValue(value);
    }

    private static CellStyle getHeadCellStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);

        // fill
//        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
     // font
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle getBodyCellStyle(Workbook workbook) {
        return getBaseCellStyle(workbook);
    }

    private static CellStyle getFloatCellStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);

        DataFormat df = workbook.createDataFormat();
        style.setDataFormat(df.getFormat("#,##0.00"));
        return style;
    }
    
    private static CellStyle getBaseCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        

        // align
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.TOP);

        // border
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return style;
    }

    private String getCellStringValue(XSSFCell cell) {
        try {
            if (null!=cell) {
                return String.valueOf(cell.getStringCellValue());
            }
        } catch (Exception e) {
            return String.valueOf(getCellIntValue(cell));
        }
        return "";
    }

    private long getCellLongValue(XSSFCell cell) {
        try {
            if (null!=cell) {
                return Long.parseLong("" + (long) cell.getNumericCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private int getCellIntValue(XSSFCell cell) {
        try {
            if (null!=cell) {
                return Integer.parseInt("" + (int) cell.getNumericCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * excel下载
     *
     * @param fileName 下载时的文件名称
     * @param response
     * @param workbook excel数据
     */
    private static void downLoadExcel(String fileName, Workbook workbook, HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
        	fileName = browserCharCodeFun(request, fileName);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
  //火狐浏览器下载文件,文件名乱码问题公共方法
    private static String browserCharCodeFun(HttpServletRequest request,String name){
  	    try {
  	    	String userAgent = request.getHeader("user-agent").toLowerCase();  
  			if (userAgent.contains("msie") || userAgent.contains("like gecko") ) {  
  			        // win10 ie edge 浏览器 和其他系统的ie  
  			    name = URLEncoder.encode(name, "UTF-8");  
  			} else {  
  			        // fe  
  			    try {
  					name = new String(name.getBytes("utf-8"), "iso-8859-1");
  				} catch (UnsupportedEncodingException e) {
  					e.printStackTrace();
  				}  
  			}  
  		} catch (UnsupportedEncodingException e) {
  			e.printStackTrace();
  		}  
  	    	return name;
  	}
    
    private static List<String> resetColsSort(List<String> cols){
    	List<String> sortCols = new ArrayList<String>();
    	for(String initCol : initCols) {
			for(String col : cols) {
				if(initCol.equalsIgnoreCase(col)) {
					sortCols.add(col);
				}
			}
		}
    	return sortCols;
    }
}