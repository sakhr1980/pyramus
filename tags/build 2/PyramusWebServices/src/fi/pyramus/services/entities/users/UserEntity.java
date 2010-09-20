package fi.pyramus.services.entities.users;

public class UserEntity {
  
  public UserEntity(Long id, String[] emails, String firstName, String lastName, String externalId, String authProvider, String role) {
    this.id = id;
    this.emails = emails;
    this.firstName = firstName;
    this.lastName = lastName;
    this.externalId = externalId;
    this.authProvider = authProvider;
    this.role = role;
  }
  
  public Long getId() {
    return id;
  }
  
  public String[] getEmails() {
    return emails;
  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  public String getExternalId() {
    return externalId;
  }
  
  public String getAuthProvider() {
    return authProvider;
  }
  
  public String getRole() {
    return role;
  }
  
  private Long id;
  private String[] emails;
  private String firstName;
  private String lastName;
  private String externalId;
  private String authProvider; 
  private String role;
}
