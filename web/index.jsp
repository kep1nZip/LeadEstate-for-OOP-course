<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Jika user sudah login, redirect sesuai role --%>
<c:if test="${not empty sessionScope.userId}">
  <c:choose>
    <c:when test="${sessionScope.roleId == 1}">
      <c:redirect url="/dashboard"/>
    </c:when>
    <c:otherwise>
      <c:redirect url="/reminder"/>
    </c:otherwise>
  </c:choose>
</c:if>

<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>LeadEstate — Login</title>

  <script src="https://cdn.tailwindcss.com"></script>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet"/>

  <script>
    tailwind.config = {
      theme: {
        extend: {
          fontFamily: { sans: ['Inter', 'sans-serif'] },
          colors: {
            navy: '#1a1a2e',
            gold:  '#f59e0b'
          }
        }
      }
    }
  </script>
</head>

<body class="bg-gray-50 font-sans min-h-screen flex items-center justify-center p-4">

<div class="w-full max-w-md">

  <!-- Logo -->
  <div class="text-center mb-8">
    <div class="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-amber-400 mb-3">
      <svg class="w-6 h-6 text-white" viewBox="0 0 24 24" fill="currentColor">
        <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
      </svg>
    </div>
    <h1 class="text-2xl font-bold text-gray-900">Lead<span class="text-amber-400">Estate</span></h1>
    <p class="text-sm text-gray-400 mt-1">Sistem CRM Properti</p>
  </div>

  <!-- Card -->
  <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">

    <!-- Tab: Login / Register -->
    <div class="flex gap-1 bg-gray-100 rounded-xl p-1 mb-6" id="tab-bar">
      <button onclick="showTab('login')" id="tab-login"
              class="flex-1 py-2 text-sm font-semibold rounded-lg transition-all bg-white shadow text-gray-900">
        Masuk
      </button>
      <button onclick="showTab('register')" id="tab-register"
              class="flex-1 py-2 text-sm font-semibold rounded-lg transition-all text-gray-400 hover:text-gray-600">
        Daftar
      </button>
    </div>

    <!-- Pesan Error / Sukses dari AuthController -->
    <c:if test="${not empty errorMessage}">
      <div class="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-600">
        ${errorMessage}
      </div>
    </c:if>
    <c:if test="${not empty successMessage}">
      <div class="mb-4 px-4 py-3 bg-emerald-50 border border-emerald-200 rounded-xl text-sm text-emerald-600">
        ${successMessage}
      </div>
    </c:if>

    <!-- ─── FORM LOGIN ─── -->
    <div id="pane-login">
      <form action="${pageContext.request.contextPath}/AuthController" method="post" class="space-y-4">
        <input type="hidden" name="action" value="login"/>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Email</label>
          <input type="email" name="email" required placeholder="nama@leadestate.com"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Password</label>
          <input type="password" name="password" required placeholder="••••••••"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <button type="submit"
                class="w-full bg-[#1a1a2e] hover:bg-[#2a2a4e] text-white font-semibold py-2.5 rounded-xl text-sm transition-colors">
          Masuk
        </button>
      </form>

      <!-- Lupa Password -->
      <div class="mt-4 text-center">
        <button onclick="showTab('forgot')" class="text-xs text-amber-500 hover:underline">
          Lupa password?
        </button>
      </div>


    </div>

    <!-- ─── FORM REGISTER ─── -->
    <div id="pane-register" class="hidden">
      <form action="${pageContext.request.contextPath}/AuthController" method="post" class="space-y-4">
        <input type="hidden" name="action" value="register"/>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Nama Lengkap</label>
          <input type="text" name="name" required placeholder="Nama Anda"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Email</label>
          <input type="email" name="email" required placeholder="nama@leadestate.com"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Password</label>
          <input type="password" name="password" required placeholder="••••••••"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Konfirmasi Password</label>
          <input type="password" name="confirmPassword" required placeholder="••••••••"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Role</label>
          <select name="role"
                  class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400">
            <option value="Sales">Sales</option>
            <option value="Admin">Admin</option>
          </select>
        </div>

        <button type="submit"
                class="w-full bg-amber-400 hover:bg-amber-500 text-white font-semibold py-2.5 rounded-xl text-sm transition-colors">
          Daftar Sekarang
        </button>
      </form>
    </div>

    <!-- ─── FORM LUPA PASSWORD ─── -->
    <div id="pane-forgot" class="hidden">
      <p class="text-sm text-gray-500 mb-4">Masukkan email terdaftar Anda. Instruksi reset akan dikirimkan.</p>
      <form action="${pageContext.request.contextPath}/AuthController" method="post" class="space-y-4">
        <input type="hidden" name="action" value="forgotPassword"/>

        <div>
          <label class="block text-xs font-semibold text-gray-600 mb-1">Email</label>
          <input type="email" name="email" required placeholder="nama@leadestate.com"
                 class="w-full border border-gray-200 rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400 focus:border-transparent"/>
        </div>

        <button type="submit"
                class="w-full bg-[#1a1a2e] hover:bg-[#2a2a4e] text-white font-semibold py-2.5 rounded-xl text-sm transition-colors">
          Kirim Instruksi Reset
        </button>
      </form>

      <div class="mt-4 text-center">
        <button onclick="showTab('login')" class="text-xs text-amber-500 hover:underline">
          ← Kembali ke login
        </button>
      </div>
    </div>

  </div><!-- /card -->

  <p class="text-center text-xs text-gray-300 mt-6">&copy; 2026 LeadEstate. All rights reserved.</p>

</div><!-- /max-w-md -->

<script>
  // Baca tab aktif dari attribute yang di-set AuthController saat forward kembali ke index.jsp
  var activeTab = '${activeTab}' || 'login';

  function showTab(tab) {
    // sembunyikan semua pane
    ['login', 'register', 'forgot'].forEach(function(t) {
      document.getElementById('pane-' + t).classList.add('hidden');
    });

    // reset semua tab button
    var loginBtn    = document.getElementById('tab-login');
    var registerBtn = document.getElementById('tab-register');

    loginBtn.className    = 'flex-1 py-2 text-sm font-semibold rounded-lg transition-all text-gray-400 hover:text-gray-600';
    registerBtn.className = 'flex-1 py-2 text-sm font-semibold rounded-lg transition-all text-gray-400 hover:text-gray-600';

    // tampilkan pane aktif
    document.getElementById('pane-' + tab).classList.remove('hidden');

    // aktifkan tab button (hanya login & register yang ada di tab bar)
    if (tab === 'login') {
      loginBtn.className = 'flex-1 py-2 text-sm font-semibold rounded-lg transition-all bg-white shadow text-gray-900';
    } else if (tab === 'register') {
      registerBtn.className = 'flex-1 py-2 text-sm font-semibold rounded-lg transition-all bg-white shadow text-gray-900';
    }
  }

  // Tampilkan tab sesuai redirect dari controller (misal setelah register berhasil → tab login)
  showTab(activeTab);
</script>

</body>
</html>
