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
data class Rating(
    var average: Float = 0f,
    var oneStar: Long = 0L,
    var twoStar: Long = 0L,
    var threeStar: Long = 0L,
    var fourStar: Long = 0L,
    var fiveStar: Long = 0L,
    var thumbsUp: Long = 0L,
    var thumbsDown: Long = 0L,
    var label: String = String(),
    var abbreviatedLabel: String = String()
) : Parcelable
