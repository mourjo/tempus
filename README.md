# tempus

Goal: Given a city and country, find its timezone details!

![tempus screenshare](docs/tempus.gif)

## Installation

This requires Java 11 and Maven 3.8.6 to run.

Tested with Java version:

```
java -version
openjdk version "11.0.16" 2022-07-19 LTS
OpenJDK Runtime Environment Corretto-11.0.16.8.1 (build 11.0.16+8-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.16.8.1 (build 11.0.16+8-LTS, mixed mode)
```

To run the server in development mode:

```
API_KEY=XXX PORT=9111 ./gradlew run 
```

To compile it into a jar and run:

``` 
API_KEY=XXX PORT=9111 ./gradlew build 
API_KEY=XXX PORT=9111 java -jar build/libs/tempus-1.0-SNAPSHOT-all.jar
```

Tests also need a valid API key but the port can be any available port:
``` 
API_KEY=XXX PORT=0 ./gradlew test
```

## How city names are converted to locations

The API key for `api.timezonedb.com` provided no longer has premium access. Hence, locations may not exactly match.
There is a [translation layer](src/main/java/me/mourjo/tempus/models/LocationTranslator.java) that converts city names
to latitude and longitude and then calls `api.timezonedb.com`.

The translation layer is a CSV file that contains city/country names and their location (latitude/longitude). The data
is obtained from [SimpleMaps](https://simplemaps.com/data/world-cities).

## Alternative locations

Because of the above, some expectations do not exactly match.

| Expectation                                     | Reality                                               |
|-------------------------------------------------|-------------------------------------------------------|
| Isengaard should return nothing                 | Matches                                               |
| Yosemite Valley should match for USA            | Does not match exactly but matches for Yosemite Lakes |
| *Athens* should return about 15 results for USA | Matches                                               |

## Slow response for wildcards

Since we are not using the premium API, requests to `api.timezonedb.com` are rate limited by 1 request per second.
Hence, for wild cards, the response can take up to 15 seconds.

## Slow tests

There are tests written that make API calls to `api.timezonedb.com`, and one of them is a wildcard test. Tests should
finish under 2 mins.

## Country dropdown

Since we have a list of all known locations, the dropdown list makes an API query to fetch all available countries. 
