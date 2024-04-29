import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.math.BigInteger
import java.security.MessageDigest

class WROPageParser {
    private val wroContentClass = "wro-content"

    public fun parseBody(body: String): Document {
        return Jsoup.parse(body)
    }

    public fun getAufgabenstellungVersion(doc: Document): String? {
        val content = getContentList(doc)
        val docLinkElement = content?.getElementsByTag("li")?.get(0)

        val versionRaw = docLinkElement?.text() // this looks like this: "Aufgabenstellung (Version 15.01.2022)" but we only need "15.01.2022"
        return versionRaw?.let { parseVersion(it) } // result: 15.01.2022
    }

    public fun getBewertungsbogenVersion(doc: Document): String? {
        val content = getContentList(doc)
        val docLinkElement = content?.getElementsByTag("li")?.get(1)

        val versionRaw = docLinkElement?.text() // this looks like this: "Aufgabenstellung (Version 15.01.2022)" but we only need "15.01.2022"
        return versionRaw?.let { parseVersion(it) } // result: 15.01.2022
    }

    public fun getRulesVersion(doc: Document): String? {
        val content = getContentList(doc)
        val docLinkElement = content?.getElementsByTag("li")?.get(3)

        val versionRaw = docLinkElement?.text() // this looks like this: "Aufgabenstellung (Version 15.01.2022)" but we only need "15.01.2022"
        return versionRaw?.let { parseVersion(it) } // result: 15.01.2022
    }

    public fun getTeamsNames(doc: Document): List<String> {
        val tableElements = doc.getElementsByClass("wro-result-table")
        val tableEntries = tableElements.map { it.getElementsByTag("tr")} // get raw table entries
        val teamNames = mutableListOf<String>()

        tableEntries.forEach {
            it.forEach {
                if(!it.hasClass("titelzeile")) {
                    teamNames.add(it.getElementsByTag("td").get(1).text())
                }

            }
        }

        return teamNames
    }

    public fun getTeamsNamesHash(teams: List<String>): String {
        return md5Hash(teams.joinToString("-"))
    }

    public fun getQuestionsHash(doc: Document): String {
        //search for divs with card-header class
        val cardElements = doc.getElementsByClass("card-header")
        //search for header text
        val cardHeaderElements = cardElements.map { it.text() }
        //search for text in cards
        val cardTextElements = cardElements.map { it.nextElementSibling()?.text() }

        //generate md5 hash from cardHeaderElements and cardTextElements
        val md5 = md5Hash(cardHeaderElements.joinToString("") + cardTextElements.joinToString("")) //convert cardHeaderElements and cardTextElements to string
        return md5
    }

    fun md5Hash(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(str.toByteArray(Charsets.UTF_8)))
        return String.format("%032x", bigInt)
    }

    private fun getContentList(doc: Document): Element? {
        //searching for right div
        val wroContent = doc.getElementsByClass(wroContentClass)[0]
        //searching for list with documents
        return wroContent.getElementsByTag("ul")[0]
    }

    private fun parseVersion(versionRaw: String): String {
        var rawVersion = versionRaw.split("(")[1].split(")")[0]
        return rawVersion.split("Version ")[1]
    }
}