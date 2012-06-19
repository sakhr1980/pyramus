package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentStudyEndReason.class)
public abstract class StudentStudyEndReason_ {

	public static volatile SingularAttribute<StudentStudyEndReason, Long> id;
	public static volatile ListAttribute<StudentStudyEndReason, StudentStudyEndReason> childEndReasons;
	public static volatile SingularAttribute<StudentStudyEndReason, String> name;
	public static volatile SingularAttribute<StudentStudyEndReason, StudentStudyEndReason> parentReason;
	public static volatile SingularAttribute<StudentStudyEndReason, Long> version;

}

