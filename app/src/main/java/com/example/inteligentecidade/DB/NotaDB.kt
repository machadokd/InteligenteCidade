package com.example.inteligentecidade.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.inteligentecidade.DAO.NotaDAO
import com.example.inteligentecidade.Entities.Nota
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Nota::class), version = 1, exportSchema = false)
public abstract class NotaDB : RoomDatabase() {

    abstract fun NotaDao(): NotaDAO

    private class NotaDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {


                }
            }
        }
    }
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotaDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotaDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotaDB::class.java,
                    "notas_database",
                )
                    //estratégia de destruição
                    .fallbackToDestructiveMigration()
                    .addCallback(NotaDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}