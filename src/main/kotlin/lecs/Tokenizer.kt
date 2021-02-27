package lecs

import arrow.core.Either

/**
 * Generates a [List] of [Token] from a [String].
 *
 * The accepted [Token] are as follows:
 * * Single quote delimited strings with escapable single quotes terminating on end of line and end of string,
 * * Double quote delimited strings with escapable double quotes terminating on end of line and end of string,
 * * Semicolon signaled line comments,
 * * Brackets: (, ), {, }, &#91;, &#93;, and
 * * Whitespace separated unicode-16 strings.
 *
 * Whitespace is considered insignificant and is only used to mark separation of tokens.
 *
 * This method may report the following [TokenizerError]:
 * * [MalformedStringError]: the closing quote does not appear on the same line as the opening quote or does not exist at all.
 *
 * @param[input] A [String] representation of the source code.
 * @return Either the first [TokenizerError] reported during tokenization or the [List] of [Token] produced from [input].
 */
fun tokenize(input: String): Either<TokenizerError, List<Token>> {
    val matches = tokenizerRegex.findAll(input)
    val tokens = mutableListOf<Token>()

    for (token in matches.map { match -> Token(match.value.trim(), match.range) }) {
        if ((token.value.startsWith("\"") && !token.value.endsWith("\"")) or
            (token.value.startsWith("'") && !token.value.endsWith("'"))
        )
            return Either.left(MalformedStringError(token))
        else
            tokens.add(token)
    }

    return Either.right(tokens)
}

/*
 * ('(?:[^'\\]|\\.)*?(['\n]|$)): Matches single-quote delimited strings terminating on end of line and end of file
 *                               and accounts for escaped single quotes inside the string
 * ("(?:[^"\\]|\\.)*?(["\n]|$)): Matches double-quote delimited strings terminating on end of line and end of file
 *                               and accounts for escaped double quotes inside the string. Yes there's probably
 *                               some way of combining this and the previous case, but I haven't figured it out
 * ;.+: Matches comments
 * \(|\)|\{|}|\[|]: Match the braces
 * [^'";()\[\]{}\s]+: Match the rest of the symbols
 */
private val tokenizerRegex =
    """('(?:[^'\\]|\\.)*?(['\n]|$))|("(?:[^"\\]|\\.)*?(["\n]|$))|;.+|\(|\)|\{|}|\[|]|[^'";()\[\]{}\s]+""".toRegex()

/**
 * Used internally. Please ignore.
 */
sealed class TokenizerTypes

/**
 * The representation of the significant subunits of the program source code.
 *
 * @property[value] The [String] representation of the significant subunits of the source code.
 * @property[location] The location within the source code of the significant subunit.
 */
data class Token(val value: String, val location: IntRange) : TokenizerTypes()
internal object StartOfStream : TokenizerTypes()

/**
 * The supertype of all tokenizer errors.
 */
sealed class TokenizerError

/**
 * The [TokenizerError] representing malformed string tokens.
 *
 * @property[malformedString] The [Token] representing the malformed string in the source code.
 */
data class MalformedStringError(val malformedString: Token) : TokenizerError()