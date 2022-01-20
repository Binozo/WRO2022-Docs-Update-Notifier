import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class PropertyFileHandler {

    private val propertyFile = File("config.properties")
    val aufgabenStellungProperty = "aufgabenStellungVersion"
    val bewertungsbogenProperty = "bewertungsbogenVersion"
    val rulesProperty = "rulesVersion"

    init{
        //Check if property file exists
        if(!propertyFile.exists()){
            println("Property file not found. Creating new one.")
            propertyFile.createNewFile()
        }
    }

    // returns null if property not found
    fun getProperty(key: String): String? {
        val prop = Properties()
        FileInputStream(propertyFile).use { prop.load(it) }

        return prop.getProperty(key)
    }

    fun setProperty(key: String, value: String){
        val prop = Properties()
        FileInputStream(propertyFile).use { prop.load(it) }

        prop.setProperty(key, value)

        val out: OutputStream = FileOutputStream(propertyFile)
        prop.store(out, null)
    }
}