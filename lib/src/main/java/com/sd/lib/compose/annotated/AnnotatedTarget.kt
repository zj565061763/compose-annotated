package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun CharSequence.fAnnotatedTargets(
   targets: List<String>,
   targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
   return fAnnotatedTargets(
      targets = targets.toTypedArray(),
      targetStyle = targetStyle,
   )
}

@Composable
fun CharSequence.fAnnotatedTargets(
   vararg targets: String,
   targetStyle: SpanStyle = SpanStyle(Color.Red),
): AnnotatedString {
   val content = this
   return remember(content, targets) {
      content.fAnnotatedTargets(
         targets = targets,
         ignoreCase = true,
         targetBlock = {
            withStyle(targetStyle) {
               append(it)
            }
         },
      )
   }
}

/**
 * 根据[targets]拆分构建[AnnotatedString]，[targets]部分调用[targetBlock]，非[targets]部分调用[normalBlock]
 */
fun CharSequence.fAnnotatedTargets(
   vararg targets: String,
   ignoreCase: Boolean = false,
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