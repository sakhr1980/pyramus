package fi.pyramus.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import fi.pyramus.plugin.PageHookContext;
import fi.pyramus.plugin.PageHookController;
import fi.pyramus.plugin.PageHookVault;

public class ExtensionHookTag extends TagSupport {

  private static final long serialVersionUID = 5603128082864676937L;

  public int doEndTag() throws javax.servlet.jsp.JspTagException {
    List<PageHookController> hookControllers = PageHookVault.getInstance().getPageHooks(getName());
    if (hookControllers != null) {
      for (PageHookController hookController : hookControllers) {
        PageHookContext pageHookContext = new PageHookContext();

        hookController.execute(pageHookContext);

        if (!StringUtils.isBlank(pageHookContext.getIncludeFtl())) {
          try {
            String includePath = pageHookContext.getIncludeFtl(); 
            pageContext.include(includePath);
          } catch (ServletException e) {
            throw new javax.servlet.jsp.JspTagException(e);
          } catch (IOException e) {
            throw new javax.servlet.jsp.JspTagException(e);
          }
        }
      }
    }

    return SKIP_BODY;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private String name;

}
