package fi.pyramus.views.students;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.pyramus.PageRequestContext;
import fi.pyramus.UserRole;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.views.PyramusViewController;

public class SearchStudentsDialogViewController implements PyramusViewController {

  public void process(PageRequestContext requestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    List<StudyProgramme> studyProgrammes = baseDAO.listStudyProgrammes();
    
    Collections.sort(studyProgrammes, new Comparator<StudyProgramme>() {
      @Override
      public int compare(StudyProgramme sp1, StudyProgramme sp2) {
        String name1 = sp1.getName();
        String name2 = sp2.getName();
        return name1.compareToIgnoreCase(name2);
      }
    });
    
    requestContext.getRequest().setAttribute("studyProgrammes", studyProgrammes);
    requestContext.setIncludeJSP("/templates/students/searchstudentsdialog.jsp");
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.GUEST, UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
}
