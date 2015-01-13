package dwf.persistence.validation;

/**
 * Used mainly for tagging entity fields for validation and updating.
 * @author Hirata
 *
 */
public interface ValidationGroups {
	/**
	 * Utilize em constraints que serão conferidas apenas na criação, e não
	 * em updates
	 * @author Hirata
	 *
	 */
	interface MergePersist {
	}

	interface ChangePassword {
	}
	
	interface ImportFromFile {
	}

}
