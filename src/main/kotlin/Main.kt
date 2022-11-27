import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.system.exitProcess

class FerreTriangelApp : Application() {
    override fun start(primaryStage: Stage) {
        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml"))
//        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("/my/hello-view.fxml"))
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath"))
        val scene = Scene(fxmlLoader.load(), 1200.0, 900.0)
        primaryStage?.title = "FerreTriangle!"
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