package dwf.persistence.utils;

import java.lang.reflect.Field;

import org.jongo.marshall.jackson.JacksonIdFieldSelector;

import dwf.persistence.annotations.MongoId;
 
/**
 * Extend JacksonIdFieldSelector to look for MongoId objects so that
 * updating the ID of the object after saving it works
 *
 * @see org.jongo.marshall.jackson.JacksonMapper.Builder#withObjectIdUpdater(org.jongo.ObjectIdUpdater)
 */
public class MongoIdFieldSelector extends JacksonIdFieldSelector {
 
    @Override
    public boolean isId(Field f) {
        return hasIdAnnotation(f) || super.isId(f);
    }
 
    private boolean hasIdAnnotation(Field f) {
        return f.getAnnotation(MongoId.class) != null;
    }
}