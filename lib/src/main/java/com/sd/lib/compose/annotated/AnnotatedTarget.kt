package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CharSequence.fAnnotatedTargets(
  targets: List<String>,
  ignoreCase: Boolean = false,
  targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
  return fAnnotatedTargets(
    targets = targets,
    ignoreCase = ignoreCase,
    onTarget = { result ->
      addStyle(targetStyle, result.range.first, result.range.last + 1)
    },
  )
}

@Composable
fun CharSequence.fAnnotatedTargets(
  targets: List<String>,
  ignoreCase: Boolean = false,
  onTarget: AnnotatedString.Builder.(MatchResult) -> Unit,
): AnnotatedString {
  val input = this
  val initialValue = remember(input) { AnnotatedString(input.toString()) }

  if (input.isEmpty()) return initialValue
  if (targets.isEmpty()) return initialValue

  val regex = remember(targets, ignoreCase) {
    if (ignoreCase) {
      Regex(targets.joinToString(separator = "|"), RegexOption.IGNORE_CASE)
    } else {
      Regex(targets.joinToString(separator = "|"))
    }
  }

  if (LocalInspectionMode.current) {
    return input.parseToAnnotatedString(
      regex = regex,
      onTarget = onTarget,
    )
  }

  val onTargetUpdated by rememberUpdatedState(onTarget)
  return produceState(initialValue = initialValue, input, regex) {
    value = withContext(Dispatchers.Default) {
      input.parseToAnnotatedString(
        regex = regex,
        onTarget = { onTargetUpdated(it) },
      )
    }
  }.value
}

private fun CharSequence.parseToAnnotatedString(
  regex: Regex,
  onTarget: AnnotatedString.Builder.(MatchResult) -> Unit,
): AnnotatedString {
  val input = this
  return buildAnnotatedString {
    append(input)
    regex.findAll(input).forEach { item ->
      onTarget(item)
    }
  }
}