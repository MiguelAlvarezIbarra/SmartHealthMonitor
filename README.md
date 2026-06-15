# SmartHealth Monitor
![Android CI](https://img.shields.io/badge/Android-API26+-green)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-MD3-blue)

Aplicación Android de monitoreo de salud personal en tiempo real.
Desarrollada como proyecto integrador — UTNG 9° Cuatrimestre 2025.

## Stack tecnológico
| Tecnología | Uso |
|---|---|
| Kotlin + Jetpack Compose | UI declarativa con Material Design 3 |
| Wearable Data Layer API  | Comunicación reloj ↔ teléfono (BLE) |
| Health Services API     | Sensor FC real en background (Wear OS) |
| Room Database           | Historial persistente de lecturas FC |
| Jetpack Navigation      | NavHost entre 4 pantallas |
| GitHub + Conventional Commits | Control de versiones profesional |

## Pantallas
| Pantalla | Descripción |
|---|---|
| LoginScreen | Autenticación con validación y State |
| DashboardScreen | FC y Pasos en tiempo real del wearable |
| HistorialScreen | Lecturas persistidas en Room con Flow reactivo |
| AlertaScreen | AlertDialog MD3 + Snackbar de confirmación |

## Capturas de pantalla
![Login](screenshots/login.jpeg)
![Dashboard](screenshots/dashboard.jpeg)
![Historial](screenshots/historial.jpeg)
![Alerta](screenshots/alerta.jpeg)
![Comprobacion](screenshots/comprobacion.jpeg)
![Wear](screenshots/wear.png)

## Autor
Miguel Angel Alvarez Ibarra — UTNG — Ing. en Desarrollo y Gestión de Software

## Unidad II — Wear OS
| Pantalla | Descripción |
|---|---|
| WearDashboardScreen | FC en tiempo real con ScalingLazyColumn y TimeText |
| WearHistorialScreen | Lista con Rotary Input (corona del reloj) |
| WearAlertaScreen    | Botones circulares de confirmación |
| SmartHealth WatchFace | Hora + FC en el WatchFace nativo |

- Watchface
<img width="269" height="266" alt="Captura de pantalla 2026-06-15 005352" src="https://github.com/user-attachments/assets/b9a6b8f6-e14f-45b7-8c34-6aa4a4fee62a" />

- Wear Dashboard
<img width="560" height="559" alt="Captura de pantalla 2026-06-12 140151" src="https://github.com/user-attachments/assets/3a72e727-d3a5-4b60-b684-1656045b4720" />
<img width="550" height="551" alt="Captura de pantalla 2026-06-12 140543" src="https://github.com/user-attachments/assets/d656ba4f-a9db-493a-8ce9-8ce30ef4f03e" />
<img width="707" height="500" alt="Captura de pantalla 2026-06-15 005308" src="https://github.com/user-attachments/assets/5949f574-2ceb-4f15-bfa9-2e86366d0b1c" />
