# Sales Management App

## Overview

The Sales Management App is an Android application developed using Kotlin in Android Studio. It helps businesses record, manage, and monitor sales transactions efficiently. The application provides a simple and user-friendly interface for storing sales records, updating existing records, viewing sales information, and deleting unwanted entries.

This project was developed as part of an Android Mobile Application Development course to demonstrate CRUD operations, local database management, unit testing, and debugging techniques.

---

## Features

- Add new sales records
- View all sales records
- Update existing sales records
- Delete sales records
- Search sales information (optional)
- Local database storage using SQLite/Room Database
- Simple and user-friendly interface
- Input validation
- Error handling

---

## Technologies Used

- Kotlin
- Android Studio
- Android SDK
- SQLite / Room Database
- Material Design Components
- JUnit for Unit Testing

---

## System Requirements

- Android Studio Hedgehog or newer
- Android SDK 24+
- Kotlin 1.9+
- Android device or emulator running Android 7.0 (Nougat) or later

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/JosphatRoy/sales-management-app.git
```

2. Open Android Studio.

3. Select **Open an Existing Project**.

4. Navigate to the cloned project folder.

5. Allow Gradle to synchronize.

6. Build the project.

7. Run the application on an emulator or physical Android device.

---

## Project Structure

```
app/
│
├── java/
│   ├── activities/
│   ├── database/
│   ├── models/
│   ├── adapters/
│   └── utils/
│
├── res/
│   ├── layout/
│   ├── drawable/
│   ├── values/
│   └── mipmap/
│
└── AndroidManifest.xml
```

---

## CRUD Operations

### Create

Allows users to add a new sales record.

### Read

Displays all stored sales records.

### Update

Allows modification of existing sales records.

### Delete

Removes unwanted sales records from the database.

---

## Database

The application stores sales information locally using SQLite/Room Database.

Typical fields include:

- Sale ID
- Product Name
- Quantity
- Unit Price
- Total Amount
- Sale Date

---

## Unit Testing

JUnit tests were implemented to verify application functionality, including:

- Insert sales record
- Update sales record
- Delete sales record

---

## Debugging

Android Studio debugging tools were used, including:

- Logcat
- Breakpoints
- Variable Inspection
- Stack Trace Analysis

---


---

## Future Improvements

- User authentication
- Cloud database integration
- Sales analytics and reports
- Export sales to PDF or Excel
- Barcode scanning
- Dashboard with charts
- Dark mode support
- Backup and restore functionality

---

## Lessons Learned

During development, the following skills were gained:

- Android application development using Kotlin
- Implementing CRUD operations
- Working with SQLite/Room Database
- Android UI design
- Writing unit tests using JUnit
- Debugging using Android Studio tools
- Version control using Git and GitHub

---

## Author

**Name:** Roy

**Course:** BIT4107 – Mobile Application Development

**Project:** Sales Management App

**Institution:** *(Mount Kenya University)*

---

## License

This project was developed for educational purposes only.
