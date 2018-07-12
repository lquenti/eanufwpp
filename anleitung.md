# APP-Projekt 2018: **FlowerWarsPP**

## Gruppenname

eanufwpp: **E**in **a**ussagekräftiger **N**ame **u**ngleich **F**lower**W**ars**PP**

## Gruppenmitglieder

- Fabian Winter
- Lars Quentin
- Michael Merse
- Thilo Wischmeyer (Gruppenleiter)

## Tutor

David Eipper

## Anleitung

### Installation und Quick Start

Im Folgenden wird beschrieben, wie das, als `tar` gepackte Spiel sich entpacken, kompilieren und starten lässt. Die verschiedenen Einstellungsmöglichkeiten werden im nächsten Abschnitt detailliert beschrieben.

1. Starten eines neuen Terminals. Unter _Unity_ lässt sich standardmäßig mit `strg + alt + t` ein neues Terminal öffnen.
2. In das Verzeichnis wechseln, in welchem sich das `tar`-Archiv mit dem gepackten Spiel befindet. Falls dieses Archiv heruntergeladen worden ist, befindet es sich unter Ubuntu höchstwahrscheinlich im Verzeichnis `~\Downloads`.
3. Das `tar`-Archiv mit folgendem Befehl entpacken:  
   **`tar xvf eanufwpp.jar`**
4. In das entpackte Verzeichnis `eanufwpp` wechseln
5. Mit Hilfe des Befehls `ant` den Quelltext übersetzen, die Javadoc-Dokumentation erzeugen und das ausführbare `jar`-Archiv packen.
6. Das Spiel ist nun installiert und vorbereitet. Mit dem folgenden Befehl kann die Hilfe des Spiels mit kurzen Erklärungen der möglichen Kommandozeilenparameter angezeigt werden:  
   **`java -jar eanufwpp.jar --help`**  
   Für ein schnelles Test-Spiel gegen den simplen Computerspieler kann der folgende Befehl benutzt werden:  
   **`java -jar eanufwpp.jar -size 8 -red human -blue simple -delay 1000`**
7. Wird das Spiel ohne jegliche Kommandozeilenparameter aufgerufen, dann wird ein interaktiver Auswahlbildschirm angezeigt mit welchem sich das Spiel anpassen lässt.

### Kommandozeilenparameter

#### Grundlegendes

_Einstellungen_ sind Parameter mit einem Wert. Sie beginnen mit einem einzelnen `-`, gefolgt vom Namen des Parameters, einem Leerzeichen und anschließend dem Wert der Einstellung. Die Einstellung `size` sieht zum Beispiel so aus:  
**`-size x`**  

_Schalter_ sind Parameter ohne Wert. Ihre reine Anwesenheit hat schon eine Wirkung. Deswegen sind Schalter optional, sie müssen nicht zwingend gesetzt werden, damit das Spiel startet. Sie beginnen mit `--`, gefolgt vom Namen des Schalters. Der Schalter `debug` wird zum Beispiel wie folgt gesetzt:  
**`--debug`**  

Im Folgenden unterscheiden wir zwischen _Notwendigen Einstellungen_, _Optionalen Einstellungen_ und den _Schaltern_.  

Notwendige Einstellungen sind zwingend erforderlich, um das Spiel zu starten. Fehlt eine notwendige Einstellung, wird das Programm sofort beendet.  

Optionale Einstellungen dienen zur Anpassung des Spiels. Sie sind aber nicht zwingend erforderlich, damit das Spiel gestartet wird.  

Schalter sind weiter oben bereits beschrieben. Sie können gesetzt werden, um das Spiel zu beeinflussen, ihre Anwesenheit ist aber nie zwingend erforderlich. Während die notwendigen und optionalen Einstellungen bei lokalem und Netzwerkspiel unterscheiden, sind Schalter überall gleich anwendbar.

Wir betrachten nun zuerst die Einstellungen und Schalter bei lokalen Spielen.

#### Notwendige Einstellungen (lokales Spiel)

Um ein lokales Spiel zu starten, müssen die folgenden Einstellungen gesetzt werden:

- Die Größe des Spielbretts, eine Ganzzahl zwischen 5 und 30 (inklusive). Diese Einstellung wird gesetzt mit  
    **`-size {5 ,..., 30}`**
- Die Spielertypen des roten und blauen Spielers. Die verfügbaren Spielertypen werden in einem folgenden Abschnitt genauer behandelt. Diese Einstellungen werden für beide Spieler gesetzt mit  
  - **`-red {human, random, simple, adv1, adv2, remote}`**
  - **`-blue {human, random, simple, adv1, adv2, remote}`**

#### Optionale Einstellungen (lokales Spiel)

- Mit dem Setzen von **`-delay <Zeit in Millisekunden>`** wird eine Verzögerung zwischen Zügen erzwungen. So kann zum Beispiel ein Spiel zwischen zwei Computerspielen welche manchmal mehrere Spielzüge in der Sekunde machen für den menschlichen Beobachter nachvollziehbar gemacht werden.
- Mit **`-load <Spielstandname>`** kann ein zuvor gespeicherter Spielstand wieder geladen werden. Beim Laden eines Spielstands muss die `-size`-Einstellung nicht gesetzt werden.
- Mit der Einstellung `-replay <Zeit in Millisekunden>` kann ein geladener Spielstand rudimentär Schritt für Schritt abgespielt werden, bis zu dem Punkt, an dem gespeichert worden ist. Die übergebene Zeit in Millisekunden beschreibt die Zeit zwischen den Zügen. Nachdem das Replay durchgelaufen ist, wird das Spiel an dem Punkt fortgesetzt, welcher durch den Spielstand beschrieben wird.
- Mit der Einstellung `-games <Anzahl Spiele>` wird der Versus-Mode gestartet. Zwei Spieler nehmen dabei an der gegebenen Anzahl an Spielen gegeneinander an, am Ende wird eine Statistik über die Anzahl der Siege und der durchschnittlichen Punktezahl ausgegeben. Ist `<Anzahl Spiele>` gleich 1 wird das Spiel wie normal gestartet.

#### Schalter (global)

- Mit dem Schalter `--debug` kann dem Logger angezeigt werden, dass auch Debug-Informationen mit aufgenommen und angezeigt werden sollen.
- Mit dem Schalter `--text` wird das Spiel nicht auf der graphischen Ausgabe angezeigt, sondern per ASCII auf der Standardausgabe.
- Mit dem Schalter `--help` wird eine verkürzte Hilfe zur Benutzung des Programms auf der Standardausgabe zurückgegeben. Das Programm wird dann sofort beendet, es ist also nicht möglich, die Hilfe aufzurufen und ein Spiel zu starten
- Mit dem Schalter `--quiet` wird der Output deaktiviert. Das Spielgeschehen kann dann nicht mehr mitverfolgt werden, jedoch kann ein interaktiver Spieler immer noch Züge eingeben, wenn der Input über die Standardeingabe abgefragt wird.

#### Notwendige Einstellungen (Netzwerkspiel)

Um einen Spieler im Netzwerk anzubieten, wird nur die Einstellung  
**`-offer <Spielertyp>`**  
benötigt. `Spielertyp` beschreibt dabei den Spielertypen, welcher im Netzwerk angeboten werden soll. Es werden keine weiteren Einstellungen benötigt (oder akzeptiert).

### Arten von Spielern

Es gibt eine Reihe von verschiedenen Typen von Spielern, welche entweder in einem lokalen Spiel verwendet oder mit `-offer <Spielertyp>` im Netzwerk angeboten werden können. Bis auf den Spielertypen `remote` können alle Spielertypen im Netzwerk angeboten werden.  
Die Spielertypen wie sie hier mit Namen genannt sind reflektieren genau die Bezeichnungen, mit welcher sie auf der Kommandozeile als Wert der Einstellung übergeben werden.

#### `human`

Ein Spieler diesen Typs wird direkt von einem Menschen kontrolliert. Der menschliche Spieler fragt Spielzüge direkt über die gewählte Eingabe ab. Im Normalfall ist dies die graphische Ein- und Ausgabe; wird der Schalter `--text` gesetzt werden Züge jedoch von der Standardeingabe übernommen.  
  Das Format eines Zuges ist der Dokumentation der `preset.Move`-Klasse zu entnehmen.

#### `random`

Dieser computergesteuerte Spieler wählt aus den dem Spieler zur Verfügung stehenden Spielzügen einen Zug zufällig aus.

#### `simple`

Dieser computergesteuerte Spieler wählt auf Basis eines einfachen Bewertungsalgorithmus zufällig einen der am höchsten bewerten Blumen-Züge aus. Wenn keine Blumen-Züge mehr möglich sind, macht der einfache Computerspieler so lange Graben-Züge bis das Spiel beendet wird.

#### `adv1`

Der verbesserte Computerspieler ersten Levels bedient sich einer angepassten Version des Bewertungsalgorithmus des einfachen Computerspielers. Außerdem führt dieser Computerspieler Graben-Züge, die zwei Gärten miteinander verbinden, sofort aus.

#### `adv2`

Der verbesserte Computerspieler zweiten Levels bedient sich einer angepassten Version des Bewertungsalgorithmus des verbesserten Computerspielers ersten Levels. Außerdem führt dieser Computerspieler Graben-Züge, die zwei Gärten miteinander verbinden, sofort aus. Zudem versucht dieser Computerspieler, die eigenen Blumen und Gärten in Clustern anzuordnen, sodass sich möglichst lange zusammenhängende Strukturen ergeben, welche die höchste Anzahl an Punkten bringt.

#### `remote`

Wird dieser Spielertyp gewählt, so versucht das Spiel einen im Netzwerk angebotenen entfernten Spieler anzufragen und zu verwenden. Es ist auch möglich, dass beide Spieler diesen Typ benutzen, eine so gestartete Instanz des Spiels agiert dann als dedizierter Server auf dem beide Spieler über das Netzwerk miteinander spielen können.

### Netzwerkspiel

Ein Netzwerkspiel lässt sich in zwei distinkte Arten kategorisieren:

1. Anbieten eines eigenen Spielers im Netzwerk
2. Finden und integrieren eines im Netzwerk angebotenen Spielers

#### Spieler anbieten

Wenn man einen Spieler im Netzwerk anbieten möchte, dann muss dies dem Spiel mit der Einstellung `-offer <Spielertyp>` mitgeteilt werden. `Spielertyp` beschreibt dabei die Spielerart, welche einer anderen Implementation angeboten werden soll.  
Falls ein eigener Spieler im Netzwerk angeboten wird, läuft die Verwaltung der Spiellogik beim Hauptprogramm der entfernten Implementation. Wird ein Spieler angeboten werden außerdem vom Benutzer ein Name, eine entfernte IP-Adresse und ein Port abgefragt. Der Port muss nicht explizit gesetzt werden, im Standardfall ist dieser `1099`, der RMI-Standardport.  
Obwohl das Spiel auf einer entfernten Instanz verwaltet wird, kann das Spielgeschehen lokal mit der graphischen Benutzeroberfläche verfolgt werden.

#### Spieler finden

Wird dem Programm beim Start mitgeteilt, dass mindestens einer der beiden Spieler den Typ `remote` hat, versucht das Spiel beim Starten, einen im Netzwerk angebotenen entfernten Spieler zu finden. Dazu werden über die Standardeingabe vom Benutzer die Adresse, der Port (standardmäßig 1099) und der Name des entfernten Spielers abgefragt.  
Kann kein entfernter Spieler gefunden werden, wird das Spiel beendet.

### Weiteres

#### Spiel aufgeben

Der menschliche Spieler kann zu jedem Zeitpunkt während seines eigenen Spielzugs das Spiel aufgeben. Das Aufgeben des einen Spielers wird dem anderen Spieler als Sieg angerechnet.

#### Speichern eines Spiels

Wird noch implementiert. TODO

#### Versus-Mode mit Statistik

Mit der Einstellung `-games <Anzahl Spiele>` wird der Versus-Mode gestartet. Zwei Spieler nehmen dabei an der gegebenen Anzahl an Spielen gegeneinander an, am Ende wird eine Statistik über die Anzahl der Siege und der durchschnittlichen Punktezahl ausgegeben. Ist `<Anzahl Spiele>` gleich 1 wird das Spiel wie normal gestartet.

Werden zum Versus-Modus noch die Schalter `--text` und `--quiet` gesetzt, kann der Versus-Mode zum einfachen Benchmarken von Computerspielern verwendet werden.

## Tabellarische Referenz

Parameter           |       Optionen                        |       Beschreibung
--------------------|---------------------------------------|----------------------
**Notwendig**       |                                       |
`-size`             |Zahl zwischen 5 und 30 (inklusive)     |Die Größe des Spielbretts wird auf den gegebenen Wert gesetzt
`-red`              |Einer der oben genannten Spielertypen  |Setzt den Spielertypen des roten Spielers auf den gegebenen Typen
`-blue`             |Einer der oben genannten Spielertypen  |Setzt den Spielertypen des blauen Spielers auf den gegebenen Typen
**Optional**        |                                       |
`-delay`            |Zeit in Millisekunden                  |Verzögerung in Millisekunden zwischen Spielzügen
`-load`             |Name des Spielstands, ohne Dateiendung |Lädt den gegebenen Spielstand und setzt das Spiel fort
`-replay`           |Zeit in Millisekunden                  |Der geladene Spielzug wird Zug für Zug ausgeführt, mit der gegebenen Verzögerung zwischen den Zügen
`-games`            |Anzahl an Spielen                      |Zwei Spieler treten in der gegebenen Anzahl von Spielen gegeneinander an. Am Ende wird eine Statistik ausgegeben
**Netzwerkspiel**   |                                       |
`-offer`            |Einer der oben genannten Spielertypen  |Bietet den angegebenen Spielertypen im Netzwerk an
**Schalter**        |                                       |
`--debug`           |Keine                                  |Falls gesetzt, werden Debug-Informationen vom Logger aufgenommen und ausgegeben
`--text`            |Keine                                  |Falls gesetzt, wird das Spielgeschehen auf der Standardausgabe angezeigt, und nicht auf der graphischen Anzeige
`--help`            |Keine                                  |Zeigt eine kurze Hilfe an und beendet das Programm
`--quiet`           |Keine                                  |Deaktiviert die Ausgabe des Spielbretts.

