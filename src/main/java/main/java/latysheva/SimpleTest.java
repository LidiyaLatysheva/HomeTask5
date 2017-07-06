package main.java.latysheva;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by Lidziya_Latyshava on 6/22/2017.
 */
public class SimpleTest {

    private WebDriver driver;
    WebDriverWait wait;

    //Data
    private static final String PATH_TO_GEKODRIVER = "lib/geckodriver.exe";
    private static final String START_URL = "https://mail.ru/";
    private static final String LOGIN = "lida.test.2017";
    private static final String PASSWORD = "$ERDFC5rtfgv";
    private static final String EMAIL_DETAILS_TO = "lida.test.2017@mail.ru";
    private static final String EMAIL_DETAILS_SUBJ = "TEST email";
    private static final String EMAIL_DETAILS_BODY = "Hello, dear!";
    private static final String EMAIL_SIGNATURE = "\n\n\n-Thank you!";
    private static final String GECKO_DRIVER_SYSTEM_PROPERTY = "webdriver.gecko.driver";
    private static final String SAVED_TO_DRAFT_MSG = "Сохранено в черновиках в";
    private static final String COMPOSE_URL = "https://e.mail.ru/compose/";
    private static final String BODY_IS_DIFFERENT_MSG = "!!! ---- Body is different ----!!!";
    private static final String EMAIL_IS_NOT_SENT_MSG = "Email is not sent";
    private static final String CHECK_EMAIL_IN_DRAFTS_FAILED_MSG = "Check that email disappears from Drafts failed";
    private static final String CHECK_EMAIL_IN_SENTS_FAILED_MSG = "Check that email appears in Sents failed";
    private static final String CHECK_EMAIL_OPENED_FAILED_MSG = "Emails is not opened";
    private static final String CHECK_SEND_BTN_PRESENT_FAILED_MSG = "Check if Send button is on the page failed";
    private static final String CHECK_LOGIN_FAILED_MSG = "Login button is still on the page. Login was unsuccessful";

    //Locators:
    private static final By LOGIN_FIELD = By.id("mailbox__login");
    private static final By PASSWORD_FIELD = By.id("mailbox__password");
    private static final By LOGIN_BUTTON = By.id("mailbox__auth__button");
    private static final By COMPOSE_BUTTON = By.cssSelector("a[data-name='compose']");
    private static final By SEND_BUTTON = By.xpath("//*[@data-name=\"send\"]");
    private static final By TO_FIELD = By.xpath("//textarea[@data-original-name=\"To\"]");
    private static final By SUBJECT_FIELD = By.name("Subject");
    private static final By BODY_FIELD_IFRAME = By.xpath("//iframe[contains(@id, 'composeEditor')]");
    private static final By BODY_FIELD = By.id("tinymce");
    private static final By SAVE_MORE_DROPDOWN = By.xpath("//div[@data-group=\"save-more\"]");
    private static final By SAVE_DRAFT_MENU_ITEM = By.cssSelector("a[data-name='saveDraft']");
    private static final By SAVE_STATUS = By.xpath("//div[@class='b-toolbar__message' and @data-mnemo=\"saveStatus\"]");
    private static final By DRAFT_LINK = By.cssSelector("a[href='/messages/drafts/']");
    private static final By SENT_LINK = By.cssSelector("a[href='/messages/sent/']");
    private static final String EMAIL = "//a[@data-subject=\"" + EMAIL_DETAILS_SUBJ + "\" and contains(@title,\"" + EMAIL_DETAILS_TO + "\")]//span[@title=\"Сегодня, ";
    private static final String PART_EMAIL_LOCATOR = "//a[@data-subject=\"" + EMAIL_DETAILS_SUBJ + "\" and contains(@title,\"" + EMAIL_DETAILS_TO + "\")]";
    private static final By LOGOUT_LINK = By.id("PH_logoutLink");
    private static final By SENT_EMAIL_VALIDATOR_LOCATOR = By.cssSelector("div.message-sent__title");
    private static final By COMPOSE_EDITOR_IFRAME = By.xpath("//iframe[contains(@id, 'composeEditor')]");
    private static final By COMPOSE_EDITOR = By.id("tinymce");

    @BeforeTest
    private void initDriver() {
        /**
         * Set System variable webdriver.gecko.driver.
         */
        System.setProperty(GECKO_DRIVER_SYSTEM_PROPERTY, PATH_TO_GEKODRIVER);
        /**
         * Initialize webdriver.
         * Set pageLoadTimeout and delete all cookies.
         */
        driver = new FirefoxDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(25, TimeUnit.SECONDS);
        /**
         * Initialize wait attribute
         */
        wait = new WebDriverWait(driver, 15);
    }

    @Test
    public void simpleTest() {
        /**
         * Open browser with set open URL
         */
        driver.get(START_URL);
        /**
         * Enter account information and login to the system.
         */
        driver.findElement(LOGIN_FIELD).sendKeys(LOGIN);
        driver.findElement(PASSWORD_FIELD).sendKeys(PASSWORD);
        driver.findElement(LOGIN_BUTTON).click();
        /**
         * Check that user is logged in to his mailbox
         * by checking that Create mail button is available.
         */
        wait.until(ExpectedConditions.elementToBeClickable(COMPOSE_BUTTON));
        /**
         * Click Compose button and check that form for new email is opened
         * by checking that Send btn is on the page.
         */
        driver.findElement(COMPOSE_BUTTON).click();
        Assert.assertTrue(isElementPresent(SEND_BUTTON), CHECK_SEND_BTN_PRESENT_FAILED_MSG);
        /**
         * Fill in To, Subject and Body fields.
         */
        wait.until(ExpectedConditions.elementToBeClickable(TO_FIELD));
        driver.findElement(TO_FIELD).sendKeys(EMAIL_DETAILS_TO);
        driver.findElement(SUBJECT_FIELD).sendKeys(EMAIL_DETAILS_SUBJ);
        driver.switchTo().frame(driver.findElement(BODY_FIELD_IFRAME));
        driver.findElement(BODY_FIELD).sendKeys(EMAIL_DETAILS_BODY);
        driver.switchTo().defaultContent();
        /**
         * Save email as Draft.
         */
        driver.findElement(SAVE_MORE_DROPDOWN).click();
        driver.findElement(SAVE_DRAFT_MENU_ITEM).click();
        /**
         * Check that Email saved message appears.
         */
        wait.until(ExpectedConditions.presenceOfElementLocated(SAVE_STATUS));
        String str = driver.findElement(SAVE_STATUS).getText();
        boolean isContained = str.contains(SAVED_TO_DRAFT_MSG);
        Assert.assertTrue(isContained);
        /**
         * Parsing saved message to get saving time for further verification.
         */
        String[] tmpWords = str.split("\\s");
        int size = tmpWords.length;
        String saveTime = tmpWords[size - 1].toString();
        /**
         *  Go to Draft folder
         */
        driver.findElement(DRAFT_LINK).click();
        /**
         * Check that Email is saved to Draft folder
         * by finding email with entered To/Subject values.
         */
        String emailLocator = EMAIL + saveTime + "\"]";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(emailLocator)));
        /**
         * Open saved email.
         * Check that email is opened and body has text entered above.
         */
        driver.findElement(By.xpath(emailLocator)).click();
        Assert.assertTrue(driver.getCurrentUrl().contains(COMPOSE_URL), CHECK_EMAIL_OPENED_FAILED_MSG);
        Assert.assertTrue(isElementPresent(SEND_BUTTON), CHECK_SEND_BTN_PRESENT_FAILED_MSG);
        driver.switchTo().frame(driver.findElement(COMPOSE_EDITOR_IFRAME));
        String bodyField = driver.findElement(COMPOSE_EDITOR).getText();
        driver.switchTo().defaultContent();
        Assert.assertEquals(bodyField, EMAIL_DETAILS_BODY + EMAIL_SIGNATURE, BODY_IS_DIFFERENT_MSG);
        /**
         * Send email.
         */
        driver.findElement(SEND_BUTTON).click();
        if (driver.findElement(By.id("MailRuConfirm")).isDisplayed()) {
            driver.findElement(By.xpath("//*[@id='MailRuConfirm']//button[@class=\"btn btn_stylish btn_main confirm-ok\"]")).click();
        }
        /**
         * Check that sent email message appears.
         */
        String tmp = driver.findElement(SENT_EMAIL_VALIDATOR_LOCATOR).getText();
        Assert.assertTrue(isElementPresent(SENT_EMAIL_VALIDATOR_LOCATOR), EMAIL_IS_NOT_SENT_MSG);

        /**
         * Check that email disappears from Draft folder.
         */
        driver.findElement(DRAFT_LINK).click();
        Assert.assertFalse(isElementPresent(By.xpath(emailLocator)), CHECK_EMAIL_IN_DRAFTS_FAILED_MSG);
        /**
         * Check that email appears in Sent folder.
         */
        driver.findElement(SENT_LINK).click();
        Assert.assertTrue(isElementPresent(By.xpath(PART_EMAIL_LOCATOR)), CHECK_EMAIL_IN_SENTS_FAILED_MSG);
        /**
         * Logout.
         * Check that user is redirected to login page.
         */
        driver.findElement(LOGOUT_LINK).click();
        Assert.assertTrue(isElementPresent(LOGIN_BUTTON));
        Assert.assertTrue(isElementPresent(LOGIN_FIELD));
        Assert.assertTrue(isElementPresent(PASSWORD_FIELD));
    }

    private boolean isElementPresent(By by) {
        try {
            return driver.findElement(by).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @AfterTest
    private void closeDriver() {
        driver.quit();
    }
}
