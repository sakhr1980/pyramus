package fi.pyramus.domainmodel.students;

import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentGroup.class)
public abstract class StudentGroup_ {

	public static volatile SetAttribute<StudentGroup, Tag> tags;
	public static volatile SetAttribute<StudentGroup, StudentGroupUser> users;
	public static volatile SetAttribute<StudentGroup, StudentGroupStudent> students;
	public static volatile SingularAttribute<StudentGroup, Date> lastModified;
	public static volatile SingularAttribute<StudentGroup, Date> beginDate;
	public static volatile SingularAttribute<StudentGroup, User> creator;
	public static volatile SingularAttribute<StudentGroup, Long> version;
	public static volatile SingularAttribute<StudentGroup, Long> id;
	public static volatile SingularAttribute<StudentGroup, Boolean> archived;
	public static volatile SingularAttribute<StudentGroup, Date> created;
	public static volatile SingularAttribute<StudentGroup, String> description;
	public static volatile SingularAttribute<StudentGroup, String> name;
	public static volatile SingularAttribute<StudentGroup, User> lastModifier;

}

