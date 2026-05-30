import PrintConsole.*
import workWithData.addOrder
import workWithData.delOrder
import workWithData.findOrders
import workWithData.sortOrders
import workWithData.AgrResult
import workWithData.calcAggregates
import workWithFile.*

// пути к файлам
val pathJSON     = "inputJSON.json"
val pathCSV      = "inputCSV.csv"
val pathSaveJSON = "outputJSON.json"
val pathSaveCSV  = "outputCSV.csv"

var agencyData: AgencyData? = null

// выполняет нужную команду в зависимости от ввода
fun executeCommand(cmd: Int, data: AgencyData?, err: AppError) {
    err.code = 0  // сброс кода ошибки перед каждой командой
    when (cmd) {
        1  -> printMenu()

        2  -> if (!printData(data, err)) printError(err)

        3  -> {
                 agencyData = loadFromJson(pathJSON, err)
                 if (agencyData == null) printError(err) else dataLoaded()
             }

        4  -> {
                 agencyData = loadFromCsv(pathCSV, err)
                 if (agencyData == null) printError(err) else dataLoaded()
             }

        5  -> {
                 printNewOrderGuide()
                 if (addOrder(data, err, -1)) orderAdded() else printError(err)
             }

        6  -> {
                 print("Enter order ID to delete: ")
                 if (delOrder(data, err)) orderDeleted() else printError(err)
             }

        7  -> if (saveToJson(pathSaveJSON, data, err)) dataSaved(pathSaveJSON) else printError(err)

        8  -> if (saveToCsv(pathSaveCSV, data, err))  dataSaved(pathSaveCSV)  else printError(err)

        9  -> {
                 printEditGuide()
                 print("Enter ID of order to edit: ")
                 val id = readln().toIntOrNull()
                 if (id == null) { wrongInput(); return }
                 printNewOrderGuide()
                 if (addOrder(data, err, id)) orderUpdated() else printError(err)
             }

        10 -> {
             println("Search orders (leave empty to skip)")
             print("Order ID: ")
             val inputId     = readln().toIntOrNull()
             print("Client name: ")
             val inputClient = readln()
             print("Ad type: ")
             val inputType   = readln()
             print("Status: ")
             val inputStatus = readln()
             print("Service names (space-separated): ")
             val inputSvcs   = readln().split(" ").filter { it.isNotEmpty() }
             val params = listOf(inputId, inputClient, inputType, inputStatus, inputSvcs)
             val found  = findOrders(data, err, params)
             if (found.isEmpty()) printError(err)
             else {
                 println("+------Found orders---------+")
                 found.forEach { printOrderEntry(it) }
             }
        }

        11 -> {
             println("Sort by field? (y / n)")
             print("Order ID: ")
             val byId     = readln() == "y"
             print("Client name: ")
             val byClient = readln() == "y"
             print("Ad type: ")
             val byType   = readln() == "y"
             print("Status: ")
             val byStatus = readln() == "y"
             print("Number of services: ")
             val bySvcs   = readln() == "y"
             val flags = listOf(byId, byClient, byType, byStatus, bySvcs)
             if (sortOrders(data, err, flags)) dataSorted() else printError(err)
        }

        12 -> {
             val res: AgrResult? = calcAggregates(data, err)
             if (res == null) printError(err) else printAggregates(res)
        }

        else -> wrongNumber()
    }
}

fun main() {
    val err = AppError(0)
    printMenu()
    // автозагрузка данных из JSON при старте
    agencyData = loadFromJson(pathJSON, err)
    if (agencyData != null) dataLoaded()
    err.code = 0  // сброс после попытки автозагрузки

    while (true) {
        print("> ")
        val cmd = readln().toIntOrNull()
        if (cmd == null) { wrongInput(); continue }
        else executeCommand(cmd, agencyData, err)
    }
}
