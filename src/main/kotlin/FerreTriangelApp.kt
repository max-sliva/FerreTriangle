import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URLDecoder
import java.util.*
import kotlin.system.exitProcess


class FerreTriangelApp : Application() {
    override fun start(primaryStage: Stage) {
//        val locale =  Locale("ru", "RU")
//        val bundle: ResourceBundle = ResourceBundle.getBundle("strings", locale)
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml"), bundle)
        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml"))
//        fxmlLoader.setResources(ResourceBundle.getBundle("bundles.stringsForUI", locale));
//        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("/my/hello-view.fxml"))
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath"))
//        val fxmlPath = "${getCurrentPath()}/mainWindow.fxml"
//        println("path = $fxmlPath")
////        println(HelloApplication::class.java.getResource("/my/hello-view.fxml"))
//        println(URL("file:$fxmlPath"))
//
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
        val scene = Scene(fxmlLoader.load())

//        val scene = Scene(fxmlLoader.load(), 1200.0, 900.0)
        primaryStage?.title = "FerreTriangle 0.8!"
        primaryStage?.scene = scene
//        primaryStage?.initStyle(StageStyle.UNDECORATED)
//        primaryStage?.setMaximized(true)

        primaryStage?.show()
        primaryStage!!.onCloseRequest = EventHandler {
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
    var decodedPath = URLDecoder.decode(path, "UTF-8")
    val last = decodedPath.lastIndexOf("/")
    val newPath = decodedPath.subSequence(0, last)

    return newPath.toString()
}
