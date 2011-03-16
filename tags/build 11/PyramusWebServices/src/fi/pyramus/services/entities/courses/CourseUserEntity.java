package fi.pyramus.services.entities.courses;

import fi.pyramus.services.entities.users.UserEntity;

public class CourseUserEntity {
  
  public CourseUserEntity(Long id, CourseEntity course, UserEntity user, CourseUserRoleEntity role) {
    super();
    this.id = id;
    this.course = course;
    this.user = user;
    this.role = role;
  }
  
  public Long getId() {
    return id;
  }
  
  public CourseEntity getCourse() {
    return course;
  }
  
  public UserEntity getUser() {
    return user;
  }
  
  public CourseUserRoleEntity getRole() {
    return role;
  }

  private Long id;
  private CourseEntity course;
  private UserEntity user;
  private CourseUserRoleEntity role;
}
