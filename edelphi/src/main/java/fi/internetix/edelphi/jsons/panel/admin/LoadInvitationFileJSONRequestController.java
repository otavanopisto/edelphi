package fi.internetix.edelphi.jsons.panel.admin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;

import com.csvreader.CsvReader;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class LoadInvitationFileJSONRequestController extends JSONController {

  public LoadInvitationFileJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FileItem csvFile = jsonRequestContext.getFile("csvFile");
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    List<Map<String, Object>> userInfos = new ArrayList<Map<String,Object>>();
    
    int invalidRecords = 0;
    try {
      CsvReader csvReader = new CsvReader(csvFile.getInputStream(), Charset.defaultCharset());
      while (csvReader.readRecord()) {
        int columnCount = csvReader.getColumnCount();
        if (columnCount == 0) {
          invalidRecords++;
        }
        else {
          Long userId = null;
          String firstName = csvReader.getColumnCount() >= 3 ? csvReader.get(0) : null;
          String lastName = csvReader.getColumnCount() >= 3 ? csvReader.get(1) : null;
          String email = (csvReader.getColumnCount() >= 3 ? csvReader.get(2) : csvReader.get(0)).toLowerCase();
          if (!RFC2822.matcher(email).matches()) {
            invalidRecords++;
          }
          else {
            UserEmail userEmail = userEmailDAO.findByAddress(email);
            if (userEmail != null) {
              userId = userEmail.getUser().getId();
            }
            Map<String, Object> userInfo = new HashMap<String, Object>();
            userInfo.put("id", userId);
            userInfo.put("firstName", firstName);
            userInfo.put("lastName", lastName);
            userInfo.put("email", email);
            userInfos.add(userInfo);
          }
        }
      }
      if (invalidRecords > 0) {
        if (invalidRecords == 1) {
          jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.csvFailedRecord"));
        }
        else {
          jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.csvFailedRecords",
              new String[] { invalidRecords + ""}));
        }
      }
      if (userInfos.size() == 0) {
        jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.csvNoUsers"));
      }
      else if (userInfos.size() == 1) {
        jsonRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "panel.admin.inviteUsers.csvUserRead"));
      }
      else {
        jsonRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "panel.admin.inviteUsers.csvUsersRead",
            new String[] { userInfos.size() + ""}));
      }
      jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.inviteUsers.csvFileRead"));
    }
    catch (IOException ioe) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_CSV_FILE, Messages.getInstance().getText(locale, "exception.1006.invalidCsvFile"));
    }
    jsonRequestContext.addResponseParameter("users", userInfos);
  }

  private static final Pattern RFC2822 = Pattern.compile(
    "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
  );

}