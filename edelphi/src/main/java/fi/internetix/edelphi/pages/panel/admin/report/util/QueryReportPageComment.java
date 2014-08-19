package fi.internetix.edelphi.pages.panel.admin.report.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class QueryReportPageComment {

  public QueryReportPageComment(Long replyId, String comment, Date date) {
    this.replyId = replyId;
    this.comment = comment;
    this.date = date;
  }
  
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
  
  public void addReply(QueryReportPageComment reply) {
    replies.add(reply);
  }
  
  public List<QueryReportPageComment> getReplies() {
    return replies;
  }

  public boolean isFiltered() {
    return filtered;
  }

  public void setFiltered(boolean filtered) {
    this.filtered = filtered;
  }
  
  public String getAnswer(String key) {
    return answers.get(key);
  }
  
  public void setAnswer(String key, String value) {
    answers.put(key, value);
  }
  
  public HashMap<String, String> getAnswers() {
    return answers;
  }

  public Long getReplyId() {
    return replyId;
  }

  public void setReplyId(Long replyId) {
    this.replyId = replyId;
  }

  private Long replyId;
  private String comment;
  private Date date;
  private boolean filtered;
  private List<QueryReportPageComment> replies = new ArrayList<QueryReportPageComment>();
  private LinkedHashMap<String, String> answers = new LinkedHashMap<String, String>();

}