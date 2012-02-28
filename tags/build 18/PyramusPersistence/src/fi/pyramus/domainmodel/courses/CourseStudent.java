package fi.pyramus.domainmodel.courses;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import fi.pyramus.domainmodel.base.ArchivableEntity;
import fi.pyramus.domainmodel.base.BillingDetails;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.persistence.usertypes.CourseOptionalityUserType;

@Entity
@TypeDefs ({
  @TypeDef (name="CourseOptionality", typeClass=CourseOptionalityUserType.class)
})
public class CourseStudent implements ArchivableEntity {
  
  public Long getId() {
    return id;
  }
  
  public Course getCourse() {
    return course;
  }
  
  public void setCourse(Course course) {
    this.course = course;
  }
  
  public Date getEnrolmentTime() {
    return enrolmentTime;
  }
  
  public void setEnrolmentTime(Date enrolmentTime) {
    this.enrolmentTime = enrolmentTime;
  }
  
  public Student getStudent() {
    return student;
  }
  
  public void setStudent(Student student) {
    this.student = student;
  }
  
  public CourseParticipationType getParticipationType() {
    return participationType;
  }
  
  public void setParticipationType(CourseParticipationType participationType) {
    this.participationType = participationType;
  }
  
  public CourseEnrolmentType getCourseEnrolmentType() {
    return courseEnrolmentType;
  }
  
  public void setCourseEnrolmentType(CourseEnrolmentType courseEnrolmentType) {
    this.courseEnrolmentType = courseEnrolmentType;
  }
  
  /**
   * Sets the archived flag of this object.
   * 
   * @param archived The archived flag of this object
   */
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  /**
   * Returns the archived flag of this object.
   * 
   * @return The archived flag of this object
   */
  public Boolean getArchived() {
    return archived;
  }
  
  public void setLodging(Boolean lodging) {
    this.lodging = lodging;
  }

  public Boolean getLodging() {
    return lodging;
  }
  
  public CourseOptionality getOptionality() {
    return optionality;
  }
  
  public void setOptionality(CourseOptionality optionality) {
    this.optionality = optionality;
  }

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  public void setBillingDetails(BillingDetails billingDetails) {
    this.billingDetails = billingDetails;
  }

  public BillingDetails getBillingDetails() {
    return billingDetails;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="CourseStudent")  
  @TableGenerator(name="CourseStudent", allocationSize=1)
  private Long id;

  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date enrolmentTime;
  
  @ManyToOne (optional = false)  
  @JoinColumn(name="student")
  private Student student;
  
  @ManyToOne
  @JoinColumn(name="course")
  private Course course;

  @NotNull
  @Column(nullable = false)
  private Boolean archived = Boolean.FALSE;
  
  @ManyToOne
  @JoinColumn(name="participationType")
  private CourseParticipationType participationType;
  
  @ManyToOne
  @JoinColumn(name="enrolmentType")
  private CourseEnrolmentType courseEnrolmentType;

  @NotNull
  @Column(nullable = false)
  private Boolean lodging = Boolean.FALSE;
  
  @Column
  @Type (type="CourseOptionality")
  private CourseOptionality optionality;

  @ManyToOne 
  @JoinColumn(name="billingDetails")
  private BillingDetails billingDetails;

  @Version
  @Column(nullable = false)
  private Long version;
}