package com.ralphmueller.nycschools.di

import android.content.Context
import androidx.room.Room
import com.ralphmueller.nycschools.data.SchoolRepo
import com.ralphmueller.nycschools.data.local.LocalDataBase
import com.ralphmueller.nycschools.data.local.NYCSchoolsLocalDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteDataSource
import com.ralphmueller.nycschools.data.remote.NYCSchoolsRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalDataSource

    @Singleton
    @RemoteDataSource
    @Provides
    fun provideRemoteDataSource(
        nycSchoolsRemoteService: NYCSchoolsRemoteService
    ): NYCSchoolsRemoteDataSource {
        return NYCSchoolsRemoteDataSource(
            nYCSchoolsRemoteService = nycSchoolsRemoteService,
        )
    }

    @Provides
    @Singleton
    fun provideNYCSchoolsRemoteService(
        retrofit: Retrofit
    ): NYCSchoolsRemoteService {
        return retrofit.create(NYCSchoolsRemoteService::class.java)
    }


    @Provides
    @Singleton
    fun provideOkhttpClient(@ApplicationContext context: Context): OkHttpClient {

        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val baseUrl = "https://data.cityofnewyork.us"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @LocalDataSource
    @Provides
    fun provideLocalDataSource(
        database: LocalDataBase,
    ): NYCSchoolsLocalDataSource {
        return NYCSchoolsLocalDataSource(database = database)
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): LocalDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocalDataBase::class.java,
            "database.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {

        @Singleton
        @Provides
        fun provideSchoolRepository(
            @RemoteDataSource remoteDataSource: NYCSchoolsRemoteDataSource,
            @LocalDataSource localDataSource: NYCSchoolsLocalDataSource,
        ): SchoolRepo {
            return SchoolRepo(
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource,
            )
        }
    }
}

