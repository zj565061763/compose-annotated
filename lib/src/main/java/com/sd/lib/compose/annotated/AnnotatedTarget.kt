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
   val content = this@fAnnotatedTarget

   val list = mutableListOf<SplitItem>()
   var preItem: IntRangeWithDelimiter? = null

   content.rangesDelimitedBy(
      delimiters = targets,
      ignoreCase = ignoreCase,
   ).map { item ->
      preItem?.let {
         if (it.delimiter.isNotEmpty()) {
            list.add(SplitItem(it.delimiter, isTarget = true))
         }
      }

      val substring = content.substring(item.intRange)
      if (substring.isNotEmpty()) {
         list.add(SplitItem(substring))
      }

      preItem = item
   }.toList()

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

private data class SplitItem(
   val content: String,
   val isTarget: Boolean = false,
)

private fun CharSequence.rangesDelimitedBy(
   delimiters: Array<out String>,
   startIndex: Int = 0,
   ignoreCase: Boolean = false,
   limit: Int = 0,
): Sequence<IntRangeWithDelimiter> {
   require(limit >= 0) { "Limit must be non-negative, but was $limit" }
   val delimitersList = delimiters.asList()

   return DelimitedRangesSequence(this, startIndex, limit) { currentIndex ->
      val find = findAnyOf(delimitersList, currentIndex, ignoreCase = ignoreCase, last = false)
      find?.let {
         MatchResult(
            index = it.first,
            length = it.second.length,
            delimiter = it.second,
         )
      }
   }
}

private data class IntRangeWithDelimiter(
   val intRange: IntRange,
   val delimiter: String,
)

private data class MatchResult(
   val index: Int,
   val length: Int,
   val delimiter: String,
)

private class DelimitedRangesSequence(
   private val input: CharSequence,
   private val startIndex: Int,
   private val limit: Int,
   private val getNextMatch: CharSequence.(currentIndex: Int) -> MatchResult?,
) : Sequence<IntRangeWithDelimiter> {

   override fun iterator(): Iterator<IntRangeWithDelimiter> = object : Iterator<IntRangeWithDelimiter> {
      var nextState: Int = -1 // -1 for unknown, 0 for done, 1 for continue
      var currentStartIndex: Int = startIndex.coerceIn(0, input.length)
      var nextSearchIndex: Int = currentStartIndex
      var nextItem: IntRangeWithDelimiter? = null
      var counter: Int = 0

      private fun calcNext() {
         if (nextSearchIndex < 0) {
            nextState = 0
            nextItem = null
         } else {
            if (limit > 0 && ++counter >= limit || nextSearchIndex > input.length) {
               nextItem = IntRangeWithDelimiter(currentStartIndex..input.lastIndex, "")
               nextSearchIndex = -1
            } else {
               val match = input.getNextMatch(nextSearchIndex)
               if (match == null) {
                  nextItem = IntRangeWithDelimiter(currentStartIndex..input.lastIndex, "")
                  nextSearchIndex = -1
               } else {
                  val (index, length, delimiter) = match
                  nextItem = IntRangeWithDelimiter(currentStartIndex until index, delimiter)
                  currentStartIndex = index + length
                  nextSearchIndex = currentStartIndex + if (length == 0) 1 else 0
               }
            }
            nextState = 1
         }
      }

      override fun next(): IntRangeWithDelimiter {
         if (nextState == -1)
            calcNext()
         if (nextState == 0)
            throw NoSuchElementException()
         val result = nextItem as IntRangeWithDelimiter
         // Clean next to avoid keeping reference on yielded instance
         nextItem = null
         nextState = -1
         return result
      }

      override fun hasNext(): Boolean {
         if (nextState == -1)
            calcNext()
         return nextState == 1
      }
   }
}

private fun CharSequence.findAnyOf(strings: Collection<String>, startIndex: Int, ignoreCase: Boolean, last: Boolean): Pair<Int, String>? {
   if (!ignoreCase && strings.size == 1) {
      val string = strings.single()
      val index = if (!last) indexOf(string, startIndex) else lastIndexOf(string, startIndex)
      return if (index < 0) null else index to string
   }

   val indices = if (!last) startIndex.coerceAtLeast(0)..length else startIndex.coerceAtMost(lastIndex) downTo 0

   if (this is String) {
      for (index in indices) {
         val matchingString = strings.firstOrNull { it.regionMatches(0, this, index, it.length, ignoreCase) }
         if (matchingString != null)
            return index to matchingString
      }
   } else {
      for (index in indices) {
         val matchingString = strings.firstOrNull { it.regionMatchesImpl(0, this, index, it.length, ignoreCase) }
         if (matchingString != null)
            return index to matchingString
      }
   }

   return null
}

private fun CharSequence.regionMatchesImpl(thisOffset: Int, other: CharSequence, otherOffset: Int, length: Int, ignoreCase: Boolean): Boolean {
   if ((otherOffset < 0) || (thisOffset < 0) || (thisOffset > this.length - length) || (otherOffset > other.length - length)) {
      return false
   }

   for (index in 0 until length) {
      if (!this[thisOffset + index].equals(other[otherOffset + index], ignoreCase))
         return false
   }
   return true
}