package io.github.crabzilla.example2

import io.github.crabzilla.command.CommandMetadata
import io.github.crabzilla.command.CommandSideEffect
import io.github.crabzilla.command.FeatureController
import io.github.crabzilla.kotlinx.KotlinxJsonObjectSerDer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.UUID

internal class FeatureResource<S: Any, C: Any, E: Any>(
  private val featureController: FeatureController<S, C, E>,
  private val serDer: KotlinxJsonObjectSerDer<S, C, E>
  )
{
  companion object {
    const val ID_PARAM: String = "id"
    private val log = LoggerFactory.getLogger(FeatureResource::class.java)
  }

  fun handle(ctx: RoutingContext, commandFactory: (Pair<CommandMetadata, JsonObject>) -> C) {
    val (metadata, body) = requestHandler(ctx)
    featureController.handle(metadata, commandFactory.invoke(Pair(metadata, body)))
      .onSuccess { successHandler(ctx, it) }
      .onFailure { errorHandler(ctx, it) }
  }

  fun handle(ctx: RoutingContext) {
    val (metadata, body) = requestHandler(ctx)
    val command = serDer.commandFromJson(body)
    featureController.handle(metadata, command)
      .onSuccess { successHandler(ctx, it) }
      .onFailure { errorHandler(ctx, it) }
  }

  private fun requestHandler(ctx: RoutingContext) : Pair<CommandMetadata, JsonObject> {
    val id = UUID.fromString(ctx.request().getParam(ID_PARAM))
    val metadata = CommandMetadata.new(id)
    return Pair(metadata, ctx.bodyAsJson)
  }

  private fun successHandler(ctx: RoutingContext, data: CommandSideEffect) {
    ctx.response().setStatusCode(201).end(data.toJsonArray().encode())
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