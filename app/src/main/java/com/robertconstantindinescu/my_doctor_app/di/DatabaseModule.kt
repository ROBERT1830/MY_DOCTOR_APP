package com.robertconstantindinescu.my_doctor_app.di

import android.content.Context
import androidx.room.Room
import com.robertconstantindinescu.my_doctor_app.models.data.database.UserCancerDatabase
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DATABSE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        UserCancerDatabase::class.java,
        DATABSE_NAME
    ).build()

    @Singleton
    @Provides
    fun providesDao(database: UserCancerDatabase) = database.cancerDataDao()
}