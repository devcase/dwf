package dwf.persistence.utils;

import java.io.IOException;

import org.jongo.marshall.jackson.oid.ObjectIdSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
* Wrapper for ObjectIdSerializer so that it can be applied on generic objects
*/
public class MongoIdSerializer extends JsonSerializer<Object> {

   private static ObjectIdSerializer objectIdSerializer = new ObjectIdSerializer();

   @Override
   public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
	   if (!(value instanceof String))
		   	throw new IOException("ID de objeto Mongo deve ser String!");
       objectIdSerializer.serialize((String) value, jgen, provider);
   }
}
