package com.haallo.api.coroutine

class APIHelper(private val gateWayService: APIService) {
    suspend fun removeAccount(accessToken: String, mobile: String) = gateWayService.removeAccount(accessToken, mobile)
}