package fi.pyramus.dao;

public class DAOFactory {
  
  public static DAOFactory getInstance() {
    return instance;
  }
  
  private final static DAOFactory instance = new DAOFactory();
  
  public BaseDAO getBaseDAO() {
    return new BaseDAO();
  }
  
  public CourseDAO getCourseDAO() {
    return new CourseDAO();
  }
  
  public DraftDAO getDraftDAO() {
    return new DraftDAO();
  }
  
  public GradingDAO getGradingDAO() {
    return new GradingDAO();
  }
  
  public ModuleDAO getModuleDAO() {
    return new ModuleDAO();
  }
  
  public ProjectDAO getProjectDAO() {
    return new ProjectDAO();
  }
  
  public ReportDAO getReportDAO() {
    return new ReportDAO();
  }
  
  public ResourceDAO getResourceDAO() {
    return new ResourceDAO();
  }
  
  public StudentDAO getStudentDAO() {
    return new StudentDAO();
  }
  
  public UserDAO getUserDAO() {
    return new UserDAO();
  }
  
  public SystemDAO getSystemDAO() {
    return new SystemDAO();
  }
}
