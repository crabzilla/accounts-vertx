package io.github.crabzilla.example2

import io.github.crabzilla.example2.accounts.AccountsRouter
import io.github.crabzilla.example2.accounts.accountModule
import io.github.crabzilla.example2.accounts.accountsComponent
import io.github.crabzilla.kotlinx.KotlinxJsonObjectSerDer
import io.github.crabzilla.stack.CrabzillaContext
import io.vertx.ext.web.Router.router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.serialization.json.Json

class WebVerticle : CoroutineVerticle() {
  override suspend fun start() {
    val router = router(vertx)
    val crabzillaContext = CrabzillaContext.new(vertx, config)
    with(crabzillaContext) {
      val json = Json { serializersModule = accountModule }
      val serDer = KotlinxJsonObjectSerDer(json, accountsComponent)
      val service = featureService(accountsComponent, serDer)
      val commandsRoutes = mapOf(Pair("deposit", "DepositMoney"), Pair("withdraw", "WithdrawMoney"))
      with(FeatureResource(service, serDer, commandsRoutes)) {
        router.route("/accounts/*").subRouter(AccountsRouter.router())
      }
    }
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080)
  }
}
