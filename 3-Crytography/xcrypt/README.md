# xcrypt - aplikácia na šifrovanie a dešifrovanie s využitím RSA a kontroly integrity


## Third part API
Aplikacia je postavena na sifrovacom python baliku `Cryptodome`. Ktory je nastavbou nad 
osvedcenym `PyCrypto` packagom. Obsahuje bezpecne sifrovacie funkcie a korektn0 implmentacie generatorov
nahodnych klucov.

## Opis aplikacie
Aplikacia je implmentovana v jazyku python. Na sifrovanie suborov - SESSION KEY som vyuzil 
Advanced Encryption Standard (AES) konkretne mod MODE_GCM (Galois/Counter Mode). AES GCM vyuziva na šifrovanie
`256 bitový kluc` aj `nonce - inicializacny vektor`. Tento sifrovaci standart som použil preto lebo je velmi bezpecny.

Aplikácia implementuje aj kontrolu integrity dat pomocov MAC modu. Tato funkcionalita odhali ak bol 
subor modifikovany pocas prenosu a to vdaka HASH vysledku (tag), ktory sa musi rovnat ako pre poslany tak aj pre prijaty
obsah dat. Tento tag je ulozeny na konci spravy/suboru. Ak MAC tag cerifikacia nie je spravna prijimatel moze vyziadat
o novu spravu alebo a staru dropnut.

Posielanie zasifrovanych dat je realizovane cez bezpecny kanal pomocou asymetrickeho RSA algortimu.
Tento princip vyuziva par klucov - sukromny a verejny. Pricom verejny kluc poznaju obe strany komunikacie ale sukromny
kluc pozna len prijimatel. Odosielatel spravy zasifruje session key (256bit AES) pomocou verejneho kluca a ulozi do 
hlavicky spravy alebo suboru. Prijimatel musi pred desiforvanim dat desifrovat session key pomocou svojho privatneho kluca.
Potom moze desifrovat data cez session key.

Bonus: 
Ukladanie z hlavicky a do hlavicky riesim sam ako je vyzadovane. Pouzivam AES GCM sifrovanie a MAC kontrolu integrity.


Format zasifrovaneho suboru:
````
 ___________________________________
|                                   |
|     RSA encrypted Session key     |
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
|               MAC tag             |
|___________________________________|

````


### Uzivatelsky manual

Poziadavky na spustenie zo zdrojovych suborov:
Python >=3.0
pycryptodome==3.9.8
click==7.1.2

Automaticke nainstalovanie vsetkych dependecies:
````
pip install -r requirements.txt
````

### Priklad pouzitia:
Generovanie noveho paru RSA klucov
````
$ python xcrypt.py --generate <optional_key_name>
````

Sifrovanie robi odosilatel vyuzitim verejneho kluca prijimatela
````
$ python xcrypt.py --encrypt <rsa_public_key_file> <input_file_name> <optional_out_file_name>
````

Desifrovanie robi prijimatel vyuzitim svojho privatneho kluca
````
$ python xcrypt.py --decrypt <rsa_private_key_file> <input_file_name> <optional_out_file_name>
````

Ak sa aplikacia pusti bez vstupnych argumentov z CLI, uzivatel bude dodatocne poziadany o 
nazov suborov a metodu sifrovania. Po uspesnom ukonceni operacie sifrovania bude uzivatel moct rozhodnut ci sa 
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
Je velmi dolezite bezpecne uchovat a nezverejnovat sukromny RSA kluc.

### Testovanie aplikacie
V priecinku sa nachadzaju testovacie vygenerovane RSA kluce (rsa_key_private.pem, rsa_key_public.pem), 
vstupny subor test.orig ktory budeme sifrovat.

Generovanie novych klucov
````
$ python xcrypt.py --generate 
````

Sifrovanie testovacich suborov
````
$ python xcrypt.py --encrypt rsa_key_public.pem test.orig
````

Desifrovanie testovacieho suboru
````
$ python xcrypt.py --decrypt rsa_key_private.pem test.enc
````

