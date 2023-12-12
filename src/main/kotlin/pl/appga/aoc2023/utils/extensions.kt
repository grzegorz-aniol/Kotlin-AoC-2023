package pl.appga.aoc2023.utils

import java.math.BigInteger
import java.security.MessageDigest

fun <T> List<T>.mulOf(f: (T) -> Int): Int =
    fold(1) { acc, it -> acc * f(it) }

fun <T> List<T>.mulOfLong(f: (T) -> Long): Long =
    fold(1L) { acc, it -> acc * f(it) }

fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')
