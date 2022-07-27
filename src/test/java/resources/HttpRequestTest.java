package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

public class HttpRequestTest {
	
	private String testDirectory = "./src/test/java/resources";
	
	@Test
	public void request_GET() throws Exception{
		
		InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
		
		
		
		
	}

}
