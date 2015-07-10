package dwf.upload.image;

import java.io.IOException;
import java.io.Serializable;

public interface ImageResizer {
	void resizeImage(Serializable id, String entityName, String property) throws IOException;
}
