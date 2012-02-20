package fi.pyramus.dao;

import fi.pyramus.dao.base.AcademicTermDAO;
import fi.pyramus.dao.base.AddressDAO;
import fi.pyramus.dao.base.BillingDetailsDAO;
import fi.pyramus.dao.base.ComponentBaseDAO;
import fi.pyramus.dao.base.ContactInfoDAO;
import fi.pyramus.dao.base.ContactTypeDAO;
import fi.pyramus.dao.base.ContactURLDAO;
import fi.pyramus.dao.base.ContactURLTypeDAO;
import fi.pyramus.dao.base.CourseBaseDAO;
import fi.pyramus.dao.base.CourseBaseVariableDAO;
import fi.pyramus.dao.base.CourseBaseVariableKeyDAO;
import fi.pyramus.dao.base.CourseEducationSubtypeDAO;
import fi.pyramus.dao.base.CourseEducationTypeDAO;
import fi.pyramus.dao.base.DefaultsDAO;
import fi.pyramus.dao.base.EducationSubtypeDAO;
import fi.pyramus.dao.base.EducationTypeDAO;
import fi.pyramus.dao.base.EducationalLengthDAO;
import fi.pyramus.dao.base.EducationalTimeUnitDAO;
import fi.pyramus.dao.base.EmailDAO;
import fi.pyramus.dao.base.LanguageDAO;
import fi.pyramus.dao.base.MagicKeyDAO;
import fi.pyramus.dao.base.MunicipalityDAO;
import fi.pyramus.dao.base.NationalityDAO;
import fi.pyramus.dao.base.PhoneNumberDAO;
import fi.pyramus.dao.base.SchoolDAO;
import fi.pyramus.dao.base.SchoolFieldDAO;
import fi.pyramus.dao.base.SchoolVariableDAO;
import fi.pyramus.dao.base.SchoolVariableKeyDAO;
import fi.pyramus.dao.base.StudyProgrammeCategoryDAO;
import fi.pyramus.dao.base.StudyProgrammeDAO;
import fi.pyramus.dao.base.SubjectDAO;
import fi.pyramus.dao.base.TagDAO;
import fi.pyramus.dao.changelog.ChangeLogEntryDAO;
import fi.pyramus.dao.changelog.ChangeLogEntryEntityDAO;
import fi.pyramus.dao.changelog.ChangeLogEntryEntityPropertyDAO;
import fi.pyramus.dao.changelog.ChangeLogEntryPropertyDAO;
import fi.pyramus.dao.changelog.TrackedEntityPropertyDAO;
import fi.pyramus.dao.courses.BasicCourseResourceDAO;
import fi.pyramus.dao.courses.CourseComponentDAO;
import fi.pyramus.dao.courses.CourseComponentResourceDAO;
import fi.pyramus.dao.courses.CourseDescriptionCategoryDAO;
import fi.pyramus.dao.courses.CourseDescriptionDAO;
import fi.pyramus.dao.courses.CourseEnrolmentTypeDAO;
import fi.pyramus.dao.courses.CourseParticipationTypeDAO;
import fi.pyramus.dao.courses.CourseStateDAO;
import fi.pyramus.dao.courses.CourseStudentDAO;
import fi.pyramus.dao.courses.CourseUserDAO;
import fi.pyramus.dao.courses.CourseUserRoleDAO;
import fi.pyramus.dao.courses.GradeCourseResourceDAO;
import fi.pyramus.dao.courses.OtherCostDAO;
import fi.pyramus.dao.courses.StudentCourseResourceDAO;
import fi.pyramus.dao.drafts.DraftDAO;
import fi.pyramus.dao.grading.CourseAssessmentDAO;
import fi.pyramus.dao.grading.CreditDAO;
import fi.pyramus.dao.grading.GradeDAO;
import fi.pyramus.dao.grading.GradingScaleDAO;
import fi.pyramus.dao.grading.ProjectAssessmentDAO;
import fi.pyramus.dao.grading.TransferCreditDAO;
import fi.pyramus.dao.grading.TransferCreditTemplateCourseDAO;
import fi.pyramus.dao.grading.TransferCreditTemplateDAO;
import fi.pyramus.dao.help.HelpFolderDAO;
import fi.pyramus.dao.help.HelpItemDAO;
import fi.pyramus.dao.help.HelpItemTitleDAO;
import fi.pyramus.dao.help.HelpPageContentDAO;
import fi.pyramus.dao.help.HelpPageDAO;
import fi.pyramus.dao.modules.ModuleComponentDAO;
import fi.pyramus.dao.modules.ModuleDAO;
import fi.pyramus.dao.projects.ProjectDAO;
import fi.pyramus.dao.projects.ProjectModuleDAO;
import fi.pyramus.dao.projects.StudentProjectDAO;
import fi.pyramus.dao.projects.StudentProjectModuleDAO;
import fi.pyramus.dao.reports.ReportCategoryDAO;
import fi.pyramus.dao.reports.ReportDAO;
import fi.pyramus.dao.resources.MaterialResourceDAO;
import fi.pyramus.dao.resources.ResourceCategoryDAO;
import fi.pyramus.dao.resources.ResourceDAO;
import fi.pyramus.dao.resources.WorkResourceDAO;
import fi.pyramus.dao.students.AbstractStudentDAO;
import fi.pyramus.dao.students.StudentActivityTypeDAO;
import fi.pyramus.dao.students.StudentContactLogEntryCommentDAO;
import fi.pyramus.dao.students.StudentContactLogEntryDAO;
import fi.pyramus.dao.students.StudentEducationalLevelDAO;
import fi.pyramus.dao.students.StudentExaminationTypeDAO;
import fi.pyramus.dao.students.StudentGroupDAO;
import fi.pyramus.dao.students.StudentGroupStudentDAO;
import fi.pyramus.dao.students.StudentGroupUserDAO;
import fi.pyramus.dao.students.StudentImageDAO;
import fi.pyramus.dao.students.StudentStudyEndReasonDAO;
import fi.pyramus.dao.students.StudentVariableDAO;
import fi.pyramus.dao.students.StudentVariableKeyDAO;
import fi.pyramus.dao.system.SettingDAO;
import fi.pyramus.dao.system.SettingKeyDAO;
import fi.pyramus.dao.users.InternalAuthDAO;
import fi.pyramus.dao.users.UserDAO;
import fi.pyramus.dao.users.UserVariableDAO;
import fi.pyramus.dao.users.UserVariableKeyDAO;

public class DAOFactory {
  
  public static DAOFactory getInstance() {
    return instance;
  }
  
  private final static DAOFactory instance = new DAOFactory();
  
  public SystemDAO getSystemDAO() {
    return new SystemDAO();
  }

  /* Draft */
  
  public DraftDAO getDraftDAO() {
    return new DraftDAO();
  }
  
  
  /* Student */
  
  public fi.pyramus.dao.students.StudentDAO getStudentDAO() {
    return new fi.pyramus.dao.students.StudentDAO();
  }

  public AbstractStudentDAO getAbstractStudentDAO() {
    return new AbstractStudentDAO();
  }
  
  public StudentActivityTypeDAO getStudentActivityTypeDAO() {
    return new StudentActivityTypeDAO();
  }
  
  public StudentContactLogEntryDAO getStudentContactLogEntryDAO() {
    return new StudentContactLogEntryDAO();
  }
  
  public StudentContactLogEntryCommentDAO getStudentContactLogEntryCommentDAO() {
    return new StudentContactLogEntryCommentDAO();
  }
  
  public StudentEducationalLevelDAO getStudentEducationalLevelDAO() {
    return new StudentEducationalLevelDAO();
  }
  
  public StudentExaminationTypeDAO getStudentExaminationTypeDAO() {
    return new StudentExaminationTypeDAO();
  }
  
  public StudentGroupDAO getStudentGroupDAO() {
    return new StudentGroupDAO();
  }
  
  public StudentGroupStudentDAO getStudentGroupStudentDAO() {
    return new StudentGroupStudentDAO();
  }
  
  public StudentGroupUserDAO getStudentGroupUserDAO() {
    return new StudentGroupUserDAO();
  }
  
  public StudentImageDAO getStudentImageDAO() {
    return new StudentImageDAO();
  }

  public StudentStudyEndReasonDAO getStudentStudyEndReasonDAO() {
    return new StudentStudyEndReasonDAO();
  }
  
  public StudentVariableDAO getStudentVariableDAO() {
    return new StudentVariableDAO();
  }
  
  public StudentVariableKeyDAO getStudentVariableKeyDAO() {
    return new StudentVariableKeyDAO();
  }

  /* Course */
  
  public BasicCourseResourceDAO getBasicCourseResourceDAO() {
    return new BasicCourseResourceDAO();
  }
  
  public CourseComponentDAO getCourseComponentDAO() {
    return new CourseComponentDAO();
  }
  
  public CourseComponentResourceDAO getCourseComponentResourceDAO() {
    return new CourseComponentResourceDAO();
  }
  
  public fi.pyramus.dao.courses.CourseDAO getCourseDAO() {
    return new fi.pyramus.dao.courses.CourseDAO();
  }
  
  public CourseDescriptionCategoryDAO getCourseDescriptionCategoryDAO() {
    return new CourseDescriptionCategoryDAO();
  }
  
  public CourseDescriptionDAO getCourseDescriptionDAO() {
    return new CourseDescriptionDAO();
  }
  
  public CourseEnrolmentTypeDAO getCourseEnrolmentTypeDAO() {
    return new CourseEnrolmentTypeDAO();
  }
  
  public CourseParticipationTypeDAO getCourseParticipationTypeDAO() {
    return new CourseParticipationTypeDAO();
  }
  
  public CourseStateDAO getCourseStateDAO() {
    return new CourseStateDAO();
  }
  
  public CourseStudentDAO getCourseStudentDAO() {
    return new CourseStudentDAO();
  }

  public CourseUserDAO getCourseUserDAO() {
    return new CourseUserDAO();
  }
  
  public CourseUserRoleDAO getCourseUserRoleDAO() {
    return new CourseUserRoleDAO();
  }
  
  public GradeCourseResourceDAO getGradeCourseResourceDAO() {
    return new GradeCourseResourceDAO();
  }

  public OtherCostDAO getOtherCostDAO() {
    return new OtherCostDAO();
  }
  
  public StudentCourseResourceDAO getStudentCourseResourceDAO() {
    return new StudentCourseResourceDAO();
  }

  /* System */
  
  public SettingDAO getSettingDAO() {
    return new SettingDAO();
  }

  public SettingKeyDAO getSettingKeyDAO() {
    return new SettingKeyDAO();
  }

  /* Report */
  
  public ReportDAO getReportDAO() {
    return new ReportDAO();
  }
  
  public ReportCategoryDAO getReportCategoryDAO() {
    return new ReportCategoryDAO();
  }

  /* Users */
  
  public InternalAuthDAO getInternalAuthDAO() {
    return new InternalAuthDAO();
  }
  
  public UserDAO getUserDAO() {
    return new UserDAO();
  }
  
  public UserVariableDAO getUserVariableDAO() {
    return new UserVariableDAO();
  }
  
  public UserVariableKeyDAO getUserVariableKeyDAO() {
    return new UserVariableKeyDAO();
  }
  
  /* Change Log */
  
  public ChangeLogEntryDAO getChangeLogEntryDAO() {
    return new ChangeLogEntryDAO();
  }
  
  public ChangeLogEntryEntityDAO getChangeLogEntryEntityDAO() {
    return new ChangeLogEntryEntityDAO();
  }
  
  public ChangeLogEntryEntityPropertyDAO getChangeLogEntryEntityPropertyDAO() {
    return new ChangeLogEntryEntityPropertyDAO();
  }
  
  public ChangeLogEntryPropertyDAO getChangeLogEntryPropertyDAO() {
    return new ChangeLogEntryPropertyDAO();
  }
  
  public TrackedEntityPropertyDAO getTrackedEntityPropertyDAO() {
    return new TrackedEntityPropertyDAO();
  }

  /* Resource */
  
  public MaterialResourceDAO getMaterialResourceDAO() {
    return new MaterialResourceDAO();
  }
  
  public ResourceCategoryDAO getResourceCategoryDAO() {
    return new ResourceCategoryDAO();
  }
  
  public ResourceDAO getResourceDAO() {
    return new ResourceDAO();
  }
  
  public WorkResourceDAO getWorkResourceDAO() {
    return new WorkResourceDAO();
  }

  /* Module */
  
  public ModuleDAO getModuleDAO() {
    return new ModuleDAO();
  }
  
  public ModuleComponentDAO getModuleComponentDAO() {
    return new ModuleComponentDAO();
  }

  /* Project */
  
  public ProjectDAO getProjectDAO() {
    return new ProjectDAO();
  }
  
  public ProjectModuleDAO getProjectModuleDAO() {
    return new ProjectModuleDAO();
  }
  
  public StudentProjectDAO getStudentProjectDAO() {
    return new StudentProjectDAO();
  }
  
  public StudentProjectModuleDAO getStudentProjectModuleDAO() {
    return new StudentProjectModuleDAO();
  }

  /* Help */
  
  public HelpFolderDAO getHelpFolderDAO() {
    return new HelpFolderDAO();
  }
  
  public HelpItemDAO getHelpItemDAO() {
    return new HelpItemDAO();
  }
  
  public HelpItemTitleDAO getHelpItemTitleDAO() {
    return new HelpItemTitleDAO();
  }
  
  public HelpPageContentDAO getHelpPageContentDAO() {
    return new HelpPageContentDAO();
  }
  
  public HelpPageDAO getHelpPageDAO() {
    return new HelpPageDAO();
  }
  
  /* Grading */
  
  public CourseAssessmentDAO getCourseAssessmentDAO() {
    return new CourseAssessmentDAO();
  }
  
  public CreditDAO getCreditDAO() {
    return new CreditDAO();
  }
  
  public GradeDAO getGradeDAO() {
    return new GradeDAO();
  }
  
  public GradingScaleDAO getGradingScaleDAO() {
    return new GradingScaleDAO();
  }
  
  public ProjectAssessmentDAO getProjectAssessmentDAO() {
    return new ProjectAssessmentDAO();
  }
  
  public TransferCreditDAO getTransferCreditDAO() {
    return new TransferCreditDAO();
  }
  
  public TransferCreditTemplateCourseDAO getTransferCreditTemplateCourseDAO() {
    return new TransferCreditTemplateCourseDAO();
  }
  
  public TransferCreditTemplateDAO getTransferCreditTemplateDAO() {
    return new TransferCreditTemplateDAO();
  }
  
  /* Base */
  
  public AcademicTermDAO getAcademicTermDAO() {
    return new AcademicTermDAO();
  }

  public AddressDAO getAddressDAO() {
    return new AddressDAO();
  }

  public BillingDetailsDAO getBillingDetailsDAO() {
    return new BillingDetailsDAO();
  }

  public ComponentBaseDAO getComponentBaseDAO() {
    return new ComponentBaseDAO();
  }

  public ContactInfoDAO getContactInfoDAO() {
    return new ContactInfoDAO();
  }

  public ContactTypeDAO getContactTypeDAO() {
    return new ContactTypeDAO();
  }

  public ContactURLDAO getContactURLDAO() {
    return new ContactURLDAO();
  }

  public ContactURLTypeDAO getContactURLTypeDAO() {
    return new ContactURLTypeDAO();
  }

  public CourseBaseDAO getCourseBaseDAO() {
    return new CourseBaseDAO();
  }

  public CourseBaseVariableDAO getCourseBaseVariableDAO() {
    return new CourseBaseVariableDAO();
  }

  public CourseBaseVariableKeyDAO getCourseBaseVariableKeyDAO() {
    return new CourseBaseVariableKeyDAO();
  }

  public CourseEducationSubtypeDAO getCourseEducationSubtypeDAO() {
    return new CourseEducationSubtypeDAO();
  }

  public CourseEducationTypeDAO getCourseEducationTypeDAO() {
    return new CourseEducationTypeDAO();
  }

  public DefaultsDAO getDefaultsDAO() {
    return new DefaultsDAO();
  }

  public EducationalLengthDAO getEducationalLengthDAO() {
    return new EducationalLengthDAO();
  }

  public EducationalTimeUnitDAO getEducationalTimeUnitDAO() {
    return new EducationalTimeUnitDAO();
  }

  public EducationSubtypeDAO getEducationSubtypeDAO() {
    return new EducationSubtypeDAO();
  }

  public EducationTypeDAO getEducationTypeDAO() {
    return new EducationTypeDAO();
  }

  public EmailDAO getEmailDAO() {
    return new EmailDAO();
  }

  public LanguageDAO getLanguageDAO() {
    return new LanguageDAO();
  }

  public MagicKeyDAO getMagicKeyDAO() {
    return new MagicKeyDAO();
  }

  public MunicipalityDAO getMunicipalityDAO() {
    return new MunicipalityDAO();
  }

  public NationalityDAO getNationalityDAO() {
    return new NationalityDAO();
  }

  public PhoneNumberDAO getPhoneNumberDAO() {
    return new PhoneNumberDAO();
  }

  public SchoolDAO getSchoolDAO() {
    return new SchoolDAO();
  }

  public SchoolFieldDAO getSchoolFieldDAO() {
    return new SchoolFieldDAO();
  }

  public SchoolVariableDAO getSchoolVariableDAO() {
    return new SchoolVariableDAO();
  }

  public SchoolVariableKeyDAO getSchoolVariableKeyDAO() {
    return new SchoolVariableKeyDAO();
  }

  public StudyProgrammeDAO getStudyProgrammeDAO() {
    return new StudyProgrammeDAO();
  }

  public StudyProgrammeCategoryDAO getStudyProgrammeCategoryDAO() {
    return new StudyProgrammeCategoryDAO();
  }

  public SubjectDAO getSubjectDAO() {
    return new SubjectDAO();
  }

  public TagDAO getTagDAO() {
    return new TagDAO();
  }
}
