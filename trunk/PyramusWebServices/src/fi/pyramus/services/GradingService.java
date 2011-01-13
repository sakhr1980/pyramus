package fi.pyramus.services;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.grading.CourseAssessmentEntity;
import fi.pyramus.services.entities.grading.GradeEntity;
import fi.pyramus.services.entities.grading.GradingScaleEntity;
import fi.pyramus.services.entities.grading.TransferCreditEntity;

public class GradingService extends PyramusService {
  
  public TransferCreditEntity[] listStudentsTransferCredits(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (TransferCreditEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listTransferCreditsByStudent(studentDAO.getStudent(studentId)));
  }
  
  public CourseAssessmentEntity[] listStudentsCourseAssessments(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (CourseAssessmentEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listCourseAssessmentsByStudent(studentDAO.getStudent(studentId)));
  }

  public GradeEntity createGrade(Long gradingScaleId, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    Grade grade = gradingDAO.createGrade(gradingDAO.findGradingScaleById(gradingScaleId), name, description, passingGrade, GPA, qualification);
    validateEntity(grade);
    return EntityFactoryVault.buildFromDomainObject(grade);
  }
  
  public GradingScaleEntity createGradingScale(String name, String description) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    GradingScale gradingScale = gradingDAO.createGradingScale(name, description);
    validateEntity(gradingScale);
    return EntityFactoryVault.buildFromDomainObject(gradingScale);
  } 
  
  public GradeEntity getGradeById(Long gradeId) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return EntityFactoryVault.buildFromDomainObject(gradingDAO.findGradeById(gradeId));
  }
  
  public GradingScaleEntity getGradingScaleById(Long gradingScaleId) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return EntityFactoryVault.buildFromDomainObject(gradingDAO.findGradingScaleById(gradingScaleId));
  }
  
  public GradingScaleEntity[] listGradingScales() {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (GradingScaleEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listGradingScales());
  }
  
  public void updateGrade(Long gradeId, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    Grade grade = gradingDAO.findGradeById(gradeId);
    gradingDAO.updateGrade(grade, name, description, passingGrade, GPA, qualification);
    validateEntity(grade);
  }
  
  public void updateGradingScale(Long gradingScaleId, String name, String description) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    GradingScale gradingScale = gradingDAO.findGradingScaleById(gradingScaleId);
    gradingDAO.updateGradingScale(gradingScale, name, description);
    validateEntity(gradingScale);
  }
  
  public CourseAssessmentEntity createCourseAssessment(Long courseStudentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    User assessingUser = userDAO.getUser(assessingUserId);
    Grade grade = gradingDAO.findGradeById(gradeId);
    
    CourseStudent courseStudent = courseDAO.findCourseStudentById(courseStudentId);
    
    CourseAssessment courseAssessment = gradingDAO.createCourseAssessment(courseStudent, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(courseAssessment);
    
    return EntityFactoryVault.buildFromDomainObject(courseAssessment);
  }

  public CourseAssessmentEntity getCourseAssessmentByCourseStudentId(Long courseStudentId) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();

    CourseStudent courseStudent = courseDAO.findCourseStudentById(courseStudentId);
    
    CourseAssessment courseAssessment = gradingDAO.findCourseAssessmentByCourseStudent(courseStudent);
    return EntityFactoryVault.buildFromDomainObject(courseAssessment);
  }
  
  public TransferCreditEntity createTransferCredit(String courseName, Integer courseNumber, Double courseLength, Long courseLengthUnitId, Long schoolId, Long subjectId, String optionality, Long studentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    EducationalTimeUnit courseLengthUnit = baseDAO.findEducationalTimeUnitById(courseLengthUnitId);
    School school = baseDAO.getSchool(schoolId);
    Subject subject = baseDAO.getSubject(subjectId);
    Student student = studentDAO.getStudent(studentId);
    User assessingUser = userDAO.getUser(assessingUserId);
    Grade grade = gradingDAO.findGradeById(gradeId);
    CourseOptionality courseOptionality = null;
    if (!StringUtils.isBlank(optionality))
      courseOptionality = CourseOptionality.valueOf(optionality);
      
    TransferCredit transferCredit = gradingDAO.createTransferCredit(courseName, courseNumber, courseLength, courseLengthUnit, school, subject, courseOptionality, student, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(transferCredit);
    
    return EntityFactoryVault.buildFromDomainObject(transferCredit);
  }

}