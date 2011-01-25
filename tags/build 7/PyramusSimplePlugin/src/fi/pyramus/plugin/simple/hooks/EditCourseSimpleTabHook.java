package fi.pyramus.plugin.simple.hooks;

import fi.pyramus.plugin.PageHookContext;
import fi.pyramus.plugin.PageHookController;

public class EditCourseSimpleTabHook implements PageHookController {
  
  @Override
  public void execute(PageHookContext pageHookContext) {
    pageHookContext.setIncludeFtl("/plugin/simple/ftl/editcoursesimpletabhook.ftl");
  }
}
