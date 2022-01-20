fun main(args: Array<String>) {
    val docsUpdateNotifier = DocsUpdateNotifier()
    val checkDelay = 1000 * 60L * 10// every 10 minutes
    docsUpdateNotifier.startListening(checkDelay)
}