package io.github.nickvolynkin.dgis;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.nickvolynkin.dgis.DGDriver.SearchResults.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Leaflet marker behaviour on ambiguous objects.
 */
@RunWith(Parameterized.class)
public class LeafletMarkerTests {

    public static final int ALLOWED_RANGE = 5;
    private static final Logger LOG = LoggerFactory.getLogger(LeafletMarkerTests.class);

    Vector3d expectedCzarTransform;
    private DGDriver driver;
    private boolean acceptNextAlert = true;

    /**
     * используемый поисковый запрос
     */
    @Parameterized.Parameter(0)
    public String searchString;

    /**
     * ID организации
     */
    @Parameterized.Parameter(1)
    public String firmID;
    /**
     * ID здания
     */
    @Parameterized.Parameter(2)
    public String geoID;

    @Parameterized.Parameter(3)
    public int expectedTransformX;

    @Parameterized.Parameter(4)
    public int expectedTransformY;

    @Parameterized.Parameter(5)
    public int expectedTransformZ;




    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                /*{searchString, firmID, geoID, x, y, z}*/
                {"главный вокзал", "141265769369926", "141373143526113", 767, 289, 0},
                {"цирк", "141265769338191", "141373143518884", 935, 289, 0},
                {"оперный", "141265769360673", "141373143521691", 767, 289, 0},
                {"старый дом", "141265769360664", "141373143532548", 767, 289, 0},
                {"сансити", "141265770417218", "141373143572328", 767, 289, 0},
        });
    }



    @Before
    public void setUp() throws Exception {
        driver = new DGDriver(); //extends FirefoxDriver
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
        LogManager.getRootLogger().setLevel(Level.INFO);

//        searchString = "главный вокзал";
//        firmID = "141265769369926";
//        geoID = "141373143526113";
        expectedCzarTransform = new Vector3d(expectedTransformX, expectedTransformY, expectedTransformZ);
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
        driver.searchResults.tryCategory(FIRMS);
        driver.searchResults.clickItem(firmID);
        LOG.info("actual firm name: " + driver.firmCard.getName());
        driver.firmCard.clickAddress();

        assertLeafletMarkerPosition(expectedCzarTransform);

        LOG.info("lmbao-01 finished");
//        try {
//            Thread.sleep(180000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
        driver.searchResults.tryCategory(GEO);
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
        driver.searchResults.tryCategory(GEO);
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
