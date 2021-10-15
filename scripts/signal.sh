curl POST http://localhost:8098/signals/simple -v -H 'Content-Type: application/json' \
  -H 'X-AUTH-TOKEN: zzzzzz' \
  -d '
    {
        "quoteAsset": "BTC",
         "baseAsset": "ETH"
    }
' /
