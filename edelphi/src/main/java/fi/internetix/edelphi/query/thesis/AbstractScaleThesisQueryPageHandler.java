package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.RequiredQueryFragment;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class AbstractScaleThesisQueryPageHandler extends AbstractThesisQueryPageHandler {

  protected static final String MULTISELECT_OPTION_NAMEPREFIX = "ms";
  
  public final static int SCALE_TYPE_RADIO = 0;
  public final static int SCALE_TYPE_SLIDER = 1;
  public final static int SCALE_TYPE_GRAPH = 2;
  
  protected void renderRadioList(PageRequestContext requestContext, String name, String label, QueryOptionField queryField, QueryQuestionOptionAnswer answer) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();

    List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(queryField);

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("scale_radiolist");
    requiredFragment.addAttribute("optionsCount", String.valueOf(options.size()));
    
    boolean selectedFound = false;
    
    int i = 0;
    for (QueryOptionFieldOption option : options) {
      requiredFragment.addAttribute("option." + i + ".value", option.getValue());
      requiredFragment.addAttribute("option." + i + ".text", option.getText());
      
      if (answer != null) {
        if (option.getValue().equals(answer.getOption().getValue())) {
          requiredFragment.addAttribute("option." + i + ".selected", "1");
          selectedFound = true;
        }
      }

      i++;
    }
    
    if (selectedFound == false) {
      requiredFragment.addAttribute("option.0.selected", "1");
    }
    
    requiredFragment.addAttribute("name", name);
    requiredFragment.addAttribute("label", label);
    addRequiredFragment(requestContext, requiredFragment);
  }
  
  protected void renderSlider(PageRequestContext requestContext, String name, String label, QueryOptionField queryField, QueryQuestionOptionAnswer answer) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();

    List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(queryField);

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("scale_slider");
    requiredFragment.addAttribute("optionsCount", String.valueOf(options.size()));
    
    boolean selectedFound = false;
    
    int i = 0;
    for (QueryOptionFieldOption option : options) {
      requiredFragment.addAttribute("option." + i + ".value", option.getValue());
      requiredFragment.addAttribute("option." + i + ".text", option.getText());
      
      if (answer != null) {
        if (option.getValue().equals(answer.getOption().getValue())) {
          requiredFragment.addAttribute("option." + i + ".selected", "1");
          selectedFound = true;
        }
      }

      i++;
    }
    
    if (selectedFound == false) {
      requiredFragment.addAttribute("option.0.selected", "1");
    }
    
    requiredFragment.addAttribute("name", name);
    requiredFragment.addAttribute("label", label);
    addRequiredFragment(requestContext, requiredFragment);
  }
  
  protected void renderMultiselectList(PageRequestContext requestContext, List<QueryOptionFieldOption> options, QueryQuestionMultiOptionAnswer answer) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("multiselect");
    requiredFragment.addAttribute("optionsCount", String.valueOf(options.size()));
    
    int i = 0;

    for (QueryOptionFieldOption option : options) {
      String optionValue = option.getValue();
      String optionText = option.getText();
      String optionName = MULTISELECT_OPTION_NAMEPREFIX + "." + optionValue;

      requiredFragment.addAttribute("option." + i + ".name", optionName);
      requiredFragment.addAttribute("option." + i + ".value", optionValue);
      requiredFragment.addAttribute("option." + i + ".text", optionText);
      
      if (answer != null) {
        if (answer.getOptions().contains(option)) {
          requiredFragment.addAttribute("option." + i + ".selected", "1");
        }
      }

      i++;
    }
    
    addRequiredFragment(requestContext, requiredFragment);
  }  
  
  /**
   * Updates field caption if field can be found. Used only when query already contains replies.
   * 
   * @param queryPage query page
   * @param fieldName field name
   * @param fieldCaption field caption
   */
  protected void synchronizeFieldCaption(QueryPage queryPage, String fieldName, String fieldCaption) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    if (queryField != null)
      queryFieldDAO.updateCaption(queryField, fieldCaption);
  }
  
  /**
   * Synchronizes field meta. Should not be used when field already contains replies
   * 
   * @param settings
   * @param queryPage
   * @param optionsOption
   * @param fieldName
   * @param fieldCaption
   * @param mandatory
   */
  protected void synchronizeField(Map<String, String> settings, QueryPage queryPage, QueryOption optionsOption, String fieldName, String fieldCaption, Boolean mandatory) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldDAO queryOptionFieldDAO = new QueryOptionFieldDAO();

    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    
    // TODO: Test with instrumented entities
    
    if (queryField != null) {
      queryFieldDAO.updateMandatory(queryField, mandatory);
      queryFieldDAO.updateCaption(queryField, fieldCaption);
    } else {
      queryField = queryOptionFieldDAO.create(queryPage, fieldName, mandatory, fieldCaption);
    }

    synchronizeFieldOptions(settings, queryPage, optionsOption, queryField);
  }
  
  protected void synchronizeFieldOptions(Map<String, String> settings, QueryPage queryPage, QueryOption optionsOption, QueryOptionField queryField) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    List<String> oldOptionValues = new ArrayList<String>();
    List<QueryOptionFieldOption> oldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);
    for (QueryOptionFieldOption oldOption : oldOptions) {
      oldOptionValues.add(oldOption.getValue());
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
  }
  
  protected void saveAnswer(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply, String fieldName, String value) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    Query query = queryPage.getQuerySection().getQuery();
    
    QueryOptionFieldOption fieldOption = queryOptionFieldOptionDAO.findByQueryFieldAndValue(queryField, value);
    if (fieldOption != null) {
      QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
      if (answer != null) {
        if (query.getAllowEditReply()) {
          answer = queryQuestionOptionAnswerDAO.updateOption(answer, fieldOption);
        } else {
          throw new IllegalStateException("Could not save reply: Already replied");
        }
      } else {
        answer = queryQuestionOptionAnswerDAO.create(queryReply, queryField, fieldOption);
      }
    } else {
      // "Saving" empty option is legal
//      throw new IllegalArgumentException("Field option '" + value + "' not found");
    }
  }

}
