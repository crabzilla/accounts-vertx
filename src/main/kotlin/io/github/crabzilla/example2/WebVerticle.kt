package io.github.crabzilla.example2

import io.github.crabzilla.example2.accounts.AccountsRouter
import io.github.crabzilla.example2.accounts.accountModule
import io.github.crabzilla.example2.accounts.accountsComponent
import io.github.crabzilla.kotlinx.KotlinxJsonObjectSerDer
import io.github.crabzilla.stack.DefaultVertxContextFactory
import io.github.crabzilla.stack.command.DefaultCommandServiceApiFactory
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router.router
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.serialization.json.Json

class WebVerticle : AbstractVerticle() {
  override fun start(start: Promise<Void>) {
    val router = router(vertx)
    router.route().handler(BodyHandler.create())
    router.get("/").handler {
      it.end("ok")
    }
    val crabzillaContext = DefaultVertxContextFactory().new(vertx, config())
    with (crabzillaContext) {
      val factory = DefaultCommandServiceApiFactory(crabzillaContext)
      with(factory) {
        val json = Json { serializersModule = accountModule }
        val serDer = KotlinxJsonObjectSerDer(json, accountsComponent)
        val service = commandService(accountsComponent, serDer)
        val commandsRoutes = mapOf(Pair("deposit", "DepositMoney"), Pair("withdraw", "WithdrawMoney"))
        with(CommandResource(service, serDer, commandsRoutes)) {
          router.route("/accounts/*").subRouter(AccountsRouter.router())
        }
      }
    }
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080)
      .onSuccess {
        start.complete()
      }.onFailure {
        start.fail(it)
      }
  }
}
