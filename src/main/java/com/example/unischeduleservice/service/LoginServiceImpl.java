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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    // Khởi tạo WebDriverManager một lần
    static {
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            System.err.println("Error setting up ChromeDriver: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> loginAndGetSessionStorage(String username, String password) {
        WebDriver driver = null;
        Map<String, String> sessionData = new HashMap<>();

        try {
            // Cấu hình Chrome options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new"); // Sử dụng headless mode mới
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            // Tạo driver
            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            System.out.println("Navigating to VNUA website...");
            driver.get("https://daotao.vnua.edu.vn/#/home");

            // Đợi trang load hoàn toàn
            wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));

            // Đợi Angular load xong
            Thread.sleep(3000);

            // Đợi form xuất hiện và có thể tương tác
            System.out.println("Waiting for login form...");

            WebElement usernameField = null;
            String[] usernameSelectors = {
                    "input[name='username']",
                    "input[formcontrolname='username']",
                    "input[type='text']",
                    ".form-control[name='username']",
                    ".form-control[formcontrolname='username']"
            };

            // Thử nhiều lần để tìm element có thể tương tác
            for (int attempt = 0; attempt < 5; attempt++) {
                System.out.println("Attempt " + (attempt + 1) + " to find username field...");

                for (String selector : usernameSelectors) {
                    try {
                        // Đợi element xuất hiện
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));

                        // Đợi element có thể tương tác
                        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));

                        usernameField = driver.findElement(By.cssSelector(selector));

                        if (usernameField.isDisplayed() && usernameField.isEnabled()) {
                            // Scroll đến element
                            js.executeScript("arguments[0].scrollIntoView(true);", usernameField);
                            Thread.sleep(500);

                            // Kiểm tra lại sau khi scroll
                            if (usernameField.isDisplayed() && usernameField.isEnabled()) {
                                System.out.println("Found username field with selector: " + selector);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Selector failed: " + selector + " - " + e.getMessage());
                        continue;
                    }
                }

                if (usernameField != null && usernameField.isDisplayed() && usernameField.isEnabled()) {
                    break;
                }

                Thread.sleep(2000); // Đợi trước khi thử lại
            }

            if (usernameField == null) {
                throw new RuntimeException("Cannot find username field after multiple attempts");
            }

            System.out.println("Entering username...");
            try {
                // Sử dụng JavaScript để clear và set value
                js.executeScript("arguments[0].value = '';", usernameField);
                js.executeScript("arguments[0].value = arguments[1];", usernameField, username);

                // Trigger các event Angular cần thiết
                js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", usernameField);
                js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", usernameField);
            } catch (Exception e) {
                // Fallback: dùng sendKeys
                usernameField.clear();
                usernameField.sendKeys(username);
            }

            // Tìm password field với logic tương tự
            WebElement passwordField = null;
            String[] passwordSelectors = {
                    "input[name='password']",
                    "input[formcontrolname='password']",
                    "input[type='password']",
                    ".form-control[name='password']",
                    ".form-control[formcontrolname='password']"
            };

            for (int attempt = 0; attempt < 5; attempt++) {
                System.out.println("Attempt " + (attempt + 1) + " to find password field...");

                for (String selector : passwordSelectors) {
                    try {
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));

                        passwordField = driver.findElement(By.cssSelector(selector));

                        if (passwordField.isDisplayed() && passwordField.isEnabled()) {
                            js.executeScript("arguments[0].scrollIntoView(true);", passwordField);
                            Thread.sleep(500);

                            if (passwordField.isDisplayed() && passwordField.isEnabled()) {
                                System.out.println("Found password field with selector: " + selector);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Password selector failed: " + selector + " - " + e.getMessage());
                        continue;
                    }
                }

                if (passwordField != null && passwordField.isDisplayed() && passwordField.isEnabled()) {
                    break;
                }

                Thread.sleep(2000);
            }

            if (passwordField == null) {
                throw new RuntimeException("Cannot find password field after multiple attempts");
            }

            System.out.println("Entering password...");
            try {
                js.executeScript("arguments[0].value = '';", passwordField);
                js.executeScript("arguments[0].value = arguments[1];", passwordField, password);
                js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", passwordField);
                js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", passwordField);
            } catch (Exception e) {
                passwordField.clear();
                passwordField.sendKeys(password);
            }

            // Tìm và click nút đăng nhập với logic cải thiện
            WebElement loginButton = null;
            String[] loginButtonSelectors = {
                    "button.btn-primary",
                    "button[type='submit']",
                    "input[type='submit']",
                    "button:contains('Đăng nhập')",
                    ".btn-primary",
                    "button.ng-star-inserted"
            };

            for (int attempt = 0; attempt < 5; attempt++) {
                System.out.println("Attempt " + (attempt + 1) + " to find login button...");

                for (String selector : loginButtonSelectors) {
                    try {
                        if (selector.contains(":contains")) {
                            // Sử dụng XPath cho text search
                            loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Đăng nhập')]"));
                        } else {
                            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                            loginButton = driver.findElement(By.cssSelector(selector));
                        }

                        if (loginButton != null && loginButton.isDisplayed() && loginButton.isEnabled()) {
                            js.executeScript("arguments[0].scrollIntoView(true);", loginButton);
                            Thread.sleep(500);

                            if (loginButton.isDisplayed() && loginButton.isEnabled()) {
                                System.out.println("Found login button with selector: " + selector);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Login button selector failed: " + selector + " - " + e.getMessage());
                        continue;
                    }
                }

                if (loginButton != null && loginButton.isDisplayed() && loginButton.isEnabled()) {
                    break;
                }

                Thread.sleep(2000);
            }

            if (loginButton == null) {
                throw new RuntimeException("Cannot find login button after multiple attempts");
            }

            System.out.println("Clicking login button...");
            try {
                // Thử click bằng JavaScript trước
                js.executeScript("arguments[0].click();", loginButton);
            } catch (Exception e) {
                // Fallback: click thông thường
                loginButton.click();
            }

            // Đợi sau khi login
            Thread.sleep(5000);

            // Kiểm tra login thành công bằng cách check URL hoặc element
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Current URL after login: " + currentUrl);

            // Lấy sessionStorage
            System.out.println("Getting sessionStorage...");
            String script = """
                try {
                    var sessionStorage = window.sessionStorage;
                    var result = {};
                    for (var i = 0; i < sessionStorage.length; i++) {
                        var key = sessionStorage.key(i);
                        result[key] = sessionStorage.getItem(key);
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

            System.out.println("SessionStorage data retrieved: " + sessionData.size() + " items");

        } catch (Exception e) {
            System.err.println("Error during login process: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.err.println("Error closing driver: " + e.getMessage());
                }
            }
        }

        return sessionData;
    }

    @Override
    public String getSpecificSessionValue(String username, String password, String key) {
        Map<String, String> sessionData = loginAndGetSessionStorage(username, password);
        return sessionData.get(key);
    }
}
