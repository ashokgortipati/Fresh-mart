package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartEntity::class,
        WishlistEntity::class,
        RecentlyViewedEntity::class,
        NotificationEntity::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun notificationDao(): NotificationDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "freshmart_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
