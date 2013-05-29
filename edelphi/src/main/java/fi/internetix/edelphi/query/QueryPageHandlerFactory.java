package fi.internetix.edelphi.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.query.collage.Collage2DQueryPageHandler;
import fi.internetix.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.internetix.edelphi.query.form.FormQueryPageHandler;
import fi.internetix.edelphi.query.text.TextQueryPageHandler;
import fi.internetix.edelphi.query.thesis.GroupingThesisQueryPageHandler;
import fi.internetix.edelphi.query.thesis.MultiSelectThesisQueryPageHandler;
import fi.internetix.edelphi.query.thesis.OrderingThesisQueryPageHandler;
import fi.internetix.edelphi.query.thesis.Scale1DThesisQueryPageHandler;
import fi.internetix.edelphi.query.thesis.Scale2DThesisQueryPageHandler;
import fi.internetix.edelphi.query.thesis.TimeSerieThesisQueryPageHandler;
import fi.internetix.edelphi.query.thesis.TimelineThesisQueryPageHandler;
import fi.internetix.smvc.SmvcRuntimeException;

public class QueryPageHandlerFactory {
  
  private static final QueryPageHandlerFactory INSTANCE = new QueryPageHandlerFactory();
  
  public static QueryPageHandlerFactory getInstance() {
    return INSTANCE;
  }
  
  public QueryPageHandlerFactory() {
    registerPageHandler(QueryPageType.TEXT, TextQueryPageHandler.class);
    registerPageHandler(QueryPageType.FORM, FormQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_TIME_SERIE, TimeSerieThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_SCALE_1D, Scale1DThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_SCALE_2D, Scale2DThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.EXPERTISE, ExpertiseQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_MULTI_SELECT, MultiSelectThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_ORDER, OrderingThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_GROUPING, GroupingThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.THESIS_TIMELINE, TimelineThesisQueryPageHandler.class);
    registerPageHandler(QueryPageType.COLLAGE_2D, Collage2DQueryPageHandler.class);
  }

  public QueryPageHandler buildPageHandler(QueryPageType queryPageType) {
    try {
      return pageHandlers.get(queryPageType).newInstance();
    } catch (Exception e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }
  
  public Set<QueryPageType> getRegisteredTypes() {
    return pageHandlers.keySet();
  }
  
  private void registerPageHandler(QueryPageType queryPageType, Class<? extends QueryPageHandler> pageHandler) {
    pageHandlers.put(queryPageType, pageHandler);
  }
  
  private Map<QueryPageType, Class<? extends QueryPageHandler>> pageHandlers = new HashMap<QueryPageType, Class<? extends QueryPageHandler>>();
}
