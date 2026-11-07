package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("rozana_user_prefs", Context.MODE_PRIVATE)

  private val _userProfile = MutableStateFlow(loadProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private fun loadProfile(): UserProfile {
    return UserProfile(
      userName = prefs.getString("user_name", "ओमप्रकाश") ?: "ओमप्रकाश",
      phoneNumber = prefs.getString("phone_number", "") ?: "",
      avatarEmoji = prefs.getString("avatar_emoji", "⚡") ?: "⚡",
      dailyTargetTasks = prefs.getInt("daily_target_tasks", 5),
      customMotto = prefs.getString("custom_motto", "हर दिन एक नया अवसर!") ?: "हर दिन एक नया अवसर!",
      isPhoneVerified = prefs.getBoolean("is_phone_verified", false)
    )
  }

  fun updateProfile(newProfile: UserProfile) {
    prefs.edit()
      .putString("user_name", newProfile.userName.trim())
      .putString("phone_number", newProfile.phoneNumber.trim())
      .putString("avatar_emoji", newProfile.avatarEmoji)
      .putInt("daily_target_tasks", newProfile.dailyTargetTasks)
      .putString("custom_motto", newProfile.customMotto.trim())
      .putBoolean("is_phone_verified", newProfile.isPhoneVerified)
      .apply()
    _userProfile.value = newProfile
  }

  fun verifyPhoneNumber(phoneNumber: String, userName: String = "") {
    val current = _userProfile.value
    val updated = current.copy(
      phoneNumber = phoneNumber.trim(),
      userName = if (userName.isNotBlank()) userName.trim() else current.userName,
      isPhoneVerified = true
    )
    updateProfile(updated)
  }
}
