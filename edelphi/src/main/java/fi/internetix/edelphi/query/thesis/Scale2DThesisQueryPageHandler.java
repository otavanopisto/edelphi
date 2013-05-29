package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
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

public class Scale2DThesisQueryPageHandler extends AbstractScaleThesisQueryPageHandler {

  public Scale2DThesisQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale2d.label.x", "panelAdmin.block.query.scale2DXLabelOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale2d.label.y", "panelAdmin.block.query.scale2DYLabelOptionLabel", QueryOptionEditor.TEXT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale2d.type", "panelAdmin.block.query.scale2DTypeOptionLabel", QueryOptionEditor.SCALE2D_TYPE, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale2d.options.x", "panelAdmin.block.query.scale2DOptionsXOptionLabel", QueryOptionEditor.OPTION_SET, false));
    options.add(new QueryOption(QueryOptionType.QUESTION, "scale2d.options.y", "panelAdmin.block.query.scale2DOptionsYOptionLabel", QueryOptionEditor.OPTION_SET, false));
  }
  @Override
  protected void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    String fieldNameX = getFieldName("x");
    String fieldNameY = getFieldName("y");
    String valueX = requestContext.getString("valueX");
    String valueY = requestContext.getString("valueY");
    
    saveAnswer(requestContext, queryPage, queryReply, fieldNameX, valueX);
    saveAnswer(requestContext, queryPage, queryReply, fieldNameY, valueY);
  }

  @Override
  protected void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    int type = getIntegerOptionValue(queryPage, getDefinedOption("scale2d.type"));

    String fieldNameX = getFieldName("x");
    String fieldNameY = getFieldName("y");

    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
    QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

    String labelX = getStringOptionValue(queryPage, getDefinedOption("scale2d.label.x"));
    String labelY = getStringOptionValue(queryPage, getDefinedOption("scale2d.label.y"));
    
    if (type == SCALE_TYPE_RADIO) {
      renderRadioList(requestContext, "valueX", labelX, queryFieldX, answerX);
      renderRadioList(requestContext, "valueY", labelY, queryFieldY, answerY);
    } else if (type == SCALE_TYPE_SLIDER) {
      renderSlider(requestContext, "valueX", labelX, queryFieldX, answerX);
      renderSlider(requestContext, "valueY", labelY, queryFieldY, answerY);
    } else if (type == SCALE_TYPE_GRAPH) {
      renderGraph(requestContext, "valueX", "valueY", labelX, labelY, queryFieldX, queryFieldY, answerX, answerY);
    }
  }

  private void renderGraph(PageRequestContext requestContext, String nameX, String nameY, String labelX, String labelY, QueryOptionField queryFieldX, QueryOptionField queryFieldY, QueryQuestionOptionAnswer answerX, QueryQuestionOptionAnswer answerY) {
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("scale_graph");
    
    List<QueryOptionFieldOption> optionsX = queryOptionFieldOptionDAO.listByQueryField(queryFieldX);
    List<QueryOptionFieldOption> optionsY = queryOptionFieldOptionDAO.listByQueryField(queryFieldY);

    if (answerX != null) {
      requiredFragment.addAttribute("valueX", answerX.getOption().getValue());
    } else {
      if (optionsX.size() > 0)
        requiredFragment.addAttribute("valueX", optionsX.get(0).getValue());
    }

    if (answerY != null) {
      requiredFragment.addAttribute("valueY", answerY.getOption().getValue());
    } else {
      if (optionsY.size() > 0)
        requiredFragment.addAttribute("valueY", optionsY.get(0).getValue());
    }
    
    int i = 0;
    for (QueryOptionFieldOption option : optionsX) {
      addJsDataVariable(requestContext, "scale_graph.options.x." + i + ".value", option.getValue());
      addJsDataVariable(requestContext, "scale_graph.options.x." + i + ".text", option.getText());
      i++;
    }
    
    addJsDataVariable(requestContext, "scale_graph.options.x.count", String.valueOf(optionsX.size()));
    addJsDataVariable(requestContext, "scale_graph.options.x.label", labelX);
    
    i = 0;
    for (QueryOptionFieldOption option : optionsY) {
      addJsDataVariable(requestContext, "scale_graph.options.y." + i + ".value", option.getValue());
      addJsDataVariable(requestContext, "scale_graph.options.y." + i + ".text", option.getText());
      i++;
    }
    
    addJsDataVariable(requestContext, "scale_graph.options.y.count", String.valueOf(optionsY.size()));
    addJsDataVariable(requestContext, "scale_graph.options.y.label", labelY);
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);

    String fieldNameX = getFieldName("x");
    String fieldNameY = getFieldName("y");
    String labelX = settings.get(getDefinedOption("scale2d.label.x").getName());
    String labelY = settings.get(getDefinedOption("scale2d.label.y").getName());

    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.QUESTION) {
        if ((hasAnswers == false) || (queryOption.isEditableWithAnswers()))
          QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
      }
    }

    if (!hasAnswers) {
      QueryOption optionsOptionX = getDefinedOption("scale2d.options.x");
      QueryOption optionsOptionY = getDefinedOption("scale2d.options.y");
  
      // TODO: Mandarory ???
  
      Boolean mandatory = false;
  
      synchronizeField(settings, queryPage, optionsOptionX, fieldNameX, labelX, mandatory);
      synchronizeField(settings, queryPage, optionsOptionY, fieldNameY, labelY, mandatory);
    } else {
      synchronizeFieldCaption(queryPage, fieldNameX, labelX);
      synchronizeFieldCaption(queryPage, fieldNameY, labelY);
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
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();

    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(exportContext.getQueryPage().getQuerySection().getQuery(), exportContext.getStamp());
    
    QueryPage queryPage = exportContext.getQueryPage();
    
    String fieldNameX = getFieldName("x");
    String fieldNameY = getFieldName("y");
    
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);

    Messages messages = Messages.getInstance();
    Locale locale = exportContext.getLocale();

    int columnIndexX = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldX.getCaption());
    int columnIndexY = exportContext.addColumn(queryPage.getTitle() + "/" + queryFieldY.getCaption());
    int commentColumnIndex = exportContext.addColumn(queryPage.getTitle() + "/" + messages.getText(locale, "panelAdmin.query.export.comment")); 
    
    for (QueryReply queryReply : queryReplies) {
      QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
      QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);

      exportContext.addCellValue(queryReply, columnIndexX, answerX != null ? answerX.getOption().getText() : null);
      exportContext.addCellValue(queryReply, columnIndexY, answerY != null ? answerY.getOption().getText() : null);

      QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
      exportContext.addCellValue(queryReply, commentColumnIndex, comment != null ? comment.getComment() : null);
    }
  }
  
  @Override
  protected void renderReport(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("report_bubblechart");
    
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    UserDAO userDAO = new UserDAO();

    Query query = queryPage.getQuerySection().getQuery();
    String fieldNameX = getFieldName("x");
    String fieldNameY = getFieldName("y");
    QueryOptionField queryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameX);
    QueryOptionField queryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(queryPage, fieldNameY);
    
    List<QueryOptionFieldOption> optionsX = queryOptionFieldOptionDAO.listByQueryField(queryFieldX);
    List<QueryOptionFieldOption> optionsY = queryOptionFieldOptionDAO.listByQueryField(queryFieldY);
    
    int maxX = 0;
    int maxY = 0;
    
    List<String> xTickLabels = new ArrayList<String>();
    
    for (QueryOptionFieldOption optionX : optionsX) {
      int x = NumberUtils.createInteger(optionX.getValue());
      maxX = Math.max(maxX, x);
      xTickLabels.add(optionX.getText());
    }

    List<String> yTickLabels = new ArrayList<String>();
    for (QueryOptionFieldOption optionY : optionsY) {
      int y = NumberUtils.createInteger(optionY.getValue());
      maxY = Math.max(maxY, y);
      yTickLabels.add(optionY.getText());
    }
    
    maxX++;
    maxY++;
    
    Double[][] values = new Double[maxX][];
    for (int x = 0; x < maxX; x++) {
      values[x] = new Double[maxY];
    }
    
    User loggedUser = requestContext.isLoggedIn() ? userDAO.findById(requestContext.getLoggedUserId()) : null;
    QueryReply excludeReply = QueryDataUtils.findQueryReply(requestContext, loggedUser, query);

    List<QueryReply> queryReplies = queryReplyDAO.listByQueryAndStamp(query, RequestUtils.getActiveStamp(requestContext));
    for (QueryReply queryReply : queryReplies) {
      if ((excludeReply == null)||(!queryReply.getId().equals(excludeReply.getId()))) {
        QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldX);
        QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryFieldY);
        
        if (answerX != null && answerY != null) {
          int x = NumberUtils.createInteger(answerX.getOption().getValue());
          int y = NumberUtils.createInteger(answerY.getOption().getValue());
          
          values[x][y] = new Double(values[x][y] != null ? values[x][y] + 1 : 1); 
        }
      }
    }

    requiredFragment.addAttribute("xAxisLabel", queryFieldX.getCaption());
    requiredFragment.addAttribute("yAxisLabel", queryFieldY.getCaption());
    requiredFragment.addAttribute("xValueCount", String.valueOf(maxX));
    requiredFragment.addAttribute("yValueCount", String.valueOf(maxY));
    
    for (int i = 0, l = xTickLabels.size(); i < l; i++) {
      requiredFragment.addAttribute("xTickLabel." + i, xTickLabels.get(i)); 
    }
    
    for (int i = 0, l = yTickLabels.size(); i < l; i++) {
      requiredFragment.addAttribute("yTickLabel." + i, yTickLabels.get(i)); 
    }
    
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (values[x][y] != null) {
          requiredFragment.addAttribute("bubble." + x + "." + y + ".x", String.valueOf((x)));
          requiredFragment.addAttribute("bubble." + x + "." + y + ".y", String.valueOf((y)));
          requiredFragment.addAttribute("bubble." + x + "." + y + ".value", String.valueOf(values[x][y]));
        }
      }
    }
    
    addRequiredFragment(requestContext, requiredFragment);
  }

  private String getFieldName(String axis) {
    return "scale2d." + axis;
  }

  private List<QueryOption> options = new ArrayList<QueryOption>();
}