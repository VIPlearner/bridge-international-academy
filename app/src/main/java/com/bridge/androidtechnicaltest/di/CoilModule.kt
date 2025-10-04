package com.bridge.androidtechnicaltest.di

import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.util.DebugLogger
import coil3.util.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton

/**
 * Custom Coil Logger that integrates with Timber
 */
class TimberCoilLogger : Logger {
    override var minLevel: Logger.Level = Logger.Level.Debug

    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?
    ) {
        when (level) {
            Logger.Level.Verbose -> {
                if (throwable != null) {
                    Timber.tag(tag).v(throwable, message)
                } else {
                    Timber.tag(tag).v(message)
                }
            }
            Logger.Level.Debug -> {
                if (throwable != null) {
                    Timber.tag(tag).d(throwable, message)
                } else {
                    Timber.tag(tag).d(message)
                }
            }
            Logger.Level.Info -> {
                if (throwable != null) {
                    Timber.tag(tag).i(throwable, message)
                } else {
                    Timber.tag(tag).i(message)
                }
            }
            Logger.Level.Warn -> {
                if (throwable != null) {
                    Timber.tag(tag).w(throwable, message)
                } else {
                    Timber.tag(tag).w(message)
                }
            }
            Logger.Level.Error -> {
                if (throwable != null) {
                    Timber.tag(tag).e(throwable, message)
                } else {
                    Timber.tag(tag).e(message)
                }
            }
        }
    }
}
