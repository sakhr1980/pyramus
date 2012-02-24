package fi.pyramus.views.students;

import java.util.ArrayList;
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
import fi.pyramus.dao.ProjectDAO;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.grading.CourseAssessment;
import fi.pyramus.domainmodel.grading.ProjectAssessment;
import fi.pyramus.domainmodel.grading.TransferCredit;
import fi.pyramus.domainmodel.projects.StudentProject;
import fi.pyramus.domainmodel.projects.StudentProjectModule;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.students.StudentContactLogEntry;
import fi.pyramus.domainmodel.students.StudentContactLogEntryComment;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.persistence.usertypes.CourseOptionality;
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
    ProjectDAO projectDAO = DAOFactory.getInstance().getProjectDAO();

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
         *  5. other
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
              o2.getStudyStartDate().compareTo(o1.getStudyStartDate()) : 0; 
        } else
          return o1class < o2class ? -1 : o1class == o2class ? 0 : 1;
      }
    });

    
    Map<Long, Boolean> studentHasImage = new HashMap<Long, Boolean>();
    Map<Long, List<CourseStudent>> courseStudents = new HashMap<Long, List<CourseStudent>>();
    Map<Long, List<StudentContactLogEntry>> contactEntries = new HashMap<Long, List<StudentContactLogEntry>>();
    Map<Long, List<TransferCredit>> transferCredits = new HashMap<Long, List<TransferCredit>>();
    Map<Long, List<CourseAssessment>> courseAssessments = new HashMap<Long, List<CourseAssessment>>();
    Map<Long, List<StudentGroup>> studentGroups = new HashMap<Long, List<StudentGroup>>();
    Map<Long, List<StudentProjectBean>> studentProjects = new HashMap<Long, List<StudentProjectBean>>();
    Map<Long, CourseAssessment> courseAssessmentsByCourseStudent = new HashMap<Long, CourseAssessment>();
    // StudentProject.id -> List of module beans
    Map<Long, List<StudentProjectModuleBean>> studentProjectModules = new HashMap<Long, List<StudentProjectModuleBean>>();
    final Map<Long, List<StudentContactLogEntryComment>> contactEntryComments = new HashMap<Long, List<StudentContactLogEntryComment>>();
    
    for (int i = 0; i < students.size(); i++) {
    	Student student = students.get(i);
    	
    	/**
    	 * Fetch courses this student is part of and sort the courses by course name
    	 */
    	
    	List<CourseStudent> courseStudentsByStudent = courseDAO.listCourseStudentsByStudent(student);
    	Map<Long, List<CourseStudent>> courseStudentsByModule = new HashMap<Long, List<CourseStudent>>();
      for (CourseStudent courseStudent : courseStudentsByStudent) {
        Long moduleId = courseStudent.getCourse().getModule().getId(); 

        List<CourseStudent> list = courseStudentsByModule.get(moduleId);
        if (list == null)
          list = new ArrayList<CourseStudent>();
        
        list.add(courseStudent);
        courseStudentsByModule.put(moduleId, list);
      }
    	
    	Collections.sort(courseStudentsByStudent, new Comparator<CourseStudent>() {
        private String getCourseAssessmentCompareStr(CourseStudent courseStudent) {
          String result = "";
          if (courseStudent != null)
            if (courseStudent.getCourse() != null)
              result = courseStudent.getCourse().getName();
            
          return result;
        }
        
        @Override
        public int compare(CourseStudent o1, CourseStudent o2) {
          String s1 = getCourseAssessmentCompareStr(o1);
          String s2 = getCourseAssessmentCompareStr(o2);
          
          return s1.compareToIgnoreCase(s2);
        }
      });

    	/**
    	 * Contact log entries
    	 */
    	
      List<StudentContactLogEntry> listStudentContactEntries = studentDAO.listStudentContactEntries(student);

      // Firstly populate comments
      
      for (int j = 0; j < listStudentContactEntries.size(); j++) {
        StudentContactLogEntry entry = listStudentContactEntries.get(j);
        
        List<StudentContactLogEntryComment> listComments = studentDAO.listStudentContactEntryComments(entry);
        
        Collections.sort(listComments, new Comparator<StudentContactLogEntryComment>() {
          public int compare(StudentContactLogEntryComment o1, StudentContactLogEntryComment o2) {
            Date d1 = o1.getCommentDate();
            Date d2 = o2.getCommentDate();
            
            int val = d1 == null ? 
                d2 == null ? 0 : 1 :
                  d2 == null ? -1 : d1.compareTo(d2);
            
            if (val == 0)
              return o1.getId().compareTo(o2.getId());
            else
              return val;
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
              if (d.before(comment.getCommentDate()))
                d = comment.getCommentDate();
            }
          }
          
          return d;
        }
        
        public int compare(StudentContactLogEntry o1, StudentContactLogEntry o2) {
          Date d1 = getDateForEntry(o1);
          Date d2 = getDateForEntry(o2);

          int val = d1 == null ? 
              d2 == null ? 0 : 1 :
                d2 == null ? -1 : d2.compareTo(d1);

          if (val == 0)
            return o2.getId().compareTo(o1.getId());
          else
            return val;
        }
      });


      /**
       * Students Course Assessments, sorted by course name
       */
      
      List<CourseAssessment> courseAssessmentsByStudent = gradingDAO.listCourseAssessmentsByStudent(student);

      for (CourseAssessment courseAssessment : courseAssessmentsByStudent) {
        Long courseStudentId = courseAssessment.getCourseStudent().getId(); 
        courseAssessmentsByCourseStudent.put(courseStudentId, courseAssessment);
      }
      
      Collections.sort(courseAssessmentsByStudent, new Comparator<CourseAssessment>() {
        private String getCourseAssessmentCompareStr(CourseAssessment courseAssessment) {
          String result = "";
          if (courseAssessment != null)
            if (courseAssessment.getCourseStudent() != null)
              if (courseAssessment.getCourseStudent().getCourse() != null)
                result = courseAssessment.getCourseStudent().getCourse().getName();
            
          return result;
        }
        
        @Override
        public int compare(CourseAssessment o1, CourseAssessment o2) {
          String s1 = getCourseAssessmentCompareStr(o1);
          String s2 = getCourseAssessmentCompareStr(o2);
          
          return s1.compareToIgnoreCase(s2);
        }
      });
      
      /**
       * Fetching and sorting of Transfer Credits 
       */
      
      List<TransferCredit> transferCreditsByStudent = gradingDAO.listTransferCreditsByStudent(student);
      Collections.sort(transferCreditsByStudent, new Comparator<TransferCredit>() {
        private String getCourseAssessmentCompareStr(TransferCredit tCredit) {
          String result = "";
         
          if (tCredit != null)
            result = tCredit.getCourseName();
           
          return result;
        }
        
        @Override
        public int compare(TransferCredit o1, TransferCredit o2) {
          String s1 = getCourseAssessmentCompareStr(o1);
          String s2 = getCourseAssessmentCompareStr(o2);
          
          return s1.compareToIgnoreCase(s2);
        }
      });

      /**
       * Project beans setup
       */
      List<StudentProject> studentsStudentProjects = projectDAO.listStudentsStudentProjects(student);
      List<StudentProjectBean> studentProjectBeans = new ArrayList<StudentProjectBean>();
      for (StudentProject studentProject : studentsStudentProjects) {
        int mandatoryModuleCount = 0;
        int optionalModuleCount = 0;
        int passedMandatoryModuleCount = 0;
        int passedOptionalModuleCount = 0;
        
        List<StudentProjectModuleBean> studentProjectModuleBeans = new ArrayList<StudentProjectModuleBean>();
        
        /**
         * Go through project modules to
         *  a) count mandatory/optional modules
         *  b) count mandatory/optional modules that have passing grade on them
         *  c) create beans to be passed to jsp
         */
        
        for (StudentProjectModule studentProjectModule : studentProject.getStudentProjectModules()) {
          boolean hasCourse = courseStudentsByModule.containsKey(studentProjectModule.getModule().getId());
          boolean hasPassingGrade = false;
          List<CourseStudent> courseStudentList = courseStudentsByModule.get(studentProjectModule.getModule().getId());
          List<TransferCredit> transferCreditList = new ArrayList<TransferCredit>();

          // Find out if there is a course that has passing grade for the module
          if (courseStudentList != null) {
            for (CourseStudent cs : courseStudentList) {
              CourseAssessment ca = courseAssessmentsByCourseStudent.get(cs.getId());
              if (ca != null && ca.getGrade() != null && ca.getGrade().getPassingGrade()) {
                hasPassingGrade = true; 
                break;
              }
            }
          } else
            courseStudentList = new ArrayList<CourseStudent>();
          
          if ((studentProjectModule.getModule().getCourseNumber() != null) && (studentProjectModule.getModule().getCourseNumber() != -1) && (studentProjectModule.getModule().getSubject() != null)) {
            for (TransferCredit tc : transferCreditsByStudent) {
              if ((tc.getCourseNumber() != null) && (tc.getCourseNumber() != -1) && (tc.getSubject() != null)) {
                if (tc.getCourseNumber().equals(studentProjectModule.getModule().getCourseNumber()) && tc.getSubject().equals(studentProjectModule.getModule().getSubject())) {
                  transferCreditList.add(tc);
                  if (tc.getGrade() != null && tc.getGrade().getPassingGrade())
                    hasPassingGrade = true;
                }
              }
            }
          }
          
          if (studentProjectModule.getOptionality() == CourseOptionality.MANDATORY) {
            mandatoryModuleCount++;
            if (hasPassingGrade)
              passedMandatoryModuleCount++;
          } else if (studentProjectModule.getOptionality() == CourseOptionality.OPTIONAL) {
            optionalModuleCount++;
            if (hasPassingGrade)
              passedOptionalModuleCount++;
          }
          
          StudentProjectModuleBean moduleBean = new StudentProjectModuleBean(studentProjectModule, hasCourse, hasPassingGrade, courseStudentList, transferCreditList);
          studentProjectModuleBeans.add(moduleBean);
        }

        // Add ModuleBeans to response
        studentProjectModules.put(studentProject.getId(), studentProjectModuleBeans);
        
        List<ProjectAssessment> projectAssessments = gradingDAO.listProjectAssessmentByProject(studentProject);
        Collections.sort(projectAssessments, new Comparator<ProjectAssessment>() {
          @Override
          public int compare(ProjectAssessment o1, ProjectAssessment o2) {
            return o2.getDate().compareTo(o1.getDate());
          }
        });

        StudentProjectBean bean = new StudentProjectBean(studentProject, mandatoryModuleCount, optionalModuleCount, passedMandatoryModuleCount, passedOptionalModuleCount, projectAssessments);
        studentProjectBeans.add(bean);
      }
      
      // Student Image
      studentHasImage.put(student.getId(), studentDAO.findStudentHasImage(student));

      courseStudents.put(student.getId(), courseStudentsByStudent);
      courseAssessments.put(student.getId(), courseAssessmentsByStudent);
      contactEntries.put(student.getId(), listStudentContactEntries);
      transferCredits.put(student.getId(), transferCreditsByStudent);
      studentGroups.put(student.getId(), studentDAO.listStudentsStudentGroups(student));
      studentProjects.put(student.getId(), studentProjectBeans);
    }
    
    pageRequestContext.getRequest().setAttribute("students", students);
    pageRequestContext.getRequest().setAttribute("courses", courseStudents);
    pageRequestContext.getRequest().setAttribute("contactEntries", contactEntries);
    pageRequestContext.getRequest().setAttribute("contactEntryComments", contactEntryComments);
    pageRequestContext.getRequest().setAttribute("transferCredits", transferCredits);
    pageRequestContext.getRequest().setAttribute("courseAssessments", courseAssessments);
    pageRequestContext.getRequest().setAttribute("studentGroups", studentGroups);
    pageRequestContext.getRequest().setAttribute("studentProjects", studentProjects);
    pageRequestContext.getRequest().setAttribute("studentProjectModules", studentProjectModules);
    pageRequestContext.getRequest().setAttribute("courseAssessmentsByCourseStudent", courseAssessmentsByCourseStudent);
    pageRequestContext.getRequest().setAttribute("studentHasImage", studentHasImage);

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
    return Messages.getInstance().getText(locale, "students.viewStudent.breadcrumb");
  }

  
  public class StudentProjectBean {
    private final StudentProject studentProject;
    private final int passedOptionalModuleCount;
    private final int mandatoryModuleCount;
    private final int optionalModuleCount;
    private final int passedMandatoryModuleCount;
    private final List<ProjectAssessment> assessments;

    public StudentProjectBean(StudentProject studentProject, int mandatoryModuleCount, int optionalModuleCount,
        int passedMandatoryModuleCount, int passedOptionalModuleCount, List<ProjectAssessment> assessments) {
      this.studentProject = studentProject;
      this.mandatoryModuleCount = mandatoryModuleCount;
      this.optionalModuleCount = optionalModuleCount;
      this.passedOptionalModuleCount = passedOptionalModuleCount;
      this.passedMandatoryModuleCount = passedMandatoryModuleCount;
      this.assessments = assessments;
    }

    public StudentProject getStudentProject() {
      return studentProject;
    }

    public int getPassedOptionalModuleCount() {
      return passedOptionalModuleCount;
    }

    public int getMandatoryModuleCount() {
      return mandatoryModuleCount;
    }

    public int getOptionalModuleCount() {
      return optionalModuleCount;
    }

    public int getPassedMandatoryModuleCount() {
      return passedMandatoryModuleCount;
    }

    public List<ProjectAssessment> getAssessments() {
      return assessments;
    }
  }
  
  public class StudentProjectModuleBean {
    private final StudentProjectModule studentProjectModule;
    private final boolean hasCourse;
    private final boolean hasPassingGrade;
    private final List<CourseStudent> courseStudents;
    private final List<TransferCredit> transferCredits;

    public StudentProjectModuleBean(StudentProjectModule studentProjectModule, boolean hasCourse, boolean hasPassingGrade, 
        List<CourseStudent> courseStudents, List<TransferCredit> transferCredits) {
      this.studentProjectModule = studentProjectModule;
      this.hasCourse = hasCourse;
      this.hasPassingGrade = hasPassingGrade;
      this.courseStudents = courseStudents;
      this.transferCredits = transferCredits;
    }

    public StudentProjectModule getStudentProjectModule() {
      return studentProjectModule;
    }

    public boolean isHasCourse() {
      return hasCourse;
    }

    public boolean isHasPassingGrade() {
      return hasPassingGrade;
    }

    public List<CourseStudent> getCourseStudents() {
      return courseStudents;
    }

    public List<TransferCredit> getTransferCredits() {
      return transferCredits;
    }
  }
}
