import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*

class MainWindow: Initializable {

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
