import javafx.collections.FXCollections
import javafx.event.ActionEvent;
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Polygon
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.IOException

class MainWindow: Initializable {

    @FXML lateinit var numCol: TableColumn<ObjectForFerre, Int>
    lateinit var placeCol: TableColumn<ObjectForFerre, String>
    lateinit var data1Col: TableColumn<ObjectForFerre, String>
    lateinit var data2Col: TableColumn<ObjectForFerre, String>
    lateinit var sandCol: TableColumn<ObjectForFerre, Double>
    lateinit var dustCol: TableColumn<ObjectForFerre, Double>
    lateinit var mudCol: TableColumn<ObjectForFerre, Double>
    lateinit var resultCol: TableColumn<ObjectForFerre, String>
    lateinit var tableForFerre: TableView<ObjectForFerre>
    lateinit var mainPane: BorderPane
    var title = ""
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("Started")
//        title = (mainPane.scene.window as Stage).title

    }

    fun openFile(actionEvent: ActionEvent) {
        val fileChooser = FileChooser().apply{
            title = "Open Excel File"
            val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
            initialDirectory = File(currentPath)
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"),
                FileChooser.ExtensionFilter("All Files", "*.*")
            )
        }
        val file = fileChooser.showOpenDialog(mainPane.scene.window)
        if (file!=null) {
            println("file = ${file.path}")
            println("file = ${file.name}")
            val stage = mainPane.scene.window as Stage
            if (title == "") title = (mainPane.scene.window as Stage).title
            stage.title = title +" "+ file.name
            println("extension = ${file.extension}")
            val excelWork = ExcelWork(file)
            excelWork.printExcelFileToConsole()
            numCol.cellValueFactory = PropertyValueFactory("num")
            placeCol.cellValueFactory = PropertyValueFactory("samplePlace")
            data1Col.cellValueFactory = PropertyValueFactory("depth")
            data2Col.cellValueFactory = PropertyValueFactory("sampleNumber")
            sandCol.cellValueFactory = PropertyValueFactory("sand")
            dustCol.cellValueFactory = PropertyValueFactory("dust")
            mudCol.cellValueFactory = PropertyValueFactory("mud")
            resultCol.cellValueFactory = PropertyValueFactory("result")
//            tableForFerre.items.removeAll()
            tableForFerre.items = FXCollections.observableArrayList(excelWork.getFerreArray())
            tableForFerre.selectionModel.clearSelection()
//            tableForFerre.onMouseClicked =
//            tableForFerre.selectionModel.selectedItemProperty().addListener { it->
//                if (it!=null){
//                    println("it = $it")
//                    val place = tableForFerre.selectionModel.selectedItem.samplePlace
//                    val sand = tableForFerre.selectionModel.selectedItem.sand
//                    val dust = tableForFerre.selectionModel.selectedItem.dust
//                    val mud = tableForFerre.selectionModel.selectedItem.mud
//                    val result = tableForFerre.selectionModel.selectedItem.result
//                    println("place = $place  sand = $sand dust = $dust  mud = $mud  sum = ${sand + dust + mud}")
//
//                    val ferreStage = Stage()
//                    val fxmlLoader = FXMLLoader(this.javaClass.getResource("ferreFrame.fxml"))
//                    val scene = Scene(fxmlLoader.load())
//                    ferreStage.title = "FerreTriangle for $place!"
//                    ferreStage.scene = scene
//                    val ferreClass = fxmlLoader.getController<FerreFrame>()
//                    val sideLength = ferreClass.triangleSideLength()
//                    println("sideLength = $sideLength")
//                    val dotOnFerre = dotXYfromFerreObject(sand, dust, mud, sideLength, ferreClass.polygon)
//                    ferreStage.show()
//                    ferreClass.setDot(place, dotOnFerre.x, dotOnFerre.y)
//                    if (ferreClass.soilName.text != result) ferreClass.soilName.text = result
//                    ferreClass.dotDetouchListenter()
//                }
//            }
        }

    }

    fun openFerreFrame(actionEvent: ActionEvent) {
//        val root = FXMLLoader.load<Parent>(Main.javaClass.getResource("table.fxml"))
        val fxmlPath = "${getCurrentPath()}/ferreFrame.fxml"
//        println("path = $fxmlPath")
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
//        val scene = Scene(fxmlLoader.load())
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("ferreFrame.fxml")) //для запуска из IDE
        val fxmlLoader = getLoader("ferreFrame.fxml")
        val stage = Stage() //создаем новое окно
        stage.scene = Scene(fxmlLoader.load()) //загружаем в него таблицу
        stage.initModality(Modality.WINDOW_MODAL) //делаем окно модальным
        stage.initOwner(mainPane.scene.window) //и его владельцем делаем главное окно
        stage.show()
    }

    fun dotXYfromFerreObject(sand: Double, dust: Double, mud: Double, sideLength: Double, polygon: Polygon): MyPoint{
        //mud - глина
        val myPoint = MyPoint(0.0,0.0)
        val xt = (mud*sideLength)/100 //значение глины в длине стороны
        val h = xt*sin(1.0472) //1.0472 == 60 градусов
        val c = xt*cos(1.0472)
        val pointA = MyPoint(polygon.points[0]+c, polygon.points[1]-h) //точка на стороне S1
        val xs3 = (sand*sideLength)/100 //значение песка в длине стороны
        val pointB = MyPoint(polygon.points[4]-xs3, polygon.points[5])  //точка на стороне S3

        val dx = polygon.points[4] - pointB.x //длина xs3
        val ds = sideLength - xt //длина маленького треугольника при отсечении от большого отрезком с точкой внутри треугольника

        myPoint.x = pointA.x + (ds-dx)
        myPoint.y = pointA.y
        return myPoint
    }

    fun saveToExcel(actionEvent: ActionEvent) {
        val fileChooser = FileChooser().apply{
            title = "Save Excel File"
            val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
            initialDirectory = File(currentPath)
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                FileChooser.ExtensionFilter("All Files", "*.*")
            )
        }
        val file = fileChooser.showSaveDialog(mainPane.scene.window)
        if (file!=null) {
            val titlesArray = arrayOf("№", "Место", "Глубина", "Номер пробы" ,"Песок", "Пыль", "Глина", "Результат")
            toExcel(titlesArray, tableForFerre.items, file)
        }

    }

    fun toExcel(tableTitles: Array<String>, ferreList: List<ObjectForFerre>, file: File)  {
        var book : Workbook? = null
        book = XSSFWorkbook() //создаем книгу
        val sheet = book.createSheet("FerreData") //создаем лист
        sheet.addMergedRegion( //создадим объединение из 4-х ячеек, чтоб сделать шапку таблицы
            CellRangeAddress( //добавляем в книгу объединенный диапазон ячеек
                0, //начальный ряд диапазона
                0, //конечный ряд диапазона
                0, //начальная колонка диапазона
                7 //конечная колонка диапазона
            )
        )
        val row: Row = sheet.createRow(0) //создаем новый ряд
        val cell: Cell = row.createCell(0) //создаем ячейку
        cell.setCellValue("!! Информация о пробах почвы !!") //задаем текст ячейки
        val cellStyle = book.createCellStyle() as XSSFCellStyle // стиль для ряда
        cellStyle.alignment = HorizontalAlignment.CENTER //задаем выравнивание по центру
        val font: Font = book.createFont() //создаем шрифт для объединенных ячеек
        font.setFontHeightInPoints(14.toShort()) //задаем размер шрифта
        font.setColor(HSSFColor.HSSFColorPredefined.RED.index) //задаем цвет шрифта
        cellStyle.setFont(font) //добавляем шрифт к стилю
        cell.cellStyle = cellStyle //устанавливаем стиль на ячейку
        headCreate(book, sheet, tableTitles) //вызываем метод для заголовка таблицы (описан ниже)
        dataToSheet(book, sheet, ferreList) //вызываем метод для заполнения данных таблицы (описан ниже)
        for (i in tableTitles.indices) { //цикл для установки ширины ячеек по содержимому
            sheet.autoSizeColumn(i)
        }
        try { // Записываем всё в файл
            book.write(FileOutputStream(file)) //пишем книгу в файл
            book.close() //закрываем книгу
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    //метод для создания заголовка таблицы
    private fun headCreate(book: Workbook, sheet: Sheet, tableTitles: Array<String>) {
// Нумерация рядов начинается с нуля
        val row: Row = sheet.createRow(1) //создаем новый ряд
        val cellStyle = book.createCellStyle() as XSSFCellStyle //создаем стиль для ряда
        cellStyle.alignment = HorizontalAlignment.CENTER //задаем выравнивание по центру
        cellStyle.borderBottom = BorderStyle.THICK //задаем нижнюю границу
        cellStyle.borderLeft = BorderStyle.THICK //и все остальные
        cellStyle.borderRight = BorderStyle.THICK
        cellStyle.borderTop = BorderStyle.THICK
        val font: Font = book.createFont() //создаем объект для параметров шрифта
        font.setBold(true) //делаем шрифт жирным
        cellStyle.setFont(font) //устанавливаем шрифт в стиль
        for (i in tableTitles.indices) { //цикл по заголовкам
            val temp: Cell = row.createCell(i) //создаем ячейку
            temp.cellStyle = cellStyle //задаем ей стиль
            temp.setCellValue(tableTitles[i]) //задаем ей значение
        }
    }
    //метод для заполнения данных таблицы
    private fun dataToSheet(book: Workbook, sheet: Sheet, ferreList: List<ObjectForFerre>): Int {
        var i = 1 //счетчик кол-ва рядов
        val cellStyle = book.createCellStyle() as XSSFCellStyle //создаем стиль для ряда
        cellStyle.borderBottom = BorderStyle.THIN //задаем нижнюю границу
        cellStyle.borderLeft = BorderStyle.THIN //и все остальные
        cellStyle.borderRight = BorderStyle.THIN
        cellStyle.borderTop = BorderStyle.THIN
        for (ferreObj in ferreList) { //цикл по списку полей
            i++ //счетчик добавленных строк - рядов
            val row: Row = sheet.createRow(i) //создаем новый ряд
            var temp: Cell = row.createCell(0) //задаем первую ячейку
            temp.setCellValue(ferreObj.num.toString()) //вставляем туда ФИО
            temp.cellStyle = cellStyle //устанавливаем стиль созданной ячейки
            temp = row.createCell(1) //задаем вторую ячейку
            temp.setCellValue(ferreObj.samplePlace) //вставляем туда логин
            temp.cellStyle = cellStyle
            temp = row.createCell(2) //задаем третью ячейку
            temp.setCellValue(ferreObj.depth) //вставляем туда статус пользователя
            temp.cellStyle = cellStyle
            temp = row.createCell(3) //задаем третью ячейку
            temp.setCellValue(ferreObj.sampleNumber) //вставляем туда статус пользователя
            temp.cellStyle = cellStyle
            temp = row.createCell(4) //задаем третью ячейку
            temp.setCellValue(ferreObj.sand) //вставляем туда статус пользователя
            temp.cellStyle = cellStyle
            temp = row.createCell(5) //задаем третью ячейку
            temp.setCellValue(ferreObj.dust) //вставляем туда статус пользователя
            temp.cellStyle = cellStyle
            temp = row.createCell(6) //задаем третью ячейку
            temp.setCellValue(ferreObj.mud) //вставляем туда статус пользователя
            temp.cellStyle = cellStyle
            temp = row.createCell(7) //задаем третью ячейку
            val font: Font = book.createFont() //создаем шрифт для объединенных ячеек
            font.setFontHeightInPoints(12.toShort()) //задаем размер шрифта
            font.setColor(HSSFColor.HSSFColorPredefined.GREEN.index) //задаем цвет шрифта
            temp.setCellValue(ferreObj.result) //вставляем туда статус пользователя
            var cellStyle1 = book.createCellStyle() as XSSFCellStyle
            cellStyle1.cloneStyleFrom(cellStyle)
            cellStyle1.setFont(font) //добавляем шрифт к стилю
//            cell.cellStyle = cellStyle //устанавливаем стиль на ячейку
            temp.cellStyle = cellStyle1
        }
        return i //можно потом использовать, чтобы узнать реальное кол-во вставленных строк
    }

    fun tableClick(mouseEvent: MouseEvent) {
        val place = tableForFerre.selectionModel.selectedItem.samplePlace
        val sand = tableForFerre.selectionModel.selectedItem.sand
        val dust = tableForFerre.selectionModel.selectedItem.dust
        val mud = tableForFerre.selectionModel.selectedItem.mud
        val result = tableForFerre.selectionModel.selectedItem.result
        println("place = $place  sand = $sand dust = $dust  mud = $mud  sum = ${sand + dust + mud}")
//        val fxmlPath = "${getCurrentPath()}/ferreFrame.fxml"//для jar-файла
//        println("path = $fxmlPath")
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("ferreFrame.fxml")) //для запуска из IDE
        val fxmlLoader = getLoader("ferreFrame.fxml")

        val ferreStage = Stage()
        val scene = Scene(fxmlLoader.load())
        ferreStage.title = "FerreTriangle for $place!"
        ferreStage.scene = scene
        val ferreClass = fxmlLoader.getController<FerreFrame>()
        val sideLength = ferreClass.triangleSideLength()
        println("sideLength = $sideLength")
        val dotOnFerre = dotXYfromFerreObject(sand, dust, mud, sideLength, ferreClass.polygon)
        ferreStage.show()
        ferreClass.setDot(place, dotOnFerre.x, dotOnFerre.y)
        ferreClass.mudVal.text = String.format("%.1f", mud)
        ferreClass.dustVal.text = String.format("%.1f", dust)
        ferreClass.sandVal.text = String.format("%.1f", sand)

        if (ferreClass.soilName.text != result) ferreClass.soilName.text = result
        ferreClass.dotDetouchListenter()
    }
}
