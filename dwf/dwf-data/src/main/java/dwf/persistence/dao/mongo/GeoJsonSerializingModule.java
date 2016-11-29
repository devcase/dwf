package dwf.persistence.dao.mongo;

import java.io.IOException;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class GeoJsonSerializingModule extends SimpleModule {

	
	public GeoJsonSerializingModule() {
		super();
		addSerializer(GeoJsonPoint.class, new GeoJsonPointSerializer());
		addSerializer(GeoJsonMultiPolygon.class, new GeoJsonMultiPolygonSerializer());
		addSerializer(GeoJsonLineString.class, new GeoJsonLineStringSerializer());
		addSerializer(GeoJsonPolygon.class, new GeoJsonPolygonSerializer());
	}

	public abstract static class GeoJsonSerializer<T extends GeoJson<?>> extends JsonSerializer<T> {
		@Override
		public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
			gen.writeStartObject();
			gen.writeStringField("type", value.getType());
			gen.writeFieldName("coordinates");
			write(value, gen, serializers);
			gen.writeEndObject();
		}
		protected abstract void write(T value, JsonGenerator gen, SerializerProvider serializers)  throws IOException;
		
		protected void writeCoordinates(GeoJsonMultiPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeStartArray();
			for (GeoJsonPolygon polygon : value.getCoordinates()) {
				writeCoordinates(polygon, gen, serializers);
			}
			gen.writeEndArray();
		}
		
		protected void writeCoordinates(GeoJsonPoint value, JsonGenerator gen, SerializerProvider serializers)  throws IOException {
			serializers.defaultSerializeValue(value.getCoordinates(), gen);
		}
		protected void writeCoordinates(GeoJsonPolygon value, JsonGenerator gen, SerializerProvider serializers)  throws IOException {
			gen.writeStartArray();
			for (GeoJsonLineString line : value.getCoordinates()) {
				writeCoordinates(line, gen, serializers);
			}
			gen.writeEndArray();
		}
		
		protected void writeCoordinates(Point value, JsonGenerator gen, SerializerProvider serializers)  throws IOException {
			gen.writeStartArray();
			gen.writeNumber(value.getX());
			gen.writeNumber(value.getY());
			gen.writeEndArray();
		}
		
		protected void writeCoordinates(GeoJsonLineString value, JsonGenerator gen, SerializerProvider serializers)  throws IOException {
			gen.writeStartArray();
			for (Point line : value.getCoordinates()) {
				writeCoordinates(line, gen, serializers);
			}
			gen.writeEndArray();
		}
	}
	
	public static class GeoJsonPointSerializer extends GeoJsonSerializer<GeoJsonPoint> {
		@Override
		protected void write(GeoJsonPoint value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			writeCoordinates(value, gen, serializers);
		}
	}
	public static class GeoJsonLineStringSerializer extends GeoJsonSerializer<GeoJsonLineString> {
		@Override
		protected void write(GeoJsonLineString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			writeCoordinates(value, gen, serializers);
		}
	}
	public static class GeoJsonPolygonSerializer extends GeoJsonSerializer<GeoJsonPolygon> {
		@Override
		protected void write(GeoJsonPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			writeCoordinates(value, gen, serializers);
		}
	}
	public static class GeoJsonMultiPolygonSerializer extends GeoJsonSerializer<GeoJsonMultiPolygon> {
		@Override
		protected void write(GeoJsonMultiPolygon value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			writeCoordinates(value, gen, serializers);
		}
	}


}
