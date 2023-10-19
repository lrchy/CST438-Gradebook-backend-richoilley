package com.cst438;

import java.util.List;


import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndToEndTestAssignments {
	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver/chromedriver.exe";
	public static final String URL = "http://localhost:3000";
	public static final int SLEEP_DURATION = 1000;
    public static final String TEST_ASSIGNMENT_NAME = "db design JunitTest";
    
    
    @Test
    public void createAssignment() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10); // 10 seconds of timeout
        
        try {
            driver.get(URL);
            
            // Trigger assignment creation
            wait.until(ExpectedConditions.elementToBeClickable(By.id("createAssignment"))).click();
            
            // Set assignment information
            wait.until(ExpectedConditions.elementToBeClickable(By.id("courseId"))).sendKeys("31045");
            driver.findElement(By.id("name")).sendKeys(TEST_ASSIGNMENT_NAME); // I assume there's a field to set the assignment name
            driver.findElement(By.id("dueDate")).sendKeys("31122023");
            driver.findElement(By.id("submit")).click();
            
            // Verify creation
            WebElement assignmentRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td='" + TEST_ASSIGNMENT_NAME + "']")));
            assertNotNull(assignmentRow, TEST_ASSIGNMENT_NAME + " not found in the assignment list.");
        
        } catch (Exception ex) {
            throw ex;
        } finally {
            driver.quit();
        
        }
    }
    
    @Test
    public void updateAssignment() throws Exception{
    	System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
    	WebDriver driver = new ChromeDriver();
    	WebDriverWait wait = new WebDriverWait(driver,10); 
    	
    	try {
    		driver.get(URL);
    		
    		// Edit page
    		WebElement assignmentEditLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td='"+ TEST_ASSIGNMENT_NAME + "']/td/a[contains(text(), 'Edit')]")));
    		assignmentEditLink.click();
    		
    		// Update assignment info
    		WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("updatedName")));
    		nameInput.clear();
    		nameInput.sendKeys(TEST_ASSIGNMENT_NAME + " Updated"); //appending Updated
    		
    		WebElement dueDateInput = driver.findElement(By.id("updatedDate"));
    		dueDateInput.clear();
    		dueDateInput.sendKeys("01012024"); //updating the date
    		
    		WebElement updateButton = driver.findElement(By.id("update"));
    		updateButton.click();
    		
    		// Click "back" button after update, to go back to assignment list
    		driver.findElement(By.id("backToList")).click();
    		
    		// Verify
    		WebElement updatedAssignmentRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td='"+ TEST_ASSIGNMENT_NAME + " Updated" + "']")));
    		assertNotNull(updatedAssignmentRow, TEST_ASSIGNMENT_NAME + " Updated" + " not found in the assignment list.");
    	
    	} catch (Exception ex) {
            throw ex;	
    	} finally {
    		driver.quit();
    		
    	}
    }
    
    @Test
    public void deleteAssignment() throws Exception {
    	System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 10);
        
        try {
        	driver.get(URL);
        	Thread.sleep(SLEEP_DURATION);
        	
        	// Find the row with the test assignment and delete it 
        	driver.findElement(By.xpath("//button[text()='Delete']")).click();
            Thread.sleep(SLEEP_DURATION);

            // Confirm the delete action in the alert dialog.
            
            wait.until(ExpectedConditions.alertIsPresent());
            Alert confirmationAlert = driver.switchTo().alert();
            confirmationAlert.accept();

        	
        	// Wait 
        	Thread.sleep(SLEEP_DURATION);
        	
        	//verify that the assignment is deleted
        	assertThrows(NoSuchElementException.class, () -> {
                driver.findElement(By.xpath("//tr[td='"+ TEST_ASSIGNMENT_NAME + " Updated" + "']"));
            });
        
        } catch (Exception ex) {
            throw ex;
        } finally {
        	driver.quit();
        
        }
    }
}
