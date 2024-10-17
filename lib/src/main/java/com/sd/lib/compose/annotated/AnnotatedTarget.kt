package com.sd.lib.compose.annotated

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

/**
 * 根据[targets]拆分构建[AnnotatedString]，[targets]部分调用[targetBlock]，非[targets]部分调用[normalBlock]
 */
fun CharSequence.fAnnotatedTarget(
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