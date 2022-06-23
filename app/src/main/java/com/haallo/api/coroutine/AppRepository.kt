package com.haallo.api.coroutine

class AppRepository(val  APIHelper: APIHelper) {

    suspend fun removeAccount(accessToken:String,mobile: String) = APIHelper.removeAccount(accessToken,mobile)
}