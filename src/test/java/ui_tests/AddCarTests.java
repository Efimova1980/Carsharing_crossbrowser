package ui_tests;

import dto.CarDto;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LetTheCarWorkPage;
import pages.LoginPage;
import utils.CarFactory;
import utils.TestNGListener;
import utils.enums.HeaderMenuItem;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static utils.UserFactory.existingUserCredentials;

@Listeners(TestNGListener.class)


@Severity(SeverityLevel.NORMAL)
public class AddCarTests extends AppManager {
    HomePage homePage;
    LoginPage loginPage;
    LetTheCarWorkPage letTheCarWorkPage;

    @BeforeMethod(alwaysRun = true)
    public void login(Method method) {
        homePage = new HomePage(getDriver());
        loginPage = homePage.getHeader().click(HeaderMenuItem.LOGIN);
        logger.info("start test " + method.getName() + " with registered user");
        loginPage.loginWithConfirm(existingUserCredentials());
        loginPage.clickBtnOk();
        letTheCarWorkPage = loginPage.getHeader().click(HeaderMenuItem.LET_THE_CAR_WORK);
    }

    @Severity(SeverityLevel.CRITICAL)
    @Test(groups = "smoke")
    public void AddNewCarPositiveTest(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isButtonSubmitEnabled(),
                "validate button Submit is enabled when all fields are valid");
    }

    //add car negative tests
    @Test(groups = "regression")
    public void AddNewNegativeTest_LocationEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setCity("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Wrong address"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_ManufactureEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setManufacture("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Make is required"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_ModelEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setModel("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Model is required"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_YearEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setYear("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Year required"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_YearNegative(){
        CarDto car = CarFactory.positiveCar();
        car.setYear("-1");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Wrong year"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_YearAfterNow(){
        CarDto car = CarFactory.positiveCar();
        car.setYear(Integer.toString(LocalDate.now().getYear() + 1));
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Wrong year"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsEmpty(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Number of seats is required"));
    }
    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsLessThanTwo(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("1");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Car must have min 2 seat"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsNegative(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("-1");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Car must have min 2 seat"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_SeatsMoreTwenty(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("21");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("To much seats"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_CarClassEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setCarClass("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Car class is required"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_CarRegistrationNumberEmpty(){
        CarDto car = CarFactory.positiveCar();
        car.setSerialNumber("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Car registration number is required"));
    }

    @Test(groups = "regression")
    public void AddNewNegativeTest_PriceEmpty(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Price is required"));
    }
    @Test(groups = "regression")
    public void AddNewNegativeTest_PriceNegative(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("-0.01");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("Price must be positive"));
    }
    @Test(groups = "regression")
    public void AddNewNegativeTest_PriceMoreThanOneThousand(){
        CarDto car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("1000.01");
        Assert.assertTrue(letTheCarWorkPage.isTextInErrorPresent("To much big price"));
    }
}
