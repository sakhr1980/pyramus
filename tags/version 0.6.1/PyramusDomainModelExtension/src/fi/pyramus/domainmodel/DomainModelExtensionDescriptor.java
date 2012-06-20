package fi.pyramus.domainmodel;

import java.util.Set;

public interface DomainModelExtensionDescriptor {
  
  public abstract Set<Class<?>> getEntityClasses();

}