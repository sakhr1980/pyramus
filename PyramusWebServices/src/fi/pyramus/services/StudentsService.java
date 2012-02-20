package fi.pyramus.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.AddressDAO;
import fi.pyramus.dao.base.ContactInfoDAO;
import fi.pyramus.dao.base.ContactTypeDAO;
import fi.pyramus.dao.base.EmailDAO;
import fi.pyramus.dao.base.LanguageDAO;
import fi.pyramus.dao.base.MunicipalityDAO;
import fi.pyramus.dao.base.NationalityDAO;
import fi.pyramus.dao.base.PhoneNumberDAO;
import fi.pyramus.dao.base.SchoolDAO;
import fi.pyramus.dao.base.StudyProgrammeDAO;
import fi.pyramus.dao.students.AbstractStudentDAO;
import fi.pyramus.dao.students.StudentActivityTypeDAO;
import fi.pyramus.dao.students.StudentDAO;
import fi.pyramus.dao.students.StudentEducationalLevelDAO;
import fi.pyramus.dao.students.StudentExaminationTypeDAO;
import fi.pyramus.dao.students.StudentStudyEndReasonDAO;
import fi.pyramus.dao.students.StudentVariableDAO;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentActivityType;
import fi.pyramus.domainmodel.students.StudentEducationalLevel;
import fi.pyramus.domainmodel.students.StudentExaminationType;
import fi.pyramus.domainmodel.students.StudentStudyEndReason;
import fi.pyramus.persistence.usertypes.Sex;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.base.AddressEntity;
import fi.pyramus.services.entities.students.AbstractStudentEntity;
import fi.pyramus.services.entities.students.StudentEntity;

public class StudentsService extends PyramusService {

  public AbstractStudentEntity getAbstractStudentById(Long abstractStudentId) {
    AbstractStudentDAO abstractStudentDAO = DAOFactory.getInstance().getAbstractStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(abstractStudentDAO.findById(abstractStudentId));
  }

  public AbstractStudentEntity getAbstractStudentBySSN(String ssn) {
    AbstractStudentDAO abstractStudentDAO = DAOFactory.getInstance().getAbstractStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(abstractStudentDAO.findBySSN(ssn));
  }

  public StudentEntity addStudyProgramme(Long studentId, Long studyProgrammeId) {
    // TODO Generalize to StudentDAO (also used in CopyStudentStudyProgrammeJSONRequestController)
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudyProgrammeDAO studyProgrammeDAO = DAOFactory.getInstance().getStudyProgrammeDAO();
    AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
    ContactInfoDAO contactInfoDAO = DAOFactory.getInstance().getContactInfoDAO();
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    PhoneNumberDAO phoneNumberDAO = DAOFactory.getInstance().getPhoneNumberDAO();

    Student oldStudent = studentDAO.findById(studentId);

    AbstractStudent abstractStudent = oldStudent.getAbstractStudent();
    String firstName = oldStudent.getFirstName();
    String lastName = oldStudent.getLastName();
    String nickname = oldStudent.getNickname();
    String additionalInfo = oldStudent.getAdditionalInfo();
    Double previousStudies = null; // student.getPreviousStudies();
    Date studyTimeEnd = null; // student.getStudyTimeEnd();
    Date studyStartTime = null; // student.getStudyStartDate();
    Date studyEndTime = null; // student.getStudyEndDate();
    String studyEndText = null; // student.getStudyEndText();
    Language language = oldStudent.getLanguage();
    Municipality municipality = oldStudent.getMunicipality();
    StudentActivityType activityType = oldStudent.getActivityType();
    StudentExaminationType examinationType = oldStudent.getExaminationType();
    StudentEducationalLevel educationalLevel = oldStudent.getEducationalLevel();
    String education = oldStudent.getEducation();
    Nationality nationality = oldStudent.getNationality();
    School school = oldStudent.getSchool();
    StudyProgramme studyProgramme = studyProgrammeId == null ? null : studyProgrammeDAO.findById(studyProgrammeId);
    StudentStudyEndReason studyEndReason = null; // student.getStudyEndReason();
    Boolean lodging = false; // oldStudent.getLodging();

    Student newStudent = studentDAO.create(abstractStudent, firstName, lastName, nickname, additionalInfo, studyTimeEnd, activityType, examinationType,
        educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies, studyStartTime, studyEndTime,
        studyEndReason, studyEndText, lodging);

    // Contact info

    contactInfoDAO.update(newStudent.getContactInfo(), oldStudent.getContactInfo().getAdditionalInfo());

    // Addresses

    List<Address> addresses = oldStudent.getContactInfo().getAddresses();
    for (int i = 0; i < addresses.size(); i++) {
      Address add = addresses.get(i);
      addressDAO.create(newStudent.getContactInfo(), add.getContactType(), add.getName(), add.getStreetAddress(), add.getPostalCode(), add.getCity(),
          add.getCountry(), add.getDefaultAddress());
    }

    // E-mail addresses

    List<Email> emails = oldStudent.getContactInfo().getEmails();
    for (int i = 0; i < emails.size(); i++) {
      Email email = emails.get(i);
      emailDAO.create(newStudent.getContactInfo(), email.getContactType(), email.getDefaultAddress(), email.getAddress());
    }

    // Phone numbers

    List<PhoneNumber> phoneNumbers = oldStudent.getContactInfo().getPhoneNumbers();
    for (int i = 0; i < phoneNumbers.size(); i++) {
      PhoneNumber phoneNumber = phoneNumbers.get(i);
      phoneNumberDAO.create(newStudent.getContactInfo(), phoneNumber.getContactType(), phoneNumber.getDefaultNumber(), phoneNumber.getNumber());
    }

    return EntityFactoryVault.buildFromDomainObject(newStudent);
  }

  public AbstractStudentEntity createAbstractStudent(Date birthday, String socialSecurityNumber, String sex) {
    AbstractStudentDAO abstractStudentDAO = DAOFactory.getInstance().getAbstractStudentDAO();

    Sex studentSex = EnumType.valueOf(Sex.class, sex);
    AbstractStudent abstractStudent = abstractStudentDAO.create(birthday, socialSecurityNumber, studentSex, null);
    validateEntity(abstractStudent);
    return EntityFactoryVault.buildFromDomainObject(abstractStudent);
  }
  
  public void endStudentStudies(Long studentId, Date endDate, Long endReasonId, String endReasonText) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudentStudyEndReasonDAO endReasonDAO = DAOFactory.getInstance().getStudentStudyEndReasonDAO();
    
    Student student = studentDAO.findById(studentId);
    if (student != null) {
      StudentStudyEndReason endReason = endReasonId == null ? null : endReasonDAO.findById(endReasonId);
      studentDAO.endStudentStudies(student, endDate, endReason, endReasonText);
    }
  }

  public StudentEntity getStudentById(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(studentDAO.findById(studentId));
  }

  public StudentEntity[] listStudents() {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listUnarchived());
  }

  public AddressEntity[] listStudentsAddresses(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.findById(studentId);
    return (AddressEntity[]) EntityFactoryVault.buildFromDomainObjects(student.getContactInfo().getAddresses());
  }
  
  public StudentEntity[] listStudentsByStudyProgramme(Long studyProgrammeId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudyProgrammeDAO studyProgrammeDAO = DAOFactory.getInstance().getStudyProgrammeDAO();
    
    StudyProgramme studyProgramme = studyProgrammeDAO.findById(studyProgrammeId);
    
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listByStudyProgramme(studyProgramme));
  }
  
  public StudentEntity[] listStudentsByAbstractStudent(Long abstractStudentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    AbstractStudentDAO abstractStudentDAO = DAOFactory.getInstance().getAbstractStudentDAO();
    
    AbstractStudent abstractStudent = abstractStudentDAO.findById(abstractStudentId);

    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listByAbstractStudent(abstractStudent));
  }
  
  public StudentEntity[] listActiveStudentsByAbstractStudent(Long abstractStudentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    AbstractStudentDAO abstractStudentDAO = DAOFactory.getInstance().getAbstractStudentDAO();
    
    AbstractStudent abstractStudent = abstractStudentDAO.findById(abstractStudentId);

    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listActiveStudentsByAbstractStudent(abstractStudent));
  }

  public StudentEntity[] listActiveStudents() {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listActiveStudents());
  }

  public StudentEntity[] listActiveStudentsByStudyProgramme(Long studyProgrammeId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudyProgrammeDAO studyProgrammeDAO = DAOFactory.getInstance().getStudyProgrammeDAO();
    StudyProgramme studyProgramme = studyProgrammeDAO.findById(studyProgrammeId);
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listActiveStudentsByStudyProgramme(studyProgramme));
  }
  
  public StudentEntity createStudent(Long abstractStudentId, String firstName, String lastName, String nickname, String phone, String additionalInfo,
      String parentalInfo, Date studyTimeEnd, Long activityTypeId, Long examinationTypeId, Long educationalLevelId, String education, Long nationalityId,
      Long municipalityId, Long languageId, Long schoolId, Long studyProgrammeId, Double previousStudies, Date studyStartDate, Date studyEndDate,
      Long studyEndReasonId, String studyEndText, Boolean lodging) {

    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    AbstractStudentDAO abstractStudentDAO = DAOFactory.getInstance().getAbstractStudentDAO();
    StudentActivityTypeDAO activityTypeDAO = DAOFactory.getInstance().getStudentActivityTypeDAO();
    StudentExaminationTypeDAO examinationTypeDAO = DAOFactory.getInstance().getStudentExaminationTypeDAO();
    StudentEducationalLevelDAO educationalLevelDAO = DAOFactory.getInstance().getStudentEducationalLevelDAO();
    StudentStudyEndReasonDAO studyEndReasonDAO = DAOFactory.getInstance().getStudentStudyEndReasonDAO();
    LanguageDAO languageDAO = DAOFactory.getInstance().getLanguageDAO();
    MunicipalityDAO municipalityDAO = DAOFactory.getInstance().getMunicipalityDAO();
    NationalityDAO nationalityDAO = DAOFactory.getInstance().getNationalityDAO();
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();
    StudyProgrammeDAO studyProgrammeDAO = DAOFactory.getInstance().getStudyProgrammeDAO();
    ContactInfoDAO contactInfoDAO = DAOFactory.getInstance().getContactInfoDAO();
    PhoneNumberDAO phoneNumberDAO = DAOFactory.getInstance().getPhoneNumberDAO();
    ContactTypeDAO contactTypeDAO = DAOFactory.getInstance().getContactTypeDAO();

    AbstractStudent abstractStudent = abstractStudentDAO.findById(abstractStudentId);
    Nationality nationality = nationalityId == null ? null : nationalityDAO.findById(nationalityId);
    Municipality municipality = municipalityId == null ? null : municipalityDAO.findById(municipalityId);
    Language language = languageId == null ? null : languageDAO.findById(languageId);
    StudentActivityType activityType = activityTypeId == null ? null : activityTypeDAO.findById(activityTypeId);
    StudentExaminationType examinationType = examinationTypeId == null ? null : examinationTypeDAO.findById(examinationTypeId);
    StudentEducationalLevel educationalLevel = educationalLevelId == null ? null : educationalLevelDAO.findById(educationalLevelId);
    School school = schoolId == null ? null : schoolDAO.findById(schoolId);
    StudyProgramme studyProgramme = studyProgrammeId == null ? null : studyProgrammeDAO.findById(studyProgrammeId);
    StudentStudyEndReason studyEndReason = studyEndReasonId == null ? null : studyEndReasonDAO.findById(studyEndReasonId);

    Student student = studentDAO.create(abstractStudent, firstName, lastName, nickname, additionalInfo, studyTimeEnd, activityType,
        examinationType, educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies, studyStartDate,
        studyEndDate, studyEndReason, studyEndText, lodging);
    
    // TODO Proper handling for phone and parental info
    ContactType contactType = contactTypeDAO.findById(new Long(1));
    phoneNumberDAO.create(student.getContactInfo(), contactType, Boolean.TRUE, phone);
    contactInfoDAO.update(student.getContactInfo(), parentalInfo);

    validateEntity(student);
    return EntityFactoryVault.buildFromDomainObject(student);
  }

  public void updateStudent(Long studentId, String firstName, String lastName, String nickname, String phone, String additionalInfo, String parentalInfo,
      Date studyTimeEnd, Long activityTypeId, Long examinationTypeId, Long educationalLevelId, String education, Long nationalityId, Long municipalityId,
      Long languageId, Long schoolId, Long studyProgrammeId, Double previousStudies, Date studyStartDate, Date studyEndDate, Long studyEndReasonId,
      String studyEndText, Boolean lodging) {

    // TODO Get rid of phone number and parental info

    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudentActivityTypeDAO activityTypeDAO = DAOFactory.getInstance().getStudentActivityTypeDAO();
    StudentExaminationTypeDAO examinationTypeDAO = DAOFactory.getInstance().getStudentExaminationTypeDAO();
    StudentEducationalLevelDAO educationalLevelDAO = DAOFactory.getInstance().getStudentEducationalLevelDAO();
    StudentStudyEndReasonDAO studyEndReasonDAO = DAOFactory.getInstance().getStudentStudyEndReasonDAO();
    LanguageDAO languageDAO = DAOFactory.getInstance().getLanguageDAO();
    MunicipalityDAO municipalityDAO = DAOFactory.getInstance().getMunicipalityDAO();
    NationalityDAO nationalityDAO = DAOFactory.getInstance().getNationalityDAO();
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();
    StudyProgrammeDAO studyProgrammeDAO = DAOFactory.getInstance().getStudyProgrammeDAO();

    Student student = studentDAO.findById(studentId);
    Nationality nationality = nationalityId == null ? null : nationalityDAO.findById(nationalityId);
    Municipality municipality = municipalityId == null ? null : municipalityDAO.findById(municipalityId);
    Language language = languageId == null ? null : languageDAO.findById(languageId);
    StudentActivityType activityType = activityTypeId == null ? null : activityTypeDAO.findById(activityTypeId);
    StudentExaminationType examinationType = activityTypeId == null ? null : examinationTypeDAO.findById(examinationTypeId);
    StudentEducationalLevel educationalLevel = educationalLevelId == null ? null : educationalLevelDAO.findById(educationalLevelId);
    School school = schoolId == null ? null : schoolDAO.findById(schoolId);
    StudyProgramme studyProgramme = studyProgrammeId == null ? null : studyProgrammeDAO.findById(studyProgrammeId);
    StudentStudyEndReason studyEndReason = studyEndReasonId == null ? null : studyEndReasonDAO.findById(studyEndReasonId);

    studentDAO.update(student, firstName, lastName, nickname, additionalInfo, studyTimeEnd, activityType, examinationType,
        educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies, studyStartDate, studyEndDate,
        studyEndReason, studyEndText, lodging);

    validateEntity(student);
  }

  public void updateStudentMunicipality(Long studentId, Long municipalityId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    MunicipalityDAO municipalityDAO = DAOFactory.getInstance().getMunicipalityDAO();

    Student student = studentDAO.findById(studentId);
    Municipality municipality = municipalityId == null ? null : municipalityDAO.findById(municipalityId);

    studentDAO.updateStudentMunicipality(student, municipality);

    validateEntity(student);
  }
  
  public void addStudentAddress(Long studentId, String addressType, String name, String streetAddress, String postalCode, String city, String country) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
    ContactTypeDAO contactTypeDAO = DAOFactory.getInstance().getContactTypeDAO();
    Student student = studentDAO.findById(studentId);
    // TODO contactType and default address
    ContactType contactType = contactTypeDAO.findById(new Long(1));
    Address address = addressDAO.create(student.getContactInfo(), contactType, name, streetAddress, postalCode, city, country, Boolean.TRUE);
    validateEntity(address);
  }

  public void addStudentEmail(Long studentId, Boolean defaultAddress, String address) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    ContactTypeDAO contactTypeDAO = DAOFactory.getInstance().getContactTypeDAO();
    Student student = studentDAO.findById(studentId);
    // TODO contactType
    ContactType contactType = contactTypeDAO.findById(new Long(1));
    Email email = emailDAO.create(student.getContactInfo(), contactType, defaultAddress, address);
    validateEntity(email);
  }

  public void archiveStudent(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.findById(studentId);
    studentDAO.archive(student);
  }

  public void unarchiveStudent(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.findById(studentId);
    studentDAO.unarchive(student);
  }

  public void updateStudentEmail(Long studentId, String fromAddress, String toAddress) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    Student student = studentDAO.findById(studentId);
    for (Email email : student.getContactInfo().getEmails()) {
      if (email.getAddress().equals(fromAddress)) {
        email = emailDAO.update(email, email.getContactType(), email.getDefaultAddress(), toAddress);
        validateEntity(email);
        break;
      }
    }
  }

  public void removeStudentEmail(Long studentId, String address) {
    EmailDAO emailDAO = DAOFactory.getInstance().getEmailDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.findById(studentId);
    for (Email email : student.getContactInfo().getEmails()) {
      if (email.getAddress().equals(address)) {
        emailDAO.delete(email);
        break;
      }
    }
  }

  public StudentEntity[] listStudentsByStudentVariable(String key, String value) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listByStudentVariable(key, value));
  }

  public String getStudentVariable(Long studentId, String key) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudentVariableDAO studentVariableDAO = DAOFactory.getInstance().getStudentVariableDAO();

    Student student = studentDAO.findById(studentId);
    return studentVariableDAO.findByStudentAndKey(student, key);
  }

  public void setStudentSchool(Long studentId, Long schoolId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();

    Student student = studentDAO.findById(studentId);
    School school = schoolId == null ? null : schoolDAO.findById(schoolId);
    studentDAO.updateSchool(student, school);
  }

  public void setStudentVariable(Long studentId, String key, String value) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudentVariableDAO studentVariableDAO = DAOFactory.getInstance().getStudentVariableDAO();
    Student student = studentDAO.findById(studentId);
    studentVariableDAO.setStudentVariable(student, key, value);
  }

}
