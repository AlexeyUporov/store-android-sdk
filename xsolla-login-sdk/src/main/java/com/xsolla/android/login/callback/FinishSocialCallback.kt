package com.xsolla.android.login.callback

interface FinishSocialCallback {
    fun onAuthSuccess()
    fun onAuthCancelled()
    fun onAuthError(throwable: Throwable?, errorMessage: String?)
}