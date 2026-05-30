package workWithFile

import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.FileReader
import java.io.FileWriter
import PrintConsole.AppError

// загрузка данных из CSV
fun loadFromCsv(path: String, err: AppError): AgencyData? {
    return try {
        val reader = CSVReader(FileReader(path))
        val rows   = reader.readAll()
        reader.close()

        // карта id -> список услуг
        val svcMap    = mutableMapOf<Int, MutableList<AdService>>()
        // карта id -> поля заказа (используем List, порядок: client, adType, status, deadline)
        val orderMeta = linkedMapOf<Int, List<String>>()

        for (i in 1 until rows.size) {  // пропускаем строку-заголовок
            val row = rows[i]
            if (row.size < 8) continue

            val id       = row[0].toIntOrNull() ?: continue
            val client   = row[1]
            val adType   = row[2]
            val status   = row[3]
            val deadline = row[4]
            val svcName  = row[5]
            val cost     = row[6]
            val duration = row[7]

            // сохраняем мета-данные заказа (один раз на id)
            if (!orderMeta.containsKey(id)) {
                orderMeta[id] = listOf(client, adType, status, deadline)
            }

            // добавляем услугу, если поле не пустое
            if (svcName.isNotEmpty()) {
                svcMap.getOrPut(id) { mutableListOf() }.add(AdService(svcName, cost, duration))
            }
        }

        val orders = orderMeta.map { (id, meta) ->
            AdOrder(
                id       = id,
                clientName = meta[0],
                adType   = meta[1],
                status   = meta[2],
                deadline = meta[3],
                services = svcMap.getOrDefault(id, mutableListOf())
            )
        }.toMutableList()

        AgencyData(orders)
    } catch (e: Exception) {
        err.code = 5
        null
    }
}

// сохранение в CSV
fun saveToCsv(path: String, data: AgencyData?, err: AppError): Boolean {
    if (data == null) { err.code = 3; return false }

    val writer = CSVWriter(FileWriter(path))
    // строка-заголовок
    writer.writeNext(arrayOf("id", "clientName", "adType", "status", "deadline",
                              "serviceName", "cost", "duration"))
    for (order in data.orders) {
        if (order.services.isEmpty()) {
            writer.writeNext(arrayOf(
                order.id.toString(), order.clientName, order.adType,
                order.status, order.deadline, "", "", ""
            ))
        } else {
            for (svc in order.services) {
                writer.writeNext(arrayOf(
                    order.id.toString(), order.clientName, order.adType,
                    order.status, order.deadline,
                    svc.serviceName, svc.cost, svc.duration
                ))
            }
        }
    }
    writer.close()
    return true
}
