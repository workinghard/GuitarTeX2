# Breaking change in version 3.6! Server implementation has changed. Old server is no longer available. Please upgrade! 
# Server online: https://latex2pdf.nikolai-rinas.de - port can be left blank

## Build
You can build it on your platform with maven. I'm using Visual Studio Code with maven an openjdk package to build the release versions.

## Documentation
Details can be found [here](https://guitartex2.nikolai-rinas.de/)


## Release Notes
=============
### Version 3.6.0
  * New server implemented. Http based and finally secured connection. Because of that the old communication method is no longer supported.
  * New GTX client. If you want to build something similar yourself, feel free to use server_open.yaml as reference
     
### Version 3.5.0
  * New build pipeline with maven and VSC (optional)
  * Code cleanup
  * Dropped standalone mac app support
  * Standalone gtx converter available
  * Published build versions for Windows/Linux/Mac - x86
  * Guitartab command is now dynamic \textwidth (thanks for contribution @e-dschungel)
  * gtxtabs command changed. If you like the old version instead, please add following line to your song/book:

    ```\renewcommand{\gtxtabs}[2]{\makebox[0cm][l]{\raisebox{#1}{#2}}}```

### Version 3.4.0:
  * Code cleanup: Java 1.8 is required
  * App for Mac: Embedded Java runtime. Works standalone

### Version 3.3.1
  * Defaultwerte angepasst

### Version 3.3.0:
  * std:Ausgaben auf die interne Konsole umleiten
  * Harp-Notes hinzugefuegt
  * Parser: An die Harp-Notes angepasst
  * Shortcuts fuer Noten
  * Templates ueber menue zu erreichen
  * GuitarTeX2: Option "-f" removed  
  * GuitarTeX2Converter: UTF8 Support eingebaut
  * Direktivloser Text wird nicht geparsed
  * Link auf die Webseite mit den Shortcuts

### Version 3.2.5:
  * Configuration: Systeme werden mid regex erkannt
  * Konsole eingebaut
  * Template fuer neue Dokumente

### Version 3.2.4:
  * Sprachbutton hinzugefuegt
  * FAQ Link im Menue hinzugefuegt

### Version 3.2.3:
  * UTF8 Support

### Version 3.2.2:
  * Parser: Windows \r wird korrekt geloescht
  * Serverseitig: Windows-Dateien werden generell in Unix Format konvertiert  

### Version 3.2.1:
  * graphicx Packet entfernt
  * Schriftarten auf lmodern umgeschaltet
  * Neue Direktive {nopagenum}

