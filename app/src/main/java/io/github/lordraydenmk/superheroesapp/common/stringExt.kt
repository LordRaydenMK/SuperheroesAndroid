package io.github.lordraydenmk.superheroesapp.common

import java.math.BigInteger
import java.security.MessageDigest

fun String.md5(): String =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16).padStart(32, '0')
