package com.sd.demo.compose.annotated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sd.demo.compose.annotated.theme.AppTheme
import com.sd.lib.compose.annotated.fAnnotatedTargets

class SampleAnnotatedTargets : ComponentActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            Content()
         }
      }
   }
}

@Composable
private fun Content(
   modifier: Modifier = Modifier,
   content: String = "1122334455-1122334455",
) {
   val annotated = content.fAnnotatedTargets("2", "4")

   Column(
      modifier = modifier
         .fillMaxSize()
         .padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
   ) {
      Text(text = annotated)
   }
}