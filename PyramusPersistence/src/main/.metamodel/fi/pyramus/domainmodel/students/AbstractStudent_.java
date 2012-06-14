package fi.pyramus.domainmodel.students;

import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(AbstractStudent.class)
public abstract class AbstractStudent_ {

	public static volatile SingularAttribute<AbstractStudent, Long> id;
	public static volatile SingularAttribute<AbstractStudent, String> basicInfo;
	public static volatile SingularAttribute<AbstractStudent, Date> birthday;
	public static volatile ListAttribute<AbstractStudent, Student> students;
	public static volatile SingularAttribute<AbstractStudent, Sex> sex;
	public static volatile SingularAttribute<AbstractStudent, String> socialSecurityNumber;
	public static volatile SingularAttribute<AbstractStudent, Boolean> secureInfo;
	public static volatile SingularAttribute<AbstractStudent, Long> version;

}

