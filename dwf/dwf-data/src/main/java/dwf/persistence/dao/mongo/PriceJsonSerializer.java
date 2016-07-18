package dwf.persistence.dao.mongo;

import java.io.IOException;
import java.math.BigDecimal;

import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import dwf.persistence.embeddable.Price;

public class PriceJsonSerializer extends JsonSerializer<Price> {

	@Override
	public void serialize(Price value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField("currencyCode", value.getCurrencyCode());
		gen.writeNumberField("cents", value.getValue().unscaledValue().longValue());
		gen.writeEndObject();;
	}
	
	public static void main(String[] args) throws Exception {
		MongoClient mongoClient = new MongoClient("localhost");
		DB db =mongoClient.getDB("test_mongo");
		DBCollection coll = db.createCollection("collection1", null);
		
		coll.drop();
		//modo 4 - com jongo, mas 		
		JacksonMapper.Builder jongomapperbuilder = new JacksonMapper.Builder();
		jongomapperbuilder.addSerializer(Price.class, new PriceJsonSerializer());
		Jongo jongo = new Jongo(db, jongomapperbuilder.build());
		jongo.getCollection("collection1").insert(new Price(new BigDecimal("3.13"), "BRL"));
		

	}
}
