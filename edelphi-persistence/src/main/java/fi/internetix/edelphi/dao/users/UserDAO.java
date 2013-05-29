package fi.internetix.edelphi.dao.users;

import java.util.Date;

import javax.persistence.PersistenceException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.search.SearchResult;

public class UserDAO extends GenericDAO<User> {
  
  public User create(String firstName, String lastName, String nickname, User creator) {
    Date now = new Date();
    
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setNickname(nickname);
    user.setCreated(now);
    user.setCreator(creator);
    user.setLastModified(now);
    user.setLastModifier(creator);
    user.setArchived(Boolean.FALSE);

    getEntityManager().persist(user);
    return user;
  }

  @SuppressWarnings("unchecked")
  public SearchResult<User> searchByFullName(int resultsPerPage, int page, String searchText) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());

    int firstResult = page * resultsPerPage;
    
    searchText = QueryParser.escape(searchText);
    searchText = searchText.replace(" ", "\\ ");
    searchText = searchText + "*";

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("fullNameSearch:");
    queryBuilder.append(searchText);
  
    try {
      String queryString = queryBuilder.toString();
      QueryParser parser = new QueryParser(Version.LUCENE_31, "", new StandardAnalyzer(Version.LUCENE_31));
      org.apache.lucene.search.Query luceneQuery = parser.parse(queryString);

      FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, User.class)
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;
      
      return new SearchResult<User>(page, pages, hits, firstResult, lastResult, query.getResultList());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<User> searchByNameOrEmail(int resultsPerPage, int page, String searchText) {
    FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(getEntityManager());

    int firstResult = page * resultsPerPage;
    
    searchText = QueryParser.escape(searchText);
    searchText = searchText.replace(" ", "\\ ");
    searchText = searchText + "*";

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("firstName:");
    queryBuilder.append(searchText);
    queryBuilder.append(" lastName:");
    queryBuilder.append(searchText);
    queryBuilder.append(" emails.address:");
    queryBuilder.append(searchText);
  
    try {
      String queryString = queryBuilder.toString();
      org.apache.lucene.search.Query luceneQuery;
      QueryParser parser = new QueryParser(Version.LUCENE_31, "", new StandardAnalyzer(Version.LUCENE_31));
      luceneQuery = parser.parse(queryString);

      FullTextQuery query = (FullTextQuery) fullTextEntityManager.createFullTextQuery(luceneQuery, User.class)
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;
      
      return new SearchResult<User>(page, pages, hits, firstResult, lastResult, query.getResultList());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }
  
  public User update(User user, String firstName, String lastName, String nickname, User modifier) {
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setNickname(nickname);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }

  public User updateFirstName(User user, String firstName, User modifier) {
    user.setFirstName(firstName);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }
  
  public User updateLastLogin(User user, Date lastLogin) {
    user.setLastLogin(lastLogin);
    getEntityManager().persist(user);
    return user;
  }

  public User updateLastName(User user, String lastName,  User modifier) {
    user.setLastName(lastName);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }

  public User updateNickname(User user, String nickname, User modifier) {
    user.setNickname(nickname);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }

  public User updateDefaultEmail(User user, UserEmail userEmail, User modifier) {
    user.setDefaultEmail(userEmail);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }
  
  public User addUserEmail(User user, UserEmail userEmail, boolean defaultEmail, User modifier) {
    user.addEmail(userEmail);
    if (defaultEmail) {
      user.setDefaultEmail(userEmail);
    }
    user.setLastModified(new Date());
    user.setLastModifier(modifier);

    getEntityManager().persist(user);
    return user;
  }
  
  public User removeUserEmail(User user, UserEmail userEmail, User modifier) {
    user.removeEmail(userEmail);
    user.setLastModified(new Date());
    user.setLastModifier(modifier);
    getEntityManager().persist(user);
    return user;
  }

}