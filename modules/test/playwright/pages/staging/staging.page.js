/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const {expect} = require('@playwright/test');

export class StagingPage {
	constructor(page) {
		this.page = page;
		this.localLiveOption = this.page.locator(
			'[id="_com_liferay_staging_configuration_web_portlet_StagingConfigurationPortlet_local"]'
		);
		this.selectAllOption = this.page.getByText('Select All');
		this.saveButton = this.page.getByRole('button', {name: 'Save'});
		this.stagingProcessStatus = this.page.locator(
			'[id="_com_liferay_staging_processes_web_portlet_StagingProcessesPortlet_publishLayoutProcesses_1"]'
		);
	}

	async navigateToStagingAdmin(siteName) {
		const stagingAdmin = `http://localhost:8080/group/${siteName}/~/control_panel/manage?p_p_id=com_liferay_staging_processes_web_portlet_StagingProcessesPortlet`;

		await this.page.goto(stagingAdmin);
	}

	async navigateToStagingSite(siteName) {
		const stagingSite = `http://localhost:8080/web/${siteName}-staging`;

		await this.page.goto(stagingSite);
	}

	async enableLocalStaging(siteName) {
		await this.navigateToStagingAdmin(siteName);
		await this.localLiveOption.click();
		await this.selectAllOption.click();
		this.page.on('dialog', async (dialog) => {
			console.log(`Dialog message: ${dialog.message()}`);
			await dialog.accept();
		});
		await this.saveButton.click();
		await expect(this.stagingProcessStatus).toContainText('Successful');
		await this.page.waitForTimeout(3000);
	}
}
