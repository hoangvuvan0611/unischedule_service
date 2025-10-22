package com.example.unischeduleservice.service;

import com.example.unischeduleservice.models.Account;
import com.example.unischeduleservice.repository.AccountRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final AccountRepository accountRepository;

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
        logger.info("Login with: username={}, password={}", username, password);
        WebDriver driver = null;
        Map<String, String> sessionData = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            ChromeOptions options = new ChromeOptions();

            // Aggressive performance options
            options.addArguments(
                    "--headless=new", // Sử dụng headless mode mới (nhanh hơn)
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu",
                    "--disable-software-rasterizer",
                    "--disable-extensions",
                    "--disable-logging",
                    "--disable-permissions-api",
                    "--disable-notification-permissions",
                    "--disable-web-security",
                    "--allow-running-insecure-content",
                    "--disable-features=VizDisplayCompositor,TranslateUI,BlinkGenPropertyTrees",
                    "--disable-ipc-flooding-protection",
                    "--disable-backgrounding-occluded-windows",
                    "--disable-renderer-backgrounding",
                    "--disable-background-timer-throttling",
                    "--disable-background-networking",
                    "--disable-sync",
                    "--disable-translate",
                    "--hide-scrollbars",
                    "--mute-audio",
                    "--no-first-run",
                    "--disable-default-apps",
                    "--disable-popup-blocking",
                    "--disable-prompt-on-repost",
                    "--metrics-recording-only",
                    "--no-default-browser-check",
                    "--disable-hang-monitor",
                    "--disable-client-side-phishing-detection",
                    "--disable-component-update",
                    "--disable-domain-reliability",
                    "--window-size=1024,768",
                    "--log-level=3"
            );

            // EAGER: Không đợi images, CSS load
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);

            // Block tất cả resources không cần thiết
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("profile.default_content_setting_values.notifications", 2);
            prefs.put("profile.managed_default_content_settings.images", 2);
            prefs.put("profile.managed_default_content_settings.stylesheets", 2);
            prefs.put("profile.managed_default_content_settings.media_stream", 2);
            prefs.put("profile.managed_default_content_settings.plugins", 2);
            prefs.put("profile.managed_default_content_settings.geolocation", 2);
            prefs.put("profile.default_content_settings.popups", 0);
            prefs.put("profile.managed_default_content_settings.cookies", 1);
            prefs.put("profile.managed_default_content_settings.javascript", 1);
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOption("prefs", prefs);

            // Exclude switches để tăng tốc
            options.setExperimentalOption("excludeSwitches",
                    new String[]{"enable-automation", "enable-logging"});

            driver = new ChromeDriver(options);

            // Timeout cực ngắn
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

            // Wait times cực ngắn
            WebDriverWait fastWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            WebDriverWait normalWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            logger.info("Navigating...");
            driver.get(urlHome);

            // Chỉ đợi DOM interactive (không cần complete)
            fastWait.until(webDriver -> {
                String state = (String) js.executeScript("return document.readyState");
                return !"loading".equals(state);
            });

            logger.info("Page loaded in {}ms", System.currentTimeMillis() - startTime);

            // Check iframe nhanh - chỉ check iframe đầu tiên
            try {
                List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
                if (!iframes.isEmpty() && isLoginFormPresent(driver.switchTo().frame(0))) {
                    logger.info("Using first iframe");
                } else {
                    driver.switchTo().defaultContent();
                }
            } catch (Exception e) {
                driver.switchTo().defaultContent();
            }

            // Tìm và fill form siêu nhanh
            logger.info("Finding form...");
            WebElement usernameField = findElementFast(driver, fastWait, js,
                    "input[name='username'], input[type='text'], input[formcontrolname='username']");

            if (usernameField == null) {
                // Fallback: lấy input text đầu tiên
                List<WebElement> inputs = driver.findElements(By.tagName("input"));
                usernameField = inputs.stream()
                        .filter(el -> {
                            String type = el.getAttribute("type");
                            return (type == null || "text".equals(type)) && el.isDisplayed();
                        })
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Cannot find username field"));
            }

            WebElement passwordField = findElementFast(driver, fastWait, js,
                    "input[type='password'], input[name='password'], input[formcontrolname='password']");

            if (passwordField == null) {
                throw new RuntimeException("Cannot find password field");
            }

            WebElement loginButton = findElementFast(driver, fastWait, js,
                    "button[type='submit'], button.btn-primary, input[type='submit']");

            if (loginButton == null) {
                // Fallback: tìm button đầu tiên
                loginButton = driver.findElements(By.tagName("button")).stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Cannot find login button"));
            }

            logger.info("Filling form...");
            // Fill cực nhanh - 1 lần execute
            js.executeScript(
                    "arguments[0].value = arguments[2];" +
                            "arguments[1].value = arguments[3];" +
                            "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                            "arguments[1].dispatchEvent(new Event('input', {bubbles:true}));" +
                            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));" +
                            "arguments[1].dispatchEvent(new Event('change', {bubbles:true}));",
                    usernameField, passwordField, username, password
            );

            logger.info("Submitting...");
            js.executeScript("arguments[0].click();", loginButton);

            // Đợi login success - check nhiều điều kiện song song
            logger.info("Waiting for login...");
            String initialUrl = driver.getCurrentUrl();

            boolean loginSuccess = normalWait.until(driver1 -> {
                // Check 1: URL changed
                if (!Objects.equals(driver1.getCurrentUrl(), initialUrl)) {
                    return true;
                }

                // Check 2: SessionStorage có data
                try {
                    Object result = js.executeScript(
                            "return sessionStorage.getItem('CURRENT_USER') !== null && " +
                                    "sessionStorage.getItem('CURRENT_USER_INFO') !== null;"
                    );
                    if (Boolean.TRUE.equals(result)) {
                        return true;
                    }
                } catch (Exception e) {
                    // Ignore
                }

                // Check 3: Có element sau login (logout button, dashboard, etc)
                try {
                    return !driver1.findElements(By.cssSelector(
                            "[href*='logout'], [class*='logout'], [class*='dashboard'], [class*='home']"
                    )).isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });

            if (!loginSuccess) {
                logger.warn("Login may have failed");
            }

            logger.info("Getting session data...");
            // Lấy data cực nhanh
            Object sessionStorageResult = js.executeScript(
                    "var r = {};" +
                            "var u = sessionStorage.getItem('CURRENT_USER');" +
                            "var i = sessionStorage.getItem('CURRENT_USER_INFO');" +
                            "if(u) r.CURRENT_USER = u;" +
                            "if(i) r.CURRENT_USER_INFO = i;" +
                            "return r;"
            );

            if (sessionStorageResult instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) sessionStorageResult;
                map.forEach((k, v) -> sessionData.put(k, v != null ? v.toString() : null));
            }

            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("Login completed in {}ms, got {} items", totalTime, sessionData.size());

            // Debug nếu không có data
            if (sessionData.isEmpty()) {
                Object allData = js.executeScript(
                        "var r={}; for(var i=0;i<sessionStorage.length;i++){" +
                                "var k=sessionStorage.key(i);r[k]=sessionStorage.getItem(k);}" +
                                "return r;"
                );
                logger.warn("No target data found. All sessionStorage: {}", allData);
                logger.warn("Current URL: {}", driver.getCurrentUrl());
            }

        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    logger.error("Error closing driver: {}", e.getMessage());
                }
            }
        }

        // Luu lai thong tin user dang nhap
        if (!sessionData.isEmpty()) {
            Account account = accountRepository.findByUsername(username);
            if (account != null && !account.getPassword().equals(password)) {
                account.setPassword(password);
                accountRepository.save(account);
            } else {
                accountRepository.save(Account.builder()
                        .username(username)
                        .password(password)
                        .build());
            }
        }
        return sessionData;
    }

    // Tìm element cực nhanh - không scroll, không retry nhiều
    private WebElement findElementFast(WebDriver driver, WebDriverWait wait, JavascriptExecutor js, String cssSelectors) {
        try {
            // Try với combined selector trước
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelectors)));
            if (el.isDisplayed() && el.isEnabled()) {
                return el;
            }
        } catch (Exception e) {
            // Ignore và return null
        }
        return null;
    }

    private boolean isLoginFormPresent(WebDriver driver) {
        try {
            return !driver.findElements(By.cssSelector("input[type='password']")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getSpecificSessionValue(String username, String password, String key) {
        Map<String, String> sessionData = loginAndGetSessionStorage(username, password);
        return sessionData.get(key);
    }
}