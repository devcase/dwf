package dwf.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



/** 
 * Toda entidade a ser salva no MongoDB (usando BaseMongoDAOImpl) deve ter a anotação MongoEntity com collectionName
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface MongoEntity {
	String collectionName();
}
