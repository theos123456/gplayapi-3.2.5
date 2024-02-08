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
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    var title: String = String(),
    var comment: String = String(),
    var commentId: String = String(),
    var userName: String = String(),
    var userPhotoUrl: String = String(),
    var appVersion: String = String(),
    var rating: Int = 0,
    var timeStamp: Long = 0L
) : Parcelable {

    override fun hashCode(): Int {
        return commentId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Review -> other.commentId == commentId
            else -> false
        }
    }

    enum class Filter(val value: String) {
        ALL("ALL"),
        POSITIVE("1"),
        CRITICAL("2"),
        FIVE("5"),
        FOUR("4"),
        THREE("3"),
        TWO("2"),
        ONE("1");
    }
}
