import java.util.UUID;

public class MW {
		String data;
		String className;
		UUID uid;
		
		public MW(String data, String className) throws ClassNotFoundException
		{
			this.data = data;
			this.className = className;
			Class<?> cls = Class.forName(className);
			System.out.println(cls.toString());
		}
	}