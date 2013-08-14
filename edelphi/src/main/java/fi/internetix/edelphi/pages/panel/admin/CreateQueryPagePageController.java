package fi.internetix.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.List;

import fi.internetix.edelphi.dao.querylayout.QueryPageTemplateDAO;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageTemplate;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.LocalizationUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class CreateQueryPagePageController extends PanelPageController {

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryPageTemplateDAO queryPageTemplateDAO = new QueryPageTemplateDAO();
    
//    ActionUtils.includeRoleAccessList(pageRequestContext);

    List<QueryPageTemplateBean> queryPageTemplateBeans = new ArrayList<CreateQueryPagePageController.QueryPageTemplateBean>();

    List<QueryPageTemplate> queryPageTemplates = queryPageTemplateDAO.listAll();
    for (QueryPageTemplate queryPageTemplate : queryPageTemplates) {
      String name = LocalizationUtils.getLocalizedText(queryPageTemplate.getName(), pageRequestContext.getRequest().getLocale());
      String description = LocalizationUtils.getLocalizedText(queryPageTemplate.getDescription(), pageRequestContext.getRequest().getLocale());
      queryPageTemplateBeans.add(new QueryPageTemplateBean(queryPageTemplate.getId(), name, queryPageTemplate.getIconName(), description));
    }
    
    pageRequestContext.getRequest().setAttribute("queryPageTemplates", queryPageTemplateBeans);
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/createquerypage.jsp");
  }
  
  public class QueryPageTemplateBean {
    
    public QueryPageTemplateBean(Long id, String name, String iconName, String description) {
      this.id = id;
      this.name = name;
      this.iconName = iconName;
      this.description = description;
    }
    
    public Long getId() {
      return id;
    }
    
    public String getName() {
      return name;
    }
    
    public String getIconName() {
      return iconName;
    }

    public String getDescription() {
      return description;
    }

    private Long id;
    private String name;
    private String iconName;
    private String description;
  }
}