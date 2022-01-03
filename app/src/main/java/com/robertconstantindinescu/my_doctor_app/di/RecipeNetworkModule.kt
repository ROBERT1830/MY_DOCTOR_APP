package com.robertconstantindinescu.my_doctor_app.di

import android.app.Application
import dagger.Module
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.FoodRecipeApi
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.GoogleMapApi
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecipeNetworkModule {
    @Singleton
    @Provides
    @Named("provideHttpClient_recipe")
    fun provideHttpClient(application: Application): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS) //tiempo de espera de lectura predeterminado para nuevas conexiones
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    @Singleton
    @Provides
    @Named("provideGsonConverterFactory_recipe")
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
        //so with this function we satify the first dependecy for the retriofit
    }

    @Singleton
    @Provides
    @Named("provideRetrofit_recipe")
    fun provideRetrofitInstance(
        @Named("provideHttpClient_recipe") okHttpClient: OkHttpClient,
        @Named("provideGsonConverterFactory_recipe") gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.RECIPE_BASE_URL)
            .client(okHttpClient) //the client is http used for request so we are going to use http for requesting
            .addConverterFactory(gsonConverterFactory)
            .build()

    }

    @Singleton
    @Provides
    fun provideApiService(@Named("provideRetrofit_recipe") retrofit: Retrofit): FoodRecipeApi {
        return retrofit.create(FoodRecipeApi::class.java)
    }

}