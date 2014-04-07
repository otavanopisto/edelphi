package fi.internetix.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartDataSeries;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportUtils;
import fi.internetix.edelphi.utils.MathUtils;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class ThesisTimeSerieQueryReportPage extends QueryReportPageController {

  public ThesisTimeSerieQueryReportPage() {
    super(QueryPageType.THESIS_TIME_SERIE);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, QueryReportChartContext chartContext, QueryPage queryPage) {
    // TODO: Any statistics for web page?

    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/time_serie.jsp", null);
  }
  
  @Override
  public Chart constructChart(QueryReportChartContext chartContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    
    Locale locale = LocaleUtils.toLocale(chartContext.getLocale());
    
    Double minX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.minX");
    Double maxX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.maxX");
    Double minY = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.minY");
    Double maxY = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.maxY");
    Double stepX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.stepX");
    Double userStepX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.userStepX");
    if (userStepX == null) {
      userStepX = stepX;
    }
    double stepGCD = MathUtils.getGCD(stepX, userStepX);
    
    int valueCount = new Double(Math.ceil((maxX - minX) / stepGCD)).intValue() + 1;
    
    List<String> categoryCaptions = new ArrayList<String>(valueCount);
    List<Double> preliminaryValues = new ArrayList<Double>(valueCount);
    List<Double> averageValues = new ArrayList<Double>(valueCount);
    List<Double> q1Values = new ArrayList<Double>(valueCount);
    List<Double> q3Values = new ArrayList<Double>(valueCount);
    List<Double> minValues = new ArrayList<Double>(valueCount);
    List<Double> maxValues = new ArrayList<Double>(valueCount);
    
    for (int i = 0; i < valueCount; i++) {
      categoryCaptions.add(null);
      preliminaryValues.add(null);
      averageValues.add(null);
      q1Values.add(null);
      q3Values.add(null);
      minValues.add(null);
      maxValues.add(null);
    }
    
    NavigableMap<String, String> predefinedValuesStringMap = QueryPageUtils.getMapSetting(queryPage, "time_serie.predefinedValues");
    int predefinedCount = 0;
    Double lastPredefinedValue = null;
    Double lastPredefinedX = null;
    NavigableMap<Double, Double> predefinedValuesMap = new TreeMap<Double, Double>();
    
    Iterator<String> stringMapIterator = predefinedValuesStringMap.keySet().iterator();
    while (stringMapIterator.hasNext()) {
      String xStr = stringMapIterator.next();
      String yStr = predefinedValuesStringMap.get(xStr);
      Double y = StringUtils.isNotBlank(yStr) ? NumberUtils.createDouble(yStr.replaceAll(",", ".")) : null;
      predefinedValuesMap.put(NumberUtils.createDouble(xStr), y);
    }
    
    for (double x = minX; x <= maxX; x += stepGCD) {
      int index = (int) Math.round((x - minX) / stepGCD);
      Double y = predefinedValuesMap.get(x);
      
      if (y != null) {
        preliminaryValues.set(index, y);
        lastPredefinedValue = y;
        lastPredefinedX = x;
        predefinedCount++;
      }
      
      categoryCaptions.set(index, Math.floor(x) == x ? String.valueOf((int) x) : String.valueOf(x));
    }

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);
    
    if (predefinedCount > 0) {
      // last predefined value is set as first value in all series connecting the predefined line with the actual series
      int lastPredefinedIndex = (int) Math.round((lastPredefinedX - minX) / stepGCD); 
      averageValues.set(lastPredefinedIndex, lastPredefinedValue);
      q1Values.set(lastPredefinedIndex, lastPredefinedValue);
      q3Values.set(lastPredefinedIndex, lastPredefinedValue);
      minValues.set(lastPredefinedIndex, lastPredefinedValue);
      maxValues.set(lastPredefinedIndex, lastPredefinedValue);
    }

    for (Double x = Math.max(minX, lastPredefinedX != null ? lastPredefinedX + userStepX : 0); x <= maxX; x += userStepX) {
      String fieldName = getFieldName(x);
      QueryNumericField queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName); 
      List<Double> numberFieldData = ReportUtils.getNumberFieldData(queryField, queryReplies);
      int index = (int) Math.round((x - minX) / stepGCD);

      QueryFieldDataStatistics statistics = new QueryFieldDataStatistics(numberFieldData);

      averageValues.set(index, statistics.getAvg());
      q1Values.set(index, statistics.getQ1());
      q3Values.set(index, statistics.getQ3());
      minValues.set(index, statistics.getMin());
      maxValues.set(index, statistics.getMax());
    }

    String predefinedValuesCaption = QueryPageUtils.getSetting(queryPage, "time_serie.predefinedSetLabel");
    
    return ChartModelProvider.createTimeSeriesChart(
        queryPage.getTitle(),
        categoryCaptions, 
        minY, maxY,
        preliminaryValues.size() > 0 ? new ChartDataSeries(predefinedValuesCaption, preliminaryValues) : null, 
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.averageValuesValuesCaption"), averageValues), 
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.1stQuartileValuesValuesCaption"), q1Values),
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.3rdQuartileValuesValuesCaption"), q3Values),
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.minValuesValuesCaption"), minValues),
        new ChartDataSeries(Messages.getInstance().getText(locale, "panel.admin.report.timeSerie.maxValuesValuesCaption"), maxValues));
  }
  
  private String getFieldName(Double x) {
    return  "time_serie." + x;
  }
}
