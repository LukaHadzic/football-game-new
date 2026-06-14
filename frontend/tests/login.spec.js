import {test, expect} from "@playwright/test";

test('successfull login redirects to home', async ({page}) => {
    await page.goto('/login')
    await expect(
        page.getByRole('button', { name: 'Login' })
    ).toBeVisible();


    await page.getByPlaceholder("Nickname or email").fill('ZERIXA3')
    await page.getByPlaceholder("Password").fill('ProbaLozinke123!')
    // await page.fill('input[name="password"]', 'ProbaLozinke123!')

    await page.click('button[type="submit"]')

    await expect(page).toHaveURL('/home')
    await expect(page.getByText('Home')).toBeVisible();
});

test('failed login displays message', async ({page}) => {
    await page.goto('/login')
    await expect(page.getByRole('button', {name:'Login'})).toBeVisible()

    await page.getByPlaceholder('Nickname or email').fill('NotExists')
    await page.getByPlaceholder('Password').fill('WrongPassword2@')

    await page.click('button[type=submit]')

    await expect(page).toHaveURL('/login')
    await expect(page.getByTestId('error-div')).toBeVisible();
})

test('click on register successful redirect', async ({page}) => {
    await page.goto('/login')
    await expect(page.getByRole('button', { name: 'Login' })).toBeVisible()

    await page.getByRole('link', {name: 'Register now.'}).click();

    await expect(page).toHaveURL('/register')
})