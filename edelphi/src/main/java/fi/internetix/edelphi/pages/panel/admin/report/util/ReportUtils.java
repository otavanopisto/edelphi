package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import fi.internetix.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.internetix.smvc.controllers.RequestContext;

public class ReportUtils {
  
//  private static final String QUERY_FILTER_PREFIX = "filters.";
//  
//  @SuppressWarnings("unchecked")
//  public static List<QueryReplyFilter> getQueryFilters(RequestContext requestContext, Long queryId) {
//    HttpSession session = requestContext.getRequest().getSession();
//    return (List<QueryReplyFilter>) session.getAttribute(QUERY_FILTER_PREFIX + queryId);
//  }
//  
//  public static void storeQueryFilters(RequestContext requestContext, Long queryId, List<QueryReplyFilter> filters) {
//    HttpSession session = requestContext.getRequest().getSession(); 
//    if (filters.isEmpty()) {
//      session.removeAttribute(QUERY_FILTER_PREFIX + queryId);
//    }
//    else {
//      session.setAttribute(QUERY_FILTER_PREFIX +  queryId, filters);  
//    }
//  }
//  
//  public static void clearQueryFilters(RequestContext requestContext) {
//    HttpSession session = requestContext.getRequest().getSession();
//    Enumeration<String> e = session.getAttributeNames();
//    while (e.hasMoreElements()) {
//      String attribute = e.nextElement();
//      if (attribute.startsWith(QUERY_FILTER_PREFIX)) {
//        session.removeAttribute(attribute);
//      }
//    }
//  }
  
  public static List<QueryReply> getQueryReplies(QueryPage queryPage, QueryReportChartContext chartContext) {
    List<QueryReplyFilter> filters = QueryReplyFilter.parseFilters(chartContext.getFilters());
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(queryPage.getQuerySection().getQuery(), chartContext.getStamp());
    for (QueryReplyFilter filter : filters) {
      queryReplies = filter.filterList(queryReplies);
    }
    return queryReplies;
  }
  
  public static Map<Long, Long> getOptionListData(QueryField queryOptionField, List<QueryOptionFieldOption> queryFieldOptions, List<QueryReply> queryReplies) {
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    Map<Long, Long> listOptionAnswerCounts = new HashMap<Long, Long>();
    
    for (QueryOptionFieldOption queryFieldOption : queryFieldOptions) {
      listOptionAnswerCounts.put(queryFieldOption.getId(), new Long(0));
    }
    
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryOptionField);
      
      if (answer != null) {
        Long v = listOptionAnswerCounts.get(answer.getOption().getId());
        listOptionAnswerCounts.put(answer.getOption().getId(), new Long(v.longValue() + 1));
      }
    }
    
    return listOptionAnswerCounts;
  }

  public static Map<Long, Long> getGroupData(QueryOptionField groupField, QueryOptionFieldOptionGroup group, List<QueryOptionFieldOption> groupOptions, List<QueryReply> queryReplies) {
    QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
    Map<Long, Long> listOptionAnswerCounts = new HashMap<Long, Long>();
    for (QueryOptionFieldOption queryFieldOption : groupOptions) {
      listOptionAnswerCounts.put(queryFieldOption.getId(), new Long(0));
    }
    for (QueryReply queryReply : queryReplies) {
      List<QueryQuestionOptionGroupOptionAnswer> groupAnswers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, groupField, group);
      for (QueryQuestionOptionGroupOptionAnswer groupAnswer : groupAnswers) {
        Long v = listOptionAnswerCounts.get(groupAnswer.getOption().getId());
        listOptionAnswerCounts.put(groupAnswer.getOption().getId(), new Long(v.longValue() + 1));
      }
    }
    
    return listOptionAnswerCounts;
  }

  public static Map<Long, Long> getMultiselectData(QueryField queryMultiselectField, List<QueryOptionFieldOption> queryFieldOptions, List<QueryReply> queryReplies) {
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    Map<Long, Long> listOptionAnswerCounts = new HashMap<Long, Long>();
    
    for (QueryOptionFieldOption queryFieldOption : queryFieldOptions) {
      listOptionAnswerCounts.put(queryFieldOption.getId(), new Long(0));
    }

    for (QueryReply queryReply : queryReplies) {
      QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryMultiselectField);

      if (answer != null) {
        Set<QueryOptionFieldOption> options = answer.getOptions();
        
        for (QueryOptionFieldOption option : options) {
          Long v = listOptionAnswerCounts.get(option.getId());
          listOptionAnswerCounts.put(option.getId(), new Long(v.longValue() + 1));
        }
      }
    }
    
    return listOptionAnswerCounts;
  }
  
  public static QueryFieldDataStatistics getOptionListStatistics(List<QueryOptionFieldOption> queryFieldOptions, Map<Long, Long> optionListData) {
    Map<Double, String> dataNames = new HashMap<Double, String>();
    List<Double> result = new ArrayList<Double>();
    
    for (int i = 0; i < queryFieldOptions.size(); i++) {
      QueryOptionFieldOption queryFieldOption = queryFieldOptions.get(i);

      dataNames.put(new Double(i), queryFieldOption.getText());
      
      Long value = optionListData.get(queryFieldOption.getId());
      
      if (value != null) {
        for (int j = 0; j < value.intValue(); j++) {
          // Add index of the option to the list
          result.add(new Double(i));
        }
      }
    }
    
    return getStatistics(result, dataNames);
  }
  
  public static QueryFieldDataStatistics getStatistics(List<Double> values, Map<Double, String> dataNames) {
    return new QueryFieldDataStatistics(values, dataNames);
  }
  
  public static List<Double> getNumberFieldData(QueryField numberField, List<QueryReply> queryReplies) {
    QueryQuestionNumericAnswerDAO questionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    List<Double> data = new ArrayList<Double>();

    for (QueryReply queryReply : queryReplies) {
      QueryQuestionNumericAnswer answer = questionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, numberField);

      if (answer != null) {
        data.add(answer.getData());
      }
    }

    return data;
  }
  
  public static Map<Double, Long> getClassifiedNumberFieldData(List<Double> data) {
    List<Double> temp = new ArrayList<Double>(data);
    Map<Double, Long> result = new HashMap<Double, Long>();
    
    while (temp.size() > 0) {
      Double v = temp.get(0);
      int c = 0;
      
      while (temp.remove(v))
        c++;
      
      result.put(v, new Long(c));
    }
    
    return result;
  }
}
