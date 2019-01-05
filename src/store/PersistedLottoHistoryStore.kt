package com.example.store

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object LottoDrawsTable : IdTable<String>("lotto_draws") {
    override val id = varchar("id", 255).primaryKey().entityId()
    val date = date("date")
    val primaryNumbers = varchar("primary_numbers", 255)
    val secondaryNumbers = varchar("secondary_numbers", 255).nullable()
    val tertiaryNumbers = varchar("tertiary_numbers", 255).nullable()
}

class LottoDrawDAO(id: EntityID<String>): Entity<String>(id) {
    companion object : EntityClass<String, LottoDrawDAO>(LottoDrawsTable)

    var date by LottoDrawsTable.date
    var primaryNumbers by LottoDrawsTable.primaryNumbers.transform(setToString, stringToSet)
    var secondaryNumbers by LottoDrawsTable.secondaryNumbers.transform(setToStringNullable, stringToSetNullable)
    var tertiaryNumbers by LottoDrawsTable.tertiaryNumbers.transform(setToStringNullable, stringToSetNullable)
}

class PersistedLottoHistoryStore : LottoHistoryStore {
    init {
        Database.connect(
            url = "jdbc:postgresql://localhost:9432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(LottoDrawsTable)
        }
    }

    override fun saveDraws(draws: List<Draw>) {
        draws.forEach {
            try {
                transaction {
                    LottoDrawDAO.new(it.id) {
                        date = it.date.toJodaDateTime()
                        primaryNumbers = it.primaryNumbers
                        secondaryNumbers = it.secondaryNumbers
                        tertiaryNumbers = it.tertiaryNumbers
                    }
                }
            } catch (e: Exception) {
                println("Couldn't insert draw to db: $e")
            }
        }
    }

    override fun getLatestDraw(): Draw? =
        getAllDraws()
            .sortedBy(Draw::date)
            .lastOrNull()

    override fun getAllDraws(): List<Draw> = transaction {
        LottoDrawDAO
            .all()
            .map {
                Draw(
                    it.id.value,
                    it.date.toJavaLocalDate(),
                    it.primaryNumbers,
                    it.secondaryNumbers,
                    it.tertiaryNumbers
                )
            }
    }
}

private fun org.joda.time.DateTime.toJavaLocalDate() =
    java.time.LocalDate.of(year, monthOfYear, dayOfMonth)

private fun java.time.LocalDate.toJodaDateTime() =
    org.joda.time.LocalDate(year, monthValue, dayOfMonth).toDateTimeAtStartOfDay()

private val setToString = { set: Set<Int> -> setToStringNullable(set)!! }

private val stringToSet = { string: String -> stringToSetNullable(string)!! }

private val setToStringNullable = { set: Set<Int>? -> set?.joinToString(",")}

private val stringToSetNullable = { string: String? ->
    string
        ?.split(",")
        ?.mapNotNull { it.toIntOrNull() }
        ?.toSet()
}