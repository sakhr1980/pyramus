package fi.pyramus.domainmodel.grading;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Credit.class)
public abstract class Credit_ {

	public static volatile SingularAttribute<Credit, Long> id;
	public static volatile SingularAttribute<Credit, CreditType> creditType;
	public static volatile SingularAttribute<Credit, User> assessingUser;
	public static volatile SingularAttribute<Credit, Boolean> archived;
	public static volatile SingularAttribute<Credit, Grade> grade;
	public static volatile SingularAttribute<Credit, String> verbalAssessment;
	public static volatile SingularAttribute<Credit, Date> date;
	public static volatile SingularAttribute<Credit, Long> version;

}

