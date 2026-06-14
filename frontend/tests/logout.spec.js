import {test, expect} from "@playwright/test";

test("successful logout redirect to login page", async ({page}) => {

    await page.goto('/home')
    await expect(page.getByText('logout-button')).toBeVisible()

    await page.getByTestId('logout-button').click()

    await expect(page).toHaveURL('/login')
})