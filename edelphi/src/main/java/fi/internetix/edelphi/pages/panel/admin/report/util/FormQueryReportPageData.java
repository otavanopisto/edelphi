package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.List;

import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;

public class FormQueryReportPageData extends QueryReportPageData {

  public FormQueryReportPageData(QueryPage queryPage, String jspFile, List<FormFieldAnswerBean> fields, PanelStamp panelStamp) {
    super(queryPage, jspFile, null, panelStamp);
    this.fields = fields;
  }

  public List<FormFieldAnswerBean> getFields() {
    return fields;
  }

  private final List<FormFieldAnswerBean> fields;

}
