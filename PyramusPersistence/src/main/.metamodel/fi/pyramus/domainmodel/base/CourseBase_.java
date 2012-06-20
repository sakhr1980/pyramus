package fi.pyramus.domainmodel.base;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CourseBase.class)
public abstract class CourseBase_ {

	public static volatile SingularAttribute<CourseBase, EducationalLength> courseLength;
	public static volatile SingularAttribute<CourseBase, Date> lastModified;
	public static volatile SingularAttribute<CourseBase, Subject> subject;
	public static volatile SingularAttribute<CourseBase, Long> version;
	public static volatile SingularAttribute<CourseBase, User> creator;
	public static volatile ListAttribute<CourseBase, CourseBaseVariable> variables;
	public static volatile SingularAttribute<CourseBase, Long> id;
	public static volatile SingularAttribute<CourseBase, Long> maxParticipantCount;
	public static volatile SingularAttribute<CourseBase, Boolean> archived;
	public static volatile SingularAttribute<CourseBase, Date> created;
	public static volatile SingularAttribute<CourseBase, String> description;
	public static volatile SingularAttribute<CourseBase, String> name;
	public static volatile SingularAttribute<CourseBase, User> lastModifier;
	public static volatile ListAttribute<CourseBase, CourseEducationType> courseEducationTypes;
	public static volatile SingularAttribute<CourseBase, Integer> courseNumber;

}

