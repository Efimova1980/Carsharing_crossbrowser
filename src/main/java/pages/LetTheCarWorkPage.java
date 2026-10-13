package pages;

import dto.Car;
import enums.Fuel;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class LetTheCarWorkPage extends BasePage {
    public LetTheCarWorkPage(WebDriver driver) {
        //setDriver(driver);
        super(driver);
        //PageFactory.initElements(new AjaxElementLocatorFactory(driver,15), this);
    }

    @FindBy(id = "pickUpPlace")
    WebElement inputLocation;
    //car details:
    @FindBy(id = "make")
    WebElement inputManufacture;
    @FindBy(id = "model")
    WebElement inputModel;
    @FindBy(id = "year")
    WebElement inputYear;
    @FindBy(xpath = "//select[@id= 'fuel']")
    WebElement selectFuel;
    @FindBy(id = "seats")
    WebElement inputSeats;
    @FindBy(id = "class")
    WebElement inputClass;
    @FindBy(id = "serialNumber")
    WebElement inputSerialNumber;
    @FindBy(id = "price")
    WebElement inputPrice;
    @FindBy(xpath = "//textarea[@id='about']")
    WebElement inputAbout;
    @FindBy(xpath = "//input[@type='file']")
    WebElement inputFile;
    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnSubmit;

    public void typeLetTheCarWorkForm(Car car){
        inputLocation.sendKeys(car.getCity());
        typeCity_WithJS(car.getCity());
        inputManufacture.sendKeys(car.getManufacture());
        inputModel.sendKeys(car.getModel());
        inputYear.sendKeys(car.getYear());
        typeFuel(car.getFuel());
        inputSeats.sendKeys("" + car.getSeats());
        inputClass.sendKeys(car.getCarClass());
        inputSerialNumber.sendKeys(car.getSerialNumber());
        inputPrice.sendKeys(Double.toString(car.getPricePerDay()));
        inputAbout.sendKeys(car.getAbout());
        //typeImage(car.getImage());
    }


    private void typeCity_WithJS(String city) {
        ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input'));",
                inputLocation, city
        );
    }

    public void typeSeats(String seats){
        inputSeats.clear();
        inputSeats.sendKeys(seats);
    }

    public void typePrice(String s) {
        inputPrice.clear();
        inputPrice.sendKeys(s);
    }

    public void typeImage(String img) {
        File photo = new File(img);
        inputFile.sendKeys(photo.getAbsolutePath());
        pause(3);
    }

    public void clickBtnSubmit_WithJS(){
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("document.querySelector(\"button[type='submit']\")" +
                ".removeAttribute(\"disabled\")");
        btnSubmit.click();
    }

    private void typeFuel(Fuel fuel){
        selectFuel.click();
        WebElement element = getDriver().findElement(By.xpath(fuel.getLocator()));
        element.click();
    }

    public boolean isButtonSubmitEnabled(){
        return btnSubmit.isEnabled();
    }




}
