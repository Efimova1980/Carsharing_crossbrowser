package ui_tests;

import dto.User;
import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.BasePage;
import pages.HomePage;
import pages.LoginPage;
import utils.PropertiesReader;
import utils.TestNGListener;
import utils.enums.FooterMenuItem;
import utils.enums.HeaderMenuItem;

@Listeners(TestNGListener.class)

public class NavigationTests extends AppManager {

    //tests with checking titles on footer menu click
    @Test
    public void iconFacebookNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(utils.enums.FooterMenuItem.ICON_FACEBOOK, "Facebook"));
    }
    @Test
    public void iconVKNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_VK, "ВКонтакте"));
    }
    @Test
    public void iconInstagramNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_INSTAGRAM, "Instagram"));
    }
    @Test
    public void iconSlackNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_SLACK, "Slack"));
    }
    @Test
    public void iconTelegramNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_TELEGRAM, "Telegram"));
    }

    //tests with checking titles on header menu click

    @Test
    public void HeaderNavigationTest_Search(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.SEARCH, "Search"));
    }
    @Test
    public void HeaderNavigationTest_LoginBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.LOGIN, "Login"));
    }
    @Test
    public void HeaderNavigationTest_SignUpBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.SIGN_UP, "Registration"));
    }
    @Test
    public void HeaderNavigationTest_TermsOfUseBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.TERMS_OF_USE, "Terms of use"));
    }

    @BeforeMethod(onlyForGroups = "loginRequired")
    public void login() {
        LoginPage loginPage = new HomePage(getDriver()).getHeader().click(HeaderMenuItem.LOGIN);
        User user = User.builder()
                .username(PropertiesReader.getProperty("base.properties","login"))
                .password(PropertiesReader.getProperty("base.properties","password"))
                .build();
        loginPage.typeLoginForm(user);
        loginPage.clickBtnYalla();
    }

    @Test(groups = "loginRequired")
    public void HeaderNavigationTest_LetTheCarWorkBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.LET_THE_CAR_WORK, "Let the car work!"));
    }

}
