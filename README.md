# WRO 2022 Document Update Notifier
A simple Discord Bot which notifies you if a version change of documents or FAQ has been detected.
Only tested on https://www.worldrobotolympiad.de/saison-2022/robomission-senior, https://www.worldrobotolympiad.de/saison-2022/faq and https://www.worldrobotolympiad.de/saison-2023/faq

## Installation
### jar
- Just run the jar provided [here](https://github.com/Binozo/WRO2022-Docs-Update-Notifier/raw/master/out/artifacts/WRO2022DocsUpdateNotifier_jar/WRO2022_docs_update_notifier.jar).

### docker-compose

````yaml
version: "3.9"
services:
  wro-update-notifier:
    restart: always
    image: "ghcr.io/binozo/wro2022-docs-update-notifier:latest"
    environment:
      discordToken: xxx
      discordTextChannelID: xxx
````

## Configuration

### With File
- Create a file named `config.properties` in the same directory as the jar.
- Add the following lines:
1. `discordToken=xxx` (replace `xxx` with your Discord Bot Token)
2. `discordTextChannelID=xxx` (replace `xxx` with your preferred Discord Text Channel which is accessible through your Discord Bot)

### With environment variables
1. Set `discordToken=xxx` and `discordTextChannelID=xxx` as environment variables (replace `xxx`)
2. The `config.properties` file will be created automatically