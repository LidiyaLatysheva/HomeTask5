package com.epam.latysheva;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
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

    //Data
    private static final String PATH_TO_GEKODRIVER = "D:\\IntroductionToAutomatedTesting\\geckodriver.exe";
    private static final String START_URL = "https://mail.ru/";
    private static final String INBOX_URL = "https://e.mail.ru/messages/inbox/";
    private static final String LOGIN = "lida.test.2017";
    private static final String PASSWORD = "$ERDFC5rtfgv";
    private static final String EMAIL_DETAILS_TO = "lida.test.2017@mail.ru";
    private static final String EMAIL_DETAILS_SUBJ = "TEST email";
    private static final String EMAIL_DETAILS_BODY = "Hello, dear!";
    private static final String EMAIL_SIGNATURE="\n\n\n-Thank you!";

    //Locators:
    private static final String LOGIN_LOCATOR = "//*[@id='mailbox__login']";
    private static final String PASSWORD_LOCATOR = "//*[@id='mailbox__password']";
    private static final String LOGIN_BUTTON_LOCATOR = "//*[@id='mailbox__auth__button']";
    private static final String COMPOSE_BUTTON_LOCATOR = "//*[@data-name=\"compose\"]";
    private static final String SEND_BUTTON_LOCATOR = "//*[@data-name=\"send\"]";
    private static final String TO_FIELD_LOCATOR = "//textarea[@data-original-name=\"To\"]";
    private static final String SUBJECT_FIELD_LOCATOR = "//input[@name=\"Subject\"]";
    private static final String BODY_FIELD_LOCATOR = "//body[@class=\"mceContentBody increase-font compose2\" and @id=\"tinymce\"]/br[1]";
    private static final String SAVE_MORE_LOCATOR = "//div[@data-group=\"save-more\"]";
    private static final String SAVE_DRAFT_LOCATOR = "//a[@data-name=\"saveDraft\"]";
    private static final String SAVE_STATUS_LOCATOR = "//div[@class='b-toolbar__message' and @data-mnemo=\"saveStatus\"]";
    private static final String DRAFT_LINK_LOCATOR = "//a[@href=\"/messages/drafts/\"]";
    private static final String SENT_LINK_LOCATOR = "//a[@href=\"/messages/sent/\"]";
    private static final String EMAIL_LOCATOR = "//a[@data-subject=\"" + EMAIL_DETAILS_SUBJ + "\" and contains(@title,\"" + EMAIL_DETAILS_TO + "\")]//span[@title=\"Сегодня, ";
    private static final String PART2_EMAIL_LOCATOR = "//span[@title=\"Сегодня, ";
    private static final String PART_EMAIL_LOCATOR = "//a[@data-subject=\"" + EMAIL_DETAILS_SUBJ + "\" and contains(@title,\"" + EMAIL_DETAILS_TO + "\")]";
    //a[@data-subject="TEST email" and @data-title="lida.test.2017@mail.ru"]//span[@title="Сегодня, 13:56"]
    private static final String SENT_EMAIL_VALIDATOR_LOCATOR = "//div[@class=\"message-sent__title\"]";

    @BeforeTest
    private void initDriver() {
        System.setProperty("webdriver.gecko.driver", PATH_TO_GEKODRIVER);
        driver = new FirefoxDriver();
        System.out.println("driver is created");
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().deleteAllCookies();

    }

    @Test
    public void simpleTest() {
        System.out.println("driver.get");
        driver.get(START_URL);
        System.out.print("driver.findElement");
        driver.findElement(By.xpath(LOGIN_LOCATOR)).sendKeys(LOGIN);
        driver.findElement(By.xpath(PASSWORD_LOCATOR)).sendKeys(PASSWORD);
        driver.findElement(By.xpath(LOGIN_BUTTON_LOCATOR)).click();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(driver.findElement(By.xpath(COMPOSE_BUTTON_LOCATOR)).isDisplayed(), "Compose button is not displayed");
        Assert.assertFalse(isElementPresent(By.xpath(LOGIN_BUTTON_LOCATOR)), "Login button is still on the page. Login was unsuccessful");
        //Click Compose button
        driver.findElement(By.xpath(COMPOSE_BUTTON_LOCATOR)).click();
        Assert.assertTrue(isElementPresent(By.xpath(SEND_BUTTON_LOCATOR)), "Check if Send button is on the page");
        //Fill in To, Subject and Body fields

        driver.findElement(By.xpath(TO_FIELD_LOCATOR)).sendKeys(EMAIL_DETAILS_TO);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.xpath(SUBJECT_FIELD_LOCATOR)).sendKeys(EMAIL_DETAILS_SUBJ);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.switchTo().frame(driver.findElement(By.xpath("//iframe[contains(@id, 'composeEditor')]")));
        //driver.findElement(By.id("tinymce")).clear();
        driver.findElement(By.id("tinymce")).sendKeys(EMAIL_DETAILS_BODY);
        driver.switchTo().defaultContent();

        //Save as Draft
        driver.findElement(By.xpath(SAVE_MORE_LOCATOR)).click();
        driver.findElement(By.xpath(SAVE_DRAFT_LOCATOR)).click();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check if Saved message appears
        String str = driver.findElement(By.xpath(SAVE_STATUS_LOCATOR)).getText();
        boolean isContained = str.contains("Сохранено в черновиках в");
        String[] tmpWords = str.split("\\s");
        int size = tmpWords.length;
        String saveTime = tmpWords[size - 1].toString();
        Assert.assertTrue(isContained);

        //Go to Draft folder
        driver.findElement(By.xpath(DRAFT_LINK_LOCATOR)).click();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String emailLocator = EMAIL_LOCATOR;
        emailLocator = EMAIL_LOCATOR + saveTime + "\"]";
        //Assert.assertTrue(isElementPresent(By.xpath(tmp)),"Check that email is on the page failed");
        System.out.println("is present: " + isElementPresent(By.xpath(emailLocator)));
        System.out.println("is displayed: " + driver.findElement(By.xpath(emailLocator)).isDisplayed());
        //String emailLocator = EMAIL_LOCATOR + saveTime + "\"]";
        driver.findElement(By.xpath(emailLocator)).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("https://e.mail.ru/compose/"), "Emails is not opened");
        Assert.assertTrue(isElementPresent(By.xpath(SEND_BUTTON_LOCATOR)), "Check if Send button is on the page failed");
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String toField = driver.findElement(By.xpath(TO_FIELD_LOCATOR)).getText();
        String subjField = driver.findElement(By.xpath(SUBJECT_FIELD_LOCATOR)).getText();
        driver.switchTo().frame(driver.findElement(By.xpath("//iframe[contains(@id, 'composeEditor')]")));
        String bodyField = driver.findElement(By.id("tinymce")).getText();
        driver.switchTo().defaultContent();

        //Assert.assertEquals(toField,EMAIL_DETAILS_TO,"!!! ---- To field is different ---- !!!");
        //Assert.assertEquals(subjField,EMAIL_DETAILS_SUBJ,"!!! ---- Subject field is different ---- !!!");
        Assert.assertEquals(bodyField, EMAIL_DETAILS_BODY+EMAIL_SIGNATURE, "!!! ---- Body is different ----!!!");


        driver.findElement(By.xpath(SEND_BUTTON_LOCATOR)).click();

        if (driver.findElement(By.id("MailRuConfirm")).isDisplayed()) {
            driver.findElement(By.xpath("//*[@id='MailRuConfirm']//button[@class=\"btn btn_stylish btn_main confirm-ok\"]")).click();
        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check that sent email message appears
        Assert.assertTrue(isElementPresent(By.xpath(SENT_EMAIL_VALIDATOR_LOCATOR)), "Email is not sent");
        //Check that email disappears from Drafts
        driver.findElement(By.xpath(DRAFT_LINK_LOCATOR)).click();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(isElementPresent(By.xpath(emailLocator)), "Check that email disappears from Drafts failed");
        //Check that email appears in Sents
        driver.findElement(By.xpath(SENT_LINK_LOCATOR)).click();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(isElementPresent(By.xpath(PART_EMAIL_LOCATOR)), "Check that email appears in Sents failed");

        //Logout
        System.out.println("Logout!");
        driver.findElement(By.id("PH_logoutLink")).click();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check logout
        Assert.assertTrue(isElementPresent(By.xpath(LOGIN_BUTTON_LOCATOR)));
        Assert.assertTrue(isElementPresent(By.xpath(LOGIN_LOCATOR)));
        Assert.assertTrue(isElementPresent(By.xpath(PASSWORD_LOCATOR)));

    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    @AfterTest
    private void closeDeriver() {
        //driver.close();
        driver.quit();
    }
}