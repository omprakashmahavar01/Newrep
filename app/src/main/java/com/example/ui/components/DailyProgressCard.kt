package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DailyProgressCard(
  totalCount: Int,
  completedCount: Int,
  progressPercent: Int,
  bestStreak: Int,
  onOpenTemplates: () -> Unit,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    label = "progressAnimation"
  )

  val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
  val greeting = when {
    currentHour < 12 -> "सुप्रभात • Good Morning ☀️"
    currentHour < 17 -> "शुभ दोपहर • Good Afternoon 🌤️"
    else -> "शुभ संध्या • Good Evening 🌙"
  }

  val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
  val todayString = dateFormat.format(Date())

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("daily_progress_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.linearGradient(
            colors = listOf(
              MaterialTheme.colorScheme.primaryContainer,
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            )
          )
        )
        .padding(20.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = greeting,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = todayString,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }

          if (bestStreak > 0) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEF3C7))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Streak",
                tint = Color(0xFFD97706),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$bestStreak दिन",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF92400E)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Progress Section
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "($completedCount/$totalCount कार्य पूर्ण)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
              progress = { animatedProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            val motivationalQuote = when {
              totalCount == 0 -> "आज की योजना शुरू करने के लिए टास्क जोड़ें!"
              progressPercent == 100 -> "शानदार! आज के सभी कार्य पूर्ण हो गए! 🎉"
              progressPercent >= 50 -> "बढ़िया गति! आधे से ज्यादा काम पूरा हो गया!"
              else -> "एक-एक कदम से हर लक्ष्य प्राप्त होता है।"
            }

            Text(
              text = motivationalQuote,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          FilledTonalButton(
            onClick = onOpenTemplates,
            modifier = Modifier.testTag("open_templates_button"),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "रुटीन",
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}
