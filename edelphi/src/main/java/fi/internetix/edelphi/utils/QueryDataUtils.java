package fi.internetix.edelphi.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.csvreader.CsvWriter;

import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.query.QueryExportContextImpl;
import fi.internetix.edelphi.query.QueryPageHandler;
import fi.internetix.edelphi.query.QueryPageHandlerFactory;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.StatusCode;
import fi.internetix.smvc.controllers.RequestContext;

public class QueryDataUtils {

  public static Object getQueryQuestionAnswer(QueryQuestionAnswer queryQuestionAnswer) {
    // TODO: Test with byte coded entities

    switch (queryQuestionAnswer.getQueryField().getType()) {
    case NUMERIC:
    case NUMERIC_SCALE:
      return ((QueryQuestionNumericAnswer) queryQuestionAnswer).getData();
    case OPTIONFIELD:
      QueryOptionFieldOption option = ((QueryQuestionOptionAnswer) queryQuestionAnswer).getOption();
      return option != null ? option.getValue() : null;
    case TEXT:
      return ((QueryQuestionTextAnswer) queryQuestionAnswer).getData();
    default:
      throw new SmvcRuntimeException(StatusCode.UNDEFINED, "Unrecognized query field type: " + queryQuestionAnswer.getQueryField().getType());
    }

  }
  
  public static Long getQueryReplyId(HttpSession session, Query query) {
    return (Long) session.getAttribute("queryReplyId_" + query.getId());
  }
  
  public static void storeQueryReplyId(HttpSession session, QueryReply queryReply) {
    session.setAttribute("queryReplyId_" + queryReply.getQuery().getId(), queryReply.getId());
  }
  
  private static void clearQueryReplyId(HttpSession session, Query query) {
    session.removeAttribute("queryReplyId_" + query.getId());
  }

  public static QueryReply findQueryReply(RequestContext requestContext, User loggedUser, Query query) {
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryReply queryReply = null;
    Long queryReplyId = requestContext.getLong("queryReplyId");
    if (queryReplyId == null) {
      queryReplyId = getQueryReplyId(requestContext.getRequest().getSession(), query);
    }
    if (queryReplyId != null) {
      queryReply = queryReplyDAO.findById(queryReplyId);
      if (queryReply != null) {
        if (queryReply.getArchived() == true) {
          queryReply = null;
          clearQueryReplyId(requestContext.getRequest().getSession(), query);
        }
        else {
          return queryReply;
        }
      }
    }
    return queryReplyDAO.findByUserAndQueryAndStamp(loggedUser, query, RequestUtils.getActiveStamp(requestContext));
  }

  public static byte[] exportQueryDataAsCSV(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, Query query, PanelStamp panelStamp) throws IOException {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
    
    Map<QueryReply, Map<Integer, Object>> rows = new HashMap<QueryReply, Map<Integer, Object>>();
    List<String> columns = new ArrayList<String>();
    
    for (QueryPage queryPage : queryPages) {
      QueryExportContextImpl exportContext = new QueryExportContextImpl(locale, queryPage, panelStamp, columns, rows);
      exportContext.setQueryReplies(replies);
      QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
      queryPageHandler.exportData(exportContext);
    }
    
    return exportDataToCsv(locale, replierExportStrategy, columns, rows);
  }

  public static byte[] exportQueryPageDataAsCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, QueryPage queryPage, PanelStamp panelStamp) throws IOException {
    Map<QueryReply, Map<Integer, Object>> rows = new HashMap<QueryReply, Map<Integer, Object>>();
    List<String> columns = new ArrayList<String>();
       
    QueryExportContextImpl exportContext = new QueryExportContextImpl(locale, queryPage, panelStamp, columns, rows);
    exportContext.setQueryReplies(replies);
    QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
    queryPageHandler.exportData(exportContext);
    
    return exportDataToCsv(locale, replierExportStrategy, exportContext.getColumns(), exportContext.getRows());
  }
  
  private static byte[] exportDataToCsv(Locale locale, ReplierExportStrategy replierExportStrategy, List<String> columns, Map<QueryReply, Map<Integer, Object>> rows) throws IOException {
    ByteArrayOutputStream csvStream = new ByteArrayOutputStream();
    CsvWriter csvWriter = new CsvWriter(csvStream, ',', Charset.forName("UTF-8"));
    
    switch (replierExportStrategy) {
    	case NONE:
    	break;
    	case HASH:
    		csvWriter.write(Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierIdColumn"));
    	break;
    	case NAME:
    		csvWriter.write(Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierNameColumn"));
    	break;
    	case EMAIL:
    		csvWriter.write(Messages.getInstance().getText(locale, "panelAdmin.query.export.csvReplierEmailColumn"));
    	break;
    }
    
    // Header
    for (String column : columns) {
      csvWriter.write(column);
    }
    csvWriter.endRecord();

    // Rows
    for (QueryReply queryReply : rows.keySet()) {
    	switch (replierExportStrategy) {
      	case NONE:
      	break;
      	case HASH:
      		csvWriter.write(queryReply.getUser() != null ? RequestUtils.md5EncodeString(String.valueOf(queryReply.getUser().getId())) : "-");
      	break;
      	case NAME:
      		csvWriter.write(queryReply.getUser() != null ? queryReply.getUser().getFullName(true, false) : "-");
      	break;
      	case EMAIL:
      		csvWriter.write(queryReply.getUser() != null ? queryReply.getUser().getDefaultEmailAsString() : "-");
      	break;
      }
    	
      Map<Integer, Object> columnValues = rows.get(queryReply);

      for (int columnIndex = 0, columnCount = columns.size(); columnIndex < columnCount; columnIndex++) {
        Object value = columnValues.get(columnIndex);
        if (value == null) {
          csvWriter.write("");
        } else {
          if (value instanceof Number) {
            csvWriter.write(String.valueOf(value));
          } else {
            csvWriter.write(String.valueOf(value));
          }
        }
      }

      csvWriter.endRecord();
    }

    csvWriter.close();

    return csvStream.toByteArray();
  }

  public enum ReplierExportStrategy {
  	NONE,
  	HASH,
  	NAME,
  	EMAIL
  }
}
