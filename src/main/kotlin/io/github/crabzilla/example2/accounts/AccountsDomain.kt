package io.github.crabzilla.example2.accounts

import io.github.crabzilla.core.CommandHandler
import io.github.crabzilla.core.CommandValidator
import io.github.crabzilla.core.EventHandler
import io.github.crabzilla.core.FeatureSession
import io.github.crabzilla.example2.accounts.AccountCommand.DepositMoney
import io.github.crabzilla.example2.accounts.AccountCommand.OpenAccount
import io.github.crabzilla.example2.accounts.AccountCommand.WithdrawMoney
import io.github.crabzilla.example2.accounts.AccountEvent.AccountOpened
import io.github.crabzilla.example2.accounts.AccountEvent.MoneyDeposited
import io.github.crabzilla.example2.accounts.AccountEvent.MoneyWithdrawn
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed class AccountEvent {
  @Serializable
  @SerialName("AccountOpened")
  data class AccountOpened(@Contextual val id: UUID, val cpf: String, val name: String) : AccountEvent()
  @Serializable
  @SerialName("MoneyDeposited")
  data class MoneyDeposited(val amount: Double, val finalBalance: Double) : AccountEvent()
  @Serializable
  @SerialName("MoneyWithdrawn")
  data class MoneyWithdrawn(val amount: Double, val finalBalance: Double) : AccountEvent()
}

@Serializable
sealed class AccountCommand {
  @Serializable
  @SerialName("OpenAccount")
  data class OpenAccount(@Contextual val id: UUID, val cpf: String, val name: String) : AccountCommand()
  @Serializable
  @SerialName("DepositMoney")
  data class DepositMoney(val amount: Double) : AccountCommand()
  @Serializable
  @SerialName("WithdrawMoney")
  data class WithdrawMoney(val amount: Double) : AccountCommand()
}

data class Account(
  val id: UUID,
  val cpf: String,
  val name: String,
  val balance: Double = 0.00
) {
  companion object {
    fun fromEvent(event: AccountOpened): Account {
      return Account(id = event.id, cpf = event.cpf, name = event.name)
    }
  }
}

val accountEventHandler = EventHandler<Account, AccountEvent> { state, event ->
  when (event) {
    is AccountOpened -> Account.fromEvent(event)
    is MoneyDeposited -> state!!.copy(balance = state.balance + event.amount)
    is MoneyWithdrawn -> state!!.copy(balance = state.balance - event.amount)
  }
}

val accountValidator = CommandValidator<AccountCommand> {
  listOf()
}

class AccountAlreadyExists(id: UUID) : IllegalArgumentException("Account $id already exists")
class AccountNotFound : NullPointerException("Account not found")
class AccountBalanceNotEnough(id: UUID) : IllegalStateException("Account $id doesn't have enough balance")
class DepositExceeded(amount: Double) : IllegalStateException("Cannot deposit more than $amount")

class AccountCommandHandler : CommandHandler<Account, AccountCommand, AccountEvent>(accountEventHandler) {
  companion object {
    private const val LIMIT = 2000.00
    private fun open(id: UUID, cpf: String, name: String): List<AccountEvent> {
      return listOf(AccountOpened(id = id, cpf, name))
    }
    private fun Account.deposit(amount: Double): List<AccountEvent> {
      if (amount > LIMIT) {
        throw DepositExceeded(LIMIT)
      }
      return listOf(MoneyDeposited(amount, balance + amount))
    }
    private fun Account.withdraw(amount: Double): List<AccountEvent> {
      if (balance < amount) throw AccountBalanceNotEnough(id)
      return listOf(MoneyWithdrawn(amount, balance - amount))
    }
  }
  override fun handleCommand(command: AccountCommand, state: Account?): FeatureSession<Account, AccountEvent> {
    return when (command) {
      is OpenAccount -> {
        if (state != null) throw AccountAlreadyExists(command.id)
        withNew(open(command.id, command.cpf, command.name))
      }
      else -> {
        if (state == null) throw AccountNotFound()
        when (command) {
          is DepositMoney -> with(state).execute { it.deposit(command.amount) }
          is WithdrawMoney -> with(state).execute { it.withdraw(command.amount) }
          else -> throw java.lang.IllegalStateException(command::class.java.name)
        }
      }
    }
  }
}
