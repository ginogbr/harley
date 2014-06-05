package mobby;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;


public class NoobTest {
	
	@DataProvider(name = "test1")
	public Object[][] createData1() {
	 return new Object[][] {
	   { "Cedric", new Integer(36) },
	   { "Anne", new Integer(37)},
	 };
	}

	
	@BeforeTest
	public void StartRCServer(){
		System.out.println("Starting server");
	}
	

	@Test
	@BeforeMethod()
	public void testLogin(){
		System.out.println("before method!!!");
	}
	
	
	
	@Test(dataProvider = "test1")
	public void verifyData(String n1, Integer n2){
		System.out.println("String is: "+n1+" Integer is :"+n2);
	}
	
	@Test
	public void search1(){
		System.out.println("test SEARCH 1 here!!!");
	}
	
	@Test
	public void search2(){
		System.out.println("test SEARCH 2 here!!!");
	}
	
	@Test
	@AfterMethod
	public void testLogout(){
		System.out.println("after method!!!");
	}
	
	
	
	@AfterTest
	public void ShutdownRCServer(){
		System.out.println("Stopping server");
	}
	
	

}
