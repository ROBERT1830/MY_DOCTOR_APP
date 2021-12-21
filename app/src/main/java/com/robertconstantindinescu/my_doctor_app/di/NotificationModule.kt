package com.robertconstantindinescu.my_doctor_app.di

import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.NotificationApi
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {


    @Singleton
    @Provides
    @Named("provideHttClient_notification")
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS) //tiempo de espera de lectura predeterminado para nuevas conexiones
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @Named("provideGsonConverterFactory_notification")
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
        //so with this function we satify the first dependecy for the retriofit
    }

    @Singleton
    @Provides
    @Named("provideRetrofit_notification")
    fun provideRetrofitInstance(
        @Named("provideHttClient_notification") okHttpClient: OkHttpClient,
        @Named("provideGsonConverterFactory_notification") gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.NOTIFICATION_BASE_URL)
            .client(okHttpClient) //the client is http used for request so we are going to use http for requesting
            .addConverterFactory(gsonConverterFactory)
            .build()

    }

    @Singleton
    @Provides
    fun provideApiService(@Named("provideRetrofit_notification")retrofit: Retrofit): NotificationApi{
        return retrofit.create(NotificationApi::class.java)
    }
}