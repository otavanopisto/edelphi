package fi.internetix.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSetting;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.ReportUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class ThesisOrderQueryReportPage extends QueryReportPageController {

  public ThesisOrderQueryReportPage() {
    super(QueryPageType.THESIS_ORDER);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    appendQueryPageComments(requestContext, queryPage);
    QueryUtils.appendQueryPageThesis(requestContext, queryPage);

    return new QueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_order.jsp", null);
  }

  @Override
  public QueryReportPage generateReportPage(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    return new QueryReportPage(queryPage.getId(), queryPage.getTitle(), "/jsp/blocks/panel/admin/report/todo.jsp");
  }

  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    // List<Double> inside has values for one series, i.e. for position 1, how many times [item index] had occured
    List<List<Double>> stackedSeries = new ArrayList<List<Double>>();
    List<String> items = QueryPageUtils.parseSerializedList(getStringOptionValue(queryPage, "orderingField.items"));
    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    
    for (int i = 0, l = items.size(); i < l; i++) {
      List<Double> data = new ArrayList<Double>();
      for (int j = 0; j < l; j++) {
        data.add(new Double(0));
      }
        
      stackedSeries.add(data);
    }
    
    for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
      String fieldName = "orderItem." + itemIndex;
      QueryNumericField numberField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      for (QueryReply queryReply : queryReplies) {
        for (int position = 0; position < items.size(); position++) {
          Double key = new Double(position);
          Long count = queryQuestionNumericAnswerDAO.countByQueryFieldQueryReplyAndData(numberField, queryReply, key);
          List<Double> list = stackedSeries.get(position);
          list.set(itemIndex, list.get(itemIndex) + new Double(count));
        }
      }      
    }
    
    return ChartModelProvider.createStackedBarChartHorizontal(queryPage.getTitle(), items, stackedSeries);
  }
  
  
  protected String getStringOptionValue(QueryPage queryPage, String queryOptionName) {
    QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
    QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
    QueryPageSettingKey key = queryPageSettingKeyDAO.findByName(queryOptionName);
    if (key != null) {
      QueryPageSetting setting = queryPageSettingDAO.findByKeyAndQueryPage(key, queryPage);
      if (setting != null)
        return setting.getValue();
    }
    
    return null;
  }

  private void appendQueryPageComments(RequestContext requestContext, QueryPage queryPage) {
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> rootComments = queryQuestionCommentDAO.listRootCommentsByQueryPageAndStamp(queryPage, panelStamp);
    @SuppressWarnings("unchecked")
    Map<Long,Map<Integer,String>> answers = (Map<Long,Map<Integer,String>>) requestContext.getRequest().getAttribute("commentAnswers");
    
    if (answers == null) {
      answers = new HashMap<Long,Map<Integer,String>>();
      requestContext.getRequest().setAttribute("commentAnswers", answers);
    }
    for (QueryQuestionComment rootComment : rootComments) {
      QueryReply reply = queryReplyDAO.findByUserAndQueryAndStamp(rootComment.getCreator(), queryPage.getQuerySection().getQuery(), panelStamp);
      List<QueryQuestionNumericAnswer> orderAnswers = queryQuestionNumericAnswerDAO.listByQueryReplyAndQueryPageOrderByData(reply, queryPage);
      if (!orderAnswers.isEmpty()) {
        Map<Integer,String> valueMap = new LinkedHashMap<Integer,String>();
        answers.put(rootComment.getId(), valueMap);
        int orderNum = 1;
        for (QueryQuestionNumericAnswer orderAnswer : orderAnswers) {
          valueMap.put(orderNum++, orderAnswer.getQueryField().getCaption());
        }
      }
    }
    
    Map<Long, List<QueryQuestionComment>> childComments = queryQuestionCommentDAO.listTreesByQueryPage(queryPage);
    QueryUtils.appendQueryPageRootComments(requestContext, queryPage.getId(), rootComments);
    QueryUtils.appendQueryPageChildComments(requestContext, childComments);
  }

}
