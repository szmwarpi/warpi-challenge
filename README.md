# warpi-challenge

This is a small Spring Boot based RESTful service that fetches Google Finance historical data and returns it in a slightly different format.

## Build instructions
This is a standard gradle project.

You will need Lombok plugin for your IDE to properly build it there.

## API

#### GET :8080/historical/v1/cids/

Fetches the list of CIDs that can be queried using this service.

Returns a JSON array of integers.

#### GET :8080/historical/v1/cids/{cid}/month-end-closing-prices

Fetches the closing price of the given CID on month-ends (the last day of the month for which we have data coming in).

The date range is basically whatever the Google API returns, we request it since 1990 and ignore the latest month.

Returns a JSON array of: {"date":"MM-dd-YYYY","close":0.00}

The result array is in reverse chronological order.