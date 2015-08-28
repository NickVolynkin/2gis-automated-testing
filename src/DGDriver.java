import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class DGDriver extends FirefoxDriver {

    private void searchFor(final String searchString) throws InterruptedException {
        findElement(By.name("search[query]")).clear();
        findElement(By.name("search[query]")).sendKeys(searchString);
        Thread.sleep(1000);
        findElement(By.name("search[query]")).submit();
//        System.out.println("Submit");
    }

}
