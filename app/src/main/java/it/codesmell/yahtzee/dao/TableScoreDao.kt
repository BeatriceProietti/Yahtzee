package it.codesmell.yahtzee.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TableScoreDao {
    @Insert //automatically inserts the TableScore into DB
    suspend fun storeTable(table: TableScore)

    @Delete //automatically deletes the TableScore from DB
    suspend fun deleteTable(table: TableScore)

    //query functions - getting ordered TableScores

    @Query("SELECT * FROM tablescore ORDER BY finalScore DESC")
    fun getTablesByScore(): Flow<List<TableScore>>

    @Query("SELECT * FROM tablescore ORDER BY date ASC")
    fun getTablesByDate(): Flow<List<TableScore>>

}