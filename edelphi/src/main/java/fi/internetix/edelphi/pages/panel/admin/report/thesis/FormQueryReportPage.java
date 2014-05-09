package fi.internetix.edelphi.pages.panel.admin.report.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eclipse.birt.chart.model.Chart;

import fi.internetix.edelphi.dao.querydata.QueryQuestionTextAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.ChartModelProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.FormFieldAnswerBean;
import fi.internetix.edelphi.pages.panel.admin.report.util.FormQueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.OptionFieldAnswerBean;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.TextFieldAnswerBean;
import fi.internetix.edelphi.query.form.FormFieldType;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.ReportUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.RequestContext;

public class FormQueryReportPage extends QueryReportPageController {

  public FormQueryReportPage() {
    super(QueryPageType.FORM);
  }

  @Override
  public QueryReportPageData loadPageData(RequestContext requestContext, ReportContext reportContext, QueryPage queryPage) {
    /**
     * Load fields on page
     */
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();

    String fieldsSetting = QueryPageUtils.getSetting(queryPage, "form.fields");
    JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);

    PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    
    List<FormFieldAnswerBean> beans = new ArrayList<FormFieldAnswerBean>();
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStampAndArchived(queryPage.getQuerySection().getQuery(), panelStamp, Boolean.FALSE);

    JSONObject fieldJson = null;
    for (int i = 0, l = fieldsJson.size(); i < l; i++) {
      fieldJson = fieldsJson.getJSONObject(i);
      FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));

      String name = fieldJson.getString("name");
      String fieldName = "form." + name;

      FormFieldAnswerBean mrBean;
      QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      if (queryField == null) {
        throw new IllegalArgumentException("Field '" + fieldName + "' not found");
      }
      else {
        switch (fieldType) {
        case MEMO:
        case TEXT:
          for (QueryReply queryReply : queryReplies) {
            QueryQuestionTextAnswer answer = queryQuestionTextAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
            if (answer != null && answer.getData() != null) {
              mrBean = new TextFieldAnswerBean(fieldType.toString(), queryField.getCaption(), answer.getData(), i, answer.getQueryReply().getId());
              beans.add(mrBean);
            }
          }
          break;
        case LIST:
          QueryOptionField queryOptionField = (QueryOptionField) queryField;

          mrBean = new OptionFieldAnswerBean(fieldType.toString(), queryOptionField, i);
          beans.add(mrBean);
          break;
        }
      }
    }
    Collections.sort(beans, new Comparator<FormFieldAnswerBean>() {
      @Override
      public int compare(FormFieldAnswerBean o1, FormFieldAnswerBean o2) {
        return o1.getReplyId() == null || o2.getReplyId() == null || o1.getReplyId().equals(o2.getReplyId())
            ? o1.getFieldIndex().compareTo(o2.getFieldIndex())
            : o1.getReplyId().compareTo(o2.getReplyId());
      }
    });

    return new FormQueryReportPageData(queryPage, "/jsp/blocks/panel_admin_report/thesis_form.jsp", beans);
  }

  @Override
  public Chart constructChart(ChartContext chartContext, QueryPage queryPage) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryOptionFieldDAO optionFieldDAO = new QueryOptionFieldDAO();

    // TODO: Rights check
    Long queryFieldId = chartContext.getLong("queryFieldId");
    QueryOptionField queryOptionField = optionFieldDAO.findById(queryFieldId);
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryOptionField);

    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext.getReportContext());
    Map<Long, Long> data = ReportUtils.getOptionListData(queryOptionField, queryFieldOptions, queryReplies);

    List<Double> values = new ArrayList<Double>();
    List<String> categoryCaptions = new ArrayList<String>();

    for (QueryOptionFieldOption optionFieldOption : queryFieldOptions) {
      Long optionId = optionFieldOption.getId();

      categoryCaptions.add(optionFieldOption.getText());
      values.add(new Double(data.get(optionId)));
    }

    QueryFieldDataStatistics statistics = ReportUtils.getOptionListStatistics(queryFieldOptions, data);

    Double avg = statistics.getCount() > 1 ? statistics.getAvg() : null;
    Double q1 = statistics.getCount() >= 5 ? statistics.getQ1() : null;
    Double q3 = statistics.getCount() >= 5 ? statistics.getQ3() : null;

    return ChartModelProvider.createBarChart(queryOptionField.getCaption(), null, categoryCaptions, values, avg, q1, q3);
  }

}
