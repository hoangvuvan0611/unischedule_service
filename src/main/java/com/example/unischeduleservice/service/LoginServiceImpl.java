package com.example.unischeduleservice.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    // Khởi tạo WebDriverManager một lần
    static {
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            logger.error("Error setting up ChromeDriver: " + e.getMessage());
        }
    }

    @Value("${url.vnua.home}")
    private String urlHome;

    @Override
    public Map<String, String> loginAndGetSessionStorage(String username, String password) {
        WebDriver driver = null;
        Map<String, String> sessionData = new HashMap<>();

        try {
            // Cấu hình Chrome options - giữ nguyên phần này
            ChromeOptions options = new ChromeOptions();
            options.setBinary("/snap/bin/chromium");
            options.addArguments(
                    "--headless",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--disable-images",
                    "--disable-css",
                    "--disable-javascript", // Có thể bỏ dòng này nếu trang cần JS
                    "--disable-plugins",
                    "--disable-extensions",
                    "--disable-logging",
                    "--disable-background-timer-throttling",
                    "--aggressive-cache-discard",
                    "--memory-pressure-off",
                    "--disable-background-networking",
                    "--disable-sync",
                    "--disable-translate",
                    "--hide-scrollbars",
                    "--mute-audio",
                    "--no-first-run",
                    "--disable-default-apps",
                    "--disable-popup-blocking"
            );

            Map<String, Object> prefs = new HashMap<>();
            prefs.put("profile.default_content_setting_values.notifications", 2);
            prefs.put("profile.managed_default_content_settings.images", 2);
            prefs.put("profile.default_content_settings.popups", 0);
            options.setExperimentalOption("prefs", prefs);

            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(8)); // Giảm timeout
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));   // Giảm wait

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            logger.info("Navigating to VNUA website...");
            driver.get(urlHome);

            // Chỉ đợi DOM ready thay vì complete
            wait.until(webDriver -> !js.executeScript("return document.readyState").equals("loading"));

            logger.info("Waiting for login form...");

            // Tìm và điền username
            WebElement usernameField = findElementWithSelectors(driver, wait, js, new String[]{
                    "input[name='username']",
                    "input[formcontrolname='username']",
                    "input[type='text']",
                    ".form-control[name='username']",
                    ".form-control[formcontrolname='username']"
            }, "username");

            if (usernameField == null) {
                throw new RuntimeException("Cannot find username field");
            }

            logger.info("Entering username...");
            fillField(js, usernameField, username);

            // Tìm và điền password
            WebElement passwordField = findElementWithSelectors(driver, wait, js, new String[]{
                    "input[name='password']",
                    "input[formcontrolname='password']",
                    "input[type='password']",
                    ".form-control[name='password']",
                    ".form-control[formcontrolname='password']"
            }, "password");

            if (passwordField == null) {
                throw new RuntimeException("Cannot find password field");
            }

            logger.info("Entering password...");
            fillField(js, passwordField, password);

            // Tìm và click nút đăng nhập
            WebElement loginButton = findLoginButton(driver, wait, js);
            if (loginButton == null) {
                throw new RuntimeException("Cannot find login button");
            }

            logger.info("Clicking login button...");
            try {
                js.executeScript("arguments[0].click();", loginButton);
            } catch (Exception e) {
                loginButton.click();
            }

            // Thay vì sleep cố định, chờ đợi sessionStorage có dữ liệu cần thiết
            logger.info("Waiting for session data...");
            boolean loginSuccess = wait.until(driver1 -> {
                try {
                    String checkScript = """
                    try {
                        var currentUser = sessionStorage.getItem('CURRENT_USER');
                        var currentUserInfo = sessionStorage.getItem('CURRENT_USER_INFO');
                        return currentUser !== null && currentUserInfo !== null;
                    } catch(e) {
                        return false;
                    }
                """;

                    Boolean hasSessionData = (Boolean) js.executeScript(checkScript);
                    return Boolean.TRUE.equals(hasSessionData);
                } catch (Exception e) {
                    return false;
                }
            });

            if (!loginSuccess) {
                logger.warn("Login may have failed - session data not found");
            }

            // Lấy chỉ những key cần thiết từ sessionStorage
            logger.info("Getting required sessionStorage data...");
            String script = """
            try {
                var result = {};
                var currentUser = sessionStorage.getItem('CURRENT_USER');
                var currentUserInfo = sessionStorage.getItem('CURRENT_USER_INFO');
                
                if (currentUser !== null) {
                    result['CURRENT_USER'] = currentUser;
                }
                if (currentUserInfo !== null) {
                    result['CURRENT_USER_INFO'] = currentUserInfo;
                }
                
                return result;
            } catch(e) {
                return {error: e.message};
            }
        """;

            Object sessionStorageResult = js.executeScript(script);

            if (sessionStorageResult instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sessionStorageMap = (Map<String, Object>) sessionStorageResult;

                for (Map.Entry<String, Object> entry : sessionStorageMap.entrySet()) {
                    sessionData.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : null);
                }
            }

            logger.info("SessionStorage data retrieved: " + sessionData.size() + " items");

        } catch (Exception e) {
            logger.error("Error during login process: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    logger.error("Error closing driver: " + e.getMessage());
                }
            }
        }

        return sessionData;
    }

    // Helper method để tìm element với nhiều selector
    private WebElement findElementWithSelectors(WebDriver driver, WebDriverWait wait, JavascriptExecutor js,
                                                String[] selectors, String fieldType) {
        for (String selector : selectors) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                WebElement element = driver.findElement(By.cssSelector(selector));

                if (element.isDisplayed() && element.isEnabled()) {
                    js.executeScript("arguments[0].scrollIntoView(true);", element);
                    logger.info("Found " + fieldType + " field with selector: " + selector);
                    return element;
                }
            } catch (Exception e) {
                logger.debug(fieldType + " selector failed: " + selector + " - " + e.getMessage());
            }
        }
        return null;
    }

    // Helper method để điền field
    private void fillField(JavascriptExecutor js, WebElement field, String value) {
        try {
            js.executeScript("arguments[0].value = '';", field);
            js.executeScript("arguments[0].value = arguments[1];", field, value);
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", field);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", field);
        } catch (Exception e) {
            field.clear();
            field.sendKeys(value);
        }
    }

    // Helper method để tìm login button
    private WebElement findLoginButton(WebDriver driver, WebDriverWait wait, JavascriptExecutor js) {
        String[] loginButtonSelectors = {
                "button.btn-primary",
                "button[type='submit']",
                "input[type='submit']",
                ".btn-primary",
                "button.ng-star-inserted"
        };

        // Thử CSS selectors trước
        for (String selector : loginButtonSelectors) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                WebElement element = driver.findElement(By.cssSelector(selector));

                if (element.isDisplayed() && element.isEnabled()) {
                    js.executeScript("arguments[0].scrollIntoView(true);", element);
                    logger.info("Found login button with selector: " + selector);
                    return element;
                }
            } catch (Exception e) {
                logger.debug("Login button selector failed: " + selector + " - " + e.getMessage());
            }
        }

        // Thử XPath với text
        try {
            WebElement element = driver.findElement(By.xpath("//button[contains(text(), 'Đăng nhập')]"));
            if (element.isDisplayed() && element.isEnabled()) {
                js.executeScript("arguments[0].scrollIntoView(true);", element);
                logger.info("Found login button with XPath");
                return element;
            }
        } catch (Exception e) {
            logger.debug("XPath login button selector failed: " + e.getMessage());
        }

        return null;
    }

    @Override
    public String getSpecificSessionValue(String username, String password, String key) {
        Map<String, String> sessionData = loginAndGetSessionStorage(username, password);
        return sessionData.get(key);
    }
}
