import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
//import it.codesmell.yahtzee.dao.MatchDao

@Database(
   //entities = [MatchEntity::class, TableScoreEntity::class],
    version = 1
)

@TypeConverters(TypeConverters::class)
abstract class Database : RoomDatabase() {
   // abstract fun matchDao(): MatchDao
}