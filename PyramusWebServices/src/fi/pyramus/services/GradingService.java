package fi.pyramus.services;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.EducationalTimeUnitDAO;
import fi.pyramus.dao.base.SchoolDAO;
import fi.pyramus.dao.base.SubjectDAO;
import fi.pyramus.dao.courses.CourseStudentDAO;
import fi.pyramus.dao.grading.CourseAssessmentDAO;
import fi.pyramus.dao.grading.GradeDAO;
import fi.pyramus.dao.grading.GradingScaleDAO;
import fi.pyramus.dao.grading.TransferCreditDAO;
import fi.pyramus.dao.students.StudentDAO;
import fi.pyramus.dao.users.UserDAO;
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
    TransferCreditDAO transferCreditDAO = DAOFactory.getInstance().getTransferCreditDAO();
    return (TransferCreditEntity[]) EntityFactoryVault.buildFromDomainObjects(transferCreditDAO.listByStudent(studentDAO.findById(studentId)));
  }
  
  public CourseAssessmentEntity[] listStudentsCourseAssessments(Long studentId) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseAssessmentDAO courseAssessmentDAO = DAOFactory.getInstance().getCourseAssessmentDAO();
    return (CourseAssessmentEntity[]) EntityFactoryVault.buildFromDomainObjects(courseAssessmentDAO.listByStudent(studentDAO.findById(studentId)));
  }

  public GradeEntity createGrade(Long gradingScaleId, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    GradingScaleDAO gradingScaleDAO = DAOFactory.getInstance().getGradingScaleDAO();
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    Grade grade = gradeDAO.create(gradingScaleDAO.findById(gradingScaleId), name, description, passingGrade, GPA, qualification);
    validateEntity(grade);
    return EntityFactoryVault.buildFromDomainObject(grade);
  }
  
  public GradingScaleEntity createGradingScale(String name, String description) {
    GradingScaleDAO gradingScaleDAO = DAOFactory.getInstance().getGradingScaleDAO();
    GradingScale gradingScale = gradingScaleDAO.create(name, description);
    validateEntity(gradingScale);
    return EntityFactoryVault.buildFromDomainObject(gradingScale);
  } 
  
  public GradeEntity getGradeById(Long gradeId) {
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    return EntityFactoryVault.buildFromDomainObject(gradeDAO.findById(gradeId));
  }
  
  public GradingScaleEntity getGradingScaleById(Long gradingScaleId) {
    GradingScaleDAO gradingScaleDAO = DAOFactory.getInstance().getGradingScaleDAO();
    return EntityFactoryVault.buildFromDomainObject(gradingScaleDAO.findById(gradingScaleId));
  }
  
  public GradingScaleEntity[] listGradingScales() {
    GradingScaleDAO gradingScaleDAO = DAOFactory.getInstance().getGradingScaleDAO();
    return (GradingScaleEntity[]) EntityFactoryVault.buildFromDomainObjects(gradingScaleDAO.listUnarchived());
  }
  
  public void updateGrade(Long gradeId, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    Grade grade = gradeDAO.findById(gradeId);
    gradeDAO.update(grade, name, description, passingGrade, GPA, qualification);
    validateEntity(grade);
  }
  
  public void updateGradingScale(Long gradingScaleId, String name, String description) {
    GradingScaleDAO gradingScaleDAO = DAOFactory.getInstance().getGradingScaleDAO();
    GradingScale gradingScale = gradingScaleDAO.findById(gradingScaleId);
    gradingScaleDAO.update(gradingScale, name, description);
    validateEntity(gradingScale);
  }
  
  public CourseAssessmentEntity createCourseAssessment(Long courseStudentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    CourseAssessmentDAO courseAssessmentDAO = DAOFactory.getInstance().getCourseAssessmentDAO();

    User assessingUser = userDAO.findById(assessingUserId);
    Grade grade = gradeDAO.findById(gradeId);
    
    CourseStudent courseStudent = courseStudentDAO.findById(courseStudentId);
    
    CourseAssessment courseAssessment = courseAssessmentDAO.create(courseStudent, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(courseAssessment);
    
    return EntityFactoryVault.buildFromDomainObject(courseAssessment);
  }
  
  public CourseAssessmentEntity updateCourseAssessment(Long courseAssessmentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    CourseAssessmentDAO courseAssessmentDAO = DAOFactory.getInstance().getCourseAssessmentDAO();

    User assessingUser = userDAO.findById(assessingUserId);
    Grade grade = gradeDAO.findById(gradeId);
    CourseAssessment courseAssessment = courseAssessmentDAO.findById(courseAssessmentId);
    
    courseAssessment = courseAssessmentDAO.update(courseAssessment, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(courseAssessment);
    
    return EntityFactoryVault.buildFromDomainObject(courseAssessment);
  }
  
  public CourseAssessmentEntity getCourseAssessmentByCourseStudentId(Long courseStudentId) {
    CourseStudentDAO courseStudentDAO = DAOFactory.getInstance().getCourseStudentDAO();
    CourseAssessmentDAO courseAssessmentDAO = DAOFactory.getInstance().getCourseAssessmentDAO();

    CourseStudent courseStudent = courseStudentDAO.findById(courseStudentId);
    
    CourseAssessment courseAssessment = courseAssessmentDAO.findByCourseStudent(courseStudent);
    return EntityFactoryVault.buildFromDomainObject(courseAssessment);
  }
  
  public TransferCreditEntity createTransferCredit(String courseName, Integer courseNumber, Double courseLength, Long courseLengthUnitId, Long schoolId, Long subjectId, String optionality, Long studentId, Long assessingUserId, Long gradeId, Date date, String verbalAssessment) {
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    GradeDAO gradeDAO = DAOFactory.getInstance().getGradeDAO();
    TransferCreditDAO transferCreditDAO = DAOFactory.getInstance().getTransferCreditDAO();
    EducationalTimeUnitDAO educationalTimeUnitDAO = DAOFactory.getInstance().getEducationalTimeUnitDAO();
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();
    SubjectDAO subjectDAO = DAOFactory.getInstance().getSubjectDAO();

    EducationalTimeUnit courseLengthUnit = educationalTimeUnitDAO.findById(courseLengthUnitId);
    School school = schoolDAO.findById(schoolId);
    Subject subject = subjectDAO.findById(subjectId);
    Student student = studentDAO.findById(studentId);
    User assessingUser = userDAO.findById(assessingUserId);
    Grade grade = gradeDAO.findById(gradeId);
    CourseOptionality courseOptionality = null;
    if (!StringUtils.isBlank(optionality))
      courseOptionality = CourseOptionality.valueOf(optionality);
      
    TransferCredit transferCredit = transferCreditDAO.create(courseName, courseNumber, courseLength, courseLengthUnit, school, subject, courseOptionality, student, assessingUser, grade, date, verbalAssessment);
    
    validateEntity(transferCredit);
    
    return EntityFactoryVault.buildFromDomainObject(transferCredit);
  }

}