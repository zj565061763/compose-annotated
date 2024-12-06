package com.sd.demo.compose.annotated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sd.demo.compose.annotated.theme.AppTheme
import com.sd.lib.compose.annotated.fAnnotatedWithRegex
import com.sd.lib.compose.annotated.fAnnotatedWithTarget

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
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    AnnotatedWithTarget()
    AnnotatedWithRegex()
  }
}

@Composable
private fun AnnotatedWithTarget(
  modifier: Modifier = Modifier,
  content: String = "1122334455-1122334455",
) {
  val target = remember { listOf("2", "4") }
  val annotated = content.fAnnotatedWithTarget(target)
  Text(
    modifier = modifier,
    text = annotated,
  )
}

@Composable
private fun AnnotatedWithRegex(
  modifier: Modifier = Modifier,
  content: String = "1122334455-1122334455",
) {
  val regex = remember { "3".toRegex() }
  val annotated = content.fAnnotatedWithRegex(regex)
  Text(
    modifier = modifier,
    text = annotated,
  )
}

@Preview
@Composable
private fun Preview() {
  Content()
}