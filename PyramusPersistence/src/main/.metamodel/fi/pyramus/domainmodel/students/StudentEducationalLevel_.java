package fi.pyramus.domainmodel.students;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentEducationalLevel.class)
public abstract class StudentEducationalLevel_ {

	public static volatile SingularAttribute<StudentEducationalLevel, Long> id;
	public static volatile SingularAttribute<StudentEducationalLevel, Boolean> archived;
	public static volatile SingularAttribute<StudentEducationalLevel, String> name;
	public static volatile SingularAttribute<StudentEducationalLevel, Long> version;

}

