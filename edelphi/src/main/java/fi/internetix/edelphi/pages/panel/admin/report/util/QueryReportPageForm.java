package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.List;

public class QueryReportPageForm extends QueryReportPage {

  public QueryReportPageForm(Long queryPageId, String title, String jspFile) {
    super(queryPageId, title, jspFile);
  }
  
  public List<FormFieldAnswerBean> getFields() {
    return fields;
  }
  
  public void addField(FormFieldAnswerBean field) {
    fields.add(field);
  }

  public void addFields(List<FormFieldAnswerBean> fields) {
    this.fields.addAll(fields);
  }

  private List<FormFieldAnswerBean> fields = new ArrayList<FormFieldAnswerBean>();
}
