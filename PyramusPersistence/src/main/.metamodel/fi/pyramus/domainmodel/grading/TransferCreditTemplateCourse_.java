package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.base.CourseOptionality;
import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.Subject;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TransferCreditTemplateCourse.class)
public abstract class TransferCreditTemplateCourse_ {

	public static volatile SingularAttribute<TransferCreditTemplateCourse, Long> id;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, EducationalLength> courseLength;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, TransferCreditTemplate> transferCreditTemplate;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, Subject> subject;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, CourseOptionality> optionality;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, Integer> courseNumber;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, Long> version;
	public static volatile SingularAttribute<TransferCreditTemplateCourse, String> courseName;

}

