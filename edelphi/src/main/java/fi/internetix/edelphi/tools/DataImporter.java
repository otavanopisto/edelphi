package fi.internetix.edelphi.tools;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import com.csvreader.CsvReader;

import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.logging.Logging;

public class DataImporter {

  public void doImport() throws Exception {
    
    EntityResolver entityResolver = new EntityResolver(queryId);
    
    if (emailMappingFile != null) {
      Logging.logInfo("Constructing e-mail address map");
      Properties emailProperties = new Properties();
      emailProperties.load(emailMappingFile.getInputStream());
      Enumeration<Object> keys = emailProperties.keys();
      while (keys.hasMoreElements()) {
        String key = (String) keys.nextElement();
        String value = StringUtils.lowerCase(StringUtils.trim(emailProperties.getProperty(key)));
        emailMap.put(key, value);
        Logging.logInfo(key + '=' + value);
      }
    }
    else {
      throw new IllegalArgumentException("No e-mail mapping file");
    }

    if (commentMappingFile != null) {
      Logging.logInfo("Constructing comment field map");
      Properties commentProperties = new Properties();
      commentProperties.load(commentMappingFile.getInputStream());
      Enumeration<Object> keys = commentProperties.keys();
      while (keys.hasMoreElements()) {
        String key = (String) keys.nextElement();
        String[] values = commentProperties.getProperty(key).split(",");
        for (int i = 0; i < values.length; i++) {
          entityResolver.mapCommentParameter(Integer.valueOf(key), StringUtils.trim(values[i]));
          Logging.logInfo(key + '=' + values[i]);
        }
      }
    }
    else {
      throw new IllegalArgumentException("No comment mapping file");
    }
    
    int minPage = -1;
    int maxPage = -1;
    
    if (fieldMappingFile != null) {
      Logging.logInfo("Constructing query field map");
      Properties fieldProperties = new Properties();
      fieldProperties.load(fieldMappingFile.getInputStream());
      Enumeration<Object> keys = fieldProperties.keys();         
      while (keys.hasMoreElements()) {
        String key = (String) keys.nextElement();
        String value = StringUtils.trim(fieldProperties.getProperty(key));
        if (key.indexOf('.') >= 0) {
          int dotPos = key.indexOf('.');
          String oldParamName = key.substring(0, dotPos);
          String oldOptionValue = key.substring(dotPos + 1);
          Logging.logInfo(oldParamName + "[" + oldOptionValue + "=" + value + "]");
          entityResolver.mapOptionFieldOption(oldParamName, oldOptionValue, value);
        }
        else {
          int dotPos = value.indexOf('.');
          Integer pageNumber = Integer.valueOf(value.substring(0, dotPos));
          minPage = minPage == -1 || pageNumber < minPage ? pageNumber : minPage;
          maxPage = maxPage == -1 || pageNumber > maxPage ? pageNumber : maxPage;
          String newFieldName = value.substring(dotPos + 1);
          Logging.logInfo(key + "=" + newFieldName + "(" + pageNumber + ")");
          entityResolver.mapField(key, pageNumber, newFieldName);
        }
      }
    }
    else {
      throw new IllegalArgumentException("No field mapping file");
    }
    
    if (matrixMappingFile != null) {
      Logging.logInfo("Constructing matrix map");
      matrixProperties.load(matrixMappingFile.getInputStream());
      String[] intresses = matrixProperties.getProperty("intressClasses").split(",");
      String[] expertises = matrixProperties.getProperty("expertiseClasses").split(",");
      Integer cellIndex = 0;
      for (int i = 0; i < intresses.length; i++) {
        for (int j = 0; j < expertises.length; j++) {
          entityResolver.mapExpertiseGroup(++cellIndex, new Long(intresses[i]), new Long(expertises[j]));
        }
      }
    }
    
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    Query query = queryDAO.findById(queryId);
    Panel panel = ResourceUtils.getResourcePanel(query);

    if (queryDataFile != null) {
      CsvReader queryDataReader = new CsvReader(queryDataFile.getInputStream(),
          Charset.defaultCharset());
      queryDataReader.setDelimiter(delimiter);
      
      // Read CSV header
      
      Map<String,Integer> csvParameterIndices = new HashMap<String,Integer>();
      queryDataReader.readRecord();
      for (int i = 0; i < queryDataReader.getColumnCount(); i++) {
        csvParameterIndices.put(queryDataReader.get(i), i);
      }
      
      // Go through the CSV data
      
      while (queryDataReader.readRecord()) {
        
        // Record creation and last modified dates
        
        Date creationDate = null;
        Date modifiedDate = null;
        String creationDateStr = queryDataReader.get(csvParameterIndices.get(createdParameter));
        try {
          creationDate = new SimpleDateFormat(datePattern).parse(creationDateStr);
          creationDate = creationDate.getTime() == 0 ? null : creationDate;
        }
        catch (ParseException pe) {
          Logging.logError("Unparseable creation date: " + creationDateStr);
        }
        String modifiedDateStr = queryDataReader.get(csvParameterIndices.get(modifiedParameter));
        try {
          modifiedDate = new SimpleDateFormat(datePattern).parse(modifiedDateStr);
          modifiedDate = modifiedDate.getTime() == 0 ? null : modifiedDate;
        }
        catch (ParseException pe) {
          Logging.logError("Unparseable modified date: " + modifiedDateStr);
        }
        creationDate = creationDate == null ? new Date() : creationDate;
        modifiedDate = modifiedDate == null ? creationDate : modifiedDate;

        // Resolve user
        
        String replicantId = queryDataReader.get(0);
        String email = emailMap.get(replicantId);
        User user = entityResolver.resolveUser(email);
        
        // Ensure user is a panelist
        
        PanelUser panelUser = entityResolver.resolvePanelUser(panel, user);
        
        // Ensure user's expertise matrix memberships
        
        String matrixCellStr = matrixProperties.getProperty(email);
        if (matrixCellStr != null) {
          String[] matrixCells = matrixCellStr.split(",");
          for (int i = 0; i < matrixCells.length; i++) {
            Integer matrixCellNumber = new Integer(matrixCells[i]);
            entityResolver.resolveExpertiseGroupUser(panelUser, matrixCellNumber);
          }
        }
        
        // Ensure user is associated with a QueryReply
        
        QueryReply queryReply = entityResolver.resolveQueryReply(user, creationDate, modifiedDate);
        
        // Go through the pages of the query
        
        for (int i = minPage; i <= maxPage; i++) {
          
          Logging.logInfo("Processing query page " + i);
          
          QueryPage queryPage = queryPageDAO.findByQueryAndPageNumber(query, i);
          
          // Field answers
          
          Set<String> fieldParameters = entityResolver.getFieldParametersOnPage(i);
          for (String fieldParameter : fieldParameters) {
            
            // Go through the fields of a page
            
            QueryField queryField = entityResolver.getQueryFieldByName(fieldParameter);
            // TODO support other field types as well
            QueryQuestionOptionAnswer answer = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(queryReply, queryField);
            String oldValue = queryDataReader.get(csvParameterIndices.get(fieldParameter));
            if (StringUtils.isEmpty(oldValue)) {
              if (answer != null) {
                Logging.logError("DELETING answer of " + user.getDefaultEmailAsString());
                queryQuestionOptionAnswerDAO.delete(answer);
              }
            }
            else {
              QueryOptionFieldOption queryOption = entityResolver.getQueryOption(fieldParameter, oldValue);
              if (answer != null) {
                Logging.logInfo("Updating answer of " + user.getDefaultEmailAsString() + " from " + answer.getOption().getValue() + " to " + queryOption.getValue());
                answer = queryQuestionOptionAnswerDAO.updateOption(answer, queryOption);
              }
              else {
                Logging.logInfo("Creating answer of " + user.getDefaultEmailAsString() + " as " + queryOption.getValue());
                answer = queryQuestionOptionAnswerDAO.create(queryReply, queryField, queryOption);
              }
            }
          }
          
          // Page comments
          
          StringBuilder sb = new StringBuilder();
          List<String> commentParameters = entityResolver.getCommentParametersOnPage(i);
          for (String commentParam : commentParameters) {
            String comment = queryDataReader.get(csvParameterIndices.get(commentParam));
            if (!StringUtils.isEmpty(comment)) {
              if (sb.length() > 0) {
                sb.append("\n\n");
              }
              sb.append(comment);
            }
          }
          QueryQuestionComment comment = queryQuestionCommentDAO.findRootCommentByQueryReplyAndQueryPage(queryReply, queryPage);
          if (comment == null) {
            if (sb.length() > 0) {
              Logging.logInfo("Creating comment of " + user.getDefaultEmailAsString());
              queryQuestionCommentDAO.create(queryReply, queryPage, null, sb.toString(), false, user, creationDate, user, modifiedDate);
            }
          }
          else {
            if (sb.length() > 0) {
              if (comment.getComment() != null && !comment.getComment().equals(sb.toString())) {
                Logging.logInfo("Updating comment of " + user.getDefaultEmailAsString());
                queryQuestionCommentDAO.updateComment(comment, sb.toString(), user, modifiedDate);
              }
            }
            else {
              Logging.logError("DELETING comment of " + user.getDefaultEmailAsString());
              queryQuestionCommentDAO.delete(comment);
            }
          }
        }
        
        // next user, thank you very much...
      }
    }
  }

  public FileItem getEmailMappingFile() {
    return emailMappingFile;
  }

  public void setEmailMappingFile(FileItem emailMappingFile) {
    this.emailMappingFile = emailMappingFile;
  }

  public FileItem getQueryDataFile() {
    return queryDataFile;
  }

  public void setQueryDataFile(FileItem queryDataFile) {
    this.queryDataFile = queryDataFile;
  }

  public char getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(char delimiter) {
    this.delimiter = delimiter;
  }

  public FileItem getFieldMappingFile() {
    return fieldMappingFile;
  }

  public void setFieldMappingFile(FileItem fieldMappingFile) {
    this.fieldMappingFile = fieldMappingFile;
  }

  public Long getQueryId() {
    return queryId;
  }

  public void setQueryId(Long queryId) {
    this.queryId = queryId;
  }

  public FileItem getCommentMappingFile() {
    return commentMappingFile;
  }

  public void setCommentMappingFile(FileItem commentMappingFile) {
    this.commentMappingFile = commentMappingFile;
  }

  public String getDatePattern() {
    return datePattern;
  }

  public void setDatePattern(String datePattern) {
    this.datePattern = datePattern;
  }

  public String getCreatedParameter() {
    return createdParameter;
  }

  public void setCreatedParameter(String createdParameter) {
    this.createdParameter = createdParameter;
  }

  public String getModifiedParameter() {
    return modifiedParameter;
  }

  public void setModifiedParameter(String modifiedParameter) {
    this.modifiedParameter = modifiedParameter;
  }

  public FileItem getMatrixMappingFile() {
    return matrixMappingFile;
  }

  public void setMatrixMappingFile(FileItem matrixMappingFile) {
    this.matrixMappingFile = matrixMappingFile;
  }

  private Map<String, String> emailMap = new HashMap<String, String>();

  private Long queryId;
  private FileItem emailMappingFile;
  private FileItem fieldMappingFile;
  private FileItem commentMappingFile;
  private FileItem queryDataFile;
  private FileItem matrixMappingFile;
  private char delimiter = ';';
  private String datePattern;
  private String createdParameter;
  private String modifiedParameter;
  private Properties matrixProperties = new Properties();

}
