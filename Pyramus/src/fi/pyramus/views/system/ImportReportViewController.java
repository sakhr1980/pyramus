package fi.pyramus.views.system;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.reports.ReportDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.domainmodel.reports.Report;
import fi.pyramus.domainmodel.users.User;
import fi.pyramus.framework.PyramusFormViewController;
import fi.pyramus.framework.UserRole;

@SuppressWarnings("deprecation")
public class ImportReportViewController extends PyramusFormViewController {

  @Override
  public void processForm(PageRequestContext requestContext) {
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();
    requestContext.getRequest().setAttribute("reports", reportDAO.listAll());
    requestContext.setIncludeJSP("/templates/system/importreport.jsp");
  }

  @Override
  public void processSend(PageRequestContext requestContext) {
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();
    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
    
    Long existingReportId = requestContext.getLong("report");
    String name = requestContext.getString("name");
    FileItem file = requestContext.getFile("file");

    try {
      ByteArrayOutputStream dataStream = null;
      if (file.getSize() > 0) {
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
        Document reportDocument = db.parse(new InputSource(new InputStreamReader(file.getInputStream(), "UTF-8")));
        NodeList hibernateDataSources = XPathAPI.selectNodeList(reportDocument.getDocumentElement(),
            "data-sources/oda-data-source[@extensionID='org.jboss.tools.birt.oda']");
        for (int i = 0, l = hibernateDataSources.getLength(); i < l; i++) {
          Element dataSourceElement = (Element) hibernateDataSources.item(i);
          Node configurationProperyNode = XPathAPI.selectSingleNode(dataSourceElement, "property[@name='configuration']");
          if (configurationProperyNode != null)
            dataSourceElement.removeChild(configurationProperyNode);
  
          Element jndiNameElement = (Element) XPathAPI.selectSingleNode(dataSourceElement, "property[@name='jndiName']");
          if (jndiNameElement == null) {
            jndiNameElement = reportDocument.createElement("property");
            jndiNameElement.setAttribute("name", "jndiName");
            dataSourceElement.appendChild(jndiNameElement);
          }
  
          jndiNameElement.setTextContent("pyramusSessionFactory");
        }
  
        reportDocument.getDocumentElement().getAttributeNode("version").setTextContent("3.2.20");
  
        NodeList jdbcDataSources = XPathAPI.selectNodeList(reportDocument.getDocumentElement(),
            "data-sources/oda-data-source[@extensionID='org.eclipse.birt.report.data.oda.jdbc']");
        for (int i = 0, l = jdbcDataSources.getLength(); i < l; i++) {
          Element dataSourceElement = (Element) jdbcDataSources.item(i);
  
          Node removeNode = XPathAPI.selectSingleNode(dataSourceElement, "property[@name='odaUser']");
          if (removeNode != null)
            dataSourceElement.removeChild(removeNode);
          removeNode = XPathAPI.selectSingleNode(dataSourceElement, "property[@name='odaURL']");
          if (removeNode != null)
            dataSourceElement.removeChild(removeNode);
          removeNode = XPathAPI.selectSingleNode(dataSourceElement, "encrypted-property[@name='odaPassword']");
          if (removeNode != null)
            dataSourceElement.removeChild(removeNode);
          removeNode = XPathAPI.selectSingleNode(dataSourceElement, "property[@name='odaDriverClass']");
          if (removeNode != null)
            dataSourceElement.removeChild(removeNode);
          removeNode = XPathAPI.selectSingleNode(dataSourceElement, "list-property[@name='privateDriverProperties']");
          if (removeNode != null)
            dataSourceElement.removeChild(removeNode);
          
          Element jndiNameElement = (Element) XPathAPI.selectSingleNode(dataSourceElement, "property[@name='odaJndiName']");
          if (jndiNameElement == null) {
            jndiNameElement = reportDocument.createElement("property");
            jndiNameElement.setAttribute("name", "odaJndiName");
            dataSourceElement.appendChild(jndiNameElement);
          }
          
          jndiNameElement.setTextContent("jdbc/pyramus");
        }
  
        dataStream = new ByteArrayOutputStream();
  
        XMLSerializer xmlSerializer = new XMLSerializer(dataStream, new OutputFormat(reportDocument));
        xmlSerializer.serialize(reportDocument);
      }
      
      User loggedUser = userDAO.findById(requestContext.getLoggedUserId());
      
      if (existingReportId != null) {
        Report report = reportDAO.findById(existingReportId);
        if (name == null && dataStream == null) {
          reportDAO.delete(report);
          requestContext.setRedirectURL(requestContext.getReferer(true));
        }
        else {
          if (!StringUtils.isBlank(name)) {
            reportDAO.updateName(report, name, loggedUser);
          }
          if (dataStream != null) {
            reportDAO.updateData(report, dataStream.toString("UTF-8"), loggedUser);
          }
          requestContext.setRedirectURL(requestContext.getRequest().getContextPath()
              + "/reports/viewreport.page?&reportId=" + report.getId());
        }
      }
      else {
        Report report = reportDAO.create(name, dataStream.toString("UTF-8"), loggedUser);
        requestContext.setRedirectURL(requestContext.getRequest().getContextPath()
            + "/reports/viewreport.page?&reportId=" + report.getId());
      }
    }
    catch (IOException e) {
      throw new SmvcRuntimeException(e);
    }
    catch (ParserConfigurationException e) {
      throw new SmvcRuntimeException(e);
    }
    catch (SAXException e) {
      throw new SmvcRuntimeException(e);
    }
    catch (TransformerException e) {
      throw new SmvcRuntimeException(e);
    }
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }

  private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
}