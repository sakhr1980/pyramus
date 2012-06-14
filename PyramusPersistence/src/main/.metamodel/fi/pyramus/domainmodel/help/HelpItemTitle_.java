package fi.pyramus.domainmodel.help;

import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import java.util.Locale;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(HelpItemTitle.class)
public abstract class HelpItemTitle_ {

	public static volatile SingularAttribute<HelpItemTitle, Long> id;
	public static volatile SingularAttribute<HelpItemTitle, String> title;
	public static volatile SingularAttribute<HelpItemTitle, Date> lastModified;
	public static volatile SingularAttribute<HelpItemTitle, Date> created;
	public static volatile SingularAttribute<HelpItemTitle, Locale> locale;
	public static volatile SingularAttribute<HelpItemTitle, HelpItem> item;
	public static volatile SingularAttribute<HelpItemTitle, User> lastModifier;
	public static volatile SingularAttribute<HelpItemTitle, User> creator;

}

