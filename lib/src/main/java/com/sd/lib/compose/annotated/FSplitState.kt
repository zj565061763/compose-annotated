package com.sd.lib.compose.annotated

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CharSequence.fSplitState(
   vararg delimiters: String,
   ignoreCase: Boolean = false,
): State<List<FSplitItem>> {
   return fSplitState(
      delimiters = delimiters.toList(),
      ignoreCase = ignoreCase,
   )
}

@Composable
fun CharSequence.fSplitState(
   delimiters: List<String>,
   ignoreCase: Boolean = false,
): State<List<FSplitItem>> {
   val content = this
   return produceState(initialValue = emptyList(), content, delimiters, ignoreCase) {
      value = withContext(Dispatchers.Default) {
         content.fSplit(
            delimiters = delimiters,
            ignoreCase = ignoreCase,
         )
      }
   }
}