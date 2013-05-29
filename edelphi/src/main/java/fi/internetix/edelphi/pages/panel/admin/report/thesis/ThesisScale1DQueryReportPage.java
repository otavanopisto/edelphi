package fi.internetix.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
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

public class ThesisScale1DQueryReportPage extends QueryReportPageController {

  public ThesisScale1DQueryReportPage() {
    super(QueryPageType.THESIS_SCALE_1D);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, QueryReportChartContext chartContext, QueryPage queryPage) {
    /**
     * Load fields on page
     */
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();

    QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);
    Map<Long, Long> data = ReportUtils.getOptionListData(queryOptionField, queryFieldOptions, queryReplies);

    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);

    QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);
    
    statistics.setShift(-queryFieldOptions.size() / 2);
    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_scale_1d.jsp", statistics, chartContext.getStamp());
  }
  
  private void appendQueryPageComments(RequestContext requestContext, final QueryPage queryPage) {
    final QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    final QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    final QueryOptionField queryOptionField = getOptionFieldFromScale1DPage(queryPage);
    final PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);

    Collections.sort(rootComments, new Comparator<QueryQuestionComment>() {
      @Override
      public int compare(QueryQuestionComment o1, QueryQuestionComment o2) {
        QueryReply o1Reply = queryReplyDAO.findByUserAndQueryAndStamp(o1.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
        QueryReply o2Reply = queryReplyDAO.findByUserAndQueryAndStamp(o2.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
        QueryQuestionOptionAnswer o1Answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(o1Reply, queryOptionField); 
        QueryQuestionOptionAnswer o2Answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(o2Reply, queryOptionField);
        String o1AnswerStr = o1Answer == null ? "" : o1Answer.getOption().getValue();
        String o2AnswerStr = o2Answer == null ? "" : o2Answer.getOption().getValue();
        return StringUtils.equals(o1AnswerStr, o2AnswerStr) ? o1.getCreated().compareTo(o2.getCreated()) : o1AnswerStr.compareTo(o2AnswerStr);
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
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(reply, queryOptionField);
      if (answer != null) {
        Map<String,String> valueMap = new LinkedHashMap<String,String>();
        answers.put(rootComment.getId(), valueMap);
        String caption = StringUtils.capitalize(StringUtils.lowerCase(answer.getOption().getOptionField().getCaption()));
        valueMap.put(caption, answer.getOption().getText());
      }
    }
    
    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
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
    Map<Long, Long> data = ReportUtils.getOptionListData(queryOptionField, queryFieldOptions, queryReplies);
    
    List<Double> values = new ArrayList<Double>();
    List<String> categoryCaptions = new ArrayList<String>();
    
    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();
      categoryCaptions.add(optionFieldOption.getText());
      values.add(new Double(data.get(optionId)));
    }
    
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingKey queryPageSettingKey = queryPageSettingKeyDAO.findByName("scale1d.label");
    QueryPageSetting queryPageSetting = queryPageSettingDAO.findByKeyAndQueryPage(queryPageSettingKey, queryPage);
    String xLabel = queryPageSetting == null ? null : queryPageSetting.getValue();

    QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);
    
    Double avg = statistics.getCount() > 1 ? statistics.getAvg() : null;
    Double q1 = statistics.getCount() >= 5 ? statistics.getQ1() : null;
    Double q3 = statistics.getCount() >= 5 ? statistics.getQ3() : null;
    
    return ChartModelProvider.createBarChart(queryPage.getTitle(), xLabel, categoryCaptions, values, avg, q1, q3);
  }
}
