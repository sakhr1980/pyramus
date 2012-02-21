package fi.pyramus.binary.students;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;

import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.students.StudentGroupDAO;
import fi.pyramus.domainmodel.students.StudentGroup;
import fi.pyramus.framework.BinaryRequestController;
import fi.pyramus.framework.UserRole;

public class StudentGroupsAutoCompleteBinaryRequestController extends BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    StudentGroupDAO studentGroupDAO = DAOFactory.getInstance().getStudentGroupDAO();
    String text = binaryRequestContext.getString("text");
    
    
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<ul>");

    if (!StringUtils.isBlank(text)) {
      text = QueryParser.escape(StringUtils.trim(text)) + '*';

      List<StudentGroup> studentGroups = studentGroupDAO.searchStudentGroupsBasic(100, 0, text).getResults();

      for (StudentGroup studentGroup : studentGroups) {
        addResult(resultBuilder, studentGroup);
      }
    }
    
    resultBuilder.append("</ul>");

    try {
      binaryRequestContext.setResponseContent(resultBuilder.toString().getBytes("UTF-8"), "text/html;charset=UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new SmvcRuntimeException(e);
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
