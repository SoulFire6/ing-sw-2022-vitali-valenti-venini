# Peer-Review 1: UML

Marco Vitali, Daniele Valenti, Callum Venini

Gruppo 53

Valutazione dei diagrammi di model, controller e network del gruppo 53.

## Lati positivi

- Tile: usare una classe astratta è un buon metodo per evitare duplicazioni di codice
- Student: è possibile distinguere gli student disk
- Cloud: contiene il minimo indispensabile, è una classe compatta
- Isle: usano una strategy per il calcolo dell’influenza
- Board: sono gestiti i limiti dell’entrance e dining room
- Usare un client model è un ottimo modo per passare solo i dati necessari alla client view
- ActionTurnHandler: buon utilizzo del pattern Strategy in CheckProfessorStrategy
- Action phase: sequence diagram relativo allo spostamento di studenti chiaro e corretto
- Character sequence diagram: chiaro e corretto

## Lati negativi

- Enum: faction ha un valore di troppo, che si potrebbe rimpiazzare nel codice con un null
- Student: è più efficiente usare un hashmap per i calcoli
- Cloud: dal modello non è chiaro se viene controllato il numero massimo di student disk, potrebbe anche essere nel controller
- Isle: il calcolo dell’influenza è nel modello, ma dovrebbe essere nel controller
- AggregatedIsland: contiene metodi duplicati da Isle e in più eredità da Isle, il join dovrebbe essere in un controller
- Board: la dining room è gestita come array di int
- Team: si potrebbe rimpiazzare con una reference all’altro giocatore del proprio team, per quanto sono possibili team da 1 o 2 persone massimo
- GameModel: le carte personaggio attivo vengono gestita come arraylist dentro il model
- Non chiaro l’utilizzo di MoveMnStrategy
- Phase non è chiaro (non si capisce se sono le fasi del gioco o altro, nel caso manca la scelta di chi inizia)
- Planning phase: dal diagramma sembra che se c’è un errore nella scelta della carta assistente da parte di un giocatore si prosegue facendo giocare la carta assistente ai giocatori seguenti, tornando in un secondo momento a far inserire la carta al primo giocatore (quando invece dopo l’errore bisognerebbe richiedere l’input al giocatore corrente fino a quando è valido)
- Action phase: sembra manchino dei controlli sulla fase relativa al movimento di madre natura: ad esempio il messaggio d’errore qualora il numero di movimenti specificato sia maggiore del numero consentito. Inoltre è probabilmente richiesto l’invio di ClientBoard al client, necessario quando il numero di torri sulla Board varia.
- Per la fase relativa al CloudChoiceMessage invece, manca l’errore riguardante il caso in cui la Cloud scelta sia vuota; è necessario inviare al client anche la Board, dato che sarà modificata l’Entrance aggiungendo ad essa gli studenti presi dalla Cloud.
- Attenzione che in molti posti le arità sono invertite

## Confronto tra le architetture

Anche noi usiamo una classe astratta Tile nel modello per gestire gli student disk senza ripetere codice, quindi sembra un’ottima idea.
Abbiamo implementato anche noi delle classi messaggio per identificare le varie mosse.

Gestiamo le isole in modo differente, con una classe al posto di due che identifica più isole nel caso in cui ci siano più torri.
Noi abbiamo più classi controller che gestiscono varie parti del model, separando dunque la logica del gioco.
