package fi.pyramus.services.entities.courses;

import java.util.Date;

import fi.pyramus.services.entities.base.SubjectEntity;
import fi.pyramus.services.entities.users.UserEntity;

public class CourseEntity {

  public CourseEntity(Long id, String name, String nameExtension, UserEntity creator, Date created, UserEntity lastModifier, Date lastModified, String description,
      SubjectEntity subject, Integer courseNumber, Double courseLength, Long courseLengthUnitId, CourseEducationTypeEntity[] educationTypes, Boolean archived,
      CourseComponentEntity[] courseComponents, Long moduleId, Date beginDate, Date endDate) {
    super();
    this.id = id;
    this.name = name;
    this.nameExtension = nameExtension;
    this.creator = creator;
    this.created = created;
    this.lastModifier = lastModifier;
    this.lastModified = lastModified;
    this.description = description;
    this.subject = subject;
    this.courseNumber = courseNumber;
    this.courseLength = courseLength;
    this.courseLengthUnitId = courseLengthUnitId;
    this.educationTypes = educationTypes;
    this.archived = archived;
    this.courseComponents = courseComponents;
    this.moduleId = moduleId;
    this.beginDate = beginDate;
    this.endDate = endDate;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public String getNameExtension() {
	return nameExtension;
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

  public CourseEducationTypeEntity[] getEducationTypes() {
    return educationTypes;
  }

  public Boolean getArchived() {
    return archived;
  }

  public CourseComponentEntity[] getCourseComponents() {
    return courseComponents;
  }

  public Long getModuleId() {
    return moduleId;
  }

  public Date getBeginDate() {
    return beginDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public Integer getCourseNumber() {
    return courseNumber;
  }

  private Long id;
  private String name;
  private String nameExtension;
  private UserEntity creator;
  private Date created;
  private UserEntity lastModifier;
  private Date lastModified;
  private String description;
  private SubjectEntity subject;
  private Integer courseNumber;
  private Double courseLength;
  private Long courseLengthUnitId;
  private CourseEducationTypeEntity[] educationTypes;
  private Boolean archived;
  private CourseComponentEntity[] courseComponents;
  private Long moduleId;
  private Date beginDate;
  private Date endDate;
}
