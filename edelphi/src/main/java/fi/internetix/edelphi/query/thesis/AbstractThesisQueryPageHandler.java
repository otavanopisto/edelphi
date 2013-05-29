package fi.internetix.edelphi.query.thesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.query.AbstractQueryPageHandler;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.QueryOptionEditor;
import fi.internetix.edelphi.query.QueryOptionType;
import fi.internetix.edelphi.query.RequiredQueryFragment;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class AbstractThesisQueryPageHandler extends AbstractQueryPageHandler {
  
  public AbstractThesisQueryPageHandler() {
  	super();

    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.text", "panelAdmin.block.query.thesisTextOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.description", "panelAdmin.block.query.thesisDescriptionOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.commentable", "panelAdmin.block.query.thesisCommentableOptionLabel", QueryOptionEditor.BOOLEAN, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.viewDiscussions", "panelAdmin.block.query.thesisViewDiscussionsOptionLabel", QueryOptionEditor.BOOLEAN, true));
    options.add(new QueryOption(QueryOptionType.THESIS, "thesis.showLiveReport", "panelAdmin.block.query.thesisShowLiveReportOptionLabel", QueryOptionEditor.BOOLEAN, true));
  }
  
  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    requestContext.getRequest().setAttribute("queryPageId", queryPage.getId());
    
    renderThesis(requestContext, queryPage);
    renderDescription(requestContext, queryPage);
    renderQuestion(requestContext, queryPage, queryReply);

    QuerySection section = queryPage.getQuerySection();
    
    if (getBooleanOptionValue(queryPage, getDefinedOption("thesis.showLiveReport")))
      renderReport(requestContext, queryPage);

    if ((section.getCommentable() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("thesis.commentable")))
      renderCommentEditor(requestContext, queryPage, queryReply);
    
    if ((section.getViewDiscussions() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("thesis.viewDiscussions")))
      renderComments(requestContext, queryPage);
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    Boolean thesisCommentable = "1".equals(settings.get("thesis.commentable"));
    Boolean thesisViewDiscussions = "1".equals(settings.get("thesis.viewDiscussions"));
    Boolean thesisShowLiveReport = "1".equals(settings.get("thesis.showLiveReport"));
    
    // commentable, viewDiscussions, showLiveReport, text and description settings 
    // can all be updated regardless whether query has any answers 
    
    QueryPageUtils.setSetting(queryPage, "thesis.commentable", thesisCommentable ? "1" : "0", modifier);
    QueryPageUtils.setSetting(queryPage, "thesis.viewDiscussions", thesisViewDiscussions ? "1" : "0", modifier);
    QueryPageUtils.setSetting(queryPage, "thesis.showLiveReport", thesisShowLiveReport ? "1" : "0", modifier);
    
    String thesisText = settings.get("thesis.text");
    String thesisDescription = settings.get("thesis.description");

    QueryPageUtils.setSetting(queryPage, "thesis.text", thesisText, modifier);
    QueryPageUtils.setSetting(queryPage, "thesis.description", thesisDescription, modifier);
  }
  
  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    saveThesisAnswers(requestContext, queryPage, queryReply);

    QuerySection section = queryPage.getQuerySection();

    // Save comment
    if (section.getCommentable() == Boolean.TRUE && getBooleanOptionValue(queryPage, getDefinedOption("thesis.commentable"))) {
      QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();

      User loggedUser = RequestUtils.getUser(requestContext);
      
      // Root level comment
      String commentText = requestContext.getString("comment");

      if (!StringUtils.isEmpty(commentText)) {
        QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
        
        if (comment != null) {
          if (!commentText.equals(comment.getComment())) {
            queryQuestionCommentDAO.updateComment(comment, commentText, loggedUser);
          }
        }
        else {
          queryQuestionCommentDAO.create(queryReply, queryPage, null, commentText, false, loggedUser);
        }
      }
      
      Long replyCount = requestContext.getLong("newRepliesCount");
      
      for (int i = 0; i < replyCount; i++) {
        Long parentId = requestContext.getLong("commentReplyParent." + i);
        String replyContent = requestContext.getString("commentReply." + i);
        
        if ((parentId != null) && (!StringUtils.isEmpty(replyContent))) {
          QueryQuestionComment parentComment = queryQuestionCommentDAO.findById(parentId);
          
          queryQuestionCommentDAO.create(queryReply, queryPage, parentComment, replyContent, false, loggedUser);
        }
      }
    }
  }
  
  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }

  protected void renderThesis(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment thesisFragment = new RequiredQueryFragment("thesis");
    thesisFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("thesis.text")));
    addRequiredFragment(requestContext, thesisFragment);
  }

  protected void renderDescription(PageRequestContext requestContext, QueryPage queryPage) {
    RequiredQueryFragment descriptionFragment = new RequiredQueryFragment("description");
    descriptionFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("thesis.description")));
    addRequiredFragment(requestContext, descriptionFragment);
  }

  protected abstract void renderQuestion(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply);
  protected abstract void saveThesisAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply);

  protected void renderCommentEditor(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    RequiredQueryFragment commentEditorFragment = new RequiredQueryFragment("comment_editor");

    if (queryReply != null) {
      QueryQuestionCommentDAO commentDAO = new QueryQuestionCommentDAO();
      QueryQuestionComment comment = commentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
  
      if (comment != null) {
        commentEditorFragment.addAttribute("userCommentId", comment.getId().toString());
        commentEditorFragment.addAttribute("userCommentContent", comment.getComment());
      }
    }
    
    addRequiredFragment(requestContext, commentEditorFragment);
  }

  protected abstract void renderReport(PageRequestContext requestContext, QueryPage queryPage);

  private void renderComments(PageRequestContext requestContext, QueryPage queryPage) {
    Boolean commentable = getBooleanOptionValue(queryPage, getDefinedOption("thesis.commentable"));
    
    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    
    RequiredQueryFragment queryFragment = new RequiredQueryFragment("commentlist");
    queryFragment.addAttribute("queryPageId", queryPage.getId().toString());
    queryFragment.addAttribute("queryPageCommentable", commentable.toString());
    addRequiredFragment(requestContext, queryFragment);
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
}
