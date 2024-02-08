/*
 *     GPlayApi
 *     Copyright (C) 2020  Aurora OSS
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.Constants.PATCH_FORMAT
import com.aurora.gplayapi.DeliveryResponse
import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.ListResponse
import com.aurora.gplayapi.ResponseWrapper
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.File
import com.aurora.gplayapi.data.providers.HeaderProvider
import com.aurora.gplayapi.exceptions.ApiException
import com.aurora.gplayapi.network.IHttpClient
import java.io.IOException

class PurchaseHelper(authData: AuthData) : BaseHelper(authData) {

    override fun using(httpClient: IHttpClient) = apply {
        this.httpClient = httpClient
    }

    /**
     * @param offset
     * @param getAllAppDetails
     * <code>false</code>: the returned App instances will only contain basic properties
     * <code>true</code>: all App details are retrieved using AppDetailsHelper
     * @return list of apps purchased by the selected account, starting at offset.
     * The number of entries is defined by Google (usually less than 20)
     */
    fun getPurchaseHistory(offset: Int = 0, getAllAppDetails: Boolean = true): List<App> {
        val headers: MutableMap<String, String> = HeaderProvider.getDefaultHeaders(authData)
        val params: MutableMap<String, String> = mutableMapOf(
            "o" to "$offset"
        )
        val playResponse = httpClient.get(
            GooglePlayApi.PURCHASE_HISTORY_URL,
            headers,
            params
        )

        val purchaseAppList: MutableList<App> = mutableListOf()
        val listResponse: ListResponse = getListResponseFromBytes(playResponse.responseBytes)
        if (listResponse.itemCount > 0) {
            for (item in listResponse.itemList) {
                for (subItem in item.subItemList) {
                    if (item.subItemCount > 0) {
                        if (item.hasAnnotations() &&
                            item.annotations.hasPurchaseHistoryDetails() &&
                            item.annotations.purchaseHistoryDetails.hasPurchaseStatus()
                        ) {
                            continue
                        }
                        purchaseAppList.addAll(getAppsFromItem(subItem))
                    }
                }
            }
        }
        if (!getAllAppDetails) {
            return purchaseAppList
        }
        return AppDetailsHelper(authData).getAppByPackageName(
            purchaseAppList.map { it.packageName }
                .distinct()
        )
    }

    @Throws(IOException::class)
    fun getDeliveryToken(packageName: String, versionCode: Int, offerType: Int, certificateHash: String): String {
        val params: MutableMap<String, String> = HashMap()
        params["ot"] = offerType.toString()
        params["doc"] = packageName
        params["vc"] = versionCode.toString()

        if (certificateHash.isNotEmpty()) {
            params["ch"] = certificateHash
        }

        val playResponse = httpClient.post(
            GooglePlayApi.PURCHASE_URL,
            HeaderProvider.getDefaultHeaders(authData),
            params
        )

        return if (playResponse.isSuccessful) {
            val payload = getPayLoadFromBytes(playResponse.responseBytes)
            payload.buyResponse.encodedDeliveryToken
        } else {
            ""
        }
    }

    @Throws(IOException::class)
    fun getDeliveryResponse(
        packageName: String,
        installedVersionCode: Int = 0,
        updateVersionCode: Int,
        offerType: Int,
        patchFormats: Array<PATCH_FORMAT> = arrayOf(
            PATCH_FORMAT.GDIFF,
            PATCH_FORMAT.GZIPPED_GDIFF,
            PATCH_FORMAT.GZIPPED_BSDIFF
        ),
        deliveryToken: String
    ): DeliveryResponse {
        val params: MutableMap<String, String> = HashMap()
        params["ot"] = offerType.toString()
        params["doc"] = packageName
        params["vc"] = updateVersionCode.toString()

        /*if (installedVersionCode > 0) {
            params["bvc"] = installedVersionCode.toString();
            params["pf"] = patchFormats[0].value.toString();
        }*/

        if (deliveryToken.isNotEmpty()) {
            params["dtok"] = deliveryToken
        }

        val playResponse =
            httpClient.get(
                GooglePlayApi.DELIVERY_URL,
                HeaderProvider.getDefaultHeaders(authData),
                params
            )
        val payload = ResponseWrapper.parseFrom(playResponse.responseBytes).payload
        return payload.deliveryResponse
    }

    @Throws(Exception::class)
    fun purchase(packageName: String, versionCode: Int, offerType: Int, certificateHash: String = ""): List<File> {
        val deliveryToken = getDeliveryToken(packageName, versionCode, offerType, certificateHash)
        val deliveryResponse = getDeliveryResponse(
            packageName = packageName,
            updateVersionCode = versionCode,
            offerType = offerType,
            deliveryToken = deliveryToken
        )

        when (deliveryResponse.status) {
            1 ->
                return getDownloadsFromDeliveryResponse(packageName, versionCode, deliveryResponse)

            2 ->
                throw ApiException.AppNotSupported()

            3 ->
                throw ApiException.AppNotPurchased()

            7 ->
                throw ApiException.AppRemoved()

            9 ->
                throw ApiException.AppNotSupported()

            else ->
                throw ApiException.Unknown()
        }
    }

    private fun getDownloadsFromDeliveryResponse(
        packageName: String?,
        versionCode: Int,
        deliveryResponse: DeliveryResponse?
    ): List<File> {
        val fileList: MutableList<File> = mutableListOf()
        if (deliveryResponse != null) {
            // Add base apk
            val androidAppDeliveryData = deliveryResponse.appDeliveryData
            if (androidAppDeliveryData != null) {
                fileList.add(
                    File().apply {
                        name = "base.apk"
                        url = androidAppDeliveryData.downloadUrl
                        size = androidAppDeliveryData.downloadSize
                        type = File.FileType.BASE
                    }
                )

                // Obb & patches (if any)
                val fileMetadataList = deliveryResponse.appDeliveryData.additionalFileList
                if (fileMetadataList != null) {
                    for (appFileMetadata in fileMetadataList) {
                        val isOBB = appFileMetadata.fileType == 0
                        val fileType = if (isOBB) "main" else "patch"
                        fileList.add(
                            File().apply {
                                name = "$fileType.$versionCode.$packageName.obb"
                                url = appFileMetadata.downloadUrl
                                size = appFileMetadata.size
                                type = if (isOBB) File.FileType.OBB else File.FileType.PATCH
                            }
                        )
                    }
                }

                // Add split apks (if any)
                val splitDeliveryDataList = deliveryResponse.appDeliveryData.splitDeliveryDataList
                if (fileMetadataList != null) {
                    for (splitDeliveryData in splitDeliveryDataList) {
                        fileList.add(
                            File().apply {
                                name = "${splitDeliveryData.name}.apk"
                                url = splitDeliveryData.downloadUrl
                                size = splitDeliveryData.downloadSize
                                type = File.FileType.SPLIT
                            }
                        )
                    }
                }
            }
        }

        if (fileList.isEmpty()) {
            throw ApiException.Unknown()
        }

        return fileList
    }
}
