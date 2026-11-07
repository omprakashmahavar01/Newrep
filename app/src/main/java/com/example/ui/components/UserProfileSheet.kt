package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserProfileSheet(
  currentProfile: UserProfile,
  onDismiss: () -> Unit,
  onSaveProfile: (UserProfile) -> Unit,
  onExportTasks: () -> Unit = {},
  onOpenPhoneLogin: () -> Unit = {}
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var name by remember { mutableStateOf(currentProfile.userName) }
  var selectedAvatar by remember { mutableStateOf(currentProfile.avatarEmoji) }
  var dailyTarget by remember { mutableIntStateOf(currentProfile.dailyTargetTasks) }
  var motto by remember { mutableStateOf(currentProfile.customMotto) }

  val avatars = listOf("⚡", "🎯", "🌟", "🧘", "💼", "🚀", "🌿", "🏆")
  val targets = listOf(3, 5, 7, 10)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .imePadding()
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(text = selectedAvatar, fontSize = 20.sp)
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "मेरी प्रोफाइल व सेटिंग्स",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "कस्टमाइज़ेशन और डाउनलोड विकल्प",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Name Input
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("आपका नाम • Your Name") },
        placeholder = { Text("उदा. ओमप्रकाश, राहुल") },
        leadingIcon = {
          Icon(Icons.Default.Person, contentDescription = null)
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_name_input"),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Mobile Number Account Status Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = if (currentProfile.phoneNumber.isNotBlank()) "+91 ${currentProfile.phoneNumber}" else "मोबाइल नंबर लिंक नहीं है",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
              )
              Text(
                text = if (currentProfile.phoneNumber.isNotBlank()) "सत्यापित खाता (Google Sign-in मुक्त)" else "बिना Google ID के सिर्फ नंबर जोड़ें",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
          Button(
            onClick = onOpenPhoneLogin,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          ) {
            Text(if (currentProfile.phoneNumber.isNotBlank()) "बदलें" else "जोड़ें", fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Avatar Selection
      Text(
        text = "अवतार या पहचान चुनें • Choose Avatar:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(10.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        avatars.forEach { avatar ->
          val isSelected = selectedAvatar == avatar
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
              )
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = CircleShape
              )
              .clickable { selectedAvatar = avatar }
              .testTag("avatar_$avatar"),
            contentAlignment = Alignment.Center
          ) {
            Text(text = avatar, fontSize = 22.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Daily Goal Target
      Text(
        text = "दैनिक टास्क लक्ष्य • Daily Target Goal:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        targets.forEach { target ->
          val isSelected = dailyTarget == target
          FilterChip(
            selected = isSelected,
            onClick = { dailyTarget = target },
            label = { Text("$target कार्य / दिन") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Motivational Motto Input
      OutlinedTextField(
        value = motto,
        onValueChange = { motto = it },
        label = { Text("प्रेरणादायक संदेश • Daily Motto") },
        placeholder = { Text("उदा. हर दिन प्रगति का नया दिन!") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_motto_input"),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(20.dp))

      // Download / Export Section
      Text(
        text = "डाउनलोड व बैकअप • Download & Backup",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(10.dp))

      // Task Data Download / Share Button
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FileDownload,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "टास्क डेटा डाउनलोड व शेयर करें",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyLarge
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "अपने सभी कार्यों, आदतों और प्रगति की पूरी सूची फ़ाइल या WhatsApp / Drive पर सेव करें।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          FilledTonalButton(
            onClick = onExportTasks,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("export_tasks_button"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("डेटा डाउनलोड व शेयर करें (Export Data)", fontWeight = FontWeight.SemiBold)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // APK Download info card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.PhoneAndroid,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "फ़ोन में APK कैसे इंस्टॉल करें?",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "AI Studio के ऊपर मेनू / सेटिंग्स में जाएँ और 'Export APK' पर क्लिक करें। APK फ़ाइल डाउनलोड होकर सीधे आपके फोन में इंस्टॉल हो जाएगी।",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
              lineHeight = 18.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Save and Cancel Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("रद्द करें • Cancel")
        }

        Button(
          onClick = {
            onSaveProfile(
              UserProfile(
                userName = name.ifBlank { "साथी" },
                avatarEmoji = selectedAvatar,
                dailyTargetTasks = dailyTarget,
                customMotto = motto.ifBlank { "हर दिन एक नया अवसर!" }
              )
            )
          },
          modifier = Modifier
            .weight(1.4f)
            .testTag("save_profile_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Text(text = "सेव करें • Save", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
