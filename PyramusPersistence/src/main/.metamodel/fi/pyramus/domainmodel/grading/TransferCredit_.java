package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.base.CourseOptionality;
import fi.pyramus.domainmodel.base.EducationalLength;
import fi.pyramus.domainmodel.base.School;
import fi.pyramus.domainmodel.base.Subject;
import fi.pyramus.domainmodel.students.Student;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TransferCredit.class)
public abstract class TransferCredit_ extends fi.pyramus.domainmodel.grading.Credit_ {

	public static volatile SingularAttribute<TransferCredit, EducationalLength> courseLength;
	public static volatile SingularAttribute<TransferCredit, School> school;
	public static volatile SingularAttribute<TransferCredit, Student> student;
	public static volatile SingularAttribute<TransferCredit, Subject> subject;
	public static volatile SingularAttribute<TransferCredit, CourseOptionality> optionality;
	public static volatile SingularAttribute<TransferCredit, Integer> courseNumber;
	public static volatile SingularAttribute<TransferCredit, String> courseName;

}

