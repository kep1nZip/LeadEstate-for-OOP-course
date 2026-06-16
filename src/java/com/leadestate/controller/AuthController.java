package com.leadestate.controller;

import com.leadestate.dao.UserDAO;
import com.leadestate.model.Admin;
import com.leadestate.model.Sales;
import com.leadestate.model.User;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Kelas AuthController.
 *
 * Servlet yang menangani seluruh proses autentikasi pada sistem
 * LeadEstate, sesuai dengan Fitur Manajemen Akses (User & Auth) pada
 * proposal:
 * <ul>
 *   <li>Login</li>
 *   <li>Register (Admin/Sales)</li>
 *   <li>Logout</li>
 *   <li>Forgot Password (versi sederhana)</li>
 * </ul>
 *
 * <p>Controller ini memanggil method-method yang sudah didefinisikan
 * pada kelas {@link User} (login, register, logout, resetPassword)
 * dan menggunakan {@link UserDAO} untuk mencari/menyimpan data User.</p>
 *
 * <p>Aksi (action) ditentukan melalui parameter request bernama
 * {@code action}, dikirim dari form pada halaman JSP, contoh:</p>
 * <pre>
 * &lt;form action="AuthController" method="post"&gt;
 *     &lt;input type="hidden" name="action" value="login" /&gt;
 *     ...
 * &lt;/form&gt;
 * </pre>
 *
 * <p><b>Catatan scope:</b> Pada class diagram, User.register() hanya
 * memiliki parameter (name, email, password) — belum ada field nomor
 * HP seperti pada user flow Register 3-step di proposal. Sehingga
 * pada controller ini, field nomor HP belum diproses. Jika field
 * tersebut ingin ditambahkan, perlu penyesuaian pada kelas User/Sales
 * terlebih dahulu.</p>
 *
 * <p>TODO: Saat Service layer dibuat, sebagian logika validasi pada
 * method handleLogin/handleRegister di bawah ini dapat dipindahkan ke
 * Service agar Controller lebih sederhana (tugas Controller cukup
 * menerima request &amp; menampilkan response).</p>
 */
@WebServlet(name = "AuthController", urlPatterns = {"/AuthController", "/login"})
public class AuthController extends HttpServlet {

    /** DAO untuk mengakses data User (Admin & Sales). */
    private UserDAO userDAO;

    /**
     * Inisialisasi servlet. Dipanggil sekali oleh container saat
     * servlet pertama kali dimuat.
     */
    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
    }

    /**
     * Menangani request POST. Seluruh aksi autentikasi (login,
     * register, logout, forgotPassword) dikirim melalui POST dari
     * form pada halaman JSP, dibedakan berdasarkan parameter
     * {@code action}.
     *
     * @param request  request dari client.
     * @param response response ke client.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {

    case "login":
        handleLogin(request, response);
        break;

    case "register":
        handleRegister(request, response);
        break;

    case "logout":
        handleLogout(request, response);
        break;

    case "forgotPassword":
        handleForgotPassword(request, response);
        break;

    case "verifyOtp":
        handleVerifyOtp(request, response);
        break;

    case "changePassword":
        handleChangePassword(request, response);
        break;
    case "updateProfile":
    handleUpdateProfile(request, response);
    break;

    default:
        response.sendRedirect("index.jsp");
        break;
}
    }

    /**
     * Menangani request GET.
     *
     * Logout biasanya diakses melalui tautan (link, method GET),
     * sehingga ditangani khusus di sini. Aksi lain (login, register,
     * forgotPassword) tetap diarahkan ke doPost agar logikanya tidak
     * duplikat.
     *
     * @param request  request dari client.
     * @param response response ke client.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            handleLogout(request, response);
        } else {
            doPost(request, response);
        }
    }

    // =========================================================================
    // HANDLER PER AKSI
    // =========================================================================

    /**
     * Menangani proses login.
     *
     * Alur:
     * <ol>
     *   <li>Cari User berdasarkan email via {@code userDAO.findByEmail()}.</li>
     *   <li>Jika ditemukan, validasi password via {@code user.login()}.</li>
     *   <li>Jika berhasil, simpan User &amp; role ke session, lalu
     *       arahkan ke dashboard sesuai role.</li>
     *   <li>Jika gagal, tampilkan pesan error di login.jsp.</li>
     * </ol>
     *
     * @param request  request dari client (parameter: email, password).
     * @param response response ke client.
     */
private void handleLogin(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String email = request.getParameter("email");
    String password = request.getParameter("password");

    System.out.println("EMAIL INPUT = " + email);
    System.out.println("PASSWORD INPUT = " + password);

    System.out.println("\n========== LOGIN DEBUG ==========");
    System.out.println("Email Input    : " + email);
    System.out.println("Password Input : " + password);

    User user = userDAO.findByEmail(email);

    if (user == null) {

        System.out.println("STATUS         : USER TIDAK DITEMUKAN");

        request.setAttribute("errorMessage",
                "Email tidak ditemukan di database.");

        request.getRequestDispatcher("index.jsp")
               .forward(request, response);
        return;
    }

    System.out.println("User Found     : " + user.getName());
    System.out.println("User ID        : " + user.getId());
    System.out.println("Role ID        : " + user.getRoleId());
    System.out.println("Role Name      : " + user.getRoleName());
    System.out.println("Password DB    : " + user.getPassword());

    boolean loginSuccess =
            password != null &&
            password.equals(user.getPassword());

    System.out.println("Login Result   : " + loginSuccess);

    if (loginSuccess) {

        HttpSession session = request.getSession();

        session.setAttribute("user", user);
        session.setAttribute("role", user.getRoleName());
        session.setAttribute("userId", user.getId());
        session.setAttribute("roleId", user.getRoleId());
        session.setAttribute("namaUser", user.getName());
        session.setAttribute("userLogin", user);

        if (Admin.ROLE_NAME.equalsIgnoreCase(user.getRoleName())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/reminder");
        }

    } else {

        request.setAttribute("errorMessage",
                "Email atau password salah.");

        request.getRequestDispatcher("index.jsp")
               .forward(request, response);
    }
}
    /**
     * Menangani proses registrasi User baru (Admin atau Sales).
     *
     * Alur:
     * <ol>
     *   <li>Validasi field wajib (nama, email, password) tidak kosong.</li>
     *   <li>Validasi konfirmasi password sama dengan password.</li>
     *   <li>Validasi email belum terdaftar via {@code userDAO.isEmailExist()}.</li>
     *   <li>Buat objek Admin/Sales sesuai parameter {@code role}.</li>
     *   <li>Panggil {@code user.register(name, email, password)} sesuai
     *       method pada class diagram, lalu simpan via
     *       {@code userDAO.save()}.</li>
     * </ol>
     *
     * @param request  request dari client (parameter: name, email,
     *                 password, confirmPassword, role).
     * @param response response ke client.
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role"); // "Admin" atau "Sales"

        // 1. Validasi field wajib
       if (name == null || name.trim().isEmpty()
        || email == null || email.trim().isEmpty()
        || password == null || password.trim().isEmpty()) {
    request.setAttribute("errorMessage",
            "Nama, email, dan password wajib diisi.");
    request.setAttribute("activeTab", "register");
    request.getRequestDispatcher("index.jsp")
           .forward(request, response);
    return;
}
        // 2. Validasi konfirmasi password
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Konfirmasi password tidak sama dengan password.");
            request.setAttribute("activeTab", "register");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        // 3. Validasi email belum terdaftar
        if (userDAO.isEmailExist(email)) {
            request.setAttribute("errorMessage", "Email sudah terdaftar, silakan gunakan email lain.");
            request.setAttribute("activeTab", "register");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        // 4. Buat objek User sesuai role yang dipilih.
        //    id di-set 0 dahulu, akan digenerate otomatis oleh UserDAO.save().
        User userBaru;
        if (Admin.ROLE_NAME.equalsIgnoreCase(role)) {
            userBaru = new Admin(0, name, email, password);
        } else {
            // Default ke Sales jika role tidak dikenali / dipilih Sales.
            userBaru = new Sales(0, name, email, password);
        }

        // 5. Panggil register() sesuai method pada class diagram User.
        userBaru.register(name, email, password);

        // 6. Simpan User baru via DAO.
        userDAO.save(userBaru);

        request.setAttribute("successMessage", "Registrasi berhasil! Silakan login.");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    /**
     * Menangani proses logout.
     *
     * Jika ada User yang sedang login (tersimpan di session), method
     * {@code user.logout()} dipanggil terlebih dahulu (sesuai class
     * diagram), kemudian session di-invalidate dan user diarahkan
     * kembali ke halaman login.
     *
     * @param request  request dari client.
     * @param response response ke client.
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                user.logout();
            }
            session.invalidate();
        }

        response.sendRedirect("index.jsp");
    }

    /**
     * Menangani proses lupa password (versi sederhana).
     *
     * Mencari User berdasarkan email, lalu memanggil
     * {@code user.resetPassword(email)} sesuai method pada class
     * diagram User.
     *
     * <p>TODO: Implementasi OTP 6 digit (berlaku 5 menit) sesuai user
     * flow pada proposal akan ditambahkan di lapisan Service saat
     * Service/Database tersedia. Untuk saat ini, method ini hanya
     * memanggil {@code resetPassword()} placeholder pada User.</p>
     *
     * @param request  request dari client (parameter: email).
     * @param response response ke client.
     */
    private void handleForgotPassword(HttpServletRequest request,
                                  HttpServletResponse response)
        throws ServletException, IOException {

    String email = request.getParameter("email");

    User user = userDAO.findByEmail(email);

    if (user == null) {
    request.setAttribute("errorMessage", "Email tidak ditemukan.");
    request.setAttribute("step", "1"); // WAJIB TAMBAHKAN INI agar tetap di form email (Step 1)
    
    request.getRequestDispatcher("/WEB-INF/views/ForgotPassword.jsp").forward(request, response);
    return;
}

    String otp = user.resetPassword(email);

    HttpSession session =
            request.getSession();

    session.setAttribute(
            "resetOtp",
            otp
    );

    session.setAttribute(
            "resetEmail",
            email
    );

    request.setAttribute(
            "otpAlert",
            otp
    );

    request.setAttribute(
            "step",
            "otp"
    );

   request.getRequestDispatcher(
        "/WEB-INF/views/ForgotPassword.jsp"
).forward(request, response);
}
    private void handleVerifyOtp(HttpServletRequest request,
                             HttpServletResponse response)
        throws ServletException, IOException {

    String inputOtp =
            request.getParameter("otp");

    HttpSession session =
            request.getSession();

    String sessionOtp =
            (String) session.getAttribute(
                    "resetOtp"
            );

    if (sessionOtp != null
            && sessionOtp.equals(inputOtp)) {

        session.setAttribute(
                "otpVerified",
                true
        );

        request.setAttribute(
                "step",
                "newPassword"
        );

    } else {

        request.setAttribute(
                "errorMessage",
                "OTP salah."
        );

        request.setAttribute(
                "step",
                "otp"
        );
    }

    request.getRequestDispatcher(
        "/WEB-INF/views/ForgotPassword.jsp"
).forward(request, response);
}
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession();
    Boolean verified = (Boolean) session.getAttribute("otpVerified");

    if (verified == null || !verified) {
        // Jika belum verifikasi OTP, tendang balik ke halaman index
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String email = (String) session.getAttribute("resetEmail");
    String newPassword = request.getParameter("newPassword");

    User user = userDAO.findByEmail(email);

    if (user == null) {
        request.setAttribute("errorMessage", "User tidak ditemukan.");
        request.getRequestDispatcher("/WEB-INF/views/ForgotPassword.jsp").forward(request, response);
        return;
    }

    boolean success = user.changePassword(newPassword);

    if (success) {
        // 1. Update data di database (Pastikan baris ini ada agar data tersimpan)
        userDAO.update(user); 

        // 2. Bersihkan semua session terkait token reset password demi keamanan
        session.removeAttribute("resetOtp");
        session.removeAttribute("resetEmail");
        session.removeAttribute("otpVerified");

        // 3. Set pesan sukses untuk ditampilkan di index.jsp
        session.setAttribute("successMessage", "Password berhasil diperbarui! Silakan login dengan password baru.");

        // 4. REDIRECT LANGSUNG KE HALAMAN LOGIN (index.jsp)
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return; 

    } else {
        request.setAttribute("errorMessage", "Gagal mengubah password.");
        request.setAttribute("step", "newPassword");
        
        // Jika gagal, tetap tampilkan form password baru di folder aman
        request.getRequestDispatcher("/WEB-INF/views/ForgotPassword.jsp").forward(request, response);
    }
}
    private void handleUpdateProfile(HttpServletRequest request,
                                 HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    User userLogin = (User) session.getAttribute("user");

    String namaDepan    = request.getParameter("namaDepan");
    String namaBelakang = request.getParameter("namaBelakang");

    if (namaDepan == null || namaDepan.trim().isEmpty()) {
        session.setAttribute("flashPesan", "Nama depan tidak boleh kosong.");
        response.sendRedirect(request.getContextPath() + "/settings.jsp");
        return;
    }

    String namaLengkap = namaDepan.trim();
    if (namaBelakang != null && !namaBelakang.trim().isEmpty()) {
        namaLengkap += " " + namaBelakang.trim();
    }

    userLogin.updateProfile(namaLengkap, userLogin.getEmail());
    boolean berhasil = userDAO.update(userLogin);

    if (berhasil) {
        session.setAttribute("namaUser", namaLengkap);
        session.setAttribute("user", userLogin);
        session.setAttribute("flashPesan", "Profil berhasil diperbarui!");
    } else {
        session.setAttribute("flashPesan", "Gagal menyimpan perubahan.");
    }

    response.sendRedirect(request.getContextPath() + "/settings.jsp");
}
    
}