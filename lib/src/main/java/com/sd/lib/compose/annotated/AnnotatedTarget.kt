package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CharSequence.fAnnotatedTargets(
   vararg targets: String,
   ignoreCase: Boolean = false,
   targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
   return fAnnotatedTargets(
      targets = targets.toList(),
      ignoreCase = ignoreCase,
      targetStyle = targetStyle,
   )
}

@Composable
fun CharSequence.fAnnotatedTargets(
   targets: List<String>,
   ignoreCase: Boolean = false,
   targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
   val content = this
   val initialValue = remember(content) { AnnotatedString(content.toString()) }
   if (targets.isEmpty()) return initialValue

   val list by content.fSplitState(delimiters = targets, ignoreCase = ignoreCase)
   if (list.isEmpty()) return initialValue

   return produceState(initialValue = initialValue, list, targetStyle) {
      value = withContext(Dispatchers.Default) {
         buildAnnotatedString {
            list.forEach { item ->
               if (item.isTarget) {
                  withStyle(targetStyle) {
                     append(item.content)
                  }
               } else {
                  append(item.content)
               }
            }
         }
      }
   }.value
}