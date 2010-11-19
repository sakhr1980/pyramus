package fi.pyramus.views.students;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.CourseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentContactLogEntry;
import fi.pyramus.domainmodel.students.StudentContactLogEntryComment;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.views.PyramusViewController;

/**
 * ViewController for editing student information.
 * 
 * @author antti.viljakainen
 */
public class ViewStudentViewController implements PyramusViewController, Breadcrumbable {

  /**
   * Returns allowed roles for this page. Allowed are UserRole.MANAGER and UserRole.ADMINISTRATOR.
   * 
   * @return allowed roles
   */
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Processes the page request.
   * 
   * In parameters
   * - student
   * - abstractStudent
   * 
   * Page parameters
   * - student - Student object
   * - nationalities - List of Nationality objects
   * - municipalities - List of Municipality objects
   * - languages - List of Language objects
   * - studentCourses - List of CourseStudent objects
   * - studentContactEntries - List of StudentContactLogEntry objects
   * 
   * @param pageRequestContext pageRequestContext
   */
  public void process(PageRequestContext pageRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    CourseDAO courseDAO = DAOFactory.getInstance().getCourseDAO();
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    Long abstractStudentId = pageRequestContext.getLong("abstractStudent");
    
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);
    
    pageRequestContext.getRequest().setAttribute("abstractStudent", abstractStudent);

    List<Student> students = studentDAO.listStudentsByAbstractStudent(abstractStudent);
    Collections.sort(students, new Comparator<Student>() {
      @Override
      public int compare(Student o1, Student o2) {
        /**
         * Ordering study programmes as follows
         *  1. studies that have start date but no end date (ongoing)
         *  2. studies that have no start nor end date
         *  3. studies that have ended
         *  4. studies that are archived
         */
        
        int o1class =
          (o1.getArchived()) ? 4:
            (o1.getStudyStartDate() != null && o1.getStudyEndDate() == null) ? 1:
              (o1.getStudyStartDate() == null && o1.getStudyEndDate() == null) ? 2:
                (o1.getStudyEndDate() != null) ? 3:
                  5;
        int o2class =
          (o2.getArchived()) ? 4:
            (o2.getStudyStartDate() != null && o2.getStudyEndDate() == null) ? 1:
              (o2.getStudyStartDate() == null && o2.getStudyEndDate() == null) ? 2:
                (o2.getStudyEndDate() != null) ? 3:
                  5;

        if (o1class == o2class) {
          // classes are the same, we try to do last comparison from the start dates
          return ((o1.getStudyStartDate() != null) && (o2.getStudyStartDate() != null)) ? 
              o1.getStudyStartDate().compareTo(o2.getStudyStartDate()) : 0; 
        } else
          return o1class < o2class ? -1 : o1class == o2class ? 0 : 1;
      }
    });

    
    Map<Long, List<CourseStudent>> courseStudents = new HashMap<Long, List<CourseStudent>>();
    Map<Long, List<StudentContactLogEntry>> contactEntries = new HashMap<Long, List<StudentContactLogEntry>>();
    Map<Long, List<TransferCredit>> transferCredits = new HashMap<Long, List<TransferCredit>>();
    Map<Long, List<CourseAssessment>> courseAssesments = new HashMap<Long, List<CourseAssessment>>();
    Map<Long, List<StudentGroup>> studentGroups = new HashMap<Long, List<StudentGroup>>();
    final Map<Long, List<StudentContactLogEntryComment>> contactEntryComments = new HashMap<Long, List<StudentContactLogEntryComment>>();
    
    for (int i = 0; i < students.size(); i++) {
    	Student student = students.get(i);
    	
      courseStudents.put(student.getId(), courseDAO.listStudentCourses(student));

      List<StudentContactLogEntry> listStudentContactEntries = studentDAO.listStudentContactEntries(student);

      // Firstly populate comments
      
      for (int j = 0; j < listStudentContactEntries.size(); j++) {
        StudentContactLogEntry entry = listStudentContactEntries.get(j);
        
        List<StudentContactLogEntryComment> listComments = studentDAO.listStudentContactEntryComments(entry);
        
        Collections.sort(listComments, new Comparator<StudentContactLogEntryComment>() {
          public int compare(StudentContactLogEntryComment o1, StudentContactLogEntryComment o2) {
            Date d1 = o1.getCommentDate();
            Date d2 = o2.getCommentDate();
            
            return d1 == null ? 
                d2 == null ? 0 : 1 :
                  d2 == null ? -1 : d1.compareTo(d2);
          }
        });
        
        contactEntryComments.put(entry.getId(), listComments);
      }

      // And then sort the entries by latest date of entry or its comments
      
      Collections.sort(listStudentContactEntries, new Comparator<StudentContactLogEntry>() {
        private Date getDateForEntry(StudentContactLogEntry entry) {
          Date d = entry.getEntryDate();

          List<StudentContactLogEntryComment> comments = contactEntryComments.get(entry.getId());
          
          for (int i = 0; i < comments.size(); i++) {
            StudentContactLogEntryComment comment = comments.get(i);
            
            if (d == null) {
              d = comment.getCommentDate();
            } else {
              if (d.compareTo(comment.getCommentDate()) < 0)
                d = comment.getCommentDate();
            }
          }
          
          return d;
        }
        
        public int compare(StudentContactLogEntry o1, StudentContactLogEntry o2) {
          Date d1 = getDateForEntry(o1);
          Date d2 = getDateForEntry(o2);

          return d1 == null ? 
              d2 == null ? 0 : 1 :
                d2 == null ? -1 : d2.compareTo(d1);
        }
      });
      
      contactEntries.put(student.getId(), listStudentContactEntries);
      transferCredits.put(student.getId(), gradingDAO.listStudentsTransferCredits(student));
      courseAssesments.put(student.getId(), gradingDAO.listStudentsCourseAssessments(student));
      studentGroups.put(student.getId(), studentDAO.listStudentsStudentGroups(student));
    }
    
    pageRequestContext.getRequest().setAttribute("students", students);
    pageRequestContext.getRequest().setAttribute("courses", courseStudents);
    pageRequestContext.getRequest().setAttribute("contactEntries", contactEntries);
    pageRequestContext.getRequest().setAttribute("contactEntryComments", contactEntryComments);
    pageRequestContext.getRequest().setAttribute("transferCredits", transferCredits);
    pageRequestContext.getRequest().setAttribute("courseAssesments", courseAssesments);
    pageRequestContext.getRequest().setAttribute("studentGroups", studentGroups);

    pageRequestContext.setIncludeJSP("/templates/students/viewstudent.jsp");
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "students.viewStudent.pageTitle");
  }

}

