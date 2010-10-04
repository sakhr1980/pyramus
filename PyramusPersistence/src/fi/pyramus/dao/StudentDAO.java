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
import fi.pyramus.persistence.usertypes.ArchiveFilter;
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
  public SearchResult<AbstractStudent> searchAbstractStudentsBasic(int resultsPerPage, int page, String queryText, boolean escapeSpecialChars) {

    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();
    if (!StringUtils.isBlank(queryText)) {
      queryBuilder.append("+(");
      addTokenizedSearchCriteria(queryBuilder, "unarchivedFirstNames", queryText, false, escapeSpecialChars);
      addTokenizedSearchCriteria(queryBuilder, "unarchivedNicknames", queryText, false, escapeSpecialChars);
      addTokenizedSearchCriteria(queryBuilder, "unarchivedLastNames", queryText, false, escapeSpecialChars);
      addTokenizedSearchCriteria(queryBuilder, "unarchivedEmails", queryText, false, escapeSpecialChars);
      queryBuilder.append(")");
    }

    queryBuilder.append(" +students.archived:false");

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
      if (!escapeSpecialChars) {
        return searchAbstractStudentsBasic(resultsPerPage, page, queryText, true);
      } else {
        throw new PersistenceException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<AbstractStudent> searchAbstractStudents(int resultsPerPage, int page, String firstName, String lastName, String nickname,
      String education, String email, Sex sex, String ssn, String addressCity, String addressCountry, String addressPostalCode, String addressStreetAddress,
      String phone, Boolean lodging, StudyProgramme studyProgramme, Language language, Nationality nationality, Municipality municipality,
      ArchiveFilter archiveFilter, boolean escapeSpecialChars) {

    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (sex != null)
      addTokenizedSearchCriteria(queryBuilder, "sex", sex.toString(), true, escapeSpecialChars);
    if (!StringUtils.isBlank(ssn))
      addTokenizedSearchCriteria(queryBuilder, "socialSecurityNumber", ssn, true, escapeSpecialChars);

    switch (archiveFilter) {
      case INCLUDEARCHIVED:
        if (!StringUtils.isBlank(firstName))
          addTokenizedSearchCriteria(queryBuilder, "archivedFirstNames", "unarchivedFirstNames", firstName, true, escapeSpecialChars);
        if (!StringUtils.isBlank(lastName))
          addTokenizedSearchCriteria(queryBuilder, "archivedLastNames", "unarchivedLastNames", lastName, true, escapeSpecialChars);
        if (!StringUtils.isBlank(nickname))
          addTokenizedSearchCriteria(queryBuilder, "archivedNicknames", "unarchivedNicknames", nickname, true, escapeSpecialChars);
        if (!StringUtils.isBlank(education))
          addTokenizedSearchCriteria(queryBuilder, "archivedEducations", "unarchivedEducations", education, true, escapeSpecialChars);
        if (!StringUtils.isBlank(email))
          addTokenizedSearchCriteria(queryBuilder, "archivedEmails", "unarchivedEmails", email, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressCity))
          addTokenizedSearchCriteria(queryBuilder, "archivedCities", "unarchivedCities", addressCity, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressCountry))
          addTokenizedSearchCriteria(queryBuilder, "archivedCountries", "unarchivedCountries", addressCountry, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressPostalCode))
          addTokenizedSearchCriteria(queryBuilder, "archivedPostalCodes", "unarchivedPostalCodes", addressPostalCode, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressStreetAddress))
          addTokenizedSearchCriteria(queryBuilder, "archivedStreetAddresses", "unarchivedStreetAddresses", addressStreetAddress, true, escapeSpecialChars);
        if (!StringUtils.isBlank(phone))
          addTokenizedSearchCriteria(queryBuilder, "archivedPhones", "unarchivedPhones", phone, true, escapeSpecialChars);
        if (studyProgramme != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedStudyProgrammeIds", "unarchivedStudyProgrammeIds", studyProgramme.getId().toString(), true,
              escapeSpecialChars);
        if (nationality != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedNationalityIds", "unarchivedNationalityIds", nationality.getId().toString(), true,
              escapeSpecialChars);
        if (municipality != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedMunicipalityIds", "unarchivedMunicipalityIds", municipality.getId().toString(), true,
              escapeSpecialChars);
        if (language != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedLanguageIds", "unarchivedLanguageIds", language.getId().toString(), true, escapeSpecialChars);
        if (lodging != null) {
          addTokenizedSearchCriteria(queryBuilder, "archivedLodgings", "unarchivedLodgings", lodging.toString(), true, escapeSpecialChars);
        }

      break;
      case ONLYARCHIVED:
        if (!StringUtils.isBlank(firstName))
          addTokenizedSearchCriteria(queryBuilder, "archivedFirstNames", firstName, true, escapeSpecialChars);
        if (!StringUtils.isBlank(lastName))
          addTokenizedSearchCriteria(queryBuilder, "archivedLastNames", lastName, true, escapeSpecialChars);
        if (!StringUtils.isBlank(nickname))
          addTokenizedSearchCriteria(queryBuilder, "archivedNicknames", nickname, true, escapeSpecialChars);
        if (!StringUtils.isBlank(education))
          addTokenizedSearchCriteria(queryBuilder, "archivedEducations", education, true, escapeSpecialChars);
        if (!StringUtils.isBlank(email))
          addTokenizedSearchCriteria(queryBuilder, "archivedEmails", email, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressCity))
          addTokenizedSearchCriteria(queryBuilder, "archivedCities", addressCity, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressCountry))
          addTokenizedSearchCriteria(queryBuilder, "archivedCountries", addressCountry, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressPostalCode))
          addTokenizedSearchCriteria(queryBuilder, "archivedPostalCodes", addressPostalCode, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressStreetAddress))
          addTokenizedSearchCriteria(queryBuilder, "archivedStreetAddresses", addressStreetAddress, true, escapeSpecialChars);
        if (!StringUtils.isBlank(phone))
          addTokenizedSearchCriteria(queryBuilder, "archivedPhones", phone, true, escapeSpecialChars);
        if (studyProgramme != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedStudyProgrammeIds", studyProgramme.getId().toString(), true, escapeSpecialChars);
        if (nationality != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedNationalityIds", nationality.getId().toString(), true, escapeSpecialChars);
        if (municipality != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedMunicipalityIds", municipality.getId().toString(), true, escapeSpecialChars);
        if (language != null)
          addTokenizedSearchCriteria(queryBuilder, "archivedLanguageIds", language.getId().toString(), true, escapeSpecialChars);
        if (lodging != null) {
          addTokenizedSearchCriteria(queryBuilder, "archivedLodgings", lodging.toString(), true, escapeSpecialChars);
        }
      break;
      case SKIPARCHIVED:
        if (!StringUtils.isBlank(firstName))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedFirstNames", firstName, true, escapeSpecialChars);
        if (!StringUtils.isBlank(lastName))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedLastNames", lastName, true, escapeSpecialChars);
        if (!StringUtils.isBlank(nickname))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedNicknames", nickname, true, escapeSpecialChars);
        if (!StringUtils.isBlank(education))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedEducations", education, true, escapeSpecialChars);
        if (!StringUtils.isBlank(email))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedEmails", email, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressCity))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedCities", addressCity, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressCountry))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedCountries", addressCountry, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressPostalCode))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedPostalCodes", addressPostalCode, true, escapeSpecialChars);
        if (!StringUtils.isBlank(addressStreetAddress))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedStreetAddresses", addressStreetAddress, true, escapeSpecialChars);
        if (!StringUtils.isBlank(phone))
          addTokenizedSearchCriteria(queryBuilder, "unarchivedPhones", phone, true, escapeSpecialChars);
        if (studyProgramme != null)
          addTokenizedSearchCriteria(queryBuilder, "unarchivedStudyProgrammeIds", studyProgramme.getId().toString(), true, escapeSpecialChars);
        if (nationality != null)
          addTokenizedSearchCriteria(queryBuilder, "unarchivedNationalityIds", nationality.getId().toString(), true, escapeSpecialChars);
        if (municipality != null)
          addTokenizedSearchCriteria(queryBuilder, "unarchivedMunicipalityIds", municipality.getId().toString(), true, escapeSpecialChars);
        if (language != null)
          addTokenizedSearchCriteria(queryBuilder, "unarchivedLanguageIds", language.getId().toString(), true, escapeSpecialChars);
        if (lodging != null) {
          addTokenizedSearchCriteria(queryBuilder, "unarchivedLodgings", lodging.toString(), true, escapeSpecialChars);
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
      if (!escapeSpecialChars) {
        return searchAbstractStudents(resultsPerPage, page, firstName, lastName, nickname, education, email, sex, ssn, addressCity, addressCountry,
            addressPostalCode, addressStreetAddress, phone, lodging, studyProgramme, language, nationality, municipality, archiveFilter, true);
      } else {
        throw new PersistenceException(e);
      }
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
  public SearchResult<StudentGroup> searchStudentGroups(int resultsPerPage, int page, String text,
      boolean filterArchived, boolean escapeSpecialChars) {
    int firstResult = page * resultsPerPage;

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(text)) {
      queryBuilder.append(escapeSpecialChars ? QueryParser.escape(text) : text);
      queryBuilder.append(" name: ").append(escapeSpecialChars ? QueryParser.escape(text) : text);
      queryBuilder.append(" description: ").append(escapeSpecialChars ? QueryParser.escape(text) : text);
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

      if (filterArchived) {
        query.enableFullTextFilter("ArchivedStudentGroup").setParameter("archived", Boolean.FALSE);
      }

      int hits = query.getResultSize();
      int pages = hits / resultsPerPage;
      if (hits % resultsPerPage > 0) {
        pages++;
      }

      int lastResult = Math.min(firstResult + resultsPerPage, hits) - 1;

      return new SearchResult<StudentGroup>(page, pages, hits, firstResult, lastResult, query.list());
    }
    catch (ParseException e) {
      if (!escapeSpecialChars) {
        return searchStudentGroups(resultsPerPage, page, text, filterArchived, true);
      }
      else {
        throw new PersistenceException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public SearchResult<StudentGroup> searchStudentGroups(int resultsPerPage, int page, String name, 
      String description, User user, Date timeframeStart, Date timeframeEnd, boolean filterArchived, 
      boolean escapeSpecialChars) {
    int firstResult = page * resultsPerPage;

    String timeframeS = null;
    if (timeframeStart != null)
      timeframeS = getSearchFormattedDate(timeframeStart);

    String timeframeE = null;
    if (timeframeEnd != null)
      timeframeE = getSearchFormattedDate(timeframeEnd);

    StringBuilder queryBuilder = new StringBuilder();

    if (!StringUtils.isBlank(name)) {
      addTokenizedSearchCriteria(queryBuilder, "name", name, true, escapeSpecialChars);
    }

    if (!StringUtils.isBlank(description)) {
      addTokenizedSearchCriteria(queryBuilder, "description", description, true, escapeSpecialChars);
    }
    
    if (user != null) {
      addTokenizedSearchCriteria(queryBuilder, "users.user.id", user.getId().toString(), true, escapeSpecialChars);
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
      if (!escapeSpecialChars) {
        return searchStudentGroups(resultsPerPage, page, name, description, user, 
            timeframeStart, timeframeEnd, filterArchived, true);
      }
      else {
        throw new PersistenceException(e);
      }
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
}