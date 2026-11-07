package com.example.data.model

data class UserProfile(
  val userName: String = "ओमप्रकाश",
  val phoneNumber: String = "",
  val avatarEmoji: String = "⚡",
  val dailyTargetTasks: Int = 5,
  val customMotto: String = "हर दिन एक नया अवसर!",
  val isPhoneVerified: Boolean = false
)
