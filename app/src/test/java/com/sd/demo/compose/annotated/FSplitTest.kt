package com.sd.demo.compose.annotated

import com.sd.lib.compose.annotated.fSplit
import org.junit.Assert.assertEquals
import org.junit.Test

class FSplitTest {
   @Test
   fun `test empty delimiters`() {
      val content = "123"
      content.fSplit("").also { result ->
         assertEquals(1, result.size)
         assertEquals(content, result.first().content)
      }

      content.fSplit("", "").also { result ->
         assertEquals(1, result.size)
         assertEquals(content, result.first().content)
      }
   }
}