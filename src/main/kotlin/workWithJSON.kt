package workWithFile

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import PrintConsole.AppError

// отдельная услуга внутри заказа
@Serializable
data class AdService(
    val serviceName: String,  // название услуги
    val cost: String,         // стоимость, например "15000 rub"
    val duration: String      // длительность, например "14 days"
)

// рекламный заказ
@Serializable
data class AdOrder(
    val id: Int,
    val clientName: String,
    val adType: String,       // billboard, banner, TV и т.д.
    val status: String,       // active, completed, cancelled
    val deadline: String,
    val services: MutableList<AdService>
)

// общий контейнер данных агентства
@Serializable
data class AgencyData(
    val orders: MutableList<AdOrder>
)

// загрузка из JSON
fun loadFromJson(fileName: String, err: AppError): AgencyData? {
    return try {
        val txt = File(fileName).readText(Charsets.UTF_8)
        Json.decodeFromString<AgencyData>(txt)
    } catch (e: Exception) {
        err.code = 5
        null
    }
}

// сохранение в JSON с форматированием (prettyPrint)
fun saveToJson(path: String, data: AgencyData?, err: AppError): Boolean {
    if (data == null) { err.code = 3; return false }
    val json = Json { prettyPrint = true }
    val txt  = json.encodeToString(data)
    File(path).writeText(txt, Charsets.UTF_8)
    return true
}
