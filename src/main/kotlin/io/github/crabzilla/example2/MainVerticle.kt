package io.github.crabzilla.example2

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.Vertx
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExperimentalSerializationApi
class MainVerticle: AbstractVerticle() {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(MainVerticle::class.java)
    private val cores = Runtime.getRuntime().availableProcessors()
    @JvmStatic
    fun main(args: Array<String>) {
      Vertx.vertx().deployVerticle(MainVerticle())
    }
  }

  // TODO https://vertx.io/blog/unit-and-integration-tests/ to test with Rest Assured

  override fun start(start: Promise<Void>) {
    log.info("Config ${config().encodePrettily()}")
    val opt = DeploymentOptions().setConfig(config()).setInstances(cores)
    vertx.deployVerticle(WebVerticle::class.java.name, opt)
      .onSuccess {
        start.complete()
      }.onFailure {
        start.fail(it)
      }
    log.info("Deployed")
  }
}
