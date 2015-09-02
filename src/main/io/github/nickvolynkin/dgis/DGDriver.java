package io.github.nickvolynkin.dgis;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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


    public final SearchResults searchResults = new SearchResults();
    public final Card firmCard = new Card("firmCard__name", "firmCard__addressLink", "/firm/");
    public final Card geoCard = new Card("geoCard2__name", "geoCard2__addressLink", "/geo/");
    public final LeafletMarker leafletMarker = new LeafletMarker();


    /**
     * Search for the query. Uses the search text box (not a direct link)
     *
     * @param query the search query.
     */
    public void searchFor(final String query) /*throws InterruptedException*/ {
        LOG.info("В строку поиска ввести \"" + query + "\" и нажать кнопку поиска");
        findElement(By.name("search[query]")).clear();
        findElement(By.name("search[query]")).sendKeys(query);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        findElement(By.name("search[query]")).submit();
    }

    /**
     * Open the homepage (currently 2gis.ru/novosibirsk)
     */
    public void homepage() {
        LOG.info("Открыть страницу " + baseUrl);
        get(baseUrl);
    }

    /**
     * Get the first occurrence of a WebElement and raise an exception if it was not found.
     *
     * @param className     required element's class name
     * @param attributeName the attribute to filter by
     * @param searchedValue the required attribute value
     * @return the searched WebElement
     * @throws IllegalStateException if the element was not found, so probably a wrong page is open
     */
    private WebElement sureGetFirstOccurrence(String className, String attributeName, String searchedValue) {
        WebElement element = tryGetFirstOccurrence(className, attributeName, searchedValue);

        if (element != null) {
            return element;
        }

        throw new IllegalStateException("no element was found of class " + className +
                " where attribute " + attributeName + " equals " + searchedValue);
    }

    /**
     * Get the firs occurrence of a WebElement or null, if there are none.
     *
     * @param className     required element's class name
     * @param attributeName the attribute to filter by
     * @param searchedValue the required attribute value
     * @return the searched WebElement
     */
    private WebElement tryGetFirstOccurrence(String className, String attributeName, String searchedValue) {

        for (WebElement element : findElements(By.className(className))) {
            String actualValue = element.getAttribute(attributeName);
            if (actualValue.contains(searchedValue)) {
//                LOG.info("tryGetFirstOccurrence >> " + element);
                return element;
            }
        }

//        LOG.info("tryGetFirstOccurrence >> " + null);
        return null;
    }

    /**
     * Check if the element is present on the webpage.
     *
     * @return true if the element is present, false otherwise
     */
    private boolean isElementPresent(By by) {
        try {
            findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    /**
     * Possible categories of all objects on maps.2gis.ru
     */
    public enum SearchCategory {

        /**
         * Geography objects: buildings etc.
         */
        GEO("места на карте", "geo"),
        /**
         * Organization objects.
         */
        FIRMS("организации", "firms"),

        /**
         * Transport objects: bus stops, train stations etc.
         */
        TRANSPORT("транспорт", "transport");

        public final String name;
        public final String attribute;

        SearchCategory(String name, String attribute) {
            this.name = name;
            this.attribute = attribute;
        }


        @Override
        public String toString() {
            return name;
        }


    }

    /**
     * Instuments for operating the leaflet marker, pointing at certain objects on the map
     */
    public class LeafletMarker {
        public static final String CZAR = "_czar";
        private final Pattern transform3dPattern = Pattern.compile("translate3d\\((.*?)px, (.*?)px, (.*?)px");

        /**
         * Get the absolute map position of the Czar (big red) leaflet marker
         *
         * @return a Vector3d, describing the map position of Czar leaflet marker
         */
        public Vector3d getCzarTransform() {
            WebElement leafletMapPane = findElement(By.className("leaflet-map-pane"));
            Vector3d mapPaneTransform = leafletMarker.getTransform3d(leafletMapPane);
            //        System.out.println(mapPaneTransform);

            WebElement leafletMarker = getCzarMarker();
            Vector3d markerTransform = getTransform3d(leafletMarker);

            return mapPaneTransform.plus(markerTransform);
        }

        /**
         * Check if the Czar leaflet marker is shown on the map.
         *
         * @return true if the czar leaflet marker is shown on the map
         */
        public boolean isCzarPresent() {
            return isElementPresent(By.className(CZAR));
        }


        /**
         * Get the WebElement of the czar leaflet marker
         *
         * @return stub
         */
        public WebElement getCzarMarker() {
            return findElement(By.className(CZAR));
        }

        /**
         * stub
         *
         * @param webElement stub
         * @return stub
         */
        public Vector3d getTransform3d(final WebElement webElement) {
            String elementStyle = webElement.getAttribute("style");

            Matcher matcher = transform3dPattern.matcher(elementStyle);
            if (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                int z = Integer.parseInt(matcher.group(3));
                return new Vector3d(x, y, z);
            } else {
                throw new IllegalStateException("transform3d(dimension) not found in " + elementStyle);
            }

        }

    }

    /**
     * Instruments for operating the geo or firm card, shown for each search element.
     */
    public class Card {

        public final String NAME_HEADER_CLASS;
        public final String ADDRESS_LINK_CLASS;
        public final String URL_ID_PREFIX;

        private Card(final String NAME_HEADER_CLASS, final String ADDRESS_LINK_CLASS, final String URL_ID_PREFIX) {
            this.NAME_HEADER_CLASS = NAME_HEADER_CLASS;
            this.ADDRESS_LINK_CLASS = ADDRESS_LINK_CLASS;
            this.URL_ID_PREFIX = URL_ID_PREFIX;
        }

        /**
         * Click the address link of the card's main object
         */
        public void clickAddress() {
            WebElement addressLink = findElement(By.className(ADDRESS_LINK_CLASS));
            addressLink.click();
            LOG.info("Перейти по ссылке с адресом \"" + addressLink.getText() + "\"");
        }

        /**
         * Find and click the link of the card's contained object with a given id.
         *
         * @param id stub
         */
        public void clickFirm(String id) {

            String firmClass = "geoCard2__listItemNameLink";
            String firmAttribute = "href";
            WebElement searched = sureGetFirstOccurrence(firmClass, firmAttribute, id);
            searched.click();
            LOG.info("Выбрать элемент с названием \"" + searched.getText() + "\" из блока \"Организации в здании\"");
        }

        /**
         * Not implemented yet
         */
        private void close() {
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

        /**
         * get the name of current card's main object
         *
         * @return stub
         */
        public String getName() {
            return findElement(By.className(NAME_HEADER_CLASS)).getText();
        }

        public void openByDirectLink(String objectID) {
            //        http://2gis.ru/novosibirsk/firm/141265769369926/
            String url = baseUrl + URL_ID_PREFIX + objectID;

            LOG.info("Ввести в адресную строку адрес " + url + " и нажать Enter");
            get(url);
        }
    }


    /**
     * Instruments for operating search results
     */
    public class SearchResults {
        public static final String TRANSPORT = "transport";
        public static final String GEO = "geo";
        public static final String FIRMS = "firms";

        static final String categoryClass = "wizard__moreLink";
        static final String categoryAttribute = "data-mode";
        static final String firmClass = "miniCard__headerTitle";
        static final String firmAttribute = "href";


        /**
         * click the link for a search category
         *
         * @param category the category to switch to
         */
        public void clickCategory(SearchCategory category) {

            LOG.info("Переключиться на категорию \"" + category.name + "\"");
            WebElement searched = sureGetFirstOccurrence(categoryClass, categoryAttribute, category.attribute);
            searched.click();

        }

        /**
         * Find and click the link for a search item with a given id.
         *
         * @param id stub
         */
        public void clickItem(String id) {

            WebElement searched = sureGetFirstOccurrence(firmClass, firmAttribute, id);
            LOG.info("Из результатов поиска выбрать элемент с названием \"" + searched.getText() + "\"");
            searched.click();
        }

        /**
         * click the link for a search category, only if it's present.
         *
         * @param key stub
         */
        public void tryCategory(SearchCategory key) {
            LOG.info("Если возможно, переключиться на категорию \"" + key.name + "\"");

            WebElement searched = tryGetFirstOccurrence(categoryClass, categoryAttribute, key.attribute);
            if (searched != null) {
                searched.click();
            }
        }
    }


}
