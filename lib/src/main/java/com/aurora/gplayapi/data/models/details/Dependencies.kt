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

package com.aurora.gplayapi.data.models.details

import android.os.Parcelable
import com.aurora.gplayapi.data.models.App
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dependencies(
    var dependentPackages: MutableList<String> = mutableListOf(),
    var dependentSplits: MutableList<String> = mutableListOf(),
    var dependentLibraries: MutableList<App> = mutableListOf(),
    var targetSDK: Int = -1,
    var totalSize: Long = -1L
) : Parcelable
