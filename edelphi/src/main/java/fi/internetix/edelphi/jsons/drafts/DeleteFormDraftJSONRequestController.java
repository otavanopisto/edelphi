package fi.internetix.edelphi.jsons.drafts;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.drafts.FormDraftDAO;
import fi.internetix.edelphi.domainmodel.drafts.FormDraft;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class DeleteFormDraftJSONRequestController extends JSONController {
  
  public void process(JSONRequestContext requestContext) {
    FormDraftDAO draftDAO = new FormDraftDAO();

    String url = RequestUtils.sortUrlQueryParams(requestContext.getRequest().getHeader("Referer"));
    String strategyStr = requestContext.getString("strategy");
    DeleteStrategy strategy = StringUtils.isEmpty(strategyStr) ? DeleteStrategy.URL_AND_USER : DeleteStrategy.valueOf(strategyStr);
    
    switch (strategy) {
      case URL:
        List<FormDraft> formDrafts = draftDAO.listByUrl(url);
        for (FormDraft formDraft : formDrafts) {
          draftDAO.delete(formDraft);
        }
        break;
      case URL_AND_USER:
        User loggedUser = RequestUtils.getUser(requestContext);
        FormDraft formDraft = draftDAO.findByUrlAndUser(url, loggedUser);
        if (formDraft != null) {
          draftDAO.delete(formDraft);
        } 
        break;
      default:
        throw new IllegalArgumentException("Unsupported strategy: " + strategyStr);
    }
  }

  private enum DeleteStrategy {
    URL,
    URL_AND_USER
  }

}
