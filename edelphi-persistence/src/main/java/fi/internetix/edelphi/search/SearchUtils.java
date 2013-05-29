package fi.internetix.edelphi.search;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.jpa.Search;

public class SearchUtils {

  public static void reindexHibernateSearchObjects(EntityManager entityManager) throws InterruptedException {
    FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
    MassIndexer massIndexer = fullTextSession.createIndexer();
    
    massIndexer.batchSizeToLoadObjects(10);
    massIndexer.threadsForSubsequentFetching(1);
    massIndexer.threadsToLoadObjects(1);
    massIndexer.cacheMode(CacheMode.IGNORE);
    
    massIndexer.startAndWait();
  } 
  
  public static void reindexHibernateSearchObjects(EntityManager entityManager, Class<?> objectsClass) throws InterruptedException {
    FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
    MassIndexer massIndexer = fullTextSession.createIndexer(objectsClass);
    
    massIndexer.batchSizeToLoadObjects(10);
    massIndexer.threadsForSubsequentFetching(1);
    massIndexer.threadsToLoadObjects(1);
    massIndexer.cacheMode(CacheMode.IGNORE);

    massIndexer.startAndWait();
  }

  public static void addTokenizedSearchCriteria(StringBuilder queryBuilder, String fieldName, String value, boolean required, Float boost) {
    String inputText = value.replaceAll(" +", " ");
    String[] tokens = escapeSearchCriteria(inputText).split("[ ,]");
    
    for (String token : tokens) {
      if (!StringUtils.isBlank(token)) {
        queryBuilder.append(' ');
        if (required) {
          queryBuilder.append("+");
        }
        queryBuilder.append(fieldName).append(':').append(token);
        
        if (boost != null)
          queryBuilder.append("^").append(boost);
      }
    }
  }
  
  public static void addTokenizedSearchCriteria(StringBuilder queryBuilder, String fieldName, String value, boolean required) {
    addTokenizedSearchCriteria(queryBuilder, fieldName, value, required, null);
  }
  
  public static void addTokenizedSearchCriteria(StringBuilder queryBuilder, String fieldName1, String fieldName2, String value, boolean required) {
    String inputText = value.replaceAll(" +", " ");
    String[] tokens = escapeSearchCriteria(inputText).split("[ ,]");
    for (String token : tokens) {
      if (!StringUtils.isBlank(token)) {
        if (required) {
          queryBuilder.append("+");
        }
        queryBuilder.append('(').append(fieldName1).append(':').append(token).append(' ').append(fieldName2).append(':').append(token).append(')');
      }
    }
  }
  
  public static String getSearchDateInfinityHigh() {
    return DATERANGE_INFINITY_HIGH;
  }
  
  public static String getSearchDateInfinityLow() {
    return DATERANGE_INFINITY_LOW;
  }
  
  private static String escapeSearchCriteria(String value) {
    return value
      .replaceAll("[\\:\\+\\-\\~\\(\\)\\{\\}\\[\\]\\^\\&\\|\\!\\\\]", "\\\\$0")
      .replaceAll("[*]{1,}", "\\*");
  }
  
  private static final String DATERANGE_INFINITY_LOW = "00000000";
  private static final String DATERANGE_INFINITY_HIGH = "99999999";

}
