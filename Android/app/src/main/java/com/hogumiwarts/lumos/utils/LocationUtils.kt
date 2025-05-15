package com.hogumiwarts.lumos.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * ACCESS_FINE_LOCATION 권한이 허용됐는지 확인합니다.
 * (정확한 위도/경도가 필요하므로 Fine 권한을 우선 요구합니다.)
 */
fun hasFineLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

/**
 * FusedLocationProviderClient 의 lastLocation 을 시도하고,
 * null 이면 최신 위치를 한 번만 업데이트 받아 반환합니다.
 */
@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Location? {
    if (!hasFineLocationPermission(context)) {
        Log.w("LocationUtils", "ACCESS_FINE_LOCATION 권한이 없습니다.")
        return null
    }

    val client = LocationServices.getFusedLocationProviderClient(context)
    return suspendCancellableCoroutine { cont ->
        try {
            client.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        cont.resume(loc)
                    } else {
                        // lastLocation 이 null 이면 한 번만 업데이트 요청
                        val request = LocationRequest.Builder(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            /* intervalMillis = */ 0L
                        )
                            .setMinUpdateIntervalMillis(0L)
                            .setMaxUpdates(1)
                            .build()

                        val callback = object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                cont.resume(result.lastLocation)
                                client.removeLocationUpdates(this)
                            }
                            override fun onLocationAvailability(availability: LocationAvailability) {
                                if (!availability.isLocationAvailable) {
                                    Timber.tag("LocationUtils").w("위치 서비스 사용 불가")
                                    cont.resume(null)
                                    client.removeLocationUpdates(this)
                                }
                            }
                        }

                        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
                        cont.invokeOnCancellation { client.removeLocationUpdates(callback) }
                    }
                }
                .addOnFailureListener { e ->
                    Timber.tag("LocationUtils").e(e, "getLastLocation 실패")
                    cont.resumeWithException(e)
                }
        } catch (e: SecurityException) {
            Timber.tag("LocationUtils").e(e, "보안 예외: 권한이 필요합니다.")
            cont.resumeWithException(e)
        }
    }
}
