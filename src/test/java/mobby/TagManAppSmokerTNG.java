package mobby;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class TagManAppSmokerTNG {
	private WebDriver driver;
	private boolean acceptNextAlert = true;
	//private StringBuffer verificationErrors = new StringBuffer();
	
	// Read from config.properties in /resources
	Config cfg = new Config();
	
	String baseUrl = cfg.getProperty("baseUrl");
	String useraccount = cfg.getProperty("useraccount");
	String username = cfg.getProperty("username");
	String userpassword = cfg.getProperty("userpassword");
	boolean saveApp = Boolean.valueOf(cfg.getProperty("saveApp"));
	String namePrefix = cfg.getProperty("namePrefix");
	String space = cfg.getProperty("space");
	
	
	@BeforeTest
	public void setUp() throws Exception {
		  
	    driver = new FirefoxDriver();
	 	driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	    driver.manage().window().maximize();
	    driver.get(baseUrl + "#apps");
	    manageLogin(useraccount,username,userpassword);
	    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

	  }

	  @Parameters({"filePath"})
	  @Test
	  public void testTagManApps(String filePath) throws Exception {
		  System.out.println("filePath is: "+filePath);
		  
		  //  Get basePath
		  String basePath = (new File(".").getCanonicalPath());
		  String fullPath = basePath+filePath;
		  

		  // Get data from the JSON file
	    JSONParser parser = new JSONParser();
	    	 
		try {			
			Object obj = parser.parse(new FileReader(fullPath));
			JSONObject jsonObject = (JSONObject) obj;	
			
			// Get productName
			String productName = (String) jsonObject.get("productName");
			
			// Get code
			// TODO: Regex out portion of code after http:// and in between \'....\' or "+"
			// ie: "http:\/\/\") + \'js.clickequations.net\/CLEQ_\' + 
			JSONObject defTagTemp = (JSONObject) jsonObject.get("defaultTagTemplateVersion");
			String code1 = (String) defTagTemp.get("code");
				
		if (isElementPresent(By.xpath("//div[@appname='"+productName+"']"))){
				
		openAppByName(productName);
									
		    //Only continue if you find appName
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		    String appTitle = driver.findElement(By.cssSelector("span.appName")).getText();
		    
		    if (appTitle.contains(productName)){

			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			
		    driver.switchTo().frame(0);

			// Loop array to get all keys
			JSONArray tagConfigAttributesArr = (JSONArray) defTagTemp.get("tagConfigAttributes");
			for (int i = 0; i < tagConfigAttributesArr.size(); i++) 
			{
				JSONObject tagAttrObj = (JSONObject)tagConfigAttributesArr.get(i);
				
				// Exit loop since we don't create all the custom fields beyond first one.
				if(tagAttrObj.get("placeholder").equals("param_name_2")){
					break;
				}
				
				// Test value - inputValue
					String inputValue = tagAttrObj.get("inputValue").toString();
		        
		        // Field name - placeholder
					String placeholder = tagAttrObj.get("placeholder").toString();
		        
				// Drop down selector of field - placeholder_sel
					String placeholder_sel = tagAttrObj.get("placeholder")+"_sel".toString(); 			
			
				// Drop down value
					String ddinputtype = tagAttrObj.get("inputType").toString();
					
					// Selenium find and populate each field
					boolean present;
					try {
						driver.findElement(By.id(placeholder));
						present = true;
					} catch (NoSuchElementException e) {
						   present = false;
							}
								
					// Populate fields 
					if (present == true){
							// Changes to "text" on dropdown
							new Select(driver.findElement(By.id(placeholder_sel))).selectByVisibleText(ddinputtype);
							driver.findElement(By.id((String) placeholder)).sendKeys((String) inputValue);
							}
				
					else{
						// close window and throw error
					}									
				}
				
				// Switch back to default page
			    driver.switchTo().defaultContent();
			    
			 // Go to step 3 and enter app name.
			    driver.findElement(By.cssSelector("button.btn.nextBtn")).click();
			    
			 // TODO:  An error should appear here if there are missing fields. Read value from 
			 // json file to see if this is expected, if not, throw error. "negativeTest" maybe?
			    
			    
			    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			    driver.findElement(By.cssSelector("button.btn.nextBtn")).click();
			    driver.findElement(By.id("deploymentName")).clear();
			    
			    // Add unique app name.
			    String productNameGen = namePrefix+productName+System.currentTimeMillis();
			    driver.findElement(By.id("deploymentName")).sendKeys(productNameGen);
			    	    
			    // This will click "Space" then select space from drop down (calls selectValueFromUnordedList).
			    driver.findElement(By.className("chosen-single")).click();
			    WebElement ul = driver.findElement(By.className("chosen-results"));
			    selectValueFromUnorderedList(ul, space);
			    
			    driver.findElement(By.cssSelector("button.nextBtn.btn")).click();
			    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			    
			    //TODO: Read in section of code here for verification.
			    // Left off here 7/1/14
				//String baseURL = jsonObject.get("baseURL").toString();
							    
		//TODO: Try scrolling into view Bootstrapper=========
			    
			   /* WebElement element = driver.findElement(By.cssSelector("div.ace_scroller"));
			    String p = ((Locatable)element).getLocationOnScreenOnceScrolledIntoView();*/
		// ======================
			    
   
		// TODO: Check if baseURL is found in generated tag code.  
			if(driver.getPageSource().contains("pixel.adacado.com")){
					System.out.println("Found: "+productName);
				}
			else{
					System.out.println("Testing: "+productName);
				}	
				
	    
		// Saves application if saveApp is set else dismiss window.
			if(saveApp){
				driver.findElement(By.cssSelector("button.deployBtn.btn.btn-default")).click();
				driver.findElement(By.cssSelector("button.newDeployment.btn.btn-default")).click();
				//driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
				driver.findElement(By.cssSelector("input.userCheckBox")).click();
				driver.findElement(By.cssSelector("button.enableBtn")).click();
				WebDriverWait wait = new WebDriverWait(driver, 8);
				WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.alert-success")));
				//TODO: Enable once env. working.
				//driver.findElement(By.cssSelector("button.commitBtn")).click();
				//driver.findElement(By.cssSelector("button.publishBtn")).click();
			   }
			else{
				driver.findElement(By.className("modalCloseButton")).click();
				driver.findElement(By.xpath("//div[7]/div[2]/div[2]/div")).click();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				}	    
		
	}
	
	else{
		System.err.println("FIELDS NOT FOUND");
	    driver.switchTo().defaultContent();
		driver.findElement(By.cssSelector("button.modalCloseButton.close")).click();
	    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
	    driver.findElement(By.xpath("//div[7]/div[2]/div[2]/div")).click();
	    System.out.println("=======================");
	}

		}
		
			else{
				  System.err.println(productName+" not found on page");
				  System.out.println("=======================");
			  }	

		}

		 catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
	    } catch (ParseException e) {
	 		e.printStackTrace();
	    }      
		
		//driver.close();
		//driver.quit();
	  }

	public void manageLogin(String useraccount, String username, String userpassword) {
		driver.findElement(By.id("account")).clear();
	    driver.findElement(By.id("account")).sendKeys(useraccount);
	    driver.findElement(By.id("username")).clear();
	    driver.findElement(By.id("username")).sendKeys(username);
	    driver.findElement(By.id("password")).clear();
	    driver.findElement(By.id("password")).sendKeys(userpassword);
	    driver.findElement(By.cssSelector("button.btn.loginButton")).click();
	}


	  private boolean isElementPresent(By by) {
	    try {
	      driver.findElement(by);
	      return true;
	    } catch (NoSuchElementException e) {
	      return false;
	    }
	  }

	  private boolean isAlertPresent() {
	    try {
	      driver.switchTo().alert();
	      return true;
	    } catch (NoAlertPresentException e) {
	      return false;
	    }
	  }
	  
	  public boolean isTextPresent(String text) {
		    try {
		    	driver.findElement(By.id("codeDetail")).getText().contains(text);	
		    	System.out.println("=======>"+driver.findElement(By.cssSelector("div.ace_content")).getText());
		    	return true;
		    } catch (NoSuchElementException e) {
		      return false;
		    }
		  }

	  private String closeAlertAndGetItsText() {
	    try {
	      Alert alert = driver.switchTo().alert();
	      String alertText = alert.getText();
	      if (acceptNextAlert) {
	        alert.accept();
	      } else {
	        alert.dismiss();
	      }
	      return alertText;
	    } finally {
	      acceptNextAlert = true;
	    }
	  }
	  
	  // Grab all items from a drop down list 
	  public void selectValueFromUnorderedList(WebElement unorderedList, final String value) {
		    List<WebElement> options = unorderedList.findElements(By.tagName("li"));

		    for (WebElement option : options) {
		        if (value.equals(option.getText())) {
		            option.click();
		            break;
		        }
		    }
		}
	  
	//  Open app by using productName swimlane updates
		  public void openAppByName(String productName){
			  
			 // Show all apps
			 driver.get(baseUrl + "#apps");
			 driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

			 driver.findElement(By.cssSelector("span.caret")).click();
			 driver.findElement(By.cssSelector("div.appCategorySelectorCheckbox")).click();
			 
			 driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			 
			// Enter productName on search field.
			 driver.findElement(By.id("search")).sendKeys(productName);
			 driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			    
			 driver.findElement(By.xpath("//div[@appname='"+productName+"']")).click();			 	
			 driver.findElement(By.xpath("//div[@appname='"+productName+"']/div/div/span/span/button")).click();
			 }  
	  
	public void testEnabledeploy(String xproductName) throws Exception {
		  	driver.get(baseUrl + "#deploy");
		  	driver.switchTo().defaultContent();
		  	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		  	driver.findElement(By.linkText("Deployments")).click();
		    driver.findElement(By.cssSelector("input[type=\"text\"]")).clear();
		    driver.findElement(By.cssSelector("input[type=\"text\"]")).sendKeys(xproductName);
		    driver.findElement(By.cssSelector("input[type=\"text\"]")).sendKeys(Keys.RETURN);
		  	driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		    driver.findElement(By.cssSelector("input.userCheckBox")).click();
		    driver.findElement(By.name("Enable")).click();
		  	driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		    driver.findElement(By.cssSelector("input.userCheckBox")).click();
		    driver.findElement(By.name("Commit")).click();
		  }

	
	@AfterTest
	public void closeBrowser() throws Exception{
		driver.close();
	}
	
	@AfterSuite
	public void shutDown() throws Exception{
		driver.close();
		driver.quit();
	}
	
	/*public void tearDown() throws Exception {
	    //driver.quit();
	    String verificationErrorString = verificationErrors.toString();
	    if (!"".equals(verificationErrorString)) {
	      Assert.fail(verificationErrorString);
	    }
	  }*/
}

