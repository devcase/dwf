package dwf.persistence.dao.mongo;

import java.io.IOException;

import org.springframework.data.geo.Point;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GeoJsonMultiPointSerializer extends JsonSerializer<Point[]> {

	@Override
	public void serialize(Point[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeStartObject();
		gen.writeStringField("type", "MultiPoint");
		gen.writeArrayFieldStart("coordinates");
		for (Point point : value) {
			gen.writeStartArray();
			gen.writeNumber(point.getX());
			gen.writeNumber(point.getY());
			gen.writeEndArray();
		}
		gen.writeEndArray();
		gen.writeEndObject();
	}

}
