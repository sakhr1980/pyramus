package fi.pyramus.domainmodel.students;

import fi.pyramus.domainmodel.base.BillingDetails;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Language;
import fi.pyramus.domainmodel.base.Municipality;
import fi.pyramus.domainmodel.base.Nationality;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.StudyProgramme;
import fi.pyramus.domainmodel.base.Tag;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Student.class)
public abstract class Student_ {

	public static volatile SingularAttribute<Student, Municipality> municipality;
	public static volatile SingularAttribute<Student, String> education;
	public static volatile ListAttribute<Student, BillingDetails> billingDetails;
	public static volatile SingularAttribute<Student, String> studyEndText;
	public static volatile SingularAttribute<Student, Long> version;
	public static volatile SingularAttribute<Student, StudyProgramme> studyProgramme;
	public static volatile SingularAttribute<Student, Long> id;
	public static volatile SingularAttribute<Student, Boolean> archived;
	public static volatile SingularAttribute<Student, ContactInfo> contactInfo;
	public static volatile SingularAttribute<Student, Date> studyEndDate;
	public static volatile SingularAttribute<Student, StudentStudyEndReason> studyEndReason;
	public static volatile SingularAttribute<Student, String> firstName;
	public static volatile SingularAttribute<Student, Double> previousStudies;
	public static volatile SetAttribute<Student, Tag> tags;
	public static volatile SingularAttribute<Student, String> lastName;
	public static volatile SingularAttribute<Student, String> nickname;
	public static volatile SingularAttribute<Student, String> additionalInfo;
	public static volatile SingularAttribute<Student, StudentExaminationType> examinationType;
	public static volatile SingularAttribute<Student, Date> studyStartDate;
	public static volatile ListAttribute<Student, StudentVariable> variables;
	public static volatile SingularAttribute<Student, School> school;
	public static volatile SingularAttribute<Student, Nationality> nationality;
	public static volatile SingularAttribute<Student, AbstractStudent> abstractStudent;
	public static volatile SingularAttribute<Student, StudentEducationalLevel> educationalLevel;
	public static volatile SingularAttribute<Student, Language> language;
	public static volatile SingularAttribute<Student, StudentActivityType> activityType;
	public static volatile SingularAttribute<Student, Date> studyTimeEnd;
	public static volatile SingularAttribute<Student, Boolean> lodging;

}

