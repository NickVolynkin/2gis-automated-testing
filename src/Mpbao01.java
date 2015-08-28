import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

public class Mpbao01 {

    private WebDriver driver;
    private String baseUrl;
    private boolean acceptNextAlert = true;
    private StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new DGDriver();
        baseUrl = "http://2gis.ru/novosibirsk";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

    }

    @Test
    public void testMpbao011() throws Exception {
        driver.get(baseUrl);
        final String searchString = "главный вокзал";
        searchFor(searchString);

        driver.findElement(By.linkText("Новосибирск-Главный, железнодорожный вокзал")).click();
        findFirmCardAddressLink().click();
        Thread.sleep(3000);

        List<WebElement> leafletMapPanes = driver.findElements(By.className("leaflet-map-pane"));

        System.out.println(leafletMapPanes.size());

        WebElement leafletMapPane = driver.findElement(By.className("leaflet-map-pane"));
        String mapPaneStyle = leafletMapPane.getAttribute("style");
        System.out.println(leafletMapPane.getAttribute("class"));
        System.out.println("leaflet-map-pane style= " + mapPaneStyle);

        WebElement leafletMarker = driver.findElement(By.className("_czar"));
        String markerStyle = leafletMarker.getAttribute("style");
        System.out.println("leaflet-marker style= " + markerStyle);


        int markerX = getTransform3dX(markerStyle);
        int markerY = getTransform3dY(markerStyle);
        int mapPaneX = getTransform3dX(mapPaneStyle);
        int mapPaneY = getTransform3dY(mapPaneStyle);
        int absX = markerX + mapPaneX;
        int absY = markerY + mapPaneY;

        System.out.println("absX: " + absX + ", absY: " + absY);


         mapPaneStyle = leafletMapPane.getAttribute("style");
        System.out.println(leafletMapPane.getAttribute("class"));
        System.out.println("leaflet-map-pane style= " + mapPaneStyle);

        System.out.print("finished");

        Thread.sleep(180000);
        //    try {
        //      assertEquals("", driver.findElement(By.xpath("//div[@id='module-1-6']/div/div[7]/div")).getText());
        //    } catch (Error e) {
        //      verificationErrors.append(e.toString());
        //    }
    }

    private void searchFor(final String searchString) throws InterruptedException {
        driver.findElement(By.name("search[query]")).clear();
        driver.findElement(By.name("search[query]")).sendKeys(searchString);
        Thread.sleep(1000);
        driver.findElement(By.name("search[query]")).submit();
        System.out.println("Submit");
    }

    private WebElement findFirmCardAddressLink() {
        return driver.findElement(By.className("firmCard__addressLink"));
    }

    private void openFirmCard(String firmNumber) {
//        http://2gis.ru/novosibirsk/firm/141265769369926/

        driver.get(baseUrl + "/firm/" + firmNumber);
    }

    private int getTransform3dX(final String style) {
        final int x;
        String translateX = "translate3d\\((.*?)px,";
        Pattern patternX = Pattern.compile(translateX);

        Matcher matcher = patternX.matcher(style);
        if (matcher.find()) {
            x = Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalStateException("translate3d(x) not found");
        }
        return x;
    }

    private int getTransform3dY(final String style) {
        final int y;
        String translateY = "translate3d\\([0-9]+px, (.*?)px,";
        Pattern patternY = Pattern.compile(translateY);

        Matcher matcher = patternY.matcher(style);
        if (matcher.find()) {
            y = Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalStateException("translate3d(y) not found");
        }
        return y;
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
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
