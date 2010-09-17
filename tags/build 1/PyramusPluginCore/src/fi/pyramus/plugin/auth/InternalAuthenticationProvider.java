package fi.pyramus.plugin.auth;

import fi.pyramus.domainmodel.users.User;

/**
 * Defines requirements for a class capable of authorizing users with username & password combination.
 */
public interface InternalAuthenticationProvider extends AuthenticationProvider {

  /**
   * Returns the user corresponding to the given credentials. If no user cannot be found, returns
   * <code>null</code>.
   * 
   * @param username The username
   * @param password The password
   * 
   * @return The user corresponding to the given credentials, or <code>null</code> if not found
   */
  public User getUser(String username, String password) throws AuthenticationException;

  /**
   * Returns whether this authorization provider is capable of updating the credentials of a user.
   * 
   * @return <code>true</code> if this authorization provider can update credentials, otherwise <code>false</code>
   */
  public boolean canUpdateCredentials();

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
      throws AuthenticationException;

}
