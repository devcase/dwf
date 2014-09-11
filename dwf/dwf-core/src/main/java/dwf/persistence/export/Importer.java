package dwf.persistence.export;

import java.io.IOException;
import java.io.InputStream;

import dwf.persistence.domain.BaseEntity;

public interface Importer<D extends BaseEntity<?>> {
	void importFromExcel(InputStream inputStream) throws IOException;
}
