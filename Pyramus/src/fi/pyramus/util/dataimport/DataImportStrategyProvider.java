package fi.pyramus.util.dataimport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fi.pyramus.dao.StudentDAO;
import fi.pyramus.domainmodel.base.Address;
import fi.pyramus.domainmodel.base.ContactInfo;
import fi.pyramus.domainmodel.base.Email;
import fi.pyramus.domainmodel.base.PhoneNumber;
import fi.pyramus.domainmodel.courses.Course;
import fi.pyramus.domainmodel.courses.CourseStudent;
import fi.pyramus.domainmodel.modules.Module;
import fi.pyramus.domainmodel.students.AbstractStudent;
import fi.pyramus.domainmodel.students.Student;
import fi.pyramus.domainmodel.users.User;

@SuppressWarnings("rawtypes")
public class DataImportStrategyProvider {

  protected DataImportStrategyProvider() {
  }
  
  public static DataImportStrategyProvider instance() {
    return _instance;
  }
  
  public void registerFieldHandler(Class entityClass, String fieldName, FieldHandlingStrategy strategy) {
    EntityFieldHandlerProvider entityHandler = getEntityFieldHandlerProvider(entityClass);

    if (entityHandler == null) {
      entityHandler = new EntityFieldHandlerProvider();
      registerEntityFieldHandler(entityClass, entityHandler);
    }
    
    entityHandler.registerFieldHandler(fieldName, strategy);
  }
  
  public FieldHandlingStrategy getFieldHandler(Class entityClass, String fieldName) {
    EntityFieldHandlerProvider entityHandler = getEntityFieldHandlerProvider(entityClass);
    
    if (entityHandler != null)
      return entityHandler.getFieldHandlingStrategy(fieldName);
    else
      return null;
  }
  
  private EntityFieldHandlerProvider getEntityFieldHandlerProvider(Class c) {
    return entityFieldHandlers.get(c);
  }
  
  public EntityHandlingStrategy getEntityHandler(Class entityClass) {
    return entityHandlers.get(entityClass);
  }
  
  public void registerEntityHandler(Class entityClass, EntityHandlingStrategy entityHandler) {
    entityHandlers.put(entityClass, entityHandler);
  }

  private void registerEntityFieldHandler(Class c, EntityFieldHandlerProvider prov) {
    entityFieldHandlers.put(c, prov);
  }

  private class EntityFieldHandlerProvider {
  
    public void registerFieldHandler(String fieldName, FieldHandlingStrategy strategy) {
      fieldHandlers.put(fieldName, strategy);
    }
    
    public FieldHandlingStrategy getFieldHandlingStrategy(String fieldName) {
      return fieldHandlers.get(fieldName);
    }
    
    private Map<String, FieldHandlingStrategy> fieldHandlers = new HashMap<String, FieldHandlingStrategy>();  
  }

  private Map<Class, EntityFieldHandlerProvider> entityFieldHandlers = new HashMap<Class, EntityFieldHandlerProvider>();
  private Map<Class, EntityHandlingStrategy> entityHandlers = new HashMap<Class, EntityHandlingStrategy>();
  private static DataImportStrategyProvider _instance;
  
  static {
    _instance = new DataImportStrategyProvider();

    Class importerClass;
    Class subClass;
    
    importerClass = Student.class;
    subClass = Student.class;
    instance().registerEntityHandler(importerClass, new DefaultEntityHandlingStrategy(importerClass) {
      
      private ContactInfo getStudentContactInfo(Student student) {
        ContactInfo result = student.getContactInfo();
        if (result == null) {
          result = new ContactInfo();
          student.setContactInfo(result);
        }
        return result;
      }
      
      @Override
      public void initializeContext(DataImportContext context) {
        super.initializeContext(context);
        
        if (context.hasField("socialSecurityNumber")) {
          String ssn = context.getFieldValue("socialSecurityNumber");

          if (ssn != null) {
            StudentDAO studentDAO = new StudentDAO();
            AbstractStudent abstractStudent = studentDAO.getAbstractStudentBySSN(ssn);
            context.addEntity(AbstractStudent.class, abstractStudent);
          }
        }
      }
      
      @Override
      protected void bindEntities(DataImportContext context) {
        super.bindEntities(context);
        
        // Bind Address, Email, Phone, AbstractStudent etc. to Student or create new ones where needed
        Student student = (Student) context.getEntity(Student.class);
        
        AbstractStudent abstractStudent = (AbstractStudent) context.getEntity(AbstractStudent.class);
        if (abstractStudent == null) {
          abstractStudent = new AbstractStudent();
          context.addEntity(AbstractStudent.class, abstractStudent);
        }
        
        abstractStudent.addStudent(student);

        // Adress        
        Address address = (Address) context.getEntity(Address.class);
        if (address != null) {
          ContactInfo contactInfo = getStudentContactInfo(student);
          contactInfo.addAddress(address);
        }

        // Email
        Email email = (Email) context.getEntity(Email.class);
        if (email != null) {
          ContactInfo contactInfo = getStudentContactInfo(student);
          contactInfo.addEmail(email);
        }

        // PhoneNumber
        PhoneNumber phone = (PhoneNumber) context.getEntity(PhoneNumber.class);
        if (phone != null) {
          ContactInfo contactInfo = getStudentContactInfo(student);
          contactInfo.addPhoneNumber(phone);
        }
      }
    });
    instance().registerFieldHandler(importerClass, "firstName", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "lastName", new DefaultFieldHandingStrategy(subClass));

    // Address
    subClass = Address.class;
    instance().registerFieldHandler(importerClass, "city", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "country", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "postalCode", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "streetAddress", new DefaultFieldHandingStrategy(subClass));

    // Email
    subClass = Email.class;
    instance().registerFieldHandler(importerClass, "email", new DefaultFieldHandingStrategy(subClass, "address"));

    // Phone
    subClass = PhoneNumber.class;
    instance().registerFieldHandler(importerClass, "phoneNumber", new DefaultFieldHandingStrategy(subClass, "number"));

    // AbstractStudent
    subClass = AbstractStudent.class;
    instance().registerFieldHandler(importerClass, "birthday", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "socialSecurityNumber", new DefaultFieldHandingStrategy(subClass));// new SocialSecurityNumberHandlingStrategy(subClass, true));
    instance().registerFieldHandler(importerClass, "sex", new DefaultFieldHandingStrategy(subClass));

    // Course
    importerClass = Course.class;
    subClass = Course.class;
    instance().registerEntityHandler(importerClass, new DefaultEntityHandlingStrategy(importerClass) {
      @Override
      protected void bindEntities(DataImportContext context) {
        super.bindEntities(context);

        Date now = new Date();
        User user = context.getLoggedUser();
        
        Course course = (Course) context.getEntity(Course.class);
        course.setCreated(now);
        course.setLastModified(now);
        course.setCreator(user);
        course.setLastModifier(user);
        
        Module module = new Module();
        module.setName(course.getName());
        module.setDescription(course.getDescription());
        module.setCreated(course.getCreated());
        module.setLastModified(course.getLastModified());
        module.setCreator(course.getCreator());
        module.setLastModifier(course.getLastModifier());
        
        course.setModule(module);
        
        context.addEntity(Module.class, module);
      }
    });
    instance().registerFieldHandler(importerClass, "name", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "description", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "beginDate", new DefaultFieldHandingStrategy(subClass));
    instance().registerFieldHandler(importerClass, "endDate", new DefaultFieldHandingStrategy(subClass));

    // CourseStudent
    importerClass = CourseStudent.class;
    subClass = CourseStudent.class;
    instance().registerEntityHandler(importerClass, new DefaultEntityHandlingStrategy(importerClass) {
      @Override
      protected void bindEntities(DataImportContext context) {
        super.bindEntities(context);
        CourseStudent courseStudent = (CourseStudent) context.getEntity(CourseStudent.class);
        courseStudent.setEnrolmentTime(new Date());
      }      
    });
    instance().registerFieldHandler(importerClass, "student", new ReferenceFieldHandlingStrategy(subClass, "student", Student.class));
    instance().registerFieldHandler(importerClass, "course", new ReferenceFieldHandlingStrategy(subClass, "course", Course.class));
  }
}