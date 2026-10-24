package com.mohamedaminelouati.tvora.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mohamedaminelouati.tvora.data.local.entity.ProgramTranslationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramTranslationDao {

    @Query("SELECT * FROM program_translations WHERE programId = :programId AND targetLanguage = :targetLanguage LIMIT 1")
    fun getTranslation(programId: Long, targetLanguage: String): Flow<ProgramTranslationEntity?>

    @Query("SELECT * FROM program_translations WHERE programId = :programId AND targetLanguage = :targetLanguage LIMIT 1")
    suspend fun getTranslationSync(programId: Long, targetLanguage: String): ProgramTranslationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: ProgramTranslationEntity)

    @Query("DELETE FROM program_translations WHERE targetLanguage = :targetLanguage")
    suspend fun deleteTranslationsForLanguage(targetLanguage: String)

    @Query("DELETE FROM program_translations")
    suspend fun clearAllTranslations()
}
