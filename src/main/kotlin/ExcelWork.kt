import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import kotlin.system.exitProcess


class ExcelWork(val file: File) {
    private var contedRows = 0
    fun printExcelFileToConsole(){
        if (file.extension=="xlsx") {
            val file = FileInputStream(file)
            val wb = XSSFWorkbook(file)
            val sheet = wb.getSheetAt(0) //creating a Sheet object to retrieve the object
            val formulaEvaluator: FormulaEvaluator = wb.creationHelper.createFormulaEvaluator()
            for (row in sheet)  {//iteration over row using for each loop
                getCellNumber(row, formulaEvaluator)
                println()
            }

        }
        if (file.extension=="xls") {
            val fis = FileInputStream(file)
            val wb = HSSFWorkbook(fis) //creating workbook instance that refers to .xls file
            val sheet = wb.getSheetAt(0) //creating a Sheet object to retrieve the object
            val formulaEvaluator: FormulaEvaluator = wb.creationHelper.createFormulaEvaluator()
            var i = 0
            var found = 0 //номер ячейки с текстом "физ.песок", от него удобно считать остальные ячейки
            for (row in sheet)  {//iteration over row using for each loop
                found = getCellNumber(row, formulaEvaluator)
                println()
                if (found!=0) {
                    break
                }
                i++
            }
            println("found = $found  i = $i")
            val anchorCell =  sheet.getRow(i).getCell(found-1)
            println("anchorCell = "+anchorCell.stringCellValue + "\t")
            if (formulaEvaluator.evaluateInCell(anchorCell).cellType==CellType.STRING && anchorCell.stringCellValue.contains("физ.песок"))
                println("anchorCell is correct")
            else {
                println("anchorCell isn't correct")
                val alert = Alert(AlertType.INFORMATION)
                alert.title = "Problem with parsing xls file"
                alert.headerText = null
                alert.contentText = "AnchorCell isn't correct!"

                alert.showAndWait()
                exitProcess(1)
            }
            contedRows = 0
            println("sheet.physicalNumberOfRows = ${sheet.physicalNumberOfRows}")
            for (j in i..sheet.lastRowNum) {
                val row = sheet.getRow(j)
                var cellsPrinted = 0
                for (k in found - 8 until found) {
                    val cell = row.getCell(k)
                    if (formulaEvaluator.evaluateInCell(cell)!=null) {
                        when (formulaEvaluator.evaluateInCell(cell).cellType) {
                            CellType.NUMERIC -> {//getting the value of the cell as a number
                                val valueWith3digits = String.format("%.3f", cell.numericCellValue)
                                print("" + valueWith3digits + "\t")
                                cellsPrinted++
                            }

                            CellType.STRING -> {//getting the value of the cell as a string
                                print(cell.stringCellValue + "\t")
                                cellsPrinted++
                            }
                            else -> {}
                        }
                    }
                }
                //todo добавить получение названия ряда из 3-й ячейки ряда
                //todo сделать массив для хранения нужных данных каждого ряда в виде объектов ObjectForFerre
                if (cellsPrinted>0 && j>i) { //если с ряду были данные и это не ряд с заголовками
                    contedRows++
                    val sandWith3digits = String.format("%.3f", row.getCell(found-1).numericCellValue)
                    val dustWith3digits = String.format("%.3f", row.getCell(found-4).numericCellValue+row.getCell(found-5).numericCellValue)
                    val mudWith3digits = String.format("%.3f", row.getCell(found-3).numericCellValue)
                    print("песок = $sandWith3digits  пыль = $dustWith3digits  ил = $mudWith3digits ")
                    println("end row")
                } else if (cellsPrinted>0) println("end row")
            }
            println("done printing, countedRows = $contedRows")
        }
    }
//метод для получения номера ячейки с текстом "физ.песок"
    private fun getCellNumber(row: Row, formulaEvaluator: FormulaEvaluator): Int {
        var printData = false
        var cellNumber = 0
//        println("cells in row = ${row.lastCellNum}")
        for (cell in row) { //iteration over cell using for each loop
            when (formulaEvaluator.evaluateInCell(cell).cellType) {
                CellType.NUMERIC -> {//getting the value of the cell as a number
                    if (printData) print("" + cell.numericCellValue + "\t")
                }
                CellType.STRING -> {//getting the value of the cell as a string
                    if (cell.stringCellValue.contains("физ.песок")) {
                        cellNumber = row.indexOfLast {
                            it.stringCellValue.equals(cell.stringCellValue)
                        }
                        println("cellnumber =  ${(cellNumber) }")
                        println("cells in row = ${row.lastCellNum}")
                        println("physical cells in row = ${row.physicalNumberOfCells}")
                        cellNumber = row.physicalNumberOfCells
//                        for (i in 1..6){
//                            println("last cell №${row.lastCellNum-i} = ${row.getCell(row.lastCellNum.toInt()-i)}")
//                        }
                        if (cellNumber!=0) return cellNumber
                    }
                    if (printData) print(cell.stringCellValue + "\t")
                }
                else -> {}
            }
        }
        return cellNumber
    }

    fun getRowsAmount(): Int{
        return contedRows-1 //-1 - потому что первый - заголовки
    }
}