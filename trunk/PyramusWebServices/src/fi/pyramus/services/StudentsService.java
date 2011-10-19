package fi.pyramus.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EnumType;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
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
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(studentDAO.getAbstractStudent(abstractStudentId));
  }

  public AbstractStudentEntity getAbstractStudentBySSN(String ssn) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(studentDAO.getAbstractStudentBySSN(ssn));
  }

  public StudentEntity addStudyProgramme(Long studentId, Long studyProgrammeId) {
    // TODO Generalize to StudentDAO (also used in CopyStudentStudyProgrammeJSONRequestController)
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Student oldStudent = studentDAO.getStudent(studentId);

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
    StudyProgramme studyProgramme = studyProgrammeId == null ? null : baseDAO.getStudyProgramme(studyProgrammeId);
    StudentStudyEndReason studyEndReason = null; // student.getStudyEndReason();
    Boolean lodging = false; // oldStudent.getLodging();

    Student newStudent = studentDAO.createStudent(abstractStudent, firstName, lastName, nickname, additionalInfo, studyTimeEnd, activityType, examinationType,
        educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies, studyStartTime, studyEndTime,
        studyEndReason, studyEndText, lodging);

    // Contact info

    baseDAO.updateContactInfo(newStudent.getContactInfo(), oldStudent.getContactInfo().getAdditionalInfo());

    // Addresses

    List<Address> addresses = oldStudent.getContactInfo().getAddresses();
    for (int i = 0; i < addresses.size(); i++) {
      Address add = addresses.get(i);
      baseDAO.createAddress(newStudent.getContactInfo(), add.getContactType(), add.getName(), add.getStreetAddress(), add.getPostalCode(), add.getCity(),
          add.getCountry(), add.getDefaultAddress());
    }

    // E-mail addresses

    List<Email> emails = oldStudent.getContactInfo().getEmails();
    for (int i = 0; i < emails.size(); i++) {
      Email email = emails.get(i);
      baseDAO.createEmail(newStudent.getContactInfo(), email.getContactType(), email.getDefaultAddress(), email.getAddress());
    }

    // Phone numbers

    List<PhoneNumber> phoneNumbers = oldStudent.getContactInfo().getPhoneNumbers();
    for (int i = 0; i < phoneNumbers.size(); i++) {
      PhoneNumber phoneNumber = phoneNumbers.get(i);
      baseDAO.createPhoneNumber(newStudent.getContactInfo(), phoneNumber.getContactType(), phoneNumber.getDefaultNumber(), phoneNumber.getNumber());
    }

    return EntityFactoryVault.buildFromDomainObject(newStudent);
  }

  public AbstractStudentEntity createAbstractStudent(Date birthday, String socialSecurityNumber, String sex) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Sex studentSex = EnumType.valueOf(Sex.class, sex);
    AbstractStudent abstractStudent = studentDAO.createAbstractStudent(birthday, socialSecurityNumber, studentSex, null);
    validateEntity(abstractStudent);
    return EntityFactoryVault.buildFromDomainObject(abstractStudent);
  }
  
  public void endStudentStudies(Long studentId, Date endDate, Long endReasonId, String endReasonText) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    if (student != null) {
      StudentStudyEndReason endReason = endReasonId == null ? null : studentDAO.getStudentStudyEndReason(endReasonId);
      studentDAO.endStudentStudies(student, endDate, endReason, endReasonText);
    }
  }

  public StudentEntity getStudentById(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return EntityFactoryVault.buildFromDomainObject(studentDAO.getStudent(studentId));
  }

  public StudentEntity[] listStudents() {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listStudents());
  }

  public AddressEntity[] listStudentsAddresses(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    return (AddressEntity[]) EntityFactoryVault.buildFromDomainObjects(student.getContactInfo().getAddresses());
  }
  
  public StudentEntity[] listStudentsByStudyProgramme(Long studyProgrammeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    StudyProgramme studyProgramme = baseDAO.getStudyProgramme(studyProgrammeId);
    
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listStudentsByStudyProgramme(studyProgramme));
  }
  
  public StudentEntity[] listStudentsByAbstractStudent(Long abstractStudentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);

    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listStudentsByAbstractStudent(abstractStudent));
  }
  
  public StudentEntity[] listActiveStudentsByAbstractStudent(Long abstractStudentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);

    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listActiveStudentsByAbstractStudent(abstractStudent));
  }

  public StudentEntity[] listActiveStudents() {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listActiveStudents());
  }

  public StudentEntity[] listActiveStudentsByStudyProgramme(Long studyProgrammeId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    StudyProgramme studyProgramme = baseDAO.getStudyProgramme(studyProgrammeId);
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listActiveStudentsByStudyProgramme(studyProgramme));
  }
  
  public StudentEntity createStudent(Long abstractStudentId, String firstName, String lastName, String nickname, String phone, String additionalInfo,
      String parentalInfo, Date studyTimeEnd, Long activityTypeId, Long examinationTypeId, Long educationalLevelId, String education, Long nationalityId,
      Long municipalityId, Long languageId, Long schoolId, Long studyProgrammeId, Double previousStudies, Date studyStartDate, Date studyEndDate,
      Long studyEndReasonId, String studyEndText, Boolean lodging) {

    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);
    Nationality nationality = nationalityId == null ? null : baseDAO.getNationality(nationalityId);
    Municipality municipality = municipalityId == null ? null : baseDAO.getMunicipality(municipalityId);
    Language language = languageId == null ? null : baseDAO.getLanguage(languageId);
    StudentActivityType activityType = activityTypeId == null ? null : studentDAO.getStudentActivityType(activityTypeId);
    StudentExaminationType examinationType = examinationTypeId == null ? null : studentDAO.getStudentExaminationType(examinationTypeId);
    StudentEducationalLevel educationalLevel = educationalLevelId == null ? null : studentDAO.getStudentEducationalLevel(educationalLevelId);
    School school = schoolId == null ? null : baseDAO.getSchool(schoolId);
    StudyProgramme studyProgramme = studyProgrammeId == null ? null : baseDAO.getStudyProgramme(studyProgrammeId);
    StudentStudyEndReason studyEndReason = studyEndReasonId == null ? null : studentDAO.getStudentStudyEndReason(studyEndReasonId);

    Student student = studentDAO.createStudent(abstractStudent, firstName, lastName, nickname, additionalInfo, studyTimeEnd, activityType,
        examinationType, educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies, studyStartDate,
        studyEndDate, studyEndReason, studyEndText, lodging);
    
    // TODO Proper handling for phone and parental info
    ContactType contactType = baseDAO.getContactTypeById(new Long(1));
    baseDAO.createPhoneNumber(student.getContactInfo(), contactType, Boolean.TRUE, phone);
    baseDAO.updateContactInfo(student.getContactInfo(), parentalInfo);

    validateEntity(student);
    return EntityFactoryVault.buildFromDomainObject(student);
  }

  public void updateStudent(Long studentId, String firstName, String lastName, String nickname, String phone, String additionalInfo, String parentalInfo,
      Date studyTimeEnd, Long activityTypeId, Long examinationTypeId, Long educationalLevelId, String education, Long nationalityId, Long municipalityId,
      Long languageId, Long schoolId, Long studyProgrammeId, Double previousStudies, Date studyStartDate, Date studyEndDate, Long studyEndReasonId,
      String studyEndText, Boolean lodging) {

    // TODO Get rid of phone number and parental info

    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Student student = studentDAO.getStudent(studentId);
    Nationality nationality = nationalityId == null ? null : baseDAO.getNationality(nationalityId);
    Municipality municipality = municipalityId == null ? null : baseDAO.getMunicipality(municipalityId);
    Language language = languageId == null ? null : baseDAO.getLanguage(languageId);
    StudentActivityType activityType = activityTypeId == null ? null : studentDAO.getStudentActivityType(activityTypeId);
    StudentExaminationType examinationType = activityTypeId == null ? null : studentDAO.getStudentExaminationType(examinationTypeId);
    StudentEducationalLevel educationalLevel = educationalLevelId == null ? null : studentDAO.getStudentEducationalLevel(educationalLevelId);
    School school = schoolId == null ? null : baseDAO.getSchool(schoolId);
    StudyProgramme studyProgramme = studyProgrammeId == null ? null : baseDAO.getStudyProgramme(studyProgrammeId);
    StudentStudyEndReason studyEndReason = studyEndReasonId == null ? null : studentDAO.getStudentStudyEndReason(studyEndReasonId);

    studentDAO.updateStudent(student, firstName, lastName, nickname, additionalInfo, studyTimeEnd, activityType, examinationType,
        educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies, studyStartDate, studyEndDate,
        studyEndReason, studyEndText, lodging);

    validateEntity(student);
  }

  public void updateStudentMunicipality(Long studentId, Long municipalityId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Student student = studentDAO.getStudent(studentId);
    Municipality municipality = municipalityId == null ? null : baseDAO.getMunicipality(municipalityId);

    studentDAO.updateStudentMunicipality(student, municipality);

    validateEntity(student);
  }
  
  public void addStudentAddress(Long studentId, String addressType, String name, String streetAddress, String postalCode, String city, String country) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    // TODO contactType and default address
    ContactType contactType = baseDAO.getContactTypeById(new Long(1));
    Address address = baseDAO.createAddress(student.getContactInfo(), contactType, name, streetAddress, postalCode, city, country, Boolean.TRUE);
    validateEntity(address);
  }

  public void addStudentEmail(Long studentId, Boolean defaultAddress, String address) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    // TODO contactType
    ContactType contactType = baseDAO.getContactTypeById(new Long(1));
    Email email = baseDAO.createEmail(student.getContactInfo(), contactType, defaultAddress, address);
    validateEntity(email);
  }

  public void archiveStudent(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    studentDAO.archiveStudent(student);
  }

  public void unarchiveStudent(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    studentDAO.unarchiveStudent(student);
  }

  public void updateStudentEmail(Long studentId, String fromAddress, String toAddress) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    for (Email email : student.getContactInfo().getEmails()) {
      if (email.getAddress().equals(fromAddress)) {
        email = baseDAO.updateEmail(email, email.getContactType(), email.getDefaultAddress(), toAddress);
        validateEntity(email);
        break;
      }
    }
  }

  public void removeStudentEmail(Long studentId, String address) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    Student student = studentDAO.getStudent(studentId);
    for (Email email : student.getContactInfo().getEmails()) {
      if (email.getAddress().equals(address)) {
        baseDAO.removeEmail(email);
        break;
      }
    }
  }

  public StudentEntity[] listStudentsByStudentVariable(String key, String value) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return (StudentEntity[]) EntityFactoryVault.buildFromDomainObjects(studentDAO.listStudentsByStudentVariable(key, value));
  }

  public String getStudentVariable(Long studentId, String key) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    return studentDAO.getStudentVariable(studentDAO.getStudent(studentId), key);
  }

  public void setStudentSchool(Long studentId, Long schoolId) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Student student = studentDAO.getStudent(studentId);
    School school = schoolId == null ? null : baseDAO.getSchool(schoolId);
    studentDAO.setStudentSchool(student, school);
  }

  public void setStudentVariable(Long studentId, String key, String value) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    studentDAO.setStudentVariable(studentDAO.getStudent(studentId), key, value);
  }

}
