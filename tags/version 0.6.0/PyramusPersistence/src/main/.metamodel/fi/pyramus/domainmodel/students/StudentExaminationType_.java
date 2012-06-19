package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentExaminationType.class)
public abstract class StudentExaminationType_ {

	public static volatile SingularAttribute<StudentExaminationType, Long> id;
	public static volatile SingularAttribute<StudentExaminationType, Boolean> archived;
	public static volatile SingularAttribute<StudentExaminationType, String> name;
	public static volatile SingularAttribute<StudentExaminationType, Long> version;

}

