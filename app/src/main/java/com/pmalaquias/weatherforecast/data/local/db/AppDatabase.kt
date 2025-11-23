package com.pmalaquias.weatherforecast.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SavedCityEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}