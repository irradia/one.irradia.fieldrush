package one.irradia.fieldrush.vanilla

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import one.irradia.fieldrush.api.FRJSONStreamType
import one.irradia.fieldrush.api.FRLexicalPosition
import one.irradia.fieldrush.api.FRParseError
import one.irradia.fieldrush.api.FRParseResult
import one.irradia.fieldrush.api.FRParseResult.FRParseFailed
import java.net.URI

internal class FRJSONStream(
  private val documentURI: URI,
  private val jsonParser: JsonParser) : FRJSONStreamType {

  override val currentText: String
    get() = this.jsonParser.text

  private var currentTokenVar: JsonToken? =
    this.jsonParser.nextToken()

  private var currentPositionVar =
    FRLexicalPosition(source = this.documentURI, line = 1, column = 0)

  override val currentPosition
    get() = this.currentPositionVar

  override val currentToken: JsonToken?
    get() = this.currentTokenVar

  override val isExpectedStartArrayToken: Boolean
    get() = this.jsonParser.isExpectedStartArrayToken

  override val isExpectedStartObjectToken: Boolean
    get() = this.jsonParser.isExpectedStartObjectToken

  override val currentName: String
    get() = this.jsonParser.currentName

  override fun nextToken(): FRParseResult<JsonToken?> {
    return try {
      this.currentTokenVar = this.jsonParser.nextToken()
      this.currentPositionVar =
        FRLexicalPosition(
          source = this.documentURI,
          line = this.jsonParser.currentLocation.lineNr,
          column = this.jsonParser.currentLocation.columnNr)
      return FRParseResult.succeed(this.currentTokenVar)
    } catch (e: Exception) {
      FRParseFailed(
        errors = listOf(
          FRParseError(
            "core",
            this.currentPosition,
            message = e.message ?: "JSON parsing failed",
            exception = e)))
    }
  }

  override fun skip(): FRParseResult<Unit> {
    return try {
      val before =
        this.jsonParser.currentToken

      if (before.isStructStart) {
        this.jsonParser.skipChildren()
        this.currentTokenVar = this.jsonParser.currentToken()
        this.currentPositionVar =
          FRLexicalPosition(
            source = this.documentURI,
            line = this.jsonParser.currentLocation.lineNr,
            column = this.jsonParser.currentLocation.columnNr)
      }

      this.nextToken().map { }
    } catch (e: Exception) {
      FRParseFailed(
        errors = listOf(
          FRParseError(
            "core",
            this.currentPosition,
            message = e.message ?: "JSON parsing failed",
            exception = e)))
    }
  }
}
