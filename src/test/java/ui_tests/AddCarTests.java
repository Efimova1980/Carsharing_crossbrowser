package ui_tests;

import dto.AuthenticationBodyDto;
import dto.CarDto;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LetTheCarWorkPage;
import pages.LoginPage;
import pages.PopupPage;
import utils.CarFactory;
import utils.ConfigReader;
import utils.TestNGListener;
import utils.enums.HeaderMenuItem;

import java.lang.reflect.Method;
import java.time.LocalDate;

@Listeners(TestNGListener.class)


@Epic("Car rental")
@Feature("Add car")
@Severity(SeverityLevel.NORMAL)
public class AddCarTests extends AppManager {
    HomePage homePage;
    LoginPage loginPage;
    LetTheCarWorkPage letTheCarWorkPage;

    @BeforeMethod(alwaysRun = true)
    public void login(Method method) {
        homePage = new HomePage(getDriver());
        loginPage = homePage.getHeader().click(HeaderMenuItem.LOGIN);
        AuthenticationBodyDto user = AuthenticationBodyDto.builder()
                .username(ConfigReader.get("login"))
                .password(ConfigReader.get("password"))
                .build();

        logger.info("start test " + method.getName() + " with " + user);
        loginPage.typeLoginForm(user);
        loginPage.clickBtnYalla();
        loginPage.clickBtnOk();
        letTheCarWorkPage = loginPage.getHeader().click(HeaderMenuItem.LET_THE_CAR_WORK);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Story("Add car with valid data")
    @Test(groups = "smoke")
    public void AddNewCarPositiveTest(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.clickBtnSubmit_WithJS();
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("must not be blank"));
    }

    //add car negative tests
    @Story("Address validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_LocationEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setCity("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Wrong address"));
    }

    @Story("Required fields")
    @Test(groups = "regression")
    public void AddNewNegativeTest_ManufactureEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setManufacture("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Make is required"));
    }

    @Story("Required fields")
    @Test(groups = "regression")
    public void AddNewNegativeTest_ModelEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setModel("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Model is required"));
    }

    @Story("Year validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_YearEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setYear("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Year required"));
    }

    @Story("Year validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_YearNegative(){
        CarDto car = CarFactory.positiveCar();
        car.setYear("-1");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Wrong year"));
    }

    @Story("Year validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_YearAfterNow(){
        CarDto car = CarFactory.positiveCar();
        car.setYear(Integer.toString(LocalDate.now().getYear() + 1));
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Wrong year"));
    }

    @Story("Seats validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsEmpty(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("");
        Assert.assertTrue(homePage.isTextInErrorPresent("Number of seats is required"));
    }
    @Story("Seats validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsLessThanTwo(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("1");
        Assert.assertTrue(homePage.isTextInErrorPresent("Car must have min 2 seat"));
    }

    @Story("Seats validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsNegative(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("-1");
        Assert.assertTrue(homePage.isTextInErrorPresent("Car must have min 2 seat"));
    }

    @Story("Seats validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsMoreTwenty(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("21");
        Assert.assertTrue(homePage.isTextInErrorPresent("To much seats"));
    }

    @Story("Required fields")
    @Test(groups = "regression")
    public void AddNewNegativeTest_CarClassEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setCarClass("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Car class is required"));
    }

    @Story("Required fields")
    @Test(groups = "regression")
    public void AddNewNegativeTest_CarRegistrationNumberEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setSerialNumber("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Car registration number is required"));
    }

    @Story("Price validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_PriceEmpty(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("");
        Assert.assertTrue(homePage.isTextInErrorPresent("Price is required"));
    }
    @Story("Price validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_PriceNegative(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("-0.01");
        Assert.assertTrue(homePage.isTextInErrorPresent("Price must be positive"));
    }
    @Story("Price validation")
    @Test(groups = "regression")
    public void AddNewNegativeTest_PriceMoreThanOneThousand(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("1000.01");
        Assert.assertTrue(homePage.isTextInErrorPresent("To much big price"));
    }
}
