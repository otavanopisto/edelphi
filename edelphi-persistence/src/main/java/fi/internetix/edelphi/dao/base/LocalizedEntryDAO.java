package fi.internetix.edelphi.dao.base;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.base.LocalizedEntry;

public class LocalizedEntryDAO extends GenericDAO<LocalizedEntry> {

  public LocalizedEntry create() {
    LocalizedEntry localizedEntry = new LocalizedEntry();
    getEntityManager().persist(localizedEntry);
    return localizedEntry;
  }
  
}
