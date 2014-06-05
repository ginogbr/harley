package mobby;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import edu.umass.cs.benchlab.har.*;
import edu.umass.cs.benchlab.har.tools.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


  public class HarTesterHeadless {
	  
	  WebDriver driver;
	  ProxyServer server;
	  String strFilePath = "myhar.har";
	  
	@BeforeSuite
	public void beforeSuite() throws Exception{
		
	}
	
	@BeforeTest
	public void startBrowser() throws Exception{
		//start the proxy server
  	    server = new ProxyServer(4444);
  	    server.start();
  	    
  	    //captures the mouse movements and navigations
  	    server.setCaptureHeaders(true);
        server.setCaptureContent(true);
        
		// get the Selenium proxy object
  	    Proxy proxy = server.seleniumProxy();
  	    
  	    // configure it as a desired capability
  	    DesiredCapabilities capabilities = new DesiredCapabilities();
  	    capabilities.setCapability(CapabilityType.PROXY, proxy);
		driver = new PhantomJSDriver(capabilities);
	}
	  
	@Parameters({"testURL","regex","verType"})
	@Test
  	public void mobTester(String testURL,String regex, String verType) throws Exception {
		
  	    // create a new HAR with the label
  	    server.newHar("ens-gino430.ensighten.com");

  	    // open page
  	    driver.get(testURL);

  	    // get the HAR data
        Har har = server.getHar();
          
        FileOutputStream fos = new FileOutputStream(strFilePath);
        har.writeTo(fos);    
          
       	File f = new File(strFilePath);
          HarFileReader r = new HarFileReader();
          HarLog log = r.readHarFile(f);
          
       // Access all elements as objects
          HarEntries entries = log.getEntries();

       // Used for loops
          List<HarEntry> hentry = entries.getEntries();
                      
       // Test out each requests
          boolean foundUrl = false;
          for (HarEntry entry : hentry) 	  
          {  	
        	  // Regex matching of searchString
        	  Pattern regpattern = Pattern.compile(regex); 
        	  //Matcher m1 = regpattern.matcher(entry.getResponse().getContent().getText());
        	  Matcher m2 = regpattern.matcher(entry.getRequest().getUrl());
        	  //System.out.println(entry.getRequest().getUrl());
   	 
        	  if (m2.find( )) {
            	  System.out.println("  ++TEST PASSED, FOUND: "+regex);
            	  System.out.println("Status code: "+entry.getResponse().getStatus());
            	  //System.out.println("Response is: "+entry.getResponse().getContent().getText());
            	  foundUrl = true;
            	  break;          	  
              }
       
          }
          if (!foundUrl){
        	  System.err.println("  --TEST FAILED:  "+regex+" not found");
          }
          
        AssertJUnit.assertTrue("Did not find pattern: "+regex, foundUrl); 

  	}
	
	@AfterTest
	public void closeBrowser() throws Exception{
		driver.quit();
		server.stop();
	}
	
	@AfterSuite
	public void shutDown() throws Exception{
		
	}	
}
