package com.sd.demo.compose.annotated

import com.sd.lib.compose.annotated.FSplitItem
import com.sd.lib.compose.annotated.fSplit
import org.junit.Assert.assertEquals
import org.junit.Test

class FSplitTest {

   @Test
   fun `test single delimiter`() {
      val content = "12345-12345"
      content.fSplit("1").also { result ->
         assertEquals(content, result.join())
         assertEquals(4, result.size)
         result[0].assertIs("1").assertIsTarget(true)
         result[1].assertIs("2345-").assertIsTarget(false)
         result[2].assertIs("1").assertIsTarget(true)
         result[3].assertIs("2345").assertIsTarget(false)
      }
   }

   @Test
   fun `test multi delimiter`() {
      val content = "12345-12345"
      content.fSplit("1", "5").also { result ->
         assertEquals(content, result.join())
         assertEquals(7, result.size)
         result[0].assertIs("1").assertIsTarget(true)
         result[1].assertIs("234").assertIsTarget(false)
         result[2].assertIs("5").assertIsTarget(true)
         result[3].assertIs("-").assertIsTarget(false)
         result[4].assertIs("1").assertIsTarget(true)
         result[5].assertIs("234").assertIsTarget(false)
         result[6].assertIs("5").assertIsTarget(true)
      }
   }

   @Test
   fun `test case`() {
      val content = "aBCDCBba"
      content.fSplit("b", ignoreCase = true).also { result ->
         assertEquals(content, result.join())
         assertEquals(6, result.size)
         result[0].assertIs("a").assertIsTarget(false)
         result[1].assertIs("B").assertIsTarget(true)
         result[2].assertIs("CDC").assertIsTarget(false)
         result[3].assertIs("B").assertIsTarget(true)
         result[4].assertIs("b").assertIsTarget(true)
         result[5].assertIs("a").assertIsTarget(false)
      }
   }

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

fun FSplitItem.assertIs(expected: String) = apply {
   assertEquals(expected, content)
}

fun FSplitItem.assertIsTarget(expected: Boolean) = apply {
   assertEquals(expected, isTarget)
}

fun List<FSplitItem>.join(): String {
   return joinToString("") { it.toString() }
}