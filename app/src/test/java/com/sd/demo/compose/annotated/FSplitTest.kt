package com.sd.demo.compose.annotated

import com.sd.lib.compose.annotated.FSplitItem
import com.sd.lib.compose.annotated.fSplit
import org.junit.Assert.assertEquals
import org.junit.Test

class FSplitTest {
   @Test
   fun `test empty content`() {
      val content = ""
      content.fSplit("123").also { result ->
         assertEquals(1, result.size)
         val item = result.first()
         item.assertIsTarget(false)
         item.assertIs(content)
      }
   }

   @Test
   fun `test empty delimiters`() {
      val content = "123"
      content.fSplit("").also { result ->
         assertEquals(1, result.size)
         val item = result.first()
         item.assertIsTarget(false)
         item.assertIs(content)
      }

      content.fSplit("", "").also { result ->
         assertEquals(1, result.size)
         val item = result.first()
         item.assertIsTarget(false)
         item.assertIs(content)
      }
   }
}

private fun FSplitItem.assertIs(expected: String) {
   assertEquals(expected, content)
}

private fun FSplitItem.assertIsTarget(expected: Boolean) {
   assertEquals(expected, isTarget)
}