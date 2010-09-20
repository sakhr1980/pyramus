package fi.pyramus.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.pyramus.domainmodel.reports.Report;
import fi.pyramus.domainmodel.users.User;

public class ReportDAO extends PyramusDAO {

  public Report findReportById(Long reportId) {
    return getEntityManager().find(Report.class, reportId);
  }

  public List<Report> listReports() {
    CriteriaQuery<Report> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(Report.class);
    Root<Report> reports = criteriaQuery.from(Report.class);
    criteriaQuery.select(reports);
    TypedQuery<Report> q = getEntityManager().createQuery(criteriaQuery);
    return q.getResultList();
  }

  public Report createReport(String name, String data, User creatingUser) {
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

  public void updateReportName(Report report, String name, User modifyingUser) {
    Date now = new Date(System.currentTimeMillis());
    
    report.setName(name);
    report.setLastModified(now);
    report.setLastModifier(modifyingUser);
    
    getEntityManager().persist(report);
  }

  public void updateReportData(Report report, String data, User modifyingUser) {
    Date now = new Date(System.currentTimeMillis());
    
    report.setData(data);
    report.setLastModified(now);
    report.setLastModifier(modifyingUser);
    
    getEntityManager().persist(report);
  }

  public void deleteReport(Report report) {
    getEntityManager().remove(report);
  }
}