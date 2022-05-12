package io.github.crabzilla.example2

import io.github.crabzilla.stack.EventRecord
import io.github.crabzilla.stack.EventRecord.Companion.toJsonArray
import io.github.crabzilla.stack.JsonObjectSerDer
import io.github.crabzilla.stack.command.FeatureException.BusinessException
import io.github.crabzilla.stack.command.FeatureException.ConcurrencyException
import io.github.crabzilla.stack.command.FeatureException.ValidationException
import io.github.crabzilla.stack.command.FeatureService
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.UUID

internal class FeatureResource<S: Any, C: Any, E: Any>(
  private val featureService: FeatureService<S, C, E>,
  private val serDer: JsonObjectSerDer<S, C, E>
)
{
  companion object {
    const val ID_PARAM: String = "id"
    private val log = LoggerFactory.getLogger(FeatureResource::class.java)
  }

  fun handle(ctx: RoutingContext, commandFactory: (UUID, JsonObject) -> C) {
    val (id, body) = extractIdAndBody(ctx)
    log.trace("id {}, body {}", id, body.encode())
    featureService.handle(id, commandFactory.invoke(id, body))
      .onSuccess { successHandler(ctx, it) }
      .onFailure { errorHandler(ctx, it) }
  }

  fun handle(ctx: RoutingContext, commandType: String) {
    val (id, body) = extractIdAndBody(ctx)
    log.trace("id {}, body {}", id, body.encode())
    val command = serDer.commandFromJson(body.put("type", commandType))
    log.trace("command {}", command)
    featureService.handle(id, command)
      .onSuccess { successHandler(ctx, it) }
      .onFailure { errorHandler(ctx, it) }
  }

  private fun extractIdAndBody(ctx: RoutingContext) : Pair<UUID, JsonObject> {
    val id = UUID.fromString(ctx.request().getParam(ID_PARAM))
    return Pair(id, ctx.bodyAsJson)
  }

  private fun successHandler(ctx: RoutingContext, data: List<EventRecord>) {
    log.debug("Success")
    ctx.response().setStatusCode(201).end(data.toJsonArray().encode())
  }

  private fun errorHandler(ctx: RoutingContext, error: Throwable) {
    log.error("Error {}", error.localizedMessage)
    // a naive convention, but hopefully effective for this demo
    when (error.cause) {
      is ValidationException -> ctx.response().setStatusCode(400).setStatusMessage(error.localizedMessage).end()
      is ConcurrencyException -> ctx.response().setStatusCode(409).setStatusMessage(error.localizedMessage).end()
      is BusinessException -> ctx.response().setStatusCode(422).setStatusMessage(error.localizedMessage).end()
      else -> ctx.response().setStatusCode(500).end()
    }
  }

}