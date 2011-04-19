package fi.pyramus.binary.students;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;

import fi.pyramus.BinaryRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.students.StudentGroup;

public class StudentGroupsAutoCompleteBinaryRequestController implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    StudentDAO studentDAO = DAOFactory.getInstance().getStudentDAO();
    String text = binaryRequestContext.getString("text");
    
    
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<ul>");

    if (!StringUtils.isBlank(text)) {
      text = QueryParser.escape(StringUtils.trim(text)) + '*';

      List<StudentGroup> studentGroups = studentDAO.searchStudentGroupsBasic(100, 0, text).getResults();

      for (StudentGroup studentGroup : studentGroups) {
        addResult(resultBuilder, studentGroup);
      }
    }
    
    resultBuilder.append("</ul>");

    try {
      binaryRequestContext.setResponseContent(resultBuilder.toString().getBytes("UTF-8"), "text/html;charset=UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new PyramusRuntimeException(e);
    }
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
  private void addResult(StringBuilder resultBuilder, StudentGroup studentGroup) {
    resultBuilder
      .append("<li>")
      .append("<span>")
      .append(StringEscapeUtils.escapeHtml(studentGroup.getName()))
      .append("</span>")
      .append("<input type=\"hidden\" name=\"id\" value=\"")
      .append(studentGroup.getId())
      .append("\"/>")
      .append("</li>");
  }
}
