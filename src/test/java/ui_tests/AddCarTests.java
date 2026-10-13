package ui_tests;

import dto.Car;
import dto.User;
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
import utils.PropertiesReader; //for local tests
import utils.TestNGListener;
import utils.enums.HeaderMenuItem;

import java.lang.reflect.Method;

@Listeners(TestNGListener.class)


public class AddCarTests extends AppManager {
    HomePage homePage;
    LoginPage loginPage;
    LetTheCarWorkPage letTheCarWorkPage;

    @BeforeMethod(alwaysRun = true)
    public void login(Method method) {
        homePage = new HomePage(getDriver());
        loginPage = homePage.getHeader().click(HeaderMenuItem.LOGIN);
        /*
        //FOR LOCAL TESTS NOT GITHUB REMOTE TESTS
        User user = User.builder()
                .username(PropertiesReader.getProperty("base.properties", "login"))
                .password(PropertiesReader.getProperty("base.properties", "password"))
                .build();
        */
        User user = User.builder()
                .username("harry@gmail.com")
                .password("Pass1234!")
                .build();

        logger.info("start test " + method.getName() + " with " + user);
        loginPage.typeLoginForm(user);
        loginPage.clickBtnYalla();
        loginPage.clickBtnOk();
        //loginPage.pause(2);
        letTheCarWorkPage = loginPage.getHeader().click(HeaderMenuItem.LET_THE_CAR_WORK);
    }

    @Test(groups = "smoke")
    public void AddNewCarPositiveTest(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        //letTheCarWorkPage.typeImage(car.getImage());
        letTheCarWorkPage.clickBtnSubmit_WithJS();
        //Assert.assertTrue(letTheCarWorkPage.isButtonSubmitEnabled());
        Assert.assertTrue(new PopupPage(getDriver()).isTextInPopupMessagePresent("must not be blank"));
    }

    //add car negative tests
    @Test
    public void AddNewNegativeTest_LocationEmpty(){
        Car car = CarFactory.positiveCar();
        car.setCity("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Wrong address"));
    }

    @Test
    public void AddNewNegativeTest_ManufactureEmpty(){
        Car car = CarFactory.positiveCar();
        car.setManufacture("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Make is required"));
    }

    @Test
    public void AddNewNegativeTest_ModelEmpty(){
        Car car = CarFactory.positiveCar();
        car.setModel("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Model is required"));
    }

    @Test
    public void AddNewNegativeTest_YearEmpty(){
        Car car = CarFactory.positiveCar();
        car.setYear("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Year required"));
    }

    @Test
    public void AddNewNegativeTest_YearNegative(){
        Car car = CarFactory.positiveCar();
        car.setYear("-1");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Wrong year"));
    }

    @Test
    public void AddNewNegativeTest_YearAfterNow(){
        Car car = CarFactory.positiveCar();
        car.setYear("2027");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Wrong year"));
    }

    @Test
    public void AddNewNegativeTest_SeatsEmpty(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("");
        Assert.assertTrue(homePage.isTextInErrorPresent("Number of seats is required"));
    }
    @Test
    public void AddNewNegativeTest_SeatsLessThanTwo(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("1");
        Assert.assertTrue(homePage.isTextInErrorPresent("Car must have min 2 seat"));
    }

    @Test
    public void AddNewNegativeTest_SeatsNegative(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("-1");
        Assert.assertTrue(homePage.isTextInErrorPresent("Car must have min 2 seat"));
    }

    @Test
    public void AddNewNegativeTest_SeatsMoreTwenty(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typeSeats("21");
        Assert.assertTrue(homePage.isTextInErrorPresent("To much seats"));
    }

    @Test
    public void AddNewNegativeTest_CarClassEmpty(){
        Car car = CarFactory.positiveCar();
        car.setCarClass("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Car class is required"));
    }

    @Test
    public void AddNewNegativeTest_CarRegistrationNumberEmpty(){
        Car car = CarFactory.positiveCar();
        car.setSerialNumber("");
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        Assert.assertTrue(homePage.isTextInErrorPresent("Car registration number is required"));
    }

    @Test
    public void AddNewNegativeTest_PriceEmpty(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("");
        Assert.assertTrue(homePage.isTextInErrorPresent("Price is required"));
    }
    @Test
    public void AddNewNegativeTest_PriceNegative(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("-0,01");
        Assert.assertTrue(homePage.isTextInErrorPresent("Price must be positive"));
    }
    @Test
    public void AddNewNegativeTest_PriceMoreThanOneThousand(){
        Car car = CarFactory.positiveCar();
        letTheCarWorkPage.typeLetTheCarWorkForm(car);
        letTheCarWorkPage.typePrice("1000,01");
        Assert.assertTrue(homePage.isTextInErrorPresent("To much big price"));
    }
}
