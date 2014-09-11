package dwf.persistence.export;

import java.io.IOException;
import java.io.OutputStream;

import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;

public interface Exporter<D extends BaseEntity<?>> {
	void exportAsExcel(OutputStream outputStream, ParsedMap filter) throws IOException;
}