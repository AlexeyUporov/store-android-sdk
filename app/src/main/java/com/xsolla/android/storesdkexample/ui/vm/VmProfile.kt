package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.callback.ResetPasswordCallback
import com.xsolla.android.login.callback.UpdateCurrentUserDetailsCallback
import com.xsolla.android.login.callback.UpdateCurrentUserPhoneCallback
import com.xsolla.android.login.entity.response.GenderResponse
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import java.util.Locale
import java.util.regex.Pattern

class VmProfile : ViewModel() {
    private companion object {
        private val PHONE_PATTERN = Pattern.compile("^\\+(\\d){5,25}\$")
    }

    private val _state = MutableLiveData<UserDetailsUi>()
    val state: LiveData<UserDetailsUi> = _state

    val stateForChanging = MutableLiveData<UserDetailsUi>()

    val message = SingleLiveEvent<String>()

    val fieldChangeResult = SingleLiveEvent<FieldChangeResult>()

    init {
        load()
    }

    fun load() {
        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                val uiEntity = UserDetailsUi(
                    id = data.id,
                    email = data.email,
                    username = data.username,
                    nickname = data.nickname,
                    firstName = data.firstName,
                    lastName = data.lastName,
                    birthday = data.birthday,
                    phone = data.phone,
                    gender = Gender.getBy(data.gender),
                    avatar = data.picture
                )
                _state.value = uiEntity
                stateForChanging.value = uiEntity
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                message.value = throwable?.message ?: errorMessage ?: "Failure"
            }
        })
    }

    fun updateFields(newState: UserDetailsUi) {
        val gender = newState.gender?.name?.toLowerCase(Locale.getDefault())?.first()?.toString()
        XLogin.updateCurrentUserDetails(newState.birthday, newState.firstName, gender, newState.lastName, newState.nickname, object : UpdateCurrentUserDetailsCallback {
            override fun onSuccess() {
                message.value = "Fields were successfuly changed"
                _state.value = newState.copy(phone = _state.value!!.phone)

                if (newState.phone != state.value!!.phone) {
                    updatePhone(newState.phone!!)
                }
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                message.value = throwable?.message ?: errorMessage ?: "Failure"
            }
        })
    }

    fun updatePhone(newPhone: String) {
        XLogin.updateCurrentUserPhone(newPhone, object : UpdateCurrentUserPhoneCallback {
            override fun onSuccess() {
                message.value = "Fields were successfuly changed"
                _state.value = state.value!!.copy(phone = newPhone)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                message.value = throwable?.message ?: errorMessage ?: "Failure"
            }
        })
    }

    fun validateField(field: FieldsForChanging, text: String?): ValidateFieldResult {
        if (text.isNullOrBlank()) return ValidateFieldResult(false, "field is blank")

        if (field in FieldsForChanging.textFields) {
            return if (text.length in 1..255) {
                ValidateFieldResult(true, null)
            } else {
                ValidateFieldResult(false, "${field.name} length must be in 1..255")
            }
        } else if (field == FieldsForChanging.PHONE) {
            return if (PHONE_PATTERN.matcher(text).matches()) {
                ValidateFieldResult(true, null)
            } else {
                ValidateFieldResult(false, "Phone must start with "+" and contains 5..25 digits")
            }
        }

        throw IllegalArgumentException()
    }

    fun updateAvatar(avatar: String?) {
        _state.value = _state.value?.copy(avatar = avatar)
    }

    fun resetPassword() {
        val username = state.value!!.username ?: return
        val email = state.value!!.email ?: return
        XLogin.resetPassword(username, object : ResetPasswordCallback {
            override fun onSuccess() {
                message.value = "A letter has been sent to the $email. Follow the link in the letter — and you can create a new password"
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                message.value = throwable?.message ?: errorMessage ?: "Failure"
            }
        })
    }
}

data class UserDetailsUi(
    val id: String = "",
    val email: String? = null,
    val username: String? = null,
    val nickname: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val birthday: String? = null,
    val phone: String? = null,
    val gender: Gender? = null,
    val avatar: String? = null
)

data class FieldChangeResult(val field: FieldsForChanging, val message: String)

enum class FieldsForChanging {
    NICKNAME,
    PHONE,
    FIRST_NAME,
    LAST_NAME,
    BIRTHDAY,
    GENDER;

    companion object {
        val textFields = arrayOf(NICKNAME, FIRST_NAME, LAST_NAME)
    }
}

enum class Gender(val response: GenderResponse) {
    Female(GenderResponse.F),
    Male(GenderResponse.M);

    companion object {
        fun getBy(response: GenderResponse?): Gender? {
            return when (response) {
                GenderResponse.F -> Female
                GenderResponse.M -> Male
                null -> null
            }
        }
    }
}

data class ValidateFieldResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)