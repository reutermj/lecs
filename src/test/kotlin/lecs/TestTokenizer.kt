package lecs

import arrow.core.Either
import kotlin.test.Test
import kotlin.test.assertEquals

class TestTokenizer {
    @Test
    fun testTokenizer() {
        for ((input, benchmarks) in tokenizerTestData) {
            val tokens = tokenize(input.replace("\\n", "\n"))
            assertEquals(benchmarks, tokens)
        }
    }

    private val tokenizerTestData: List<Pair<String, Either<TokenizerError, List<Token>>>> =
        listOf(
            Pair(
                """"\"" "\\\"" "\\\\\""""",
                Either.right(
                    listOf(
                        Token(""""\""""", 0..3),
                        Token(""""\\\""""", 5..10),
                        Token(""""\\\\\""""", 12..19)
                    )
                )
            ),
            Pair(
                """"\'" "\\\'" "\\\\\'"""",
                Either.right(
                    listOf(
                        Token(""""\'"""", 0..3),
                        Token(""""\\\'"""", 5..10),
                        Token(""""\\\\\'"""", 12..19)
                    )
                )
            ),
            Pair(
                """'\'' '\\\'' '\\\\\''""",
                Either.right(
                    listOf(
                        Token("""'\''""", 0..3),
                        Token("""'\\\''""", 5..10),
                        Token("""'\\\\\''""", 12..19)
                    )
                )
            ),
            Pair(
                """'\"' '\\\"' '\\\\\"'""",
                Either.right(
                    listOf(
                        Token("""'\"'""", 0..3),
                        Token("""'\\\"'""", 5..10),
                        Token("""'\\\\\"'""", 12..19)
                    )
                )
            ),
            Pair(
                """"\\""sometext" "\\\\""sometext" "\\\\\\""sometext"""",
                Either.right(
                    listOf(
                        Token(""""\\"""", 0..3),
                        Token(""""sometext"""", 4..13),
                        Token(""""\\\\"""", 15..20),
                        Token(""""sometext"""", 21..30),
                        Token(""""\\\\\\"""", 32..39),
                        Token(""""sometext"""", 40..49)
                    )
                )
            ),
            Pair(
                """'\\''sometext' '\\\\''sometext' '\\\\\\''sometext'""",
                Either.right(
                    listOf(
                        Token("""'\\'""", 0..3),
                        Token("""'sometext'""", 4..13),
                        Token("""'\\\\'""", 15..20),
                        Token("""'sometext'""", 21..30),
                        Token("""'\\\\\\'""", 32..39),
                        Token("""'sometext'""", 40..49)
                    )
                )
            ),
            Pair(
                """"'" '"'""",
                Either.right(
                    listOf(
                        Token(""""'"""", 0..2),
                        Token("""'"'""", 4..6)
                    )
                )
            ),
            Pair(
                """'"' "'"""",
                Either.right(
                    listOf(
                        Token("""'"'""", 0..2),
                        Token(""""'"""", 4..6)
                    )
                )
            ),
            Pair(
                """"(" ")" "[" "]" "{" "}"""",
                Either.right(
                    listOf(
                        Token(""""("""", 0..2),
                        Token("""")"""", 4..6),
                        Token(""""["""", 8..10),
                        Token(""""]"""", 12..14),
                        Token(""""{"""", 16..18),
                        Token(""""}"""", 20..22)
                    )
                )
            ),
            Pair(
                """'(' ')' '[' ']' '{' '}'""",
                Either.right(
                    listOf(
                        Token("""'('""", 0..2),
                        Token("""')'""", 4..6),
                        Token("""'['""", 8..10),
                        Token("""']'""", 12..14),
                        Token("""'{'""", 16..18),
                        Token("""'}'""", 20..22)
                    )
                )
            ),
            Pair(
                """"("(")")"["["]"]"{"{"}"}""",
                Either.right(
                    listOf(
                        Token(""""("""", 0..2),
                        Token("""(""", 3..3),
                        Token("""")"""", 4..6),
                        Token(""")""", 7..7),
                        Token(""""["""", 8..10),
                        Token("""[""", 11..11),
                        Token(""""]"""", 12..14),
                        Token("""]""", 15..15),
                        Token(""""{"""", 16..18),
                        Token("""{""", 19..19),
                        Token(""""}"""", 20..22),
                        Token("""}""", 23..23)
                    )
                )
            ),
            Pair(
                """'('(')')'['[']']'{'{'}'}""",
                Either.right(
                    listOf(
                        Token("""'('""", 0..2),
                        Token("""(""", 3..3),
                        Token("""')'""", 4..6),
                        Token(""")""", 7..7),
                        Token("""'['""", 8..10),
                        Token("""[""", 11..11),
                        Token("""']'""", 12..14),
                        Token("""]""", 15..15),
                        Token("""'{'""", 16..18),
                        Token("""{""", 19..19),
                        Token("""'}'""", 20..22),
                        Token("""}""", 23..23)
                    )
                )
            ),
            Pair(
                """"('"'("'(")'"')"')"['"'["'["]'"']"']"{'"'{"'{"}'"'}"'}""",
                Either.right(
                    listOf(
                        Token(""""('"""", 0..3),
                        Token("""'("'""", 4..7),
                        Token("""(""", 8..8),
                        Token("""")'"""", 9..12),
                        Token("""')"'""", 13..16),
                        Token(""")""", 17..17),
                        Token(""""['"""", 18..21),
                        Token("""'["'""", 22..25),
                        Token("""[""", 26..26),
                        Token(""""]'"""", 27..30),
                        Token("""']"'""", 31..34),
                        Token("""]""", 35..35),
                        Token(""""{'"""", 36..39),
                        Token("""'{"'""", 40..43),
                        Token("""{""", 44..44),
                        Token(""""}'"""", 45..48),
                        Token("""'}"'""", 49..52),
                        Token("""}""", 53..53)
                    )
                )
            ),
            Pair(
                """"('\"\\"'("\'\\'(")'\"\\"')"\'\\')"['\"\\"'["\'\\'["]'\"\\"']"\'\\']"{'\"\\"'{"\'\\'{"}'\"\\"'}"\'\\'}""",
                Either.right(
                    listOf(
                        Token(""""('\"\\"""", 0..7),
                        Token("""'("\'\\'""", 8..15),
                        Token("""(""", 16..16),
                        Token("""")'\"\\"""", 17..24),
                        Token("""')"\'\\'""", 25..32),
                        Token(""")""", 33..33),
                        Token(""""['\"\\"""", 34..41),
                        Token("""'["\'\\'""", 42..49),
                        Token("""[""", 50..50),
                        Token(""""]'\"\\"""", 51..58),
                        Token("""']"\'\\'""", 59..66),
                        Token("""]""", 67..67),
                        Token(""""{'\"\\"""", 68..75),
                        Token("""'{"\'\\'""", 76..83),
                        Token("""{""", 84..84),
                        Token(""""}'\"\\"""", 85..92),
                        Token("""'}"\'\\'""", 93..100),
                        Token("""}""", 101..101)
                    )
                )
            ),
            Pair(
                """"('\\\"\\\\\\"'("\\\'\\\\\\'(")'\\\"\\\\\\"')"\\\'\\\\\\')"['\\\"\\\\\\"'["\\\'\\\\\\'["]'\\\"\\\\\\"']"\\\'\\\\\\']"{'\\\"\\\\\\"'{"\\\'\\\\\\'{"}'\\\"\\\\\\"'}"\\\'\\\\\\'}""",
                Either.right(
                    listOf(
                        Token(""""('\\\"\\\\\\"""", 0..13),
                        Token("""'("\\\'\\\\\\'""", 14..27),
                        Token("""(""", 28..28),
                        Token("""")'\\\"\\\\\\"""", 29..42),
                        Token("""')"\\\'\\\\\\'""", 43..56),
                        Token(""")""", 57..57),
                        Token(""""['\\\"\\\\\\"""", 58..71),
                        Token("""'["\\\'\\\\\\'""", 72..85),
                        Token("""[""", 86..86),
                        Token(""""]'\\\"\\\\\\"""", 87..100),
                        Token("""']"\\\'\\\\\\'""", 101..114),
                        Token("""]""", 115..115),
                        Token(""""{'\\\"\\\\\\"""", 116..129),
                        Token("""'{"\\\'\\\\\\'""", 130..143),
                        Token("""{""", 144..144),
                        Token(""""}'\\\"\\\\\\"""", 145..158),
                        Token("""'}"\\\'\\\\\\'""", 159..172),
                        Token("""}""", 173..173)
                    )
                )
            ),
            Pair(
                """"some(text'\"\\"'some(text"\'\\'some(text"some)text'\"\\"'some)text"\'\\'some)text"some[text'\"\\"'some[text"\'\\'some[text"some]text'\"\\"'some]text"\'\\'some]text"some{text'\"\\"'some{text"\'\\'some{text"some}text'\"\\"'some}text"\'\\'some}text""",
                Either.right(
                    listOf(
                        Token(""""some(text'\"\\"""", 0..15),
                        Token("""'some(text"\'\\'""", 16..31),
                        Token("""some""", 32..35),
                        Token("""(""", 36..36),
                        Token("""text""", 37..40),
                        Token(""""some)text'\"\\"""", 41..56),
                        Token("""'some)text"\'\\'""", 57..72),
                        Token("""some""", 73..76),
                        Token(""")""", 77..77),
                        Token("""text""", 78..81),
                        Token(""""some[text'\"\\"""", 82..97),
                        Token("""'some[text"\'\\'""", 98..113),
                        Token("""some""", 114..117),
                        Token("""[""", 118..118),
                        Token("""text""", 119..122),
                        Token(""""some]text'\"\\"""", 123..138),
                        Token("""'some]text"\'\\'""", 139..154),
                        Token("""some""", 155..158),
                        Token("""]""", 159..159),
                        Token("""text""", 160..163),
                        Token(""""some{text'\"\\"""", 164..179),
                        Token("""'some{text"\'\\'""", 180..195),
                        Token("""some""", 196..199),
                        Token("""{""", 200..200),
                        Token("""text""", 201..204),
                        Token(""""some}text'\"\\"""", 205..220),
                        Token("""'some}text"\'\\'""", 221..236),
                        Token("""some""", 237..240),
                        Token("""}""", 241..241),
                        Token("""text""", 242..245)
                    )
                )
            ),
            Pair(
                """";";as"dfasdf""",
                Either.right(
                    listOf(
                        Token("""";"""", 0..2),
                        Token(""";as"dfasdf""", 3..12)
                    )
                )
            ),
            Pair(
                """';';as'dfasdf""",
                Either.right(
                    listOf(
                        Token("""';'""", 0..2),
                        Token(""";as'dfasdf""", 3..12)
                    )
                )
            ),
            Pair(
                """(asdf df \n [] d2-=: [ <>.!@dfw 54afv %# f)""",
                Either.right(
                    listOf(
                        Token("""(""", 0..0),
                        Token("""asdf""", 1..4),
                        Token("""df""", 6..7),
                        Token("""[""", 11..11),
                        Token("""]""", 12..12),
                        Token("""d2-=:""", 14..18),
                        Token("""[""", 20..20),
                        Token("""<>.!@dfw""", 22..29),
                        Token("""54afv""", 31..35),
                        Token("""%#""", 37..38),
                        Token("""f""", 40..40),
                        Token(""")""", 41..41)
                    )
                )
            ),
            Pair(
                """"asdf""",
                Either.left(
                    MalformedStringError(Token(""""asdf""", 0..4))
                )
            ),
            Pair(
                """'asdf""",
                Either.left(
                    MalformedStringError(Token("""'asdf""", 0..4))
                )
            ),
            Pair(
                """(defn fun [] " a string\n  continued here")""",
                Either.left(
                    MalformedStringError(Token("""" a string""", 13..23))
                )
            ),
            Pair(
                """(defn fun [] ' a string\n  continued here')""",
                Either.left(
                    MalformedStringError(Token("""' a string""", 13..23))
                )
            )
        )
}
