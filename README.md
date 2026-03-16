# LeadEstate - Sistem Follow-Up & Reminder Lead Penjualan Rumah

> **Mata Kuliah:** Rekayasa Perangkat Lunak / IMK  
> **Dosen Pengampu:** Muhammad Shiddiq Azis, S.T., MBA  
> **Tahun:** 2026

---

## Deskripsi Proyek

LeadEstate adalah aplikasi berbasis web yang dirancang untuk membantu tim sales properti dalam mengelola calon pembeli rumah (lead).  
Sistem ini memungkinkan sales untuk melakukan follow-up secara terstruktur, mencatat aktivitas komunikasi, serta mendapatkan pengingat otomatis agar tidak melewatkan peluang penjualan.

Fitur utama sistem ini meliputi:

- Reminder follow-up otomatis (H+1, H+3, H+7)
- Template pesan WhatsApp / Email
- Log aktivitas follow-up
- Status respon lead
- Notifikasi kepada sales
- Tracking closing rate per sales

---

# Perancangan Sistem (DFD)

## DFD Level 0 (Context Diagram)

![DFD Level 0](assets/dfdlv0.jfif)

Diagram konteks yang menggambarkan interaksi antara pengguna eksternal seperti Admin dan Sales dengan sistem LeadEstate.

---

## DFD Level 1

![DFD Level 1](assets/dfd1.png)

Diagram ini menjelaskan proses utama dalam sistem seperti:

1. Management Data Lead
2. Management Database
3. Sistem Reminder dan Follow-Up
4. Update Status Lead
5. Laporan & Closing Rate

---

# Mockup Antarmuka

Rancangan antarmuka dibuat menggunakan Figma dengan fokus pada kemudahan penggunaan (usability) bagi tim sales.

| Login Page | Dashboard | Halaman Lead |
| :--------: | :-------: | :----------: |
| ![Login](assets/login.png) | ![Dashboard](assets/dashboard.png) | ![Lead](assets/lead.png) |

---

# Stack Teknologi

Teknologi yang digunakan dalam pengembangan sistem ini:
- **Frontend:** ReactJS (Vite)
- **Backend:** Java (Spring Boot)
- **Database:** MySQL
- **UI Design:** Figma
- **Version Control:** GitHub

# Arsitektur Sistem

Aplikasi LeadEstate menggunakan arsitektur **client-server**.

Frontend dibangun menggunakan React (Vite) yang berkomunikasi dengan backend melalui REST API.

Backend menggunakan Spring Boot yang menangani:
- Business logic
- API endpoint
- Integrasi database

Database menggunakan MySQL untuk menyimpan data:
- Lead
- Sales
- Follow-up
- Aktivitas komunikasi

---

# Role Pengguna

### Admin
- Mengelola data sales
- Mengelola data properti
- Mengelola template pesan
- Melihat laporan closing rate
- Melihat seluruh data lead
- Mengedit atau menghapus data lead
- Menentukan lead dialokasikan ke sales tertentu

### Sales
- Melihat data lead yang ditugaskan
- Menambahkan lead baru (calon pembeli)
- Mengedit data lead
- Melakukan follow-up
- Mengubah status lead
- Melihat reminder follow-up
- Melihat riwayat follow-up

---

# Struktur Fitur Sistem

Beberapa fitur utama dalam aplikasi:

1. **Manajemen Lead**
   - Menyimpan data calon pembeli
   - Mengelola status lead

2. **Follow-Up Lead**
   - Mencatat aktivitas komunikasi dengan calon pembeli
   - Mengupdate hasil follow-up

3. **Reminder Otomatis**
   - Sistem memberikan pengingat follow-up pada H+1, H+3, dan H+7

4. **Laporan Penjualan**
   - Menampilkan statistik closing rate tiap sales

---

# Cara Menjalankan Project

## Menjalankan Backend
1. cd backend
2. ./mvnw spring-boot:run
3. Backend akan berjalan di: http://localhost:8080

---

## Menjalankan Frontend
1. cd frontend
2. npm install
3. npm run dev
4. Frontend akan berjalan di: http://localhost:5173
