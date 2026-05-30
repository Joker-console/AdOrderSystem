package PrintConsole

import workWithFile.AgencyData
import workWithFile.AdOrder
import workWithData.AgrResult

data class AppError(var code: Int)

fun printMenu() {
    println("""
        +------------------------------------------+
        |              COMMANDS                    |
        +------------------------------------------+
        |  [1]  View commands                      |
        |  [2]  View all orders                    |
        |  [3]  Load from JSON                     |
        |  [4]  Load from CSV                      |
        |  [5]  Add order                          |
        |  [6]  Delete order                       |
        |  [7]  Save to JSON                       |
        |  [8]  Save to CSV                        |
        |  [9]  Edit order                         |
        |  [10] Search orders                      |
        |  [11] Sort orders                        |
        |  [12] Aggregated stats                   |
        +------------------------------------------+
        |  Enter your choice (1-12):               |
        +------------------------------------------+
    """)
}

fun wrongInput() {
    println("""
        +-----------------------------------------+
        |  !        INCORRECT INPUT             ! |
        +-----------------------------------------+
    """)
}

fun wrongNumber() {
    println("""
        +-----------------------------------------+
        |  !          WRONG NUMBER              ! |
        +-----------------------------------------+
    """)
}

fun printNewOrderGuide() {
    println("""
        +--------------------------------------------+
        |  !         New order guide             !   |
        +--------------------------------------------+
        |  1) Order ID                               |
        |  2) Client name                            |
        |  3) Ad type                                |
        |  4) Status                                 |
        |  5) Deadline                               |
        |  6) Services (space-separated names)       |
        |     6.1) Cost for each service             |
        |     6.2) Duration for each service         |
        +--------------------------------------------+
    """)
}

fun printEditGuide() {
    println("""
        +--------------------------------------------+
        |  !         Edit order guide            !   |
        +--------------------------------------------+
        |  1) Enter ID of order to edit              |
        |  2) Fill in new data                       |
        +--------------------------------------------+
    """)
}

fun dataLoaded() {
    println("""
        +-----------------------------------------+
        |  !           DATA LOADED              ! |
        +-----------------------------------------+
    """)
}

fun orderAdded() {
    println("""
        +-----------------------------------------+
        |  !         ORDER ADDED               !  |
        +-----------------------------------------+
    """)
}

fun orderDeleted() {
    println("""
        +-----------------------------------------+
        |  !         ORDER DELETED             !  |
        +-----------------------------------------+
    """)
}

fun orderUpdated() {
    println("""
        +-----------------------------------------+
        |  !         ORDER UPDATED             !  |
        +-----------------------------------------+
    """)
}

fun dataSaved(path: String) {
    println("""
        +-----------------------------------------+
        |   DATA SAVED AS: $path
        +-----------------------------------------+
    """)
}

fun dataSorted() {
    println("""
        +-----------------------------------------+
        |  !          DATA SORTED              !  |
        +-----------------------------------------+
    """)
}

fun emptyData() {
    println("""
        +-----------------------------------------+
        |  !           DATA IS EMPTY           !  |
        +-----------------------------------------+
    """)
}

fun notFoundMsg() {
    println("""
        +-----------------------------------------+
        |  !          NOT FOUND                !  |
        +-----------------------------------------+
    """)
}

fun loadError() {
    println("""
        +-----------------------------------------+
        |  !        ERROR LOADING FILE         !  |
        +-----------------------------------------+
    """)
}

fun printData(data: AgencyData?, err: AppError): Boolean {
    if (data == null) { err.code = 3; return false }
    if (data.orders.isEmpty()) { err.code = 3; return false }
    println("\n+---------------ORDERS-------------------+")
    for (order in data.orders) printOrderEntry(order)
    println("+-----------------------------------------+\n")
    return true
}

fun printOrderEntry(order: AdOrder) {
    println("ID: ${order.id}")
    println("   CLIENT:   ${order.clientName}")
    println("   AD TYPE:  ${order.adType}")
    println("   STATUS:   ${order.status}")
    println("   DEADLINE: ${order.deadline}")
    println("   Services:")
    for (svc in order.services) {
        println("       NAME:     ${svc.serviceName}")
        println("           COST:     ${svc.cost}")
        println("           DURATION: ${svc.duration}")
    }
}

fun printError(err: AppError) {
    when (err.code) {
        1 -> wrongInput()
        2 -> wrongNumber()
        3 -> emptyData()
        4 -> notFoundMsg()
        5 -> loadError()
    }
}

fun printAggregates(res: AgrResult) {
    println("""
        +-----------------------------------------+
        |         Aggregated statistics           |
        +-----------------------------------------+
    """.trimIndent())
    println("AVG:")
    res.avg.forEach { println("   ${it.key}: ${it.value}") }
    println("\nSUM:")
    res.sum.forEach { println("   ${it.key}: ${it.value}") }
    println("\nMIN:")
    res.min.forEach { println("   ${it.key}: ${it.value}") }
    println("\nMAX:")
    res.max.forEach { println("   ${it.key}: ${it.value}") }
    println("+-----------------------------------------+")
}
