# Methoden

## Scrambling

### scrambling

-   Stelle in pi

### entscrambling

-   Zahlen nach Stelle in pi

## Transposition

-

## Schlüsselgenerator

-

## Hashfunktion

-   Division

# Ablauf

## Block Cipher

### Schlüsselgeneration

-   in Hashfunktion
-   in Schlüsselgenerator

### Verschlüsselung

1. Eingabe:
    1. in Binärzahl konvertieren
    2. in Blöcke einteilen
    3. Blöcke scramblen
2. Schleife:
    1. Blöcke transponieren
    2. xor
3. Ausgabe:
    1. Blöcke auflösen
    2. in nettes Format konvertieren

### Entschlüsselung

1. Eingabe:
    1. in Binärzahl konvertieren
    2. in Blöcke einteilen
2. Schleife:
    1. xor mit rückwärts Schlüssel
    2. blöcke enttransponieren
3. Ausgabe
    1. Blöcke entscramblen
    2. Blöcke auflösen
    3. in nettes Format konvertieren

## Stream Cipher

### Schlüsselgeneration

### Verschlüsselung/Entschlüsselung

1. Stream in Blöcke einteilen
2. xor mit einzelnen Blöcken <- Schlüssel mit Blocknummer
