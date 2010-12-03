package fi.pyramus.json.students;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.StaleObjectStateException;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.SystemDAO;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactType;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.PhoneNumber;
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

public class EditStudentJSONRequestController implements JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    SystemDAO systemDAO = DAOFactory.getInstance().getSystemDAO();

    Long abstractStudentId = NumberUtils.createLong(requestContext.getRequest().getParameter("abstractStudentId"));
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);

    Date birthday = requestContext.getDate("birthday");
    String ssecId = requestContext.getString("ssecId");
    Sex sex = "male".equals(requestContext.getRequest().getParameter("gender")) ? Sex.MALE : Sex.FEMALE;
    String basicInfo = requestContext.getString("basicInfo");
    Long version = requestContext.getLong("version"); 
    
    if (!abstractStudent.getVersion().equals(version))
      throw new StaleObjectStateException(AbstractStudent.class.getName(), abstractStudent.getId());

    // Abstract student
    studentDAO.updateAbstractStudent(abstractStudent, birthday, ssecId, sex, basicInfo);

    List<Student> students = studentDAO.listStudentsByAbstractStudent(abstractStudent);

    for (Student student : students) {
    	Long studentVersion = requestContext.getLong("studentVersion." + student.getId());
      if (!student.getVersion().equals(studentVersion))
        throw new StaleObjectStateException(Student.class.getName(), student.getId());

      String firstName = requestContext.getString("firstName." + student.getId());
	    String lastName = requestContext.getString("lastName." + student.getId());
	    String nickname = requestContext.getString("nickname." + student.getId());
	    String additionalInfo = requestContext.getString("additionalInfo." + student.getId());
	    String additionalContactInfo = requestContext.getString("additionalContactInfo." + student.getId());
	    String education = requestContext.getString("education." + student.getId());
	    Double previousStudies = requestContext.getDouble("previousStudies." + student.getId());
	    Date studyTimeEnd = requestContext.getDate("studyTimeEnd." + student.getId());
	    Date studyStartDate = requestContext.getDate("studyStartDate." + student.getId());
	    Date studyEndDate = requestContext.getDate("studyEndDate." + student.getId());
	    String studyEndText = requestContext.getString("studyEndText." + student.getId());
	    Boolean lodging = "1".equals(requestContext.getString("lodging." + student.getId()));
	    String tagsText = requestContext.getString("tags." + student.getId());
	    
	    Set<Tag> tagEntities = new HashSet<Tag>();
	    if (!StringUtils.isBlank(tagsText)) {
	      List<String> tags = Arrays.asList(tagsText.split("[\\ ,]"));
	      for (String tag : tags) {
	        if (!StringUtils.isBlank(tag)) {
  	        Tag tagEntity = baseDAO.findTagByText(tag.trim());
  	        if (tagEntity == null)
  	          tagEntity = baseDAO.createTag(tag);
  	        tagEntities.add(tagEntity);
	        }
	      }
	    }
	    
	    Long entityId = requestContext.getLong("language." + student.getId());
	    Language language = entityId == null ? null : baseDAO.getLanguage(entityId);
	
	    entityId = requestContext.getLong("activityType." + student.getId());
	    StudentActivityType activityType = entityId == null ? null : studentDAO.getStudentActivityType(entityId);
	
	    entityId = requestContext.getLong("activityType." + student.getId());
	    StudentExaminationType examinationType = entityId == null ? null : studentDAO.getStudentExaminationType(entityId);
	
	    entityId = requestContext.getLong("educationalLevel." + student.getId());
	    StudentEducationalLevel educationalLevel = entityId == null ? null : studentDAO
	        .getStudentEducationalLevel(entityId);
	
	    entityId = requestContext.getLong("nationality." + student.getId());
	    Nationality nationality = entityId == null ? null : baseDAO.getNationality(entityId);
	
	    entityId = requestContext.getLong("municipality." + student.getId());
	    Municipality municipality = entityId == null ? null : baseDAO.getMunicipality(entityId);
	
	    entityId = requestContext.getLong("school." + student.getId());
	    School school = entityId != null && entityId > 0 ? baseDAO.getSchool(entityId) : null;
	
	    entityId = requestContext.getLong("studyProgramme." + student.getId());
	    StudyProgramme studyProgramme = entityId != null && entityId > 0 ? baseDAO.getStudyProgramme(entityId) : null;
	
	    entityId = requestContext.getLong("studyEndReason." + student.getId());
	    StudentStudyEndReason studyEndReason = entityId == null ? null : studentDAO.getStudentStudyEndReason(entityId);
	
	    Integer variableCount = requestContext.getInteger("variablesTable." + student.getId() + ".rowCount");
	    if (variableCount != null) {
	      for (int i = 0; i < variableCount; i++) {
  	      String colPrefix = "variablesTable." + student.getId() + "." + i;
  	      String variableKey = requestContext.getString(colPrefix + ".key");
  	      String variableValue = requestContext.getString(colPrefix + ".value");
  	      studentDAO.setStudentVariable(student, variableKey, variableValue);
  	    }
	    }
	    
	    // Student

	    studentDAO.updateStudent(student, firstName, lastName, nickname, additionalInfo, studyTimeEnd,
	        activityType, examinationType, educationalLevel, education, nationality, municipality, language, school, studyProgramme,
	        previousStudies, studyStartDate, studyEndDate, studyEndReason, studyEndText, lodging);
	   
	    // Tags

	    studentDAO.setStudentTags(student, tagEntities);
	    
	    // Contact info
	    
	    baseDAO.updateContactInfo(student.getContactInfo(), additionalContactInfo);
	    
	    // Student addresses
	    
	    Set<Long> existingAddresses = new HashSet<Long>();
	    int rowCount = requestContext.getInteger("addressTable." + student.getId() + ".rowCount");
	    for (int i = 0; i < rowCount; i++) {
	      String colPrefix = "addressTable." + student.getId() + "." + i;
	      Long addressId = requestContext.getLong(colPrefix + ".addressId");
	      Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
	      ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
	      String name = requestContext.getString(colPrefix + ".name");
	      String street = requestContext.getString(colPrefix + ".street");
	      String postal = requestContext.getString(colPrefix + ".postal");
	      String city = requestContext.getString(colPrefix + ".city");
	      String country = requestContext.getString(colPrefix + ".country");
	      boolean hasAddress = name != null || street != null || postal != null || city != null || country != null;
	      if (addressId == -1 && hasAddress) {
	        Address address = baseDAO.createAddress(student.getContactInfo(), contactType, name, street, postal, city, country, defaultAddress);
	        existingAddresses.add(address.getId());
	      }
	      else if (addressId > 0) {
	        Address address = baseDAO.getAddressById(addressId);
	        if (hasAddress) {
	          existingAddresses.add(addressId);
	          baseDAO.updateAddress(address, defaultAddress, contactType, name, street, postal, city, country);
	        }
	      }
	    }
	    List<Address> addresses = student.getContactInfo().getAddresses();
	    for (int i = addresses.size() - 1; i >= 0; i--) {
	      Address address = addresses.get(i);
	      if (!existingAddresses.contains(address.getId())) {
	        baseDAO.removeAddress(address);
	      }
	    }

	    // Email addresses

	    Set<Long> existingEmails = new HashSet<Long>();
	    rowCount = requestContext.getInteger("emailTable." + student.getId() + ".rowCount");
	    for (int i = 0; i < rowCount; i++) {
	      String colPrefix = "emailTable." + student.getId() + "." + i;
        Boolean defaultAddress = requestContext.getBoolean(colPrefix + ".defaultAddress");
        ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
	      String email = requestContext.getString(colPrefix + ".email");
	      Long emailId = requestContext.getLong(colPrefix + ".emailId");
	      if (emailId == -1) {
	        emailId = baseDAO.createEmail(student.getContactInfo(), contactType, defaultAddress, email).getId(); 
	      }
	      else {
	        baseDAO.updateEmail(baseDAO.getEmailById(emailId), contactType, defaultAddress, email);
	      }
	      existingEmails.add(emailId);
	    }
	    List<Email> emails = student.getContactInfo().getEmails();
	    for (int i = emails.size() - 1; i >= 0; i--) {
	      Email email = emails.get(i);
	      if (!existingEmails.contains(email.getId())) {
	        baseDAO.removeEmail(email);
	      }
	    }
	    
	    // Phone numbers
	    
      Set<Long> existingPhoneNumbers = new HashSet<Long>();
      rowCount = requestContext.getInteger("phoneTable." + student.getId() + ".rowCount");
      for (int i = 0; i < rowCount; i++) {
        String colPrefix = "phoneTable." + student.getId() + "." + i;
        Boolean defaultNumber = requestContext.getBoolean(colPrefix + ".defaultNumber");
        ContactType contactType = baseDAO.getContactTypeById(requestContext.getLong(colPrefix + ".contactTypeId"));
        String number = requestContext.getString(colPrefix + ".phone");
        Long phoneId = requestContext.getLong(colPrefix + ".phoneId");
        if (phoneId == -1 && number != null) {
          phoneId = baseDAO.createPhoneNumber(student.getContactInfo(), contactType, defaultNumber, number).getId();
          existingPhoneNumbers.add(phoneId);
        }
        else if (phoneId > 0 && number != null) {
          baseDAO.updatePhoneNumber(baseDAO.getPhoneNumberById(phoneId), contactType, defaultNumber, number);
          existingPhoneNumbers.add(phoneId);
        }
      }
      List<PhoneNumber> phoneNumbers = student.getContactInfo().getPhoneNumbers();
      for (int i = phoneNumbers.size() - 1; i >= 0; i--) {
        PhoneNumber phoneNumber = phoneNumbers.get(i);
        if (!existingPhoneNumbers.contains(phoneNumber.getId())) {
          baseDAO.removePhoneNumber(phoneNumber);
        }
      }
    }
    
    // Contact information of a student won't be reflected to AbstractStudent
    // used when searching students, so a manual re-index is needed

    systemDAO.forceReindex(abstractStudent);
        
    requestContext.setRedirectURL(requestContext.getReferer(true));
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}