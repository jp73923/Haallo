package com.haallo.base.network

import com.haallo.base.extension.DeactivatedAccountException
import com.haallo.base.extension.onSafeError
import com.haallo.base.extension.onSafeSuccess
import com.haallo.base.network.model.HaalloCommonResponse
import com.haallo.base.network.model.HaalloResponse
import io.reactivex.Single

class HaalloResponseConverter {

    fun <T> convert(haalloResponse: HaalloResponse<T>?): Single<T> {
        return convertToSingle(haalloResponse)
    }

    fun <T> convertToSingle(haalloResponse: HaalloResponse<T>?): Single<T> {
        return Single.create { emitter ->
            when {
                haalloResponse == null -> emitter.onSafeError(Exception("Failed to process the info"))
                !haalloResponse.success -> {
                    emitter.onSafeError(Exception(haalloResponse.message))
                }
                haalloResponse.success -> {
                    emitter.onSafeSuccess(haalloResponse.data)
                }
                else -> emitter.onSafeError(Exception("Failed to process the info"))
            }
        }
    }

    fun <T> convertToSingleWithFullResponse(haalloResponse: HaalloResponse<T>?): Single<HaalloResponse<T>> {
        return Single.create { emitter ->
            when {
                haalloResponse == null -> emitter.onSafeError(Exception("Failed to process the info"))
                !haalloResponse.success -> {
                    if (haalloResponse.deactive == true) {
                        emitter.onSafeError(DeactivatedAccountException(haalloResponse.message))
                    } else {
                        emitter.onSafeError(Exception(haalloResponse.message))
                    }
                }
                haalloResponse.success -> {
                    emitter.onSafeSuccess(haalloResponse)
                }
                else -> emitter.onSafeError(Exception("Failed to process the info"))
            }
        }
    }

    fun convertCommonResponse(haalloCommonResponse: HaalloCommonResponse?): Single<HaalloCommonResponse> {
        return Single.create { emitter ->
            when {
                haalloCommonResponse == null -> emitter.onSafeError(Exception("Failed to process the info"))
                !haalloCommonResponse.success -> {
                    emitter.onSafeError(Exception(haalloCommonResponse.message))
                }
                haalloCommonResponse.success -> {
                    emitter.onSafeSuccess(haalloCommonResponse)
                }
                else -> emitter.onSafeError(Exception("Failed to process the info"))
            }
        }
    }
}