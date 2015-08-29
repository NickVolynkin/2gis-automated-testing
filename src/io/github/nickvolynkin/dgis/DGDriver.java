package io.github.nickvolynkin.dgis;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nick Volynkin nick.volynkin@gmail.com
 */
public class DGDriver extends FirefoxDriver {
    public static final String baseUrl = "http://2gis.ru/novosibirsk";
    private static final Logger LOG = LoggerFactory.getLogger(DGDriver.class);
    private static final String translateX = "translate3d\\((.*?)px,";
    private static final Pattern xPattern = Pattern.compile(translateX);

    private static final String translateY = "translate3d\\([0-9]+px, (.*?)px,";
    private static final Pattern yPattern = Pattern.compile(translateY);

    public final SearchResults searchResults = new SearchResults();
    public final Card firmCard = new Card("firmCard__name", "firmCard__addressLink", "/firm/");
    public final Card geoCard = new Card("geoCard2__name", "geoCard2__addressLink", "/geo/");
    public final LeafletMarker leafletMarker = new LeafletMarker();

    public void searchFor(final String searchString) /*throws InterruptedException*/ {
        findElement(By.name("search[query]")).clear();
        findElement(By.name("search[query]")).sendKeys(searchString);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        findElement(By.name("search[query]")).submit();
        LOG.info("Submit");
    }

    public void homepage() {
        get(baseUrl);
    }

    private WebElement getFirstOccurrence(String className, String attributeName, String searchedValue) {
        for (WebElement element : findElements(By.className(className))) {
            String actualValue = element.getAttribute(attributeName);
//                System.out.println(element + " – " + actualValue + ".");
            if (actualValue.contains(searchedValue)) {
                return element;
            }
        }

        throw new IllegalStateException("no link was found of class " + className + " where attribute " + attributeName + " equals " + searchedValue);
    }

    public class LeafletMarker {

        public Vector3d getCzarTransform() {
            WebElement leafletMapPane = findElement(By.className("leaflet-map-pane"));
            Vector3d mapPaneTransform = leafletMarker.getTransform3d(leafletMapPane);
            //        System.out.println(mapPaneTransform);

            WebElement leafletMarker = findElement(By.className("_czar"));
            Vector3d markerTransform = getTransform3d(leafletMarker);

            return mapPaneTransform.plus(markerTransform);
        }

        public Vector3d getTransform3d(final WebElement webElement) {
            String elementStyle = webElement.getAttribute("style");
            //        System.out.println(elementStyle + " " + webElement);

            int x = getTransformDimension(elementStyle, xPattern);
            int y = getTransformDimension(elementStyle, yPattern);
            return new Vector3d(x, y, 0);
        }

        private int getTransformDimension(final String style, final Pattern dimensionPattern) {
            final int dimension;

            Matcher matcher = dimensionPattern.matcher(style);
            if (matcher.find()) {
                dimension = Integer.parseInt(matcher.group(1));
            } else {
                throw new IllegalStateException("translate3d(dimension) not found in " + style);
            }
            return dimension;
        }
    }

    public class Card {

//        public static final String NAME_HEADER_CLASS = "firmCard__name";
//        public static final String ADDRESS_LINK_CLASS = "firmCard__addressLink";
//        public static final String URL_ID_PREFIX = "/firm/";

        public final String NAME_HEADER_CLASS;
        public final String ADDRESS_LINK_CLASS;
        public final String URL_ID_PREFIX;

        private Card(final String NAME_HEADER_CLASS, final String ADDRESS_LINK_CLASS, final String URL_ID_PREFIX) {
            this.NAME_HEADER_CLASS = NAME_HEADER_CLASS;
            this.ADDRESS_LINK_CLASS = ADDRESS_LINK_CLASS;
            this.URL_ID_PREFIX = URL_ID_PREFIX;
        }


        public void clickAddress() {
            LOG.info("Card.clickAddress()");
            findElement(By.className(ADDRESS_LINK_CLASS)).click();
        }

        public void clickFirm(String key) {

            String firmClass = "geoCard2__listItemNameLink";
            String firmAttribute = "href";
            WebElement searched = getFirstOccurrence(firmClass, firmAttribute, key);
            searched.click();
        }

        public void close() {
            LOG.info("Card.close()");
            List<WebElement> controlButtons = findElements(By.className("frame__controlsButton"));
            for (WebElement webElement : controlButtons) {
                System.out.println(webElement);
                String aClass = webElement.getAttribute("class");
                System.out.println(aClass);
                if (aClass.contains("_close")) {
                    webElement.click();
                    return;
                }
            }
        }

        public String getName() {
            return findElement(By.className(NAME_HEADER_CLASS)).getText();
        }

        public void openByDirectLink(String objectID) {
            LOG.info("Card.openByDirectLink()");
            //        http://2gis.ru/novosibirsk/firm/141265769369926/
            get(baseUrl + URL_ID_PREFIX + objectID);
        }
    }

    public class SearchResults {
        public static final String TRANSPORT = "transport";
        public static final String GEO = "geo";
        public static final String FIRMS = "firms";


        public void clickCategory(String key) {
            String categoryClass = "wizard__moreLink";
            String categoryAttribute = "data-mode";
            WebElement searched = getFirstOccurrence(categoryClass, categoryAttribute, key);
            searched.click();
        }

        public void clickItem(String key) {
            String firmClass = "miniCard__headerTitle";
            String firmAttribute = "href";
            WebElement searched = getFirstOccurrence(firmClass, firmAttribute, key);
            searched.click();
        }


    }

}
