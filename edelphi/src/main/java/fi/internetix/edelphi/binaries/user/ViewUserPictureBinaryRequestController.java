package fi.internetix.edelphi.binaries.user;

import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserPictureDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserPicture;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.BinaryRequestContext;

public class ViewUserPictureBinaryRequestController extends BinaryController {

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    UserDAO userDAO = new UserDAO();
    UserPictureDAO pictureDAO = new UserPictureDAO();

    Long userId = binaryRequestContext.getLong("userId");
    
    User user = userDAO.findById(userId);
    UserPicture picture = pictureDAO.findByUser(user);
  
    if (picture != null) {
      binaryRequestContext.setResponseContent(picture.getData(), picture.getContentType());
    } else {
      throw new PageNotFoundException(binaryRequestContext.getRequest().getLocale());
    }
  }
  
}
