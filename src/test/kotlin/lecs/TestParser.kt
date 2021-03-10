package lecs

import arrow.core.Either
import kotlin.test.Test
import kotlin.test.assertEquals
import lecs.parser.*

class TestParser {
    @Test
    fun testParser() {
        for ((input, benchmarks) in parserTestData) {
            val results = parse(input)
            assertEquals(results, benchmarks)
        }
    }

    private val parserTestData: List<Pair<List<Token>, Either<ParseError, List<LecsNode>>>> =
        listOf(
            Pair(
                listOf(
                    Token("""(""", 0..0),
                    Token("""defn""", 1..4),
                    Token("""bla""", 6..8),
                    Token("""[""", 10..10),
                    Token("""]""", 11..11),
                    Token("""{""", 13..13),
                    Token("""do""", 14..15),
                    Token("""something""", 17..25),
                    Token("""}""", 26..26),
                    Token(""")""", 27..27),
                ),
                Either.right(
                    listOf(
                        SequenceNode(
                            Token("""(""", 0..0),
                            listOf(
                                SymbolNode(Token("""defn""", 1..4)),
                                SymbolNode(Token("""bla""", 6..8)),
                                SequenceNode(Token("""[""", 10..10), listOf(), Token("""]""", 11..11)),
                                SequenceNode(
                                    Token("""{""", 13..13),
                                    listOf(
                                        SymbolNode(Token("""do""", 14..15)),
                                        SymbolNode(Token("""something""", 17..25)),
                                    ),
                                    Token("""}""", 26..26)
                                ),
                            ),
                            Token(""")""", 27..27)
                        ),
                    )
                )
            ),
            Pair(
                listOf(
                    Token("""(""", 0..0),
                    Token("""defn""", 1..4),
                    Token("""bla""", 6..8),
                    Token("""[""", 10..10),
                    Token("""]""", 11..11),
                    Token("""{""", 13..13),
                    Token("""do""", 14..15),
                    Token("""something""", 17..25),
                    Token("""}""", 26..26),
                    Token(""""a string"""", 28..37),
                    Token(""")""", 38..38),
                    Token(""";asdf""", 39..43),
                    Token("""(""", 45..45),
                    Token("""define""", 46..51),
                    Token("""do-more-things""", 53..66),
                    Token("""[""", 68..68),
                    Token("""]""", 69..69),
                    Token("""(""", 71..71),
                    Token("""more""", 72..75),
                    Token("""things""", 77..82),
                    Token(""")""", 83..83),
                    Token(""")""", 84..84),
                    Token("""(""", 86..86),
                    Token("""a""", 87..87),
                    Token("""thrid""", 89..93),
                    Token("""thing""", 95..99),
                    Token(""")""", 100..100),
                ),
                Either.right(
                    listOf(
                        SequenceNode(
                            Token("""(""", 0..0),
                            listOf(
                                SymbolNode(Token("""defn""", 1..4)),
                                SymbolNode(Token("""bla""", 6..8)),
                                SequenceNode(Token("""[""", 10..10), listOf(), Token("""]""", 11..11)),
                                SequenceNode(
                                    Token("""{""", 13..13),
                                    listOf(
                                        SymbolNode(Token("""do""", 14..15)),
                                        SymbolNode(Token("""something""", 17..25)),
                                    ),
                                    Token("""}""", 26..26)
                                ),
                                SymbolNode(Token(""""a string"""", 28..37)),
                            ),
                            Token(""")""", 38..38)
                        ),
                        SequenceNode(
                            Token("""(""", 45..45),
                            listOf(
                                SymbolNode(Token("""define""", 46..51)),
                                SymbolNode(Token("""do-more-things""", 53..66)),
                                SequenceNode(Token("""[""", 68..68), listOf(), Token("""]""", 69..69)),
                                SequenceNode(
                                    Token("""(""", 71..71),
                                    listOf(
                                        SymbolNode(Token("""more""", 72..75)),
                                        SymbolNode(Token("""things""", 77..82)),
                                    ),
                                    Token(""")""", 83..83)
                                ),
                            ),
                            Token(""")""", 84..84)
                        ),
                        SequenceNode(
                            Token("""(""", 86..86),
                            listOf(
                                SymbolNode(Token("""a""", 87..87)),
                                SymbolNode(Token("""thrid""", 89..93)),
                                SymbolNode(Token("""thing""", 95..99)),
                            ),
                            Token(""")""", 100..100)
                        ),
                    )
                )
            ),
            Pair(
                listOf(
                    Token("""(""", 0..0),
                    Token("""defn""", 1..4),
                    Token("""do-something""", 6..17),
                    Token("""[""", 19..19),
                    Token("""]""", 20..20),
                    Token("""{""", 22..22),
                    Token("""unmatched""", 23..31),
                    Token("""brace""", 33..37),
                    Token("""]""", 38..38),
                    Token(""")""", 39..39),
                ),
                Either.left(MismatchedClosingBrace(Token("""{""", 22..22), Token("""]""", 38..38)))
            ),
            Pair(
                listOf(
                    Token("""(""", 0..0),
                    Token("""defn""", 1..4),
                    Token("""do-something""", 6..17),
                    Token("""[""", 19..19),
                    Token("""]""", 20..20),
                    Token("""{""", 22..22),
                    Token("""unmatched""", 23..31),
                    Token("""brace""", 33..37),
                    Token("""}""", 38..38),
                    Token("""}""", 39..39),
                ),
                Either.left(MismatchedClosingBrace(Token("""(""", 0..0), Token("""}""", 39..39)))
            ),
            Pair(
                listOf(
                    Token("""(""", 0..0),
                    Token("""defn""", 1..4),
                    Token("""do-something""", 6..17),
                    Token("""[""", 19..19),
                    Token("""]""", 20..20),
                    Token("""{""", 22..22),
                    Token("""unmatched""", 23..31),
                    Token("""brace""", 33..37),
                    Token("""}""", 38..38),
                    Token(""")""", 39..39),
                    Token("""]""", 40..40),
                ),
                Either.left(UnmatchedClosingBrace(Token("""]""", 40..40)))
            ),
            Pair(
                listOf(
                    Token("""(""", 0..0),
                    Token("""[""", 1..1),
                    Token("""{""", 2..2),
                    Token("""(""", 3..3),
                    Token("""defn""", 4..7),
                    Token("""do-something""", 9..20),
                    Token("""[""", 22..22),
                    Token("""]""", 23..23),
                    Token("""{""", 25..25),
                    Token("""unmatched""", 26..34),
                    Token("""brace""", 36..40),
                    Token("""}""", 41..41),
                    Token(""")""", 42..42),
                ),
                Either.left(
                    UnmatchedOpeningBraces(
                        listOf(
                            Token("""(""", 0..0),
                            Token("""[""", 1..1),
                            Token("""{""", 2..2)
                        )
                    )
                )
            )
        )
}