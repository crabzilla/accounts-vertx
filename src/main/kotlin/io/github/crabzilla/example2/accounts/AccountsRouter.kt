package io.github.crabzilla.example2.accounts

import io.github.crabzilla.example2.FeatureResource
import io.github.crabzilla.example2.FeatureResource.Companion.ID_PARAM
import io.github.crabzilla.example2.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.stack.CrabzillaContext
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

context(CrabzillaContext, FeatureResource<Account, AccountCommand, AccountEvent>)
class AccountsRouter {
  fun router(): Router {
    val router = Router.router(vertx())
    router.route().handler(BodyHandler.create())
    router.put("/:$ID_PARAM")
      .handler {
        handle(it) { stateId, body ->
          OpenAccount(stateId, body.getString("cpf"), body.getString("name"))
        }
      }
    router.post("/:$ID_PARAM/deposit")
      .handler { handle(it, "DepositMoney") }
    router.post("/:$ID_PARAM/withdraw")
      .handler { handle(it, "WithdrawMoney") }
    return router
  }
}