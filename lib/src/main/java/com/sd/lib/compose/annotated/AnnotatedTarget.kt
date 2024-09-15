package com.sd.lib.compose.annotated

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

/**
 * 根据[target]拆分构建[AnnotatedString]，[target]部分调用[targetBlock]，非[target]部分调用[normalBlock]
 */
fun CharSequence.fAnnotatedTarget(
   target: String,
   ignoreCase: Boolean = false,
   normalBlock: AnnotatedString.Builder.(String) -> Unit = { append(it) },
   targetBlock: AnnotatedString.Builder.(String) -> Unit,
): AnnotatedString {
   val content = this@fAnnotatedTarget
   val list = splitWithDelimiter(target, ignoreCase)
   return buildAnnotatedString {
      if (list.isEmpty()) {
         append(content)
      } else {
         list.forEach { item ->
            if (item == target) {
               targetBlock(target)
            } else {
               normalBlock(item)
            }
         }
      }
   }
}

private fun CharSequence.splitWithDelimiter(
   delimiter: String,
   ignoreCase: Boolean,
): List<String> {
   var currentOffset = 0
   var nextIndex = indexOf(delimiter, currentOffset, ignoreCase)
   if (nextIndex == -1) {
      return listOf(this.toString())
   }

   if (nextIndex == 0 && length == delimiter.length) {
      return listOf(this.toString())
   }

   val result = ArrayList<String>(10)
   do {
      val substring = substring(currentOffset, nextIndex)
      if (substring.isNotEmpty()) {
         result.add(substring)
      }
      result.add(delimiter)
      currentOffset = nextIndex + delimiter.length
      nextIndex = indexOf(delimiter, currentOffset, ignoreCase)
   } while (nextIndex != -1)

   val substring = substring(currentOffset, length)
   if (substring.isNotEmpty()) {
      result.add(substring)
   }
   return result
}