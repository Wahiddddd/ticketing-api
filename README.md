## 🚀 Project Overview

**Nama Proyek:** Ticketing API Service

### Deskripsi Proyek
Ticketing API Service adalah platform backend berbasis Spring Boot yang dirancang untuk menangani seluruh siklus hidup manajemen tiket event. Mulai dari pendaftaran pengguna, pencarian event, hingga transaksi pembelian tiket yang aman dan pelaporan administratif yang komprehensif.

### Target Pengguna & Pasar
- **Event Organizers:** Membutuhkan sistem handal untuk mengelola kuota dan memantau pendapatan.
- **End Users:** Individu yang menginginkan kemudahan dalam mencari dan membeli tiket event secara aman.
- **System Administrators:** Tim yang bertanggung jawab atas operasional teknis seperti manajemen kursi dan refund darurat.

### Masalah & Kebutuhan yang Diatasi (Problem & Solution)
- **Integritas Data Transaksi:** Mencegah *race conditions* saat pembelian tiket simultan melalui **Atomic Transactions**.
- **Efisiensi Operasional:** Menyederhanakan proses pembatalan dan refund dengan **Otomatisasi Pemulihan Kuota**.
- **Transparansi Data:** Mengatasi kesulitan dalam pelaporan keuangan dengan **Sistem Reporting Terpusat**.

---

## 📂 Project Structure

```text
ticketing-api/
├── src/
│   ├── main/
│   │   ├── java/com/bootcamp/ticketing_api/
│   │   │   ├── config/             # Konfigurasi Security, JWT, dll.
│   │   │   ├── controller/         # REST Controllers
│   │   │   ├── DTO/                # Data Transfer Objects
│   │   │   ├── entity/             # JPA Entities (Database Models)
│   │   │   ├── exception/          # Global Exception Handling
│   │   │   ├── repository/         # Data Access Layer
│   │   │   └── service/            # Business Logic Layer
│   │   └── resources/
│   │       ├── db/migration/       # Database Migrations (SQL)
│   │       └── application.properties
│   └── test/                       # Unit & Integration Tests
├── Dockerfile                      # Build image settings
├── docker-compose.yml              # Container orchestration
└── pom.xml                         # Maven dependencies
```

---

## 🐳 Running with Docker

Proyek ini telah dikonfigurasi untuk dijalankan dengan Docker Compose, yang akan mengatur aplikasi dan database MySQL secara otomatis.

### Prasyarat
- Docker Desktop terinstal.

### Langkah-langkah
1.  **Build & Run:**
    Jalankan perintah berikut di root direktori proyek:
    ```bash
    docker-compose up --build
    ```
2.  **Akses Aplikasi:**
    - API: `http://localhost:8085`
    - Database (Host): `localhost:3307` (Internal: 3306)

### Variabel Lingkungan (Environment Variables)
Konfigurasi utama di `docker-compose.yml`:
- `MYSQL_DATABASE`: `db_ticketing`
- `MYSQL_ROOT_PASSWORD`: `dibimbing123`
- `JWT_SECRET`: (Sudah terkonfigurasi secara default)

---

## Cara Menjalankan (Tanpa Docker)
1. Clone repository ini.
2. Pastikan MySQL berjalan (Konfigurasi ada di `src/main/resources/application.properties`).
3. Jalankan build:
   ```bash
   mvn clean install
   ```
4. Jalankan aplikasi:
   ```bash
   mvn spring-boot:run
   ```

## 📝 Dokumentasi API

Akses API menggunakan `Base URL`: `http://localhost:8085`

### 1. Autentikasi & Akun
| Method | Endpoint | Role | Deskripsi |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Registrasi akun user baru. |
| `POST` | `/api/auth/login` | Public | Login untuk mendapatkan JWT Token. |
| `POST` | `/api/auth/admin/create` | SUPER_ADMIN | Membuat akun Admin baru. |
| `GET` | `/api/users/me` | USER | Melihat profil diri sendiri. |
| `POST` | `/api/users/topup` | USER | Melakukan top-up saldo akun. |

### 2. Manajemen Event (Public & Admin)
| Method | Endpoint | Role | Deskripsi |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/events` | Public | List event aktif (mendukung search & filter). |
| `GET` | `/api/events/{id}` | Public | Detail event lengkap beserta kategori tiket. |
| `GET` | `/api/admin/events` | ADMIN | List semua event untuk manajemen admin. |
| `POST` | `/api/admin/events` | ADMIN | Membuat event baru. |
| `PUT` | `/api/admin/events/{id}` | ADMIN | Memperbarui data event. |
| `DELETE` | `/api/admin/events/{id}` | ADMIN | Menghapus event secara permanen. |

### 3. Pemesanan & Tiket (User)
| Method | Endpoint | Role | Deskripsi |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/bookings` | USER | Memesan tiket (validasi stok & saldo otomatis). |
| `GET` | `/api/tickets/my-tickets` | USER | Melihat semua tiket yang pernah dibeli. |
| `PATCH` | `/api/tickets/{id}/cancel` | USER | Membatalkan tiket (Refund saldo dilakukan otomatis). |

### 4. Manajemen Admin & Operasional (Super Admin)
| Method | Endpoint | Role | Deskripsi |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/superadmin/admins` | SUPER_ADMIN | List semua akun dengan role Admin. |
| `PUT` | `/api/superadmin/admins/{id}` | SUPER_ADMIN | Mengedit data akun Admin. |
| `DELETE` | `/api/superadmin/admins/{id}` | SUPER_ADMIN | Menghapus akun Admin. |
| `PATCH` | `/api/superadmin/tickets/{code}/disable` | ADMIN | Menonaktifkan tiket (Status: BROKEN). |
| `PATCH` | `/api/superadmin/tickets/{code}/move-seat` | ADMIN | Pemindahan nomor kursi user. |
| `POST` | `/api/superadmin/tickets/{code}/refund` | ADMIN | Refund paksa oleh admin (Emergency). |

### 5. Laporan & Monitoring (Admin)
| Method | Endpoint | Role | Deskripsi |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reports/event/{id}` | ADMIN | Laporan pendapatan per-event spesifik. |
| `GET` | `/api/reports/summary` | ADMIN | Ringkasan pendapatan global dengan filter tanggal. |

---
*Untuk pengujian lebih detail, Anda dapat mengimpor koleksi Postman yang sesuai dengan spesifikasi di atas *

`Dokumentasi Postman:`
`https://www.postman.com/bootcamp-qa-digital-skola-batch-7/project-bootcamp-dibimbing/request/23091047-7a1dcb6a-abab-467b-ad1f-a3b3f38f8184?sideView=agentMode`

