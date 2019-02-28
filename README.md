# Landoop Exercise

## Running

Call
```
sbt run
```
to init the http server.

## Using

Example usages with curl

Good request

```
$> curl -i -d '{"fromCurrency":"EUR", "toCurrency":"PLN", "amount": 10.0}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/convert
HTTP/1.1 200 OK
Content-Type: application/json
Date: Wed, 20 Feb 2019 23:25:24 GMT
Content-Length: 46

{"exchange":2.0,"amount":20.0,"original":10.0}%
```

Bad request

```
➜  landoopex git:(master) ✗ curl -i -d '{"fromCurrency":"EUR", "toCurrency":"PLN", "amount": -10.0}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/convert
HTTP/1.1 400 Bad Request
Content-Type: text/plain; charset=UTF-8
Date: Wed, 20 Feb 2019 23:25:28 GMT
Content-Length: 48

Provided amount -10.0 is insufficient to convert%
```

## Potential improvements

1. Cache should be invalidated
2. Cache should potentially hold only (X, Y)->Rate, not (X, Y)->Rate & (Y, x)->Rate


~~don't express as money as Double, that's just asking for trouble~~
~~Exchange has hardcoded URI to the exchange, should be provided as configuration (`Kleisli`)~~
~~What if exchange is dead? We could cache results to potentially stay resilient with cached data (`MonadState`)~~
