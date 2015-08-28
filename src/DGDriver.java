import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class DGDriver extends FirefoxDriver {

    public static final String baseUrl = "http://2gis.ru/novosibirsk";

    private static final String translateX = "translate3d\\((.*?)px,";
    private static final Pattern xPattern = Pattern.compile(translateX);

    private static final String translateY = "translate3d\\([0-9]+px, (.*?)px,";
    private static final Pattern yPattern = Pattern.compile(translateY);

    public final SearchResults searchResults = new SearchResults();
    public final FirmCard firmCard = new FirmCard();

    public void searchFor(final String searchString) throws InterruptedException {
        findElement(By.name("search[query]")).clear();
        findElement(By.name("search[query]")).sendKeys(searchString);
        Thread.sleep(1000);
        findElement(By.name("search[query]")).submit();
//        System.out.println("Submit");
    }

    public Vector3d getCzarMarkerTransform() {
        WebElement leafletMapPane = findElement(By.className("leaflet-map-pane"));
        Vector3d mapPaneTransform = getTransform3d(leafletMapPane);
//        System.out.println(mapPaneTransform);

        WebElement leafletMarker = findElement(By.className("_czar"));
        Vector3d markerTransform = getTransform3d(leafletMarker);

        return mapPaneTransform.plus(markerTransform);
    }

    public class FirmCard {
        private WebElement findAddressLink() {
            return findElement(By.className("firmCard__addressLink"));
        }

        public void clickAddress() {
            findAddressLink().click();
        }

        public String getFirmName() {
            return findElement(By.className("firmCard__name")).getText();
        }

        public void openByDirectLink(String firmNumber) {
//        http://2gis.ru/novosibirsk/firm/141265769369926/

            get(baseUrl + "/firm/" + firmNumber);
        }
    }




    public void homepage() {
        get(baseUrl);
    }

    public Vector3d getTransform3d(final WebElement webElement) {
        String elementStyle = webElement.getAttribute("style");

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

    public class SearchResults {
        public static final String TRANSPORT = "transport";
        public static final String GEO = "geo";
        public static final String FIRMS = "firms";


        public void clickCategory(String key) {
            String categoryClass = "wizard__moreLink";
            String categoryAttribute = "data-mode";
            WebElement searched = getFirstOcurrence(categoryClass, categoryAttribute, key);
            searched.click();
        }

        public void clickFirm(String key) {
            String firmClass = "miniCard__headerTitle";
            String firmAttribute = "href";
            WebElement searched = getFirstOcurrence(firmClass, firmAttribute, key);
            searched.click();
        }

        private WebElement getFirstOcurrence(String className, String attributeName, String searchedValue) {
            for (WebElement element : findElements(By.className(className))) {
                String actualValue = element.getAttribute(attributeName);
                System.out.println(element + " – " + actualValue + ".");
                if (actualValue.contains(searchedValue)) {
                    return element;
                }
            }

            throw new IllegalStateException("no link was found of class " + className + " where attribute " + attributeName + " equals " + searchedValue);
        }
    }

}
