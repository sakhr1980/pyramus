package fi.pyramus.views.settings;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.I18N.Messages;
import fi.pyramus.breadcrumbs.Breadcrumbable;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.base.SchoolDAO;
import fi.pyramus.dao.base.SchoolVariableKeyDAO;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.SchoolVariableKey;
import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.framework.PyramusViewController;
import fi.pyramus.framework.UserRole;
import fi.pyramus.util.JSONArrayExtractor;
import fi.pyramus.util.StringAttributeComparator;

/**
 * The controller responsible of the Edit School view of the application.
 * 
 * @see fi.pyramus.json.settings.EditSchoolJSONRequestController
 */
public class ViewSchoolViewController extends PyramusViewController implements Breadcrumbable {

  /**
   * Processes the page request by including the corresponding JSP page to the response.
   * 
   * @param pageRequestContext Page request context
   */
  public void process(PageRequestContext pageRequestContext) {
    SchoolDAO schoolDAO = DAOFactory.getInstance().getSchoolDAO();
    SchoolVariableKeyDAO schoolVariableKeyDAO = DAOFactory.getInstance().getSchoolVariableKeyDAO();

    Long schoolId = NumberUtils.createLong(pageRequestContext.getRequest().getParameter("school"));
    School school = schoolDAO.findById(schoolId);

    List<SchoolVariableKey> schoolUserEditableVariableKeys = schoolVariableKeyDAO.listUserEditableVariableKeys();
    Collections.sort(schoolUserEditableVariableKeys, new StringAttributeComparator("getVariableName"));
    
    StringBuilder tagsBuilder = new StringBuilder();
    Iterator<Tag> tagIterator = school.getTags().iterator();
    while (tagIterator.hasNext()) {
      Tag tag = tagIterator.next();
      tagsBuilder.append(tag.getText());
      if (tagIterator.hasNext())
        tagsBuilder.append(' ');
    }
    
    List<Address> addresses = school.getContactInfo().getAddresses();
    JSONArray jaAddresses = new JSONArrayExtractor("id",
                                                   "name",
                                                   "streetAddress",
                                                   "postalCode",
                                                   "city",
                                                   "country").extract(addresses);
    for (int i=0; i<jaAddresses.size(); i++) {
      JSONObject joAddress = jaAddresses.getJSONObject(i);
      if (addresses.get(i).getContactType() != null) {
        joAddress.put("contactTypeName", addresses.get(i).getContactType().getName());
      }
    }
    
    List<Email> emails = school.getContactInfo().getEmails();
    JSONArray jaEmails = new JSONArrayExtractor("id", "defaultAddress", "address").extract(emails);
    for (int i=0; i<jaEmails.size(); i++) {
      JSONObject joEmail = jaEmails.getJSONObject(i);
      if (emails.get(i).getContactType() != null) {
        joEmail.put("contactTypeName", emails.get(i).getContactType().getName());
      }
    }
    
    List<PhoneNumber> phoneNumbers = school.getContactInfo().getPhoneNumbers();
    JSONArray jaPhoneNumbers = new JSONArrayExtractor("id", "defaultNumber", "number").extract(phoneNumbers);
    for (int i=0; i<jaPhoneNumbers.size(); i++) {
      JSONObject joPhoneNumber = jaPhoneNumbers.getJSONObject(i);
      if (phoneNumbers.get(i).getContactType() != null) {
        joPhoneNumber.put("contactTypeName", emails.get(i).getContactType().getName());
      }
    }
    
    JSONArray jaVariableKeys = new JSONArrayExtractor("variableName", "variableKey", "variableType").extract(schoolUserEditableVariableKeys);
    for (int i=0; i<schoolUserEditableVariableKeys.size(); i++) {
      JSONObject joVariableKey = jaVariableKeys.getJSONObject(i);
      Map<String,String> variables = school.getVariablesAsStringMap();
      joVariableKey.put("variableValue", variables.get(joVariableKey.getString("variableKey")));
    }


    this.setJsDataVariable(pageRequestContext, "addresses", jaAddresses.toString());
    this.setJsDataVariable(pageRequestContext, "emails", jaEmails.toString());
    this.setJsDataVariable(pageRequestContext, "phoneNumbers", jaPhoneNumbers.toString());
    this.setJsDataVariable(pageRequestContext, "variableKeys", jaVariableKeys.toString());
    pageRequestContext.getRequest().setAttribute("tags", tagsBuilder.toString()); // used by jsp
    pageRequestContext.getRequest().setAttribute("school", school);

    pageRequestContext.setIncludeJSP("/templates/settings/viewschool.jsp");
  }

  /**
   * Returns the roles allowed to access this page.
   * 
   * @return The roles allowed to access this page
   */
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
    return Messages.getInstance().getText(locale, "settings.viewSchool.pageTitle");
  }

}