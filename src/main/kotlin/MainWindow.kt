import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent;
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*

class MainWindow: Initializable {

    @FXML lateinit var numCol: TableColumn<ObjectForFerre, Int>
    lateinit var placeCol: TableColumn<ObjectForFerre, String>
    lateinit var data1Col: TableColumn<ObjectForFerre, String>
    lateinit var data2Col: TableColumn<ObjectForFerre, String>
    lateinit var sandCol: TableColumn<ObjectForFerre, Double>
    lateinit var dustCol: TableColumn<ObjectForFerre, Double>
    lateinit var mudCol: TableColumn<ObjectForFerre, Double>
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
            data1Col.cellValueFactory = PropertyValueFactory("sampleData1")
            data2Col.cellValueFactory = PropertyValueFactory("sampleData2")
            sandCol.cellValueFactory = PropertyValueFactory("sand")
            dustCol.cellValueFactory = PropertyValueFactory("dust")
            mudCol.cellValueFactory = PropertyValueFactory("mud")
            tableForFerre.items = FXCollections.observableArrayList(excelWork.getFerreArray())
            //todo сделать показ треугольника ферре с выбранной строкой
        }

    }

    fun openFerreFrame(actionEvent: ActionEvent) {
//        val root = FXMLLoader.load<Parent>(Main.javaClass.getResource("table.fxml"))
        val fxmlLoader = FXMLLoader(this.javaClass.getResource("FerreFrame.fxml"))
        val stage = Stage() //создаем новое окно
        stage.scene = Scene(fxmlLoader.load()) //загружаем в него таблицу
        stage.initModality(Modality.WINDOW_MODAL) //делаем окно модальным
        stage.initOwner(mainPane.scene.window) //и его владельцем делаем главное окно
        stage.show()
    }
}
