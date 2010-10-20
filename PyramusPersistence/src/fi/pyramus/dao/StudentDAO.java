package fi.pyramus.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentActivityType;
import fi.pyramus.domainmodel.students.StudentContactLogEntry;
import fi.pyramus.domainmodel.students.StudentEducationalLevel;
import fi.pyramus.domainmodel.students.StudentExaminationType;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.domainmodel.students.StudentGroupStudent;
import fi.pyramus.domainmodel.students.StudentGroupUser;
import fi.pyramus.domainmodel.students.StudentStudyEndReason;
import fi.pyramus.domainmodel.students.StudentVariable;
import fi.pyramus.domainmodel.students.StudentVariableKey;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.persistence.search.SearchResult;
import fi.pyramus.persistence.search.StudentFilter;
import fi.pyramus.persistence.usertypes.Sex;
import fi.pyramus.persistence.usertypes.StudentContactLogEntryType;

/**
 * The Data Access Object for student related operations.
 */
public class StudentDAO extends PyramusDAO {

  /**
   * Archives a student.
   * 
   * @param student
   *          The student to be archived
   */
  public void archiveStudent(Student student) {
    Session s = getHibernateSession();
    student.setArchived(Boolean.TRUE);
    s.saveOrUpdate(student);
    
    // Also archive course students of the archived student
    
    List<CourseStudent> courseStudents = listCourseStudents(student);
    if (courseStudents.size() > 0) {
      CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
      for (CourseStudent courseStudent : courseStudents) {
        courseDAO.archiveCourseStudent(courseStudent);
      }
    }

    // This is necessary because AbstractStudent entity does not really
    // change in this operation but it still needs to be reindexed

    forceReindex(student.getAbstractStudent());
  }

  /**
   * Unarchives a student.
   * 
   * @param student
   *          The student to be unarchived
   */
  public void unarchiveStudent(Student student) {
    Session s = getHibernateSession();
    student.setArchived(Boolean.FALSE);
    s.saveOrUpdate(student);

    // This is necessary because AbstractStudent entity does not really
    // change in this operation but it still needs to be reindexed

    forceReindex(student.getAbstractStudent());
  }

  /**
   * Creates new AbstractStudent instance and saves it to database
   * 
   * @param birthday
   *          student's birthday
   * @param socialSecurityNumber
   *          student's social security number
   * @param sex
   *          student's sex
   * @return new instance of AbstractStudent
   */
  public AbstractStudent createAbstractStudent(Date birthday, String socialSecurityNumber, Sex sex, String basicInfo) {
    EntityManager entityManager = getEntityManager();

    AbstractStudent abstractStudent = new AbstractStudent();
    abstractStudent.setBirthday(birthday);
    abstractStudent.setSocialSecurityNumber(socialSecurityNumber);
    abstractStudent.setSex(sex);
    abstractStudent.setBasicInfo(basicInfo);
    entityManager.persist(abstractStudent);

    return abstractStudent;
  }

  public Student createStudent(AbstractStudent abstractStudent, String firstName, String lastName, String nickname, String additionalInfo,
      Date studyTimeEnd, StudentActivityType activityType, StudentExaminationType examinationType, StudentEducationalLevel educationalLevel, String education,
      Nationality nationality, Municipality municipality, Language language, School school, StudyProgramme studyProgramme, Double previousStudies,
      Date studyStartDate, Date studyEndDate, StudentStudyEndReason studyEndReason, String studyEndText, Boolean lodging) {

    EntityManager entityManager = getEntityManager();

    Student student = new Student();
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setNickname(nickname);
    student.setAdditionalInfo(additionalInfo);
    student.setStudyTimeEnd(studyTimeEnd);
    student.setActivityType(activityType);
    student.setExaminationType(examinationType);
    student.setEducationalLevel(educationalLevel);
    student.setEducation(education);
    student.setNationality(nationality);
    student.setMunicipality(municipality);
    student.setLanguage(language);
    student.setSchool(school);
    student.setStudyProgramme(studyProgramme);
    student.setPreviousStudies(previousStudies);
    student.setStudyStartDate(studyStartDate);
    student.setStudyEndDate(studyEndDate);
    student.setStudyEndReason(studyEndReason);
    student.setStudyEndText(studyEndText);
    student.setLodging(lodging);

    entityManager.persist(student);

    abstractStudent.addStudent(student);

    entityManager.persist(abstractStudent);

    return student;
  }

  public Student setStudentTags(Student student, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    student.setTags(tags);
    
    entityManager.persist(student);
    
    return student;
  }

  /**
   * Retrieves AbstractStudent from database by id
   * 
   * @param abstractStudentId
   *          AbstractStudent's id
   * @return AbstractStudent
   */
  public AbstractStudent getAbstractStudent(Long abstractStudentId) {
    Session s = getHibernateSession();
    return (AbstractStudent) s.load(AbstractStudent.class, abstractStudentId);
  }

  /**
   * Returns an abstract student with the given social security number.
   * 
   * @param ssn
   *          The social security number
   * 
   * @return An abstract student with the given social security number
   */
  public AbstractStudent getAbstractStudentBySSN(String ssn) {
    Session s = getHibernateSession();
    return (AbstractStudent) s.createCriteria(AbstractStudent.class).add(Restrictions.eq("socialSecurityNumber", ssn)).uniqueResult();
  }

  /**
   * Deletes AbstractStudent from database
   * 
   * @param abstractStudent
   *          AbstractStudent instance
   */
  public void deleteAbstractStudent(AbstractStudent abstractStudent) {
    Session s = getHibernateSession();
    s.delete(abstractStudent);
  }

  /**
   * Retrieves Student from database by id
   * 
   * @param studentId
   *          Student's id
   * @return Student
   */
  public Student getStudent(Long studentId) {
    Session s = getHibernateSession();
    return (Student) s.load(Student.class, studentId);
  }

  /**
   * Deletes Student from database
   * 
   * @param student
   *          Student instance
   */
  public void deleteStudent(Student student) {
    EntityManager entityManager = getEntityManager();

    AbstractStudent abstractStudent = student.getAbstractStudent();
    abstractStudent.removeStudent(student);
    entityManager.persist(abstractStudent);

    entityManager.remove(student);
  }

  /**
   * Lists all students in database
   * 
   * @return list of all students
   */
  @SuppressWarnings("unchecked")
  public List<Student> listStudents() {
    Session s = getHibernateSession();
    return s.createCriteria(Student.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<Student> listStudentsByAbstractStudent(AbstractStudent abstractStudent) {
    Session s = getHibernateSession();
    return s.createCriteria(Student.class)
        .add(Restrictions.eq("archived", Boolean.FALSE))
        .add(Restrictions.eq("abstractStudent", abstractStudent)).list();
  }

  public StudentActivityType getStudentActivityType(Long id) {
    Session s = getHibernateSession();
    return (StudentActivityType) s.load(StudentActivityType.class, id);
  }

  public StudentExaminationType getStudentExaminationType(Long id) {
    Session s = getHibernateSession();
    return (StudentExaminationType) s.load(StudentExaminationType.class, id);
  }

  public StudentEducationalLevel getStudentEducationalLevel(Long id) {
    Session s = getHibernateSession();
    return (StudentEducationalLevel) s.load(StudentEducationalLevel.class, id);
  }

  private StudentVariable createStudentVariable(Student student, StudentVariableKey key, String value) {
    Session s = getHibernateSession();

    StudentVariable studentVariable = new StudentVariable();
    studentVariable.setStudent(student);
    studentVariable.setKey(key);
    studentVariable.setValue(value);
    s.saveOrUpdate(studentVariable);

    student.getVariables().add(studentVariable);
    s.saveOrUpdate(student);

    return studentVariable;
  }

  private void updateStudentVariable(StudentVariable studentVariable, String value) {
    Session s = getHibernateSession();
    studentVariable.setValue(value);
    s.saveOrUpdate(studentVariable);
  }

  private void deleteStudentVariable(StudentVariable studentVariable) {
    Session s = getHibernateSession();
    s.delete(studentVariable);
  }

  /**
   * Lists all contact log entries for given student.
   * 
   * @param student
   *          Student to list contact entries for.
   * @return List with StudentContactLogEntry items belonging to the student.
   */
  @SuppressWarnings("unchecked")
  public List<StudentContactLogEntry> listStudentContactEntries(Student student) {
    Session s = getHibernateSession();

    return s.createCriteria(StudentContactLogEntry.class).
      add(Restrictions.eq("student", student)).
      add(Restrictions.eq("archived", Boolean.FALSE)).
      list();
  }

  /**
   * Returns a list of the course students of the given student.
   * 
   * @param student The student
   * 
   * @return A list of the course students of the given student
   */
  @SuppressWarnings("unchecked")
  public List<CourseStudent> listCourseStudents(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(CourseStudent.class).add(Restrictions.eq("student", student)).add(
        Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<StudentActivityType> listStudentActivityTypes() {
    Session s = getHibernateSession();
    return s.createCriteria(StudentActivityType.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<StudentExaminationType> listStudentExaminationTypes() {
    Session s = getHibernateSession();
    return s.createCriteria(StudentExaminationType.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  @SuppressWarnings("unchecked")
  public List<StudentEducationalLevel> listStudentEducationalLevels() {
    Session s = getHibernateSession();
    return s.createCriteria(StudentEducationalLevel.class).add(Restrictions.eq("archived", Boolean.FALSE)).list();
  }

  public void updateAbstractStudent(AbstractStudent abstractStudent, Date birthday, String socialSecurityNumber, Sex sex, String basicInfo) {
    Session s = getHibernateSession();
    abstractStudent.setBirthday(birthday);
    abstractStudent.setSocialSecurityNumber(socialSecurityNumber);
    abstractStudent.setSex(sex);
    abstractStudent.setBasicInfo(basicInfo);
    s.saveOrUpdate(abstractStudent);
  }

  public void updateStudent(Student student, String firstName, String lastName, String nickname, String additionalInfo,
      Date studyTimeEnd, StudentActivityType activityType, StudentExaminationType examinationType, StudentEducationalLevel educationalLevel, String education,
      Nationality nationality, Municipality municipality, Language language, School school, StudyProgramme studyProgramme, Double previousStudies,
      Date studyStartDate, Date studyEndDate, StudentStudyEndReason studyEndReason, String studyEndText, Boolean lodging) {
    Session s = getHibernateSession();

    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setNickname(nickname);
    student.setAdditionalInfo(additionalInfo);
    student.setStudyTimeEnd(studyTimeEnd);
    student.setActivityType(activityType);
    student.setExaminationType(examinationType);
    student.setEducationalLevel(educationalLevel);
    student.setEducation(education);
    student.setNationality(nationality);
    student.setMunicipality(municipality);
    student.setLanguage(language);
    student.setSchool(school);
    student.setStudyProgramme(studyProgramme);
    student.setPreviousStudies(previousStudies);
    student.setStudyStartDate(studyStartDate);
    student.setStudyEndDate(studyEndDate);
    student.setStudyEndReason(studyEndReason);
    student.setStudyEndText(studyEndText);
    student.setLodging(lodging);

    s.saveOrUpdate(student);
  }

  @SuppressWarnings("unchecked")
  public SearchResult<AbstractStudent> searchAbstractStudentsBasic(int resultsPerPage, int page, String queryText) {

    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();
    if (!StringUtils.isBlank(queryText)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "activeFirstNames", queryText, false);
      addTokenizedSearchCriteria(queryBuilder, "activeNicknames", queryText, false);
      addTokenizedSearchCriteria(queryBuilder, "activeLastNames", queryText, false);
      addTokenizedSearchCriteria(queryBuilder, "activeEmails", queryText, false);
      addTokenizedSearchCriteria(queryBuilder, "activeTags", queryText, false);
      queryBuilder.append(")");
    }
   
    addTokenizedSearchCriteria(queryBuilder, "active", "true", true);
    
    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      String queryString = queryBuilder.toString();

      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      Query luceneQuery = parser.parse(queryString);
    
      FullTextQuery query = fullTextSession
          .createFullTextQuery(luceneQuery, AbstractStudent.class)
          .setSort(
              new Sort(new SortField[] { SortField.FIELD_SCORE, new SortField("lastNameSortable", SortField.STRING),
                  new SortField("firstNameSortable", SortField.STRING) })).setFirstResult(firstResult).setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<AbstractStudent>(page, pages, hits, firstResult, lastResult, query.list());
    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<AbstractStudent> searchAbstractStudents(int resultsPerPage, int page, String firstName, String lastName, String nickname, String tags, 
      String education, String email, Sex sex, String ssn, String addressCity, String addressCountry, String addressPostalCode, String addressStreetAddress,
      String phone, Boolean lodging, StudyProgramme studyProgramme, Language language, Nationality nationality, Municipality municipality,
      StudentFilter studentFilter) {

    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (sex != null)
      addTokenizedSearchCriteria(queryBuilder, "sex", sex.toString(), true);
    if (!StringUtils.isBlank(ssn))
      addTokenizedSearchCriteria(queryBuilder, "socialSecurityNumber", ssn, true);

    switch (studentFilter) {
      case INCLUDE_INACTIVE:

        // Search should find past students as well, so an abstract student is considered
        // a match if it contains at least one non-archived student, no matter whether
        // their study end date has been set or not
        
        queryBuilder.append("+(");
        addTokenizedSearchCriteria(queryBuilder, "active", "true", false);
        addTokenizedSearchCriteria(queryBuilder, "inactive", "true", false);
        queryBuilder.append(")");
        
        // Other search terms
        
        if (!StringUtils.isBlank(firstName))
          addTokenizedSearchCriteria(queryBuilder, "inactiveFirstNames", "activeFirstNames", firstName, true);
        if (!StringUtils.isBlank(lastName))
          addTokenizedSearchCriteria(queryBuilder, "inactiveLastNames", "activeLastNames", lastName, true);
        if (!StringUtils.isBlank(nickname))
          addTokenizedSearchCriteria(queryBuilder, "inactiveNicknames", "activeNicknames", nickname, true);
        if (!StringUtils.isBlank(tags))
          addTokenizedSearchCriteria(queryBuilder, "inactiveTags", "activeTags", tags, true);
        if (!StringUtils.isBlank(education))
          addTokenizedSearchCriteria(queryBuilder, "inactiveEducations", "activeEducations", education, true);
        if (!StringUtils.isBlank(email))
          addTokenizedSearchCriteria(queryBuilder, "inactiveEmails", "activeEmails", email, true);
        if (!StringUtils.isBlank(addressCity))
          addTokenizedSearchCriteria(queryBuilder, "inactiveCities", "activeCities", addressCity, true);
        if (!StringUtils.isBlank(addressCountry))
          addTokenizedSearchCriteria(queryBuilder, "inactiveCountries", "activeCountries", addressCountry, true);
        if (!StringUtils.isBlank(addressPostalCode))
          addTokenizedSearchCriteria(queryBuilder, "inactivePostalCodes", "activePostalCodes", addressPostalCode, true);
        if (!StringUtils.isBlank(addressStreetAddress))
          addTokenizedSearchCriteria(queryBuilder, "inactiveStreetAddresses", "activeStreetAddresses", addressStreetAddress, true);
        if (!StringUtils.isBlank(phone))
          addTokenizedSearchCriteria(queryBuilder, "inactivePhones", "activePhones", phone, true);
        if (studyProgramme != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveStudyProgrammeIds", "activeStudyProgrammeIds", studyProgramme.getId().toString(), true);
        if (nationality != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveNationalityIds", "activeNationalityIds", nationality.getId().toString(), true);
        if (municipality != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveMunicipalityIds", "activeMunicipalityIds", municipality.getId().toString(), true);
        if (language != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveLanguageIds", "activeLanguageIds", language.getId().toString(), true);
        if (lodging != null) {
          addTokenizedSearchCriteria(queryBuilder, "inactiveLodgings", "activeLodgings", lodging.toString(), true);
        }

      break;
      case ONLY_INACTIVE:

        // Search should only find past students, so an abstract student is considered
        // a match if it only contains non-archived students who have their study end date
        // set and that date is in the past
        
        addTokenizedSearchCriteria(queryBuilder, "active", "false", true);
        addTokenizedSearchCriteria(queryBuilder, "inactive", "true", true);
        
        // Other search terms
        
        if (!StringUtils.isBlank(firstName))
          addTokenizedSearchCriteria(queryBuilder, "inactiveFirstNames", firstName, true);
        if (!StringUtils.isBlank(lastName))
          addTokenizedSearchCriteria(queryBuilder, "inactiveLastNames", lastName, true);
        if (!StringUtils.isBlank(nickname))
          addTokenizedSearchCriteria(queryBuilder, "inactiveNicknames", nickname, true);
        if (!StringUtils.isBlank(tags))
          addTokenizedSearchCriteria(queryBuilder, "inactiveTags", tags, true);
        if (!StringUtils.isBlank(education))
          addTokenizedSearchCriteria(queryBuilder, "inactiveEducations", education, true);
        if (!StringUtils.isBlank(email))
          addTokenizedSearchCriteria(queryBuilder, "inactiveEmails", email, true);
        if (!StringUtils.isBlank(addressCity))
          addTokenizedSearchCriteria(queryBuilder, "inactiveCities", addressCity, true);
        if (!StringUtils.isBlank(addressCountry))
          addTokenizedSearchCriteria(queryBuilder, "inactiveCountries", addressCountry, true);
        if (!StringUtils.isBlank(addressPostalCode))
          addTokenizedSearchCriteria(queryBuilder, "inactivePostalCodes", addressPostalCode, true);
        if (!StringUtils.isBlank(addressStreetAddress))
          addTokenizedSearchCriteria(queryBuilder, "inactiveStreetAddresses", addressStreetAddress, true);
        if (!StringUtils.isBlank(phone))
          addTokenizedSearchCriteria(queryBuilder, "inactivePhones", phone, true);
        if (studyProgramme != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveStudyProgrammeIds", studyProgramme.getId().toString(), true);
        if (nationality != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveNationalityIds", nationality.getId().toString(), true);
        if (municipality != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveMunicipalityIds", municipality.getId().toString(), true);
        if (language != null)
          addTokenizedSearchCriteria(queryBuilder, "inactiveLanguageIds", language.getId().toString(), true);
        if (lodging != null) {
          addTokenizedSearchCriteria(queryBuilder, "inactiveLodgings", lodging.toString(), true);
        }
      break;
      case SKIP_INACTIVE:
        
        // Search should skip past students, so an abstract student is considered a match
        // if it contains at least one non-archived student who hasn't got his study end
        // date set or the date is in the future

        addTokenizedSearchCriteria(queryBuilder, "active", "true", true);
        
        // Other search terms
        
        if (!StringUtils.isBlank(firstName))
          addTokenizedSearchCriteria(queryBuilder, "activeFirstNames", firstName, true);
        if (!StringUtils.isBlank(lastName))
          addTokenizedSearchCriteria(queryBuilder, "activeLastNames", lastName, true);
        if (!StringUtils.isBlank(nickname))
          addTokenizedSearchCriteria(queryBuilder, "activeNicknames", nickname, true);
        if (!StringUtils.isBlank(education))
          addTokenizedSearchCriteria(queryBuilder, "activeEducations", education, true);
        if (!StringUtils.isBlank(tags))
          addTokenizedSearchCriteria(queryBuilder, "activeTags", tags, true);
        if (!StringUtils.isBlank(email))
          addTokenizedSearchCriteria(queryBuilder, "activeEmails", email, true);
        if (!StringUtils.isBlank(addressCity))
          addTokenizedSearchCriteria(queryBuilder, "activeCities", addressCity, true);
        if (!StringUtils.isBlank(addressCountry))
          addTokenizedSearchCriteria(queryBuilder, "activeCountries", addressCountry, true);
        if (!StringUtils.isBlank(addressPostalCode))
          addTokenizedSearchCriteria(queryBuilder, "activePostalCodes", addressPostalCode, true);
        if (!StringUtils.isBlank(addressStreetAddress))
          addTokenizedSearchCriteria(queryBuilder, "activeStreetAddresses", addressStreetAddress, true);
        if (!StringUtils.isBlank(phone))
          addTokenizedSearchCriteria(queryBuilder, "activePhones", phone, true);
        if (studyProgramme != null)
          addTokenizedSearchCriteria(queryBuilder, "activeStudyProgrammeIds", studyProgramme.getId().toString(), true);
        if (nationality != null)
          addTokenizedSearchCriteria(queryBuilder, "activeNationalityIds", nationality.getId().toString(), true);
        if (municipality != null)
          addTokenizedSearchCriteria(queryBuilder, "activeMunicipalityIds", municipality.getId().toString(), true);
        if (language != null)
          addTokenizedSearchCriteria(queryBuilder, "activeLanguageIds", language.getId().toString(), true);
        if (lodging != null) {
          addTokenizedSearchCriteria(queryBuilder, "activeLodgings", lodging.toString(), true);
        }
      break;
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

      FullTextQuery query = fullTextSession
          .createFullTextQuery(luceneQuery, AbstractStudent.class)
          .setFirstResult(firstResult)
          .setSort(
              new Sort(new SortField[] { SortField.FIELD_SCORE, new SortField("lastNameSortable", SortField.STRING),
                  new SortField("firstNameSortable", SortField.STRING) })).setMaxResults(resultsPerPage);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<AbstractStudent>(page, pages, hits, firstResult, lastResult, query.list());
    } catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  /**
   * Creates new contact log entry for a student.
   * 
   * @param student
   *          Student
   * @param type
   *          Type for the new entry
   * @param text
   *          Text for then new entry
   * @param entryDate
   *          Entry date for the new entry
   * @param creator
   *          Creator for the new entry
   * @return The new entry
   */
  public StudentContactLogEntry createStudentContactLogEntry(Student student, StudentContactLogEntryType type, String text, Date entryDate, String creator) {
    Session s = getHibernateSession();

    StudentContactLogEntry entry = new StudentContactLogEntry();
    entry.setStudent(student);
    entry.setCreatorName(creator);
    entry.setEntryDate(entryDate);
    entry.setText(text);
    entry.setType(type);

    s.save(entry);
    return entry;
  }

  private StudentVariable getStudentVariable(Student student, StudentVariableKey key) {
    Session s = getHibernateSession();
    StudentVariable studentVariable = (StudentVariable) s.createCriteria(StudentVariable.class).add(Restrictions.eq("student", student))
        .add(Restrictions.eq("key", key)).uniqueResult();
    return studentVariable;
  }

  private StudentVariableKey getStudentVariableKey(String key) {
    Session s = getHibernateSession();
    StudentVariableKey studentVariableKey = (StudentVariableKey) s.createCriteria(StudentVariableKey.class).add(Restrictions.eq("variableKey", key))
        .uniqueResult();
    return studentVariableKey;
  }

  public String getStudentVariable(Student student, String key) {
    StudentVariableKey studentVariableKey = getStudentVariableKey(key);
    if (studentVariableKey != null) {
      StudentVariable studentVariable = getStudentVariable(student, studentVariableKey);
      return studentVariable == null ? null : studentVariable.getValue();
    } else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }

  public void setStudentSchool(Student student, School school) {
    Session s = getHibernateSession();
    student.setSchool(school);
    s.saveOrUpdate(student);
  }

  public void setStudentVariable(Student student, String key, String value) {
    StudentVariableKey studentVariableKey = getStudentVariableKey(key);
    if (studentVariableKey != null) {
      StudentVariable studentVariable = getStudentVariable(student, studentVariableKey);
      if (StringUtils.isBlank(value)) {
        if (studentVariable != null) {
          deleteStudentVariable(studentVariable);
        }
      } else {
        if (studentVariable == null) {
          studentVariable = createStudentVariable(student, studentVariableKey, value);
        } else {
          updateStudentVariable(studentVariable, value);
        }
      }
    } else {
      throw new PersistenceException("Unknown VariableKey");
    }
  }

  @SuppressWarnings("unchecked")
  public List<Student> listStudentsByStudentVariable(String key, String value) {
    Session s = getHibernateSession();

    StudentVariableKey studentVariableKey = getStudentVariableKey(key);
    return (List<Student>) s.createCriteria(StudentVariable.class).add(Restrictions.eq("key", studentVariableKey)).add(Restrictions.eq("value", value))
        .setProjection(Projections.property("student")).list();
  }

  public StudentStudyEndReason getStudentStudyEndReason(Long studyEndReasonId) {
    Session s = getHibernateSession();

    return (StudentStudyEndReason) s.load(StudentStudyEndReason.class, studyEndReasonId);
  }

  /**
   * Returns a list of all student variable keys from the database, sorted by their user interface name.
   * 
   * @return A list of student school variable keys
   */
  @SuppressWarnings("unchecked")
  public List<StudentVariableKey> listStudentVariableKeys() {
    Session s = getHibernateSession();

    List<StudentVariableKey> studentVariableKeys = s.createCriteria(StudentVariableKey.class).list();

    Collections.sort(studentVariableKeys, new Comparator<StudentVariableKey>() {
      public int compare(StudentVariableKey o1, StudentVariableKey o2) {
        return o1.getVariableName() == null ? -1 : o2.getVariableName() == null ? 1 : o1.getVariableName().compareTo(o2.getVariableName());
      }
    });

    return studentVariableKeys;
  }

  @SuppressWarnings("unchecked")
  public List<StudentStudyEndReason> listStudentStudyEndReasons() {
    Session s = getHibernateSession();
    return s.createCriteria(StudentStudyEndReason.class).list();
  }

  /**
   * Returns a list of user editable student variable keys from the database, sorted by their user interface name.
   * 
   * @return A list of user editable student variable keys
   */
  @SuppressWarnings("unchecked")
  public List<StudentVariableKey> listUserEditableStudentVariableKeys() {
    Session s = getHibernateSession();

    List<StudentVariableKey> studentVariableKeys = s.createCriteria(StudentVariableKey.class).add(Restrictions.eq("userEditable", Boolean.TRUE)).list();

    Collections.sort(studentVariableKeys, new Comparator<StudentVariableKey>() {
      public int compare(StudentVariableKey o1, StudentVariableKey o2) {
        return o1.getVariableName() == null ? -1 : o2.getVariableName() == null ? 1 : o1.getVariableName().compareTo(o2.getVariableName());
      }
    });

    return studentVariableKeys;
  }

  @SuppressWarnings("unchecked")
  public List<StudentStudyEndReason> listTopLevelStudentStudyEndReasons() {
    Session s = getHibernateSession();
    return s.createCriteria(StudentStudyEndReason.class).add(Restrictions.isNull("parentReason")).list();
  }

  /**
   * StudentGroup methods
   */
  
  public StudentGroup createStudentGroup(String name, String description, Date beginDate, User creatingUser) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date(System.currentTimeMillis());
    
    StudentGroup studentGroup = new StudentGroup();
    studentGroup.setName(name);
    studentGroup.setDescription(description);
    studentGroup.setBeginDate(beginDate);

    studentGroup.setCreator(creatingUser);
    studentGroup.setCreated(now);
    studentGroup.setLastModifier(creatingUser);
    studentGroup.setLastModified(now);

    entityManager.persist(studentGroup);

    return studentGroup;
  }
  
  public StudentGroup setStudentGroupTags(StudentGroup studentGroup, Set<Tag> tags) {
    EntityManager entityManager = getEntityManager();
    
    studentGroup.setTags(tags);
    
    entityManager.persist(studentGroup);
    
    return studentGroup;
  }

  public StudentGroup findStudentGroupById(Long studentGroupId) {
    Session s = getHibernateSession();
    return (StudentGroup) s.load(StudentGroup.class, studentGroupId);
  }

  @SuppressWarnings("unchecked")
  public SearchResult<StudentGroup> searchStudentGroupsBasic(int resultsPerPage, int page, String text) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();
    if (!StringUtils.isBlank(text)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "name", text, false);
      addTokenizedSearchCriteria(queryBuilder, "tags.text", text, false);
      addTokenizedSearchCriteria(queryBuilder, "description", text, false);
      queryBuilder.append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "name", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, StudentGroup.class)
          .setSort(new Sort(new SortField[] { SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      query.enableFullTextFilter("ArchivedStudentGroup").setParameter("archived", Boolean.FALSE);


      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<StudentGroup>(page, pages, hits, firstResult, lastResult, query.list());
    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<StudentGroup> searchStudentGroups(int resultsPerPage, int page, String name, 
      String tags, String description, User user, Date timeframeStart, Date timeframeEnd, 
      boolean filterArchived) {
    int firstResult = page * resultsPerPage;

    String timeframeS = null;
    if (timeframeStart != null)
      timeframeS = getSearchFormattedDate(timeframeStart);

    String timeframeE = null;
    if (timeframeEnd != null)
      timeframeE = getSearchFormattedDate(timeframeEnd);

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(name)) {
      addTokenizedSearchCriteria(queryBuilder, "name", name, true);
    }

    if (!StringUtils.isBlank(tags)) {
      addTokenizedSearchCriteria(queryBuilder, "tags.text", tags, true);
    }
    
    if (!StringUtils.isBlank(description)) {
      addTokenizedSearchCriteria(queryBuilder, "description", description, true);
    }
    
    if (user != null) {
      addTokenizedSearchCriteria(queryBuilder, "users.user.id", user.getId().toString(), true);
    }
    
    if ((timeframeS != null) && (timeframeE != null)) {
      /**
       * (beginDate between timeframeStart - timeframeEnd or endDate between timeframeStart -
       * timeframeEnd) or (startDate less than timeframeStart and endDate more than
       * timeframeEnd)
       **/
      queryBuilder.append(" +(").append("(").append("beginDate:[").append(timeframeS).append(" TO ").append(
          timeframeE).append("]").append(")").append(")");
    }
    else if (timeframeS != null) {
      /** beginDate > timeframeStart **/
      queryBuilder.append(" +(").append("beginDate:[").append(timeframeS).append(" TO ").append(
          getSearchDateInfinityHigh()).append("]").append(")");
    }
    else if (timeframeE != null) {
      /** beginDate < timeframeEnd **/
      queryBuilder.append(" +(").append("beginDate:[").append(getSearchDateInfinityLow()).append(" TO ").append(
          timeframeE).append("]").append(")");
    }

    Session s = getHibernateSession();
    FullTextSession fullTextSession = Search.getFullTextSession(s);

    try {
      QueryParser parser = new QueryParser(Version.LUCENE_29, "", new StandardAnalyzer(Version.LUCENE_29));
      String queryString = queryBuilder.toString();
      Query luceneQuery;

      if (StringUtils.isBlank(queryString)) {
        luceneQuery = new MatchAllDocsQuery();
      }
      else {
        luceneQuery = parser.parse(queryString);
      }

      FullTextQuery query = fullTextSession.createFullTextQuery(luceneQuery, StudentGroup.class)
          .setSort(new Sort(new SortField[]{SortField.FIELD_SCORE, new SortField("nameSortable", SortField.STRING)}))
          .setFirstResult(firstResult)
          .setMaxResults(resultsPerPage);

      if (filterArchived)
        query.enableFullTextFilter("ArchivedStudentGroup").setParameter("archived", Boolean.FALSE);

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0)
        pages++;

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<StudentGroup>(page, pages, hits, firstResult, lastResult, query.list());
    }
    catch (ParseException e) {
      throw new PersistenceException(e);
    }
  }

  public void updateStudentGroup(StudentGroup studentGroup, String name, String description, Date beginDate, User updatingUser) {
    Session s = getHibernateSession();

    studentGroup.setName(name);
    studentGroup.setDescription(description);
    studentGroup.setBeginDate(beginDate);

    studentGroup.setLastModified(new Date(System.currentTimeMillis()));
    studentGroup.setLastModifier(updatingUser);

    s.saveOrUpdate(studentGroup);
  }
  
  public StudentGroupStudent addStudentGroupStudent(StudentGroup studentGroup, Student student, User updatingUser) {
    Session s = getHibernateSession();
    StudentGroupStudent sgs = new StudentGroupStudent();
    sgs.setStudent(student);
    s.save(sgs);
    
    studentGroup.addStudent(sgs);

    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));

    s.saveOrUpdate(studentGroup);
    
    return sgs;
  }

  public void removeStudentGroupStudent(StudentGroup studentGroup, StudentGroupStudent student, User updatingUser) {
    Session s = getHibernateSession();
    studentGroup.removeStudent(student);

    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));

    s.saveOrUpdate(studentGroup);
    s.delete(student);
  }

  public StudentGroupUser addStudentGroupUser(StudentGroup studentGroup, User user, User updatingUser) {
    Session s = getHibernateSession();
    StudentGroupUser sgu = new StudentGroupUser();
    sgu.setUser(user);
    s.save(sgu);

    studentGroup.addUser(sgu);

    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));

    s.saveOrUpdate(studentGroup);
    
    return sgu;
  }

  public void removeStudentGroupUser(StudentGroup studentGroup, StudentGroupUser user, User updatingUser) {
    Session s = getHibernateSession();
    studentGroup.removeUser(user);

    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));

    s.saveOrUpdate(studentGroup);
    s.delete(user);
  }

  public void archiveStudentGroup(StudentGroup studentGroup, User updatingUser) {
    Session s = getHibernateSession();
    studentGroup.setArchived(Boolean.TRUE);

    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));

    s.saveOrUpdate(studentGroup);
  }

  public void unarchiveStudentGroup(StudentGroup studentGroup, User updatingUser) {
    Session s = getHibernateSession();
    studentGroup.setArchived(Boolean.FALSE);

    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));

    s.saveOrUpdate(studentGroup);
  }
  
  public StudentGroupStudent findStudentGroupStudentById(Long studentGroupStudentId) {
    Session s = getHibernateSession();
    return (StudentGroupStudent) s.load(StudentGroupStudent.class, studentGroupStudentId);
  }

  public void updateStudentGroupStudent(StudentGroupStudent studentGroupStudent, Student student, User updatingUser) {
    Session s = getHibernateSession();
    StudentGroup studentGroup = studentGroupStudent.getStudentGroup();
    studentGroup.setLastModifier(updatingUser);
    studentGroup.setLastModified(new Date(System.currentTimeMillis()));
    s.saveOrUpdate(studentGroup);
    
    studentGroupStudent.setStudent(student);
    s.saveOrUpdate(studentGroupStudent);
  }

  @SuppressWarnings("unchecked")
  public List<StudentGroup> listStudentsStudentGroups(Student student) {
    Session s = getHibernateSession();
    return s.createCriteria(StudentGroupStudent.class).add(Restrictions.eq("student", student)).setProjection(Projections.property("studentGroup")).list();
  }
  
  public StudentContactLogEntry findStudentContactLogEntryById(Long entryId) {
    Session s = getHibernateSession();
    return (StudentContactLogEntry) s.load(StudentContactLogEntry.class, entryId);
  }
  
  public void updateStudentContactLogEntry(StudentContactLogEntry entry, StudentContactLogEntryType type, 
      String text, Date entryDate, String creator) {
    Session s = getHibernateSession();

    entry.setType(type);
    entry.setText(text);
    entry.setEntryDate(entryDate);
    entry.setCreatorName(creator);
    s.saveOrUpdate(entry);
  }

  public void archiveContactEntry(StudentContactLogEntry entry) {
    Session s = getHibernateSession();

    entry.setArchived(Boolean.TRUE);
    s.saveOrUpdate(entry);
  }

  public void unarchiveContactEntry(StudentContactLogEntry entry) {
    Session s = getHibernateSession();

    entry.setArchived(Boolean.FALSE);
    s.saveOrUpdate(entry);
  }

  public void archiveStudentActivityType(StudentActivityType studentActivityType) {
    Session s = getHibernateSession();
    studentActivityType.setArchived(Boolean.TRUE);
    s.saveOrUpdate(studentActivityType);
  }

  public void unarchiveStudentActivityType(StudentActivityType studentActivityType) {
    Session s = getHibernateSession();
    studentActivityType.setArchived(Boolean.FALSE);
    s.saveOrUpdate(studentActivityType);
  }

  public void archiveStudentEducationalLevel(StudentEducationalLevel studentEducationalLevel) {
    Session s = getHibernateSession();
    studentEducationalLevel.setArchived(Boolean.TRUE);
    s.saveOrUpdate(studentEducationalLevel);
  }

  public void unarchiveStudentEducationalLevel(StudentEducationalLevel studentEducationalLevel) {
    Session s = getHibernateSession();
    studentEducationalLevel.setArchived(Boolean.FALSE);
    s.saveOrUpdate(studentEducationalLevel);
  }
  
  public void archiveStudentExaminationType(StudentExaminationType studentExaminationType) {
    Session s = getHibernateSession();
    studentExaminationType.setArchived(Boolean.TRUE);
    s.saveOrUpdate(studentExaminationType);
  }

  public void unarchiveStudentExaminationType(StudentExaminationType studentExaminationType) {
    Session s = getHibernateSession();
    studentExaminationType.setArchived(Boolean.FALSE);
    s.saveOrUpdate(studentExaminationType);
  }

}