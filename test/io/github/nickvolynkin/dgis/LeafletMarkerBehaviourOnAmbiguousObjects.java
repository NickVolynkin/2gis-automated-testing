package io.github.nickvolynkin.dgis;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;

import java.util.concurrent.TimeUnit;

/**
 * Leaflet marker behaviour on ambiguous objects.
 * */
public class LeafletMarkerBehaviourOnAmbiguousObjects {
    //DATA
    String searchString = "главный вокзал";
    /**
     * Новосибирск-Главный, железнодорожный вокзал
     */
    String firmID = "141265769369926";


    String geoID = "141373143526113";
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
    public void standardTransform() {
        driver.homepage();

        driver.firmCard.openByDirectLink(firmID);
        driver.firmCard.clickAddress();
        Vector3d czarTransform = driver.leafletMarker.getCzarTransform();
        System.out.println(czarTransform);
    }

    @Test
    public void lmbao01() throws Exception {
        driver.homepage();
        driver.searchFor(searchString);
//        driver.searchResults.clickCategory(io.github.nickvolynkin.dgis.DGDriver.SearchResults.TRANSPORT);
        driver.searchResults.clickItem(firmID);
//        driver.findElement(By.linkText("Новосибирск-Главный, железнодорожный вокзал")).click();
//        driver.clickCurrentFirmCardAddress();
        System.out.println(driver.firmCard.getName());

        driver.firmCard.clickAddress();

        Vector3d markerTransform = driver.leafletMarker.getCzarTransform();
        String actual = markerTransform.toString();
        System.out.println(actual);
        Assert.assertTrue(markerTransform.inRange(expectedCzarTransform, 2));

        System.out.print("finished");

        Thread.sleep(180000);
    }

    @Test
    public void lmbao02() throws Exception {
        driver.homepage();
        driver.searchFor(searchString);
        driver.searchResults.clickCategory(DGDriver.SearchResults.GEO);
        driver.searchResults.clickItem(geoID);
        System.out.println(driver.geoCard.getName());
        System.out.println("clickAddress");
        driver.geoCard.clickAddress();

        System.out.println("before close");
//        Thread.sleep(5000);
        driver.geoCard.close();
        System.out.println("after close");

        driver.searchResults.clickCategory(DGDriver.SearchResults.FIRMS);
        driver.searchResults.clickItem(firmID);
//        driver.findElement(By.linkText("Новосибирск-Главный, железнодорожный вокзал")).click();
//        driver.clickCurrentFirmCardAddress();
        System.out.println(driver.firmCard.getName());

        driver.firmCard.clickAddress();

        Thread.sleep(1000);
        Vector3d markerTransform = driver.leafletMarker.getCzarTransform();


        String actual = markerTransform.toString();
        System.out.println(actual);
        Assert.assertTrue(markerTransform.inRange(expectedCzarTransform, 2));

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
