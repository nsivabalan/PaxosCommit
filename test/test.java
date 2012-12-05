import java.util.UUID;

import com.google.gson.Gson;
import common.MessageWrapper;


public class test {
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		
		Class cls = MW.class;
	    String name = cls.getName(); 
	    
		MW message = new MW("HelloWorld ", name);
		message.uid = UUID.randomUUID();
		
		String s = gson.toJson(message, message.getClass());
		System.out.println(s);
	}

}
