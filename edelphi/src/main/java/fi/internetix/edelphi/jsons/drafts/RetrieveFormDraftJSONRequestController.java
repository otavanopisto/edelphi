package fi.internetix.edelphi.jsons.drafts;

import fi.internetix.edelphi.dao.drafts.FormDraftDAO;
import fi.internetix.edelphi.domainmodel.drafts.FormDraft;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class RetrieveFormDraftJSONRequestController extends JSONController {
  
  public void process(JSONRequestContext requestContext) {
    FormDraftDAO draftDAO = new FormDraftDAO();

    String url = RequestUtils.sortUrlQueryParams(requestContext.getRequest().getHeader("Referer"));
    User loggedUser = RequestUtils.getUser(requestContext);
    
    FormDraft formDraft = draftDAO.findByUrlAndUser(url, loggedUser);
    if (formDraft == null) {
      requestContext.addResponseParameter("draftDeleted", true);
    } else {
      requestContext.addResponseParameter("draftDeleted", false);
      requestContext.addResponseParameter("url", formDraft.getUrl());
      requestContext.addResponseParameter("draftData", formDraft.getData());
      requestContext.addResponseParameter("draftCreated", formDraft.getCreated());
      requestContext.addResponseParameter("draftModified", formDraft.getModified());
    }
  }

}

