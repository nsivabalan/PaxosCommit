package common;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Resource {
	private String fileName;
	private File file;
	private FileReader fileReader;
	private PrintWriter fileWriter;

	public Resource(String fileName, String path) throws IOException
	{
		this.fileName = fileName;
		this.file = new File(path + "/" + fileName);
		this.fileReader = new FileReader(this.file);
		
	}

	//Read contents of a file.
	public String ReadResource(int readLineNumber) throws IOException
	{
		StringBuilder data = new StringBuilder();
		LineNumberReader lnr = null;

		try {
			lnr = new LineNumberReader(this.fileReader);
			String line = lnr.readLine();  

			while (line != null && lnr.getLineNumber() <= readLineNumber ) {   
				data.append(line);
				line = lnr.readLine();  
			}
		}
		finally {
			lnr.close();
		}
		return data.toString();
	}
	
	//Write contents to a file.
	public void AppendtoResource(String data)
	{
		try 
		{
			fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.file, true)));
		    fileWriter.println(data);
		    fileWriter.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		    //TODO : Add to log.			
		}
	}
}
