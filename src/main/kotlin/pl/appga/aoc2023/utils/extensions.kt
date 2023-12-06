package pl.appga.aoc2023.utils

fun <T> List<T>.mulOf(f: (T) -> Int): Int =
    fold(1) { acc, it -> acc * f(it) }

fun <T> List<T>.mulOfLong(f: (T) -> Long): Long =
    fold(1L) { acc, it -> acc * f(it) }
