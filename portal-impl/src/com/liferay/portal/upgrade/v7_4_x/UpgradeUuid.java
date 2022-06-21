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
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Amos Fong
 */
public class UpgradeUuid extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgradeUuid("AccountEntry", "accountEntryId");
		upgradeUuid("AccountGroup", "accountGroupId");
		upgradeUuid("CommerceCatalog", "commerceCatalogId");
		upgradeUuid("CommerceChannel", "commerceChannelId");
		upgradeUuid("CIWarehouse", "CIWarehouseId");
		upgradeUuid("CIWarehouseItem", "CIWarehouseItemId");
		upgradeUuid("CommerceOrderItem", "commerceOrderItemId");
		upgradeUuid("CommerceOrderNote", "commerceOrderNoteId");
		upgradeUuid("CommerceOrderType", "commerceOrderTypeId");
		upgradeUuid("CommerceOrderTypeRel", "commerceOrderTypeRelId");
		upgradeUuid("CommerceShipment", "commerceShipmentId");
		upgradeUuid("CommerceShipmentItem", "commerceShipmentItemId");
		upgradeUuid("CommerceTermEntry", "commerceTermEntryId");
		upgradeUuid("COREntry", "COREntryId");
		upgradeUuid("CPTaxCategory", "CPTaxCategoryId");
		upgradeUuid("DispatchTrigger", "dispatchTriggerId");
	}

	protected void upgradeUuid(String tableName, String primKeyColumnName)
		throws Exception {

		if (!hasTable(tableName)) {
			return;
		}

		if (!hasColumn(tableName, "uuid_")) {
			alterTableAddColumn(tableName, "uuid_", "VARCHAR(75) null");
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			StringBundler selectSB = new StringBundler(5);

			selectSB.append("select ");
			selectSB.append(primKeyColumnName);
			selectSB.append(" from ");
			selectSB.append(tableName);
			selectSB.append(" where uuid_ is null or uuid_ = ''");

			StringBundler updateSB = new StringBundler(5);

			updateSB.append("update ");
			updateSB.append(tableName);
			updateSB.append(" set uuid_ = ? where ");
			updateSB.append(primKeyColumnName);
			updateSB.append(" = ?");

			try (PreparedStatement preparedStatement1 =
					connection.prepareStatement(selectSB.toString());
				PreparedStatement preparedStatement2 =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection.prepareStatement(updateSB.toString()));
				ResultSet resultSet = preparedStatement1.executeQuery()) {

				while (resultSet.next()) {
					preparedStatement2.setString(1, PortalUUIDUtil.generate());
					preparedStatement2.setLong(2, resultSet.getLong(1));

					preparedStatement2.addBatch();
				}

				preparedStatement2.executeBatch();
			}
		}
	}

}