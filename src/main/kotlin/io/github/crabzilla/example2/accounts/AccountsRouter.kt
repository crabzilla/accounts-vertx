package io.github.crabzilla.example2.accounts

import io.github.crabzilla.example2.CommandResource
import io.github.crabzilla.example2.CommandResource.Companion.COMMAND_ROUTE
import io.github.crabzilla.example2.CommandResource.Companion.ID_PARAM
import io.github.crabzilla.example2.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.stack.CrabzillaContext
import io.vertx.ext.web.Router

object AccountsRouter {
  context(CrabzillaContext, CommandResource<Account, AccountCommand, AccountEvent>)
  fun router(): Router {
    val router = Router.router(vertx())
    router.put("/:$ID_PARAM")
      .handler {
        handle(it) { stateId, body ->
          OpenAccount(stateId, body.getString("cpf"), body.getString("name"))
        }
      }
    router.post("/:$ID_PARAM/:$COMMAND_ROUTE")
      .handler { handle(it) }
    return router
  }
}
