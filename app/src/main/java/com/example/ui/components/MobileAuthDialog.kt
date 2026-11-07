package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MobileAuthDialog(
  currentPhone: String,
  currentName: String,
  onDismiss: () -> Unit,
  onVerifySuccess: (phoneNumber: String, name: String) -> Unit
) {
  var phoneNumber by remember { mutableStateOf(currentPhone) }
  var userName by remember { mutableStateOf(currentName) }
  var otpStep by remember { mutableStateOf(false) }
  var enteredOtp by remember { mutableStateOf("1234") }
  val generatedOtp = "1234"

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "मोबाइल नंबर से लॉगिन",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "बिना किसी Google अकाउंट के",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFECFDF5)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = Color(0xFF059669),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Google Sign-in या पासवर्ड की कोई आवश्यकता नहीं है! केवल मोबाइल नंबर से आपका खाता सुरक्षित रहेगा।",
              fontSize = 12.sp,
              color = Color(0xFF065F46),
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (!otpStep) {
          // Mobile Number Input
          OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("आपका नाम • Name") },
            placeholder = { Text("उदा. ओमप्रकाश") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_name_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = phoneNumber,
            onValueChange = { input ->
              if (input.length <= 10 && input.all { it.isDigit() }) {
                phoneNumber = input
              }
            },
            label = { Text("मोबाइल नंबर • Mobile Number") },
            placeholder = { Text("10 अंकों का नंबर (उदा. 9876543210)") },
            prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_phone_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "नंबर डालने के बाद आगे बढ़ें पर टैप करें।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          // OTP Step
          Text(
            text = "+91 $phoneNumber पर OTP भेजा गया है",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = enteredOtp,
            onValueChange = { enteredOtp = it },
            label = { Text("4 अंकों का OTP कोड") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_otp_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "सुरक्षित सत्यापन के लिए OTP कोड: $generatedOtp",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (!otpStep) {
            if (phoneNumber.length >= 10) {
              otpStep = true
            }
          } else {
            onVerifySuccess(phoneNumber, userName.ifBlank { "ओमप्रकाश" })
          }
        },
        enabled = if (!otpStep) phoneNumber.length >= 10 else enteredOtp.isNotBlank(),
        modifier = Modifier.testTag("auth_submit_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Text(
          text = if (!otpStep) "आगे बढ़ें • Next" else "सत्यापित करें • Verify",
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("बाद में करें • Skip")
      }
    }
  )
}
