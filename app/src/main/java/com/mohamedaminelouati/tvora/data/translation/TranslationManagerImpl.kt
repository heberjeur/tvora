package com.mohamedaminelouati.tvora.data.translation

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.mohamedaminelouati.tvora.data.local.db.ProgramTranslationDao
import com.mohamedaminelouati.tvora.data.local.entity.ProgramTranslationEntity
import com.mohamedaminelouati.tvora.data.local.prefs.UserPreferences
import com.mohamedaminelouati.tvora.domain.translation.TranslationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationManagerImpl @Inject constructor(
    private val programTranslationDao: ProgramTranslationDao,
    private val userPreferences: UserPreferences
) : TranslationManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val modelManager = RemoteModelManager.getInstance()

    private val _isEnglishModelDownloaded = MutableStateFlow(false)
    override val isEnglishModelDownloaded: StateFlow<Boolean> = _isEnglishModelDownloaded.asStateFlow()

    private val _isDownloadingEnglishModel = MutableStateFlow(false)
    override val isDownloadingEnglishModel: StateFlow<Boolean> = _isDownloadingEnglishModel.asStateFlow()

    private var englishTranslator: Translator? = null

    init {
        scope.launch {
            checkModelStatus()
        }
    }

    override suspend fun checkModelStatus() {
        withContext(Dispatchers.IO) {
            try {
                val models = modelManager.getDownloadedModels(TranslateRemoteModel::class.java).await()
                _isEnglishModelDownloaded.value = models.any { it.language == TranslateLanguage.ENGLISH }
            } catch (e: Exception) {
                Log.w("TranslationManager", "Error checking model status", e)
                _isEnglishModelDownloaded.value = false
            }
        }
    }

    override suspend fun downloadEnglishModel(wifiOnly: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            _isDownloadingEnglishModel.value = true
            try {
                val model = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()
                val conditions = DownloadConditions.Builder().apply {
                    if (wifiOnly) requireWifi()
                }.build()
                modelManager.download(model, conditions).await()
                _isEnglishModelDownloaded.value = true
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("TranslationManager", "Failed to download model", e)
                Result.failure(e)
            } finally {
                _isDownloadingEnglishModel.value = false
            }
        }
    }

    override suspend fun deleteEnglishModel(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val model = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()
                modelManager.deleteDownloadedModel(model).await()
                englishTranslator?.close()
                englishTranslator = null
                _isEnglishModelDownloaded.value = false
                programTranslationDao.deleteTranslationsForLanguage("en")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("TranslationManager", "Failed to delete model", e)
                Result.failure(e)
            }
        }
    }

    private fun getOrCreateTranslator(): Translator {
        val existing = englishTranslator
        if (existing != null) return existing
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.FRENCH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val created = Translation.getClient(options)
        englishTranslator = created
        return created
    }

    override suspend fun translateText(text: String): String? {
        if (text.isBlank()) return text
        return withContext(Dispatchers.IO) {
            try {
                val translator = getOrCreateTranslator()
                val wifiOnly = userPreferences.translateWifiOnly.first()
                val conditions = DownloadConditions.Builder().apply {
                    if (wifiOnly) requireWifi()
                }.build()
                translator.downloadModelIfNeeded(conditions).await()
                _isEnglishModelDownloaded.value = true
                translator.translate(text).await()
            } catch (e: Exception) {
                Log.w("TranslationManager", "Error translating text", e)
                null
            }
        }
    }

    override suspend fun translateProgram(programId: Long, title: String, description: String): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            val cached = programTranslationDao.getTranslationSync(programId, "en")
            if (cached != null) {
                return@withContext Pair(cached.translatedTitle, cached.translatedDescription)
            }

            val autoTranslate = userPreferences.autoTranslateEnabled.first()
            if (!autoTranslate) return@withContext null

            try {
                val translator = getOrCreateTranslator()
                val wifiOnly = userPreferences.translateWifiOnly.first()
                val conditions = DownloadConditions.Builder().apply {
                    if (wifiOnly) requireWifi()
                }.build()
                translator.downloadModelIfNeeded(conditions).await()
                _isEnglishModelDownloaded.value = true

                val translatedTitle = if (title.isNotBlank()) {
                    translator.translate(title).await()
                } else title

                val translatedDescription = if (description.isNotBlank()) {
                    translator.translate(description).await()
                } else description

                val entity = ProgramTranslationEntity(
                    programId = programId,
                    targetLanguage = "en",
                    translatedTitle = translatedTitle,
                    translatedDescription = translatedDescription
                )
                programTranslationDao.insertTranslation(entity)
                Pair(translatedTitle, translatedDescription)
            } catch (e: Exception) {
                Log.w("TranslationManager", "Failed to translate program $programId", e)
                null
            }
        }
    }
}
