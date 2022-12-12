import javafx.collections.FXCollections
import javafx.event.ActionEvent;
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Polygon
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainWindow: Initializable {

    @FXML lateinit var numCol: TableColumn<ObjectForFerre, Int>
    lateinit var placeCol: TableColumn<ObjectForFerre, String>
    lateinit var data1Col: TableColumn<ObjectForFerre, String>
    lateinit var data2Col: TableColumn<ObjectForFerre, String>
    lateinit var sandCol: TableColumn<ObjectForFerre, Double>
    lateinit var dustCol: TableColumn<ObjectForFerre, Double>
    lateinit var mudCol: TableColumn<ObjectForFerre, Double>
    lateinit var resultCol: TableColumn<ObjectForFerre, String>
    @FXML lateinit var tableForFerre: TableView<ObjectForFerre>
    lateinit var mainPane: BorderPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("Started")
    }

    fun openFile(actionEvent: ActionEvent) {
        val fileChooser = FileChooser().apply{
            title = "Open Image File"
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
            tableForFerre.items = FXCollections.observableArrayList(excelWork.getFerreArray())

            tableForFerre.selectionModel.selectedItemProperty().addListener { it->
                val place = tableForFerre.selectionModel.selectedItem.samplePlace
                val sand = tableForFerre.selectionModel.selectedItem.sand
                val dust = tableForFerre.selectionModel.selectedItem.dust
                val mud = tableForFerre.selectionModel.selectedItem.mud
                val result = tableForFerre.selectionModel.selectedItem.result
                println("place = $place  sand = $sand dust = $dust  mud = $mud  sum = ${sand+dust+mud}")

                val ferreStage = Stage()
                val fxmlLoader = FXMLLoader(this.javaClass.getResource("ferreFrame.fxml"))
                val scene = Scene(fxmlLoader.load())
                ferreStage.title = "FerreTriangle for $place!"
                ferreStage.scene = scene
                val ferreClass = fxmlLoader.getController<FerreFrame>()
                val sideLength = ferreClass.triangleSideLength()
                println("sideLength = $sideLength")
                val dotOnFerre = dotXYfromFerreObject(sand, dust, mud, sideLength, ferreClass.polygon)
                ferreStage.show()
                ferreClass.setDot(place, dotOnFerre.x, dotOnFerre.y)
                if (ferreClass.soilName.text!=result) ferreClass.soilName.text = result
                ferreClass.dotDetouchListenter()
            }
        }

    }

    fun openFerreFrame(actionEvent: ActionEvent) {
//        val root = FXMLLoader.load<Parent>(Main.javaClass.getResource("table.fxml"))
        val fxmlLoader = FXMLLoader(this.javaClass.getResource("ferreFrame.fxml"))
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
//    todo сделать сохранение в результатов Excel
    }
}
