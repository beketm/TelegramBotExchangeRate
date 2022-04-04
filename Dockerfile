FROM maven:3.6.3-jdk-11 as maven
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn dependency:go-offline -B
RUN mvn package


FROM openjdk:11
WORKDIR /TelegramBotExchangeRate
COPY --from=maven target/TelegramBotExchangeRate-1.0-SNAPSHOT.jar ./ExchangeRatesTelegramBot.jar
CMD ["$", "exec:java", "-Dexec.mainClass=ExchangeRatesTelegramBot"]
#CMD ["java", "-cp", "./ExchangeRatesTelegramBot.jar", "ExchangeRatesTelegramBot"]

