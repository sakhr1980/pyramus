package fi.pyramus.domainmodel.grading;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TransferCreditTemplate.class)
public abstract class TransferCreditTemplate_ {

	public static volatile SingularAttribute<TransferCreditTemplate, Long> id;
	public static volatile ListAttribute<TransferCreditTemplate, TransferCreditTemplateCourse> courses;
	public static volatile SingularAttribute<TransferCreditTemplate, String> name;
	public static volatile SingularAttribute<TransferCreditTemplate, Long> version;

}

