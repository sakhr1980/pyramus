package fi.pyramus.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.EducationalTimeUnit;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.Credit;
import fi.pyramus.domainmodel.grading.Grade;
import fi.pyramus.domainmodel.grading.GradingScale;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.grading.TransferCreditTemplate;
import fi.pyramus.domainmodel.grading.TransferCreditTemplateCourse;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.usertypes.CourseOptionality;
import fi.pyramus.persistence.search.SearchResult;

/**
 * The Data Access Object for grading related operations.  
 */
public class GradingDAO extends PyramusDAO {

  /* Grade */

  /**
   * Returns grade
   * 
   * @return Grade
   */
  public Grade findGradeById(Long id) {
    Session s = getHibernateSession();
    return (Grade) s.load(Grade.class, id);
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
    Session s = getHibernateSession();

    Grade grade = new Grade();
    grade.setName(name);
    grade.setDescription(description);
    grade.setGPA(GPA);
    grade.setPassingGrade(passingGrade);
    grade.setQualification(qualification);
    s.saveOrUpdate(grade);
    
    gradingScale.addGrade(grade);
    
    s.saveOrUpdate(gradingScale);
    
    return grade;
  }

  /**
   * Updates Grade
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
   * Deletes a Grade
   * 
   * @param grade Grade to be deleted
   */
  public void deleteGrade(Grade grade)  {
    Session s = getHibernateSession();
    s.delete(grade);
  }
  
  /* GradingScale*/

  /**
   * Returns grading scale
   * 
   * @return GradingScale
   */
  public GradingScale findGradingScaleById(Long id) {
    Session s = getHibernateSession();
    return (GradingScale) s.load(GradingScale.class, id);
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
   * Returns a list of non archived grading scales
   * 
   * @return a list of non archived grading scales
   */
  @SuppressWarnings("unchecked")
  public List<GradingScale> listGradingScales() {
    Session s = getHibernateSession();
    return s.createCriteria(GradingScale.class)
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
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
  
  /* Credit */
  
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
    
  /**
   * Lists all student's credits excluding archived ones
   * 
   * @return list of all students credits
   */
  @SuppressWarnings("unchecked")
  public List<Credit> listCreditsByStudent(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(Credit.class)
      .add(Restrictions.eq("student", student))
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  
  /* CourseAssessment */
  
  public CourseAssessment findCourseAssessmentById(Long courseAssessmentId) {
    Session s = getHibernateSession();
    return (CourseAssessment) s.load(CourseAssessment.class, courseAssessmentId);
  }
  
  public CourseAssessment createCourseAssessment(CourseStudent courseStudent, User assessingUser, Grade grade, Date date, String verbalAssessment) {
    Session s = getHibernateSession();

    CourseAssessment courseAssessment = new CourseAssessment();
    courseAssessment.setAssessingUser(assessingUser);
    courseAssessment.setCourseStudent(courseStudent);
    courseAssessment.setDate(date);
    courseAssessment.setGrade(grade);
    courseAssessment.setVerbalAssessment(verbalAssessment);
    
    s.saveOrUpdate(courseAssessment);
    
    return courseAssessment;
  }
  
  /**
   * Lists all student's course assessments excluding archived ones
   * 
   * @return list of all students course assessments
   */
  @SuppressWarnings("unchecked")
  public List<CourseAssessment> listCourseAssessmentsByStudent(Student student) {
    Session s = getHibernateSession();
    
    return s.createQuery(
        "from CourseAssessment ca " +
        "where ca.courseStudent.student=:student and ca.archived=:archived and ca.courseStudent.archived=:archived2")
      .setEntity("student", student)
      .setBoolean("archived", Boolean.FALSE)
      .setBoolean("archived2", Boolean.FALSE)
      .list();
  }  
  
  /* TransferCredit */
  
  public TransferCredit findTransferCreditById(Long transferCreditId) {
    Session s = getHibernateSession();
    return (TransferCredit) s.load(TransferCredit.class, transferCreditId);
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
  
  public TransferCredit updateTransferCredit(TransferCredit transferCredit, String courseName, Integer courseNumber, Double courseLength, EducationalTimeUnit courseLengthUnit, School school, Subject subject, CourseOptionality optionality, Student student, User assessingUser, Grade grade, Date date, String verbalAssessment) {
    EntityManager entityManager = getEntityManager();
    
    EducationalLength courseEducationalLength = transferCredit.getCourseLength();
    courseEducationalLength.setUnits(courseLength);
    courseEducationalLength.setUnit(courseLengthUnit);
    entityManager.persist(courseEducationalLength);
    
    transferCredit.setAssessingUser(assessingUser);
    transferCredit.setCourseName(courseName);
    transferCredit.setCourseNumber(courseNumber);
    transferCredit.setDate(date);
    transferCredit.setGrade(grade);
    transferCredit.setOptionality(optionality);
    transferCredit.setSchool(school);
    transferCredit.setStudent(student);
    transferCredit.setSubject(subject);
    transferCredit.setVerbalAssessment(verbalAssessment);
    entityManager.persist(transferCredit);
    
    return transferCredit;
  }
  
  /**
   * Lists all student's transfer credits excluding archived ones
   * 
   * @return list of all students transfer credits
   */
  @SuppressWarnings("unchecked")
  public List<TransferCredit> listTransferCreditsByStudent(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(TransferCredit.class)
      .add(Restrictions.eq("student", student))
      .add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }
  
  /* TransferCreditTemplate */
  
  public TransferCreditTemplate findTransferCreditTemplateById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(TransferCreditTemplate.class, id);
  }
  
  public TransferCreditTemplate createTransferCreditTemplate(String name) {
    EntityManager entityManager = getEntityManager();
    
    TransferCreditTemplate transferCreditTemplate = new TransferCreditTemplate();
    transferCreditTemplate.setName(name);
    
    entityManager.persist(transferCreditTemplate);
    
    return transferCreditTemplate;
  }
  
  public TransferCreditTemplate updateTransferCreditTemplate(TransferCreditTemplate transferCreditTemplate, String name) {
    EntityManager entityManager = getEntityManager();
    
    transferCreditTemplate.setName(name);
    
    entityManager.persist(transferCreditTemplate);
    
    return transferCreditTemplate;
  }
  
  @SuppressWarnings("unchecked")
  public List<TransferCreditTemplate> listTransferCreditTemplates() {
    Session session = getHibernateSession();
    return session.createCriteria(TransferCreditTemplate.class)
      .list();

  }
  
  public void deleteTransferCreditTemplate(TransferCreditTemplate transferCreditTemplate) {
    EntityManager entityManager = getEntityManager();
    entityManager.remove(transferCreditTemplate);
  }
  
  @SuppressWarnings("unchecked")
  public SearchResult<TransferCreditTemplateCourse> searchTransferCreditTemplateCoursesBasic(int resultsPerPage, int page, String text) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "courseName", text, false);
      queryBuilder.append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      String queryString = queryBuilder.toString();
      Query luceneQuery;
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      } else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, TransferCreditTemplateCourse.class)
        .setFirstResult(firstResult)
        .setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<TransferCreditTemplateCourse>(page, pages, hits, firstResult, lastResult, query.list());

    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }
  
  /* TransferCreditTemplateCourse */
  
  public TransferCreditTemplateCourse findTransferCreditTemplateCourseById(Long id) {
    EntityManager entityManager = getEntityManager();
    return entityManager.find(TransferCreditTemplateCourse.class, id);
  }
  
  public TransferCreditTemplateCourse createTransferCreditTemplateCourse(TransferCreditTemplate transferCreditTemplate, String courseName, Integer courseNumber, CourseOptionality optionality, Double courseLength, EducationalTimeUnit courseLengthUnit, Subject subject) {
    EntityManager entityManager = getEntityManager();
    
    EducationalLength courseEductionalLength = new EducationalLength();
    courseEductionalLength.setUnits(courseLength);
    courseEductionalLength.setUnit(courseLengthUnit);
    entityManager.persist(courseEductionalLength);
    
    TransferCreditTemplateCourse transferCreditTemplateCourse = new TransferCreditTemplateCourse();
    transferCreditTemplateCourse.setCourseLength(courseEductionalLength);
    transferCreditTemplateCourse.setCourseName(courseName);
    transferCreditTemplateCourse.setCourseNumber(courseNumber);
    transferCreditTemplateCourse.setOptionality(optionality);
    transferCreditTemplateCourse.setSubject(subject);

    entityManager.persist(transferCreditTemplateCourse);
    
    transferCreditTemplate.addCourse(transferCreditTemplateCourse);

    entityManager.persist(transferCreditTemplate);
  
    return transferCreditTemplateCourse;
  }
  
  public TransferCreditTemplateCourse updateTransferCreditTemplateCourse(TransferCreditTemplateCourse transferCreditTemplateCourse, String courseName, Integer courseNumber, CourseOptionality optionality, Double courseLength, EducationalTimeUnit courseLengthUnit, Subject subject) {
    EntityManager entityManager = getEntityManager();
    
    EducationalLength educationalLength = transferCreditTemplateCourse.getCourseLength();
    educationalLength.setUnits(courseLength);
    educationalLength.setUnit(courseLengthUnit);
    entityManager.persist(educationalLength);
    
    transferCreditTemplateCourse.setCourseName(courseName);
    transferCreditTemplateCourse.setCourseNumber(courseNumber);
    transferCreditTemplateCourse.setOptionality(optionality);
    transferCreditTemplateCourse.setSubject(subject);

    entityManager.persist(transferCreditTemplateCourse);
    
    return transferCreditTemplateCourse;
  }
  
  @SuppressWarnings("unchecked")
  public List<TransferCreditTemplateCourse> listTransferCreditTemplateCoursesByTemplate(TransferCreditTemplate template) {
    Session session = getHibernateSession();
    return session.createCriteria(TransferCreditTemplateCourse.class)
      .add(Restrictions.eq("transferCreditTemplate", template))
      .list();
  }
  
  public void deleteTransferCreditTemplate(TransferCreditTemplateCourse transferCreditTemplateCourse) {
    EntityManager entityManager = getEntityManager();
    entityManager.remove(transferCreditTemplateCourse);
  }

  public CourseAssessment findCourseAssessmentByCourseStudent(CourseStudent courseStudent) {
    Session s = getHibernateSession();
    
    return (CourseAssessment) s.createCriteria(CourseAssessment.class)
      .add(Restrictions.eq("courseStudent", courseStudent)).uniqueResult();
  }

  public CourseAssessment updateCourseAssessment(CourseAssessment assessment, User assessingUser, Grade grade, Date assessmentDate, String verbalAssessment) {
    EntityManager entityManager = getEntityManager();

    assessment.setAssessingUser(assessingUser);
    assessment.setGrade(grade);
    assessment.setDate(assessmentDate);
    assessment.setVerbalAssessment(verbalAssessment);
    
    entityManager.persist(assessment);
    
    return assessment;
  }
}
