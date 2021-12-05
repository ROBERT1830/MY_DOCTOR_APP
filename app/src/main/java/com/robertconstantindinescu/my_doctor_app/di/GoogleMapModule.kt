package com.robertconstantindinescu.my_doctor_app.di

import android.app.Application
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.GoogleMapApi
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.GOOGLE_MAP_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleMapModule {

    @Singleton
    @Provides
    @Named("provideHttpClient_googleApi")
    fun provideHttpClient(application: Application): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS) //tiempo de espera de lectura predeterminado para nuevas conexiones
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    @Singleton
    @Provides
    fun provideConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
        //so with this function we satify the first dependecy for the retriofit
    }

    @Singleton
    @Provides
    @Named("provideRetrofit_googleApi")
    fun provideRetrofitInstance(
        @Named("provideHttpClient_googleApi") okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_MAP_BASE_URL)
            .client(okHttpClient) //the client is http used for request so we are going to use http for requesting
            .addConverterFactory(moshiConverterFactory)
            .build()

    }

    // TODO: 5/12/21 GoogleMapAPI
    @Singleton
    @Provides
    fun provideApiService(@Named("provideRetrofit_googleApi") retrofit: Retrofit): GoogleMapApi{
        return retrofit.create(GoogleMapApi::class.java)
    }



}