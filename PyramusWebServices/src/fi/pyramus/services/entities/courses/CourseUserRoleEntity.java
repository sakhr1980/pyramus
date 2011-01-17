package fi.pyramus.services.entities.courses;

public class CourseUserRoleEntity {
  
  public CourseUserRoleEntity(Long id, String name) {
    super();
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  private Long id;
  private String name;
}
