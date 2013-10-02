import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.Platform

driver = { new FirefoxDriver() } // use firefox by default
//driver = { new ChromeDriver() }
//driver = { new HtmlUnitDriver() }
//baseUrl = "http://localhost:8088"
waiting {
    timeout = 5 // default wait is two seconds
}
environments {
    chrome {
        driver = { new ChromeDriver() }
    }
    safari {
        driver = { new SafariDriver() }
    }
    firefox {
        driver = { new FirefoxDriver() }
    }
    headless {
        driver = { new HtmlUnitDriver() }
    }
    saucelabs {
        // Login to saucelabs.com. Name: fredrikengstrom  Pwd: PqAcf3jFjpi3T9uHoMct
        driver = {
            DesiredCapabilities capabilities = DesiredCapabilities.firefox();
            capabilities.setCapability("version", "23");
            capabilities.setCapability("platform", Platform.WIN8);
            new RemoteWebDriver(
                          new URL("http://fredrikengstrom:8abb0a52-2709-4c9c-90f0-767523be1b80@ondemand.saucelabs.com:80/wd/hub"),
                          capabilities);
        }
    }
    'win-ie' {
        driver = {
            new RemoteWebDriver(new URL("http://windows.ci-server.local"), DesiredCapabilities.internetExplorer())
        }
    }
}

