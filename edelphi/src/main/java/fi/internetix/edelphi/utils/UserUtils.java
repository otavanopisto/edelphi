package fi.internetix.edelphi.utils;

import java.util.List;

import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserIdentificationDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.dao.users.UserPictureDAO;
import fi.internetix.edelphi.domainmodel.base.DelfoiUser;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.domainmodel.users.UserIdentification;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.domainmodel.users.UserPicture;

public class UserUtils {

  public static void archivePanelUser(PanelUser panelUser, User modifier) {
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    panelUserDAO.archive(panelUser, modifier);
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    List<PanelExpertiseGroupUser> expertiseGroupUsers = panelExpertiseGroupUserDAO.listByUser(panelUser);
    for (PanelExpertiseGroupUser expertiseGroupUser : expertiseGroupUsers) {
      panelExpertiseGroupUserDAO.delete(expertiseGroupUser);
    }
  }

  public static boolean hasLoginCapability(User user) {
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
    UserPassword userPassword = userPasswordDAO.findByUser(user);
    if (userPassword == null) {
      UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
      List<UserIdentification> userIdentifications = userIdentificationDAO.listByUser(user);
      return !userIdentifications.isEmpty();
    }
    return true;
  }
  
  public static boolean isPanelUser(Panel panel, User user) {
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
    return panelUser != null;
  }

  public static void merge(User source, User target) {
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
    UserPictureDAO userPictureDAO = new UserPictureDAO();
    UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    // Internal authentication
    UserPassword sourcePassword = userPasswordDAO.findByUser(source);
    UserPassword targetPassword = userPasswordDAO.findByUser(target);
    if (sourcePassword != null && targetPassword == null) {
      userPasswordDAO.create(target, sourcePassword.getPasswordHash());
      userPasswordDAO.delete(sourcePassword);
    }
    // External authentications
    List<UserIdentification> sourceIdentifications = userIdentificationDAO.listByUser(source);
    List<UserIdentification> targetIdentifications = userIdentificationDAO.listByUser(target);
    for (UserIdentification sourceIdentification : sourceIdentifications) {
      boolean targetHasAuthSource = false;
      for (UserIdentification targetIdentification : targetIdentifications) {
        Long sourceAuthId = sourceIdentification.getAuthSource().getId();
        Long targetAuthId = targetIdentification.getAuthSource().getId();
        if (sourceAuthId.equals(targetAuthId)) {
          targetHasAuthSource = true;
          break;
        }
      }
      if (!targetHasAuthSource) {
        userIdentificationDAO.create(target, sourceIdentification.getExternalId(), sourceIdentification.getAuthSource());
      }
      userIdentificationDAO.delete(sourceIdentification);
    }
    // E-mail addresses
    if (source.getDefaultEmail() != null) {
      userDAO.updateDefaultEmail(source, null, target);
    }
    List<UserEmail> sourceEmails = userEmailDAO.listByUser(source);
    for (UserEmail sourceEmail : sourceEmails) {
      userEmailDAO.delete(sourceEmail);
      userEmailDAO.flush(); // required for the previous delete to take place before the insert below
      userEmailDAO.create(target, sourceEmail.getAddress());
    }
    // User picture
    UserPicture sourceUserPicture = userPictureDAO.findByUser(source);
    UserPicture targetUserPicture = userPictureDAO.findByUser(target);
    if (sourceUserPicture != null && targetUserPicture == null) {
      userPictureDAO.updateUser(sourceUserPicture, target);
    }
    // Delfoi memberships
    List<DelfoiUser> sourceDelfoiUsers = delfoiUserDAO.listByUser(source);
    List<DelfoiUser> targetDelfoiUsers = delfoiUserDAO.listByUser(target);
    for (DelfoiUser sourceDelfoiUser : sourceDelfoiUsers) {
      boolean targetHasMembership = false;
      for (DelfoiUser targetDelfoiUser : targetDelfoiUsers) {
        if (targetDelfoiUser.getDelfoi().getId().equals(sourceDelfoiUser.getDelfoi().getId())) {
          targetHasMembership = true;
          break;
        }
      }
      if (!targetHasMembership) {
        delfoiUserDAO.create(sourceDelfoiUser.getDelfoi(), target, sourceDelfoiUser.getRole(), target);
      }
      delfoiUserDAO.archive(sourceDelfoiUser);
    }
    // Panel memberships
    List<PanelUser> sourcePanelUsers = panelUserDAO.listByUser(source);
    List<PanelUser> targetPanelUsers = panelUserDAO.listByUser(target);
    for (PanelUser sourcePanelUser : sourcePanelUsers) {
      boolean targetHasMembership = false;
      for (PanelUser targetPanelUser : targetPanelUsers) {
        if (targetPanelUser.getPanel().getId().equals(sourcePanelUser.getPanel().getId())) {
          targetHasMembership = true;
          break;
        }
      }
      if (!targetHasMembership) {
        panelUserDAO.create(sourcePanelUser.getPanel(), target, sourcePanelUser.getRole(), sourcePanelUser.getJoinType(), sourcePanelUser.getStamp(), target);
      }
      panelUserDAO.archive(sourcePanelUser);
    }
    // Query replies
    List<QueryReply> sourceReplies = queryReplyDAO.listByUser(source);
    List<QueryReply> targetReplies = queryReplyDAO.listByUser(target);
    for (QueryReply sourceReply : sourceReplies) {
      boolean targetHasReplied = false;
      for (QueryReply targetReply : targetReplies) {
        if (targetReply.getQuery().getId().equals(sourceReply.getQuery().getId())) {
          targetHasReplied = true;
          break;
        }
      }
      if (!targetHasReplied) {
        queryReplyDAO.updateUser(sourceReply, target, target);
      }
      queryReplyDAO.archive(sourceReply);
    }
    // Archive source
    userDAO.archive(source);
  }

}
