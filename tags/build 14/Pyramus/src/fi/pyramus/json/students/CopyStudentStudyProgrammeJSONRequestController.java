package fi.pyramus.json.students;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.base.Address;
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
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

public class CopyStudentStudyProgrammeJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Long studentId = NumberUtils.createLong(requestContext.getRequest().getParameter("studentId"));
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
    StudyProgramme studyProgramme = null; // student.getStudyProgramme();
    StudentStudyEndReason studyEndReason = null; // student.getStudyEndReason();
    Boolean lodging = false; // oldStudent.getLodging();

    Student newStudent = studentDAO.createStudent(abstractStudent, firstName, lastName, nickname, additionalInfo, studyTimeEnd,
        activityType, examinationType, educationalLevel, education, nationality, municipality, language, school, studyProgramme, previousStudies,
        studyStartTime, studyEndTime, studyEndReason, studyEndText, lodging);
    
    // Contact info
    
    baseDAO.updateContactInfo(newStudent.getContactInfo(), oldStudent.getContactInfo().getAdditionalInfo());

    // Addresses

    List<Address> addresses = oldStudent.getContactInfo().getAddresses();
    for (int i = 0; i < addresses.size(); i++) {
      Address add = addresses.get(i);
      baseDAO.createAddress(newStudent.getContactInfo(), add.getContactType(), add.getName(), add.getStreetAddress(), add.getPostalCode(), add.getCity(),
          add.getCountry(), add.getDefaultAddress());
    }

    // Email addresses

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

    String redirectURL = requestContext.getRequest().getContextPath() + "/students/editstudent.page?student=" + newStudent.getAbstractStudent().getId();
    String refererAnchor = requestContext.getRefererAnchor();

    if (!StringUtils.isBlank(refererAnchor))
      redirectURL += "#" + refererAnchor;

    requestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}