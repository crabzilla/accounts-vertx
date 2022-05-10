package io.github.crabzilla.example2

import io.github.crabzilla.kotlinx.KotlinxJsonObjectSerDer
import io.github.crabzilla.stack.EventRecord
import io.github.crabzilla.stack.command.FeatureService
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.UUID

internal class FeatureResource<S: Any, C: Any, E: Any>(
  private val featureController: FeatureService<S, C, E>,
  private val serDer: KotlinxJsonObjectSerDer<S, C, E>
  )
{
  companion object {
    const val ID_PARAM: String = "id"
    private val log = LoggerFactory.getLogger(FeatureResource::class.java)
  }

  fun handle(ctx: RoutingContext, commandFactory: (UUID, JsonObject) -> C) {
    val (id, body) = extractIdAndBody(ctx)
    featureController.handle(id, commandFactory.invoke(id, body))
      .onSuccess { successHandler(ctx, it) }
      .onFailure { errorHandler(ctx, it) }
  }

  fun handle(ctx: RoutingContext) {
    val (id, body) = extractIdAndBody(ctx)
    val command = serDer.commandFromJson(body)
    featureController.handle(id, command)
      .onSuccess { successHandler(ctx, it) }
      .onFailure { errorHandler(ctx, it) }
  }

  private fun extractIdAndBody(ctx: RoutingContext) : Pair<UUID, JsonObject> {
    val id = UUID.fromString(ctx.request().getParam(ID_PARAM))
    return Pair(id, ctx.bodyAsJson)
  }

  private fun successHandler(ctx: RoutingContext, data: List<EventRecord>) {
    ctx.response().setStatusCode(201).end(JsonArray(data.map { it.toJsonObject()}).encode())
  }

  private fun errorHandler(ctx: RoutingContext, error: Throwable) {
    log.error(ctx.request().absoluteURI(), error)
    // a silly convention, but hopefully effective for this demo
    when (error.cause) {
      is IllegalArgumentException -> ctx.response().setStatusCode(400).setStatusMessage(error.message).end()
      is NullPointerException -> ctx.response().setStatusCode(404).setStatusMessage(error.message).end()
      is IllegalStateException -> ctx.response().setStatusCode(409).setStatusMessage(error.message).end()
      else -> ctx.response().setStatusCode(500).setStatusMessage(error.message).end()
    }
  }

}