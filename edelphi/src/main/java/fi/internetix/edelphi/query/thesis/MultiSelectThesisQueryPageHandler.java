package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
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

public class MultiSelectThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  public MultiSelectThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, "multiselect.options", "panelAdmin.block.query.multiselectOptionsOptionLabel", QueryOptionEditor.OPTION_SET, false));
  }
  
  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    String fieldName = getFieldName();

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    Query query = queryPage.getQuerySection().getQuery();

    List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(queryField);
    QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
    Set<QueryOptionFieldOption> newOptions = new HashSet<QueryOptionFieldOption>();

    for (QueryOptionFieldOption option : options) {
      String paramName = MULTISELECT_OPTION_NAMEPREFIX + "." + option.getValue();
      String value = requestContext.getString(paramName);

      if ((value != null) && (value.equals(option.getValue()))) {
        newOptions.add(option);
      }
    }

    if (answer != null) {
      if (query.getAllowEditReply()) {
        answer = queryQuestionMultiOptionAnswerDAO.updateOptions(answer, newOptions);
      } else {
        throw new IllegalStateException("Could not save reply: Already replied");
      }
    } else {
      answer = queryQuestionMultiOptionAnswerDAO.create(queryReply, queryField, newOptions);
    }
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    String fieldName = getFieldName();

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);

    List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(queryField);

    QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);

    renderMultiselectList(requestContext, options, answer);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();

    String fieldName = getFieldName();
    String fieldCaption = queryPage.getTitle();
    
    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (queryOption.isEditableWithAnswers()))
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }
        
    if (!hasAnswers) {
      QueryOptionFieldDAO queryOptionFieldDAO = new QueryOptionFieldDAO();
      QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
      QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
  
      QueryOption optionsOption = getDefinedOption("multiselect.options");
  
      List<String> oldOptions = getListOptionValue(queryPage, optionsOption);
      List<String> oldOptionValues = new ArrayList<String>();
      for (int i = 0, l = oldOptions.size(); i < l; i++) {
        oldOptionValues.add(String.valueOf(i));
      }
  
      // TODO: Mandarory ???
  
      Boolean mandatory = false;
  
      // TODO: Test with instrumented entities
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      if (queryField != null) {
        queryFieldDAO.updateMandatory(queryField, mandatory);
        queryFieldDAO.updateCaption(queryField, fieldCaption);
      } else {
        queryField = queryOptionFieldDAO.create(queryPage, fieldName, mandatory, queryPage.getTitle());
      }
  
      int i = 0;
      List<String> options = QueryPageUtils.parseSerializedList(settings.get(optionsOption.getName()));
      for (String option : options) {
        String optionValue = String.valueOf(i);
  
        QueryOptionFieldOption optionFieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, optionValue);
        if (optionFieldOption == null) {
          queryOptionFieldOptionDAO.create(queryField, option, optionValue);
        } else {
          queryOptionFieldOptionDAO.updateText(optionFieldOption, option);
        }
  
        oldOptionValues.remove(optionValue);
  
        i++;
      }
  
      for (String optionValue : oldOptionValues) {
        QueryOptionFieldOption optionFieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, optionValue);
        if (optionFieldOption != null) {
          long answerCount = queryQuestionOptionAnswerDAO.countByQueryOptionFieldOption(optionFieldOption);
          if (answerCount == 0) {
            queryOptionFieldOptionDAO.delete(optionFieldOption);
          }
          else {
            queryOptionFieldOptionDAO.archive(optionFieldOption);
          }
        }
      }
    } else {
      QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      queryFieldDAO.updateCaption(queryField, fieldCaption);
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
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();

    QueryPage queryPage = exportContext.getQueryPage();

    QueryOption optionsOption = getDefinedOption("multiselect.options");
    List<String> options = getListOptionValue(queryPage, optionsOption);

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());
    List<QueryReply> queryReplies = exportContext.getQueryReplies();

    int value = 0;
    for (String option : options) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + option);

      for (QueryReply queryReply : queryReplies) {
        QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
        if (answer != null) {
          Set<QueryOptionFieldOption> selectedOptions = answer.getOptions();
  
          for (QueryOptionFieldOption selectedOption : selectedOptions) {
            if (selectedOption.getValue().equals(String.valueOf(value))) {
              exportContext.addCellValue(queryReply, columnIndex, "1");
              break;
            }
          }
        }
      }
      
      value++;
    }
    
    Messages messages = Messages.getInstance();
    Locale locale = exportContext.getLocale();
    int commentColumnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + messages.getText(locale, "panelAdmin.query.export.comment")); 
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
      exportContext.addCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
    }
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    UserDAO userDAO = new UserDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_barchart");
    
    String fieldName = getFieldName();
    Query query = queryPage.getQuerySection().getQuery();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(queryField);
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
    
    List<String> ids = new ArrayList<String>(options.size());
    List<Integer> values = new ArrayList<Integer>(options.size());
    
    for (QueryOptionFieldOption option : options) {
      ids.add(option.getValue());
      values.add(new Integer(0));
    }
    
    for (QueryReply queryReply : includeReplies) {
      QueryQuestionMultiOptionAnswer answer = queryQuestionMultiOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
      if (answer != null) {
        Set<QueryOptionFieldOption> selectedOptions = answer.getOptions();

        for (QueryOptionFieldOption selectedOption : selectedOptions) {
          int index = ids.indexOf(selectedOption.getValue());
          values.set(index, values.get(index) + 1); 
        }
      }
    }
    
    requiredFragment.addAttribute("axisLabel", queryField.getCaption());
    requiredFragment.addAttribute("valueCount", String.valueOf(values.size()));
    for (int i = 0, l = values.size(); i < l; i++) {
      requiredFragment.addAttribute("name." + i, "reportValue." + String.valueOf(Math.round(i)));
      requiredFragment.addAttribute("value." + i, String.valueOf(values.get(i)));
    }
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  private String getFieldName() {
    return "multiselect";
  }

  private List<QueryOption> options = new ArrayList<QueryOption>();
}