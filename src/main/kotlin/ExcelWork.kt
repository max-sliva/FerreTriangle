import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class ExcelWork(val file: File) {
    fun printExcelFileToConsole(){
        if (file.extension=="xlsx") {
            val file: FileInputStream = FileInputStream(file)
            val workbook: Workbook = XSSFWorkbook(file)
        }
        if (file.extension=="xls") {
            val fis = FileInputStream(file)
            val wb = HSSFWorkbook(fis) //creating workbook instance that refers to .xls file
            val sheet = wb.getSheetAt(0) //creating a Sheet object to retrieve the object
            val formulaEvaluator: FormulaEvaluator = wb.creationHelper.createFormulaEvaluator()
            for (row in sheet)  {//iteration over row using for each loop
                for (cell in row) { //iteration over cell using for each loop
                    when (formulaEvaluator.evaluateInCell(cell).cellType) {
                        CellType.NUMERIC-> //getting the value of the cell as a number
                            print(""+cell.numericCellValue + "\t\t")
                        CellType.STRING -> //getting the value of the cell as a string
                            print(cell.stringCellValue + "\t\t")
                        else -> {}
                    }
                }
                println()
            }
        }
    }

    fun getStringsAmount(): Int{
        var stringsNumber = 0
        return stringsNumber
    }
}