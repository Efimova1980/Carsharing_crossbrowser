package pages;

import dto.CarDto;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
    @FindBy(xpath = "//button[@type='submit']")
    WebElement btnSubmit;

    public void typeLetTheCarWorkForm(CarDto car){
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

    // fuel is the option value as in CarDto: "Diesel", "Petrol", ...
    private void typeFuel(String fuel){
        selectFuel.click();
        WebElement element = getDriver().findElement(By.xpath("//option[@value='" + fuel + "']"));
        element.click();
    }

    public boolean isButtonSubmitEnabled(){
        return btnSubmit.isEnabled();
    }




}
