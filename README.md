# **AppOintment**

**AppOintment** es una aplicación móvil desarrollada en **Kotlin** utilizando **Firebase** como base de datos. El proyecto sigue el patrón arquitectónico **MVVM (Modelo-Vista-Modelo de Vista)**, diseñado para proporcionar una experiencia organizada y eficiente tanto para clientes como para comercios.

---

## **Características principales**
- **Gestión de citas para clientes:**
  - Ver citas programadas.
  - Crear, modificar y cancelar citas.
- **Gestión de citas para comercios:**
  - Ver y gestionar las citas reservadas por los clientes.
  - Organizar horarios y disponibilidad.

---

## **Tecnologías utilizadas**
- **Lenguaje:** Kotlin  
- **Base de datos:** Firebase (Firestore para almacenamiento de datos en tiempo real)  
- **Arquitectura:** MVVM (Modelo-Vista-Modelo de Vista)  
- **Herramientas adicionales:**
  - Firebase Authentication (para la autenticación de usuarios).
  - Firebase Cloud Messaging (para notificaciones en tiempo real).

---

## **Estructura del proyecto**
La aplicación se organiza siguiendo el patrón MVVM, separando claramente la lógica de negocio, los datos y las interfaces de usuario:

- **Modelo (Model):**
  - Gestiona los datos de la aplicación (citas, usuarios, comercios).
  - Se conecta con Firebase para recuperar y guardar información.
- **Vista (View):**
  - Diseñada con Android XML y vistas dinámicas en Kotlin.
  - Proporciona una interfaz amigable tanto para clientes como para comercios.
- **Modelo de Vista (ViewModel):**
  - Actúa como intermediario entre el modelo y la vista.
  - Gestiona el estado de la aplicación y realiza las llamadas necesarias a Firebase.

---

## **Cómo instalar y ejecutar**
1. Clona el repositorio:  
   ```bash
   git clone https://github.com/usuario/appointment.git

2. Abre el proyecto en Android Studio.
3. Configura tu proyecto en Firebase:
    - Agrega el archivo google-services.json en la carpeta app.
    - Configura Firebase Authentication y Firestore en la consola de Firebase.
4. Ejecuta la aplicación en un dispositivo o emulador con Android 7.0 o superior.



---


# **AppOintment**

**AppOintment** is a mobile application developed in **Kotlin**, using **Firebase** as the database. The project follows the **MVVM (Model-View-ViewModel)** architectural pattern, designed to provide an efficient and user-friendly experience for both clients and businesses.

---

## **Key Features**
- **Appointment management for clients:**
  - View scheduled appointments.
  - Create, edit, and cancel appointments.
- **Appointment management for businesses:**
  - View and manage appointments booked by clients.
  - Organize schedules and availability.

---

## **Technologies Used**
- **Language:** Kotlin  
- **Database:** Firebase (Firestore for real-time data storage)  
- **Architecture:** MVVM (Model-View-ViewModel)  
- **Additional Tools:**
  - Firebase Authentication (for user authentication).
  - Firebase Cloud Messaging (for real-time notifications).

---

## **Project Structure**
The application is organized following the MVVM pattern, clearly separating business logic, data, and user interfaces:

- **Model:**
  - Manages application data (appointments, users, businesses).
  - Connects to Firebase to fetch and save information.
- **View:**
  - Designed with Android XML and dynamic views in Kotlin.
  - Provides a user-friendly interface for clients and businesses.
- **ViewModel:**
  - Acts as an intermediary between the model and the view.
  - Manages application state and performs necessary Firebase calls.

---

## **How to Install and Run**
1. Clone the repository:  
   ```bash
   git clone https://github.com/user/appointment.git
2. Open the project in Android Studio.
3. Configure your project in Firebase:
    - Add the google-services.json file in the app folder.
    - Set up Firebase Authentication and Firestore in the Firebase console.
4. Run the application on a device or emulator with Android 7.0 or higher.




