/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const {expect} = require('@playwright/test');

export class SiteAdminPage {
	constructor(page) {
		this.page = page;
		this.addSiteButton = this.page.getByRole('link', {name: 'Add Site'});
		this.blankSiteTemplate = this.page.getByRole('button', {
			name: 'Select Template: Blank Site',
		});
		this.addSiteFrame = this.page.frameLocator('iframe[title="Add Site"]');
		this.alertMessage = this.page.getByRole('alert');
		this.deleteButton = this.page.getByRole('button', {name: 'Delete'});
	}

	async navigateToSiteAdmin() {
		const siteAdmin =
			'http://localhost:8080/group/control_panel/manage/-/sites/sites';

		await this.page.goto(siteAdmin);
	}

	async createBlankSite(siteName) {
		await this.navigateToSiteAdmin();
		await this.addSiteButton.click();
		await this.blankSiteTemplate.click();
		await this.addSiteFrame
			.getByLabel('Name\n\n\t\t\t\n\t\t\t\t\n\n\t\t\t\tRequired')
			.fill(siteName);
		await this.addSiteFrame.getByRole('button', {name: 'Add'}).click();
		await this.assertSuccessMessage();
	}

	async deleteAllSite() {
		await this.navigateToSiteAdmin();
		await this.page.getByLabel('Select All Items on the Page').check();
		await this.deleteButton.click();
		await this.page
			.getByLabel('Delete Sites')
			.getByRole('button', {name: 'Delete'})
			.click();
		await this.assertSuccessMessage();
	}

	async assertSuccessMessage() {
		await expect(this.alertMessage).toContainText('Success');
	}
}
