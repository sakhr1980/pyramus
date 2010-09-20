package fi.pyramus.services.entities.base;


public class SchoolEntity {
  
  public SchoolEntity(Long id, String code, String name, Boolean archived) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.archived = archived;
  }
  
  
  public Long getId() {
    return id;
  }
  
  public String getCode() {
    return code;
  }
  
  public String getName() {
    return name;
  }
  
  public Boolean getArchived() {
    return archived;
  }
  
  private Long id;
  private String code;
  private String name;
  private Boolean archived;
}
