package fi.pyramus.services.entities.courses;


public class CourseEntitySearchResult {

  public CourseEntitySearchResult(int page, int pages, int totalHitCount, CourseEntity[] results) {
    super();
    this.page = page;
    this.pages = pages;
    this.totalHitCount = totalHitCount;
    this.results = results;
  }

  public int getPage() {
    return page;
  }
  
  public int getPages() {
    return pages;
  }
  
  public CourseEntity[] getResults() {
    return results;
  }
  
  public int getTotalHitCount() {
    return totalHitCount;
  }
  
  private int page;
  private int pages;
  private int totalHitCount;
  private CourseEntity[] results;
}
