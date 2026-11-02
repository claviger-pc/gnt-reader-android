package com.mattrobertson.greek.reader.reading.ui

import com.mattrobertson.greek.reader.verseref.Word

internal fun wordAtOffset(words: Map<Int, Word>, offset: Int): Word? =
    words.entries.firstOrNull { (start, word) -> offset >= start && offset < start + word.text.length }?.value

internal fun paginateLines(
    textLength: Int,
    pageHeightPx: Int,
    lineCount: Int,
    lineTop: (Int) -> Float,
    lineBottom: (Int) -> Float,
    lineEnd: (Int) -> Int
): List<ReaderPageSlice> {
    if (textLength == 0) return listOf(ReaderPageSlice(0, 0))
    val pages = mutableListOf<ReaderPageSlice>()
    var firstLine = 0
    var start = 0
    while (firstLine < lineCount && start < textLength) {
        val top = lineTop(firstLine)
        var lastLine = firstLine
        while (lastLine + 1 < lineCount && lineBottom(lastLine + 1) - top <= pageHeightPx) lastLine++
        val end = lineEnd(lastLine).coerceIn(start, textLength)
        if (end > start) pages += ReaderPageSlice(start, end)
        start = end
        firstLine = lastLine + 1
    }
    if (start < textLength) pages += ReaderPageSlice(start, textLength)
    return pages.ifEmpty { listOf(ReaderPageSlice(0, textLength)) }
}
