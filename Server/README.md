# Info
Server jest deployowany na RespberryPi pod [tym adresem](http://5.104.252.32:8000).<br/>
Domumentację można znaleźć [tutaj](http://5.104.252.32:8000/docs).


## Inicjalizacja
```
source env/bin/activate
pip install -r requirements.txt
```
## Stworzenie bazy danych
```
$ python database.py
```
## Uruchomienie
```
$ python3 -m uvicorn main:app --reload
```

## End-Pointy
Po uruchomieniu servera nalezy wejść w url: *http://127.0.0.1:8000/docs#/*

## Wysyłanie zapytań
* Postać requestów można sprawdzić w [docs](http://127.0.0.1:8000/docs#/)
* Testując na [docs](*http://127.0.0.1:8000/docs#/*), próbując wysłać zapytanie do zabezpieczonego endpointa, trzeba się wcześniej zalogować (`username1`, `password1`)
* Testując na [postmanie](https://www.postman.com/), lub wysyłając zapytania już z aplikacji, trzeba dołączyć do nagłówka `token`, otrzymany podczas logowania / rejestracji
    * `Authorization` ->
    * type: `OAuth 2.0` ->
    * header prefix: `Bearer` ->
    * Access Token: `token`
![](../README_imgs/postman.png)

## Podgląd
Bazę danych można wygodnie podejrzeć [tutaj](https://inloop.github.io/sqlite-viewer/).
