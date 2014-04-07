package fi.internetix.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class ThesisMultiselectQueryReportPage extends QueryReportPageController {

  public ThesisMultiselectQueryReportPage() {
    super(QueryPageType.THESIS_MULTI_SELECT);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, QueryReportChartContext chartContext, QueryPage queryPage) {
    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);

    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_multiselect.jsp", null);
  }

  private QueryOptionField getOptionFieldFromScale1DPage(QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO(); 
  
    List<QueryField> pageFields = queryFieldDAO.listByQueryPage(queryPage);
    
    if (pageFields.size() == 1) 
      return (QueryOptionField) pageFields.get(0);
    else
      throw new RuntimeException("");
  }
  
  @Override
  public Chart constructChart(QueryReportChartContext chartContext, QueryPage queryPage) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();

    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);

    Map<Long, Long> listOptionAnswerCounts = ReportUtils.getMultiselectData(queryOptionField, queryFieldOptions, queryReplies);
    
    List<String> categoryCaptions = new ArrayList<String>();
    List<Double> values = new ArrayList<Double>();
    
    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();
      
      categoryCaptions.add(optionFieldOption.getText());
      values.add(new Double(listOptionAnswerCounts.get(optionId)));
    }
    
    return ChartModelProvider.createBarChartHorizontal(queryPage.getTitle(), null, categoryCaptions, values, null, null, null);
  }
}
