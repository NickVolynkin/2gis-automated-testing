import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public class Mpbao01 {
    //DATA
    String searchString = "главный вокзал";
    String firmID = "141265769369926";
    Vector3d expectedCzarTransform = new Vector3d(769, 289, 0);
    private DGDriver driver;
    //    private StringBuffer verificationErrors = new StringBuffer();
    private boolean acceptNextAlert = true;
    //END OF DATA

    @Before
    public void setUp() throws Exception {
        driver = new DGDriver();
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);

    }

    @Test
    public void testMpbao01() throws Exception {
        driver.homepage();

        driver.searchFor(searchString);
//        driver.searchResults.clickCategory(DGDriver.SearchResults.TRANSPORT);

        driver.searchResults.clickFirm(firmID);
//        driver.findElement(By.linkText("Новосибирск-Главный, железнодорожный вокзал")).click();
//        driver.clickCurrentFirmCardAddress();
        System.out.println(driver.firmCard.getFirmName());
        driver.firmCard.clickAddress();
//        Thread.sleep(3000);

        Vector3d czarTransform = driver.getCzarMarkerTransform();


        String actual = czarTransform.toString();
        System.out.println(actual);
        Assert.assertTrue(czarTransform.inRange(expectedCzarTransform, 2));

        System.out.print("finished");

        Thread.sleep(180000);
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
//        String verificationErrorString = verificationErrors.toString();
//        if (!"".equals(verificationErrorString)) {
//            fail(verificationErrorString);
//        }
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
}
