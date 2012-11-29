package common;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Resource {
	private String fileName;
	private File file;

	public Resource(String fileName, String path)
	{
		this.fileName = fileName;
		this.file = new File(path);
	}

	//Read contents of a file.
	public String ReadResource() throws IOException
	{
		FileInputStream stream = new FileInputStream(this.file);
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}

	}
	
	//Acquire Shared Lock
	public void AcquireSharedLock()
	{
		//TODO : Implement function.
	}
	
	//Release Shared Locks
	public void ReleaseSharedLock()
	{
		//TODO : Implement function
	}
	
	//Acquire Exclusive Lock
	public void AcquireExclusiveLock()
	{
		//TODO : Implement function
	}
	
	//Release Exclusive Lock
	public void ReleaseExclusiveLock()
	{
		//TODO : Implement Function
	}
}
