# Release-Notes

## 4.8.2026 (Version 0.4.1)
## Bugfix
- Encoding des S3-Identifier-Outputs an Fremdsystem ISO-8859-1 angepasst.  

## 30.7.2026 (Version 0.4.0)
## Aenderung
- Vorgangsanlage mit Erstellung Identifier und Ausgabedatei fuer die Druckstrasse.

## 9.7.2026
## Aenderung
- Anpassung an eAkte26 verwende Templates EH-Akte und EH-Vorgang.

## 27.5.2026
## Erweiterung
- Aktualisierung der gmm/xta-message-id Stati hinzugefuegt.

## 8.5.2026
## Aenderung
- Upgrade eAkte Version 1.2.5.
- Umstellung auf Schnittstellen-Verfahrensdaten (eAkte: Name, Vorname, Geburtsdatum).

## 6.5.2026
## Aenderung
- Umbennung EH-Antrag und Kassenzeichen in eAkte.

## 5.5.2026
## Aenderung
- Kassenzeichen in aktenzeichen.freitext uebernehmen.
- EMail Betreff konfigurierbar machen.
- Automatisches anlegen von GpId Bereichen in der eAktewenn nicht vorhanden.
- Base64 Kodierung beim BebPo Versand entfernt.

## 2.3.2026
## Aenderung
- Attachment Dateinamen fuer den Versand an xjustiz angepasst.

## 23.2.2026
### Refactoring
- Anpassung an modularisiertes xJustiz-Artefakt.
- xjustiz Version 3.5.1 --> 3.6.2

## 12.02.2026
### Hinzugefuegt
- Zentralaktkennung im Betreff der Einzelakte der eAkte anzeigen.

## 28.1.2026
### Aenderung
- PDFs ohne Metadaten werden aussortiert.
- Die Tatzeitraum Attribute anfangsdatum, anfangsuhrzeit, endedatum, endeuhrzeit sind unabhaengig voneinander und optional.

## 14.1.2026
- Erste Version zur Übermittlung (Versand) von xJustiz Nachrichten (NachrichtStrafOwiVerfahrensmitteilungExternAnJustiz0500010 ) mit dem Behördenpostfach.
