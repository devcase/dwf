package dwf.persistence.dao.mongo;

import java.io.IOException;

import org.springframework.data.geo.Point;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class PointArrayAsGeoJsonMultiPointDeserializer extends JsonDeserializer<Point[]> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml
	 * .jackson.core.JsonParser,
	 * com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Point[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.readValueAsTree();
		JsonNode coordinates = node.get("coordinates");

		if (coordinates != null && coordinates.isArray()) {
			Point[] array = new Point[coordinates.size()];
			for(int i = 0; i < coordinates.size(); i++) {
				JsonNode pNode = coordinates.get(i);
				array[i] = new Point(pNode.get(0).asDouble(), pNode.get(1).asDouble());
			}
			return array;
		}
		return null;
	}
}
