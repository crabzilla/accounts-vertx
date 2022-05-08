package io.github.crabzilla.example2

import io.github.crabzilla.example2.accounts.AccountsRouter
import io.vertx.ext.web.Router.router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class WebVerticle : CoroutineVerticle() {

  override suspend fun start() {

    val router = router(vertx)

    router.mountSubRouter("/accounts", AccountsRouter(vertx, config).router())
    // TODO transfers

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080)

  }

}
