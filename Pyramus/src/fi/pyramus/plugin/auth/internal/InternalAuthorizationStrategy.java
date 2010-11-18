package fi.pyramus.plugin.auth.internal;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.math.NumberUtils;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.users.InternalAuth;
import fi.pyramus.domainmodel.users.Role;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.plugin.auth.AuthenticationException;
import fi.pyramus.plugin.auth.InternalAuthenticationProvider;
import fi.pyramus.plugin.auth.utils.EncodingUtils;

/**
 * An authorization provider using the credential storage of the application itself.
 */
public class InternalAuthorizationStrategy implements InternalAuthenticationProvider {

  /**
   * Creates a new user.
   * 
   * @param firstName The first name of the user
   * @param lastName The last name of the user
   * @param email The email address of the user
   * @param username The username of the user
   * @param password The password of the user
   * @param role The role of the user
   * 
   * @return The created user
   */
  public User createUser(String firstName, String lastName, String email, String username, String password, Role role) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    try {
      String passwordEncoded = EncodingUtils.md5EncodeString(password);
      InternalAuth internalAuth = userDAO.createInternalAuth(username, passwordEncoded);
      User user = userDAO.createUser(firstName, lastName, String.valueOf(internalAuth.getId()), getName(), role);
      // TODO Default contact type?
      baseDAO.createEmail(user.getContactInfo(), null, Boolean.TRUE, email);
      return user;
    }
    catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
    catch (NoSuchAlgorithmException e) {
      throw new PyramusRuntimeException(e);
    }
  }

  /**
   * Returns the username of a user corresponding to the given identifier, or <code>null</code> if
   * not found.
   * 
   * @param externalId The user identifier
   * 
   * @return The username of the user corresponding to the given identifier, or <code>null</code> if
   * not found
   */
  public String getUsername(String externalId) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    Long internalAuthId = NumberUtils.createLong(externalId);
    if (internalAuthId != null && internalAuthId > 0) {
      InternalAuth internalAuth = userDAO.getInternalAuth(internalAuthId);
      return internalAuth == null ? null : internalAuth.getUsername();
    }
    
    return null;
  }

  /**
   * Returns the user corresponding to the given credentials. If no user cannot be found, returns
   * <code>null</code>.
   * 
   * @param username The username
   * @param password The password
   * 
   * @return The user corresponding to the given credentials, or <code>null</code> if not found
   */
  public User getUser(String username, String password) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    String passwordEncoded;
    try {
      passwordEncoded = EncodingUtils.md5EncodeString(password);
    }
    catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
    
    catch (NoSuchAlgorithmException e) {
      throw new PyramusRuntimeException(e);
    }

    InternalAuth internalAuth = userDAO.getInternalAuthByUsernameAndPassword(username, passwordEncoded);
    if (internalAuth != null) {
      User user = userDAO.getUser(String.valueOf(internalAuth.getId()), getName());
      return user;
    }
    else {
      return null;
    }
  }
  
  @Override
  public String createCredentials(String username, String password) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    try {
      String newPasswordEncoded = EncodingUtils.md5EncodeString(password);
      InternalAuth internalAuth = userDAO.createInternalAuth(username, newPasswordEncoded);
      String externalId = internalAuth.getId().toString();
      return externalId;
    }
    catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
    catch (NoSuchAlgorithmException e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
  @Override
  public void updateUsername(String externalId, String username) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    InternalAuth internalAuth = userDAO.getInternalAuth(NumberUtils.createLong(externalId));
    userDAO.updateInternalAuthUsername(internalAuth, username);
  }
  
  @Override
  public void updatePassword(String externalId, String password) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    try {
      InternalAuth internalAuth = userDAO.getInternalAuth(NumberUtils.createLong(externalId));

      String newPasswordEncoded = EncodingUtils.md5EncodeString(password);
      userDAO.updateInternalAuthPassword(internalAuth, newPasswordEncoded);
    }
    catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
    catch (NoSuchAlgorithmException e) {
      throw new PyramusRuntimeException(e);
    }
  }

  /**
   * Updates the credentials of the user corresponding to the given identifer.
   * 
   * @param externalId The user identifier
   * @param currentPassword The current password of the user
   * @param newUsername The new username of the user
   * @param newPassword The new password of the user
   * 
   * @throws AuthenticationException If the current password is invalid
   */
  public void updateCredentials(String externalId, String currentPassword, String newUsername, String newPassword)
      throws AuthenticationException {
    
  }

  /**
   * Returns whether this authorization provider is capable of updating the credentials of a user.
   * This provider is capable of that, so <code>true</code> is always returned.
   * 
   * @return Always <code>true</code>
   */
  public boolean canUpdateCredentials() {
    return true;
  }

  /**
   * Returns the name of this authorization provider.
   * 
   * @return The name of this authorization provider
   */
  public String getName() {
    return "internal";
  }

}
