package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
  @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
  fun getUserProfile(): Flow<UserEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUserProfile(user: UserEntity)

  @Query("DELETE FROM user_profile")
  suspend fun clearUserProfile()

  @Query("SELECT siteId FROM saved_sites")
  fun getSavedSiteIds(): Flow<List<String>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSite(site: SavedSiteEntity)

  @Query("DELETE FROM saved_sites WHERE siteId = :siteId")
  suspend fun removeSavedSite(siteId: String)

  @Query("SELECT * FROM downloaded_maps")
  fun getDownloadedMaps(): Flow<List<DownloadedMapEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addDownloadedMap(map: DownloadedMapEntity)

  @Query("DELETE FROM downloaded_maps WHERE cityId = :cityId")
  suspend fun deleteDownloadedMap(cityId: String)

  @Query("SELECT * FROM translation_history ORDER BY timestamp DESC LIMIT 20")
  fun getTranslationHistory(): Flow<List<TranslationHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addTranslationHistory(history: TranslationHistoryEntity)

  @Query("DELETE FROM translation_history")
  suspend fun clearTranslationHistory()
}
