# warpi-challenge

This is a small Spring Boot based web service that fetches Google Finance historical data and returns it in a slightly different format.

## Build instructions
This is a standard gradle project.

You will need Lombok plugin for your IDE to properly build it there.

## API

GET **:8080/historical/v1/cids/**

Fetches the list of CIDs that can be queried using this service.

Returns a JSON array of integers.

GET **:8080/historical/v1/cids/{cid}/month-end-closing-prices**

Fetches the closing price of the given cid on every month-end since 1990.

Returns a JSON array of: {"date":"MM-dd-YYYY","close":0.00}

The array is always in reverse chronological order, and the last month is ignored.

