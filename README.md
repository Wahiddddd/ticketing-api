# Ticketing API Service

## Latar Belakang
Proyek ini dikembangkan sebagai solusi backend untuk sistem manajemen tiket event yang modern dan skalabel. Fokus utama dari API ini adalah memberikan pengalaman pemesanan tiket yang aman (Atomic Transaction) dan manajemen operasional admin yang efisien. Sistem ini menangani alur dari registrasi user, pencarian event, pembelian tiket dengan validasi stok real-time, hingga sistem refund dan pelaporan pendapatan yang mendalam bagi administrator.

## Fitur Utama
- **User Management**: Registrasi, Login (JWT), dan Role-Based Access (ADMIN, USER).
- **Event Management**: CRUD Event dengan kategori tiket, kuota, dan harga dinamis.
- **Booking System**: Pembelian tiket dengan validasi stok (Atomic) dan saldo user.
- **Admin Workflow**:
    - **Move Seat**: Pemindahan kursi user (Seat Swapping) yang aman.
    - **Quick Disable**: Menonaktifkan tiket/kursi yang rusak.
    - **Admin Refund**: Pengembalian saldo paksa oleh admin dengan pemulihan kuota otomatis.
- **Reporting**: Laporan pendapatan bersih dan tren penjualan tiket (Bulanan/Global).
- **Pro Handling**: Global Exception Handling (JSON error response) dan Logging Terstruktur (SLF4J).

## Tech Stack
- **Backend**: Java 17, Spring Boot 3
- **Security**: Spring Security, JWT (JSON Web Token)
- **Database**: MySQL (Production), H2 (Testing)
- **Library**: Lombok, MapStruct (optional), Validation
- **Logging**: SLF4J with Logback

## Flowchart & Desain Basis Data

### 1. Desain Database
![Desain Database](./Flow%20Chart/Desain%20Database.drawio.png)

### 2. Autentikasi (Register & Login)
| Register User | Login System |
| :--- | :--- |
| ![Register](./Flow%20Chart/Register%20User.drawio.png) | ![Login](./Flow%20Chart/Login.drawio.png) |

### 3. Flow Pemesanan & Monitoring (User)
| Booking Event | Monitoring Tiket |
| :--- | :--- |
| ![Booking](./Flow%20Chart/Event%20&%20Booking%20User.drawio.png) | ![Monitoring](./Flow%20Chart/Monitoring%20(tiket)%20User.drawio.png) |

### 4. Manajemen Admin (Event & Tiket)
| Add Event | Edit Event | Delete Event |
| :--- | :--- | :--- |
| ![Add](./Flow%20Chart/Manajement%20event%20(Add%20Event).drawio.png) | ![Edit](./Flow%20Chart/Manajement%20event%20(Edit%20Event).drawio.png) | ![Delete](./Flow%20Chart/Manajement%20Event%20(Delete%20Event).drawio.png) |

### 5. Manajemen Tiket & Refund
![Manajemen Tiket](./Flow%20Chart/Manajement%20Ticket.drawio.png)

### 6. Laporan & Analisis
![Laporan](./Flow%20Chart/Laporan%20&%20analisis%20admin%20drawio.png)

## Cara Menjalankan
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

