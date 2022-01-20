import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

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