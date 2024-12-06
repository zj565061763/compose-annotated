package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

@Composable
fun CharSequence.fAnnotatedWithTargets(
  targets: List<String>,
  ignoreCase: Boolean = false,
  targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
  return fAnnotatedWithTargets(
    targets = targets,
    ignoreCase = ignoreCase,
    onTarget = { result ->
      addStyle(targetStyle, result.range.first, result.range.last + 1)
    },
  )
}

@Composable
fun CharSequence.fAnnotatedWithTargets(
  targets: List<String>,
  ignoreCase: Boolean = false,
  onTarget: AnnotatedString.Builder.(MatchResult) -> Unit,
): AnnotatedString {
  val input = this
  val initialValue = remember(input) { AnnotatedString(input.toString()) }
  if (input.isEmpty()) return initialValue
  if (targets.isEmpty()) return initialValue

  val regex = remember(targets, ignoreCase) {
    targets.joinToString(separator = "|").let { reg ->
      if (ignoreCase) {
        reg.toRegex(RegexOption.IGNORE_CASE)
      } else {
        reg.toRegex()
      }
    }
  }

  return fAnnotatedWithRegex(
    regex = regex,
    onTarget = onTarget,
  )
}