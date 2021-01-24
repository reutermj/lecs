package lecs

import arrow.core.Either

/*
 * Generates a parse tree represented by a list of nodes from a list of tokens
 * or returns an error documenting the first parse error encountered when iterating the list of tokens.
 *
 * This method will report the following parse errors:
 * * Mismatched closing brace. The found closing brace does not match the respective opening brace,
 * * Unmatched opening brace. All tokens were parsed but some opening braces were never closed.
 * * Unmatched closing brace. No opening brace was found to match the found closing brace
 */
fun parse(tokens: List<Token>): Either<ParseError, List<LecsNode>> {
    val context = initializeParserContext()
    
    for (token in tokens) {
        if (isComment(token)) continue

        when (token.value) {
            ")", "}", "]" -> {
                val (nodes, openingBrace) = context.pop()

                when (openingBrace) {
                    is StartOfStream -> return Either.left(UnmatchedClosingBrace(token))
                    is Token -> {
                        if (areBracesMismatched(openingBrace, token))
                            return Either.left(MismatchedClosingBrace(openingBrace, token))

                        context.nodes.add(
                            when (getMode(openingBrace)) {
                                ParseMode.ListMode -> ListNode(openingBrace, nodes, token)
                                ParseMode.SetMode -> SetNode(openingBrace, nodes, token)
                                ParseMode.VectorMode -> VectorNode(openingBrace, nodes, token)
                            }
                        )
                    }
                }
            }
            "(", "{", "[" -> context.push(token)
            else -> context.nodes.add(SymbolNode(token))
        }
    }

    val unmatchedOpeningBraces = getUnmatchedOpeningBraces(context)
    if (unmatchedOpeningBraces.any())
        return Either.left(UnmatchedOpeningBraces(unmatchedOpeningBraces))

    return Either.right(context.nodes)
}

/* Return type definitions */
sealed class LecsNode
data class ListNode(val startToken: Token, val nodes: List<LecsNode>, val endToken: Token) : LecsNode()
data class VectorNode(val startToken: Token, val nodes: List<LecsNode>, val endToken: Token) : LecsNode()
data class SetNode(val startToken: Token, val nodes: List<LecsNode>, val endToken: Token) : LecsNode()
data class SymbolNode(val token: Token) : LecsNode()

sealed class ParseError
data class MismatchedClosingBrace(val openingBrace: Token, val closingBrace: Token) : ParseError()
data class UnmatchedClosingBrace(val closingBrace: Token) : ParseError()
data class UnmatchedOpeningBraces(val openingBraces: List<Token>) : ParseError()

/* Implementation details */
private enum class ParseMode {
    ListMode, SetMode, VectorMode
}

private fun getMode(bracket: Token): ParseMode =
    when (bracket.value) {
        "(", ")" -> ParseMode.ListMode
        "{", "}" -> ParseMode.SetMode
        else -> ParseMode.VectorMode
    }

private fun areBracesMismatched(openingBrace: Token, closingBrace: Token) =
    getMode(openingBrace) != getMode(closingBrace)

private fun getUnmatchedOpeningBraces(context: MutableList<Pair<MutableList<LecsNode>, TokenizerTypes>>): List<Token> {
    return context.map { it.second } //select the opening braces from the frames still on the stack
                  .filterIsInstance<Token>() //remove the StartOfStream at the bottom of the stack
}

private fun isComment(token: Token) = token.value.startsWith(";")

/*
 * The above parsing algorithm uses a stack to keep track of the current state of the parse tree as it's being constructed.
 * The following provides the implementation for the stack operations
 * */
private fun initializeParserContext(): MutableList<Pair<MutableList<LecsNode>, TokenizerTypes>> =
    mutableListOf(Pair(mutableListOf(), StartOfStream))

private fun MutableList<Pair<MutableList<LecsNode>, TokenizerTypes>>.push(token: Token) {
    this.add(Pair(mutableListOf(), token))
}

private fun MutableList<Pair<MutableList<LecsNode>, TokenizerTypes>>.pop(): Pair<MutableList<LecsNode>, TokenizerTypes> {
    val contextFrame = this[this.size - 1]
    this.removeAt(this.size - 1)
    return contextFrame
}

private val MutableList<Pair<MutableList<LecsNode>, TokenizerTypes>>.nodes: MutableList<LecsNode>
    get() = this[this.size - 1].first