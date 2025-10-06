package com.bridge.androidtechnicaltest.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bridge.androidtechnicaltest.config.ApiConstants
import com.bridge.androidtechnicaltest.data.datastore.DataStoreConstants
import com.bridge.androidtechnicaltest.data.datastore.DataStoreRepository
import com.bridge.androidtechnicaltest.data.db.AppDatabase
import com.bridge.androidtechnicaltest.data.network.GeocodingApi
import com.bridge.androidtechnicaltest.data.network.PupilApi
import com.bridge.androidtechnicaltest.data.repository.IPupilRepository
import com.bridge.androidtechnicaltest.data.repository.PupilRepository
import com.bridge.androidtechnicaltest.data.sync.PupilSyncManager
import com.bridge.androidtechnicaltest.utils.NetworkLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

const val API_TIMEOUT: Long = 30
private const val BASE_URL = "https://androidtechnicaltestapi-test.bridgeinternationalacademies.com/"
private const val OPENWEATHER_BASE_URL = "https://api.openweathermap.org/"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenWeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PupilRetrofit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DataStoreConstants.PREFERENCES_DATASTORE_NAME,
)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)

        val userAgent = "Bridge Android Tech Test"
        val requestInterceptor =
            Interceptor { chain ->
                val originalRequest = chain.request()
                val newRequest =
                    originalRequest
                        .newBuilder()
                        .addHeader("X-Request-ID", ApiConstants.PUPIL_API_REQUEST_ID)
                        .addHeader("User-Agent", userAgent)
                        .build()
                chain.proceed(newRequest)
            }

        builder.addInterceptor(requestInterceptor)
        val loggingInterceptor =
            HttpLoggingInterceptor(NetworkLogger())
                .apply { level = HttpLoggingInterceptor.Level.BODY }
        builder.addInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    @PupilRetrofit
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePupilApi(
        @PupilRetrofit retrofit: Retrofit,
    ): PupilApi = retrofit.create(PupilApi::class.java)

    @Provides
    @Singleton
    @OpenWeatherRetrofit
    fun provideOpenWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(OPENWEATHER_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGeocodingApi(
        @OpenWeatherRetrofit retrofit: Retrofit,
    ): GeocodingApi = retrofit.create(GeocodingApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "TechnicalTestDb")
            .fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun providesPupilDao(database: AppDatabase) = database.pupilDao

    @Provides
    @Singleton
    fun providesLocationCacheDao(database: AppDatabase) = database.locationCacheDao

    @Provides
    @Singleton
    fun providePupilRepository(
        database: AppDatabase,
        pupilApi: PupilApi,
        syncManager: PupilSyncManager,
    ): IPupilRepository = PupilRepository(database, pupilApi, syncManager)
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreRepository = DataStoreRepository(dataStore)
}
