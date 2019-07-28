# Bláznivá križovatka
## Info
Autor: Filip Bednárik

Škola: Slovenská technická univerzita v Bratislave

Fakulta: Fakulta informatiky a informačných technológií

Adresa: Ilkovičova 3, 842 16 Bratislava 4

Študijný program: Informatika

Ročník: 3

Predmet: Umelá inteligencia

Vedúci projektu: Ing. Ivan Kapustík

Ak. rok: 2012/13

## Úvod
### Zadanie

Úlohou je nájsť riešenie hlavolamu Bláznivá križovatka. Hlavolam je reprezentovaný mriežkou, ktorá má rozmery 6 krát 6 políčok a obsahuje niekoľko vozidiel (áut a nákladiakov) rozložených na mriežke tak, aby sa neprekrývali. Všetky vozidlá majú šírku 1 políčko, autá sú dlhé 2 a nákladiaky sú dlhé 3 políčka. V prípade, že vozidlo nie je blokované iným vozidlom alebo okrajom mriežky, môže sa posúvať dopredu alebo dozadu, nie však do strany, ani sa nemôže otáčať. V jednom kroku sa môže pohybovať len jedno vozidlo. V prípade, že je pred (za) vozidlom voľných n políčok, môže sa vozidlo pohnúť o 1 až n políčok dopredu (dozadu). Ak sú napríklad pred vozidlom voľné 3 políčka (napr. oranžové vozidlo na počiatočnej pozícii, obr. 1), to sa môže posunúť buď o 1, 2, alebo 3 políčka.

Hlavolam je vyriešený, keď je červené auto (v smere jeho jazdy) na okraji križovatky a môže z nej teda dostať von. Predpokladajte, že červené auto je vždy otočené horizontálne a smeruje doprava. Je potrebné nájsť postupnosť posunov vozidiel (nie pre všetky počiatočné pozície táto postupnosť existuje) tak, aby sa červené auto dostalo von z križovatky alebo vypísať, že úloha nemá riešenie.

Použite algoritmus prehľadávania do šírky a do hĺbky. Porovnajte ich výsledky.

Príklad:

| Počiatočná pozícia | Cieľová pozícia |
| --- | --- |
| ![Počiatočná pozícia](docs/krizovatka1.png?raw=true) | ![Cieľová pozícia](docs/krizovatka2.png?raw=true) |
Obr. 1 Počiatočná a cieľová pozícia hlavolamu Bláznivá križovatka.

## Riešenie
### Použité technológie

Java, OpenCV, JavaCV, Swing

### Inštalácia

1. Nainštalujte JDK alebo JRE (32bit/64bit podľa vášho procesoru)
2. Skompilujte a zostavte JAR súbor
3. Spustite aplikáciu príkazom java -jar Krizovatka.jar

### Opis riešenia

Opis použitia algoritmov. Aké možné vstupy je možné použiť je opísané v sekcii testovanie možností vstupov.

Program využíva Deque, ktorá reprezentuje zásobník FIFO ale aj LIFO, podľa toho aké metódy sa nad ňou použijú.

Program vytvára stavy. Každý stav má svoj „hash&quot; čo je vlastne textový opis stavu a tento je pre stav jedinečný.

Program funguje následovne:

- Vytvorí si prvý stav
- Pre každé auto v stave zistí všetky jeho možné pohyby
- Pre každý pohyb vytvorí nový stav
- Každý tento stav pridá do Deque a zoznamu stavov ak je jedinečný
- Pokračuje stavom získaného z vrchu Deque
- Ak je Deque prázdne tak sa program končí neúspechom
- Ak stav obsahuje auto v pozícii do ktorej ho chceme dostať tak je stav označený ako výsledný a program končí úspechom

Program si pri každom stave zapamätáva predchodcu a operand použitý pri prechode. Tieto informácie využíva pri vypisovaní postupu riešenia pomocou rekurzie.

**Ako prehľadáva stavy algortimus s LIFO zásobníkom (prehľadávanie do hĺbky):**

![Príklad úlohy 1](docs/krizovatka3.png?raw=true)
*Obr.2 Príklad úlohy 1*

![Diagram stavov](docs/krizovatka4.png?raw=true)
*Obr.3 Diagram stavov*

S1: (cervene 2 3 4 h)(ruzove 2 2 6 v)

Rozvíjanie stavu S1:

S2: (cervene 2 3 3 h)(ruzove 2 2 6 v)

S3: (cervene 2 3 2 h)(ruzove 2 2 6 v)

S4: (cervene 2 3 1 h)(ruzove 2 2 6 v)

S5: (cervene 2 3 4 h)(ruzove 2 1 6 v)

S6: (cervene 2 3 4 h)(ruzove 2 3 6 v)

S7: (cervene 2 3 4 h)(ruzove 2 4 6 v)

S8: (cervene 2 3 4 h)(ruzove 2 5 6 v)

Rozvíjanie stavu S8 (ignorované duplicity):

S9: (cervene 2 3 3 h)(ruzove 2 5 6 v)

S10: (cervene 2 3 2 h)(ruzove 2 5 6 v)

S11: (cervene 2 3 2 h)(ruzove 2 5 6 v)

S12: (cervene 2 3 5 h)(ruzove 2 5 6 v)

Stav S12 je finálny, algoritmus sa ukončí úspechom a cesta je:

 (cervene 2 3 4 h)(ruzove 2 2 6 v)

DOLE(ruzove, 3)

 (cervene 2 3 4 h)(ruzove 2 5 6 v)

VPRAVO(cervene, 1)

 (cervene 2 3 5 h)(ruzove 2 5 6 v)

**Ako prehľadáva stavy algoritmus s FIFO zásobníkom (prehľadávanie do šírky):**

![Diagram stavov](docs/krizovatka5.png?raw=true)
*Obr.4 Diagram stavov*

S1: (cervene 2 3 4 h)(ruzove 2 2 6 v)

Rozvíjanie stavu S1:

S2: (cervene 2 3 3 h)(ruzove 2 2 6 v)

S3: (cervene 2 3 2 h)(ruzove 2 2 6 v)

S4: (cervene 2 3 1 h)(ruzove 2 2 6 v)

S5: (cervene 2 3 4 h)(ruzove 2 1 6 v)

S6: (cervene 2 3 4 h)(ruzove 2 3 6 v)

S7: (cervene 2 3 4 h)(ruzove 2 4 6 v)

S8: (cervene 2 3 4 h)(ruzove 2 5 6 v)

Rozvíjanie stavu S2: (ignorované duplicity)

S9: (cervene 2 3 3 h)(ruzove 2 1 6 v)

S10: (cervene 2 3 3 h)(ruzove 2 3 6 v)

S11: (cervene 2 3 3 h)(ruzove 2 4 6 v)

S12: (cervene 2 3 3 h)(ruzove 2 5 6 v)

Rozvíjanie stavu S3: (ignorované duplicity)

S13: (cervene 2 3 2 h)(ruzove 2 1 6 v)

S14: (cervene 2 3 2 h)(ruzove 2 3 6 v)

S15: (cervene 2 3 2 h)(ruzove 2 4 6 v)

S16: (cervene 2 3 2 h)(ruzove 2 5 6 v)

Rozvíjanie stavu S4: (ignorované duplicity)

S17: (cervene 2 3 1 h)(ruzove 2 1 6 v)

S18: (cervene 2 3 1 h)(ruzove 2 3 6 v)

S19: (cervene 2 3 1 h)(ruzove 2 4 6 v)

S20: (cervene 2 3 2 h)(ruzove 2 5 6 v)

Rozvíjanie stavu S5:

S21: (cervene 2 3 5 h)(ruzove 2 1 6 v)

Stav S21 je finálny, algoritmus sa ukončí úspechom a cesta je:

(cervene 2 3 4 h)(ruzove 2 2 6 v)

HORE(ruzove, 1)

 (cervene 2 3 4 h)(ruzove 2 1 6 v)

VPRAVO(cervene, 1)

(cervene 2 3 5 h)(ruzove 2 1 6 v)

**Ako prehľadáva stavy algoritmus s FIFO zásobníkom (prehľadávanie do šírky):**

![Príklad úlohy 2](docs/krizovatka6.png?raw=true)
*Obr.5 Príklad úlohy 2*

![Diagram stavov](docs/krizovatka7.png?raw=true)
*Obr.6 Diagram stavov*

S1: (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

Rozvíjanie stavu S1:

S2: (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

S3: (cervene 2 3 4 h)(cierne 2 5 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

Rozvíjanie stavu S2: (ignorované duplicity)

S4:(cervene 2 3 5 h)(cierne 2 4 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

Stav S4 je finálny, algoritmus sa ukončí úspechom a cesta je:

 (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

HORE(fialove, 1)

 (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

VPRAVO(cervene, 1)

 (cervene 2 3 5 h)(cierne 2 4 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

**Ako prehľadáva stavy algortimus s LIFO zásobníkom (prehľadávanie do hĺbky):**

![Diagram stavov](docs/krizovatka8.png?raw=true)
*Obr.7 Diagram stavov*

S1: (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

Rozvíjanie stavu S1:

S2: (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

S3: (cervene 2 3 4 h)(cierne 2 5 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

Rozvíjanie stavu S3 (ignorované duplicity):

S4:(cervene 2 3 4 h)(cierne 2 5 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

S5:(cervene 2 3 4 h)(cierne 2 5 6 v)(fialove 2 3 6 v)(oranzove 3 3 1 h)

Rozvíjanie stavu S5(ignorované duplicity):

Rozvíjanie stavu S4 (ignorované duplicity):

S6:(cervene 2 3 5 h)(cierne 2 5 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

Stav S6 je finálny, algoritmus sa ukončí úspechom a cesta je:

 (cervene 2 3 4 h)(cierne 2 4 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

DOLE(cierne, 1)

 (cervene 2 3 4 h)(cierne 2 5 6 v)(fialove 2 2 6 v)(oranzove 3 3 1 h)

HORE(fialove, 1)

 (cervene 2 3 4 h)(cierne 2 5 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)

VPRAVO(cervene, 1)

 (cervene 2 3 5 h)(cierne 2 5 6 v)(fialove 2 1 6 v)(oranzove 3 3 1 h)



### Reprezentácia údajov

Údaje sú reprezentované objektmi uloženými v štruktúre hashmáp.

Použité boli dva algoritmy prehľadávanie do šírky a do hĺbky.

Všetky stavy sú uložené v štruktúre HashMap\&lt;String, Vertex\&gt;. Nespracované stavy sú uložené v štruktúre Deque\&lt;Vertex\&gt;.

Stav (Vertex) má v sebe záznam na predchodcu, operator akým sa z neho dostal a štruktúru aut.

Štruktúra aut je vyjadrená mapou HashMap\&lt;String, Auto\&gt; kde String je textová reprezentácia auta a Auto je samotný objekt auta.

Všetky údaje sú reprezentované aj graficky v GUI aj textovo v konzole.

## Testovanie
### Testovanie rýchlosti algoritmov

Algoritmus používajúci zásobník FIFO je pomalší ako algoritmus používajuci LIFO avšak vracia najkratšiu cestu k výsledku.

### Testovanie možností vstupov

Vstup je možné zadávať viacerými spôsobmi:

#### Vstup textovým súborom

Vstupom je popísaný počiatočný stav aut pomocou slov v následovnom tvare:

((cervene 2 3 1 h)(oranzove 2 1 1 v)(zlte 2 1 2 h)(fialove 2 1 4 h)(zelene 2 2 4 v)

(svetlomodre 3 2 6 v)(sive 3 3 3 v)(tmavomodre 2 4 4 h)(ruzove 2 5 4 v)(tmavosive 2 5 5 h)(cierne 3 6 1 h))

Začiatok vstupu je označený otvárajúcou zátvorkou a koniec vstupu uzavierajúcou zátvorkou. Jednotlivé autá majú farbu: (cervene, oranzove, zlte, fialove, zelene, svetlomodre, sive, ruzove, cierne, tmavosive, tmavomodre)

Nasleduje veľkosť auta, súradnice Y a X (Y je riadok X je stĺpec) a orientácia auta (h pre horizontálne a v pre vertikálne).

Program nekontroluje úplnú korektnosť vstupu, sám si ho upravuje na korektný do istej miery a pre správne vyriešenie problému vyžaduje červené auto v horizontálnej polohe). Program ukáže ako interpretoval vstup až po kliknutí na solve. Samozrejme je možné dokresliť autá pri vstupe myšou.

#### Vstup myšou

Jednoduchým klikaním je možné naklikať jednotlivé autá v mriežke. Vyberte farbu a následne kliknite na políčko v mriežke. Následne kliknite na Analyze Input. Program analyzuje vstup a následne umožní riešenie problému. Pri každej úprave je potrebné analyzovať vstup inak sa bude riešiť problém bez úprav.

Program si sám upravuje vstup tak aby bol korektný. Pre správne vyriešenie problému vyžaduje červené auto v horizontálnej polohe). Program ukáže ako interpretoval vstup až po kliknutí na solve. Program interpretuje autá veľkosti 1 ako horizontálne.

#### Vstup obrázkom

Pri vybratí súboru obrázku program automaticky analyzuje objekty na obrázku a vytvorí rekonštrukciu problému. Program nie vždy správne odhadne farbu aut (berie ju zo stredu auta). Programu sa nie vždy podarí nájsť všetky autá a môže nájsť autá ktoré neexistujú. Samozrejme je možné upraviť si to vstupom myšou. Program sám odhaduje, ktoré auto je najčervenšie. Ak upravíte vstup myšou musíte explicitne nakresliť červené auto. Ak program zle určí červené auto jednoducho nakreslite červené auto tam kde má byť myšou a dajte analyzovať vstup.

#### Vstup zachytením obrazu

Vstup zachytením obrazu využíva rovnaký postup ako vstup obrázkom ale vstupom nie je súbor ale kamera.

### Testovanie špeciálnych prípadov

V prípade neplatného vstupu program vypíše Failure.

Testoval som level 85 z hry a program ho vyriešil FIFO zásobníkom za 295 ms a LIFO zásobníkom za 45 ms.

Testoval som vstupy uvedené v zadaní a program ich vyriešil za 187 ms (FIFO) a 22 ms (LIFO).

## Záver
### Zhodnotenie riešenia

Riešenie zadania je komplexné a dokáže čítať rôzne vstupy, čo mu dáva výhodu najmä v použiteľnosti v praxi. Samotný algoritmus je primerane optimálny, vylučuje duplicitné stavy pomocou hashov a je možné si zvoliť ktoré prehľadávanie (do šírky/do hĺbky) sa použije vybratím typu zásobníka.

### Možnosti zlepšenia

- Viem si predstaviť, že by bolo možné robiť hashe jednoduchšie prípadne si stavy pamätať v inej štruktúre čo by zrýchlilo rýchlosť algoritmu o pár milisekúnd. Pri probléme 6x6 to však nehrá až takú rolu.
- Program by mohol lepšie ošetrovať vstupy.
- Program by mohol byť viac používateľsky prívetivý.
- Program by mohol zobrazovať obraz ktorý zachytáva aj s vyznačenou detekciou objektov (mám to implementované ale naskytli sa problémy so synchroznizáciou tak som to kvôli nedostatku času nedal do výsledného programu)
- Program by mohol mať viac nastavení (napr. nastavenie debug módu, úpravy obrazu pred spracovaním do objektov atď.)
- Program by mohol bežať na mobilných zariadeniach. (Android by nemal byť problém – trebalo by len spraviť GUI)
- Program by mohol riešenie robiť priamo na obraze z kamery a tak rozšíriť realitu.

### Výhody a nevýhody riešenia

**Výhody**

- Množstvo vstupov
- Rýchly algoritmus
- Výber algoritmu v GUI
- GUI s nastaveniami a reprezentáciou problem
- Objektovosť a rozšíriteľnosť riešenia
- Portabilita a multiplatformovosť

**Nevýhody**

- Program vyžaduje ffmpeg ak chceme zachytávať obraz z kamery
- Program vyžaduje openCV knižnicu pre rozpoznávanie obrázkov, ktorá je trošku pamäťovo náročnejšia
