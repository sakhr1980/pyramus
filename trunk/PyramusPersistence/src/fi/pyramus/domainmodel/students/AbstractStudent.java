package fi.pyramus.domainmodel.students;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.persistence.usertypes.Sex;
import fi.pyramus.persistence.usertypes.SexUserType;

/**
 * AbstractStudent defines a student "shell" that can be used to link different student instances to each other. i.e. same student can be "archived" from
 * previous education department and joined as new Student to a new education department thus perceiving all the information the student had at the time of the
 * earlier studies.
 * 
 * @author antti.viljakainen
 */

@Entity
@Indexed
@TypeDefs({ @TypeDef(name = "Sex", typeClass = SexUserType.class) })
public class AbstractStudent {

  /**
   * Returns unique identifier for this AbstractStudent
   * 
   * @return unique id of this AbstractStudent
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets birthday for this AbstractStudent
   * 
   * @param birthday
   *          New birthday
   */
  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  /**
   * Returns birthday given for this AbstractStudent
   * 
   * @return birthday
   */
  public Date getBirthday() {
    return birthday;
  }

  /**
   * Sets social security number for this AbstractStudent
   * 
   * @param socialSecurityNumber
   *          New social security number
   */
  public void setSocialSecurityNumber(String socialSecurityNumber) {
    this.socialSecurityNumber = socialSecurityNumber;
  }

  /**
   * Returns social security number given for this AbstractStudent
   * 
   * @return social security number
   */
  public String getSocialSecurityNumber() {
    return socialSecurityNumber;
  }

  /**
   * Returns sex given for this AbstractStudent
   * 
   * @return sex
   */
  public Sex getSex() {
    return sex;
  }

  /**
   * Sets the sex of this AbstractStudent
   * 
   * @param sex
   *          New sex
   */
  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public List<Student> getStudents() {
    return students;
  }

  @SuppressWarnings("unused")
  private void setStudents(List<Student> students) {
    this.students = students;
  }

  public void addStudent(Student student) {
    if (!this.students.contains(student)) {
      student.setAbstractStudent(this);
      students.add(student);
    } else {
      throw new PersistenceException("Student is already in this AbstractStudent");
    }
  }

  public void removeStudent(Student student) {
    if (this.students.contains(student)) {
      student.setAbstractStudent(null);
      students.remove(student);
    } else {
      throw new PersistenceException("Student is not in this AbstractStudent");
    }
  }

  @Transient
  public Student getLatestStudent() {
    // TODO: This is a last student in a list not a latest student
    return students.size() > 0 ? students.get(students.size() - 1) : null;
  }

  public void setBasicInfo(String basicInfo) {
    this.basicInfo = basicInfo;
  }

  public String getBasicInfo() {
    return basicInfo;
  }

  @Transient
  @Field(index = Index.UN_TOKENIZED, store = Store.YES)
  public String getLastNameSortable() {
    Student student = getLatestStudent();
    return student != null ? student.getLastName() : "";
  }

  @Transient
  @Field(index = Index.UN_TOKENIZED, store = Store.YES)
  public String getFirstNameSortable() {
    Student student = getLatestStudent();
    return student != null ? student.getFirstName() : "";
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveFirstNames() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getFirstName() != null) {
          results.add(student.getFirstName());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveLastNames() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getLastName() != null) {
          results.add(student.getLastName());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveNicknames() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getNickname() != null) {
          results.add(student.getNickname());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveEducations() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getEducation() != null) {
          results.add(student.getEducation());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveEmails() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (Email email : student.getContactInfo().getEmails()) {
          if (email.getAddress() != null) {
            results.add(email.getAddress());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveStreetAddresses() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getStreetAddress() != null) {
            results.add(address.getStreetAddress());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactivePostalCodes() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getPostalCode() != null) {
            results.add(address.getPostalCode());
          }
        }
      }
    }
    return setToString(results);
  }

  /**
   * Returns whether this abstract student contains at least one non-archived student who has got his
   * study end date set and that date is in the past.
   *  
   * @return <code>true</code> if this abstract student contains at least one inactive student, otherwise <code>false</code>
   */
  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactive() {
    String result = Boolean.FALSE.toString();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        result = Boolean.TRUE.toString();
        break;
      }
    }
    return result;
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveCities() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getCity() != null) {
            results.add(address.getCity());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveCountries() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getCountry() != null) {
            results.add(address.getCountry());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactivePhones() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (PhoneNumber phoneNumber : student.getContactInfo().getPhoneNumbers()) {
          results.add(phoneNumber.getNumber());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveLodgings() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getLodging() != null) {
          results.add(student.getLodging().toString());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveStudyProgrammeIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getStudyProgramme() != null)
          results.add(student.getStudyProgramme().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveLanguageIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getLanguage() != null)
          results.add(student.getLanguage().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveMunicipalityIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getMunicipality() != null)
          results.add(student.getMunicipality().getId().toString());
      }
    }
    return results.toString();
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveNationalityIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        if (student.getNationality() != null)
          results.add(student.getNationality().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveFirstNames() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getFirstName() != null) {
          results.add(student.getFirstName());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveLastNames() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getLastName() != null) {
          results.add(student.getLastName());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveNicknames() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getNickname() != null) {
          results.add(student.getNickname());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveEducations() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getEducation() != null) {
          results.add(student.getEducation());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveEmails() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (Email email : student.getContactInfo().getEmails()) {
          if (email.getAddress() != null) {
            results.add(email.getAddress());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveStreetAddresses() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getStreetAddress() != null) {
            results.add(address.getStreetAddress());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActivePostalCodes() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getPostalCode() != null) {
            results.add(address.getPostalCode());
          }
        }
      }
    }
    return setToString(results);
  }

  /**
   * Returns whether this abstract student contains at least one non-archived student who hasn't got his
   * study end date set or it has been set but it is in the future.
   *  
   * @return <code>true</code> if this abstract student contains at least one active student, otherwise <code>false</code>
   */
  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActive() {
    String result = Boolean.FALSE.toString();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        result = Boolean.TRUE.toString();
        break;
      }
    }
    return result;
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveCities() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getCity() != null) {
            results.add(address.getCity());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveCountries() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (Address address : student.getContactInfo().getAddresses()) {
          if (address.getCountry() != null) {
            results.add(address.getCountry());
          }
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActivePhones() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (PhoneNumber phoneNumber : student.getContactInfo().getPhoneNumbers()) {
          results.add(phoneNumber.getNumber());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveLodgings() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getLodging() != null) {
          results.add(student.getLodging().toString());
        }
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveStudyProgrammeIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getStudyProgramme() != null)
          results.add(student.getStudyProgramme().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveLanguageIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getLanguage() != null)
          results.add(student.getLanguage().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveMunicipalityIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getMunicipality() != null)
          results.add(student.getMunicipality().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveNationalityIds() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        if (student.getNationality() != null)
          results.add(student.getNationality().getId().toString());
      }
    }
    return setToString(results);
  }

  @Transient
  @Field(index = Index.TOKENIZED)
  public String getActiveTags() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && student.getActive()) {
        for (Tag tag : student.getTags()) {
          results.add(tag.getText());
        }
      }
    }

    return setToString(results);
  }

  /**
   * Returns whether this abstract student contains at least one student who has got his/her
   * study end date set or it has been set but it is in the future.
   *  
   * @return <code>true</code> if this abstract student contains at least one active student, otherwise <code>false</code>
   */
  @Transient
  @Field(index = Index.TOKENIZED)
  public String getInactiveTags() {
    Set<String> results = new HashSet<String>();
    for (Student student : getStudents()) {
      if (!student.getArchived() && !student.getActive()) {
        for (Tag tag : student.getTags()) {
          results.add(tag.getText());
        }
      }
    }

    return setToString(results);
  }

  private String setToString(Set<String> set) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> i = set.iterator();
    while (i.hasNext()) {
      sb.append(i.next());
      if (i.hasNext()) {
        sb.append(' ');
      }
    }
    return sb.toString();
  }

  @SuppressWarnings("unused")
  private void setVersion(Long version) {
    this.version = version;
  }

  public Long getVersion() {
    return version;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "AbstractStudent")
  @TableGenerator(name = "AbstractStudent", allocationSize = 1)
  @DocumentId
  private Long id;

  @Column
  @Temporal(value = TemporalType.DATE)
  private Date birthday;

  @Column
  @Field(index = Index.TOKENIZED, store = Store.NO)
  private String socialSecurityNumber;

  @Column
  @Type(type = "Sex")
  @Field(index = Index.TOKENIZED, store = Store.NO)
  private Sex sex;

  @Basic(fetch = FetchType.LAZY)
  @Column(length = 1073741824)
  private String basicInfo;

  @OneToMany
  @JoinColumn(name = "abstractStudent")
  @IndexedEmbedded
  private List<Student> students = new ArrayList<Student>();

  @Version
  @Column(nullable = false)
  private Long version;  
}
