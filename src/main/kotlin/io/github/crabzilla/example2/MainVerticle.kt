package io.github.crabzilla.example2

import io.github.crabzilla.example2.accounts.AccountsVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExperimentalSerializationApi
class MainVerticle : AbstractVerticle() {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(AccountsVerticle::class.java)
    private val cores = Runtime.getRuntime().availableProcessors()
  }

  override fun start(startPromise: Promise<Void>) {

    val config: JsonObject =
      JsonObject()
        .put("url", "postgresql://localhost:5432/accounts")
        .put("username", "user1")
        .put("password", "pwd1")

    log.info("Config ${config.encodePrettily()}")

    val opt = DeploymentOptions().setConfig(config).setInstances(cores / 2)

    vertx.deployVerticle(AccountsVerticle::class.java.name, opt)
      .onSuccess {
        log.info("Deployed")
        startPromise.complete()
      }
      .onFailure {
        it.printStackTrace()
        startPromise.fail(it)
      }

  }
}
