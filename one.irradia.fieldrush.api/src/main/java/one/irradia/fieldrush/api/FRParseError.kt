package one.irradia.fieldrush.api

/**
 * A specific parse error.
 */

data class FRParseError(

  /**
   * The parser that produced the error. This will typically be the name of the core parser, or
   * the name of one of the extension parsers.
   */

  val producer: String,

  /**
   * Lexical information for the parse error.
   */

  val position: FRLexicalPosition,

  /**
   * The error message.
   */

  val message: String,

  /**
   * The exception raised, if any.
   */

  val exception: Exception?)