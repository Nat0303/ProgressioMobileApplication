package com.example.progressiomobileapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.progressiomobileapp.data.dao.*

// Define the database class
@Database(entities = [User::class, Admin::class, Task::class, ChecklistItem::class, Comment::class, History::class, UserHistory::class, Notification::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Accessor method for UserDao
    abstract fun userDao(): UserDao

    // Accessor method for AdminDao
    abstract fun adminDao(): AdminDao

    // Accessor method for TaskDao
    abstract fun taskDao(): TaskDao

    // Accessor method for ChecklistItemDao
    abstract fun checklistItemDao(): ChecklistItemDao

    // Accessor method for CommentDao
    abstract fun commentDao(): CommentDao

    // Accessor method for HistoryDao
    abstract fun historyDao(): HistoryDao

    // Accessor method for UserHistoryDao
    abstract fun userHistoryDao(): UserHistoryDao

    // Accessor method for NotificationDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton pattern to get a single instance of the database
        fun getDatabase(context: Context): AppDatabase {
            // Check if instance is null
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"  // The name of your database
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}