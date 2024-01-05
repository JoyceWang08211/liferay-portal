/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, test} from '@playwright/test';

import {PageAdminPage} from '../../pages/staging/pageAdmin.page';
import {SiteAdminPage} from '../../pages/staging/siteAdmin.page';
import {StagingPage} from '../../pages/staging/staging.page';

let siteAdminpage;

test.beforeEach(async ({page}) => {

	// Create a site named Site Name

	siteAdminpage = new SiteAdminPage(page);
	await siteAdminpage.createBlankSite('Site Name');

	// Create a widget page named Staging Test Page

	const pageAdminPage = new PageAdminPage(page);
	await pageAdminPage.addWidgetPage('Test Page', 'site-name');
});

test.afterEach(async ({}) => {

	// Delete the created site

	await siteAdminpage.deleteAllSite();
});

test('Enable local staging', async ({page}) => {

	// Enable local staging

	const stagingPage = new StagingPage(page);
	await stagingPage.enableLocalStaging('site-name');

	// Assert that the local staging is enabled

	await stagingPage.navigateToStagingSite('site-name');
	await expect(page.getByRole('alert')).toContainText('Site Name Is Staged.');
});
