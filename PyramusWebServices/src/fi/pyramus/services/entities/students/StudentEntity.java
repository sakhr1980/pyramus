package fi.pyramus.services.entities.students;

import java.util.Date;

import fi.pyramus.services.entities.base.AddressEntity;
import fi.pyramus.services.entities.base.LanguageEntity;
import fi.pyramus.services.entities.base.MunicipalityEntity;
import fi.pyramus.services.entities.base.NationalityEntity;
import fi.pyramus.services.entities.base.SchoolEntity;
import fi.pyramus.services.entities.base.StudyProgrammeEntity;

public class StudentEntity {
  
  public StudentEntity(Long id, AbstractStudentEntity abstractStudent, String[] emails, String firstName, String lastName, AddressEntity[] addresses,
      String phone, String additionalInfo, String parentalInfo, Date studyTimeEnd, NationalityEntity nationality, LanguageEntity language,
      MunicipalityEntity municipality, SchoolEntity school, StudyProgrammeEntity studyProgramme, Boolean archived) {
    super();
    this.id = id;
    this.abstractStudent = abstractStudent;
    this.emails = emails;
    this.firstName = firstName;
    this.lastName = lastName;
    this.addresses = addresses;
    this.phone = phone;
    this.additionalInfo = additionalInfo;
    this.studyTimeEnd = studyTimeEnd;
    this.parentalInfo = parentalInfo;
    this.nationality = nationality;
    this.language = language;
    this.municipality = municipality;
    this.school = school;
    this.archived = archived;
    this.studyProgramme = studyProgramme;
  }

  public Long getId() {
    return id;
  }

  public AbstractStudentEntity getAbstractStudent() {
    return abstractStudent;
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

  public AddressEntity[] getAddresses() {
    return addresses;
  }

  public String getPhone() {
    return phone;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public String getParentalInfo() {
    return parentalInfo;
  }

  public Date getStudyTimeEnd() {
    return studyTimeEnd;
  }

  public NationalityEntity getNationality() {
    return nationality;
  }

  public LanguageEntity getLanguage() {
    return language;
  }

  public MunicipalityEntity getMunicipality() {
    return municipality;
  }

  public SchoolEntity getSchool() {
    return school;
  }

  public StudyProgrammeEntity getStudyProgramme() {
    return studyProgramme;
  }
  
  public Boolean getArchived() {
    return archived;
  }
  
  private Long id;
  private AbstractStudentEntity abstractStudent;
  private String[] emails;
  private String firstName;
  private String lastName;
  private String phone;
  private String additionalInfo;
  private String parentalInfo;
  private Date studyTimeEnd;
  private NationalityEntity nationality;
  private LanguageEntity language;
  private MunicipalityEntity municipality;
  private SchoolEntity school;
  private StudyProgrammeEntity studyProgramme;
  private Boolean archived;
  private AddressEntity[] addresses;
}
