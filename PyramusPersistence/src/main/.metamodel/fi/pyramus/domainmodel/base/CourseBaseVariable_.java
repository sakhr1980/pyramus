package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseBaseVariable.class)
public abstract class CourseBaseVariable_ {

	public static volatile SingularAttribute<CourseBaseVariable, Long> id;
	public static volatile SingularAttribute<CourseBaseVariable, CourseBase> courseBase;
	public static volatile SingularAttribute<CourseBaseVariable, String> value;
	public static volatile SingularAttribute<CourseBaseVariable, CourseBaseVariableKey> key;
	public static volatile SingularAttribute<CourseBaseVariable, Long> version;

}

