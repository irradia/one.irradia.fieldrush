package one.irradia.fieldrush.api

import com.fasterxml.jackson.core.JsonToken

interface FRJSONStreamType {

  val currentToken: JsonToken?

  val isExpectedStartArrayToken: Boolean

  val isExpectedStartObjectToken: Boolean

  val currentPosition: FRLexicalPosition

  val currentName: String

  val currentText: String

  fun nextToken(): FRParseResult<JsonToken?>

  fun skip(): FRParseResult<Unit>
}
