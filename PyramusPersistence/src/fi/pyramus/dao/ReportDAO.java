package fi.pyramus.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.reports.Report;
import fi.pyramus.domainmodel.reports.ReportCategory;
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
  
  public void updateReport(Report report, String name, ReportCategory reportCategory) {
    Session s = getHibernateSession();
    report.setName(name);
    report.setCategory(reportCategory);
    s.saveOrUpdate(report);
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
  
  public ReportCategory findReportCategoryById(Long reportCategoryId) {
    return getEntityManager().find(ReportCategory.class, reportCategoryId);
  }

  @SuppressWarnings("unchecked")
  public List<ReportCategory> listReportCategories() {
    Session s = getHibernateSession();
    List<ReportCategory> categories = s.createCriteria(ReportCategory.class).list();
    Collections.sort(categories, new Comparator<ReportCategory>() {
      public int compare(ReportCategory o1, ReportCategory o2) {
        if (o1.getIndexColumn() == o2.getIndexColumn() || o1.getIndexColumn().equals(o2.getIndexColumn())) {
          return o1.getName() == null ? -1 : o2.getName() == null ? 1 : o1.getName().compareTo(o2.getName());
        }
        else {
          return o1.getIndexColumn() == null ? -1 : o2.getIndexColumn() == null ? 1 : o1.getIndexColumn().compareTo(o2.getIndexColumn());
        }
      }
    });
    return categories;
  }

  public ReportCategory createReportCategory(String name, Integer indexColumn) {
    Session s = getHibernateSession();
    ReportCategory reportCategory = new ReportCategory();
    reportCategory.setName(name);
    reportCategory.setIndexColumn(indexColumn);
    s.persist(reportCategory);
    return reportCategory;
  }

  public ReportCategory updateReportCategory(ReportCategory reportCategory, String name, Integer indexColumn) {
    Session s = getHibernateSession();
    reportCategory.setName(name);
    reportCategory.setIndexColumn(indexColumn);
    s.saveOrUpdate(reportCategory);
    return reportCategory;
  }
  
  public void deleteReportCategory(ReportCategory reportCategory) {
    Session s = getHibernateSession();
    s.delete(reportCategory);
  }
  
  public boolean isReportCategoryInUse(ReportCategory reportCategory) {
    Session s = getHibernateSession();
    return !s.createCriteria(Report.class).add(Restrictions.eq("category", reportCategory)).list().isEmpty();
  }
  
}