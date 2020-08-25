package io.github.lordraydenmk.superheroesapp.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StringExtKtTest : FunSpec({

    test("md5 - stojan - ab995d0e21fcd42ac20a55a558a6d929") {
        "stojan".md5() shouldBe "ab995d0e21fcd42ac20a55a558a6d929"
    }

    test("md5 - 1abcd1234 - ffd275c5130566a2916217b101f26150") {
        "1abcd1234".md5() shouldBe "ffd275c5130566a2916217b101f26150"
    }
})
