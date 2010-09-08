package fi.pyramus.services;

import java.util.Date;

import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.dao.UserDAO;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.services.entities.EntityFactoryVault;
import fi.pyramus.services.entities.grading.CourseAssessmentEntity;
import fi.pyramus.services.entities.grading.CreditEntity;
import fi.pyramus.services.entities.grading.GradeEntity;
import fi.pyramus.services.entities.grading.GradingScaleEntity;
import fi.pyramus.services.entities.grading.TransferCreditEntity;

public class GradingService extends PyramusService {
  
  public TransferCreditEntity[] listStudentsTransferCredits(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (TransferCreditEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listStudentsTransferCredits(studentDAO.getStudent(studentId)));
  }
  
  public CourseAssessmentEntity[] listStudentsCourseAssessments(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (CourseAssessmentEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listStudentsCourseAssessments(studentDAO.getStudent(studentId)));
  }
  
  public CreditEntity[] listStudentsCredits(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (CreditEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listStudentsCredits(studentDAO.getStudent(studentId)));
  }
  
  public GradeEntity createGrade(Long gradingScaleId, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    Grade grade = gradingDAO.createGrade(gradingDAO.getGradingScale(gradingScaleId), name, description, passingGrade, GPA, qualification);
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
    return EntityFactoryVault.buildFromDomainObject(gradingDAO.getGrade(gradeId));
  }
  
  public GradingScaleEntity getGradingScaleById(Long gradingScaleId) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return EntityFactoryVault.buildFromDomainObject(gradingDAO.getGradingScale(gradingScaleId));
  }
  
  public GradingScaleEntity[] listGradingScales() {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    return (GradingScaleEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingDAO.listGradingScales());
  }
  
  public void updateGrade(Long gradeId, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    Grade grade = gradingDAO.getGrade(gradeId);
    gradingDAO.updateGrade(grade, name, description, passingGrade, GPA, qualification);
    validateEntity(grade);
  }
  
  public void updateGradingScale(Long gradingScaleId, String name, String description) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();
    GradingScale gradingScale = gradingDAO.getGradingScale(gradingScaleId);
    gradingDAO.updateGradingScale(gradingScale, name, description);
    validateEntity(gradingScale);
  }
  
  public CourseAssessmentEntity createCourseAssessment(Long courseId, Long studentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    Course course = courseDAO.getCourse(courseId);
    Student student = studentDAO.getStudent(studentId);
    User assessingUser = userDAO.getUser(assessingUserId);
    Grade grade = gradingDAO.getGrade(gradeId);
    
    CourseAssessment courseAssessment = gradingDAO.createCourseAssessment(course, student, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(courseAssessment);
    
    return EntityFactoryVault.buildFromDomainObject(courseAssessment);
  }
  
  public TransferCreditEntity createTransferCredit(String courseName, Double courseLength, Long courseLengthUnitId, Long schoolId, Long subjectId, Long studentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    EducationalTimeUnit courseLengthUnit = baseDAO.getEducationalTimeUnit(courseLengthUnitId);
    School school = baseDAO.getSchool(schoolId);
    Subject subject = baseDAO.getSubject(subjectId);
    Student student = studentDAO.getStudent(studentId);
    User assessingUser = userDAO.getUser(assessingUserId);
    Grade grade = gradingDAO.getGrade(gradeId);
    
    TransferCredit transferCredit = gradingDAO.createTransferCredit(courseName, courseLength, courseLengthUnit, school, subject, student, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(transferCredit);
    
    return EntityFactoryVault.buildFromDomainObject(transferCredit);
  }

}