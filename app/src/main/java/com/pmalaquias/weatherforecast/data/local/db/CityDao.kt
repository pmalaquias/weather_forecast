package com.pmalaquias.weatherforecast.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM saved_cities ORDER BY addedAt DESC")
    fun getAllCities(): Flow<List<SavedCityEntity>> // Usa Flow para observar mudanças automaticamente

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCityEntity)

    @Query("DELETE FROM saved_cities WHERE cityName = :cityName")
    suspend fun deleteCity(cityName: String)
}