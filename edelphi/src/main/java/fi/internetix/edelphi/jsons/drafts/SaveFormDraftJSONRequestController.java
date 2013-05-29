package fi.internetix.edelphi.jsons.drafts;

import fi.internetix.edelphi.dao.drafts.FormDraftDAO;
import fi.internetix.edelphi.domainmodel.drafts.FormDraft;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SaveFormDraftJSONRequestController extends JSONController {
  
  public void process(JSONRequestContext requestContext) {
    FormDraftDAO draftDAO = new FormDraftDAO(); 

    String url = RequestUtils.sortUrlQueryParams(requestContext.getRequest().getHeader("Referer"));
    String draftData = requestContext.getString("draftData");
    
    if (draftData != null) {
      User loggedUser = RequestUtils.getUser(requestContext);
      
      FormDraft formDraft = draftDAO.findByUrlAndUser(url, loggedUser);
      if (formDraft == null)
        formDraft = draftDAO.create(url, draftData, loggedUser);
      else
        draftDAO.update(formDraft, draftData);
      
      requestContext.addResponseParameter("url", formDraft.getUrl());
      requestContext.addResponseParameter("draftCreated", formDraft.getCreated());
      requestContext.addResponseParameter("draftModified", formDraft.getModified());
    } 
  }

}
