package fi.pyramus.views.students;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.views.PyramusViewController;

public class EditStudentViewController implements PyramusViewController, Breadcrumbable {

  public void process(PageRequestContext pageRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Long abstractStudentId = pageRequestContext.getLong("abstractStudent");
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);
    
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
    
    Map<Long, String> studentTags = new HashMap<Long, String>();
    
    for (Student student : students) {
      StringBuilder tagsBuilder = new StringBuilder();
      Iterator<Tag> tagIterator = student.getTags().iterator();
      while (tagIterator.hasNext()) {
        Tag tag = tagIterator.next();
        tagsBuilder.append(tag.getText());
        if (tagIterator.hasNext())
          tagsBuilder.append(' ');
      }
      
      studentTags.put(student.getId(), tagsBuilder.toString());
    }
    
    pageRequestContext.getRequest().setAttribute("tags", studentTags);
    pageRequestContext.getRequest().setAttribute("abstractStudent", abstractStudent);
    pageRequestContext.getRequest().setAttribute("students", students);
    pageRequestContext.getRequest().setAttribute("activityTypes", studentDAO.listStudentActivityTypes());
    pageRequestContext.getRequest().setAttribute("contactURLTypes", baseDAO.listContactURLTypes());
    pageRequestContext.getRequest().setAttribute("contactTypes", baseDAO.listContactTypes());
    pageRequestContext.getRequest().setAttribute("examinationTypes", studentDAO.listStudentExaminationTypes());
    pageRequestContext.getRequest().setAttribute("educationalLevels", studentDAO.listStudentEducationalLevels());
    pageRequestContext.getRequest().setAttribute("nationalities", baseDAO.listNationalities());
    pageRequestContext.getRequest().setAttribute("municipalities", baseDAO.listMunicipalities());
    pageRequestContext.getRequest().setAttribute("languages", baseDAO.listLanguages());
    pageRequestContext.getRequest().setAttribute("schools", baseDAO.listSchools());
    pageRequestContext.getRequest().setAttribute("studyProgrammes", baseDAO.listStudyProgrammes());
    pageRequestContext.getRequest().setAttribute("studyEndReasons", studentDAO.listTopLevelStudentStudyEndReasons());
    pageRequestContext.getRequest().setAttribute("variableKeys", studentDAO.listUserEditableStudentVariableKeys());
    
    pageRequestContext.setIncludeJSP("/templates/students/editstudent.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

  /**
   * Returns the localized name of this page. Used e.g. for breadcrumb navigation.
   * 
   * @param locale The locale to be used for the name
   * 
   * @return The localized name of this page
   */
  public String getName(Locale locale) {
    return Messages.getInstance().getText(locale, "students.editStudent.breadcrumb");
  }

}
