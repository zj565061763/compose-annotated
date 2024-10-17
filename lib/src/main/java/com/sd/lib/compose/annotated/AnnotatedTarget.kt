package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
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
   initialValue: AnnotatedString = EmptyAnnotatedString,
   ignoreCase: Boolean = true,
   targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
   return fAnnotatedTargets(
      targets = targets.toTypedArray(),
      initialValue = initialValue,
      ignoreCase = ignoreCase,
      targetStyle = targetStyle,
   )
}

@Composable
fun CharSequence.fAnnotatedTargets(
   vararg targets: String,
   initialValue: AnnotatedString = EmptyAnnotatedString,
   ignoreCase: Boolean = true,
   targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
   val content = this
   return produceState(initialValue = initialValue, content, targets, ignoreCase, targetStyle) {
      value = withContext(Dispatchers.Default) {
         content.fAnnotatedTargets(
            targets = targets,
            ignoreCase = ignoreCase,
            targetBlock = {
               withStyle(targetStyle) {
                  append(it)
               }
            },
         )
      }
   }.value
}

/**
 * 根据[targets]拆分构建[AnnotatedString]，[targets]部分调用[targetBlock]，非[targets]部分调用[normalBlock]
 */
inline fun CharSequence.fAnnotatedTargets(
   vararg targets: String,
   ignoreCase: Boolean = true,
   normalBlock: AnnotatedString.Builder.(String) -> Unit = { append(it) },
   targetBlock: AnnotatedString.Builder.(String) -> Unit,
): AnnotatedString {
   val content = this
   if (targets.isEmpty()) {
      return buildAnnotatedString {
         normalBlock(content.toString())
      }
   }

   val list = fSplit(
      delimiters = targets,
      ignoreCase = ignoreCase,
   )
   return buildAnnotatedString {
      if (list.isEmpty()) {
         append(content)
      } else {
         list.forEach { item ->
            if (item.isTarget) {
               targetBlock(item.content)
            } else {
               normalBlock(item.content)
            }
         }
      }
   }
}

private val EmptyAnnotatedString = AnnotatedString("")