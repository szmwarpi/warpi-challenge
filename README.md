# warpi-challenge

This is a small Spring Boot based web service that fetches Google Finance historical data and returns it in a slightly different format.

## Build instructions
This is a standard gradle project.

## API

GET **:8080/historical/v1/cids/{cid}/month-end-closing-prices**

Fetches the closing price of the given cid on every month-end since 1990.

Returns a JSON array of: {"date":"MM-dd-YYYY","close":0.00}
