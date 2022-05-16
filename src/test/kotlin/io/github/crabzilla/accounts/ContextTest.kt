package io.github.crabzilla.accounts

import io.github.crabzilla.core.CommandValidator
import io.github.crabzilla.core.EventHandler
import io.github.crabzilla.example2.accounts.AccountCommand
import io.github.crabzilla.example2.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.example2.accounts.AccountEvent.AccountOpened
import io.github.crabzilla.example2.accounts.accountEventHandler
import io.github.crabzilla.example2.accounts.accountValidator
import java.util.UUID


// Just to test context receivers

interface Logger {
  fun log()
}

class MyLogger : Logger {
  override fun log() {
    println("doing")
  }
}

context (Logger)
fun store() {
  log()
}

//

interface Feature<S : Any, C: Any, E: Any> {
  fun doIt()
}

class FeatureX<C: Any>() : Feature<Any, Any, C> {
  override fun doIt() {
    println("doing 2")
  }
}

context (Logger, CommandValidator<C>, EventHandler<S, E>)
fun <S: Any, C: Any, E: Any> x() {
  println("**** ")
}

fun main() {
  with(MyLogger()) {
    store()
  }
  with(FeatureX<AccountCommand>()) {
    doIt()
    with(MyLogger()) {
      log()
      with(accountValidator) {
         val e = validate(OpenAccount(UUID.randomUUID(), "cpf", "name"))
         log()
         println(e.size)
        with(accountEventHandler) {
          x()
          val s = handle(null, AccountOpened(UUID.randomUUID(), "cpf", "name"))
          println(s)
        }
      }
    }
  }
}





