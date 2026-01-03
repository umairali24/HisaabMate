package com.hisaabmate.di

import android.app.Application
import androidx.room.Room
import com.hisaabmate.data.local.HisaabDatabase
import com.hisaabmate.data.repository.HisaabRepositoryImpl
import com.hisaabmate.data.preferences.UserPreferencesRepository
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHisaabDatabase(app: Application): HisaabDatabase {
        return Room.databaseBuilder(
            app,
            HisaabDatabase::class.java,
            "hisaab_db"
        ).fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideHisaabRepository(db: HisaabDatabase): HisaabRepository {
        return HisaabRepositoryImpl(db)
    }



    @Provides
    @Singleton
    fun provideUserPreferencesRepository(dataStore: DataStore<Preferences>): UserPreferencesRepository {
        return UserPreferencesRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
