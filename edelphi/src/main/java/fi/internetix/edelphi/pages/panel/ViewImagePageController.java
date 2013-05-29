package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.ImageDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Image;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ViewImagePageController extends PanelPageController {

  public ViewImagePageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) { 
    // TODO: If query is hidden only users with manage material rights should be able to enter
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    ImageDAO imageDAO = new ImageDAO();
    Long imageId = pageRequestContext.getLong("imageId");
    Image image = imageDAO.findById(imageId);
    if (image == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("image", image);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewimage.jsp");
  }
}
