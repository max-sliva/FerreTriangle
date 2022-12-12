data class ObjectForFerre(val num: Int, val samplePlace: String, val depth: String, val sampleNumber: String, val sand: Double, val dust: Double, val mud: Double, val result: String ){
       companion object {
        fun checkForFerreResult(mud: Double, sand: Double, dust: Double): String {
            var result = ""
            if (sand > 85 && (dust + 1.5*mud)<15) {
                result = "Песок"
            }
            else if (sand in 70.0..91.0 && (dust + 1.5*mud)>15 && (dust + 2*mud)<30){
                result = "Суглинистый песок"
            }
            else if (((mud.toInt() in (7..20)) && (sand > 52) && ((dust + 2*mud)>=30))
                || ((mud<7) && (dust<50) && (sand>43))
            ) {
                result = "Опесчаненный Суглинок"
            }
            else if ((mud.toInt() in (7..27)) and (sand<=52) and (dust.toInt() in 28..50)) {
                result = "Суглинок"
            }
            else if (mud<12 && dust>=80) {
                result = "Тонкий суглинок"
            }
            else if (mud.toInt() in 20..35 && dust<28 && sand > 45){
                result = "Опесчаненный глинистый суглинок"
            }
            else if (((mud.toInt() in (12..27)) and (sand < 50)) or (mud < 12 && (dust in 50.0..80.0))) {
                result = "Пылеватый суглинок"
            }
            else if (mud.toInt() in 27..40 && sand.toInt() in 20..46){
                result = "Глинистый суглинок"
            }
            else if (mud.toInt() in 27..40 && sand<=20){
                result = "Пылевато-глинистый суглинок"
            }
            else if (mud>=35 && sand>=45) {
                result = "Глина песчаная"
            }
            else if (mud>=40 && dust>=40) {
                result = "Пылеватая глина"
            }
            else if (mud>=40 && sand<=45 && dust<40) {
                result = "Глина"
            }
            return  result
        }
    }
}

