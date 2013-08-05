package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryNumericField;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportUtils;
import fi.internetix.edelphi.query.QueryExportContext;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.QueryOptionEditor;
import fi.internetix.edelphi.query.QueryOptionType;
import fi.internetix.edelphi.query.RequiredQueryFragment;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;
import fi.internetix.smvc.logging.Logging;

public class TimelineThesisQueryPageHandler extends AbstractThesisQueryPageHandler {
  
  public final static int TIMELINE_TYPE_1VALUE = 0;
  public final static int TIMELINE_TYPE_2VALUE = 1;
  
  public TimelineThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, "timeline.type", "panelAdmin.block.query.timelineType", QueryOptionEditor.TIMELINE_TYPE, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "timeline.value1Label", "panelAdmin.block.query.timelineValue1", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "timeline.value2Label", "panelAdmin.block.query.timelineValue2", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "timeline.min", "panelAdmin.block.query.timelineMin", QueryOptionEditor.INTEGER, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "timeline.max", "panelAdmin.block.query.timelineMax", QueryOptionEditor.INTEGER, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "timeline.step", "panelAdmin.block.query.timelineStep", QueryOptionEditor.INTEGER, false));
  }
  
  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();

    Query query = queryPage.getQuerySection().getQuery();

    // First timeline value

    String value = requestContext.getString("value1");
    Double data = StringUtils.isEmpty(value) ? null : Double.parseDouble(value); 
    QueryNumericField queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE1);
    if (queryField == null) {
      Logging.logError("Query page " + queryPage.getId() + " has no field by name " + TIMELINE_VALUE1);
      throw new IllegalArgumentException("Query field not found");
    }
    QueryQuestionNumericAnswer answer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField); 
    if (answer != null) {
      if (query.getAllowEditReply()) {
        answer = queryQuestionNumericAnswerDAO.updateData(answer, data);
      } else {
        throw new IllegalStateException("Could not save reply: Already replied");
      }
    }
    else {
      answer = queryQuestionNumericAnswerDAO.create(queryReply, queryField, data);
    }

    if (getIntegerOptionValue(queryPage, getDefinedOption("timeline.type")) == TIMELINE_TYPE_2VALUE) {
      value = requestContext.getString("value2");
      data = StringUtils.isEmpty(value) ? null : Double.parseDouble(value); 
      queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE2);
      if (queryField == null) {
        Logging.logError("Query page " + queryPage.getId() + " has no field by name " + TIMELINE_VALUE2);
        throw new IllegalArgumentException("Query field not found");
      }
      answer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField); 
      if (answer != null) {
        if (query.getAllowEditReply()) {
          answer = queryQuestionNumericAnswerDAO.updateData(answer, data);
        } else {
          throw new IllegalStateException("Could not save reply: Already replied");
        }
      }
      else {
        answer = queryQuestionNumericAnswerDAO.create(queryReply, queryField, data);
      }
    }
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    QueryNumericField queryField1 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE1);
    QueryQuestionNumericAnswer answer1 = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField1);
    String answer1Str = answer1 == null || answer1.getData() == null ? null : answer1.getData().intValue() + "";
    
    QueryNumericField queryField2 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE2);
    QueryQuestionNumericAnswer answer2 = queryField2 == null ? null : queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField2);
    String answer2Str = answer2 == null || answer2.getData() == null ? null : answer2.getData().intValue() + "";

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("timeline");
    
    requiredFragment.addAttribute("value1", answer1Str);
    requiredFragment.addAttribute("value2", answer2Str);
    requiredFragment.addAttribute("type", getStringOptionValue(queryPage, getDefinedOption("timeline.type")));
    requiredFragment.addAttribute("value1Label", getStringOptionValue(queryPage, getDefinedOption("timeline.value1Label")));
    requiredFragment.addAttribute("value2Label", getStringOptionValue(queryPage, getDefinedOption("timeline.value2Label")));
    requiredFragment.addAttribute("min", getStringOptionValue(queryPage, getDefinedOption("timeline.min")));
    requiredFragment.addAttribute("max", getStringOptionValue(queryPage, getDefinedOption("timeline.max")));

    addJsDataVariable(requestContext, getDefinedOption("timeline.step"), getStringOptionValue(queryPage, getDefinedOption("timeline.step")));

    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    // Default field options
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (queryOption.isEditableWithAnswers()))
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }
   
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryNumericFieldDAO queryNumericFieldDAO = new QueryNumericFieldDAO();
    QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();
    
    String caption1 = getStringOptionValue(queryPage, getDefinedOption("timeline.value1Label"));
    String caption2 = getStringOptionValue(queryPage, getDefinedOption("timeline.value2Label"));

    if (hasAnswers) {
      // If page already contains answers, only captions can be updated.
      
      QueryNumericField queryNumericField1 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE1);
      queryNumericFieldDAO.updateCaption(queryNumericField1, caption1);

      if (getIntegerOptionValue(queryPage, getDefinedOption("timeline.type")) == TIMELINE_TYPE_2VALUE) {
        // If page has two fields we need to update second caption too
        QueryNumericField queryNumericField2 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE2);
        queryNumericFieldDAO.updateCaption(queryNumericField2, caption2);
      } 
    } else {
      // TODO: Mandatory
      
      Boolean mandatory1 = Boolean.FALSE;
      Boolean mandatory2 = Boolean.FALSE;
      
      // If field does not exist we create new. Otherwise we just update existing properties
      QueryNumericField queryNumericField1 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE1);
      if (queryNumericField1 == null) {
        queryNumericFieldDAO.create(queryPage, TIMELINE_VALUE1, mandatory1, caption1, null, null, 1d);
      } else {
        queryFieldDAO.updateMandatory(queryNumericField1, mandatory1);
        queryFieldDAO.updateCaption(queryNumericField1, caption1);
      }

      QueryNumericField queryNumericField2 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE2);
      if (getIntegerOptionValue(queryPage, getDefinedOption("timeline.type")) == TIMELINE_TYPE_2VALUE) {
        // if page has also second field by it does not exist yet we need to create new. Otherwise we just update existing properties
        if (queryNumericField2 == null) {
          queryNumericFieldDAO.create(queryPage, TIMELINE_VALUE2, mandatory2, caption2, null, null, 1d);
        } else {
          queryFieldDAO.updateMandatory(queryNumericField2, mandatory2);
          queryFieldDAO.updateCaption(queryNumericField2, caption2);
        }
      } else {
        // If query does not anymore have two fields but the field still exists we need to delete that deprecated field
        if (queryNumericField2 != null) {
          long answerCount = queryQuestionAnswerDAO.countByQueryField(queryNumericField2);
          if (answerCount == 0) {
            queryFieldDAO.delete(queryNumericField2);
          }
          else {
            queryFieldDAO.archive(queryNumericField2);
          }
        }
      }
    }
     
  }
  
  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    QueryNumericField queryField1 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE1);
    QueryNumericField queryField2 = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, TIMELINE_VALUE2);
    
    int columnIndex1 = exportContext.addColumn(queryPage.getTitle() + "/" + queryField1.getCaption());
    int columnIndex2 = -1;
    
    if (queryField2 != null)
      columnIndex2 = exportContext.addColumn(queryPage.getTitle() + "/" + queryField2.getCaption());
    
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionNumericAnswer answer1 = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField1);
      if ((answer1 != null) && (answer1.getData() != null)) {
        exportContext.addCellValue(queryReply, columnIndex1, Math.round(answer1.getData()));
      }

      QueryQuestionNumericAnswer answer2 = queryField2 == null ? null : queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField2);
      if ((answer2 != null) && (answer2.getData() != null)) {
        exportContext.addCellValue(queryReply, columnIndex2, Math.round(answer2.getData()));
      }
    }
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    UserDAO userDAO = new UserDAO();

    Double min = QueryPageUtils.getDoubleSetting(queryPage, "timeline.min");
    Double max = QueryPageUtils.getDoubleSetting(queryPage, "timeline.max");
    Double step = QueryPageUtils.getDoubleSetting(queryPage, "timeline.step");
    int type = QueryPageUtils.getIntegerSetting(queryPage, "timeline.type");
    Query query = queryPage.getQuerySection().getQuery();
    
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(query, RequestUtils.getActiveStamp(requestContext));
    List<QueryReply> includeReplies = new ArrayList<QueryReply>();
    User loggedUser = requestContext.isLoggedIn() ? userDAO.findById(requestContext.getLoggedUserId()) : null;
    QueryReply excludeReply = QueryDataUtils.findQueryReply(requestContext, loggedUser, query);
    
    if (excludeReply != null) {
      for (QueryReply queryReply : queryReplies) {
        if (!queryReply.getId().equals(excludeReply.getId())) {
          includeReplies.add(queryReply); 
        }
      }
    } else {
      includeReplies.addAll(queryReplies); 
    }
    
    int valuesCount = (int) Math.round(Math.ceil((max - min) / step));

    if (type == TIMELINE_TYPE_2VALUE) {
      Double[][] values = new Double[valuesCount + 1][valuesCount + 1];
      QueryField xField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value1");
      List<Double> xValues = ReportUtils.getNumberFieldData(xField, includeReplies);
      QueryField yField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value2");
      List<Double> yValues = ReportUtils.getNumberFieldData(yField, includeReplies);
      for (int i = 0, l = Math.min(xValues.size(), yValues.size()); i < l; i++) {
        Double xValue = xValues.get(i);
        Double yValue = yValues.get(i);
        if (xValue != null && yValue != null) {
          int x = (int) (xValue - min / step);
          int y = (int) (yValue - min / step);
          values[x][y] = new Double(values[x][y] != null ? values[x][y] + 1 : 1);
        }
      }

      RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_bubblechart");
      
      requiredFragment.addAttribute("xAxisLabel", xField.getCaption());
      requiredFragment.addAttribute("yAxisLabel", yField.getCaption());
      requiredFragment.addAttribute("xValueCount", String.valueOf(valuesCount));
      requiredFragment.addAttribute("yValueCount", String.valueOf(valuesCount));
      
      for (int x = 0; x < valuesCount; x++) {
        for (int y = 0; y < valuesCount; y++) {
          if (values[x][y] != null) {
            requiredFragment.addAttribute("bubble." + x + "." + y + ".x", String.valueOf((x + min) * step));
            requiredFragment.addAttribute("bubble." + x + "." + y + ".y", String.valueOf((y + min) * step));
            requiredFragment.addAttribute("bubble." + x + "." + y + ".value", String.valueOf(values[x][y]));
          }
        }
      }
      
      addRequiredFragment(requestContext, requiredFragment);
    } else {
      RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_barchart");
      QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, "timeline.value1");
      List<Double> data = ReportUtils.getNumberFieldData(queryField, includeReplies);
      Map<Double, Long> classifiedData = ReportUtils.getClassifiedNumberFieldData(data);
      
      requiredFragment.addAttribute("valueCount", String.valueOf(valuesCount));
      int i = 0;
      for (double d = min; d <= max; d += step) {
        if (classifiedData.containsKey(d)) {
          requiredFragment.addAttribute("name." + i, "reportValue." + String.valueOf(Math.round(d)));
          requiredFragment.addAttribute("value." + i, classifiedData.containsKey(d) ? String.valueOf(classifiedData.get(d)) : "0");
        }
        i++;
      }
      
      addRequiredFragment(requestContext, requiredFragment);
    }
    
  }

  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
  
  private static final String TIMELINE_VALUE1 = "timeline.value1";
  private static final String TIMELINE_VALUE2 = "timeline.value2";

}