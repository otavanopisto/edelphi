package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

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
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryFieldDataStatistics;
import fi.internetix.edelphi.query.QueryExportContext;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.QueryOptionEditor;
import fi.internetix.edelphi.query.QueryOptionType;
import fi.internetix.edelphi.query.RequiredQueryFragment;
import fi.internetix.edelphi.utils.MathUtils;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.ReportUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;
import fi.internetix.smvc.logging.Logging;

public class TimeSerieThesisQueryPageHandler extends AbstractThesisQueryPageHandler {

  public TimeSerieThesisQueryPageHandler() {
    // TODO: Localize defaults
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.maxY", "panelAdmin.block.query.timeSerieMaxYOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.maxX", "panelAdmin.block.query.timeSerieMaxXOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.minY", "panelAdmin.block.query.timeSerieMinYOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.minX", "panelAdmin.block.query.timeSerieMinXOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.stepX", "panelAdmin.block.query.timeSerieStepXOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.stepY", "panelAdmin.block.query.timeSerieStepYOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.userStepX", "panelAdmin.block.query.timeSerieUserStepXOptionLabel", QueryOptionEditor.FLOAT, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.yTickDecimals", "panelAdmin.block.query.timeSerieYTickDecimalsOptionLabel", QueryOptionEditor.INTEGER, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.xAxisTitle", "panelAdmin.block.query.timeSerieXAxisTitleOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.yAxisTitle", "panelAdmin.block.query.timeSerieYAxisTitleOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.predefinedSetLabel", "panelAdmin.block.query.timeSeriePredefinedSetOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.userSetLabel", "panelAdmin.block.query.timeSerieUserSetOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "time_serie.predefinedValues", "panelAdmin.block.query.timeSeriePredefinedValuesOptionLabel", QueryOptionEditor.TIME_SERIE_DATA, false));
  }
  
  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("time_serie");
    int predefinedValueCount = 0;
    
    List<QueryOption> definedOptions = getDefinedOptions();
    for (QueryOption definedOption : definedOptions) {
      String settingValue = getStringOptionValue(queryPage, definedOption);

      switch (definedOption.getType()) {
      case QUESTION:
        switch (definedOption.getEditor()) {
        case BOOLEAN:
          addJsDataVariable(requestContext, definedOption, "1".equals(settingValue) ? "1" : "0");
          break;
        case TIME_SERIE_DATA:
          NavigableMap<String, String> timeSerieData = getMapOptionValue(queryPage, definedOption);
          
          addJsDataVariable(requestContext, definedOption.getName() + ".count", String.valueOf(timeSerieData.size()));

          NavigableSet<String> xs = timeSerieData.navigableKeySet();

          int i = 0;
          for (String x : xs) {
            String y = timeSerieData.get(x);
            y = StringUtils.isNotBlank(y) ? y.replaceAll(",", ".") : null;
            
            addJsDataVariable(requestContext, definedOption.getName() + "." + i + ".x", x);
            addJsDataVariable(requestContext, definedOption.getName() + "." + i + ".y", y);
            
            if (y != null) {
              predefinedValueCount = i;
            }
            
            i++;
          }
          break;
        default:
          addJsDataVariable(requestContext, definedOption, settingValue);
          break;
        }
        break;
				default:
				break;
      }
    }

    Double minX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.minX"));
    Double maxX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.maxX"));
    Double stepX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.stepX"));
    Double userStepX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.userStepX"));
    if (userStepX == null)
      userStepX = stepX;

    if (minX != null && maxX != null && userStepX != null) {
      minX = getUserAnswerMinX(getPredefinedValues(queryPage), minX);

      int i = 0;
      for (Double x = minX + (predefinedValueCount > 0 ? userStepX : 0); x <= maxX; x += userStepX) {
        String fieldName = getFieldName(x);
        Double fieldValue = null;

        if (queryReply != null) {
          QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
          QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
          if (numericAnswer != null)
            fieldValue = numericAnswer.getData();
        }

        requiredFragment.addAttribute("fieldName." + i, fieldName);
        if (fieldValue != null)
          requiredFragment.addAttribute("fieldValue." + i, String.valueOf(fieldValue));
        i++;
      }
      
      requiredFragment.addAttribute("fieldCount", String.valueOf(i));
    }

    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    List<QueryOption> definedOptions = getDefinedOptions();

    for (QueryOption definedOption : definedOptions) {
      if (definedOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (definedOption.isEditableWithAnswers())) {
          String value = settings.get(definedOption.getName());

          switch (definedOption.getEditor()) {
            case BOOLEAN:
              QueryPageUtils.setSetting(queryPage, definedOption.getName(), "1".equals(value) ? "1" : "0", modifier);
            break;
            default:
              QueryPageUtils.setSetting(queryPage, definedOption.getName(), value, modifier);
            break;
          }
        }
      }
    }
    
    if (!hasAnswers) {
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryNumericFieldDAO queryNumericFieldDAO = new QueryNumericFieldDAO();
      QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();

      Set<String> deprecatedFieldNames = new HashSet<String>();
  
      List<QueryField> existingFields = queryFieldDAO.listByQueryPage(queryPage);
      for (QueryField existingField : existingFields) {
        if (!deprecatedFieldNames.contains(existingField.getName())) {
          deprecatedFieldNames.add(existingField.getName());
        }
      }
  
      Double maxX = null;
      Double minX = null;
      Double minY = null;
      Double maxY = null;
      Double stepX = null;
      Double stepY = null;
      Double userStepX = null;
  
      // Settings saving
  
      for (QueryOption definedOption : definedOptions) {
        if (definedOption.getType() == QueryOptionType.QUESTION) {
          if ("time_serie.maxX".equals(definedOption.getName()))
            maxX = getDoubleOptionValue(queryPage, definedOption);
          else if ("time_serie.minX".equals(definedOption.getName()))
            minX = getDoubleOptionValue(queryPage, definedOption);
          else if ("time_serie.maxY".equals(definedOption.getName()))
            maxY = getDoubleOptionValue(queryPage, definedOption);
          else if ("time_serie.minY".equals(definedOption.getName()))
            minY = getDoubleOptionValue(queryPage, definedOption);
          else if ("time_serie.stepX".equals(definedOption.getName()))
            stepX = getDoubleOptionValue(queryPage, definedOption);
          else if ("time_serie.stepY".equals(definedOption.getName()))
            stepY = getDoubleOptionValue(queryPage, definedOption);
          else if ("time_serie.userStepX".equals(definedOption.getName()))
            userStepX = getDoubleOptionValue(queryPage, definedOption);
        }
      }
  
      // We assume that undefined steps are 1
      if (stepX == null)
        stepX = 1.0;
  
      if (stepY == null)
        stepY = 1.0;
  
      // If user step is not defined we assume that it is the same as stepX
      if (userStepX == null)
        userStepX = stepX;
  
      // If some of the min/max values are missing we cannot save time serie data or query fields because
      // both of the rely on those values.
  
      if ((minX != null) && (maxX != null) && (minY != null) && (maxY != null)) {
        // TODO: Mandarory ???
  
        Boolean mandatory = false;
        NavigableMap<String,String> predefinedValues = getPredefinedValues(queryPage);
        int predefinedValueCount = getPredefinedValueCount(predefinedValues);
        minX = getUserAnswerMinX(predefinedValues, minX);
        
        for (Double x = minX + (predefinedValueCount > 0 ? userStepX : 0); x <= maxX; x += userStepX) {
          String caption = String.valueOf(x);
  
          String fieldName = getFieldName(x);
  
          QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
          if (queryNumericField != null) {
            // If field already exists we just need to update possible changes to it
            if (!queryNumericField.getMax().equals(maxY)) {
              queryNumericFieldDAO.updateMax(queryNumericField, maxY);
            }
            if (!queryNumericField.getMin().equals(minY)) {
              queryNumericFieldDAO.updateMin(queryNumericField, minY);
            }
            if (!queryNumericField.getPrecision().equals(stepY)) {
              queryNumericFieldDAO.updatePrecision(queryNumericField, stepY);
            }
  
            queryFieldDAO.updateCaption(queryNumericField, caption);
          } else {
            // If field does not exist we create new one
            queryNumericFieldDAO.create(queryPage, fieldName, mandatory, caption, minY, maxY, stepY);
          }
  
          if (deprecatedFieldNames.contains(fieldName)) {
            deprecatedFieldNames.remove(fieldName);
          }
        }
  
        for (String deprecatedFieldName : deprecatedFieldNames) {
          QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, deprecatedFieldName);
          long answerCount = queryQuestionAnswerDAO.countByQueryField(queryNumericField);
          if (answerCount == 0) {
            queryFieldDAO.delete(queryNumericField);
          }
          else {
            queryFieldDAO.archive(queryNumericField);
          }
        }
      }
    }
  }

  @Override
  public void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();

    Double minX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.minX"));
    Double maxX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.maxX"));
    Double stepX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.stepX"));
    Double userStepX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.userStepX"));
    if (userStepX == null)
      userStepX = stepX;

    if (minX != null && maxX != null && userStepX != null) {
      Query query = queryPage.getQuerySection().getQuery();
      NavigableMap<String,String> predefinedValues = getPredefinedValues(queryPage);
      int predefinedValueCount = getPredefinedValueCount(predefinedValues);
      minX = getUserAnswerMinX(predefinedValues, minX);

      for (Double x = minX + (predefinedValueCount > 0 ? userStepX : 0); x <= maxX; x += userStepX) {
        String fieldName = getFieldName(x);

        QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryNumericField == null) {
          Logging.logError("Query page " + queryPage.getId() + " has no field by name " + fieldName);
          throw new IllegalArgumentException("Query field not found");
        }

        Double value = requestContext.getDouble(fieldName);

        QueryQuestionNumericAnswer questionAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryNumericField);
        if (questionAnswer != null) {
          if (query.getAllowEditReply()) {
            queryQuestionNumericAnswerDAO.updateData(questionAnswer, value);
          } else {
            throw new IllegalStateException("Could not save reply: Already replied");
          }
        } else {
          questionAnswer = queryQuestionNumericAnswerDAO.create(queryReply, queryNumericField, value);
        }
      }

    } else {
      throw new IllegalArgumentException("Could not save reply, query metadata is incomplete");
    }
  }

  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();

    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    Double minX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.minX"));
    Double maxX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.maxX"));
    Double stepX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.stepX"));
    Double userStepX = getDoubleOptionValue(queryPage, getDefinedOption("time_serie.userStepX"));
    
    if (userStepX == null)
      userStepX = stepX;

    if (minX != null && maxX != null && userStepX != null) {
      NavigableMap<String,String> predefinedValues = getPredefinedValues(queryPage);
      int predefinedValueCount = getPredefinedValueCount(predefinedValues);
      minX = getUserAnswerMinX(predefinedValues, minX);

      for (Double x = minX + (predefinedValueCount > 0 ? userStepX : 0); x <= maxX; x += userStepX) {
        String fieldName = getFieldName(x);

        QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        
        int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + queryNumericField.getCaption());
        for (QueryReply queryReply : queryReplies) {
          QueryQuestionNumericAnswer answer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryNumericField);
          if (answer != null && answer.getData() != null)
            exportContext.addCellValue(queryReply, columnIndex, answer.getData());
        }
      }
    }
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_linechart");
    
    Locale locale = requestContext.getRequest().getLocale();
    UserDAO userDAO = new UserDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    
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
    
    Double minX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.minX");
    Double maxX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.maxX");
    Double stepX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.stepX");
    Double userStepX = QueryPageUtils.getDoubleSetting(queryPage, "time_serie.userStepX");
    if (userStepX == null) {
      userStepX = stepX;
    }
    double stepGCD = MathUtils.getGCD(stepX, userStepX);
    
    int valueCount = new Double(Math.ceil((maxX - minX) / stepGCD)).intValue() + 1;
    
    List<String> categoryCaptions = new ArrayList<String>(valueCount);
    List<Double> preliminaryValues = new ArrayList<Double>(valueCount);
    List<Double> averageValues = new ArrayList<Double>(valueCount);
    List<Double> q1Values = new ArrayList<Double>(valueCount);
    List<Double> q3Values = new ArrayList<Double>(valueCount);
    List<Double> minValues = new ArrayList<Double>(valueCount);
    List<Double> maxValues = new ArrayList<Double>(valueCount);
    
    for (int i = 0; i < valueCount; i++) {
      categoryCaptions.add(null);
      preliminaryValues.add(null);
      averageValues.add(null);
      q1Values.add(null);
      q3Values.add(null);
      minValues.add(null);
      maxValues.add(null);
    }
    
    NavigableMap<String, String> predefinedValuesStringMap = QueryPageUtils.getMapSetting(queryPage, "time_serie.predefinedValues");
    int predefinedCount = 0;
    Double lastPredefinedValue = null;
    Double lastPredefinedX = null;
    NavigableMap<Double, Double> predefinedValuesMap = new TreeMap<Double, Double>();
    
    Iterator<String> stringMapIterator = predefinedValuesStringMap.keySet().iterator();
    while (stringMapIterator.hasNext()) {
      String xStr = stringMapIterator.next();
      String yStr = predefinedValuesStringMap.get(xStr);
      Double y = StringUtils.isNotBlank(yStr) ? NumberUtils.createDouble(yStr.replaceAll(",", ".")) : null;
      predefinedValuesMap.put(NumberUtils.createDouble(xStr), y);
    }
    
    for (double x = minX; x <= maxX; x+=stepGCD) {
      int index = (int) Math.round((x - minX) / stepGCD);
      Double y = predefinedValuesMap.get(x);
      
      if (y != null) {
        preliminaryValues.set(index, y);
        lastPredefinedValue = y;
        lastPredefinedX = x;
        predefinedCount++;
      }
      categoryCaptions.set(index, Math.floor(x) == x ? String.valueOf((int) x) : String.valueOf(x));
    }

    if (predefinedCount > 0) {
      // last predefined value is set as first value in all series connecting the predefined line with the actual series
      int lastPredefinedIndex = (int) Math.round((lastPredefinedX - minX) / stepGCD); 
      averageValues.set(lastPredefinedIndex, lastPredefinedValue);
      q1Values.set(lastPredefinedIndex, lastPredefinedValue);
      q3Values.set(lastPredefinedIndex, lastPredefinedValue);
      minValues.set(lastPredefinedIndex, lastPredefinedValue);
      maxValues.set(lastPredefinedIndex, lastPredefinedValue);
    }

    for (Double x = Math.max(minX, lastPredefinedX != null ? lastPredefinedX + userStepX: 0); x <= maxX; x += userStepX) {
      String fieldName = getFieldName(x);
      QueryNumericField queryField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName); 
      List<Double> numberFieldData = ReportUtils.getNumberFieldData(queryField, includeReplies);
      int index = (int) Math.round((x - minX) / stepGCD);

      QueryFieldDataStatistics statistics = new QueryFieldDataStatistics(numberFieldData);

      averageValues.set(index, statistics.getAvg());
      q1Values.set(index, statistics.getQ1());
      q3Values.set(index, statistics.getQ3());
      minValues.set(index, statistics.getMin());
      maxValues.set(index, statistics.getMax());
    }

    String predefinedValuesCaption = QueryPageUtils.getSetting(queryPage, "time_serie.predefinedSetLabel");
    
    requiredFragment.addAttribute("title", queryPage.getTitle());
    requiredFragment.addAttribute("replyCount", String.valueOf(includeReplies.size()));
    requiredFragment.addAttribute("userDataSetLabel", QueryPageUtils.getSetting(queryPage, "time_serie.userSetLabel"));
    
    for (int i = 0, l = categoryCaptions.size(); i < l; i++) {
      requiredFragment.addAttribute("tickLabel" + "." + i, String.valueOf(categoryCaptions.get(i)));
    }
    requiredFragment.addAttribute("tickLabelCount", String.valueOf(categoryCaptions.size()));
    
    addReportSerie(requiredFragment, 0, predefinedValuesCaption, preliminaryValues);
    addReportSerie(requiredFragment, 1, Messages.getInstance().getText(locale, "panelAdmin.block.query.timeSerieLiveReportAverageValuesCaption"), averageValues);
    addReportSerie(requiredFragment, 2, Messages.getInstance().getText(locale, "panelAdmin.block.query.timeSerieLiveReportMinValuesValuesCaption"), minValues);
    addReportSerie(requiredFragment, 3, Messages.getInstance().getText(locale, "panelAdmin.block.query.timeSerieLiveReportMaxValuesValuesCaption"), maxValues);
    
    requiredFragment.addAttribute("serie." + "count", "4");
    
    addRequiredFragment(requestContext, requiredFragment);
  }
  
  private void addReportSerie(RequiredQueryFragment requiredFragment, int index, String caption, List<Double> list) {
    for (int i = 0, l = list.size(); i < l; i++) {
      requiredFragment.addAttribute("serie." + index + "." + i, String.valueOf(list.get(i)));
    }
    
    requiredFragment.addAttribute("serie." + index + ".count", String.valueOf(list.size()));
    requiredFragment.addAttribute("serie." + index + ".caption", caption);
  }
  
  private NavigableMap<String, String> getPredefinedValues(QueryPage queryPage) {
    QueryOption predefinedValuesOption = getDefinedOption("time_serie.predefinedValues");
    if (predefinedValuesOption != null) {
      return getMapOptionValue(queryPage, predefinedValuesOption);
    }
    
    return null;
  }

  private Double getUserAnswerMinX(NavigableMap<String, String> predefinedValues, Double minX) {
    for (String key : predefinedValues.navigableKeySet()) {
      if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(predefinedValues.get(key))) {
        minX = Math.max(minX, NumberUtils.createDouble(key));
      }
    }

    return minX;
  }
  
  private int getPredefinedValueCount(NavigableMap<String, String> predefinedValues) {
    Set<String> keySet = predefinedValues.keySet();
    
    int predefinedValueCount = 0;
    int i = 0;
    
    for (String x : keySet) {
      String y = predefinedValues.get(x);
      
      if (y != null) {
        predefinedValueCount = i;
      }
      
      i++;
    }
    
    return predefinedValueCount;
  }

  private String getFieldName(Double x) {
    return "time_serie." + x;
  }

  private List<QueryOption> options = new ArrayList<QueryOption>();
}
