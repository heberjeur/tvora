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
import com.mohamedaminelouati.tvora.domain.model.Program
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
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationManagerImpl @Inject constructor(
    private val programTranslationDao: ProgramTranslationDao,
    private val userPreferences: UserPreferences
) : TranslationManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val modelManager = RemoteModelManager.getInstance()
    private val titleCache = ConcurrentHashMap<String, String>()

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
                val frenchModel = TranslateRemoteModel.Builder(TranslateLanguage.FRENCH).build()
                val isFrenchReady = try {
                    modelManager.isModelDownloaded(frenchModel).await()
                } catch (_: Exception) {
                    false
                }

                if (isFrenchReady) {
                    _isEnglishModelDownloaded.value = true
                    return@withContext
                }

                val models = modelManager.getDownloadedModels(TranslateRemoteModel::class.java).await()
                _isEnglishModelDownloaded.value = models.any { it.language == TranslateLanguage.FRENCH }
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
                val conditions = DownloadConditions.Builder().apply {
                    if (wifiOnly) requireWifi()
                }.build()

                val translator = getOrCreateTranslator()
                translator.downloadModelIfNeeded(conditions).await()

                val frenchModel = TranslateRemoteModel.Builder(TranslateLanguage.FRENCH).build()
                try {
                    modelManager.download(frenchModel, conditions).await()
                } catch (e: Exception) {
                    Log.w("TranslationManager", "Explicit french model download skipped", e)
                }

                _isEnglishModelDownloaded.value = true
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("TranslationManager", "Failed to download model", e)
                _isEnglishModelDownloaded.value = false
                Result.failure(e)
            } finally {
                _isDownloadingEnglishModel.value = false
            }
        }
    }

    override suspend fun deleteEnglishModel(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                englishTranslator?.close()
                englishTranslator = null

                val frenchModel = TranslateRemoteModel.Builder(TranslateLanguage.FRENCH).build()
                val englishModel = TranslateRemoteModel.Builder(TranslateLanguage.ENGLISH).build()

                try {
                    modelManager.deleteDownloadedModel(frenchModel).await()
                } catch (e: Exception) {
                    Log.w("TranslationManager", "Could not delete french model", e)
                }
                try {
                    modelManager.deleteDownloadedModel(englishModel).await()
                } catch (e: Exception) {
                    Log.w("TranslationManager", "Could not delete english model", e)
                }

                _isEnglishModelDownloaded.value = false
                titleCache.clear()
                programTranslationDao.deleteTranslationsForLanguage("en")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("TranslationManager", "Failed to delete model", e)
                _isEnglishModelDownloaded.value = false
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

    override suspend fun translateTitle(title: String): String {
        if (title.isBlank()) return title
        val cached = titleCache[title]
        if (cached != null) return cached

        val autoTranslate = userPreferences.autoTranslateEnabled.first()
        if (!autoTranslate) return title

        val lang = userPreferences.appLanguage.first()
        if (lang != "en") return title

        return withContext(Dispatchers.IO) {
            try {
                val translator = getOrCreateTranslator()
                val wifiOnly = userPreferences.translateWifiOnly.first()
                val conditions = DownloadConditions.Builder().apply {
                    if (wifiOnly) requireWifi()
                }.build()
                translator.downloadModelIfNeeded(conditions).await()
                _isEnglishModelDownloaded.value = true
                val translated = translator.translate(title).await()
                titleCache[title] = translated
                translated
            } catch (e: Exception) {
                Log.w("TranslationManager", "Failed to translate title: $title", e)
                title
            }
        }
    }

    override suspend fun translateProgram(programId: Long, title: String, description: String): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            val cached = programTranslationDao.getTranslationSync(programId, "en")
            if (cached != null) {
                titleCache[title] = cached.translatedTitle
                return@withContext Pair(cached.translatedTitle, cached.translatedDescription)
            }

            val autoTranslate = userPreferences.autoTranslateEnabled.first()
            if (!autoTranslate) return@withContext null

            val lang = userPreferences.appLanguage.first()
            if (lang != "en") return@withContext null

            try {
                val translator = getOrCreateTranslator()
                val wifiOnly = userPreferences.translateWifiOnly.first()
                val conditions = DownloadConditions.Builder().apply {
                    if (wifiOnly) requireWifi()
                }.build()
                translator.downloadModelIfNeeded(conditions).await()
                _isEnglishModelDownloaded.value = true

                val translatedTitle = if (title.isNotBlank()) {
                    titleCache[title] ?: run {
                        val res = translator.translate(title).await()
                        titleCache[title] = res
                        res
                    }
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

    override suspend fun translateProgram(program: Program): Program {
        val autoTranslate = userPreferences.autoTranslateEnabled.first()
        if (!autoTranslate) return program

        val lang = userPreferences.appLanguage.first()
        if (lang != "en") return program

        val cached = programTranslationDao.getTranslationSync(program.id, "en")
        if (cached != null) {
            titleCache[program.title] = cached.translatedTitle
            return program.copy(
                title = cached.translatedTitle,
                description = cached.translatedDescription
            )
        }

        val pair = translateProgram(program.id, program.title, program.description)
        return if (pair != null) {
            program.copy(title = pair.first, description = pair.second)
        } else {
            program
        }
    }

    override suspend fun translatePrograms(programs: List<Program>): List<Program> {
        val autoTranslate = userPreferences.autoTranslateEnabled.first()
        if (!autoTranslate) return programs

        val lang = userPreferences.appLanguage.first()
        if (lang != "en") return programs

        return withContext(Dispatchers.IO) {
            val translator = try {
                if (_isEnglishModelDownloaded.value) getOrCreateTranslator() else null
            } catch (_: Exception) {
                null
            }

            programs.map { prog ->
                val cachedTitle = titleCache[prog.title]
                if (cachedTitle != null) {
                    prog.copy(title = cachedTitle)
                } else if (translator != null && prog.title.isNotBlank()) {
                    val translatedTitle = try {
                        val res = translator.translate(prog.title).await()
                        titleCache[prog.title] = res
                        res
                    } catch (_: Exception) {
                        prog.title
                    }
                    prog.copy(title = translatedTitle)
                } else {
                    prog
                }
            }
        }
    }
}
