<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Forgot Password</title>
    <style>
        body { font-family: Arial; background:#f4f6f9; display:flex; justify-content:center; align-items:center; height:100vh; }
        .card { width:400px; background:white; padding:30px; border-radius:10px; box-shadow:0 0 15px rgba(0,0,0,.1); }
        h2 { text-align:center; }
        input { width:100%; padding:10px; margin-top:10px; margin-bottom:15px; }
        button { width:100%; padding:12px; background:#2563eb; color:white; border:none; cursor:pointer; }
        .success { color:green; }
        .error { color:red; }
    </style>
</head>
<body>

<div class="card">

    <%-- Menampilkan Pesan Error / Sukses global dari Controller --%>
    <c:if test="${not empty errorMessage}">
        <p class="error">${errorMessage}</p>
    </c:if>
    <c:if test="${not empty successMessage}">
        <p class="success">${successMessage}</p>
    </c:if>

    <%-- ======================================================
         STEP 1 - INPUT EMAIL (Default jika step kosong atau "1")
         ====================================================== --%>
    <c:if test="${empty step || step == '1'}">
        <h2>Lupa Password</h2>
        <p>Masukkan email anda</p>
        <form action="${pageContext.request.contextPath}/AuthController" method="post">
            <input type="hidden" name="action" value="ForgotPassword"/>
            <input type="email" name="email" placeholder="Masukkan email" required>
            <button type="submit">Kirim OTP</button>
        </form>
    </c:if>

    <%-- ======================================================
         STEP 2 - VERIFIKASI OTP (Ditampilkan jika step == 'otp')
         ====================================================== --%>
    <c:if test="${step == 'otp'}">
        <h2>Verifikasi OTP</h2>
        <p>OTP telah dikirim ke: <br><b>${sessionScope.resetEmail}</b></p>
        
        <%-- Tampilkan alert bantuan OTP jika ada --%>
        <c:if test="${not empty otpAlert}">
            <div style="background: #fef08a; padding: 10px; margin-bottom: 10px; text-align: center;">
                <strong>Debug OTP:</strong> ${otpAlert}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/AuthController" method="post">
            <input type="hidden" name="action" value="verifyOtp"/>
            <input type="text" name="otp" maxlength="6" placeholder="Masukkan OTP" required>
            <button type="submit">Verifikasi OTP</button>
        </form>
    </c:if>

    <%-- ======================================================
         STEP 3 - PASSWORD BARU (Ditampilkan jika step == 'newPassword')
         ====================================================== --%>
    <c:if test="${step == 'newPassword'}">
        <h2>Password Baru</h2>
        <form action="${pageContext.request.contextPath}/AuthController" method="post">
            <input type="hidden" name="action" value="changePassword"/>
            <input type="password" name="newPassword" placeholder="Password baru" required>
            <button type="submit">Simpan Password</button>
        </form>
    </c:if>

    <%-- ======================================================
         STEP 4 - SUCCESS
         ====================================================== --%>
    <c:if test="${step == 'success'}">
        <h2>Password Berhasil Diganti</h2>
        <p class="success">Password berhasil diperbarui.</p>
        <a href="${pageContext.request.contextPath}/index.jsp" style="display:block; text-align:center; margin-top:15px;">
            Kembali ke Login
        </a>
    </c:if>

</div>

</body>
</html>