package dwf.persistence.dao.mongo;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import dwf.persistence.embeddable.Price;

public class PriceJsonDeserializer extends StdDeserializer<Price> {

	public PriceJsonDeserializer() {
		super(Price.class);
	}

	@Override
	public Price deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Price price = new Price();
		while(p.nextToken() == JsonToken.FIELD_NAME) {
			String name = p.getCurrentName();
			p.nextToken();
			if("currencyCode".equals(name)) {
				price.setCurrencyCode(_parseString(p, ctxt));
			} else if("cents".equals(name)) {
				Long cents = _parseLong(p, ctxt);
				if(cents != null) {
					price.setValue(new BigDecimal(BigInteger.valueOf(cents), 2, MathContext.DECIMAL32));
				}
			}
		}
		return price;
	}

}
