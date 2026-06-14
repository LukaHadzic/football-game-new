import {test, expect} from "@playwright/test";

test('failed register shows error message', async ({page}) => {

    await page.goto('/register')
    await expect(page.getByRole('button', {name: 'Register'})).toBeVisible()

    await page.getByTestId('name-input').fill('Luka')

    await page.click('button[type="submit"]')

    await expect(page).toHaveURL('/register')
    await expect(page.getByTestId('error-div')).toBeVisible()

});

test('failed register shows user exists message', async ({page}) => {
    await page.goto('/register')
    await expect(page.getByRole('button', {name: 'Register'})).toBeVisible()

    await page.getByTestId('name-input').fill('Luka')
    await page.getByTestId('surname-input').fill('Hadzic')
    await page.getByTestId('nickname-input').fill('ZERIXA3')
    await page.getByTestId('email-input').fill('lukahadzic1@gmail.com')
    await page.getByTestId('password-input').fill('ProbaLozinke123!')

    await page.click('button[type="submit"]')

    await expect(page.getByTestId('error-div')).toBeVisible()
});

test('successful register redirects to register-success page', async ({page}) => {
    await page.goto('/register')
    await expect(page.getByRole('button', {name: 'Register'})).toBeVisible()

    const unique = Date.now().toString(36)

    await page.getByTestId('name-input').fill('Luka')
    await page.getByTestId('surname-input').fill('Hadzic')
    await page.getByTestId('nickname-input').fill(`Nick${unique}`)
    await page.getByTestId('email-input').fill(`email${unique}@gmail.com`)
    await page.getByTestId('password-input').fill('ProbaLozinke123!')

    await page.click('button[type="submit"]')

    await expect(page).toHaveURL('/register-success')
    await expect(page.getByTestId('heading-div')).toBeVisible()
    await expect(page.getByTestId('message-div')).toBeVisible()
    // await expect(page.getByText(`Nick${unique}, please check provided email's inbox (email${unique}@gmail.com) in order to verify Your identity.`)).toBeVisible()
});

