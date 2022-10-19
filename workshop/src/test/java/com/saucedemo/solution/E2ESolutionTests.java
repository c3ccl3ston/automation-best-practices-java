package com.saucedemo.solution;

import com.deque.html.axecore.results.Results;
import com.saucedemo.solution.pages.*;
import com.saucelabs.saucebindings.DataCenter;
import com.saucelabs.saucebindings.junit4.SauceBaseTest;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class E2ESolutionTests extends SauceBaseTest {

    @Override
    public DataCenter getDataCenter() {
        // Select data center to execute against
        return DataCenter.US_WEST; // DataCenter.EU_CENTRAL
    }

    @Test()
    public void appRenders() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.visit();
        assertTrue(loginPage.isDisplayed());
    }

    @Test()
    public void loginWorks() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.visit();
        loginPage.login("standard_user");
        assertTrue(new ProductsPage(driver).isDisplayed());
    }

    @Test()
    public void userCanCheckout() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.visit();
        loginPage.login("standard_user");
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.addAnyProductToCart();
        CheckoutStepOnePage stepOnePage = new CheckoutStepOnePage(driver);
        stepOnePage.visit();
        stepOnePage.enterPersonalDetails();
        new CheckoutOverviewPage(driver).finish();
        assertTrue(new CheckoutCompletePage(driver).isDisplayed());
    }

    @Test()
    public void userCanCheckoutAtomic() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.visit();

        // Hide this behavior in an App object. I put it here only for clarity
        // You can create an App.setState(AppState appStateObject)
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        Cookie loginCookie = new Cookie("session-username", "standard_user");
        // try document.cookie="session-username=standard_user" in browser Console
        driver.manage().addCookie(loginCookie);

        ShoppingCartPage cart = new ShoppingCartPage(driver);
        cart.visit();
        // checking that app is in correct state
        assertEquals(0, cart.getItemsCount());

        ((JavascriptExecutor) driver)
                .executeScript("localStorage.setItem(\"cart-contents\", \"[4]\")");
        driver.navigate().refresh();
        // checking that app is in correct state
        assertEquals(1, cart.getItemsCount());

        // now we can actually do the checkout logic that we want to test
        CheckoutStepOnePage stepOnePage = new CheckoutStepOnePage(driver);
        stepOnePage.visit();
        stepOnePage.enterPersonalDetails();
        new CheckoutOverviewPage(driver).finish();
        assertTrue(new CheckoutCompletePage(driver).isDisplayed());
    }
}
