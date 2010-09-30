package fi.pyramus.domainmodel.grading;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import fi.pyramus.domainmodel.base.ArchivableEntity;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.usertypes.CreditType;
import fi.pyramus.persistence.usertypes.CreditTypeUserType;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@TypeDefs ({
  @TypeDef (name="CreditType", typeClass=CreditTypeUserType.class)
})
public class Credit implements ArchivableEntity {

  public Long getId() {
    return id;
  }
  
  public Student getStudent() {
    return student;
  }
  
  public void setStudent(Student student) {
    this.student = student;
  }
  
  public Date getDate() {
    return date;
  }
  
  public void setDate(Date date) {
    this.date = date;
  }
  
  public Grade getGrade() {
    return grade;
  }
  
  public void setGrade(Grade grade) {
    this.grade = grade;
  }
  
  public String getVerbalAssessment() {
    return verbalAssessment;
  }
  
  public void setVerbalAssessment(String verbalAssessment) {
    this.verbalAssessment = verbalAssessment;
  }
  
  public void setAssessingUser(User assessingUser) {
    this.assessingUser = assessingUser;
  }
  
  public User getAssessingUser() {
    return assessingUser;
  }
  
  public Boolean getArchived() {
    return archived;
  }
  
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }
  
  public CreditType getCreditType() {
    return creditType;
  }
  
  protected void setCreditType(CreditType creditType) {
    this.creditType = creditType;
  }
  
  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Credit")  
  @TableGenerator(name="Credit", allocationSize=1)
  private Long id;
  
  @ManyToOne  
  @JoinColumn(name="student")
  private Student student;
  
  @Column (nullable=false)
  @Temporal (value=TemporalType.TIMESTAMP)
  private Date date;
  
  @OneToOne  
  @JoinColumn(name="grade")
  private Grade grade;
  
  @Basic (fetch = FetchType.LAZY)
  @Column (length=1073741824)
  private String verbalAssessment;
  
  @ManyToOne  
  @JoinColumn(name="assessingUser")
  private User assessingUser;
  
  @Basic (optional = false)
  private Boolean archived = Boolean.FALSE;

  @Type (type="CreditType")  
  @Column (insertable = true, updatable = false, nullable = false)
  private CreditType creditType;
}
