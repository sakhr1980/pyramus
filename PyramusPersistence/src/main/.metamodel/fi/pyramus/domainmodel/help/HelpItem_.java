package fi.pyramus.domainmodel.help;

import fi.pyramus.domainmodel.base.Tag;
import fi.pyramus.domainmodel.users.User;
import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(HelpItem.class)
public abstract class HelpItem_ {

	public static volatile SetAttribute<HelpItem, Tag> tags;
	public static volatile SingularAttribute<HelpItem, Long> id;
	public static volatile SingularAttribute<HelpItem, Date> lastModified;
	public static volatile SingularAttribute<HelpItem, Date> created;
	public static volatile SingularAttribute<HelpItem, User> lastModifier;
	public static volatile ListAttribute<HelpItem, HelpItemTitle> titles;
	public static volatile SingularAttribute<HelpItem, HelpFolder> parent;
	public static volatile SingularAttribute<HelpItem, Integer> indexColumn;
	public static volatile SingularAttribute<HelpItem, User> creator;

}

