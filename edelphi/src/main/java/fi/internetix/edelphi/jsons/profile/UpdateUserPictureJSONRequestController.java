package fi.internetix.edelphi.jsons.profile;

import java.util.Locale;

import org.apache.commons.fileupload.FileItem;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.UserPictureDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserPicture;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdateUserPictureJSONRequestController extends JSONController {

  public UpdateUserPictureJSONRequestController() {
    super();
//    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserPictureDAO pictureDAO = new UserPictureDAO();
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);

    FileItem file = jsonRequestContext.getFile("imageData");
    if (file.getSize() > 102400) {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.PROFILE_IMAGE_TOO_LARGE, messages.getText(locale, "exception.1029.profileImageTooLarge"));
    }
    byte[] data = file.get();
    String contentType = file.getContentType();
    
    UserPicture picture = pictureDAO.findByUser(loggedUser);
    if (picture != null)
      pictureDAO.updateData(picture, contentType, data);
    else
      pictureDAO.create(loggedUser, contentType, data);

    jsonRequestContext.getRequest().getSession(true).setAttribute("loggedUserHasPicture", Boolean.TRUE);
  }
  
}
