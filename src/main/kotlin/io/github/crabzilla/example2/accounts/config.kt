package io.github.crabzilla.example2.accounts

import io.github.crabzilla.core.FeatureComponent
import io.github.crabzilla.example2.accounts.AccountCommand.DepositMoney
import io.github.crabzilla.example2.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.example2.accounts.AccountCommand.WithdrawMoney
import io.github.crabzilla.example2.accounts.AccountEvent.AccountOpened
import io.github.crabzilla.example2.accounts.AccountEvent.MoneyDeposited
import io.github.crabzilla.example2.accounts.AccountEvent.MoneyWithdrawn
import io.github.crabzilla.example2.kotlinx.javaModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@kotlinx.serialization.ExperimentalSerializationApi
val accountModule = SerializersModule {
    include(javaModule)
    polymorphic(Account::class) {
      subclass(Account::class, Account.serializer())
    }
    polymorphic(AccountCommand::class) {
      subclass(OpenAccount::class, OpenAccount.serializer())
      subclass(DepositMoney::class, DepositMoney.serializer())
      subclass(WithdrawMoney::class, WithdrawMoney.serializer())
    }
    polymorphic(AccountEvent::class) {
      subclass(AccountOpened::class, AccountEvent.AccountOpened.serializer())
      subclass(MoneyDeposited::class, MoneyDeposited.serializer())
      subclass(MoneyWithdrawn::class, MoneyWithdrawn.serializer())
    }
}

val featureComponent = FeatureComponent(
  Account::class,
  AccountCommand::class,
  AccountEvent::class,
  accountEventHandler,
  { AccountCommandHandler() }
)