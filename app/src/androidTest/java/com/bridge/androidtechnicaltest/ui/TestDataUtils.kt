package com.bridge.androidtechnicaltest.ui

import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.entity.PupilWithLocationEntity
import com.bridge.androidtechnicaltest.ui.screens.listview.PupilItem

object TestDataUtils {
    fun createTestPupilEntity(
        id: Int = 1,
        name: String = "Test Pupil",
        country: String = "Test Country",
        image: String? = "https://example.com/image.jpg",
        latitude: Double = 40.7128,
        longitude: Double = -74.0060,
    ) = PupilEntity(
        id = id,
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude,
    )

    fun createTestPupilWithLocationEntity(
        id: Int = 1,
        name: String = "Test Pupil",
        country: String = "Test Country",
        image: String? = "https://example.com/image.jpg",
        latitude: Double = 40.7128,
        longitude: Double = -74.0060,
        prettyLocation: String? = "New York, NY",
    ) = PupilWithLocationEntity(
        id = id,
        name = name,
        country = country,
        image = image,
        latitude = latitude,
        longitude = longitude,
        prettyLocation = prettyLocation,
    )

    fun createTestPupilItem(
        id: Int = 1,
        name: String = "Test Pupil",
        prettyLocation: String = "New York, NY",
        imageUrl: String = "https://example.com/image.jpg",
    ) = PupilItem(
        id = id,
        name = name,
        prettyLocation = prettyLocation,
        imageUrl = imageUrl,
    )

    fun createTestPupilList(count: Int = 3): List<PupilItem> =
        (1..count).map { index ->
            createTestPupilItem(
                id = index,
                name = "Pupil $index",
                prettyLocation = "Location $index",
                imageUrl = "https://example.com/image$index.jpg",
            )
        }
}
