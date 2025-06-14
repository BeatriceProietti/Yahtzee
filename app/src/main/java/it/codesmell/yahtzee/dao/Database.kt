import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
//import it.codesmell.yahtzee.dao.MatchDao

@Database(
    //entities = [MatchEntity::class, TableScoreEntity::class],
    version = 1
)
@TypeConverters(TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun matchDao(): MatchDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase { //singleton
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
