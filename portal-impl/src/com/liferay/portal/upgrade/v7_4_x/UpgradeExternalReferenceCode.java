/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Amos Fong
 */
public class UpgradeExternalReferenceCode extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeExternalReference("AccountEntry", "accountEntryId");
		upgradeExternalReference("AccountGroup", "accountGroupId");
		upgradeExternalReference("Address", "addressId");
		upgradeExternalReference("AssetCategory", "categoryId");
		upgradeExternalReference("AssetVocabulary", "vocabularyId");
		upgradeExternalReference("BlogsEntry", "entryId");
		upgradeExternalReference("CIWarehouse", "CIWarehouseId");
		upgradeExternalReference("CIWarehouseItem", "CIWarehouseItemId");
		upgradeExternalReference("CommerceCatalog", "commerceCatalogId");
		upgradeExternalReference("CommerceChannel", "commerceChannelId");
		upgradeExternalReference("CommerceDiscount", "commerceDiscountId");
		upgradeExternalReference("CommerceOrder", "commerceOrderId");
		upgradeExternalReference(
			"CommerceOrderItem", "commerceOrderItemId");
		upgradeExternalReference(
			"CommerceOrderNote", "commerceOrderNoteId");
		upgradeExternalReference(
			"CommerceOrderType", "commerceOrderTypeId");
		upgradeExternalReference(
			"CommerceOrderTypeRel", "commerceOrderTypeRelId");
		upgradeExternalReference(
			"CommercePriceEntry", "commercePriceEntryId");
		upgradeExternalReference(
			"CommercePriceList", "commercePriceListId");
		upgradeExternalReference(
			"CommercePriceModifier", "commercePriceModifierId");
		upgradeExternalReference(
			"CommercePricingClass", "commercePricingClassId");
		upgradeExternalReference(
			"CommerceTermEntry", "commerceTermEntryId");
		upgradeExternalReference(
			"CommerceTierPriceEntry", "commerceTierPriceEntryId");
		upgradeExternalReference("COREntry", "COREntryId");
		upgradeExternalReference(
			"CPAttachmentFileEntry", "CPAttachmentFileEntryId");
		upgradeExternalReference("CPInstance", "CPInstanceId");
		upgradeExternalReference("CPOption", "CPOptionId");
		upgradeExternalReference("CPOptionValue", "CPOptionValueId");
		upgradeExternalReference("CProduct", "CProductId");
		upgradeExternalReference("CPTaxCategory", "CPTaxCategoryId");
		upgradeExternalReference("DLFileEntry", "fileEntryId");
		upgradeExternalReference("KBArticle", "kbArticleId");
		upgradeExternalReference("KBFolder", "kbFolderId");
		upgradeExternalReference("MBMessage", "messageId");
		upgradeExternalReference("ObjectEntry", "objectEntryId");
		upgradeExternalReference("Organization_", "organizationId");
		upgradeExternalReference("RemoteAppEntry", "remoteAppEntryId");
		upgradeExternalReference("User_", "userId");
		upgradeExternalReference("UserGroup", "userGroupId");
		upgradeExternalReference("WikiNode", "nodeId");
		upgradeExternalReference("WikiPage", "pageId");
	}

	protected void upgradeExternalReference(
			String tableName, String primKeyColumnName)
		throws Exception {

		if (!hasTable(tableName)) {
			return;
		}

		if (!hasColumn(tableName, "externalReferenceCode")) {
			alterTableAddColumn(
				tableName, "externalReferenceCode", "VARCHAR(75)");
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			StringBundler selectSB = new StringBundler(7);

			selectSB.append("select ");
			selectSB.append(primKeyColumnName);

			boolean hasUuid = hasColumn(tableName, "uuid_");

			if (hasUuid) {
				selectSB.append(", uuid_");
			}

			selectSB.append(" from ");
			selectSB.append(tableName);
			selectSB.append(" where externalReferenceCode is null or ");
			selectSB.append("externalReferenceCode = ''");

			StringBundler updateSB = new StringBundler(5);

			updateSB.append("update ");
			updateSB.append(tableName);
			updateSB.append(" set externalReferenceCode = ? where ");
			updateSB.append(primKeyColumnName);
			updateSB.append(" = ?");

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(selectSB.toString());
				ResultSet resultSet = preparedStatement1.executeQuery();
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection.prepareStatement(updateSB.toString()))) {

				while (resultSet.next()) {
					long primKey = resultSet.getLong(1);

					if (hasUuid) {
						String uuid = resultSet.getString(2);

						preparedStatement2.setString(1, uuid);
					}
					else {
						preparedStatement2.setString(
							1, String.valueOf(primKey));
					}

					preparedStatement2.setLong(2, primKey);

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

}