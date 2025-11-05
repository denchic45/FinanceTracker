package com.denchic45.financetracker.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object YearMonthSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("YearMonth", PrimitiveKind.STRING)

    private const val PATTERN = "yyyy_MM"
    val formatter = DateTimeFormatter.ofPattern(PATTERN)

    override fun serialize(encoder: Encoder, value: YearMonth) {
        val result = value.format(formatter)
        encoder.encodeString(result)
    }

    override fun deserialize(decoder: Decoder): YearMonth {
        return YearMonth.parse(decoder.decodeString(), formatter)
    }

    fun format(yearMonth: YearMonth): String = yearMonth.format(formatter)
}
