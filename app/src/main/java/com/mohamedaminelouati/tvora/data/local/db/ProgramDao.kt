package com.mohamedaminelouati.tvora.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mohamedaminelouati.tvora.data.local.entity.ProgramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Query("SELECT * FROM programs WHERE :currentTimestamp >= startTimestamp AND :currentTimestamp < stopTimestamp ORDER BY channelId ASC")
    fun getNowPlaying(currentTimestamp: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE :currentTimestamp >= startTimestamp AND :currentTimestamp < stopTimestamp ORDER BY channelId ASC")
    suspend fun getNowPlayingDirect(currentTimestamp: Long): List<ProgramEntity>

    @Query("SELECT * FROM programs WHERE channelId = :channelId AND stopTimestamp > :dayStart AND startTimestamp < :dayEnd ORDER BY startTimestamp ASC")
    fun getProgramsForChannel(channelId: String, dayStart: Long, dayEnd: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE channelId IN (:channelIds) AND (:dayStart = 0 OR (stopTimestamp > :dayStart AND startTimestamp < :dayEnd)) ORDER BY startTimestamp ASC")
    fun getProgramsForChannelAliases(channelIds: List<String>, dayStart: Long, dayEnd: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE id = :id LIMIT 1")
    fun getProgramById(id: Long): Flow<ProgramEntity?>

    @Query("SELECT * FROM programs WHERE id = :id LIMIT 1")
    suspend fun getProgramByIdDirect(id: Long): ProgramEntity?

    @Query("SELECT * FROM programs WHERE channelId = :channelId AND startTimestamp = :startTimestamp LIMIT 1")
    suspend fun getProgramByChannelAndStart(channelId: String, startTimestamp: Long): ProgramEntity?

    @Query("SELECT * FROM programs WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR directors LIKE '%' || :query || '%' OR actors LIKE '%' || :query || '%' OR presenters LIKE '%' || :query || '%' OR guests LIKE '%' || :query || '%' ORDER BY startTimestamp ASC LIMIT 100")
    fun searchPrograms(query: String): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR subTitle LIKE '%' || :query || '%') AND stopTimestamp > :fromTime ORDER BY startTimestamp ASC")
    suspend fun searchUpcomingProgramsByText(query: String, fromTime: Long): List<ProgramEntity>

    @Query("SELECT * FROM programs WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR subTitle LIKE '%' || :query || '%') AND stopTimestamp > :fromTime ORDER BY startTimestamp ASC")
    fun searchUpcomingProgramsByTextFlow(query: String, fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE directors LIKE '%' || :personName || '%' OR actors LIKE '%' || :personName || '%' OR presenters LIKE '%' || :personName || '%' OR guests LIKE '%' || :personName || '%' ORDER BY startTimestamp ASC")
    fun getProgramsByPerson(personName: String): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE title = :title AND startTimestamp > :fromTime ORDER BY startTimestamp ASC LIMIT 10")
    fun getUpcomingBroadcasts(title: String, fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE LOWER(title) = LOWER(:title) ORDER BY seasonNumber ASC, episodeNumber ASC, startTimestamp ASC")
    fun getProgramsByTitle(title: String): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE (:category = '' OR categories LIKE '%' || :category || '%') AND stopTimestamp > :fromTime ORDER BY startTimestamp ASC LIMIT 250")
    fun getProgramsByCategory(category: String, fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE (:category = '' OR categories LIKE '%' || :category || '%') AND (channelId IN (:channelIds) OR LOWER(channelId) IN (:lowerChannelIds)) AND (:dayStart = 0 OR stopTimestamp > :dayStart) AND (:dayEnd = 0 OR startTimestamp < :dayEnd) AND stopTimestamp > :fromTime ORDER BY startTimestamp ASC LIMIT 2500")
    fun getFilteredProgramsForChannels(category: String, channelIds: List<String>, lowerChannelIds: List<String>, dayStart: Long, dayEnd: Long, fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE (:category = '' OR categories LIKE '%' || :category || '%') AND (:dayStart = 0 OR stopTimestamp > :dayStart) AND (:dayEnd = 0 OR startTimestamp < :dayEnd) AND stopTimestamp > :fromTime ORDER BY startTimestamp ASC LIMIT 5000")
    fun getFilteredProgramsAll(category: String, dayStart: Long, dayEnd: Long, fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE (:category = '' OR categories LIKE '%' || :category || '%') AND (:channelId IS NULL OR channelId = :channelId) AND (:dayStart = 0 OR (stopTimestamp > :dayStart AND startTimestamp < :dayEnd)) AND stopTimestamp > :fromTime ORDER BY startTimestamp ASC LIMIT 2500")
    fun getFilteredPrograms(category: String, channelId: String?, dayStart: Long, dayEnd: Long, fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE stopTimestamp > :startTime AND startTimestamp < :endTime ORDER BY channelId ASC, startTimestamp ASC")
    fun getProgramsByTimeRange(startTime: Long, endTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE stopTimestamp > :startTime AND startTimestamp < :endTime ORDER BY channelId ASC, startTimestamp ASC")
    suspend fun getProgramsByTimeRangeDirect(startTime: Long, endTime: Long): List<ProgramEntity>

    @Query("SELECT * FROM programs WHERE stopTimestamp > :fromTime ORDER BY startTimestamp ASC")
    fun getAllActivePrograms(fromTime: Long): Flow<List<ProgramEntity>>

    @Query("SELECT COUNT(*) FROM programs")
    suspend fun getProgramCount(): Int

    @Query("SELECT COUNT(*) FROM programs WHERE channelId = :channelId")
    suspend fun getProgramCountForChannel(channelId: String): Int

    @Query("SELECT COUNT(*) FROM programs WHERE channelId IN (:channelIds)")
    suspend fun getProgramCountForChannelAliases(channelIds: List<String>): Int

    @Query("SELECT MAX(stopTimestamp) FROM programs")
    suspend fun getLatestProgramTimestamp(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<ProgramEntity>)

    @Query("DELETE FROM programs WHERE stopTimestamp < :cutoffTimestamp")
    suspend fun deleteExpiredPrograms(cutoffTimestamp: Long)

    @Query("DELETE FROM programs WHERE channelId = :channelId")
    suspend fun deleteProgramsForChannel(channelId: String)

    @Query("DELETE FROM programs WHERE channelId IN (:channelIds)")
    suspend fun deleteProgramsForChannels(channelIds: List<String>)

    @Query("DELETE FROM programs")
    suspend fun clearAll()

    @Query("UPDATE programs SET channelId = 'LCP.fr' WHERE LOWER(channelId) IN ('lachaineparlementaire.fr', 'lachaineparlementaire', 'lcp.fr', 'lcp')")
    suspend fun normalizeLcp()

    @Query("UPDATE programs SET channelId = 'TFX.fr' WHERE LOWER(channelId) IN ('nt1.fr', 'nt1', 'tfx.fr', 'tfx')")
    suspend fun normalizeTfx()

    @Query("UPDATE programs SET channelId = 'LEquipe.fr' WHERE LOWER(channelId) IN ('lequipe21.fr', 'lequipe21', 'lequipe.fr', 'lequipe')")
    suspend fun normalizeLEquipe()

    @Query("UPDATE programs SET channelId = 'RMCStory.fr' WHERE LOWER(channelId) IN ('numero23.fr', 'numero23', 'rmcstory.fr', 'rmcstory')")
    suspend fun normalizeRmcStory()

    @Query("UPDATE programs SET channelId = 'RMCLife.fr' WHERE LOWER(channelId) IN ('cherie25.fr', 'cherie25', 'rmclife.fr', 'rmclife')")
    suspend fun normalizeRmcLife()

    @Query("UPDATE programs SET channelId = 'CanalPlus.fr' WHERE LOWER(channelId) IN ('canalplus.fr', 'canalplus', 'canal+', 'c')")
    suspend fun normalizeCanalPlus()

    @Query("UPDATE programs SET channelId = 'TMC.fr' WHERE LOWER(channelId) IN ('tmc.fr', 'tmc', 'telemontecarlo.fr', 'telemontecarlo')")
    suspend fun normalizeTmc()

    @Transaction
    suspend fun normalizeAllChannelIds() {
        normalizeLcp()
        normalizeTfx()
        normalizeLEquipe()
        normalizeRmcStory()
        normalizeRmcLife()
        normalizeCanalPlus()
        normalizeTmc()
    }
}
