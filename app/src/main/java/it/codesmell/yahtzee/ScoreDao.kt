import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.codesmell.yahtzee.GamesData

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: GamesData)

    @Query("SELECT * FROM scores ORDER BY date DESC")
    suspend fun getAllScores(): List<GamesData>
}
