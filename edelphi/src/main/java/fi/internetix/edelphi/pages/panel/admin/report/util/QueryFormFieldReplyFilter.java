package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;

public class QueryFormFieldReplyFilter extends QueryReplyFilter {

  public QueryFormFieldReplyFilter() {
  }
  
  @Override
  public List<QueryReply> filterList(List<QueryReply> list) {
    QueryOptionFieldDAO optionFieldDAO = new QueryOptionFieldDAO();
    QueryQuestionOptionAnswerDAO optionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    List<QueryReply> result = new ArrayList<QueryReply>();

    String token = getValue();
    
    int p = token.indexOf('=');
    String fieldId = token.substring(0, p);
    String value = token.substring(p + 1);

    QueryOptionField queryOptionField = optionFieldDAO.findById(Long.parseLong(fieldId));
    
    for (QueryReply reply : list) {
      StringTokenizer valueToken = new StringTokenizer(value, ",");
      
      List<QueryQuestionOptionAnswer> answers = optionAnswerDAO.listByQueryReplyAndQueryField(reply, queryOptionField);

      while (valueToken.hasMoreElements()) {
        String filterValue = valueToken.nextToken();
        boolean y = false;
        
        for (QueryQuestionOptionAnswer answer : answers) {
          String fieldValue = answer.getOption().getValue();
          if (filterValue.equals(fieldValue)) {
            result.add(reply);
            y = true;
            break;
          }
        }
        if (y)
          break;
      }
    }
    
    return result;
  }

  @Override
  public QueryReplyFilterType getType() {
    return QueryReplyFilterType.FORMFIELD;
  }
  
}