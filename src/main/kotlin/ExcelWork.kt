import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TextInputDialog
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream


class ExcelWork(val file: File) {
    private var countedRows = 0
    private var ferreArray = ArrayList<ObjectForFerre>()

    fun getFerreArray() = ferreArray

    fun printExcelFileToConsole() {
        ferreArray = ArrayList<ObjectForFerre>()
        var colWithPlace = 2 //Номер столбца с местом пробы
        val dialog = TextInputDialog("2")

        dialog.title = "Номер столбца с местом пробы"
        dialog.headerText = "Введите номер столбца с местом пробы:"
        dialog.contentText = "Номер столбца:"

        val result = dialog.showAndWait()

        result.ifPresent { num -> colWithPlace = num.toInt() }
        val fis = FileInputStream(file)
        val wb =
            if (file.extension == "xls") HSSFWorkbook(fis) else XSSFWorkbook(file)//creating workbook instance that refers to .xls file
//            val wb = HSSFWorkbook(fis) //creating workbook instance that refers to .xls file
        val sheet = wb.getSheetAt(0) //creating a Sheet object to retrieve the object
        val formulaEvaluator: FormulaEvaluator = wb.creationHelper.createFormulaEvaluator()
        var i = 0
        var found = 0 //номер ячейки с текстом "физ.песок", от него удобно считать остальные ячейки
//            for (row in sheet)  {//iteration over row using for each loop
//                found = getCellNumber(row, formulaEvaluator)
//                println()
//                if (found!=0) {
//                    break
//                }
//                i++
//            }
        for (i1 in 0..sheet.lastRowNum) {//iteration over row using for each loop
            val row = sheet.getRow(i1)
            println("row = $row")
            if (row != null) found = getCellNumber(row, formulaEvaluator)
            println()
            if (found != 0) {
                break
            }
            i++
        }
        println("found = $found  i = $i")
//            var anchorCell: HSSFCell? = null //XSSFCell
        var anchorCell: Cell?
        if (found > 0) { //если найдена ячейка с нужным якорным значением
            anchorCell = sheet.getRow(i).getCell(found, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK)
            println("anchorCell = $anchorCell\t")
            while (anchorCell == null) {
                println("null")
                found--
                anchorCell = sheet.getRow(i).getCell(found)
            }
            while (!anchorCell.toString().contains("физ.песок")) {
                println("_")
                found--
                anchorCell = sheet.getRow(i).getCell(found)
            }
            println("anchorCell = $anchorCell\t")
//                while (formulaEvaluator.evaluateInCell(anchorCell).cellType != CellType.STRING && !anchorCell?.stringCellValue!!.contains("физ.песок"))    {
//                        println("anchorCell = $anchorCell\t")
//                        anchorCell = sheet.getRow(i).getCell(found--)
//                }//                println("ok")
//                if (anchorCell.toString().contains("физ.песок")){
//                    println("anchorCell contains физ.песок")
//                } else {
//                    println("cell ${anchorCell}")
//                    println("smth wrong")
//                }
            if (formulaEvaluator.evaluateInCell(anchorCell).cellType == CellType.STRING && anchorCell!!.stringCellValue.contains(
                    "физ.песок"
                )
            ) {
                println("anchorCell is correct")
                countedRows = 0
                println("sheet.physicalNumberOfRows = ${sheet.physicalNumberOfRows}")
                for (j in i..sheet.lastRowNum) {
                    val row = sheet.getRow(j)
                    var cellsPrinted = 0
                    for (k in found - 8 until found) {
                        val cell = row.getCell(k)
                        if (formulaEvaluator.evaluateInCell(cell) != null) {
                            when (formulaEvaluator.evaluateInCell(cell).cellType) {
                                CellType.NUMERIC -> {//getting the value of the cell as a number
//                                        val valueWith3digits = String.format("%.3f", cell.numericCellValue)
//                                        print("" + valueWith3digits + "\t")
                                    cellsPrinted++
                                }

                                CellType.STRING -> {//getting the value of the cell as a string
//                                        print(cell.stringCellValue + "\t")
                                    cellsPrinted++
                                }

                                else -> {}
                            }
                        }
                    }
                    if (cellsPrinted > 0 && j > i) { //если в ряду были данные и это не ряд с заголовками
                        countedRows++
//                            val samplePlace = row.getCell(2).stringCellValue
                        val samplePlace = row.getCell(colWithPlace - 1).toString()
                        print("samplePlace = $samplePlace")
//                            val sampleData1 = row.getCell(3).stringCellValue
                        val sampleData1 = row.getCell(colWithPlace).toString()
//                            val sampleData2 = row.getCell(4).stringCellValue
                        val sampleData2 = row.getCell(colWithPlace + 1).toString()
//                            val sand = row.getCell(found - 1).numericCellValue
                        val sand = row.getCell(found).numericCellValue
                        val sandWith3digits = String.format("%.3f", sand)
//                            val dust = row.getCell(found - 4).numericCellValue + row.getCell(found - 5).numericCellValue
                        val dust = row.getCell(found - 3).numericCellValue + row.getCell(found - 4).numericCellValue
                        val dustWith3digits = String.format("%.3f", dust)
//                            val mud= row.getCell(found - 3).numericCellValue
                        val mud = row.getCell(found - 2).numericCellValue
                        val mudWith3digits = String.format("%.3f", mud)
                        val ferreResult = ObjectForFerre.checkForFerreResult(mud, sand, dust)
                        val objForFerre = ObjectForFerre(
                            countedRows,
                            samplePlace,
                            sampleData1,
                            sampleData2,
                            sand,
                            dust,
                            mud,
                            ferreResult
                        )
                        ferreArray.add(objForFerre)
                        print("место = $samplePlace данные 1 = $sampleData1 данные 2 = $sampleData2 песок = $sandWith3digits  пыль = $dustWith3digits  ил = $mudWith3digits ")
                        println("end row")
                    } else if (cellsPrinted > 0) println("end row")
                }
                println("done printing, countedRows = $countedRows")
                println("ferreArray size = ${ferreArray.size} ")

            }
        } else { //если якорная ячейка не найдена
            println("anchorCell isn't correct")
            val alert = Alert(AlertType.INFORMATION)
            alert.title = "Problem with parsing xls file"
            alert.headerText = null
            alert.contentText = "AnchorCell isn't correct!"

            alert.showAndWait()
            //exitProcess(1)
        }
//        }
    }

    //метод для получения номера ячейки с текстом "физ.песок"
    private fun getCellNumber(row: Row, formulaEvaluator: FormulaEvaluator): Int {
        var printData = true
        var cellNumber = 0
//        println("cells in row = ${row.lastCellNum}")

        for (cn in row.lastCellNum downTo 0) {
            // If the cell is missing from the file, generate a blank one
            // (Works by specifying a MissingCellPolicy)
            val cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
            if (cell.toString().contains("физ.песок")) {
                print("CELL: $cn --> $cell")
                cellNumber = cn
                break
            }
        }
        println()
        return cellNumber
    }

    fun getRowsAmount(): Int {
        return countedRows - 1 //-1 - потому что первый - заголовки
    }
}