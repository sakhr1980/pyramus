package fi.pyramus.binary.settings;

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
import fi.pyramus.dao.GradingDAO;
import fi.pyramus.domainmodel.grading.TransferCreditTemplateCourse;

public class TransferCreditCourseNameAutoCompleteBinaryRequestController implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    GradingDAO gradingDAO = DAOFactory.getInstance().getGradingDAO();

    String text = binaryRequestContext.getString("text");

    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<ul>");

    if (!StringUtils.isBlank(text)) {
      text = QueryParser.escape(StringUtils.trim(text)) + '*';

      List<TransferCreditTemplateCourse> results = gradingDAO.searchTransferCreditTemplateCoursesBasic(100, 0, text).getResults();
      
      for (TransferCreditTemplateCourse course : results) {
        addResultItem(resultBuilder, course);
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
  
  private void addResultItem(StringBuilder resultBuilder, TransferCreditTemplateCourse course) {
    resultBuilder
      .append("<li>")
      .append("<span>")
      .append(StringEscapeUtils.escapeHtml(course.getCourseName()))
      .append("</span>")

      .append("<input type=\"hidden\" name=\"id\" value=\"")
      .append(course.getId())
      .append("\"/>")

      .append("<input type=\"hidden\" name=\"subjectId\" value=\"")
      .append(course.getSubject().getId())
      .append("\"/>")

      .append("<input type=\"hidden\" name=\"subjectName\" value=\"")
      .append(course.getSubject().getName())
      .append("\"/>")

      .append("<input type=\"hidden\" name=\"courseLength\" value=\"")
      .append(course.getCourseLength().getUnits())
      .append("\"/>")
    
      .append("<input type=\"hidden\" name=\"courseLengthUnitId\" value=\"")
      .append(course.getCourseLength().getUnit().getId())
      .append("\"/>")

      .append("<input type=\"hidden\" name=\"courseLengthUnitName\" value=\"")
      .append(course.getCourseLength().getUnit().getName())
      .append("\"/>")

      .append("<input type=\"hidden\" name=\"courseNumber\" value=\"")
      .append(course.getCourseNumber())
      .append("\"/>")
      
      .append("</li>");
  }
}
