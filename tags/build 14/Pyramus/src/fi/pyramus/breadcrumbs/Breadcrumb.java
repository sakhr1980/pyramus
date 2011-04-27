package fi.pyramus.breadcrumbs;

public class Breadcrumb {
  
  public Breadcrumb(String url, String name) {
    this.url = url;
    this.name = name;
  }

  public void setUrl(String url) {
    this.url = url;
  }
  public String getUrl() {
    return url;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }
  
  private String url;
  
  private String name;

}
