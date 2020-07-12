package com.xsolla.android.storesdkexample.fragments

import android.content.Intent
import android.os.Bundle
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.api.XLoginCallback
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.entity.response.AuthResponse
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.util.ViewUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseFragment() {

    private var selectedSocialNetwork: SocialNetwork? = null


    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun initUI() {
        rootView.loginButton.setOnClickListener { v ->
            ViewUtils.disable(v)

            hideKeyboard()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            XLogin.login(username, password, object : XLoginCallback<AuthResponse?>() {
                override fun onSuccess(response: AuthResponse?) {
                    // openFragment(MainFragment())
                    showSnack("Login success")
                    ViewUtils.enable(v)
                }

                override fun onFailure(errorMessage: String) {
                    showSnack(errorMessage)
                    ViewUtils.enable(v)
                }
            })
        }

        rootView.googleButton.setOnClickListener {
            selectedSocialNetwork = SocialNetwork.GOOGLE
            XLogin.startSocialAuth(this, SocialNetwork.GOOGLE, startSocialCallback)
        }

        rootView.facebookButton.setOnClickListener {
            selectedSocialNetwork = SocialNetwork.FACEBOOK
            XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, startSocialCallback)
        }

        rootView.twitterButton.setOnClickListener {
            selectedSocialNetwork = SocialNetwork.TWITTER
            XLogin.startSocialAuth(this, SocialNetwork.TWITTER, startSocialCallback)
        }

        rootView.linkedinButton.setOnClickListener {
            selectedSocialNetwork = SocialNetwork.LINKEDIN
            XLogin.startSocialAuth(this, SocialNetwork.LINKEDIN, startSocialCallback)
        }

        rootView.naverButton.setOnClickListener {
            selectedSocialNetwork = SocialNetwork.NAVER
            XLogin.startSocialAuth(this, SocialNetwork.NAVER, startSocialCallback)
        }

        rootView.resetPasswordButton.setOnClickListener { restPassword() }
    }



    private fun restPassword() {
        activity?.let {
            it.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.rootFragmentContainer, ResetPasswordFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        XLogin.finishSocialAuth(context, selectedSocialNetwork, requestCode, resultCode, data, finishSocialCallback)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val startSocialCallback: StartSocialCallback = object : StartSocialCallback {
        override fun onAuthStarted() {
            // auth successfully started
        }

        override fun onError(errorMessage: String) {
            showSnack(errorMessage)
        }
    }

    private val finishSocialCallback: FinishSocialCallback = object : FinishSocialCallback {
        override fun onAuthSuccess() {
            // openFragment(MainFragment())
            showSnack("Social Auth success")
        }

        override fun onAuthCancelled() {
            showSnack("Auth cancelled")
        }

        override fun onAuthError(errorMessage: String) {
            showSnack(errorMessage)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedSocialNetwork?.let {
            outState.putString("selectedSocialNetwork", it.name)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let { state ->
            state.getString("selectedSocialNetwork")?.let {
                selectedSocialNetwork = SocialNetwork.valueOf(it)
            }
        }
    }

}