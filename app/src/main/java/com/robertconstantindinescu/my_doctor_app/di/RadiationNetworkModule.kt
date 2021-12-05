package com.robertconstantindinescu.my_doctor_app.di

import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.UvRadiationApi
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RadiationNetworkModule {

    @Singleton
    @Provides

    @Named("provideHttpClient_uvIndex")
    fun provideHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS) //tiempo de espera de lectura predeterminado para nuevas conexiones
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
        //so with this function we satify the first dependecy for the retriofit
    }

    @Singleton
    @Provides
    @Named("provideRetrofit_uvIndex")
    fun provideRetrofitInstance(
        @Named("provideHttpClient_uvIndex") okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) //the client is http used for request so we are going to use http for requesting
            .addConverterFactory(gsonConverterFactory)
            .build()

    }

    @Singleton
    @Provides
    fun provideapiService(@Named("provideRetrofit_uvIndex") retrofit: Retrofit): UvRadiationApi{
        return retrofit.create(UvRadiationApi::class.java)
    }






}