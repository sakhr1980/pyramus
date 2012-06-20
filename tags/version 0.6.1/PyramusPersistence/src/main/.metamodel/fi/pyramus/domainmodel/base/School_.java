package fi.pyramus.domainmodel.base;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(School.class)
public abstract class School_ {

	public static volatile SingularAttribute<School, SchoolField> field;
	public static volatile SetAttribute<School, Tag> tags;
	public static volatile SingularAttribute<School, Long> id;
	public static volatile SingularAttribute<School, Boolean> archived;
	public static volatile SingularAttribute<School, ContactInfo> contactInfo;
	public static volatile SingularAttribute<School, String> name;
	public static volatile SingularAttribute<School, String> code;
	public static volatile SingularAttribute<School, Long> version;
	public static volatile ListAttribute<School, SchoolVariable> variables;

}

