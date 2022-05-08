package io.github.crabzilla.example2.accounts

import io.github.crabzilla.CrabzillaContext
import io.github.crabzilla.example2.FeatureResource
import io.github.crabzilla.example2.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.kotlinx.KotlinxJsonObjectSerDer
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.serialization.json.Json

class AccountsRouter(private val vertx: Vertx, private val config: JsonObject) {

  fun router(): Router {
    fun resource(): FeatureResource<Account, AccountCommand, AccountEvent> {
      val crabzilla = CrabzillaContext.new(vertx, config)
      val json = Json { serializersModule = accountModule }
      val serDer = KotlinxJsonObjectSerDer(json, accountsComponent)
      val controller = crabzilla.featureController(accountsComponent, serDer)
      // yes, boilerplate. Waiting for Kotlin context receivers to implement framework free DI
      return FeatureResource(controller, serDer)
    }
    val featureResource = resource()
    val router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router
      .put("/:${FeatureResource.ID_PARAM}")
      .handler { featureResource.handle(it) { (metadata, body) ->
          OpenAccount(metadata.stateId, body.getString("cpf"), body.getString("name"))
        }
      }
    router
      .post("/:${FeatureResource.ID_PARAM}/deposit")
      .handler { featureResource.handle(it) }
    router
      .post("/:${FeatureResource.ID_PARAM}/withdraw")
      .handler { featureResource.handle(it) }
    return router
  }

}