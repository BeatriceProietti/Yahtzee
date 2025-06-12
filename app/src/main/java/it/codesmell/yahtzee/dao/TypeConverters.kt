package it.codesmell.yahtzee.dao

import androidx.room.TypeConverter

class TypeConverters {
    @TypeConverter
    fun fromGameMode(value: GameMode): String = value.name

    @TypeConverter
    fun toGameMode(value: String): GameMode = GameMode.valueOf(value)
}
