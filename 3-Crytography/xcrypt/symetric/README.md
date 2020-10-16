# xcrypt - aplikácie na šifrovanie súborov

Aplikácia umožní šifrovat a dešifrovat súbory symetrickým klucom.

## Third part API
Aplikacia je postavena na sifrovacom python baliku `Cryptodome`. Ktory je nastavbou nad 
osvedcenym `PyCrypto` packagom. Obsahuje bezpecne sifrovacie funkcie a implmentacie generatorov
nahodnych klucov.

## Opis aplikacie
Aplikacia je implmentovana v jazyku python. Na sifrovanie suborov som vyuzil 
Advanced Encryption Standard (AES) konkretne mod MODE_CBC (Cipher Blocker Chaining). Ktory vyuziva 
okrem `256 bitoveho kluca` aj `inicializacny vektor`, ktory je pred encryptovani ulozeny v hlavicke suboru.
Tento sifrovaci standart je velmi bezpecny a tazko decriptovatelny bez poznania kluca, preto je odporucany.
V hlavicke suboru je este ulozena velkost suboru, kvoli tomu ze pri dekriptovani budeme potrebovat 
vediet velkost originalneho suboru kvoli orezaniu paddingu. Padding je pri AES.block_size rovny 16.


Format zasifrovaneho suboru:
````
 ___________________________________
|                                   |
|             File size             |
|___________________________________|
|                                   |
|        Initialization vector      |
|___________________________________|
|                                   |
|                                   |
|        Encrypted raw data         |
|                 .                 |
                  .
|                 .                 |
|___________________________________|

````

### Rychlost sifrovania:
Na testovanie rychlosti som pouzil subor archivovany subor s velkostou 998 MB.
````
$ python main.py --encode ctf8.zip ctf8.enc

File has size:  998558948
Crypting time:  0:00:02.654827
````

````
$ python main.py --decode ctf8.enc ctf8_new.zip

File has size:  998558984
Crypting time:  0:00:02.027342
````

Teda cas sifrovania je pre 1Gb subor priblizne 2.5 sekundy. Kvoli velkosti suboru su data
citane postupne a sifrovane po castiach.

### Uzivatelsky manual

Poziadavky na spustenie zo zdrojovych suborov:
Python >=3.0
pycryptodome==3.9.8
click==7.1.2

Automaticke nainstalovanie vsetkych dependecies:
````
pip install -r requirements.txt
````

Priklad pouzitia:
````
$ python main.py --encode <input_file_name>
````

````
$ python main.py --decode <input_file_name> <key_file_name>
````

Ak sa aplikacia pusti bez vstupnych argumentov z CLI, uzivatel bude dodatocne poziadany o 
nazov suboru a metodu sifrovania. Po uspesnom ukonceni operacie sifrovania bude uzivatel moct rozhodnut ci sa 
stary subor vymaze.

Priklad vystupu:

````

File has size:  344
Crypting time:  0:00:00.001965
Do you want to remove input file y/n?y
Press any key to exit.

````

### Spustenie aplikacie bez nutnosti instalacie Python interpretera
V subore /output/main sa nachadza executable skompilovana aplikacia `main.exe`, ktora otvory CLI s moznostov
zadania vstupnych parametrov.


### Odporucania
Aplikacia po encriptovani ulozi pouzity nahodne vygenerovany kluc do samotneho suboru `key.bin`. Tento subor
je potrebne uchovat a bezpecne ulozit aby mohol byt pouzity pri desifrovani.