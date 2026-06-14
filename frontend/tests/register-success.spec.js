import {test, expect} from "@playwright/test";

test("click on button redirects to login page", async ({page}) => {
    await page.goto("/register-success")
    await expect(page.getByTestId('login-redirect-button')).toBeVisible()

    await page.getByTestId('login-redirect-button').click()

    await expect(page).toHaveURL('/login')
    await expect(page.getByTestId('login-button')).toBeVisible()
});