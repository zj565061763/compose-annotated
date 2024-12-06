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
  async: Boolean = false,
  onTarget: AnnotatedString.Builder.(MatchResult) -> Unit,
): AnnotatedString {
  val input = this
  if (input.isEmpty() || targets.isEmpty()) {
    return remember(input) { AnnotatedString(input.toString()) }
  }

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
  return if (async) {
    val initialValue = remember(input) { AnnotatedString(input.toString()) }
    produceState(initialValue = initialValue, input, regex) {
      value = withContext(Dispatchers.Default) {
        input.parseToAnnotatedString(
          regex = regex,
          onTarget = { onTargetUpdated(it) },
        )
      }
    }.value
  } else {
    remember(input, regex) {
      input.parseToAnnotatedString(
        regex = regex,
        onTarget = { onTargetUpdated(it) },
      )
    }
  }
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