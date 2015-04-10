package dwf.persistence.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
@Retention(RUNTIME)
 
// Override the serializer for ObjectId annotation
public @interface MongoId {

}
