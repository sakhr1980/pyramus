package fi.pyramus.freemarker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

public class FreemarkerServlet extends freemarker.ext.servlet.FreemarkerServlet {

  /**
   * SerialVersionUID
   */
  private static final long serialVersionUID = 2472454788723357007L;

  @Override
  protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws ServletException, IOException {
    ((SimpleHash) data).put("contextPath", request.getContextPath());

    return super.preTemplateProcess(request, response, template, data);
  }  
}
