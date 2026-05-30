package workWithData

import workWithFile.*
import PrintConsole.AppError

// класс для хранения агрегированных показателей
data class AgrResult(
    var avg: MutableMap<String, Double>,
    var sum: MutableMap<String, Double>,
    var min: MutableMap<String, Double>,
    var max: MutableMap<String, Double>
)

// добавление / изменение заказа
// если index == -1 — новый заказ, иначе заменяем существующий по ID
fun addOrder(data: AgencyData?, err: AppError, index: Int): Boolean {
    if (data == null) { err.code = 3; return false }

    print("Order ID: ")
    val newId = readln().toIntOrNull()
    if (newId == null) { err.code = 1; return false }

    print("Client name: "); val client   = readln()
    print("Ad type: ");     val adType   = readln()
    print("Status: ");      val status   = readln()
    print("Deadline: ");    val deadline = readln()

    print("Services (space-separated names): ")
    val svcNames = readln().split(" ").filter { it.isNotEmpty() }

    val svcList = mutableListOf<AdService>()
    for (name in svcNames) {
        print("  Cost for $name: ");     val cost     = readln()
        print("  Duration for $name: "); val duration = readln()
        svcList.add(AdService(name, cost, duration))
    }

    val newOrder = AdOrder(newId, client, adType, status, deadline, svcList)

    when (index) {
        -1   -> data.orders.add(newOrder)
        else -> {
            val idx = data.orders.indexOfFirst { it.id == index }
            if (idx == -1) { err.code = 4; return false }
            data.orders[idx] = newOrder
        }
    }
    return true
}

// удаление заказа по ID
fun delOrder(data: AgencyData?, err: AppError): Boolean {
    if (data == null) { err.code = 3; return false }
    val id = readln().toIntOrNull()
    if (id == null) { err.code = 1; return false }
    val removed = data.orders.removeIf { it.id == id }
    if (!removed) { err.code = 4; return false }
    return true
}

// поиск заказов по набору параметров (логика ИЛИ — хотя бы одно совпадение)
fun findOrders(data: AgencyData?, err: AppError, params: List<Any?>): List<AdOrder> {
    val result = mutableListOf<AdOrder>()
    if (data == null) { err.code = 3; return result }

    // получаем все критерии поиска
    val searchId     = params[0] as? Int
    val searchClient = (params[1] as? String).orEmpty()
    val searchType   = (params[2] as? String).orEmpty()
    val searchStatus = (params[3] as? String).orEmpty()
    @Suppress("UNCHECKED_CAST")
    val svcSearch    = (params[4] as? List<String> ?: emptyList()).filter { it.isNotEmpty() }

    // если все критерии пустые — показываем все заказы
    if (searchId == null && searchClient.isEmpty() && searchType.isEmpty()
        && searchStatus.isEmpty() && svcSearch.isEmpty()) {
        return data.orders.toMutableList()
    }

    for (order in data.orders) {
        if (searchId != null && order.id == searchId) {
            result.add(order); continue
        }
        if (searchClient.isNotEmpty() && order.clientName == searchClient) {
            result.add(order); continue
        }
        if (searchType.isNotEmpty() && order.adType == searchType) {
            result.add(order); continue
        }
        if (searchStatus.isNotEmpty() && order.status == searchStatus) {
            result.add(order); continue
        }
        if (svcSearch.isNotEmpty() && order.services.any { it.serviceName in svcSearch }) {
            result.add(order); continue
        }
    }

    if (result.isEmpty()) err.code = 4
    return result
}

// сортировка данных
fun sortOrders(data: AgencyData?, err: AppError, flags: List<Boolean>): Boolean {
    if (data == null) { err.code = 3; return false }
    if (flags[0]) data.orders.sortBy { it.id }
    if (flags[1]) data.orders.sortBy { it.clientName }
    if (flags[2]) data.orders.sortBy { it.adType }
    if (flags[3]) data.orders.sortBy { it.status }
    if (flags[4]) data.orders.sortBy { it.services.size }
    return true
}

// разбираем строку вроде "15000 rub" → (15000.0, "rub")
fun parseCost(cost: String): Pair<Double, String>? {
    val regex = Regex("""(\d+(\.\d+)?)\s+(\w+)""")
    val m = regex.find(cost) ?: return null
    val value = m.groupValues[1].toDouble()
    val unit  = m.groupValues[3]
    return Pair(value, unit)
}

// вычисление агрегированных показателей по стоимостям услуг
fun calcAggregates(data: AgencyData?, err: AppError): AgrResult? {
    if (data == null) { err.code = 3; return null }

    val byUnit = mutableMapOf<String, MutableList<Double>>()
    for (order in data.orders) {
        for (svc in order.services) {
            val parsed = parseCost(svc.cost) ?: continue
            val (v, u) = parsed
            byUnit.getOrPut(u) { mutableListOf() }.add(v)
        }
    }

    if (byUnit.isEmpty()) { err.code = 3; return null }

    val avgMap = mutableMapOf<String, Double>()
    val sumMap = mutableMapOf<String, Double>()
    val minMap = mutableMapOf<String, Double>()
    val maxMap = mutableMapOf<String, Double>()

    for ((unit, vals) in byUnit) {
        avgMap[unit] = vals.average()
        sumMap[unit] = vals.sum()
        minMap[unit] = vals.min()
        maxMap[unit] = vals.max()
    }

    return AgrResult(avgMap, sumMap, minMap, maxMap)
}
