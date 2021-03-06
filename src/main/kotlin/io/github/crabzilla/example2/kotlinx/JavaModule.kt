package io.github.crabzilla.example2.kotlinx

import kotlinx.serialization.modules.SerializersModule
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@kotlinx.serialization.ExperimentalSerializationApi
val javaModule = SerializersModule {
  contextual(UUID::class, UUIDSerializer)
  contextual(LocalDateTime::class, LocalDateTimeSerializer)
  contextual(LocalDate::class, LocalDateSerializer)
  contextual(BigDecimal::class, BigDecimalAsStringSerializer)
}
