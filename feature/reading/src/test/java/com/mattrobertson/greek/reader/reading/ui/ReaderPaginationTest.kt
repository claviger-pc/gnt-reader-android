package com.mattrobertson.greek.reader.reading.ui

import com.mattrobertson.greek.reader.settings.Settings
import com.mattrobertson.greek.reader.verseref.*
import org.junit.Assert.*
import org.junit.Test

class ReaderPaginationTest {
    @Test fun trailingEmptyLineDoesNotCreateAnEmptyPage() {
        val pages = paginateLines(10, 20, 3, { it * 10f }, { (it + 1) * 10f }, { listOf(5, 10, 11)[it] })
        assertEquals(listOf(ReaderPageSlice(0, 10)), pages)
    }

    @Test fun pagesFitTheTextAreaAndCoverEveryCharacter() {
        val pages = paginateLines(25, 20, 5, { it * 10f }, { (it + 1) * 10f }, { (it + 1) * 5 })
        assertEquals(listOf(ReaderPageSlice(0, 10), ReaderPageSlice(10, 20), ReaderPageSlice(20, 25)), pages)
    }

    @Test fun veryShortViewportStillAdvancesOneLineAtATime() {
        val pages = paginateLines(10, 1, 2, { it * 10f }, { (it + 1) * 10f }, { (it + 1) * 5 })
        assertEquals(listOf(ReaderPageSlice(0, 5), ReaderPageSlice(5, 10)), pages)
    }

    @Test fun emptyReadingHasOneEmptyPage() {
        assertEquals(listOf(ReaderPageSlice(0, 0)), paginateLines(0, 20, 0, { 0f }, { 0f }, { 0 }))
    }

    @Test fun wordOffsetsMatchTextWithAndWithoutVerseNumbersAndNewlines() {
        val first = Word("alpha", "alpha", WordParsing.decode("X-"))
        val second = Word("beta", "beta", WordParsing.decode("X-"))
        val ref = VerseRef(Book.JOHN, 1)
        val verses = listOf(Verse(VerseRef(Book.JOHN, 1, 1), listOf(first)), Verse(VerseRef(Book.JOHN, 1, 2), listOf(second)))
        for (newLines in listOf(false, true)) for (numbers in listOf(false, true)) {
            val words = mutableMapOf<Int, Word>()
            val text = buildChapterText(Settings.default.copy(versesOnNewLines = newLines, showVerseNumbers = numbers), ref, verses, words, -1).text
            assertEquals(first, wordAtOffset(words, text.indexOf("alpha")))
            assertEquals(second, wordAtOffset(words, text.indexOf("beta") + 3))
            assertNull(wordAtOffset(words, text.indexOf("alpha") + first.text.length))
            assertEquals(2, words.size)
        }
    }
}
