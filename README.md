# Droply Backend

Backend repository of the Droply project

## Stack

The following technologies are used for development:

Kotlin (main programming language)
Spring (&Boot) IoC, Data
Ktor (WebSocket transport)

## Launch

The following command can be performed in order to roll out the backend with 2 replicas
(as stated in docker-compose.yml: main, secondary):

```shell
docker-compose up
```

After executing the above command, you will be able to connect to both instances of the application via WebSocket using
the following addresses:

- localhost:8081
- localhost:8082

Recommendation:
use [WebSocket Test Client](https://chrome.google.com/webstore/detail/websocket-test-client/fgponpodhbmadfljofbimhhlengambbn)