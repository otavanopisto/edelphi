package fi.internetix.edelphi.query.text;

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
import fi.internetix.edelphi.query.QueryExportContext;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.QueryOptionEditor;
import fi.internetix.edelphi.query.QueryOptionType;
import fi.internetix.edelphi.query.RequiredQueryFragment;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class TextQueryPageHandler extends AbstractQueryPageHandler {

  public TextQueryPageHandler() {
    options.add(new QueryOption(QueryOptionType.TEXT, "text.content", "panelAdmin.block.query.textContentOptionLabel", QueryOptionEditor.MEMO, true));
    options.add(new QueryOption(QueryOptionType.TEXT, "text.commentable", "panelAdmin.block.query.textCommentableOptionLabel", QueryOptionEditor.BOOLEAN, true));
    options.add(new QueryOption(QueryOptionType.TEXT, "text.viewDiscussions", "panelAdmin.block.query.textViewDiscussionsOptionLabel", QueryOptionEditor.BOOLEAN, true));
  }
  
  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    requestContext.getRequest().setAttribute("queryPageId", queryPage.getId());

    RequiredQueryFragment requiredFragment = new RequiredQueryFragment("text");
    
    requiredFragment.addAttribute("text", getStringOptionValue(queryPage, getDefinedOption("text.content")));
    addRequiredFragment(requestContext, requiredFragment);

    QuerySection section = queryPage.getQuerySection();
    
    if ((section.getCommentable() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("text.commentable")))
      renderCommentEditor(requestContext, queryPage, queryReply);
    
    if ((section.getViewDiscussions() == Boolean.TRUE) && getBooleanOptionValue(queryPage, getDefinedOption("text.viewDiscussions")))
      renderComments(requestContext, queryPage);
  }
  
  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    for (QueryOption queryOption : getDefinedOptions()) {
      if (queryOption.getType() == QueryOptionType.TEXT)
        QueryPageUtils.setSetting(queryPage, queryOption.getName(), settings.get(queryOption.getName()), modifier);
    }
  }

  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
    QuerySection section = queryPage.getQuerySection();

    // Save comment
    if (section.getCommentable() == Boolean.TRUE && getBooleanOptionValue(queryPage, getDefinedOption("text.commentable"))) {
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
  
  @Override
  public void exportData(QueryExportContext exportContext) {
  }

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

  private void renderComments(PageRequestContext requestContext, QueryPage queryPage) {
    Boolean commentable = getBooleanOptionValue(queryPage, getDefinedOption("text.commentable"));
    
    QueryUtils.appendQueryPageComments(requestContext, queryPage);
    
    RequiredQueryFragment queryFragment = new RequiredQueryFragment("commentlist");
    queryFragment.addAttribute("queryPageId", queryPage.getId().toString());
    queryFragment.addAttribute("queryPageCommentable", commentable.toString());
    addRequiredFragment(requestContext, queryFragment);
  }

  private List<QueryOption> options = new ArrayList<QueryOption>();
}
