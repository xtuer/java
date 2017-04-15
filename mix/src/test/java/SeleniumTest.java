import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

public class SeleniumTest {
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = new SafariDriver();
    }

    @After
    public void cleanUp() {
//        driver.quit(); // 关闭浏览器
    }

    @Test
    public void test() {
        System.out.println("-----------");
        driver.get("http://www.baidu.com");
    }
}
