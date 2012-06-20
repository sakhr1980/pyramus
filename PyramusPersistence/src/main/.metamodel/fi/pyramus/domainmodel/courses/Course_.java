package fi.pyramus.domainmodel.courses;

import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.modules.Module;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Course.class)
public abstract class Course_ extends fi.pyramus.domainmodel.base.CourseBase_ {

	public static volatile SetAttribute<Course, Tag> tags;
	public static volatile ListAttribute<Course, CourseUser> courseUsers;
	public static volatile SingularAttribute<Course, Module> module;
	public static volatile SingularAttribute<Course, Double> planningHours;
	public static volatile ListAttribute<Course, CourseComponent> courseComponents;
	public static volatile SingularAttribute<Course, Double> localTeachingDays;
	public static volatile SingularAttribute<Course, CourseState> state;
	public static volatile SingularAttribute<Course, Double> assessingHours;
	public static volatile SingularAttribute<Course, Date> endDate;
	public static volatile SingularAttribute<Course, Date> beginDate;
	public static volatile ListAttribute<Course, StudentCourseResource> studentCourseResources;
	public static volatile ListAttribute<Course, BasicCourseResource> basicCourseResources;
	public static volatile ListAttribute<Course, OtherCost> otherCosts;
	public static volatile SingularAttribute<Course, String> nameExtension;
	public static volatile SingularAttribute<Course, Double> distanceTeachingDays;
	public static volatile ListAttribute<Course, GradeCourseResource> gradeCourseResources;
	public static volatile SingularAttribute<Course, Date> enrolmentTimeEnd;
	public static volatile SingularAttribute<Course, Double> teachingHours;
	public static volatile ListAttribute<Course, CourseStudent> courseStudents;

}

