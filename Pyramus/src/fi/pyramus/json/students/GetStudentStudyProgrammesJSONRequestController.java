package fi.pyramus.json.students;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.pyramus.JSONRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.UserRole;
import fi.pyramus.json.JSONRequestController;

/**
 * JSON request controller to view student info.
 * 
 * @author antti.viljakainen
 */
public class GetStudentStudyProgrammesJSONRequestController implements JSONRequestController {
  
  /**
   * Processes JSON request
   * 
   * In parameters
   * - studentId - student id to retrieve information for
   * 
   * Page parameters
   * - student - Map including
   * * id - Student id
   * * firstname - First name
   * * lastname - Last name
   * 
   * @param requestContext JSON request context
   */
  public void process(JSONRequestContext requestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();

    Long abstractStudentId = NumberUtils.createLong(requestContext.getRequest().getParameter("abstractStudentId"));
    AbstractStudent abstractStudent = studentDAO.getAbstractStudent(abstractStudentId);
    List<Student> students = abstractStudent.getStudents();

    List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
    
    for (int i = 0; i < students.size(); i++) {
    	Student student = students.get(i);
    	
      if (student != null) {
    		if (student.getStudyProgramme() != null) {
          Map<String, Object> studentInfo = new HashMap<String, Object>();
          studentInfo.put("studyProgrammeId", student.getStudyProgramme().getId());
          if (!student.getArchived())
          	studentInfo.put("studyProgrammeName", student.getStudyProgramme().getName());
          else
          	studentInfo.put("studyProgrammeName", student.getStudyProgramme().getName() + " *");
          studentInfo.put("studentId", student.getId());

          result.add(studentInfo);
    		} else {
          Map<String, Object> studentInfo = new HashMap<String, Object>();
          studentInfo.put("studyProgrammeId", new Long(-1));
          if (!student.getArchived())
            studentInfo.put("studyProgrammeName", Messages.getInstance().getText(requestContext.getRequest().getLocale(), "students.editStudent.noStudyProgrammeDropDownItemLabel"));
          else
          	studentInfo.put("studyProgrammeName", Messages.getInstance().getText(requestContext.getRequest().getLocale(),"students.editStudent.noStudyProgrammeDropDownItemLabel") + " *");
          studentInfo.put("studentId", student.getId());

          result.add(studentInfo);
    		}
    	}
    }
    
    requestContext.addResponseParameter("studentStudyProgrammes", result);
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }

}
