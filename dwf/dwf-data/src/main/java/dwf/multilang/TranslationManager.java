package dwf.multilang;

import dwf.multilang.domain.BaseMultilangEntity;
import dwf.multilang.domain.Translation;

public interface TranslationManager {
	<D extends BaseMultilangEntity<?>> void setTranslation(D domain, String property, String language, String value);
	<D extends BaseMultilangEntity<?>> String getTranslation(D domain, String property, String language);
	<D extends BaseMultilangEntity<?>> String getTranslation(D domain, String property, String language, boolean fallbackToDefault);
	<D extends BaseMultilangEntity<?>> Translation<D> getTranslation(D domain, String language);
}
