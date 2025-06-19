# GymTrackerAI – Frontend

---

## Descrizione Generale

GymTrackerAI è un'app Android sviluppata in **Kotlin** con **Jetpack Compose**, progettata per monitorare e classificare esercizi fisici utilizzando il sensore **SensorTile.Box PRO** via **Bluetooth Low Energy (BLE)**. I dati acquisiti vengono inviati in tempo reale al backend, che li elabora tramite un modello di Machine Learning esterno per riconoscere l'esercizio svolto.

---

## Architettura del Sistema

- **Linguaggio**: Kotlin  
- **UI Framework**: Jetpack Compose  
- **Gestione BLE**: ST BlueST SDK (connessione, ricezione dati da SensorTile.Box PRO)  
- **Gestione Stato**: ViewModel + State  
- **Navigazione**: Navigation Component  
- **Networking**: Retrofit per chiamate al backend  
- **Persistenza**: SharedPreferences per il JWT token  
- **Schermate principali**:
  - `HomeScreen`, `LoginScreen`, `WorkoutScreen`, `ReportScreen`, `HistoryActivityScreen`, `BLEScanScreen`
- **Manager e Helper**: `TrainingSessionManager`, `BleSessionManager`, `TokenManager`, `NavigationUtils`, `Validators`

---

## Dettagli Componenti Frontend

- Connettività BLE con **ST BlueST SDK**.
- Registrazione/Login/Recupero password.
- Sessione di allenamento con timer, conteggio ripetizioni e invio dati.
- Interfacce responsive in stile **Fitness App**.
- Schermate principali:
  - `HomeScreen`: video in loop, accesso/login/info.
  - `WorkoutScreen`: inizio allenamento reale.
  - `ReportScreen`: riepilogo sessione (tempo, esercizi, ripetizioni).
  - `HistoryActivityScreen`: storico degli allenamenti.

---

## Repository Correlati

- Frontend (questo repo): [github.com/tuo-user/gymtracker-frontend](https://github.com/tuo-user/gymtracker-frontend)
- Backend: [github.com/tuo-user/gymtracker-backend](https://github.com/tuo-user/gymtracker-backend)
- Machine Learning: [github.com/tuo-user/gymtracker-ml](https://github.com/tuo-user/gymtracker-ml)

