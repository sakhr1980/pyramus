package fi.pyramus.domainmodel.students;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.persistence.search.filters.ArchivedStudentFilterFactory;

/**
 * Student
 * 
 * @author antti.viljakainen
 */

@Entity
@Indexed
@FullTextFilterDefs (
  @FullTextFilterDef (
     name="ArchivedStudent",
     impl=ArchivedStudentFilterFactory.class
  )
)
public class Student {

  /**
   * Returns internal unique id
   * 
   * @return Internal unique id
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns first name of this student.
   * 
   * @return First name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets first name for this student.
   * 
   * @param firstName New first name
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Returns last name of this student.
   * 
   * @return Last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets last name for this student.
   * 
   * @param lastName New last name
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
    
  /**
   * Returns full name (first name + last name) of this student.
   *  
   * @return Full name
   */
  @Transient 
  @Field (index = Index.TOKENIZED, store = Store.NO)
  public String getFullName() {
    return getFirstName() + ' ' + getLastName();
  }
  
  @Transient 
  public Email getPrimaryEmail() {
    for (Email email : getContactInfo().getEmails()) {
      if (email.getDefaultAddress())
        return email;
    }
    return null;
  }

  /**
   * Sets additional info for this student.
   * 
   * @param additionalInfo Additional info
   */
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  /**
   * Returns additional info of this student.
   * 
   * @return Additional info
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  /**
   * Sets AbstractStudent for this student
   * 
   * @param abstractStudent AbstractStudent
   */
  protected void setAbstractStudent(AbstractStudent abstractStudent) {
    this.abstractStudent = abstractStudent;
  }

  /**
   * Returns AbstractStudent of this student.
   * 
   * @return AbstractStudent
   */
  public AbstractStudent getAbstractStudent() {
    return abstractStudent;
  }

  /**
   * Sets the municipality of this student.
   * 
   * @param municipality Municipality
   */
  public void setMunicipality(Municipality municipality) {
    this.municipality = municipality;
  }

  /**
   * Returns the municipality of this student.
   * 
   * @return Municipality
   */
  public Municipality getMunicipality() {
    return municipality;
  }

  /**
   * Sets nationality for this student.
   * 
   * @param nationality Nationality
   */
  public void setNationality(Nationality nationality) {
    this.nationality = nationality;
  }

  /**
   * Returns the nationality of this student.
   * 
   * @return The nationality of this student
   */
  public Nationality getNationality() {
    return nationality;
  }

  /**
   * Sets language for this student.
   * 
   * @param language Language
   */
  public void setLanguage(Language language) {
    this.language = language;
  }

  /**
   * Returns language of this student.
   * 
   * @return Language
   */
  public Language getLanguage() {
    return language;
  }
  
  /**
   * Returns an ending date of students right to study
   * 
   * @return ending date of students right to study
   */
  public Date getStudyTimeEnd() {
    return studyTimeEnd;
  }
  
  /**
   * Sets an ending date of students right to study
   * 
   * @param studyTimeEnd
   */
  public void setStudyTimeEnd(Date studyTimeEnd) {
    this.studyTimeEnd = studyTimeEnd;
  }
  
  public void setSchool(School school) {
    this.school = school;
  }

  public School getSchool() {
    return school;
  }
  
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }
  
  public Boolean getArchived() {
    return archived;
  }
  
  public StudyProgramme getStudyProgramme() {
    return studyProgramme;
  }
  
  public void setStudyProgramme(StudyProgramme studyProgramme) {
    this.studyProgramme = studyProgramme;
  }

  public void setStudyEndDate(Date studyEndDate) {
	  this.studyEndDate = studyEndDate;
  }

	public Date getStudyEndDate() {
	  return studyEndDate;
  }

	public void setStudyStartDate(Date studyStartDate) {
	  this.studyStartDate = studyStartDate;
  }

	public Date getStudyStartDate() {
	  return studyStartDate;
  }

	public void setPreviousStudies(Double previousStudies) {
	  this.previousStudies = previousStudies;
  }

	public Double getPreviousStudies() {
	  return previousStudies;
  }

	public void setStudyEndReason(StudentStudyEndReason studyEndReason) {
	  this.studyEndReason = studyEndReason;
  }

	public StudentStudyEndReason getStudyEndReason() {
	  return studyEndReason;
  }

	public void setStudyEndText(String studyEndText) {
	  this.studyEndText = studyEndText;
  }

	public String getStudyEndText() {
	  return studyEndText;
  }
	
  @Transient
  public Map<String, String> getVariablesAsStringMap() {
    Map<String, String> result = new HashMap<String, String>();
    for (StudentVariable studentVariable : variables) {
      result.put(studentVariable.getKey().getVariableKey(), studentVariable.getValue());
    }
    return result;
  } 

	public void setActivityType(StudentActivityType activityType) {
    this.activityType = activityType;
  }

  public StudentActivityType getActivityType() {
    return activityType;
  }

  public void setEducationalLevel(StudentEducationalLevel educationalLevel) {
    this.educationalLevel = educationalLevel;
  }

  public StudentEducationalLevel getEducationalLevel() {
    return educationalLevel;
  }

  public void setExaminationType(StudentExaminationType examinationType) {
    this.examinationType = examinationType;
  }

  public StudentExaminationType getExaminationType() {
    return examinationType;
  }
  
  @Transient
  public Address getDefaultAddress() {
    for (Address address : contactInfo.getAddresses()) {
      if (address.getDefaultAddress()) {
        return address;
      }
    }
    return null;
  }

  @Transient
  public Email getDefaultEmail() {
    for (Email email : contactInfo.getEmails()) {
      if (email.getDefaultAddress()) {
        return email;
      }
    }
    return null;
  }
  
  @Transient
  public PhoneNumber getDefaultPhone() {
    for (PhoneNumber phone : contactInfo.getPhoneNumbers()) {
      if (phone.getDefaultNumber()) {
        return phone;
      }
    }
    return null;
  }

  public String getEducation() {
    return education;
  }
  
  public void setEducation(String education) {
    this.education = education;
  }
  
  public String getNickname() {
    return nickname;
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Boolean getLodging() {
    return lodging;
  }
  
  public void setLodging(Boolean lodging) {
    this.lodging = lodging;
  }
  
  public void setVariables(List<StudentVariable> variables) {
    this.variables = variables;
  }

  public List<StudentVariable> getVariables() {
    return variables;
  }

  public void setContactInfo(ContactInfo contactInfo) {
    this.contactInfo = contactInfo;
  }

  public ContactInfo getContactInfo() {
    return contactInfo;
  }

  @Id 
  @GeneratedValue(strategy=GenerationType.TABLE, generator="Student")  
  @TableGenerator(name="Student", allocationSize=1)
  @DocumentId
  private Long id;

  @ManyToOne
  @JoinColumn (name = "abstractStudent")
  private AbstractStudent abstractStudent;
  
  @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="contactInfo")
  private ContactInfo contactInfo = new ContactInfo();
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String firstName;
  
  @NotNull
  @Column (nullable = false)
  @NotEmpty
  private String lastName;
  
  private String nickname;
    
  @Basic (fetch = FetchType.LAZY)
  @Column (length=1073741824)
  private String additionalInfo;
  
  @ManyToOne
  @JoinColumn (name = "nationality")
  private Nationality nationality;

  @ManyToOne
  @JoinColumn (name = "language")
  private Language language;

  @ManyToOne
  @JoinColumn (name = "municipality")
  private Municipality municipality;

  @ManyToOne
  @JoinColumn (name = "school")
  @IndexedEmbedded
  private School school;
  
  @NotNull
  @Column (nullable = false)
  @Field (index=Index.TOKENIZED)
  private Boolean archived = Boolean.FALSE;
  
  @ManyToOne
  @JoinColumn (name = "activityType")
  private StudentActivityType activityType;

  @ManyToOne
  @JoinColumn (name = "examinationType")
  private StudentExaminationType examinationType;

  @ManyToOne
  @JoinColumn (name = "educationalLevel")
  private StudentEducationalLevel educationalLevel;

  @Temporal (value=TemporalType.DATE)
  private Date studyTimeEnd;
  
  @ManyToOne  
  @JoinColumn(name="studyProgramme")
  private StudyProgramme studyProgramme;

  private Double previousStudies; 
  
  private String education;
  
  @NotNull
  @Column (nullable = false)
  private Boolean lodging;
  
  @Temporal (value=TemporalType.DATE)
  private Date studyStartDate;
  
  @Temporal (value=TemporalType.DATE)
  private Date studyEndDate;
  
  @ManyToOne
  @JoinColumn (name = "studyEndReason")
  @IndexedEmbedded
  private StudentStudyEndReason studyEndReason;
  
  @Basic (fetch = FetchType.LAZY)
  private String studyEndText;
  
  @OneToMany (cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn (name="student")
  private List<StudentVariable> variables = new ArrayList<StudentVariable>();

}