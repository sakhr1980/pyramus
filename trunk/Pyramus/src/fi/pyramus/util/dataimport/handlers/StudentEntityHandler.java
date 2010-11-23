package fi.pyramus.util.dataimport.handlers;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.util.dataimport.DataImportContext;
import fi.pyramus.util.dataimport.DefaultEntityHandlingStrategy;

public class StudentEntityHandler extends DefaultEntityHandlingStrategy {

  public StudentEntityHandler() {
    super(Student.class, "Student");
  }
  
  @Override
  public void initializeContext(DataImportContext context) {
    super.initializeContext(context);
    
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    
    if (context.hasField("socialSecurityNumber")) {
      String ssn = context.getFieldValue("socialSecurityNumber");

      if (ssn != null) {
        AbstractStudent abstractStudent = studentDAO.getAbstractStudentBySSN(ssn);
        context.addEntity(AbstractStudent.class, abstractStudent);
      }
    }
    
    if (context.hasField("studyProgramme")) {
      String studyProgramme = context.getFieldValue("studyProgramme");
      if (studyProgramme != null) {
        StudyProgramme studentStudyProgramme = baseDAO.findStudyProgammeByName(studyProgramme);
        context.addEntity(StudyProgramme.class, studentStudyProgramme);
      }
    }
  }
  
  @Override
  protected void bindEntities(DataImportContext context) {
    super.bindEntities(context);
    
    // Bind Address, Email, Phone, AbstractStudent etc. to Student or create new ones where needed
    Student student = (Student) context.getEntity(Student.class);
    
    AbstractStudent abstractStudent = (AbstractStudent) context.getEntity(AbstractStudent.class);
    if (abstractStudent == null) {
      abstractStudent = new AbstractStudent();
      context.addEntity(AbstractStudent.class, abstractStudent);
    }
    
    abstractStudent.addStudent(student);

    // Adress        
    Address address = (Address) context.getEntity(Address.class);
    if (address != null) {
      ContactInfo contactInfo = getStudentContactInfo(student);
      contactInfo.addAddress(address);
    }

    // Email
    Email email = (Email) context.getEntity(Email.class);
    if (email != null) {
      ContactInfo contactInfo = getStudentContactInfo(student);
      contactInfo.addEmail(email);
    }

    // PhoneNumber
    PhoneNumber phone = (PhoneNumber) context.getEntity(PhoneNumber.class);
    if (phone != null) {
      ContactInfo contactInfo = getStudentContactInfo(student);
      contactInfo.addPhoneNumber(phone);
    }
  }

  private ContactInfo getStudentContactInfo(Student student) {
    ContactInfo result = student.getContactInfo();
    if (result == null) {
      result = new ContactInfo();
      student.setContactInfo(result);
    }
    return result;
  }
}
