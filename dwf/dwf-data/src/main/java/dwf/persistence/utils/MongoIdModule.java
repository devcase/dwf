package dwf.persistence.utils;

import java.io.Serializable;
import java.util.List;

import org.jongo.ReflectiveObjectIdUpdater;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.configuration.MapperModifier;
import org.jongo.marshall.jackson.oid.ObjectIdDeserializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.NameTransformer;

import dwf.persistence.annotations.MongoId;

/**
* Module to convert to and from
*/
public class MongoIdModule extends SimpleModule {

   @Override
   public void setupModule(SetupContext context) {
       context.addBeanSerializerModifier(new SerializerModifier());
       context.addBeanDeserializerModifier(new DeserializerModifier());
   }

   public static class SerializerModifier extends BeanSerializerModifier {

       @Override
       public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
           for (int i = 0; i < beanProperties.size(); i++) {
               BeanPropertyWriter beanPropertyWriter = beanProperties.get(i);
               MongoId annotation = beanPropertyWriter.getAnnotation(MongoId.class);
               if (annotation != null) {
            	   BeanPropertyWriter beanPropertyWriterRenamed = beanPropertyWriter.rename(new ToIdTransformer(beanPropertyWriter.getName()));
            	   beanPropertyWriterRenamed.assignSerializer(new MongoIdSerializer());
                   beanProperties.set(i, beanPropertyWriterRenamed);
               }
           }
           return beanProperties;
       }
   }

   public static class DeserializerModifier extends BeanDeserializerModifier {

       JsonDeserialize deserializeAnnotation;

       @JsonDeserialize(using = ObjectIdDeserializer.class)
       private static class DeserializeUsingObjectId {}

       public DeserializerModifier() {
           deserializeAnnotation = DeserializeUsingObjectId.class.getAnnotation(JsonDeserialize.class);
       }

       @Override
       public List<BeanPropertyDefinition> updateProperties(DeserializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> propDefs) {
           for (int i = 0; i < propDefs.size(); i++) {
               BeanPropertyDefinition propertyDefinition = propDefs.get(i);

               AnnotatedField field = propertyDefinition.getField();
               if (field != null && (field.hasAnnotation(MongoId.class)) || (propertyDefinition.getGetter() != null && propertyDefinition.getGetter().hasAnnotation(MongoId.class))) {
            	   field.addIfNotPresent(deserializeAnnotation);
                   propDefs.set(i, propertyDefinition.withSimpleName("_id"));
               }
           }
           return propDefs;
       }

   }

   public static class ToIdTransformer extends NameTransformer {

       private static final String ID_FIELD = "_id";

       private String originalName;

       public ToIdTransformer(String originalName) {
           this.originalName = originalName;
       }

       @Override
       public String reverse(String transformed) {
           return originalName;
       }

       @Override
       public String transform(String name) {
           return ID_FIELD;
       }

   }

   public static class MapperModder implements MapperModifier {
       @Override
       public void modify(ObjectMapper mapper) {
           mapper.registerModule(new MongoIdModule());
       }
   }
   
   /**
    * Use this for JacksonMapper setup
    */
   public static JacksonMapper.Builder getMapperBuilder() {
       return new JacksonMapper.Builder()
           .addModifier(new MongoIdModule.MapperModder())
           .withObjectIdUpdater(new ReflectiveObjectIdUpdater(new MongoIdFieldSelector()));
   }
}
