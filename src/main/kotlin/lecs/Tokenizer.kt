package lecs

import arrow.core.Either

/**
 * Converts a string into a list of tokens. The tokens are as follows:
 * * Single quote delimited strings with escapable single quotes terminating on end of line and end of string,
 * * Double quote delimited strings with escapable double quotes terminating on end of line and end of string,
 * * Semicolon signaled line comments,
 * * Brackets: (, ), {, }, [, ],
 * * Whitespace separated unicode-16 strings.
 *
 * Whitespace is considered insignificant and is only used to mark separation of tokens.
 *
 * Malformed strings are considered to be strings where the the closing quote does not appear on the
 * same line as the opening quote or does not exist at all. In the case where a malformed string appears
 * in [input], a MalformedStringError is returned
 *
 * @param[input] A string representation of the program
 * @return Either the list of tokens produced from [input] or the first error reported during tokenization
 */
fun tokenize(input: String): Either<TokenizerError, List<Token>> {
    val matches = tokenizerRegex.findAll(input)
    val tokens = mutableListOf<Token>()

    for(token in matches.map { match -> Token(match.value.trim(), match.range) }) {
        if((token.value.startsWith("\"") && !token.value.endsWith("\"")) or
            (token.value.startsWith("'") && !token.value.endsWith("'")))
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

sealed class TokenizerTypes
data class Token(val value: String, val location: IntRange) : TokenizerTypes()
internal object StartOfStream : TokenizerTypes()

sealed class TokenizerError
data class MalformedStringError(val malformedString: Token) : TokenizerError()