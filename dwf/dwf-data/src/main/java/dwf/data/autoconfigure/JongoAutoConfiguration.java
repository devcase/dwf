package dwf.data.autoconfigure;

import java.net.UnknownHostException;

import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import dwf.persistence.utils.MongoIdModule;

@Configuration
@ConditionalOnClass(Jongo.class)
@ConditionalOnBean(MongoClient.class)
@AutoConfigureAfter(org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class)
public class JongoAutoConfiguration {
	@Autowired
	private MongoProperties properties;
	@Autowired
	private MongoClient mongoClient; 
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public Jongo jongo() throws UnknownHostException {
		DB db = mongoClient.getDB(properties.getDatabase());
		return new Jongo(db,  MongoIdModule.getMapperBuilder().build());
	}
}
