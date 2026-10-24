package com.mohamedaminelouati.tvora.domain.translation

import com.mohamedaminelouati.tvora.domain.model.Program
import kotlinx.coroutines.flow.StateFlow

interface TranslationManager {
    val isEnglishModelDownloaded: StateFlow<Boolean>
    val isDownloadingEnglishModel: StateFlow<Boolean>

    suspend fun checkModelStatus()
    suspend fun downloadEnglishModel(wifiOnly: Boolean): Result<Unit>
    suspend fun deleteEnglishModel(): Result<Unit>
    suspend fun translateText(text: String): String?
    suspend fun translateTitle(title: String): String
    suspend fun translateProgram(programId: Long, title: String, description: String): Pair<String, String>?
    suspend fun translateProgram(program: Program): Program
    suspend fun translatePrograms(programs: List<Program>): List<Program>
}
