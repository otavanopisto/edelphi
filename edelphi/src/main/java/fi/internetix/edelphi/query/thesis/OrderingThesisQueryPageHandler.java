package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryNumericFieldDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryNumericField;
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
import fi.internetix.smvc.logging.Logging;

public class OrderingThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  // QueryPageType.THESIS_ORDER
  
  public OrderingThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, "orderingField.items", "panelAdmin.block.query.orderItemsOptionLabel", QueryOptionEditor.OPTION_SET, false));
  }

  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();

    String orderStr = requestContext.getString("order");
    
    if (orderStr != null) {
      StringTokenizer tokenizer = new StringTokenizer(orderStr, ",");
      
      int itemValue = 0;
      
      while (tokenizer.hasMoreElements()) {
        String token = tokenizer.nextToken();
        
        String fieldName = "orderItem." + token;
  
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField == null) {
          Logging.logError("Query page " + queryPage.getId() + " has no field by name " + fieldName);
          throw new IllegalArgumentException("Query field not found");
        }
        
        QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
        
        if (numericAnswer != null)
          queryQuestionNumericAnswerDAO.updateData(numericAnswer, (double) itemValue);
        else
          queryQuestionNumericAnswerDAO.create(queryReply, queryField, (double) itemValue);
            
        itemValue++;
      }
    } // TODO: is this error or not, if there by 'accident' is no options at all (?)
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("thesis_order");

    QueryOption queryOption = getDefinedOption("orderingField.items");
    
    List<String> itemList = QueryPageUtils.parseSerializedList(getStringOptionValue(queryPage, queryOption));
    List<Integer> itemIndexList = new ArrayList<Integer>();
    StringBuffer order = new StringBuffer();
    List<QueryQuestionNumericAnswer> answers;

    requiredFragment.addAttribute("optionsCount", String.valueOf(itemList.size()));
    boolean hasAnswer = false;

    if (queryReply != null) {
      List<String> fieldNames = new ArrayList<String>();
      answers = new ArrayList<QueryQuestionNumericAnswer>();
      
      for (int i = 0; i < itemList.size(); i++) {
        String fieldName = "orderItem." + i;
        fieldNames.add(fieldName);

        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName); 
        QueryQuestionNumericAnswer numericAnswer = queryQuestionNumericAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
        if ((numericAnswer != null) && (numericAnswer.getData() != null)) {
          answers.add(numericAnswer);
        }
      }
      
      hasAnswer = answers.size() == itemList.size();
      
      if (hasAnswer) {
        Collections.sort(answers, new Comparator<QueryQuestionNumericAnswer>() {
          @Override
          public int compare(QueryQuestionNumericAnswer o1, QueryQuestionNumericAnswer o2) {
            return o1.getData().compareTo(o2.getData());
          }
        });
        
        for (QueryQuestionNumericAnswer answer : answers) {
          itemIndexList.add(fieldNames.indexOf(answer.getQueryField().getName()));
        }
      }
    }
    
    if (hasAnswer) {
      int itemIndex = 0;
      for (int j = 0; j < itemIndexList.size(); j++) {
        Integer index = itemIndexList.get(j);
        
        String item = itemList.get(index.intValue());

        requiredFragment.addAttribute("item." + itemIndex + ".text", item);
        requiredFragment.addAttribute("item." + itemIndex + ".name", index.toString());
        
        if (order.length() > 0)
          order.append(',');
        order.append(index.intValue());

        itemIndex++;
      }
    }
    else {
      int itemIndex = 0;

      for (String item : itemList) {
        requiredFragment.addAttribute("item." + itemIndex + ".text", item);
        requiredFragment.addAttribute("item." + itemIndex + ".name", String.valueOf(itemIndex));

        if (order.length() > 0)
          order.append(',');
        order.append(itemIndex);

        itemIndex++;
      }
    }
    
    requiredFragment.addAttribute("order", order.toString());
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    if (!hasAnswers) {
      QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
      QueryNumericFieldDAO queryNumericFieldDAO = new QueryNumericFieldDAO();
      
      for (QueryOption queryOption : getDefinedOptions()) {
        if (queryOption.getType() == QueryOptionType.QUESTION)
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
  
      List<String> itemList = getListOptionValue(queryPage, getDefinedOption("orderingField.items"));
      
      int i = 0;
      for (String itemName : itemList) {
        String fieldName = "orderItem." + i;
  
        // TODO: ?
        Boolean mandatory = Boolean.FALSE;
        
        QueryNumericField queryNumericField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
  
        if (queryNumericField != null) {
          queryFieldDAO.updateCaption(queryNumericField, itemName);
          queryFieldDAO.updateMandatory(queryNumericField, mandatory);
        } else {
          queryNumericFieldDAO.create(queryPage, fieldName, mandatory, itemName, null, null, 1d);
        }
        
        i++;
      }
    }
  }

  @Override
  public void exportData(QueryExportContext exportContext) {
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    
    List<QueryReply> queryReplies = exportContext.getQueryReplies();
    
    QueryPage queryPage = exportContext.getQueryPage();

    Messages messages = Messages.getInstance();
    Locale locale = exportContext.getLocale();
    
    int columnIndex = exportContext.addColumn(queryPage.getTitle());
    int commentColumnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + messages.getText(locale, "panelAdmin.query.export.comment")); 
    
    for (QueryReply queryReply : queryReplies) {
      List<QueryQuestionNumericAnswer> answers = queryQuestionNumericAnswerDAO.listByQueryReplyAndQueryPageOrderByData(queryReply, queryPage);
      StringBuilder cellValueBuilder = new StringBuilder();
      for (int i = 0, l = answers.size(); i < l; i++) {
        QueryNumericField queryNumericField =  (QueryNumericField) answers.get(i).getQueryField();
        
        cellValueBuilder.append(queryNumericField.getCaption());
        if (i < (l - 1))
          cellValueBuilder.append(',');
      }

      exportContext.addCellValue(queryReply, columnIndex, cellValueBuilder.toString());

      QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
      exportContext.addCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
    }
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    UserDAO userDAO = new UserDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_stackedbarchart");

    Query query = queryPage.getQuerySection().getQuery();
    List<List<Double>> stackedSeries = new ArrayList<List<Double>>();
    List<String> items = getListOptionValue(queryPage, getDefinedOption("orderingField.items"));
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

    for (int i = 0, l = items.size(); i < l; i++) {
      List<Double> data = new ArrayList<Double>();
      for (int j = 0; j < l; j++) {
        data.add(new Double(0));
      }
        
      stackedSeries.add(data);
    }
    
    for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
      String fieldName = "orderItem." + itemIndex;
      QueryNumericField numberField = (QueryNumericField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
      for (QueryReply queryReply : includeReplies) {
        for (int position = 0; position < items.size(); position++) {
          Double key = new Double(position);
          Long count = queryQuestionNumericAnswerDAO.countByQueryFieldQueryReplyAndData(numberField, queryReply, key);
          List<Double> list = stackedSeries.get(position);
          list.set(itemIndex, list.get(itemIndex) + new Double(count));
        }
      }      
    }

    requiredFragment.addAttribute("title", queryPage.getTitle());
    requiredFragment.addAttribute("itemCount", String.valueOf(items.size()));
    for (int i = 0, l = items.size(); i < l; i++) {
      String item = items.get(i);
      List<Double> stackedSerie = stackedSeries.get(i);
      requiredFragment.addAttribute("item." + i + ".label", item);
      requiredFragment.addAttribute("item." + i + ".values", StringUtils.join(stackedSerie, ','));
    }
    
    addRequiredFragment(requestContext, requiredFragment);
  }
  
  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }

  private List<QueryOption> options = new ArrayList<QueryOption>();
}