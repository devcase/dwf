package dwf.serialization;

public interface View {
	public static class Summary {}
	public static class Detail extends Summary {}
	
	public static class Private extends Detail {}
	public static class Mongo {}
	public static class RestList {}
	public static class RestDetails {}
}
