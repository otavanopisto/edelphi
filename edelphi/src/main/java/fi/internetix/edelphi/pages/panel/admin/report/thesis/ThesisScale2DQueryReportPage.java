package fi.internetix.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class ThesisScale2DQueryReportPage extends QueryReportPageController {

  public ThesisScale2DQueryReportPage() {
    super(QueryPageType.THESIS_SCALE_2D);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, QueryReportChartContext chartContext, QueryPage queryPage) {
//    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);
    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_scale_2d.jsp", null, chartContext.getStamp());
  }

  private void appendQueryPageComments(RequestContext requestContext, final QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("x"));
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("y"));
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);

    @SuppressWarnings("unchecked")
    Map<Long,Map<String,String>> answers = (Map<Long,Map<String,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    if (answers == null) {
      answers = new HashMap<Long,Map<String,String>>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }
    final Map<Long,String> answerMap = new HashMap<Long,String>();
    for (QueryQuestionComment comment : rootComments) {
      QueryQuestionOptionAnswer xAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryFieldX); 
      QueryQuestionOptionAnswer yAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(comment.getQueryReply(), queryFieldY);
      answerMap.put(comment.getId(), (xAnswer == null ? "-" : xAnswer.getOption().getValue()) + (yAnswer == null ? "-" : yAnswer.getOption().getValue()));
      if (xAnswer != null || yAnswer != null) {
        Map<String,String> valueMap = new LinkedHashMap<String,String>();
        answers.put(comment.getId(), valueMap);
        if (xAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(xAnswer.getOption().getOptionField().getCaption()));
          valueMap.put(caption, xAnswer.getOption().getText());
        }
        if (yAnswer != null) {
          String caption = StringUtils.capitalize(StringUtils.lowerCase(yAnswer.getOption().getOptionField().getCaption()));
          valueMap.put(caption, yAnswer.getOption().getText());
        }
      }
    }
    Collections.sort(rootComments, new Comparator<QueryQuestionComment>() {
      @Override
      public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
        return answerMap.get(o2.getId()).compareTo(answerMap.get(o1.getId()));
      }
    });

    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }
  
  @Override
  public Chart constructChart(QueryReportChartContext chartContext, QueryPage queryPage) {
    
    // Data Access Objects

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    // Determine whether 2D is rendered as bubble chart or as an X/Y axis bar chart   
    // TODO chart parameters?

    Map<String, String> chartParameters = chartContext.getParameters();
    String axis = chartParameters.get("render2dAxis");
    Render2dAxis render2dAxis = "x".equals(axis) ? Render2dAxis.X : "y".equals(axis) ? Render2dAxis.Y : Render2dAxis.BOTH;
    
    //render2dAxis = Render2dAxis.Y; // TODO Just testing
    
    if (render2dAxis == Render2dAxis.BOTH) {
    
      // Render an ordinary 2D bubble chart
      
      String fieldNameX = getFieldName("x");
      String fieldNameY = getFieldName("y");
      QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
      QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

      List<QueryOptionFieldOption> optionsX = queryOptionFieldOptionDAO.listByQueryField(queryFieldX);
      List<QueryOptionFieldOption> optionsY = queryOptionFieldOptionDAO.listByQueryField(queryFieldY);

      int maxX = 0;
      int maxY = 0;

      List<String> xTickLabels = new ArrayList<String>();

      for (QueryOptionFieldOption optionX : optionsX) {
        int x = NumberUtils.createInteger(optionX.getValue());
        maxX = Math.max(maxX, x);
        xTickLabels.add(optionX.getText());
      }

      List<String> yTickLabels = new ArrayList<String>();
      for (QueryOptionFieldOption optionY : optionsY) {
        int y = NumberUtils.createInteger(optionY.getValue());
        maxY = Math.max(maxY, y);
        yTickLabels.add(optionY.getText());
      }

      maxX++;
      maxY++;

      Double[][] values = new Double[maxX][];
      for (int x = 0; x < maxX; x++) {
        values[x] = new Double[maxY];
      }

      List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);
      for (QueryReply queryReply : queryReplies) {
        QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
        QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

        if (answerX != null && answerY != null) {
          int x = NumberUtils.createInteger(answerX.getOption().getValue());
          int y = NumberUtils.createInteger(answerY.getOption().getValue());

          values[x][y] = new Double(values[x][y] != null ? values[x][y] + 1 : 1); 
        }
      }

      QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
      QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
      QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale2d.label.x");
      QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
      String xLabel = queryPageSetting == null ? null : queryPageSetting.getValue();
      queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale2d.label.y");
      queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
      String yLabel = queryPageSetting == null ? null : queryPageSetting.getValue();

      return ChartModelProvider.createBubbleChart(queryPage.getTitle(), xLabel, xTickLabels, yLabel, yTickLabels, 0, 0, values);
    }
    else {

      // Render a bar chart of X or Y axis
      
      String fieldName = render2dAxis == Render2dAxis.X ? getFieldName("x") : getFieldName("y");
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);

      List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);
      Map<Long, Long> data = ReportUtils.getOptionListData(queryField, queryFieldOptions, queryReplies);
      
      List<Double> values = new ArrayList<Double>();
      List<String> categoryCaptions = new ArrayList<String>();
      
      for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
        Long optionId = optionFieldOption.getId();
        categoryCaptions.add(optionFieldOption.getText());
        values.add(new Double(data.get(optionId)));
      }
      
      // Axis label
      
      QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
      QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
      String labelSettingName = render2dAxis == Render2dAxis.X ? "scale2d.label.x" : "scale2d.label.y";
      QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName(labelSettingName);
      QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
      String labelText = queryPageSetting == null ? null : queryPageSetting.getValue();

      // Statistics
      // TODO These could be calculated elsewhere and added below the chart image?
      
      QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);
      Double avg = statistics.getCount() > 1 ? statistics.getAvg() : null;
      Double q1 = statistics.getCount() >= 5 ? statistics.getQ1() : null;
      Double q3 = statistics.getCount() >= 5 ? statistics.getQ3() : null;
      
      // Bar chart rendering
      
      return ChartModelProvider.createBarChart(queryPage.getTitle(), labelText, categoryCaptions, values, avg, q1, q3);
    }
  }
  

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }

  private enum Render2dAxis {
    X,
    Y,
    BOTH;
  }
 
}
