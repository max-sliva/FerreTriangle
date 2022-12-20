import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Polygon
import javafx.scene.text.Font
import javafx.scene.text.Text
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt
class Circle1(var number: Int): Circle() {

}


//исходная картинка взята с https://direct.farm/post/opredeleniye-mekhanicheskogo-sostava-pochvy-metodom-ferre-5708
class FerreFrame: Initializable {
    @FXML lateinit var soilName: Label
    lateinit var ferreImageView: ImageView
    lateinit var anchorPane: AnchorPane
//    lateinit var boundTriangle: Polygon
    lateinit var movingDot: Circle
    private var anchorX = 0.0
    private var anchorY = 0.0
    private var mouseOffsetFromNodeZeroX = 0.0
    private var mouseOffsetFromNodeZeroY = 0.0
    lateinit var polygon : Polygon
    var curX = 0.0
    var curY = 0.0
    var oldX = 0.0
    var xAfterStop = 0.0
    var yAfterStop = 0.0
    lateinit var myTriangle: MyPolyline
    var sidePoints = ArrayList<MyPoint>()
    var arrayOflines = ArrayList<Line>()
    var paramsForFerre = DoubleArray(3)
    val mudVal = Text()  //текстовое поле со значением глины для текущей точки
    val dustVal = Text() //текстовое поле со значением пыли для текущей точки
    val sandVal = Text() //текстовое поле со значением песка для текущей точки

    fun mousePressedDot(mouseEvent: MouseEvent) {
        anchorX = mouseEvent.sceneX
        anchorY = mouseEvent.sceneY
        movingDot.cursor = Cursor.MOVE
//        curX = movingDot.layoutX + mouseEvent.sceneX - anchorX
//        oldX = curX
//        println("triangle points = ${ polygon.points}")
    }
    fun moveDot(mouseEvent: MouseEvent) {
        // уравнение левой стороны треугольника: y = -1.73x + 574
        curX = movingDot.layoutX + mouseEvent.sceneX - anchorX
        curY = movingDot.layoutY + mouseEvent.sceneY - anchorY
//        if (curY >= (-1.73*curX+574)){ // если двигаем точку внутри треугольника
        if (myTriangle.isPointInside(MyPoint(curX, curY))) {
            movingDot.translateX = mouseEvent.sceneX - anchorX
            movingDot.translateY = mouseEvent.sceneY - anchorY
//            println("moving dot x = ${mouseEvent.sceneX}")
            xAfterStop = curX
            yAfterStop = curY
            moveLinesTo(curX, curY)
            checkSoilName()
            mudVal.text = String.format("%.1f", paramsForFerre[0])
            dustVal.text = String.format("%.1f", paramsForFerre[1])
            sandVal.text = String.format("%.1f", paramsForFerre[2])
        }
//        else if ((curX == ((curY-574) / (-1.73)).toInt().toDouble())){  //если точка на границе треугольника
        else if (myTriangle.sideWithPoint(MyPoint(curX, curY)).size == 2){
            xAfterStop = mouseEvent.sceneX
//            sidePoints = myTriangle.sideWithPoint(MyPoint(curX, curY))
//            println("on side!!!")
        }
        else if (myTriangle.sideOutOfPoint(MyPoint(curX, curY)).size == 2){  //если курсор выходит за пределы треугольника
            sidePoints = myTriangle.sideOutOfPoint(MyPoint(curX, curY))
             println("side = ${Arrays.toString(sidePoints.toArray())}")
//             println("sidePoints size = ${sidePoints.size}")
//            movingDot.layoutX = (curY-574) / (-1.73)
//            xAfterStop = movingDot.layoutX
//            movingDot.translateX = 0.0
//            movingDot.translateY = mouseEvent.sceneY - anchorY
//            println("xAfterStop = $xAfterStop")
        }
        oldX = curX
    }

    fun mouseReleasedDot(mouseEvent: MouseEvent) {
//        if (curY <(-1.73*curX+574)) { //если остановились за пределами треугольника
        if (myTriangle.sideOutOfPoint(MyPoint(curX, curY)).size == 2) { //если остановились за пределами треугольника
            movingDot.layoutX = xAfterStop
            movingDot.layoutY = yAfterStop
        }
        else {
            movingDot.layoutX = mouseEvent.sceneX - mouseOffsetFromNodeZeroX
            movingDot.layoutY = mouseEvent.sceneY - mouseOffsetFromNodeZeroY
        }
        //clear changes from TranslateX and TranslateY
        movingDot.translateX = 0.0
        movingDot.translateY = 0.0
        movingDot.cursor = Cursor.HAND
    }

    fun mouseEnteredDot(mouseEvent: MouseEvent) {
        movingDot.cursor = Cursor.HAND
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
//        println("pane width = ${anchorPane.prefWidth}  pane height = ${anchorPane.prefHeight}")
        polygon = Polygon()
        val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
//        println("path = $currentPath")
        ferreImageView.image = Image("file:$currentPath/ferre2.png")
//        ferreImageView.image = Image("file:$currentPath/USDA-Soil-Triangle.png")
//        ferreImageView.image = Image("file:$currentPath/ferre3.png")
        val image = ferreImageView.image
//        println("image width = ${image.width}  image height = ${image.height}")
//        println("image width2 = ${image.widthProperty()}  image height2 = ${image.heightProperty()}")
        for (x in 0 until image.width.toInt()) { //в циклах ищем красные точки, чтоб их добавить к полигону
            for (y in 0 until image.height.toInt()) {
                val r = image.pixelReader
                val argb = r.getColor(x, y)
                if (argb == Color.RED) {
                    println("$argb $x  $y")
                    polygon.points.add(x.toDouble())
                    polygon.points.add(y.toDouble())
                }
            }
        }

        polygon.fill = Color.TRANSPARENT
        polygon.stroke = Color.BLUE
        polygon.strokeWidth = 4.0
        anchorPane.children.add(polygon)
        var myPoints = ArrayList<MyPoint>()
//        var j = 0
//        var myPoint: MyPoint
        for (i in 0..4 step 2){
            myPoints.add(MyPoint(polygon.points[i], polygon.points[i+1]))
//            println("added to myPoints ${polygon.points[i]}, ${polygon.points[i+1]}")
        }
        myTriangle = MyPolyline(myPoints.toTypedArray())
//        println("polygon points = ${ polygon.points}")
//        println("myTriangle points = $myTriangle")
        movingDot.layoutY = polygon.points[3]+50
        movingDot.layoutX = polygon.points[2]

        //отрезок к первой стороне
        var xFor0_1 = xForY(movingDot.layoutY, myTriangle.points[0].y, myTriangle.points[1].y,  myTriangle.points[0].x, myTriangle.points[1].x)
        val lineFor0_1 = Line(movingDot.layoutX, movingDot.layoutY, xFor0_1,  movingDot.layoutY)
        lineFor0_1.stroke = Color.ORANGE
        lineFor0_1.strokeWidth = 4.0
        anchorPane.children.add(lineFor0_1)
        arrayOflines.add(lineFor0_1)
        //ко второй стороне
        var xOn1_2 = xForY(movingDot.layoutY, myTriangle.points[1].y, myTriangle.points[2].y, myTriangle.points[1].x, myTriangle.points[2].x)
        //x3=(x2-x1)*cos(60)-(y2-y1)*sin(60)+x1 - формулы нахождения третьей вершины равностороннего треугольника (https://www.programmersforum.ru/showthread.php?t=317832)
        //y3=(x2-x1)*sin(60)+(y2-y1)*cos(60)+y1  в нашем случае  y2-y1 = 0, т.к. у 2-х вершин координаты по Y одинаковые
        val x3=(movingDot.layoutX-xOn1_2)*cos(1.0472) + xOn1_2 //из формул нахождения третьей вершины равностороннего треугольника
        val y3=(movingDot.layoutX-xOn1_2)*kotlin.math.sin(1.0472) + movingDot.layoutY
        val lineFor1_2 = Line(movingDot.layoutX, movingDot.layoutY, x3, y3)
        lineFor1_2.stroke = Color.ORANGE
        lineFor1_2.strokeWidth = 4.0
        anchorPane.children.add(lineFor1_2)
        arrayOflines.add(lineFor1_2)

        //к третьей стороне
//        val xOn1_2 = xForY(movingDot.layoutY, myTriangle.points[1].y, myTriangle.points[2].y, myTriangle.points[1].x, myTriangle.points[2].x)
        val distFromDotToSide2 = xOn1_2-movingDot.layoutX
        val lineFor2_0 = Line(movingDot.layoutX, movingDot.layoutY, myTriangle.points[2].x-distFromDotToSide2,  myTriangle.points[2].y)
        lineFor2_0.stroke = Color.ORANGE
        lineFor2_0.strokeWidth = 4.0
        anchorPane.children.add(lineFor2_0)
        arrayOflines.add(lineFor2_0)
        checkSoilName()
        polygon.toFront()
        movingDot.toFront()
        mudVal.layoutX = xFor0_1 - 30
//        mudVal.layoutXProperty().bind(lineFor0_1.endXProperty()) // xFor0_1
        mudVal.layoutY = movingDot.layoutY
//        mudVal.layoutYProperty().bind(lineFor0_1.endYProperty()) // xFor0_1
        mudVal.text = String.format("%.1f", paramsForFerre[0])
        anchorPane.children.add(mudVal)
        mudVal.fill = Color.ORANGE
        mudVal.style = "-fx-font-weight: bold"
//        mudVal.font = Font.font()

        dustVal.layoutX = lineFor1_2.endX+10
        dustVal.layoutY = lineFor1_2.endY
        dustVal.text = String.format("%.1f", paramsForFerre[1])
        anchorPane.children.add(dustVal)
        dustVal.fill = Color.ORANGE


        sandVal.layoutX = lineFor2_0.endX
        sandVal.layoutY = lineFor2_0.endY+20
        sandVal.text = String.format("%.1f", paramsForFerre[2])
        anchorPane.children.add(sandVal)
        sandVal.fill = Color.ORANGE
    }
//функция для получения координаты x для заданного y по уравнению прямой
    fun xForY(y: Double, y0: Double, y1: Double, x0: Double, x1: Double) = (y - y1 + x1*(y1-y0)/(x1-x0))/((y1-y0)/(x1-x0))

    fun setDot(place: String, x: Double = 0.0, y: Double = 0.0, number: Int, tableForFerre: TableView<ObjectForFerre>? = null) { //для перемещения точки
//        println("place = $place x = $x  y = $y")
        movingDot.layoutX = x
        movingDot.layoutY = y
        curX = x
        curY = y
        moveLinesTo(curX, curY)
        addTextForDot(number, x, y, movingDot.radius, tableForFerre)
        movingDot.onMouseClicked = EventHandler<MouseEvent?> {
//            println("Dot")
            println("Dot = ${it.source}")
        }
    }

    fun addTextForDot(number: Int, x: Double, y: Double, radius: Double, tableForFerre: TableView<ObjectForFerre>?){ //для добавления текста на точку
        val textForDot = Text(if (number >= 10) (x - radius / 2-3) else  x - radius / 2, y + radius / 2, number.toString())
        val font = Font( 9.0)
//        font.set = "-fx-font-color: "
        textForDot.font = font
        textForDot.fill = Color.WHITE
        textForDot.style = "-fx-font-weight: bold"
        anchorPane.children.add(textForDot)
        textForDot.onMouseClicked = EventHandler<MouseEvent?> {
            println("Dot1 text = ${(it.source as Text).text}")
            tableForFerre?.selectionModel?.clearSelection()
            tableForFerre?.selectionModel?.select((it.source as Text).text.toInt()-1)
//            tableForFerre?.setRowFactory { tv ->
//                val row = TableRow<ObjectForFerre>()
////                if (row.index.toString() == (it.source as Text).text) {
//                if (row.index==2) {
//                    println("row = number")
//                    row.style = "-fx-background-color: yellow;"
//                }
//                return@setRowFactory row
//            }
        }
        textForDot.cursor = Cursor.HAND
    }

    private fun checkSoilName() { //для получения почвы для текущей точки
        paramsForFerre = DoubleArray(3) { i ->
            MyLine( //отрезок на соответствующей стороне
                myTriangle.lines[i].point1,
                arrayOflines[i].endPoint()
            ).length() * 100 / myTriangle.lines[i].length() // умн на 100 и делим на длину стороны
        }
        paramsForFerre.forEach { print("${String.format("%.3f", it)} ") }
        println()
        val name = ObjectForFerre.checkForFerreResult(paramsForFerre[0], paramsForFerre[2], paramsForFerre[1])
//        println("soil = ${ObjectForFerre.checkForFerreResult(paramsForFerre[0], paramsForFerre[2], paramsForFerre[1])}")
        if (soilName.text != name) soilName.text = name
//        for (i in 0..2){
//            val s = myTriangle.lines[i].length()
//            val sx = MyLine(myTriangle.lines[i].point1, arrayOflines[i].endPoint()).length()
//            println("side length = $s")
//            val a = (sx*100)/s
//            paramsForFerre.
//        }

    }

    fun Line.endPoint(): MyPoint {
        return MyPoint(this.endX, this.endY)
    }

    private fun moveLinesTo(curX: Double, curY: Double){ //для перемещения линий к сторонам от точки
        for (line in arrayOflines){ //проходим по всем отрезкам от точки к сторонам треугольника
            line.startX = curX
            line.startY = curY
            if (line == arrayOflines[0]) {
                line.endY = curY
                line.endX = xForY(curY, myTriangle.points[0].y, myTriangle.points[1].y,  myTriangle.points[0].x, myTriangle.points[1].x)
                mudVal.layoutX = line.endX - 30
                mudVal.layoutY = line.endY
            }
            if (line == arrayOflines[1]){
                val xOn1_2 = xForY(curY, myTriangle.points[1].y, myTriangle.points[2].y, myTriangle.points[1].x, myTriangle.points[2].x)
                val x3=(curX-xOn1_2)*cos(1.0472) + xOn1_2
                val y3=(curX-xOn1_2)*kotlin.math.sin(1.0472) + curY
                line.endX = x3
                line.endY = y3
                dustVal.layoutX = line.endX+10
                dustVal.layoutY = line.endY
            }
            if (line == arrayOflines[2]){
                val xOn1_2 = xForY(curY, myTriangle.points[1].y, myTriangle.points[2].y, myTriangle.points[1].x, myTriangle.points[2].x)
                val distFromDotToSide2 = xOn1_2-curX
                line.endX = myTriangle.points[2].x - distFromDotToSide2
                sandVal.layoutX = line.endX
                sandVal.layoutY = line.endY+20
            }
        }
    }

    fun dotDetouchListenter() {
        movingDot.onMouseDragged = null
//        movingDot.onMouseClicked = null
        movingDot.onMouseMoved = null
        movingDot.onMouseEntered = null
        movingDot.onMousePressed = null
        movingDot.onMouseReleased  = null
    }

    fun triangleSideLength():Double {
//        println("x1 = ${polygon.points[0]} y1 = ${polygon.points[1]}  x2 = ${polygon.points[2]} y2 = ${polygon.points[3]}")
        val s =  sqrt((polygon.points[0] - polygon.points[2]).pow(2.0) + (polygon.points[1] - polygon.points[3]).pow(2.0))
//        println("s = $s")
        return s
    }

    fun addDot(x: Double, y: Double, number: Int, tableForFerre: TableView<ObjectForFerre>) {
        //todo сделать чтобы появлялась надпись при наведении на точку, а при щелчке выделялась строка с объектом
        println("in addDot")
        var newDot = Circle1(number)
//        BeanUtils.copyProperties(newDot, movingDot)
//        newDot.id = "newDot"
        newDot.radius = movingDot.radius+1
        newDot.stroke = movingDot.stroke
        newDot.fill = movingDot.fill
        newDot.layoutX = x
        newDot.layoutY = y
        anchorPane.children.addAll(newDot)

        addTextForDot(number, x, y, newDot.radius, tableForFerre)
        newDot.onMouseClicked = EventHandler<MouseEvent?> {
            println("Dot1 circle= ${(it.source as Circle1).number}")
        }
        newDot.cursor = Cursor.HAND
    }
//    fun getAnchopane() = return anchorPane
}