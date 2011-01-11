package fi.pyramus.services.entities.modules;

import java.util.Date;

import fi.pyramus.services.entities.base.SubjectEntity;
import fi.pyramus.services.entities.courses.CourseEducationTypeEntity;
import fi.pyramus.services.entities.users.UserEntity;

public class ModuleEntity {

  public ModuleEntity(Long id, String name, UserEntity creator, Date created,
      UserEntity lastModifier, Date lastModified, String description,
      SubjectEntity subject, Integer courseNumber, Double courseLength, Long courseLengthUnitId,
      CourseEducationTypeEntity[] courseEducationTypes, Boolean archived,
      ModuleComponentEntity[] moduleComponents) {
    super();
    this.id = id;
    this.name = name;
    this.creator = creator;
    this.created = created;
    this.lastModifier = lastModifier;
    this.lastModified = lastModified;
    this.description = description;
    this.subject = subject;
    this.courseNumber = courseNumber;
    this.courseLength = courseLength;
    this.courseLengthUnitId = courseLengthUnitId;
    this.courseEducationTypes = courseEducationTypes;
    this.archived = archived;
    this.moduleComponents = moduleComponents;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public UserEntity getCreator() {
    return creator;
  }

  public Date getCreated() {
    return created;
  }

  public UserEntity getLastModifier() {
    return lastModifier;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public String getDescription() {
    return description;
  }

  public SubjectEntity getSubject() {
    return subject;
  }

  public Double getCourseLength() {
    return courseLength;
  }
  
  public Long getCourseLengthUnitId() {
    return courseLengthUnitId;
  }
  
   public CourseEducationTypeEntity[] getCourseEducationTypes() {
    return courseEducationTypes;
  }

  public Boolean getArchived() {
    return archived;
  }

  public ModuleComponentEntity[] getModuleComponents() {
    return moduleComponents;
  }

  public Integer getCourseNumber() {
    return courseNumber;
  }

  private Long id;
  private String name;
  private UserEntity creator;
  private Date created;
  private UserEntity lastModifier;
  private Date lastModified;
  private String description;
  private SubjectEntity subject;
  private Integer courseNumber;
  private Double courseLength;
  private Long courseLengthUnitId;
  private CourseEducationTypeEntity[] courseEducationTypes;
  private Boolean archived;
  private ModuleComponentEntity[] moduleComponents;
}
