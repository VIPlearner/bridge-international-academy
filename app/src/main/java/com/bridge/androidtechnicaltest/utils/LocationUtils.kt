package com.bridge.androidtechnicaltest.utils

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
)

fun boundingBox(
    lat: Double,
    lng: Double,
    radiusKm: Double,
): BoundingBox {
    val earthRadiusKm = 6371.0

    val radiusRad = radiusKm / earthRadiusKm

    val latRad = Math.toRadians(lat)
    val lngRad = Math.toRadians(lng)

    val minLatRad = latRad - radiusRad
    val maxLatRad = latRad + radiusRad

    val deltaLng = asin(sin(radiusRad) / cos(latRad))
    val minLngRad = lngRad - deltaLng
    val maxLngRad = lngRad + deltaLng

    return BoundingBox(
        minLat = Math.toDegrees(minLatRad),
        maxLat = Math.toDegrees(maxLatRad),
        minLng = Math.toDegrees(minLngRad),
        maxLng = Math.toDegrees(maxLngRad),
    )
}

fun distanceKm(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double,
): Double {
    val earthRadiusKm = 6371.0

    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)

    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)

    val a = sin(dLat / 2).pow(2) + sin(dLng / 2).pow(2) * cos(lat1Rad) * cos(lat2Rad)
    val c = 2 * asin(sqrt(a))

    return earthRadiusKm * c
}
