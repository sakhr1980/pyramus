package fi.pyramus.dao.reports;

import java.util.Date;

import javax.persistence.EntityManager;

import fi.pyramus.dao.PyramusEntityDAO;
import fi.pyramus.domainmodel.reports.Report;
import fi.pyramus.domainmodel.reports.ReportCategory;
import fi.pyramus.domainmodel.users.User;

public class ReportDAO extends PyramusEntityDAO<Report> {

  public Report create(String name, String data, User creatingUser) {
    Date now = new Date(System.currentTimeMillis());
    
    Report report = new Report();
    
    report.setData(data);
    report.setName(name);
    report.setCreated(now);
    report.setCreator(creatingUser);
    report.setLastModified(now);
    report.setLastModifier(creatingUser);

    getEntityManager().persist(report);

    return report;
  }
  
  public void update(Report report, String name, ReportCategory reportCategory) {
    EntityManager entityManager = getEntityManager();

    report.setName(name);
    report.setCategory(reportCategory);
    
    entityManager.persist(report);
  }

  public void updateName(Report report, String name, User modifyingUser) {
    Date now = new Date(System.currentTimeMillis());
    
    report.setName(name);
    report.setLastModified(now);
    report.setLastModifier(modifyingUser);
    
    getEntityManager().persist(report);
  }

  public void updateData(Report report, String data, User modifyingUser) {
    Date now = new Date(System.currentTimeMillis());
    
    report.setData(data);
    report.setLastModified(now);
    report.setLastModifier(modifyingUser);
    
    getEntityManager().persist(report);
  }

  @Override
  public void delete(Report report) {
    super.delete(report);
  }
  
}