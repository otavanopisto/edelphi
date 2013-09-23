package fi.internetix.edelphi.pages.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.base.DelfoiDAO;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageSettingKeyDAO;
import fi.internetix.edelphi.dao.querylayout.QuerySectionDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.resources.GoogleDocumentDAO;
import fi.internetix.edelphi.dao.resources.GoogleImageDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserSettingDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageSettingKey;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.GoogleDocument;
import fi.internetix.edelphi.domainmodel.resources.GoogleImage;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserSetting;
import fi.internetix.edelphi.domainmodel.users.UserSettingKey;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.edelphi.utils.UserUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class SystemUtilsPageController extends PageController {

  public SystemUtilsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String action = pageRequestContext.getString("action");
    if ("mergeUser".equals(action)) {
      Long sourceUserId = pageRequestContext.getLong("sourceUserId");
      Long targetUserId = pageRequestContext.getLong("targetUserId");
      UserDAO userDAO = new UserDAO();
      User sourceUser = userDAO.findById(sourceUserId);
      User targetUser = userDAO.findById(targetUserId);
      UserUtils.merge(sourceUser, targetUser);
    }
    else if ("representUser".equals(action)) {
      Long userId = pageRequestContext.getLong("userId");
      UserDAO userDAO = new UserDAO();
      User user = userDAO.findById(userId);
      RequestUtils.loginUser(pageRequestContext, user);
      pageRequestContext.setRedirectURL(pageRequestContext.getRequest().getContextPath() + "/index.page");
    }
    else if ("copyQuery".equals(action)) {
      Long queryId = pageRequestContext.getLong("queryId");
      String name = pageRequestContext.getString("name");
      Long targetFolderId = pageRequestContext.getLong("targetFolderId");
      Boolean copyAnswers = pageRequestContext.getBoolean("copyAnswers");
      Boolean copyComments = pageRequestContext.getBoolean("copyComments");
      QueryDAO queryDAO = new QueryDAO();
      Query query = queryDAO.findById(queryId);
      FolderDAO folderDAO = new FolderDAO();
      Folder targetFolder = folderDAO.findById(targetFolderId);
      PanelDAO panelDAO = new PanelDAO();
      Panel targetPanel = panelDAO.findByRootFolder(targetFolder);
      QueryUtils.copyQuery(pageRequestContext, query, name, targetPanel, copyAnswers, copyComments);
    }
    else if ("reindexEntities".equals(action)) {
      DelfoiDAO delfoiDAO = new DelfoiDAO();
      delfoiDAO.reindexEntities();
    }
    else if ("rebuildOrderPageCaptions".equals(action)) {
      QueryPageDAO queryPageDAO = new QueryPageDAO();
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryPageSettingDAO queryPageSettingDAO = new QueryPageSettingDAO();
      QueryPageSettingKeyDAO queryPageSettingKeyDAO = new QueryPageSettingKeyDAO();
      QueryPageSettingKey settingKey = queryPageSettingKeyDAO.findByName("orderingField.items");
      List<QueryPage> queryPages = queryPageDAO.listByType(QueryPageType.THESIS_ORDER);
      for (QueryPage queryPage : queryPages) {
        String[] items = queryPageSettingDAO.findByKeyAndQueryPage(settingKey, queryPage).getValue().split("&");
        for (int i = 0; i < items.length; i++) {
          try {
            items[i] = URLDecoder.decode(items[i], "UTF-8");
            QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, "orderItem." + i);
            if (queryNumericField != null && queryNumericField.getCaption().equals(queryNumericField.getName())) {
              queryFieldDAO.updateCaption(queryNumericField, items[i]);
            }
          }
          catch (Exception e) {
          }
        }
      }
    }
    else if ("queryInfo".equals(action)) {
      PanelDAO panelDAO = new PanelDAO();
      QueryDAO queryDAO = new QueryDAO();
      QuerySectionDAO querySectionDAO = new QuerySectionDAO();
      QueryPageDAO queryPageDAO = new QueryPageDAO();
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
      String name = pageRequestContext.getString("name");
      String urlName = name == null ? pageRequestContext.getString("urlName") : ResourceUtils.getUrlName(name);
      List<Query> queries;
      if (urlName != null) {
        queries = queryDAO.listByUrlName(urlName);
      }
      else {
        queries = new ArrayList<Query>();
        Query query = queryDAO.findById(pageRequestContext.getLong("queryId"));
        if (query != null) {
          queries.add(query);
        }
      }
      try {
        pageRequestContext.getResponse().setContentType("text/plain");
        PrintWriter out = pageRequestContext.getResponse().getWriter();
        for (Query query : queries) {
          out.println(pageRequestContext.getRequest().getContextPath() + query.getFullPath());
          Panel panel = panelDAO.findByRootFolder(query.getParentFolder());
          out.println(panel.getId() + " - " + panel.getName());
          out.println("  QUERY id:" + query.getId() + " name:" + query.getName());
          List<QuerySection> sections = querySectionDAO.listByQuery(query);
          Collections.sort(sections, new Comparator<QuerySection>() {
            @Override
            public int compare(QuerySection o1, QuerySection o2) {
              return o1.getSectionNumber().compareTo(o2.getSectionNumber());
            }
          });
          for (QuerySection section : sections) {
            out.println();
            out.println("    SECTION id:" + section.getId() + " title:" + section.getTitle());
            List<QueryPage> pages = queryPageDAO.listByQuerySection(section);
            Collections.sort(pages, new Comparator<QueryPage>() {
              @Override
              public int compare(QueryPage o1, QueryPage o2) {
                return o1.getPageNumber().compareTo(o2.getPageNumber());
              }
            });
            for (QueryPage page : pages) {
              out.println("      --------------------------------------------------------------------------------------");
              out.println("      PAGE id:" + page.getId() + " number:" + page.getPageNumber() + " title:" + page.getTitle());
              List<QueryField> fields = queryFieldDAO.listByQueryPage(page);
              for (QueryField field : fields) {
                out.println("        FIELD id:" + field.getId() + " type:" + field.getType() + " caption:" + field.getCaption() + " name:" + field.getName());
                if (field instanceof QueryOptionField) {
                  List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField((QueryOptionField) field);
                  for (QueryOptionFieldOption option : options) {
                    out.println("          OPTION id:" + option.getId() + " text:" + option.getText() + " value:" + option.getValue());
                  }
                }
              }
            }
          }
        }
      }
      catch (Exception e) {
      }
    }
    else if ("timeSerieFieldCheck".equals(action)) {
      QueryPageDAO queryPageDAO = new QueryPageDAO();
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryNumericFieldDAO queryNumericFieldDAO = new QueryNumericFieldDAO();
      
      List<QueryPage> timeSeriePages = queryPageDAO.listByType(QueryPageType.THESIS_TIME_SERIE);
      for (QueryPage timeSeriePage : timeSeriePages) {
        NavigableMap<String, String> predefinedValuesStringMap = QueryPageUtils.getMapSetting(timeSeriePage, "time_serie.predefinedValues");
        
        boolean hasPredefinedValues = false;
        Iterator<String> stringMapIterator = predefinedValuesStringMap.keySet().iterator();
        while (stringMapIterator.hasNext()) {
          String xStr = stringMapIterator.next();
          String yStr = predefinedValuesStringMap.get(xStr);
          if (StringUtils.isNotBlank(yStr)) {
            hasPredefinedValues = true;
            break;
          }
        }
        
        // Some of the fields that do not have any predefined values
        if (!hasPredefinedValues) {
          Double minX = QueryPageUtils.getDoubleSetting(timeSeriePage, "time_serie.minX");
          Double minY = QueryPageUtils.getDoubleSetting(timeSeriePage, "time_serie.minY");
          Double maxY = QueryPageUtils.getDoubleSetting(timeSeriePage, "time_serie.maxY");
          Double stepY = QueryPageUtils.getDoubleSetting(timeSeriePage, "time_serie.stepY");
          if (stepY == null)
            stepY = 1d;
          
          String fieldName = "time_serie." + minX;
          QueryField queryField = queryFieldDAO.findByQueryPageAndName(timeSeriePage, fieldName);
          if (queryField == null) {
            queryNumericFieldDAO.create(timeSeriePage, fieldName, false, String.valueOf(minX), minY, maxY, stepY);
          }
        }
      }
    }
    else if ("fixOptionAnswers".equals(action)) {
      try {
        pageRequestContext.getResponse().setContentType("text/plain");
        PrintWriter out = pageRequestContext.getResponse().getWriter();
        int deleted = 0;
        QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
        QueryPageDAO queryPageDAO = new QueryPageDAO();
        QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
        QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
        List<QueryPage> pages = queryPageDAO.listByType(QueryPageType.THESIS_GROUPING);
        for (QueryPage page : pages) {
          List<QueryField> fields = queryFieldDAO.listByQueryPage(page);
          for (QueryField field : fields) {
            List<QueryQuestionOptionAnswer> answers = queryQuestionOptionAnswerDAO.listByQueryField(field);
            for (QueryQuestionOptionAnswer answer : answers) {
              QueryQuestionOptionGroupOptionAnswer groupAnswer = queryQuestionOptionGroupOptionAnswerDAO.findById(answer.getId());
              if (groupAnswer == null) {
                deleted++;
                queryQuestionOptionAnswerDAO.delete(answer);
                out.println("Removed answer " + answer.getId());
              }
            }
          }
        }
        out.println("Cleaned " + deleted + " option answers");
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else if ("listGoogleDocuments".equals(action)) {
    	String baseUrl = RequestUtils.getBaseUrl(pageRequestContext.getRequest());
    	
    	pageRequestContext.getResponse().setContentType("text/html");
      try {
				PrintWriter out = pageRequestContext.getResponse().getWriter();
  			out.println("<html><body>");

  			out.println("<h1>GoogleDocuments</h1>");
  			GoogleDocumentDAO googleDocumentDAO = new GoogleDocumentDAO();
      	List<GoogleDocument> documents = googleDocumentDAO.listAll();
      	for (GoogleDocument document : documents) {
      		if (!document.getArchived()) {
        		out.println("<a href=\"");
        		out.print(baseUrl + document.getFullPath());
        		out.print("\">");
        		out.print(document.getName());
        		out.print("&nbsp;(" + document.getId() + ")");
        		out.print("</a><br/>");
      		}
      	}
      	
      	out.println("<h1>GoogleImages</h1>");
  			GoogleImageDAO googleImageDAO = new GoogleImageDAO();
      	List<GoogleImage> images = googleImageDAO.listAll();
      	for (GoogleImage image : images) {
      		if (!image.getArchived()) {
        		out.println("<a href=\"");
        		out.print(baseUrl + image.getFullPath());
        		out.print("\">");
        		out.print(image.getName());
        		out.print("&nbsp;(" + image.getId() + ")");
        		out.print("</a><br/>");
      		}
      	}

				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    else if ("turnOnNotifications".equals(action)) {
      UserDAO userDAO = new UserDAO();
      UserSettingDAO userSettingDAO = new UserSettingDAO();
      List<User> users = userDAO.listAll();
      for (User user : users) {
        UserSetting userSetting = userSettingDAO.findByUserAndKey(user, UserSettingKey.MAIL_COMMENT_REPLY);
        if (userSetting == null) {
          userSettingDAO.create(user, UserSettingKey.MAIL_COMMENT_REPLY, "1");
        }
        else {
          userSettingDAO.updateValue(userSetting, "1");
        }
      }
    }
    else {
      throw new IllegalArgumentException("Invalid system action: " + action);
    }
  }

}