import { test, expect } from '@playwright/test'

const USERNAME = process.env.E2E_USERNAME || 'alice'
const PASSWORD = process.env.E2E_PASSWORD || 'password'

async function login(page) {
  await page.goto('/login')
  await page.getByPlaceholder('Username or Email').fill(USERNAME)
  await page.getByPlaceholder('Password').fill(PASSWORD)
  await page.getByRole('button', { name: 'Login' }).click()

  // Dashboard route is '/'
  await expect(page).toHaveURL(/\/($|\?)/)
  await expect(page.getByText('Dashboard', { exact: false })).toBeVisible()
}

test('smoke: login loads dashboard', async ({ page }) => {
  await login(page)
})

test('smoke: create team -> view tasks -> create task -> change status', async ({ page }) => {
  await login(page)

  // Create team
  await page.goto('/teams')
  await page.getByRole('button', { name: /new team/i }).click()

  const teamName = `E2E Team ${Date.now()}`
  await page.getByLabel('Name').fill(teamName)
  await page.getByLabel('Description').fill('Created by Playwright smoke test')
  await page.getByRole('button', { name: 'Create' }).click()

  // Confirm team card exists
  await expect(page.getByText(teamName)).toBeVisible()

  // View tasks for the created team
  const teamCard = page.locator('.card-surface', { hasText: teamName }).first()
  await teamCard.getByRole('button', { name: /view tasks/i }).click()
  await expect(page).toHaveURL(/\/tasks\?teamId=\d+/)

  // Create task (inside Create task card)
  const taskTitle = `E2E Task ${Date.now()}`
  await page.getByLabel('Title').fill(taskTitle)
  await page.getByLabel('Description').fill('Created by Playwright')
  await page.getByRole('button', { name: /^create$/i }).click()

  // Verify task appears
  const taskCard = page.locator('.card-surface', { hasText: taskTitle }).first()
  await expect(taskCard).toBeVisible()

  // Verify assignee visible (creator assigned by UI)
  await expect(taskCard.getByText(/assignees/i)).toBeVisible()
  await expect(taskCard.getByText(/alice|alice a\./i)).toBeVisible()

  // Change status to DONE
  await taskCard.getByRole('combobox').first().selectOption('DONE')
  await expect(taskCard.getByText('DONE', { exact: true })).toBeVisible()
})
