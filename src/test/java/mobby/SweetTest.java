package mobby;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SweetTest {
	@BeforeTest
	public void StartRCServer(){
		System.out.println("Starting server");
	}
	

	 @Test(dataProviderClass=mobby.SampleDataProvider.class,dataProvider="fileDataProvider")
	    public void testDataProvider(String line) {
	        //This should print each of the file content one after the other
	        //testng calls this method for each line.
	        System.out.println("SampleTest2:testDataProvider: testData got from fileDataProvider: " + line);
	    }
	
	
	@Test
	public void search1(){
		System.out.println("test SEARCH 1 here!!!");
	}
	

	

	
	@AfterTest
	public void ShutdownRCServer(){
		System.out.println("Stopping server");
	}
	
}
