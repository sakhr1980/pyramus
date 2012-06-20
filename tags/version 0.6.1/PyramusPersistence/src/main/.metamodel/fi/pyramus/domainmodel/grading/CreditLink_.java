package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CreditLink.class)
public abstract class CreditLink_ {

	public static volatile SingularAttribute<CreditLink, Long> id;
	public static volatile SingularAttribute<CreditLink, Boolean> archived;
	public static volatile SingularAttribute<CreditLink, Student> student;
	public static volatile SingularAttribute<CreditLink, Date> created;
	public static volatile SingularAttribute<CreditLink, Credit> credit;
	public static volatile SingularAttribute<CreditLink, User> creator;

}

