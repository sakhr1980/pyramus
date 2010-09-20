package fi.pyramus.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.util.Version;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.users.InternalAuth;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.domainmodel.users.UserVariable;
import fi.pyramus.domainmodel.users.UserVariableKey;
import fi.pyramus.persistence.search.SearchResult;

public class UserDAO extends PyramusDAO {

  public User getUser(Long userId) {
    Session s = getHibernateSession();
    User user = (User) s.load(User.class, userId);
    return user;
  }
  
  @SuppressWarnings("unchecked")
  public List<User> listUsers() {
    Session s = getHibernateSession();
    return s.createCriteria(User.class).list();
  }
  
  @SuppressWarnings("unchecked")
  public List<User> listUsersByUserVariable(String key, String value) {
    Session s = getHibernateSession();
    
    UserVariableKey userVariableKey = getUserVariableKey(key);
    return (List<User>) s.createCriteria(UserVariable.class)
      .add(Restrictions.eq("key", userVariableKey))
      .add(Restrictions.eq("value", value))
      .setProjection(Projections.property("user")).list();
  }
  
  public void deleteUser(User user) {
    Session s = getHibernateSession();
    s.delete(user);
  }

  public User createUser(String firstName, String lastName, String externalId, String authProvider, Role role) {
    Session s = getHibernateSession();
    
    User newUser = new User();
    newUser.setFirstName(firstName);
    newUser.setLastName(lastName);
    newUser.setAuthProvider(authProvider);
    newUser.setExternalId(externalId);
    newUser.setRole(role);

    s.save(newUser);

    return newUser;
  }
  
  public User updateUser(User user, String firstName, String lastName, Role role) {
    Session s = getHibernateSession();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setRole(role);
    s.saveOrUpdate(user);
    return user;
  }

  public User getUser(String externalId, String authProvider) {
    Session s = getHibernateSession();
    return (User) s.createCriteria(User.class)
      .add(Restrictions.eq("externalId", externalId))
      .add(Restrictions.eq("authProvider", authProvider))
      .uniqueResult();
  }
  
  public User getUserByEmail(String email) {
    Session s = getHibernateSession();
    Query query = s.createQuery("select user from User user inner join user.contactInfo contactInfo inner join contactInfo.emails email where email.address = :address");
    query.setString("address", email);
    return (User)query.uniqueResult();
  }

  public void updateAuthProvider(User user, String authProvider) {
    EntityManager entityManager = getEntityManager();
    
    user.setAuthProvider(authProvider);
    
    entityManager.persist(user);
  }

  private UserVariable createUserVariable(User user, UserVariableKey key, String value) {
    Session s = getHibernateSession();

    UserVariable userVariable = new UserVariable();
    userVariable.setUser(user);
    userVariable.setKey(key);
    userVariable.setValue(value);
    s.saveOrUpdate(userVariable);
    
    user.getVariables().add(userVariable);
    s.saveOrUpdate(user);
    
    return userVariable;
  }
  
  private void updateUserVariable(UserVariable userVariable, String value) {
    Session s = getHibernateSession();
    userVariable.setValue(value);
    s.saveOrUpdate(userVariable);
  }
  
  public void updateExternalId(User user, String externalId) {
    Session s = getHibernateSession();
    user.setExternalId(externalId);
    s.saveOrUpdate(user);
  }

  public InternalAuth createInternalAuth(String username, String password) {
    Session s = getHibernateSession();
    
    InternalAuth internalAuth = new InternalAuth();
    internalAuth.setUsername(username);
    internalAuth.setPassword(password);
    
    s.save(internalAuth);
  
    return internalAuth;
  }
  
  private UserVariable getUserVariable(User user, UserVariableKey key) {
    Session s = getHibernateSession();
    UserVariable userVariable = (UserVariable) s.createCriteria(UserVariable.class)
        .add(Restrictions.eq("user", user))
        .add(Restrictions.eq("key", key))
        .uniqueResult();
    return userVariable;
  }

  public String getUserVariable(User user, String key) {
    UserVariableKey userVariableKey = getUserVariableKey(key);
    if (userVariableKey != null) {
      UserVariable userVariable = getUserVariable(user, userVariableKey); 
      return userVariable == null ? null : userVariable.getValue();
    }
    else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }

  private UserVariableKey getUserVariableKey(String key) {
    Session s = getHibernateSession();
    UserVariableKey userVariableKey = (UserVariableKey) s.createCriteria(UserVariableKey.class)
        .add(Restrictions.eq("variableKey", key)).uniqueResult();
    return userVariableKey;
  }
  
  public void setUserVariable(User user, String key, String value) {
    UserVariableKey userVariableKey = getUserVariableKey(key);
    if (userVariableKey != null) {
      UserVariable userVariable = getUserVariable(user, userVariableKey);
      if (StringUtils.isBlank(value)) {
        if (userVariable != null) {
          deleteUserVariable(userVariable);
        }
      }
      else {
        if (userVariable == null) {
          userVariable = createUserVariable(user, userVariableKey, value);
        }
        else {
          updateUserVariable(userVariable, value);
        }
      }
    }
    else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }
  
  /**
   * Returns a list of user variable keys from the database, sorted by their user interface name.
   * 
   * @return A list of uservariable keys
   */
  @SuppressWarnings("unchecked")
  public List<UserVariableKey> listUserVariableKeys() {
    Session s = getHibernateSession();

    List<UserVariableKey> userVariableKeys = s.createCriteria(UserVariableKey.class).list();

    Collections.sort(userVariableKeys, new Comparator<UserVariableKey>() {
      public int compare(UserVariableKey o1, UserVariableKey o2) {
        return o1.getVariableName() == null ? -1 : o2.getVariableName() == null ? 1 : o1.getVariableName().compareTo(
            o2.getVariableName());
      }
    });

    return userVariableKeys;
  }
  
  /**
   * Returns a list of user editable user variable keys from the database, sorted by their user interface name.
   * 
   * @return A list of user editable uservariable keys
   */
  @SuppressWarnings("unchecked")
  public List<UserVariableKey> listUserEditableUserVariableKeys() {
    Session s = getHibernateSession();

    List<UserVariableKey> userVariableKeys = s.createCriteria(UserVariableKey.class).add(
        Restrictions.eq("userEditable", Boolean.TRUE)).list();

    Collections.sort(userVariableKeys, new Comparator<UserVariableKey>() {
      public int compare(UserVariableKey o1, UserVariableKey o2) {
        return o1.getVariableName() == null ? -1 : o2.getVariableName() == null ? 1 : o1.getVariableName().compareTo(
            o2.getVariableName());
      }
    });

    return userVariableKeys;
  }

  public InternalAuth updateInternalAuth(InternalAuth internalAuth, String username) {
    Session s = getHibernateSession();
    internalAuth.setUsername(username);
    s.saveOrUpdate(internalAuth);
    return internalAuth;
  }

  public InternalAuth updateInternalAuth(InternalAuth internalAuth, String username, String password) {
    Session s = getHibernateSession();
    internalAuth.setUsername(username);
    internalAuth.setPassword(password);
    s.saveOrUpdate(internalAuth);
    return internalAuth;
  }
  
  private void deleteUserVariable(UserVariable userVariable) {
    Session s = getHibernateSession();
    s.delete(userVariable);
  }

  public InternalAuth getInternalAuth(Long id) {
    Session s = getHibernateSession();
    return (InternalAuth) s.load(InternalAuth.class, id);
  }
  
  public InternalAuth getInternalAuthByUsernameAndPassword(String username, String passwordEncoded) {
    Session s = getHibernateSession();
    
    return (InternalAuth) s.createCriteria(InternalAuth.class).add(
        Restrictions.eq("username", username)).add(Restrictions.eq("password", passwordEncoded)).uniqueResult();
  }

  @SuppressWarnings("unchecked")
  public SearchResult<User> searchUsers(int resultsPerPage, int page, String firstName, String lastName,
      String email, Role role, boolean escapeSpecialChars) {

    int firstResult = page * resultsPerPage;
    
    boolean hasFirstName = !StringUtils.isBlank(firstName);
    boolean hasLastName = !StringUtils.isBlank(lastName);
    boolean hasEmail = !StringUtils.isBlank(email);

    StringBuilder queryBuilder = new StringBuilder();
    if (hasFirstName || hasLastName || hasEmail) {
      queryBuilder.append("+(");
      if (hasFirstName) {
        queryBuilder.append(" firstName:").append(escapeSpecialChars ? QueryParser.escape(firstName) : firstName);
      }
      if (hasLastName) {
        queryBuilder.append(" lastName:").append(escapeSpecialChars ? QueryParser.escape(lastName) : lastName);
      }
      if (hasEmail) {
        queryBuilder.append(" contactInfo.emails.address:").append(escapeSpecialChars ? QueryParser.escape(email) : email);
      }
      queryBuilder.append(")");
    }
    if (role != null) {
      queryBuilder.append(" +role:").append(role);
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      String queryString = queryBuilder.toString();
      org.apache.lucene.search.Query luceneQuery;
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, User.class).setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<User>(page, pages, hits, firstResult, lastResult, query.list());

    } catch (ParseException e) {
      if (!escapeSpecialChars) {
        return searchUsers(resultsPerPage, page, firstName, lastName, email, role, true);
      }
      else {
        throw new PersistenceException(e);
      }
    }
  }
  
  public void setInternalAuthPassword(InternalAuth internalAuth, String password) {
    Session s = getHibernateSession();
    internalAuth.setPassword(password); 
    s.saveOrUpdate(internalAuth);
  }
 
  public void deleteInternalAuth(InternalAuth internalAuth) {
    Session s = getHibernateSession();
    s.delete(internalAuth);
  }
  
}