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
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.domainmodel.base.School;

public class SchoolsAutoCompleteBinaryRequestController implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();

    String text = binaryRequestContext.getString("text");

    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<ul>");

    if (!StringUtils.isBlank(text)) {
      text = QueryParser.escape(StringUtils.trim(text)) + '*';
      
      List<School> schools = baseDAO.searchSchoolsBasic(100, 0, text).getResults();
      
      for (School school : schools) {
        addSchool(resultBuilder, school);
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
  
  private void addSchool(StringBuilder resultBuilder, School school) {
    resultBuilder
      .append("<li>")
      .append("<span>")
      .append(StringEscapeUtils.escapeHtml(school.getName()))
      .append("</span>")
      .append("<input type=\"hidden\" name=\"id\" value=\"")
      .append(school.getId())
      .append("\"/>")
      .append("</li>");
  }
}
