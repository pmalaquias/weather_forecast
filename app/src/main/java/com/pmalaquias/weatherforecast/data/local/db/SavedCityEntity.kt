package com.pmalaquias.weatherforecast.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey
    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long = System.currentTimeMillis()
)
