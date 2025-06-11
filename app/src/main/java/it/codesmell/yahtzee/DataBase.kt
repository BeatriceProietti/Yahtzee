import androidx.room.Database
import androidx.room.RoomDatabase
import it.codesmell.yahtzee.GamesData

@Database(entities = [GamesData::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao
}
