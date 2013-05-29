package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionGroupDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOptionGroup;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
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

public class GroupingThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  public GroupingThesisQueryPageHandler() {
    // TODO: need title?
//    options.add(new QueryOption(QueryOptionType.QUESTION, "scale1d.label", "-- Insert text --", "panelAdmin.block.query.scale1DLabelOptionLabel", QueryOptionEditor.TEXT));
    options.add(new QueryOption(QueryOptionType.QUESTION, "grouping.groups", "panelAdmin.block.query.groupingGroupsOptionLabel", QueryOptionEditor.OPTION_SET, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "grouping.items", "panelAdmin.block.query.groupingItemsOptionLabel", QueryOptionEditor.OPTION_SET, false));
  }
  
  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    String groupingFieldValue = requestContext.getString("groupingFieldValue");
    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionGroupDAO groupDAO = new QueryOptionFieldOptionGroupDAO();
    QueryOptionFieldOptionDAO optionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionGroupOptionAnswerDAO answerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();

    String fieldName = getFieldName();
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    List<QueryOptionFieldOptionGroup> groups = groupDAO.listByQueryField(queryField); 

    JSONObject answerData = JSONObject.fromObject(groupingFieldValue);
    
    for (QueryOptionFieldOptionGroup group : groups) {
      JSONArray groupItems = answerData.getJSONArray(group.getId().toString());
      List<QueryQuestionOptionGroupOptionAnswer> oldItems = answerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, queryField, group);

      // Remove excess items from this group
      while (oldItems.size() > groupItems.size()) {
        int ind = oldItems.size() - 1;
        answerDAO.delete(oldItems.get(ind));
        oldItems.remove(ind);
      }

      // Update all existing
      for (int i = 0; i < oldItems.size(); i++) {
        String optionName = (String) groupItems.get(i);
        
        QueryOptionFieldOption option = optionDAO.findByQueryFieldAndValue(queryField, optionName);
        
        answerDAO.updateOption(oldItems.get(i), option);
      }

      // Names.size > olds.size so we create some more
      for (int i = oldItems.size(); i < groupItems.size(); i++) {
        String optionName = (String) groupItems.get(i);
        QueryOptionFieldOption option = optionDAO.findByQueryFieldAndValue(queryField, optionName);

        answerDAO.create(queryReply, queryField, option, group);
      }
    }
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionGroupOptionAnswerDAO questionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
    QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO = new QueryOptionFieldOptionGroupDAO();
//    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    
    JSONObject answerData = new JSONObject();
    
    String fieldName = getFieldName();
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
    
    List<QueryOptionFieldOptionGroup> fieldGroups = queryOptionFieldOptionGroupDAO.listByQueryField(queryField);
    List<QueryOptionFieldOption> fieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);
    
    List<QueryQuestionOptionGroupOptionAnswer> answer = questionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryField(queryReply, queryField);

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("grouping");

    requiredFragment.addAttribute("groupCount", String.valueOf(fieldGroups.size()));
    requiredFragment.addAttribute("optionCount", String.valueOf(fieldOptions.size()));

    int itemIndex = 0;
    for (QueryOptionFieldOptionGroup group : fieldGroups) {
      requiredFragment.addAttribute("group." + itemIndex + ".text", group.getName());
      requiredFragment.addAttribute("group." + itemIndex + ".name", group.getId().toString());
      
      JSONArray groupItems = new JSONArray();
      int answerOptionIndex = 0;
      for (QueryQuestionOptionGroupOptionAnswer ans : answer) {
        if (ans.getGroup().getId().equals(group.getId())) {
          requiredFragment.addAttribute("group." + itemIndex + ".option." + answerOptionIndex + ".text", ans.getOption().getText());
          requiredFragment.addAttribute("group." + itemIndex + ".option." + answerOptionIndex + ".name", ans.getOption().getValue());
          answerOptionIndex++;
          
          groupItems.add(ans.getOption().getValue());
        }
      }

      answerData.put(group.getId().toString(), groupItems);
      requiredFragment.addAttribute("group." + itemIndex + ".optionCount", String.valueOf(answerOptionIndex));
      itemIndex++;
    }
    
    itemIndex = 0;
    for (QueryOptionFieldOption option : fieldOptions) {
      requiredFragment.addAttribute("option." + itemIndex + ".text", option.getText());
      requiredFragment.addAttribute("option." + itemIndex + ".name", option.getValue());
      itemIndex++;
    }

    requiredFragment.addAttribute("groupingFieldValue", answerData.toString());
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    String fieldName = getFieldName();
    String fieldCaption = queryPage.getTitle();

    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (queryOption.isEditableWithAnswers()))
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }
    
    if (!hasAnswers) {
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO = new QueryOptionFieldOptionGroupDAO();
      QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
      
      QueryOption optionsOption = getDefinedOption("grouping.items");
      QueryOption groupsOption = getDefinedOption("grouping.groups");
      
      // TODO: Mandarory ???
      
      Boolean mandatory = false;
      
      synchronizeField(settings, queryPage, optionsOption, fieldName, fieldCaption, mandatory);
      
      // Groups
      
      // TODO: This will only work when there are absolutely no answers to the query because ordering of the fields may change due to db fetch
  
      QueryOptionField optionField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      
      List<QueryOptionFieldOptionGroup> oldGroups = queryOptionFieldOptionGroupDAO.listByQueryField(optionField);
      
      List<String> groupNames = QueryPageUtils.parseSerializedList(getStringOptionValue(queryPage, groupsOption));    
      
      // Remove excess groups
      while (oldGroups.size() > groupNames.size()) {
        int ind = oldGroups.size() - 1;
        long answerCount = queryQuestionOptionGroupOptionAnswerDAO.countByQueryOptionFieldOptionGroup(oldGroups.get(ind));
        if (answerCount == 0) {
          queryOptionFieldOptionGroupDAO.delete(oldGroups.get(ind));
        }
        else {
          queryOptionFieldOptionGroupDAO.archive(oldGroups.get(ind));
        }
        oldGroups.remove(ind);
      }
  
      // Do update for all existing
      for (int i = 0; i < oldGroups.size(); i++) {
        queryOptionFieldOptionGroupDAO.updateName(oldGroups.get(i), groupNames.get(i));
      }
  
      // Names.size > olds.size so we create some more
      for (int i = oldGroups.size(); i < groupNames.size(); i++) {
        queryOptionFieldOptionGroupDAO.create(optionField, groupNames.get(i));
      }
    } else {
      synchronizeFieldCaption(queryPage, fieldName, fieldCaption);
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
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO = new QueryOptionFieldOptionGroupDAO();
    QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();

    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(exportContext.getQueryPage().getQuerySection().getQuery(), exportContext.getStamp());
    QueryPage queryPage = exportContext.getQueryPage();
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());
    List<QueryOptionFieldOptionGroup> fieldGroups = queryOptionFieldOptionGroupDAO.listByQueryField(queryField);
    
    for (QueryOptionFieldOptionGroup fieldGroup : fieldGroups) {
      int columnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + fieldGroup.getName());
      
      for (QueryReply queryReply : queryReplies) {
        List<QueryQuestionOptionGroupOptionAnswer> answers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, queryField, fieldGroup);
        if (answers.size() > 0) {
          List<String> cellValues = new ArrayList<String>();
          
          for (QueryQuestionOptionGroupOptionAnswer answer : answers) {
            cellValues.add(answer.getOption().getText());
          }
          
          StringBuilder cellValueBuilder = new StringBuilder();
          for (int i = 0, l = cellValues.size(); i < l; i++) {
            cellValueBuilder.append(cellValues.get(i));
            if (i < (l - 1))
              cellValueBuilder.append(',');
          }
  
          exportContext.addCellValue(queryReply, columnIndex, cellValueBuilder.toString());
        }
      }
    }    
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    UserDAO userDAO = new UserDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionGroupDAO queryOptionFieldOptionGroupDAO = new QueryOptionFieldOptionGroupDAO();
    QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    
    QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, getFieldName());
    List<QueryOptionFieldOptionGroup> fieldGroups = queryOptionFieldOptionGroupDAO.listByQueryField(queryField);
    
    User loggedUser = requestContext.isLoggedIn() ? userDAO.findById(requestContext.getLoggedUserId()) : null;
    Query query = queryPage.getQuerySection().getQuery();
    QueryReply excludeReply = QueryDataUtils.findQueryReply(requestContext, loggedUser, query);
    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(query, RequestUtils.getActiveStamp(requestContext));

    RequiredQueryFragment groupFragment = new RequiredQueryFragment("report_piechart");
    
    List<QueryOptionFieldOption> queryFieldOptions = queryOptionFieldOptionDAO.listByQueryField(queryField);

    int chartIndex = 0;
    for (QueryOptionFieldOptionGroup fieldGroup : fieldGroups) {
      List<String> ids = new ArrayList<String>(queryFieldOptions.size());
      List<String> captions = new ArrayList<String>(queryFieldOptions.size());
      List<Double> values = new ArrayList<Double>(queryFieldOptions.size());
      
      for (QueryOptionFieldOption queryFieldOption : queryFieldOptions) {
        ids.add(queryFieldOption.getValue());
        captions.add(queryFieldOption.getText());
        values.add(new Double(0));
      }
      
      for (QueryReply queryReply : queryReplies) {
        if ((excludeReply == null)||(!queryReply.getId().equals(excludeReply.getId()))) {
          List<QueryQuestionOptionGroupOptionAnswer> answers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryReplyAndQueryFieldAndOptionFieldGroup(queryReply, queryField, fieldGroup);
          if (answers.size() > 0) {
            for (QueryQuestionOptionGroupOptionAnswer answer : answers) {
              int index = ids.indexOf(answer.getOption().getValue());
              values.set(index, values.get(index) + 1);
            }
          }
        }
      }
      
      groupFragment.addAttribute("chart." + chartIndex + ".id", String.valueOf(fieldGroup.getId()));
      groupFragment.addAttribute("chart." + chartIndex + ".caption", fieldGroup.getName());
      groupFragment.addAttribute("chart." + chartIndex + ".dataSetSize", String.valueOf(captions.size()));
      
      for (int i = 0, l = captions.size(); i < l; i++) {
        groupFragment.addAttribute("chart." + chartIndex + ".dataSet." + i + ".id", String.valueOf(ids.get(i)));
        groupFragment.addAttribute("chart." + chartIndex + ".dataSet." + i + ".caption", String.valueOf(captions.get(i)));
        groupFragment.addAttribute("chart." + chartIndex + ".dataSet." + i + ".value", String.valueOf(values.get(i)));
      }
      
      chartIndex++;
    }
    
    groupFragment.addAttribute("chartCount", String.valueOf(chartIndex));
    
    addRequiredFragment(requestContext, groupFragment);
  }
  
  private String getFieldName() {
    return "grouping";
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
}