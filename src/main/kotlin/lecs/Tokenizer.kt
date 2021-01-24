package lecs

/*
 * Converts a string into a list of tokens. The tokens are as follows:
 * * Single quote delimited strings with escapable single quotes terminating on end of line and end of string,
 * * Double quote delimited strings with escapable double quotes terminating on end of line and end of string,
 * * Semicolon signaled line comments,
 * * Brackets: (, ), {, }, [, ],
 * * Whitespace separated unicode-16 strings.
 *
 * Whitespace is considered insignificant and is only used to mark separation of tokens.
 *
 * @param[input]
 * @return The list of tokens produced from [input]
 */
fun tokenize(input: String): List<Token> {
    val matches = tokenizerRegex.findAll(input)
    return matches.map { match -> Token(match.value.trim(), match.range) }.toList()
}

/*
 * ('(?:[^'\\]|\\.)*?(['\n]|$)): Matches single-quote dilimited strings terminating on end of line and end of file
 *                               and accounts for escaped single quotes inside the string
 * ("(?:[^"\\]|\\.)*?(["\n]|$)): Matches double-quote dilimited strings terminating on end of line and end of file
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