package io.github.crabzilla.example2.accounts

import io.github.crabzilla.CrabzillaContext
import io.github.crabzilla.example2.FeatureResource
import io.github.crabzilla.example2.FeatureResource.Companion.ID_PARAM
import io.github.crabzilla.accounts.domain.accounts.AccountCommand.DepositMoney
import io.github.crabzilla.accounts.domain.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.accounts.domain.accounts.AccountCommand.WithdrawMoney
import io.github.crabzilla.accounts.domain.accounts.accountConfig
import io.github.crabzilla.accounts.domain.accounts.accountModule
import io.github.crabzilla.kotlinx.KotlinxJsonObjectSerDer
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExperimentalSerializationApi
class AccountsVerticle: AbstractVerticle() {

  companion object {
    private val log: Logger = LoggerFactory.getLogger(AccountsVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {

    val crabzilla = CrabzillaContext.new(vertx, config())
    val json = Json { serializersModule = accountModule }
    val serDer = KotlinxJsonObjectSerDer(json, accountConfig)
    val controller = crabzilla.featureController(accountConfig, serDer)
    val featureResource = FeatureResource(controller)

    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router
      .put("/accounts/:$ID_PARAM")
      .handler {
        featureResource.handle(it)
        { (metadata, body) -> OpenAccount(metadata.stateId, body.getString("cpf"), body.getString("name")) }
      }
    router
      .put("/accounts/:$ID_PARAM/deposit")
      .handler {
        featureResource.handle(it) { (_, body) -> DepositMoney(body.getDouble("amount")) }
      }
    router
      .put("/accounts/:$ID_PARAM/withdraw")
      .handler {
        featureResource.handle(it) { (_, body) -> WithdrawMoney(body.getDouble("amount")) }
      }

    vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080) { http ->
        if (http.succeeded()) {
          startPromise.complete()
          println("Deployed")
          log.info("HTTP server started on port 8080")
        } else {
          startPromise.fail(http.cause())
        }
      }
  }

  override fun stop() {
    log.info("**** Stopped")
  }

}