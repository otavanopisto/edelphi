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
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
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
    final QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    final QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    final QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("x"));
    final QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName("y"));
    final PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);

    Collections.sort(rootComments, new Comparator<QueryQuestionComment>() {
      @Override
      public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
        QueryReply o1Reply = queryReplyDAO.findByUserAndQueryAndStamp(o1.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
        QueryReply o2Reply = queryReplyDAO.findByUserAndQueryAndStamp(o2.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
        QueryQuestionOptionAnswer o1XAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(o1Reply, queryFieldX); 
        QueryQuestionOptionAnswer o1YAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(o1Reply, queryFieldY); 
        QueryQuestionOptionAnswer o2XAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(o2Reply, queryFieldX);
        QueryQuestionOptionAnswer o2YAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(o2Reply, queryFieldY);
        String o1XAnswerStr = o1XAnswer == null ? "" : o1XAnswer.getOption().getValue();
        String o1YAnswerStr = o1YAnswer == null ? "" : o1YAnswer.getOption().getValue();
        String o2XAnswerStr = o2XAnswer == null ? "" : o2XAnswer.getOption().getValue();
        String o2YAnswerStr = o2YAnswer == null ? "" : o2YAnswer.getOption().getValue();
        return StringUtils.equals(o1XAnswerStr, o2XAnswerStr) ?
            StringUtils.equals(o1YAnswerStr, o2YAnswerStr) ?
                o1.getCreated().compareTo(o2.getCreated()) :
                  o1YAnswerStr.compareTo(o2YAnswerStr) :
                    o1XAnswerStr.compareTo(o2XAnswerStr);
      }
    });
    
    @SuppressWarnings("unchecked")
    Map<Long,Map<String,String>> answers = (Map<Long,Map<String,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    if (answers == null) {
      answers = new HashMap<Long,Map<String,String>>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }
    for (QueryQuestionComment rootComment : rootComments) {
      QueryReply reply = queryReplyDAO.findByUserAndQueryAndStamp(rootComment.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
      QueryQuestionOptionAnswer xAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(reply, queryFieldX);
      QueryQuestionOptionAnswer yAnswer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(reply, queryFieldY);
      if (xAnswer != null || yAnswer != null) {
        Map<String,String> valueMap = new LinkedHashMap<String,String>();
        answers.put(rootComment.getId(), valueMap);
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
    
    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }
  
  @Override
  public Chart constructChart(QueryReportChartContext chartContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
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
  

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }
}
