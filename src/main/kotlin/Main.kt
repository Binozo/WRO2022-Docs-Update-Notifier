fun main(args: Array<String>) {
    val propertyFileHandler = PropertyFileHandler()

    val discordTokenEnv = System.getenv("discordToken")
    if(discordTokenEnv != null) {
        propertyFileHandler.setProperty("discordToken", discordTokenEnv)
    }
    val discordTextChannelIDEnv = System.getenv("discordTextChannelID")
    if(discordTextChannelIDEnv != null) {
        propertyFileHandler.setProperty("discordTextChannelID", discordTextChannelIDEnv)
    }

    val docsUpdateNotifier = DocsUpdateNotifier(propertyFileHandler)
    val checkDelay = 1000 * 60L * 10// every 10 minutes
    docsUpdateNotifier.startListening(checkDelay)
}