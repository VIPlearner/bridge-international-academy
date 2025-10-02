package com.bridge.androidtechnicaltest.data.repository

import com.bridge.androidtechnicaltest.data.db.dto.Pupil
import kotlinx.coroutines.flow.Flow

interface IPupilRepository {
    val pupils: Flow<List<Pupil>>
    suspend fun addPupil(pupil: Pupil)
    suspend fun updatePupil(pupil: Pupil)
    suspend fun deletePupil(pupilId: Int)
    fun startSync()
    fun stopSync()
}
