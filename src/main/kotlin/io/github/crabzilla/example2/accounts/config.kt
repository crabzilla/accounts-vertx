package io.github.crabzilla.accounts.domain.accounts

import io.github.crabzilla.accounts.domain.accounts.AccountCommand.DepositMoney
import io.github.crabzilla.accounts.domain.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.accounts.domain.accounts.AccountCommand.WithdrawMoney
import io.github.crabzilla.accounts.domain.accounts.AccountEvent.AccountOpened
import io.github.crabzilla.accounts.domain.accounts.AccountEvent.MoneyDeposited
import io.github.crabzilla.accounts.domain.accounts.AccountEvent.MoneyWithdrawn
import io.github.crabzilla.core.FeatureComponent
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
      subclass(AccountOpened::class, AccountOpened.serializer())
      subclass(MoneyDeposited::class, MoneyDeposited.serializer())
      subclass(MoneyWithdrawn::class, MoneyWithdrawn.serializer())
    }
}

val accountConfig = FeatureComponent(
  Account::class,
  AccountCommand::class,
  AccountEvent::class,
  accountEventHandler,
  { AccountCommandHandler() }
)