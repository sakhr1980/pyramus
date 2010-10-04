package fi.pyramus.json.students;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentActivityType;
import fi.pyramus.domainmodel.students.StudentEducationalLevel;
import fi.pyramus.domainmodel.students.StudentExaminationType;
import fi.pyramus.domainmodel.students.StudentStudyEndReason;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;
import fi.pyramus.persistence.usertypes.Sex;

public class CreateStudentJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();

    Date birthday = requestContext.getDate("birthday");
    String ssecId = requestContext.getString("ssecId");
    Sex sex = "male".equals(requestContext.getRequest().getParameter("gender")) ? Sex.MALE : Sex.FEMALE;
    String basicInfo = requestContext.getString("basicInfo");
    String firstName = requestContext.getString("firstName");
    String lastName = requestContext.getString("lastName");
    String nickname = requestContext.getString("nickname");
    String additionalInfo = requestContext.getString("additionalInfo");
    String otherContactInfo = requestContext.getString("otherContactInfo");
    String education = requestContext.getString("education");
    Boolean lodging = "1".equals(requestContext.getString("lodging"));
    Double previousStudies = requestContext.getDouble("previousStudies");
    Date studyTimeEnd = requestContext.getDate("studyTimeEnd");
    Date studyStartTime = requestContext.getDate("studyStartDate");
    Date studyEndTime = requestContext.getDate("studyEndDate");
    String studyEndText = requestContext.getString("studyEndText");
    String tagsText = requestContext.getString("tags");
    
    Set<Tag> tagEntities = new HashSet<Tag>();
    if (!StringUtils.isBlank(tagsText)) {
      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
      for (String tag : tags) {
        Tag tagEntity = baseDAO.findTagByText(tag.trim());
        if (tagEntity == null)
          tagEntity = baseDAO.createTag(tag);
        tagEntities.add(tagEntity);
      }
    }
    
    Long entityId = requestContext.getLong("language");
    Language language = entityId == null ? null : baseDAO.getLanguage(entityId);

    entityId = requestContext.getLong("municipality");
    Municipality municipality = entityId == null ? null : baseDAO.getMunicipality(entityId);

    entityId = requestContext.getLong("activityType");
    StudentActivityType activityType = entityId == null ? null : studentDAO.getStudentActivityType(entityId);

    entityId = requestContext.getLong("examinationType");
    StudentExaminationType examinationType = entityId == null ? null : studentDAO.getStudentExaminationType(entityId);

    entityId = requestContext.getLong("educationalLevel");
    StudentEducationalLevel educationalLevel = entityId == null ? null : studentDAO
        .getStudentEducationalLevel(entityId);

    entityId = requestContext.getLong("nationality");
    Nationality nationality = entityId == null ? null : baseDAO.getNationality(entityId);

    entityId = requestContext.getLong("school");
    School school = entityId != null && entityId > 0 ? baseDAO.getSchool(entityId) : null;

    entityId = requestContext.getLong("studyProgramme");
    StudyProgramme studyProgramme = entityId != null && entityId > 0 ? baseDAO.getStudyProgramme(entityId) : null;

    entityId = requestContext.getLong("studyEndReason");
    StudentStudyEndReason studyEndReason = entityId == null ? null : studentDAO.getStudentStudyEndReason(entityId);

    // TODO: Find the abstract student by the bday+ssecid combination

    AbstractStudent abstractStudent = studentDAO.createAbstractStudent(birthday, ssecId, sex, basicInfo);
    
    Student student = studentDAO.createStudent(abstractStudent, firstName, lastName, nickname, additionalInfo,
        studyTimeEnd, activityType, examinationType, educationalLevel, education, nationality, municipality,
        language, school, studyProgramme, previousStudies, studyStartTime, studyEndTime, studyEndReason, studyEndText, lodging);

    // Tags

    studentDAO.setStudentTags(student, tagEntities);
    
    // Contact info
    
    baseDAO.updateContactInfo(student.getContactInfo(), otherContactInfo);

    // Addresses
    
    int addressCount = requestContext.getInteger("addressTable.rowCount");
    for (int i = 0; i < addressCount; i++) {
      String colPrefix = "addressTable." + i;
      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String name = requestContext.getString(colPrefix + ".name");
      String street = requestContext.getString(colPrefix + ".street");
      String postal = requestContext.getString(colPrefix + ".postal");
      String city = requestContext.getString(colPrefix + ".city");
      String country = requestContext.getString(colPrefix + ".country");
      boolean hasAddress = name != null || street != null || postal != null || city != null || country != null;
      if (hasAddress) {
        baseDAO.createAddress(student.getContactInfo(), contactType, name, street, postal, city, country, defaultAddress);
      }
    }
    
    // Email addresses

    int emailCount = requestContext.getInteger("emailTable.rowCount");
    for (int i = 0; i < emailCount; i++) {
      String colPrefix = "emailTable." + i;
      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String email = requestContext.getString(colPrefix + ".email");
      if (email != null) {
        baseDAO.createEmail(student.getContactInfo(), contactType, defaultAddress, email);
      }
    }
    
    // Phone numbers

    int phoneCount = requestContext.getInteger("phoneTable.rowCount");
    for (int i = 0; i < phoneCount; i++) {
      String colPrefix = "phoneTable." + i;
      Boolean defaultNumber = requestContext.getBoolean(colPrefix + ".defaultNumber");
      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
      String number = requestContext.getString(colPrefix + ".phone");
      if (number != null) {
        baseDAO.createPhoneNumber(student.getContactInfo(), contactType, defaultNumber, number);
      }
    }
    
    // Student variables

    Integer variableCount = requestContext.getInteger("variablesTable.rowCount");
    if (variableCount != null) {
      for (int i = 0; i < variableCount; i++) {
        String colPrefix = "variablesTable." + i;
        String variableKey = requestContext.getRequest().getParameter(colPrefix + ".key");
        String variableValue = requestContext.getRequest().getParameter(colPrefix + ".value");
        studentDAO.setStudentVariable(student, variableKey, variableValue);
      }
    }
    
    // Contact information of a student won't be reflected to AbstractStudent
    // used when searching students, so a manual re-index is needed
    
    systemDAO.forceReindex(student.getAbstractStudent());
    
    String redirectURL = requestContext.getRequest().getContextPath() + "/students/editstudent.page?abstractStudent=" + student.getAbstractStudent().getId();
    String refererAnchor = requestContext.getRefererAnchor();
    
    if (!StringUtils.isBlank(refererAnchor)) {
      redirectURL += "#" + refererAnchor;
    }

    requestContext.setRedirectURL(redirectURL);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}