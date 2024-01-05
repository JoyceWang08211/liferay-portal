/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const {expect} = require('@playwright/test');

export class PageAdminPage {
	constructor(page) {
		this.page = page;
		this.addPageButton = this.page.getByLabel('New', {exact: true});
		this.pageOption = this.page.getByRole('menuitem', {
			name: 'Page',
			exact: true,
		});
		this.widgetPageTemplate = this.page.getByRole('button', {
			name: 'Widget Page',
		});
		this.addPageFrame = this.page.frameLocator('iframe[title="Add Page"]');
		this.alertMessage = this.page.getByRole('alert');
	}

	async navigateToPageAdmin(siteName) {
		const pageAdmin = `http://localhost:8080/group/${siteName}/~/control_panel/manage?p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet`;

		await this.page.goto(pageAdmin);
	}

	async addWidgetPage(pageName, siteName) {
		await this.navigateToPageAdmin(siteName);
		await this.addPageButton.click();
		await this.pageOption.click();
		await this.widgetPageTemplate.click();
		await this.addPageFrame.getByPlaceholder('Add Page Name').click();
		await this.addPageFrame
			.getByPlaceholder('Add Page Name')
			.fill(pageName);
		await this.addPageFrame.getByRole('button', {name: 'Add'}).click();
		await this.assertSuccessMessage();
	}

	async assertSuccessMessage() {
		await expect(this.alertMessage).toContainText(
			'Success:The page was created successfully.'
		);
	}
}
