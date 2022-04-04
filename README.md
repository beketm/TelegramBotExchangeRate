
# Exchange Rates Telegram Bot in Java

This is a telegram bot that displays foreign currency exchange rates for the past 10 days. Current supported currencies are European Euro, US Dollar, Russian Ruble. Kazakstani Tenge was used as a base currency. 

## Authors

- [@beketm](https://github.com/beketm)


## Demo

![Alt Text](mp4.gif)
## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`X_RAPIDAPI_HOST`

`X_RAPIDAPI_KEY`

You can get these variables from [RapidAPI website](https://rapidapi.com/fixer/api/fixer-currency/)

`BOT_TOKEN`-create a new Telegram Bot in [@BotFather](https://telegram.me/BotFather) and get TOKEN

`BOT_USERNAME` - name of the Telegram bot


## Run Locally

Clone the project

```bash
  git clone https://github.com/beketm/TelegramBotExchangeRate
```

Go to the project directory

```bash
  cd TelegramBotExchangeRate/
```

Install dependencies

```bash
  mvn clean install
```

Start the server

```bash
  mvn exec:java -Dexec.mainClass=ExchangeRatesTelegramBot
```

