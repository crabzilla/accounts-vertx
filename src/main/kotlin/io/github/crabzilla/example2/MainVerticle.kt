package io.github.crabzilla.example2

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExperimentalSerializationApi
class MainVerticle : CoroutineVerticle() {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
    private val cores = Runtime.getRuntime().availableProcessors()
  }

  // TODO https://vertx.io/blog/unit-and-integration-tests/ to test with Rest Assured

  override suspend fun start() {

    val config: JsonObject =
      JsonObject()
        .put("url", "postgresql://localhost:5432/accounts")
        .put("username", "user1")
        .put("password", "pwd1")

    log.info("Config ${config.encodePrettily()}")

    val opt = DeploymentOptions().setConfig(config).setInstances(cores / 2)

    vertx.deployVerticle(WebVerticle::class.java.name, opt)

    log.info("Deployed")

  }
}
