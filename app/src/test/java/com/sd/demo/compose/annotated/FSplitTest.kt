package com.sd.demo.compose.annotated

import com.sd.lib.compose.annotated.FSplitItem
import com.sd.lib.compose.annotated.fSplit
import org.junit.Assert.assertEquals
import org.junit.Test

class FSplitTest {
   @Test
   fun `test empty content`() {
      fun test(content: String) {
         content.fSplit("123").also { result ->
            assertEquals(1, result.size)
            val item = result.first()
            item.assertIsTarget(false)
            item.assertIs(content)
         }
      }

      test(content = "")
      test(content = "    ")
   }

   @Test
   fun `test empty delimiters`() {
      fun test(
         vararg delimiters: String,
         content: String,
      ) {
         content.fSplit(delimiters = delimiters).also { result ->
            assertEquals(1, result.size)
            val item = result.first()
            item.assertIsTarget(false)
            item.assertIs(content)
         }
         content.fSplit(delimiters = delimiters).also { result ->
            assertEquals(1, result.size)
            val item = result.first()
            item.assertIsTarget(false)
            item.assertIs(content)
         }
      }

      test("", content = "123")
      test("    ", content = "123")
   }

   @Test
   fun `test has content in delimiters`() {
      fun test(
         vararg delimiters: String,
         content: String,
      ) {
         content.fSplit(delimiters = delimiters).also { result ->
            assertEquals(1, result.size)
            val item = result.first()
            item.assertIsTarget(true)
            item.assertIs(content)
         }
      }

      test("123", content = "123")
      test("", "123", content = "123")
      test("456", "123", content = "123")
   }
}

fun FSplitItem.assertIs(expected: String) {
   assertEquals(expected, content)
}

fun FSplitItem.assertIsTarget(expected: Boolean) {
   assertEquals(expected, isTarget)
}