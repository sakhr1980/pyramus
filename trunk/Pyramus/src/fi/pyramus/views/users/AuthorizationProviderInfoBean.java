package fi.pyramus.views.users;

public class AuthorizationProviderInfoBean {
  public AuthorizationProviderInfoBean(String name, boolean active, boolean canUpdateCredentials) {
    this.name = name;
    this.active = active;
    this.canUpdateCredentials = canUpdateCredentials;
  }

  public String getName() {
    return name;
  }

  public boolean getActive() {
    return active;
  }

  public boolean getCanUpdateCredentials() {
    return canUpdateCredentials;
  }

  private String name;
  private boolean active;
  private boolean canUpdateCredentials;
}
