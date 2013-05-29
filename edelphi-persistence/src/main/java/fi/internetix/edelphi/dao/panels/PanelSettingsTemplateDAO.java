package fi.internetix.edelphi.dao.panels;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelSettingsTemplate;

public class PanelSettingsTemplateDAO extends GenericDAO<PanelSettingsTemplate> {

  public PanelSettingsTemplate updateTemplateNameAndDescription(PanelSettingsTemplate template, String templateName, String templateDesc) {
    template.setName(templateName);
    template.setDescription(templateDesc);
    getEntityManager().persist(template);
    return template;
  }

}
