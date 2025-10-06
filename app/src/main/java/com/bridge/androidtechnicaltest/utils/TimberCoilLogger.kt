package com.bridge.androidtechnicaltest.utils

import coil3.util.Logger
import timber.log.Timber

/**
 * Custom Coil Logger that integrates with Timber
 */
class TimberCoilLogger : Logger {
    override var minLevel: Logger.Level = Logger.Level.Debug

    override fun log(
        tag: String,
        level: Logger.Level,
        message: String?,
        throwable: Throwable?,
    ) {
        when (level) {
            Logger.Level.Verbose -> {
                if (throwable != null) {
                    Timber.Forest.tag(tag).v(throwable, message)
                } else {
                    Timber.Forest.tag(tag).v(message)
                }
            }
            Logger.Level.Debug -> {
                if (throwable != null) {
                    Timber.Forest.tag(tag).d(throwable, message)
                } else {
                    Timber.Forest.tag(tag).d(message)
                }
            }
            Logger.Level.Info -> {
                if (throwable != null) {
                    Timber.Forest.tag(tag).i(throwable, message)
                } else {
                    Timber.Forest.tag(tag).i(message)
                }
            }
            Logger.Level.Warn -> {
                if (throwable != null) {
                    Timber.Forest.tag(tag).w(throwable, message)
                } else {
                    Timber.Forest.tag(tag).w(message)
                }
            }
            Logger.Level.Error -> {
                if (throwable != null) {
                    Timber.Forest.tag(tag).e(throwable, message)
                } else {
                    Timber.Forest.tag(tag).e(message)
                }
            }
        }
    }
}
