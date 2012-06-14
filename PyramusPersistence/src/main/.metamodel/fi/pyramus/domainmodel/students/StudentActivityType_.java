package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentActivityType.class)
public abstract class StudentActivityType_ {

	public static volatile SingularAttribute<StudentActivityType, Long> id;
	public static volatile SingularAttribute<StudentActivityType, Boolean> archived;
	public static volatile SingularAttribute<StudentActivityType, String> name;
	public static volatile SingularAttribute<StudentActivityType, Long> version;

}

