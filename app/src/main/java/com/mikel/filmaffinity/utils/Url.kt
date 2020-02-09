package com.mikel.filmaffinity.utils

val EXTRACT_URL_REGEX = """(https?:\/\/[^\s]*)""".toRegex(setOf(RegexOption.MULTILINE))

fun extractUrl(text: String): String? {
    val match = EXTRACT_URL_REGEX.find(text) ?: return null

    return match.groups[1]?.value
}