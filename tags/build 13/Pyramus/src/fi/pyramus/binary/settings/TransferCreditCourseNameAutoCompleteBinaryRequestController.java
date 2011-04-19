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
      .append("</span>");
    
    addHiddenValue(resultBuilder, "id", course.getId());
    addHiddenValue(resultBuilder, "subjectId", course.getSubject() != null ? course.getSubject().getId() : null);
    addHiddenValue(resultBuilder, "subjectName", course.getSubject() != null ? course.getSubject().getName() : null);
    addHiddenValue(resultBuilder, "courseLength", course.getCourseLength() != null ? course.getCourseLength().getUnits() : null);
    addHiddenValue(resultBuilder, "courseLengthUnitId", course.getCourseLength() != null ? course.getCourseLength().getUnit().getId() : null);
    addHiddenValue(resultBuilder, "courseLengthUnitName", course.getCourseLength() != null ? course.getCourseLength().getUnit().getName() : null);
    addHiddenValue(resultBuilder, "courseNumber", course.getCourseNumber());
    
    resultBuilder.append("</li>");
  }
  
  private void addHiddenValue(StringBuilder resultBuilder, String name, Object value) {
    String stringValue = value == null ? null : String.valueOf(value);
    
    resultBuilder.append("<input type=\"hidden\" name=\"");
    resultBuilder.append(name);
    resultBuilder.append("\" value=\"");
    
    if (!StringUtils.isBlank(stringValue)) {
      resultBuilder.append(StringEscapeUtils.escapeHtml(stringValue));
    } 
    
    resultBuilder.append("\"/>");
  }
}
