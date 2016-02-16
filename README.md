Text Mining Standford NER Trainer


Dieses Programm ist das Ergebnis des Praktikums für das Modul Text Mining im WS 15/16 an der Universität Leipzig.
Es wurde mit Maven realisiert. Die abhängigen Bibliotheken können der POM-Datei entnommen werden.
Zusätzlich wird das Python-Programm Wikipedia-Extractor (siehe http://medialab.di.unipi.it/wiki/Wikipedia_Extractor) benötigt.
Dies muss sich im Root-Ordner des Programm befinden.

Das Programm hat mehrere Aufgaben:
- Daten der Gruppe "Titel" in ein genormtes CSV-Format bringen
- Teilmenge der Artikel aus dem Wikipedia-XML-Dump extrahieren und in Klartext speichern
- Kategorien "Personen", "Orte", "Organisationen" im Klartext mit Hilfe eines Wörterbuchs
  (welches auf den CSV-Dateien beruht) markieren und in den NER-Format bringen


-- Benutzung --
Das Programm kann über die Main-Klasse im Root-Folder gestartet werden.
Hierfür kann man entweder Parameter angeben, die man mit dem Parameter "-h" erfahren kann
oder man startet das Programm ohne Parameter, worauf hin ein Menü ausgegeben wird, dass mit Eingaben gesteuert werden kann.

Achtung!
Die Option 'b' im Menü, bzw. der Parameter "-cxml" benötigt dringend Python 2, genauer die ausführbare python2-Anwendung in der PATH-Umgebung.
Sollte diese dort nicht vorhanden sein, oder sollte das Python-Programm an einem anderen Ort liegen, so sollte man diesen Schritt evtl. manuell ausführen.
Hierfür startet man ein Terminal im Ordner, in dem sich das Python-Programm befindet und führt folgenden Befehl aus:

python WikiExtractor.py -b 1G -o <Ergebnis-Ordner> <Pfad zur Datei, die extrahiert werden soll>

(hierfür muss Python 2 als Haupt-Version installiert sein!)
