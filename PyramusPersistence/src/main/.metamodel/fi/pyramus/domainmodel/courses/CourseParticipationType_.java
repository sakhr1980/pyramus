package fi.pyramus.domainmodel.courses;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseParticipationType.class)
public abstract class CourseParticipationType_ {

	public static volatile SingularAttribute<CourseParticipationType, Long> id;
	public static volatile SingularAttribute<CourseParticipationType, Boolean> archived;
	public static volatile SingularAttribute<CourseParticipationType, String> name;
	public static volatile SingularAttribute<CourseParticipationType, Integer> indexColumn;
	public static volatile SingularAttribute<CourseParticipationType, Long> version;

}

