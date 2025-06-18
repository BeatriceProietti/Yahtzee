package it.codesmell.yahtzee.dao

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TableScore::class],
    version = 1
)

abstract class TableScoreDatabase: RoomDatabase() {
    abstract val dao: TableScoreDao
}