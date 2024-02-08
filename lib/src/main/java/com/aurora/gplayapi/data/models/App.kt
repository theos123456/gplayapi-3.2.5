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

package com.aurora.gplayapi.data.models

import android.os.Parcelable
import com.aurora.gplayapi.Constants.Restriction
import com.aurora.gplayapi.data.models.details.AppInfo
import com.aurora.gplayapi.data.models.details.Badge
import com.aurora.gplayapi.data.models.details.Chip
import com.aurora.gplayapi.data.models.details.Dependencies
import com.aurora.gplayapi.data.models.details.TestingProgram
import com.aurora.gplayapi.data.models.editor.EditorChoiceReason
import kotlinx.parcelize.Parcelize

@Parcelize
data class App(
    var packageName: String,
    var id: Int = 0,
    var appInfo: AppInfo = AppInfo(),
    var categoryArtwork: Artwork = Artwork(),
    var categoryId: Int = 0,
    var categoryName: String = String(),
    var categoryStreamUrl: String? = String(),
    var changes: String = String(),
    var chips: MutableList<Chip> = mutableListOf(),
    var containsAds: Boolean = false,
    var coverArtwork: Artwork = Artwork(),
    var dependencies: Dependencies = Dependencies(),
    var description: String = String(),
    var detailsStreamUrl: String? = String(),
    var detailsPostAcquireStreamUrl: String? = String(),
    var developerAddress: String = String(),
    var developerEmail: String = String(),
    var developerName: String = String(),
    var developerWebsite: String = String(),
    var displayBadges: MutableList<Badge> = mutableListOf(),
    var displayName: String = String(),
    var editorReason: EditorChoiceReason? = null,
    var downloadString: String = String(),
    var earlyAccess: Boolean = false,
    var fileList: MutableList<File> = mutableListOf(),
    var footerHtml: String = String(),
    var iconArtwork: Artwork = Artwork(),
    var infoBadges: MutableList<Badge> = mutableListOf(),
    var inPlayStore: Boolean = false,
    var installs: Long = 0,
    var instantAppLink: String = String(),
    var isFree: Boolean = false,
    var isInstalled: Boolean = false,
    var isSystem: Boolean = false,
    var labeledRating: String = String(),
    var liveStreamUrl: String? = String(),
    var offerDetails: MutableMap<String, String> = mutableMapOf(),
    var offerType: Int = 0,
    var permissions: MutableList<String> = mutableListOf(),
    var price: String = String(),
    var promotionStreamUrl: String? = String(),
    var rating: Rating = Rating(),
    var relatedLinks: MutableMap<String, String> = mutableMapOf(),
    var restriction: Restriction = Restriction.NOT_RESTRICTED,
    var screenshots: MutableList<Artwork> = mutableListOf(),
    var shareUrl: String = String(),
    var shortDescription: String = String(),
    var size: Long = 0,
    var targetSdk: Int = 21,
    var testingProgram: TestingProgram? = null,
    var userReview: Review = Review(),
    var updatedOn: String = String(),
    var versionCode: Int = 0,
    var versionName: String = String(),
    var videoArtwork: Artwork = Artwork(),
    var certificateHashList: MutableList<String> = mutableListOf(),
    var certificateSetList: MutableList<EncodedCertificateSet> = mutableListOf()
) : Parcelable {

    override fun hashCode(): Int {
        return packageName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is App -> packageName == other.packageName
            else -> false
        }
    }
}
