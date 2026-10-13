package ui_tests;

import manager.AppManager;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginPage;
import pages.PopupPage;
import utils.ConfigReader;
import utils.TestNGListener;
import utils.enums.FooterMenuItem;
import utils.enums.HeaderMenuItem;

import static utils.UserFactory.userWithCredentials;

@Listeners(TestNGListener.class)

public class NavigationTests extends AppManager {

    //tests with checking titles on footer menu click
    //"external": they open third-party sites (titles, cookie banners, regions change) - not in regression
    @Test(groups = "external")
    public void iconFacebookNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_FACEBOOK, "Facebook"));
    }
    @Test(groups = "external")
    public void iconVKNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_VK, "VK"));
    }
    @Test(groups = "external")
    public void iconInstagramNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_INSTAGRAM, "Instagram"));
    }
    @Test(groups = "external")
    public void iconSlackNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_SLACK, "Slack"));
    }
    @Test(groups = "external")
    public void iconTelegramNavigationTest(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickIconFooter(FooterMenuItem.ICON_TELEGRAM, "Telegram"));
    }

    //tests with checking titles on header menu click

    @Test(groups = "regression")
    public void HeaderNavigationTest_Search(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.SEARCH, "Search"));
    }
    @Test(groups = "regression")
    public void HeaderNavigationTest_LoginBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.LOGIN, "Login"));
    }
    @Test(groups = "regression")
    public void HeaderNavigationTest_SignUpBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.SIGN_UP, "Registration"));
    }
    @Test(groups = "regression")
    public void HeaderNavigationTest_TermsOfUseBtn(){
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.TERMS_OF_USE, "Terms of use"));
    }

    // "Let the car work" is in the header only for a logged-in user
    @Test(groups = "regression")
    public void HeaderNavigationTest_LetTheCarWorkBtn(){
        LoginPage loginPage = new HomePage(getDriver()).getHeader().click(HeaderMenuItem.LOGIN);
        loginPage.typeLoginForm(userWithCredentials(ConfigReader.get("login"), ConfigReader.get("password")));
        loginPage.clickBtnYalla();
        // wait until login is finished, otherwise HomePage may open before the user is logged in
        new PopupPage(getDriver()).isTextInPopupMessagePresent("Logged in success");
        Assert.assertTrue(new HomePage(getDriver())
                .isTitlePresentOnClickBtnHeader(HeaderMenuItem.LET_THE_CAR_WORK, "Let the car work!"));
    }
}
