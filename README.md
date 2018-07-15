# APP-Projekt 2018: **FlowerWarsPP**

## Gruppenname

**eanufwpp**: **E**in **a**ussagekräftiger **N**ame **u**ngleich **F**lower**W**ars**PP**

## Gruppenmitglieder

- Fabian Winter
- Lars Quentin
- Michael Merse
- Thilo Wischmeyer (Gruppenleiter)

## Tutor

Maximilian David Eipper

## Anleitung

### Installation und Quick Start

Im Folgenden wird beschrieben, wie das, als `tar` gepackte Spiel sich entpacken, kompilieren und starten lässt. Die
verschiedenen Einstellungsmöglichkeiten werden im nächsten Abschnitt detailliert beschrieben.

1. Ein neues Terminal starten.
2. In das Verzeichnis wechseln, in welchem sich das `tar`-Archiv mit dem gepackten Spiel befindet.
3. Das `tar`-Archiv mit folgendem Befehl entpacken:  
   `tar xvf eanufwpp.tar`
4. In das entpackte Verzeichnis `eanufwpp` wechseln
5. Mit Hilfe des Befehls `ant` den Quelltext übersetzen, die Javadoc-Dokumentation erzeugen und das ausführbare
`jar`-Archiv packen.:  
   `ant`
6. Das Spiel ist nun kompiliert und vorbereitet.
 
Mit dem folgenden Befehl kann die Hilfe des Spiels mit kurzen
Erklärungen der möglichen Kommandozeilenparameter angezeigt werden:  

```
/eanufwpp.jar --help
```

Wenn die jar-Datei ohne Kommandozeilenargumente aufgerufen wird, wird der Startup-Dialog angezeigt. Mit diesem 
lassen sich die Parameter des Spiels eingestellt und somit ein Spiel mit der grafischen Ausgabe gestartet werden:  

```
./eanufwpp.jar
```

Für ein schnelles Test-Spiel gegen den simplen Computerspieler kann der folgende Befehl benutzt werden:  

```
./eanufwpp.jar -size 8 -red human -blue simple -delay 1000
```


### Kommandozeilenparameter

#### Grundlegendes

_Einstellungen_ sind Parameter mit einem Wert. Sie beginnen mit einem einzelnen `-`, gefolgt vom Namen des Parameters,
einem Leerzeichen und anschließend dem Wert der Einstellung. Die Einstellung `size` sieht zum Beispiel so aus:
`-size x`  

_Schalter_ sind Parameter ohne Wert. Sie beginnen mit `--`, gefolgt vom Namen des Schalters.
 Der Schalter `debug` wird zum Beispiel wie folgt gesetzt:
`--debug`  

Im Folgenden unterscheiden wir zwischen _Notwendigen Einstellungen_, _Optionalen Einstellungen_ und den _Schaltern_.  

Notwendige Einstellungen sind zwingend erforderlich, um das Spiel zu starten. Fehlt eine notwendige Einstellung, wird
das Programm sofort beendet.

Optionale Einstellungen dienen zur Anpassung des Spiels. Sie sind aber nicht zwingend erforderlich, damit das Spiel
gestartet wird.

Schalter können gesetzt werden, um das Spiel zu beeinflussen, ihre Anwesenheit ist aber nie zwingend erforderlich. 
Während die notwendigen und optionalen Einstellungen bei lokalem und Netzwerkspiel unterscheiden, sind Schalter 
überall gleich anwendbar.

#### Notwendige Einstellungen (lokales Spiel)

Um ein lokales Spiel zu starten, müssen die folgenden Einstellungen gesetzt werden:

- Die Größe des Spielbretts, eine Ganzzahl zwischen 5 und 30 (inklusive). Diese Einstellung wird gesetzt mit  
    `-size {5 ,..., 30}`
- Die Spielertypen des roten und blauen Spielers. Die verfügbaren Spielertypen werden in einem folgenden Abschnitt
genauer behandelt. Diese Einstellungen werden für beide Spieler gesetzt mit
  - `-red {human, random, simple, adv1, adv2, remote}`
  - `-blue {human, random, simple, adv1, adv2, remote}`

#### Optionale Einstellungen (lokales Spiel)

- Mit dem Setzen von `-delay <Zeit in Millisekunden>` wird eine Verzögerung zwischen Zügen erzwungen. So kann zum
Beispiel ein Spiel zwischen zwei Computerspielen für den menschlichen Beobachter nachvollziehbar gemacht werden.
- Mit `-load <Dateipfad>` kann ein zuvor gespeicherter Spielstand wieder geladen werden. Beim Laden eines
Spielstands muss die `-size`-Einstellung nicht gesetzt werden. `load` funktioniert bei dem RemotePlayer nicht.
- Mit der Einstellung `-replay <Zeit in Millisekunden>` kann ein geladener Spielstand rudimentär Schritt für Schritt
 abgespielt werden, bis zu dem Punkt, an dem gespeichert worden ist. Die übergebene Zeit in Millisekunden beschreibt die
  Verzögerung zwischen den Zügen. Nachdem das Replay durchgelaufen ist, wird das Spiel an dem Punkt fortgesetzt, der durch
  den Spielstand beschrieben wird.
- Mit der Einstellung `-games <Anzahl Spiele>` wird der Benchmark-Modus gestartet. Zwei Spieler nehmen dabei an der
gegebenen Anzahl an Spielen teil. Am Ende wird eine Statistik über die Anzahl der Siege und die
durchschnittliche Punktezahl für jeden Spieler ausgegeben. Ist `<Anzahl Spiele>` gleich 1 wird das Spiel wie normal gestartet.

#### Schalter (global)

- Mit dem Schalter `--debug` kann die Ausgabe von debug-Informationen eingeschaltet werden.
- Mit dem Schalter `--text` wird das Spiel nicht auf der grafischen Ausgabe angezeigt, sondern auf der
Standardausgabe.
- Mit dem Schalter `--help` wird eine kurze Hilfe zur Verwendung des Programms auf der Standardausgabe
ausgegeben.
- Mit dem Schalter `--quiet` wird die Ausgabe deaktiviert. Das Spielgeschehen kann dann nicht mehr mitverfolgt
werden, jedoch kann ein interaktiver Spieler immer noch Züge über die Standardeingabe eingeben.

#### Notwendige Einstellungen (Netzwerkspiel - Spieler finden)

Falls mindestens einer der beiden Spieler den Typen `remote` hat, muss dem Programm zusätzlich mitgeteilt werden, wo 
dieser entfernte Spieler zu finden ist. Dazu muss die Einstellung `-redUrl <URL>` bzw. `-blueUrl <URL>` gesetzt werden. 
Eine URL hat folgendes Format:

`host:port/name`

#### Notwendige Einstellungen (Netzwerkspiel - Spieler anbieten)

Um einen Spieler im Netzwerk anzubieten, wird die Einstellung

`-offer <Spielertyp>`

benötigt. `Spielertyp` beschreibt dabei den Spielertypen, welcher im Netzwerk angeboten werden soll. Außerdem muss dem
Programm mit `-name <Name>` der Name des anzubietenden Spielers mitgeteilt werden.

Falls der anzubietende Spieler den Typ `remote` hat, muss dem Programm mit der Einstellung `-offerUrl <URL>` mitgeteilt
werden, wo der anzubietende entfernte Spieler zu finden ist. 

#### Optionale Einstellungen (Netzwerkspiel - Spieler anbieten)

Mit der Einstellung `-port <Port>` kann dem Programm mitgeteilt werden, auf welchem Port der Spieler angeboten werden
soll.

Beispiel für Anbieten eines Spielers im Netzwerk:

```
./eanufwpp.jar -offer human -name Name
```

### Starten ohne Kommandozeilenparameter

Wird die als `.jar` gepackte Version des Spiels ohne Kommandozeilenargumente von der Konsole gestartet oder durch 
doppeltes Klicken auf die Datei in einem Dateimanager aufgerufen, öffnet sich ein grafischer 
Dialog, mit dem die Parameter des Spiels eingestellt werden können.

Dieser Startdialog erlaubt das Starten von lokalen oder Netzwerkspielen. Jedoch können im Startdialog 
keine Schalter gesetzt werden. Diese Restriktion existiert, weil alle verfügbaren Schalter eine geöffnete 
Konsole voraussetzen. Der Startdialog ist also nur zum Starten von Spielen vorgesehen, welche ausschlielßlich über 
die grafische Ausgabe mitverfolgt und gesteuert werden können.

### Arten von Spielern

Es gibt eine Reihe von verschiedenen Typen von Spielern, welche entweder in einem lokalen Spiel verwendet oder mit
`-offer <Spielertyp>` im Netzwerk angeboten werden können.

#### `human`

Ein Spieler diesen Typs wird direkt von einem Menschen kontrolliert.

#### `random`

Dieser computergesteuerte Spieler macht zufällige Spielzüge.

#### `simple`

Dieser computergesteuerte Spieler wählt auf Basis eines einfachen Bewertungsalgorithmus Blumen-Züge aus. 
Wenn keine Blumen-Züge mehr möglich sind, macht der einfache Computerspieler so lange Graben-Züge bis das Spiel beendet ist.

#### `adv1`

Der verbesserte Computerspieler ersten Levels bedient sich einer angepassten Version des Bewertungsalgorithmus des
einfachen Computerspielers. Außerdem führt dieser Computerspieler Graben-Züge, die zwei Gärten miteinander verbinden,
sofort aus. Dieser Spieler beendet das Spiel vorzeitig, falls er in Führung liegt.

#### `adv2`

Der verbesserte Computerspieler zweiten Levels bedient sich einer angepassten Version des Bewertungsalgorithmus des
verbesserten Computerspielers ersten Levels. Außerdem führt dieser Computerspieler Graben-Züge, die zwei Gärten
miteinander verbinden, sofort aus. Zudem versucht dieser Computerspieler, die eigenen Blumen und Gärten in Clustern
anzuordnen, sodass sich möglichst lange zusammenhängende Strukturen ergeben.
.

#### `remote`

Wird dieser Spielertyp gewählt, so versucht das Spiel einen im Netzwerk angebotenen entfernten Spieler anzufragen und zu
 verwenden. Es ist auch möglich, dass beide Spieler diesen Typ benutzen.

### Weiteres

#### Speichern eines Spiels

Das Spiel kann nur über die grafische Ausgabe gespeichert werden. Ein Klick auf den Button mit der Aufschrift
"Spielstand speichern" öffnet einen Dialog, in welchem der Speicherort der Datei ausgewählt werden kann. Das Spiel wird
dann in der ausgewählten Datei gespeichert und kann zu einem späteren Zeitpunkt wieder geladen werden.

Wird beim Speichern der Datei keine Endung angegeben, wird automatisch die Endung `.sav` ergänzt.

## Tabellarische Referenz

| Parameter                      | Optionen                                | Beschreibung
| ------------------------------ | --------------------------------------- | ----------------------
| **Notwendig (Lokales Spiel)**
| `-size`                        | Zahl zwischen 5 und 30 (inklusive)      | Die Größe des Spielbretts wird auf den gegebenen Wert gesetzt
| `-red`                         | Einer der oben genannten Spielertypen   | Setzt den Spielertypen des roten Spielers auf den gegebenen Typen
| `-blue`                        | Einer der oben genannten Spielertypen   | Setzt den Spielertypen des blauen Spielers auf den gegebenen Typen
| *Falls Rot/Blau Remote*
| `-redUrl`/`-blueUrl`           | Eine URL: HOST:PORT/NAME                | Adresse unter welcher der entfernte Spieler zu finden ist
| **Optional (Lokales Spiel)**
| `-delay`                       | Zeit in Millisekunden                   | Verzögerung in Millisekunden zwischen Spielzügen
| `-load`                        | Pfad zur Spielstanddatei                | Lädt den gegebenen Spielstand und setzt das Spiel fort
| `-replay`                      | Zeit in Millisekunden                   | Der geladene Spielzug wird Zug für Zug ausgeführt, mit der gegebenen Verzögerung zwischen den Zügen
| `-games`                       | Anzahl an Spielen                       | Zwei Spieler treten in der gegebenen Anzahl von Spielen gegeneinander an. Am Ende wird eine Statistik ausgegeben
| **Notwendig (Netzwerkspiel)**
| `-offer`                       | Einer der oben genannten Spielertypen   | Bietet den angegebenen Spielertypen im Netzwerk an
| `-name`                        | Der Name des Spielers                   | Der Name unter welchem der Spieler angeboten werden soll
| **Optional (Netzwerkspiel)**
| `-port`                        | Ein valider, offener Port               | Der Port an welchem der Spieler angeboten werden soll
| *Falls Remote angeboten wird*
| `-offerUrl`                    | Eine URL: HOST:PORT/NAME                | Adresse unter welcher der entfernte Spieler zu finden ist
| **Schalter**
| `--debug`                      | Keine                                   | Falls gesetzt, werden Debug-Informationen vom Logger aufgenommen und ausgegeben
| `--text`                       | Keine                                   | Falls gesetzt, wird das Spielgeschehen auf der Standardausgabe angezeigt, und nicht auf der graphischen Anzeige
| `--help`                       | Keine                                   | Zeigt eine kurze Hilfe an und beendet das Programm
| `--quiet`                      | Keine                                   | Deaktiviert die Ausgabe des Spielbretts.
