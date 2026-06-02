# MyAttendanceApp

## Control de Asistencia fácil, seguro y confiable.
objetivo lograr marcar asistencia de ingreso en 1 minuto
para Trabajadores Remotos

MyAttendanceApp es una solución tecnológica diseñada para gestionar y supervisar la asistencia de colaboradores que desempeñan sus funciones de manera remota. La aplicación móvil permite registrar entradas y salidas mediante geolocalización GPS y evidencia fotográfica, mientras que un panel web administrativo facilita la consulta de reportes y el seguimiento de la asistencia del personal.

---

##  Objetivo del Proyecto

Desarrollar una plataforma integrada que permita registrar y controlar la asistencia de trabajadores remotos de forma segura, confiable y en tiempo real, reduciendo los procesos manuales y mejorando la supervisión administrativa.

---

## 📱 Aplicación Móvil

La aplicación Android permite a los empleados:

* Iniciar sesión de forma segura.
* Registrar entrada y salida laboral.
* Capturar evidencia fotográfica (selfie).
* Obtener ubicación GPS automáticamente.
* Consultar historial de asistencias.
* Visualizar el detalle de cada registro.
* Gestionar información de perfil.
* Recibir futuras notificaciones y recordatorios.

---

##  Panel Web Administrativo

El sistema web permitirá a los administradores:

* Visualizar indicadores generales de asistencia.
* Gestionar empleados.
* Consultar registros de asistencia.
* Visualizar ubicaciones registradas en mapas.
* Generar reportes de asistencia.
* Administrar estados y permisos de usuarios.

---

## Tecnologías Utilizadas

### Aplicación Móvil

* Kotlin
* Android Studio
* Material Design 3
* ViewBinding
* Retrofit (API REST)
* GPS Location Services
* Cámara del dispositivo

### Backend API

* REST API
* JSON
* Arquitectura Cliente-Servidor

### Base de Datos

* MySQL
* Modelo Relacional
* Integridad Referencial mediante Foreign Keys

### Panel Administrativo

* HTML5
* CSS3
* JavaScript
* Bootstrap
* Consumo de APIs REST

---

## 🗄️ Modelo de Datos

El sistema está compuesto principalmente por las siguientes entidades:

* Usuarios
* Roles
* Estados de Usuario
* Asistencias
* Estados de Asistencia

Cada registro de asistencia almacena:

* Fecha
* Hora de entrada
* Hora de salida
* Horas trabajadas
* Latitud y longitud
* Dirección obtenida por GPS
* Fotografía de entrada
* Fotografía de salida

---

##  Reglas de Negocio

* Un usuario solo puede registrar una entrada por día.
* No es posible registrar una salida sin haber registrado previamente una entrada.
* El correo electrónico debe ser único.
* Cada asistencia está asociada a un usuario y un estado.
* Se registra evidencia fotográfica y geolocalización para validar la asistencia.

---

## Estructura General del Proyecto

```text
MyAttendanceApp
│
├── app/                     # Aplicación Android
├── docs/                    # Documentación
│   ├── informe/
│   ├── database/
│   └── mockups/
│
├── gradle/
├── README.md
└── database.sql
```

---

## Estado del Proyecto

Actualmente en desarrollo.

### Módulos planificados

* [x] Diseño de Base de Datos
* [x] Diseño UI/UX
* [x] Configuración del proyecto Android
* [ ] Módulo de autenticación
* [ ] Registro de asistencia
* [ ] Integración GPS
* [ ] Captura de fotografías
* [ ] Consumo de API REST
* [ ] Panel Web Administrativo
* [ ] Reportes y estadísticas
* [ ] Despliegue final

---

##  Equipo de Desarrollo

Proyecto académico desarrollado como solución para el control de asistencia de trabajadores remotos mediante tecnologías móviles y web.

---

## 📄 Licencia

Proyecto con fines académicos y educativos.
