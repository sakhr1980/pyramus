package fi.pyramus.domainmodel.changelog;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ChangeLogEntryProperty.class)
public abstract class ChangeLogEntryProperty_ {

	public static volatile SingularAttribute<ChangeLogEntryProperty, Long> id;
	public static volatile SingularAttribute<ChangeLogEntryProperty, ChangeLogEntry> entry;
	public static volatile SingularAttribute<ChangeLogEntryProperty, String> value;
	public static volatile SingularAttribute<ChangeLogEntryProperty, ChangeLogEntryEntityProperty> property;

}

