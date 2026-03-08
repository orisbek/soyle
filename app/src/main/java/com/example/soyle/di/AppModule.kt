package com.example.soyle.di

import android.content.Context
import androidx.room.Room
import com.example.soyle.data.api.SoyleApi
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.dao.ExerciseDao
import com.example.soyle.data.local.db.SoyleDatabase
import com.example.soyle.data.repository.SpeechRepositoryImpl
import com.example.soyle.domain.repository.SpeechRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
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
import javax.inject.Singleton

// ── Network ───────────────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)    // AI анализ может занять до 30с
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY  // TODO: NONE в release
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient : OkHttpClient,
        moshi        : Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/")    // эмулятор → localhost сервера
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideSoyleApi(retrofit: Retrofit): SoyleApi =
        retrofit.create(SoyleApi::class.java)
}

// ── Database ──────────────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SoyleDatabase = Room.databaseBuilder(
        context,
        SoyleDatabase::class.java,
        "soyle.db"
    ).build()

    @Provides
    fun provideAttemptDao(db: SoyleDatabase): AttemptDao =
        db.attemptDao()

    @Provides
    fun provideExerciseDao(db: SoyleDatabase): ExerciseDao =
        db.exerciseDao()
}

// ── Repository ────────────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSpeechRepository(
        impl: SpeechRepositoryImpl
    ): SpeechRepository
}