package io.github.nickvolynkin.dgis;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.nickvolynkin.dgis.DGDriver.SearchResults.*;

import java.util.concurrent.TimeUnit;

/**
 * Leaflet marker behaviour on ambiguous objects.
 */
public class LeafletMarkerTests {

    public static final int ALLOWED_RANGE = 5;
    private static final Logger LOG = LoggerFactory.getLogger(LeafletMarkerTests.class);

    //DATA
    /**
     * используемый поисковый запрос
     */
    String searchString;
    /**
     * ID организации
     */
    String firmID;
    /**
     * ID здания
     */
    String geoID;
    Vector3d expectedCzarTransform;
    //END OF DATA


    private DGDriver driver;
    //    private StringBuffer verificationErrors = new StringBuffer();
    private boolean acceptNextAlert = true;

    @Before
    public void setUp() throws Exception {
        driver = new DGDriver(); //extends FirefoxDriver
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
        LogManager.getRootLogger().setLevel(Level.INFO);

        searchString = "главный вокзал";
        firmID = "141265769369926";
        geoID = "141373143526113";
        expectedCzarTransform = new Vector3d(769, 289, 0);
    }

//    @Test
    public void standardTransform() {
        driver.homepage();

        driver.firmCard.openByDirectLink(firmID);
        driver.firmCard.clickAddress();
        Vector3d czarTransform = driver.leafletMarker.getCzarTransform();
        System.out.println(czarTransform);
    }

    @Test
    public void lmbao01() {
        LOG.info("lmbao-01 started");

        driver.homepage();
        driver.searchFor(searchString);
        driver.searchResults.clickItem(firmID);
        LOG.info("actual firm name: " + driver.firmCard.getName());
        driver.firmCard.clickAddress();

        assertLeafletMarkerPosition(expectedCzarTransform);

        LOG.info("lmbao-01 finished");

//        Thread.sleep(180000);
    }

    private void assertLeafletMarkerPosition(Vector3d expectedTransform) {
        Vector3d markerTransform = driver.leafletMarker.getCzarTransform();
        LOG.info("actual transform3d: " + markerTransform.toString());
        LOG.info("expected transform3d: " + expectedTransform.toString());
        Assert.assertTrue(markerTransform.inRange(expectedTransform, ALLOWED_RANGE));
    }

    @Test
    public void lmbao02() {
        driver.homepage();

        driver.searchFor(searchString);
        driver.searchResults.clickCategory(GEO);
        driver.searchResults.clickItem(geoID);

        driver.geoCard.clickFirm(firmID);
        driver.firmCard.clickAddress();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        assertLeafletMarkerPosition(expectedCzarTransform);
    }

    @Test
    public void lmbao03() {
        LOG.info("lmbao-03 started");

        driver.homepage();

        driver.searchFor(searchString);
        driver.searchResults.clickCategory(GEO);
        driver.searchResults.clickItem(geoID);
        LOG.info(driver.geoCard.getName());

        driver.geoCard.clickAddress();

        driver.searchResults.clickCategory(FIRMS);
        driver.searchResults.clickItem(firmID);
//        driver.findElement(By.linkText("Новосибирск-Главный, железнодорожный вокзал")).click();
//        driver.clickCurrentFirmCardAddress();
        LOG.info(driver.firmCard.getName());

        driver.firmCard.clickAddress();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Vector3d markerTransform = driver.leafletMarker.getCzarTransform();


        assertLeafletMarkerPosition(expectedCzarTransform);

        LOG.info("lmbao-03 finished");
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
