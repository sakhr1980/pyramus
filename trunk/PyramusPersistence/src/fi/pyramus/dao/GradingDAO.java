package fi.pyramus.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Credit;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.usertypes.CourseOptionality;

/**
 * The Data Access Object for grading related operations.  
 */
public class GradingDAO extends PyramusDAO {

  /**
   * Lists all student's transfer credits excluding archived ones
   * 
   * @return list of all students transfer credits
   */
  @SuppressWarnings("unchecked")
  public List<TransferCredit> listStudentsTransferCredits(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(TransferCredit.class)
      .add(Restrictions.eq("student", student))
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  
  /**
   * Lists all student's course assessments excluding archived ones
   * 
   * @return list of all students course assessments
   */
  @SuppressWarnings("unchecked")
  public List<CourseAssessment> listStudentsCourseAssessments(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(CourseAssessment.class)
      .add(Restrictions.eq("student", student))
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }  
  
  /**
   * Lists all student's credits excluding archived ones
   * 
   * @return list of all students credits
   */
  @SuppressWarnings("unchecked")
  public List<Credit> listStudentsCredits(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(Credit.class)
      .add(Restrictions.eq("student", student))
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  /**
   * Archives a Grade
   * 
   * @param grade Grade to be deleted
   */
  public void archiveGrade(Grade grade)  {
    Session s = getHibernateSession();
    grade.setArchived(Boolean.TRUE);
    s.saveOrUpdate(grade);
  }

  public void unarchiveGrade(Grade grade)  {
    Session s = getHibernateSession();
    grade.setArchived(Boolean.FALSE);
    s.saveOrUpdate(grade);
  }
  
  /**
   * Archives a GradingScale
   * 
   * @param gradingScale GradingScale to be deleted
   */
  public void archiveGradingScale(GradingScale gradingScale)  {
    Session s = getHibernateSession();
    gradingScale.setArchived(Boolean.TRUE);
    s.saveOrUpdate(gradingScale);
  }

  public void unarchiveGradingScale(GradingScale gradingScale)  {
    Session s = getHibernateSession();
    gradingScale.setArchived(Boolean.FALSE);
    s.saveOrUpdate(gradingScale);
  }
  
  /**
   * Creates new Grade
   * 
   * @param name grades's name
   * @param description description for grade
   * @param passingGrade indicates that grade is or is not a passing grade 
   * @param GPA grade points average or numeric representation of grade in grading system which don't use GPAs
   * @param qualification literal equivalent for grade (e.x. excellent)  
   *  
   * @return Grade
   */
  public Grade createGrade(GradingScale gradingScale, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    Grade grade = new Grade();
    grade.setName(name);
    grade.setDescription(description);
    grade.setGPA(GPA);
    grade.setPassingGrade(passingGrade);
    grade.setQualification(qualification);
    
    gradingScale.addGrade(grade);
    
    Session s = getHibernateSession();
    s.saveOrUpdate(grade);
    s.saveOrUpdate(gradingScale);
    
    return grade;
  }

  /**
   * Creates new GradingScale
   * 
   * @param name scale's name
   * @param description description for scale
   * @return GradingScale
   */
  public GradingScale createGradingScale(String name, String description) {
    GradingScale gradingScale = new GradingScale();
    gradingScale.setName(name);
    gradingScale.setDescription(description);
    
    Session s = getHibernateSession();
    s.saveOrUpdate(gradingScale);
    
    return gradingScale;
  }

  /**
   * Deletes a Grade
   * 
   * @param grade Grade to be deleted
   */
  public void deleteGrade(Grade grade)  {
    Session s = getHibernateSession();
    s.delete(grade);
  }

  /**
   * Deletes a GradingScale
   * 
   * @param gradingScale GradingScale to be deleted
   */
  public void deleteGradingScale(GradingScale gradingScale)  {
    Session s = getHibernateSession();
    s.delete(gradingScale);
  }

  /**
   * Returns grade
   * 
   * @return Grade
   */
  public Grade getGrade(Long id) {
    Session s = getHibernateSession();
    return (Grade) s.load(Grade.class, id);
  }

  /**
   * Returns grading scale
   * 
   * @return GradingScale
   */
  public GradingScale getGradingScale(Long id) {
    Session s = getHibernateSession();
    return (GradingScale) s.load(GradingScale.class, id);
  }

  /**
   * Returns a list of non archived grading scales
   * 
   * @return a list of non archived grading scales
   */
  @SuppressWarnings("unchecked")
  public List<GradingScale> listGradingScales() {
    Session s = getHibernateSession();
    return s.createCriteria(GradingScale.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  /**
   * Creates new Grade
   * 
   * @param name grades's name
   * @param description description for grade
   * @param passingGrade indicates that grade is or is not a passing grade 
   * @param GPA grade points average or numeric representation of grade in grading system which don't use GPAs
   * @param qualification literal equivalent for grade (e.x. excellent)  
   *  
   * @return Grade
   */
  public Grade updateGrade(Grade grade, String name, String description, Boolean passingGrade, Double GPA, String qualification) {
    grade.setName(name);
    grade.setDescription(description);
    grade.setGPA(GPA);
    grade.setPassingGrade(passingGrade);
    grade.setQualification(qualification);
    Session s = getHibernateSession();
    s.saveOrUpdate(grade);
    
    return grade;
  }

  /**
   * Updates GradingScale
   * 
   * @param name scale's name
   * @param description description for scale
   * @return GradingScale
   */
  public GradingScale updateGradingScale(GradingScale gradingScale, String name, String description) {
    gradingScale.setName(name);
    gradingScale.setDescription(description);
    
    Session s = getHibernateSession();
    s.saveOrUpdate(gradingScale);
    
    return gradingScale;
  }
  
  public CourseAssessment createCourseAssessment(Course course, Student student, User assessingUser, Grade grade, Date date, String verbalAssessment) {
    CourseAssessment courseAssessment = new CourseAssessment();
    courseAssessment.setAssessingUser(assessingUser);
    courseAssessment.setCourse(course);
    courseAssessment.setDate(date);
    courseAssessment.setGrade(grade);
    courseAssessment.setStudent(student);
    courseAssessment.setVerbalAssessment(verbalAssessment);
    
    Session s = getHibernateSession();
    s.saveOrUpdate(courseAssessment);
    
    return courseAssessment;
  }
  
  public CourseAssessment getCourseAssessment(Long courseAssessmentId) {
    Session s = getHibernateSession();
    return (CourseAssessment) s.load(CourseAssessment.class, courseAssessmentId);
  }
  
  public TransferCredit createTransferCredit(String courseName, Integer courseNumber, Double courseLength, EducationalTimeUnit courseLengthUnit, School school, Subject subject, CourseOptionality optionality, Student student, User assessingUser, Grade grade, Date date, String verbalAssessment) {
    TransferCredit transferCredit = new TransferCredit();
    
    EducationalLength length = new EducationalLength();
    length.setUnits(courseLength);
    length.setUnit(courseLengthUnit);
    
    transferCredit.setAssessingUser(assessingUser);
    transferCredit.setDate(date);
    transferCredit.setGrade(grade);
    transferCredit.setCourseLength(length);
    transferCredit.setCourseName(courseName);
    transferCredit.setCourseNumber(courseNumber);
    transferCredit.setSchool(school);
    transferCredit.setStudent(student);
    transferCredit.setSubject(subject);
    transferCredit.setOptionality(optionality);
    transferCredit.setVerbalAssessment(verbalAssessment);
    
    Session s = getHibernateSession();
    s.saveOrUpdate(length);
    s.saveOrUpdate(transferCredit);
    
    return transferCredit;
  }
  
  public TransferCredit getTransferCredit(Long transferCreditId) {
    Session s = getHibernateSession();
    return (TransferCredit) s.load(TransferCredit.class, transferCreditId);
  }

  public void archiveCredit(Credit credit)  {
    Session s = getHibernateSession();
    credit.setArchived(Boolean.TRUE);
    s.saveOrUpdate(credit);
  }

  public void unarchiveCredit(Credit credit)  {
    Session s = getHibernateSession();
    credit.setArchived(Boolean.FALSE);
    s.saveOrUpdate(credit);
  }
}
