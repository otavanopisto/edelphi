package fi.internetix.edelphi.query;

import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public interface QueryPageHandler {

  void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply);
  void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply);
  void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers);
  List<QueryOption> getDefinedOptions();
  void exportData(QueryExportContext exportContext);
  
}
