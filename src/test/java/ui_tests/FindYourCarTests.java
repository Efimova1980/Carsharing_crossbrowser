package ui_tests;

import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
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
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(20);
        homePage.typeFindYourCarForm(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isUrlContains("results",2));
    }

    //HOMEWORK10
    //with calendar
    @Test(groups = "regression")
    public void findYourCarPositiveTestWithCalandar(){
        String city = "Rehovot";
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(20);
        homePage.typeFindYourCarFormWithCalandar(city, startDate, endDate);
        homePage.clickBtnYalla();
        Assert.assertTrue(homePage.isUrlContains("results",2));
    }

    //HOMEWORK 10
    //NEGATIVE TESTS: the form shows errors and Yalla stays disabled (so it is not clicked)
    // row: case name, city, start date, end date, expected error texts
    @DataProvider
    public Object[][] invalidSearchData() {
        LocalDate today = LocalDate.now();
        return new Object[][]{
                {"empty city", "", today.plusDays(10), today.plusDays(20),
                        new String[]{"City is required"}},
                {"the same days", "Rehovot", today.plusDays(10), today.plusDays(10),
                        new String[]{"You can't book car for less than a day"}},
                {"first date is bigger", "Rehovot", today.plusDays(20), today.plusDays(10),
                        new String[]{"Second date must be after first date", "You can't book car for less than a day"}},
                {"dates in the past", "Rehovot", LocalDate.of(2024, 3, 12), LocalDate.of(2024, 3, 22),
                        new String[]{"You can't pick date before today"}}
        };
    }

    @Test(groups = "regression", dataProvider = "invalidSearchData")
    public void findYourCarNegativeTest(String caseName, String city, LocalDate startDate, LocalDate endDate,
                                        String[] expectedErrors) {
        SoftAssert softAssert = new SoftAssert();
        homePage.typeFindYourCarForm_WOJavascript(city, startDate, endDate);
        for (String error : expectedErrors) {
            softAssert.assertTrue(homePage.isTextInErrorPresent(error),
                    "validate error '" + error + "' for " + caseName);
        }
        softAssert.assertFalse(homePage.isBtnYallaEnabled(), "validate button Yalla is disabled for " + caseName);
        softAssert.assertAll();
    }
}
