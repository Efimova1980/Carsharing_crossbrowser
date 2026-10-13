package ui_tests;

import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import utils.TestNGListener;

import java.time.LocalDate;

@Listeners(TestNGListener.class)

public class FindYourCarTests extends AppManager {
    HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    public void openHomePage(){
        homePage= new HomePage(getDriver());
    }

    @Test(groups = "smoke")
    public void findYourCarPositiveTest(){
        String city = "Rehovot";
        LocalDate startDate = LocalDate.of(2026,12,12);
        LocalDate endDate = LocalDate.of(2026,12,22);
        homePage.typeFindYourCarForm(city, startDate, endDate);
        //homePage.clickBtnYalla_WithWait();
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isUrlContains("results",2));
    }

    //HOMEWORK10
    //with calendar
    @Test
    public void findYourCarPositiveTestWithCalandar(){
        String city = "Rehovot";
        LocalDate startDate = LocalDate.of(2026,3,12);
        LocalDate endDate = LocalDate.of(2026,3,22);
        homePage.typeFindYourCarFormWithCalandar(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isUrlContains("results",2));
    }

    //demo exception
    @Test(expectedExceptions =  org.openqa.selenium.TimeoutException.class)
    public void findYourCarNegativeTest_EmptyFieldCityWithException(){
        String city = "";
        LocalDate startDate = LocalDate.of(2026,12,12);
        LocalDate endDate = LocalDate.of(2026,12,22);
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        homePage.clickBtnYalla();
    }

    @Test()
    public void findYourCarNegativeTest_EmptyFieldCity(){
        String city = "";
        LocalDate startDate = LocalDate.of(2026,3,12);
        LocalDate endDate = LocalDate.of(2026,3,22);
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isTextInErrorPresent("City is required"));
    }

    //HOMEWORK 10
    //NEGATIVE TESTS WITH WRONG DATES
    @Test()
    public void findYourCarNegativeTest_WrongDate_TheSameDays(){
        String city = "Rehovot";
        LocalDate startDate = LocalDate.of(2026,3,12);
        LocalDate endDate = LocalDate.of(2026,3,12);
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isTextInErrorPresent("You can't book car for less than a day"));
    }

    @Test()
    public void findYourCarNegativeTest_WrongDate_FistIsBigger() {
        String city = "Rehovot";
        LocalDate startDate = LocalDate.of(2026, 3, 22);
        LocalDate endDate = LocalDate.of(2026, 3, 12);
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isTextInErrorPresent("Second date must be after first date")
                && homePage.isTextInErrorPresent("You can't book car for less than a day"));
    }

    @Test()
    public void findYourCarNegativeTest_WrongDate_DateInThePast() {
        String city = "Rehovot";
        LocalDate startDate = LocalDate.of(2024, 3, 12);
        LocalDate endDate = LocalDate.of(2024, 3, 22);
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isTextInErrorPresent("You can't pick date before today"));
    }

    @Test(expectedExceptions = java.time.DateTimeException.class)
    public void findYourCarNegativeTest_WrongDate_DateInvalid() {
        String city = "Rehovot";
        LocalDate startDate = LocalDate.of(2024, 2, 30);//wrong date in FEB
        LocalDate endDate = LocalDate.of(2024, 3, 22);
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        homePage.clickBtnYalla();
    }
}
