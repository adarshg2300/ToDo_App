package com.trainee.appinventiv.notesapp.di

import com.trainee.appinventiv.notesapp.api.AuthInterceptor
import com.trainee.appinventiv.notesapp.api.NotesApi
import com.trainee.appinventiv.notesapp.api.UserApi
import com.trainee.appinventiv.notesapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofitBuilder() :Retrofit.Builder{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor):OkHttpClient{
        return OkHttpClient.Builder()
            .writeTimeout(5,TimeUnit.MINUTES)
            .connectTimeout(5,TimeUnit.MINUTES)
            .addInterceptor(authInterceptor).build()
    }

    @Singleton
    @Provides
    fun provideUserApi(retrofitBuilder: Retrofit.Builder):UserApi{
        return retrofitBuilder.build().create(UserApi::class.java)
    }

    @Singleton
    @Provides
    fun providesNoteApi(retrofitBuilder: Retrofit.Builder,okHttpClient: OkHttpClient):NotesApi{
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(NotesApi::class.java)
    }

}