package fi.pyramus.binary.reports;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fi.pyramus.BinaryRequestContext;
import fi.pyramus.PyramusRuntimeException;
import fi.pyramus.UserRole;
import fi.pyramus.binary.BinaryRequestController;
import fi.pyramus.dao.BaseDAO;
import fi.pyramus.dao.DAOFactory;
import fi.pyramus.dao.ReportDAO;
import fi.pyramus.domainmodel.base.MagicKey;
import fi.pyramus.domainmodel.reports.Report;

public class DownloadReportBinaryRequestController implements BinaryRequestController {

  public void process(BinaryRequestContext binaryRequestContext) {
    BaseDAO baseDAO = DAOFactory.getInstance().getBaseDAO();
    ReportDAO reportDAO = DAOFactory.getInstance().getReportDAO();
    
    Long reportId = binaryRequestContext.getLong("reportId");
    String formatParameter = binaryRequestContext.getString("format");
    ReportOutputFormat outputFormat = Enum.valueOf(ReportOutputFormat.class, formatParameter);
    
    StringBuilder magicKeyBuilder = new StringBuilder()
      .append(Long.toHexString(reportId))
      .append('-')
      .append(Long.toHexString(System.currentTimeMillis()))
      .append('-')
      .append(Long.toHexString(Thread.currentThread().getId()));
  
    MagicKey magicKey = baseDAO.createMagicKey(magicKeyBuilder.toString()); 
    
    Report report = reportDAO.findReportById(reportId);
    
    String reportName = report.getName().toLowerCase().replaceAll("[^a-z0-9\\.]", "_");
    String reportsContextPath = System.getProperty("reports.contextPath");
    
    StringBuilder urlBuilder = new StringBuilder()
      .append(reportsContextPath)
      .append("/preview")
      .append("?magicKey=")
      .append(magicKey.getName())
      .append("&__report=reports/")
      .append(reportId)
      .append(".rptdesign")
      .append("&__format=").append(outputFormat.name());
    
    Map<String, String[]> parameterMap = binaryRequestContext.getRequest().getParameterMap();
    for (String parameterName : parameterMap.keySet()) {
      if (!reservedParameters.contains(parameterName)) {
        String[] values = parameterMap.get(parameterName);
        for (String value : values) {
          // TODO ISO-8859-1 should be UTF-8, once Birt's parameter dialog form has its accept-charset="UTF-8" set 
          try {
            urlBuilder.append('&').append(parameterName).append('=').append(URLEncoder.encode(value, "ISO-8859-1"));
          }
          catch (UnsupportedEncodingException e) {
            throw new PyramusRuntimeException(e);
          }
        }
      }
    }

    System.out.println("DownloadReportBinaryRequestController: " + urlBuilder);
    
    binaryRequestContext.setFileName(reportName + '.' + outputFormat.getExt());
    binaryRequestContext.setContentUrl(urlBuilder.toString());
  }
  
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.USER, UserRole.MANAGER, UserRole.ADMINISTRATOR };
  }
  
  private enum ReportOutputFormat {
    HTML ("html", "text/html"),
    PDF  ("pdf", "application/pdf"),
    RTF  ("rtf", "application/rtf"),
    XLS  ("xml", "application/vnd.ms-excel");
    
    ReportOutputFormat (String ext, String mimeType) {
      this.ext = ext;
      this.mimeType = mimeType;
    }
    
    public String getMimeType() {
      return mimeType;
    }
    
    public String getExt() {
      return ext;
    }
    
    private String ext;
    private String mimeType;
  }
  

  private static Set<String> reservedParameters = new HashSet<String>();
  
  static {
    reservedParameters.add("reportId");
    reservedParameters.add("magicKey");
    reservedParameters.add("format");
    reservedParameters.add("__format");
    reservedParameters.add("__report");
  }
}
