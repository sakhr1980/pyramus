package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Subject.class)
public abstract class Subject_ {

	public static volatile SingularAttribute<Subject, Long> id;
	public static volatile SingularAttribute<Subject, EducationType> educationType;
	public static volatile SingularAttribute<Subject, Boolean> archived;
	public static volatile SingularAttribute<Subject, String> name;
	public static volatile SingularAttribute<Subject, String> code;
	public static volatile SingularAttribute<Subject, Long> version;

}

