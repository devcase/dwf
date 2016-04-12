package dwf.serialization;

public interface View {
	public static interface Summary {}
	public static interface Detail extends Summary {}
	
	public static interface Private extends Detail {}
	public static interface Mongo {}
	public static interface RestList {}
	public static interface RestDetails {}
}
