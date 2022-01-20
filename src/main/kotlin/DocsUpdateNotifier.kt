
import kotlinx.coroutines.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.system.exitProcess

class DocsUpdateNotifier {
    private val httpClient = OkHttpClient()
    private val wroPageUrl = "https://www.worldrobotolympiad.de/saison-2022/robomission-senior"
    private val propertyFileHandler = PropertyFileHandler()
    private lateinit var jda: JDA
    private val wroPageParser = WROPageParser()
    private lateinit var textChannelID: String

    init{
        //read discord token
        val discordToken = propertyFileHandler.getProperty("discordToken")
        if(discordToken == null){
            println("No \"discordToken\" found in property file")
            exitProcess(-1)
        }
        jda = JDABuilder
            .createDefault(discordToken)
            .setActivity(Activity.playing("WRO 2022")) // optional
            .build().awaitReady()

        //read text channel id
        val textChannelID = propertyFileHandler.getProperty("discordTextChannelID")
        if(textChannelID == null){
            println("No \"discordTextChannelID\" found in property file")
            exitProcess(-1)
        }
        //check if text channel exists
        val textChannel = jda.getTextChannelById(textChannelID)
        if(textChannel == null){
            println("TextChannel with id $textChannelID not found")
            exitProcess(-1)
        }
    }

    public fun startListening(checkDelayInMs: Long){
        val job: Job = startRepeatingJob(checkDelayInMs)
    }

    private fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (NonCancellable.isActive) {
                checkIfNewDocsAvailable()
                delay(timeInterval)
            }
        }
    }

    private fun checkIfNewDocsAvailable(){
        val response = httpClient.newCall(Request.Builder().url(wroPageUrl).build()).execute()
        val responseBody = response.body?.string()
        if(responseBody != null){
            //parse response
            val doc = wroPageParser.parseBody(responseBody)
            val aufgabenstellungVersion: String? = wroPageParser.getAufgabenstellungVersion(doc)
            val bewertungsbogenVersion: String? = wroPageParser.getBewertungsbogenVersion(doc)
            val rulesVersion: String? = wroPageParser.getRulesVersion(doc)
            if(aufgabenstellungVersion == null)
                println("Could not parse aufgabenstellungVersion")
            if(bewertungsbogenVersion == null)
                println("Could not parse bewertungsbogenVersion")
            if(rulesVersion == null)
                println("Could not parse rulesVersion")

            //check if versions are different
            val oldAufgabenStellungVersion = propertyFileHandler.getProperty(propertyFileHandler.aufgabenStellungProperty)
            val oldBewertungsbogenVersion = propertyFileHandler.getProperty(propertyFileHandler.bewertungsbogenProperty)
            val oldRulesVersion = propertyFileHandler.getProperty(propertyFileHandler.rulesProperty)

            if(oldAufgabenStellungVersion == null){
                println(propertyFileHandler.aufgabenStellungProperty + " not found in property file. Setting to " + aufgabenstellungVersion)
                propertyFileHandler.setProperty(propertyFileHandler.aufgabenStellungProperty, aufgabenstellungVersion!!)
            }else{
                if(oldAufgabenStellungVersion != aufgabenstellungVersion){
                    println("New AufgabenstellungVersion found: $aufgabenstellungVersion")
                    propertyFileHandler.setProperty(propertyFileHandler.aufgabenStellungProperty, aufgabenstellungVersion!!)
                    sendDiscordMessage("Aufgabenstellung has been updated! Newest Version: $aufgabenstellungVersion \n$wroPageUrl")
                }else{
                    println("No new AufgabenstellungVersion found")
                }
            }

            if(oldBewertungsbogenVersion == null){
                println(propertyFileHandler.bewertungsbogenProperty + " not found in property file. Setting to " + bewertungsbogenVersion)
                propertyFileHandler.setProperty(propertyFileHandler.bewertungsbogenProperty, bewertungsbogenVersion!!)
            }else{
                if(oldBewertungsbogenVersion != bewertungsbogenVersion){
                    println("New Bewertungsbogen found: $bewertungsbogenVersion")
                    propertyFileHandler.setProperty(propertyFileHandler.bewertungsbogenProperty, bewertungsbogenVersion!!)
                    sendDiscordMessage("Bewertungsbogen has been updated! Newest Version: $bewertungsbogenVersion \n$wroPageUrl")
                }else{
                    println("No new Bewertungsbogen found")
                }
            }

            if(oldRulesVersion == null){
                println(propertyFileHandler.rulesProperty + " not found in property file. Setting to " + rulesVersion)
                propertyFileHandler.setProperty(propertyFileHandler.rulesProperty, rulesVersion!!)
            }else{
                if(oldRulesVersion != rulesVersion){
                    println("New Rules found: $rulesVersion")
                    propertyFileHandler.setProperty(propertyFileHandler.rulesProperty, rulesVersion!!)
                    sendDiscordMessage("Rules has been updated! Newest Version: $rulesVersion \n$wroPageUrl")
                }else{
                    println("No new Rules found")
                }
            }


        }else{
            println("Connection to WRO page failed: ${response.code}")
        }
    }

    private fun sendDiscordMessage(message: String){
        val textChannel = jda.getTextChannelById(textChannelID)
        if(textChannel != null){
            textChannel.sendMessage(message).queue()
        }else{
            println("TextChannel $textChannelID not found")
        }
    }
}