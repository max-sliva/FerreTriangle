import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URLDecoder
import kotlin.system.exitProcess


class FerreTriangelApp : Application() {
    override fun start(primaryStage: Stage) {
//        val locale =  Locale("ru", "RU")
//        val bundle: ResourceBundle = ResourceBundle.getBundle("strings", locale)
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml"), bundle)

//        fxmlLoader.setResources(ResourceBundle.getBundle("bundles.stringsForUI", locale));
//        val fxmlPath = "${getCurrentPath()}/mainWindow.fxml"
//        println("path = $fxmlPath")
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
        val fxmlLoader = getLoader("mainWindow.fxml")
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml")) //для запуска из IDE
        primaryStage.title = "FerreTriangle 0.9!"
        val scene = Scene(fxmlLoader.load())
        primaryStage.scene = scene

//        val scene = Scene(fxmlLoader.load(), 1200.0, 900.0)
//        primaryStage?.initStyle(StageStyle.UNDECORATED)
//        primaryStage?.setMaximized(true)

        primaryStage.show()
        primaryStage.onCloseRequest = EventHandler {
            Platform.exit()
            exitProcess(0)
        }
    }

    companion object { //специальный объект для запуска проекта в рамках фреймворка JavaFX
        @JvmStatic // его всегда оставляем одинаковым для всех проектов
        fun main(args: Array<String>) {
            launch(FerreTriangelApp::class.java) // Main – имя запускного класса
        }
    }
}

fun getCurrentPath(): String {
    val path: String = FerreTriangelApp::class.java.getProtectionDomain().getCodeSource().getLocation().getPath()
    val decodedPath = URLDecoder.decode(path, "UTF-8")
    val last = decodedPath.lastIndexOf("/")
    val newPath = decodedPath.subSequence(0, last)

    return newPath.toString()
}

fun getLoader(fxmlName: String): FXMLLoader {
    val fxmlPath = "${getCurrentPath()}/$fxmlName"
//    val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
    val fxmlLoader = FXMLLoader(FerreTriangelApp::class.java.getResource(fxmlName)) //для запуска из IDE
    return fxmlLoader
}
