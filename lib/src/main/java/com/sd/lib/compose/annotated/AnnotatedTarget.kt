package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
      onTarget = { target ->
         withStyle(targetStyle) {
            append(target)
         }
      },
   )
}

@Composable
fun CharSequence.fAnnotatedTargets(
   targets: List<String>,
   ignoreCase: Boolean = false,
   onTarget: AnnotatedString.Builder.(String) -> Unit,
): AnnotatedString {
   val content = this
   val initialValue = remember(content) { AnnotatedString(content.toString()) }
   if (targets.isEmpty()) return initialValue

   val onTargetUpdated by rememberUpdatedState(onTarget)
   return produceState(initialValue = initialValue, content, targets, ignoreCase) {
      value = withContext(Dispatchers.Default) {
         val list = content.fSplit(
            delimiters = targets,
            ignoreCase = ignoreCase,
         )
         buildAnnotatedString {
            list.forEach { item ->
               if (item.isTarget) {
                  onTargetUpdated(item.content)
               } else {
                  append(item.content)
               }
            }
         }
      }
   }.value
}