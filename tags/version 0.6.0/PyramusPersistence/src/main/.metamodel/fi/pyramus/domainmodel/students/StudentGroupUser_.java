package fi.pyramus.domainmodel.students;

import fi.pyramus.domainmodel.users.User;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(StudentGroupUser.class)
public abstract class StudentGroupUser_ {

	public static volatile SingularAttribute<StudentGroupUser, Long> id;
	public static volatile SingularAttribute<StudentGroupUser, StudentGroup> studentGroup;
	public static volatile SingularAttribute<StudentGroupUser, User> user;
	public static volatile SingularAttribute<StudentGroupUser, Long> version;

}

