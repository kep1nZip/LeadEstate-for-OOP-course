<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Manajemen Sales — LeadEstate</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
  <style>
    :root {
        --sidebar-bg:       #0f1923;
        --sidebar-border:   #1e3040;
        --brand-gold:       #c9a84c;
        --brand-gold-light: #e8c97a;
    }
    .sidebar-grid::before {
        content: "";
        position: absolute;
        inset: 0;
        background-image:
            linear-gradient(rgba(201,168,76,.055) 1px, transparent 1px),
            linear-gradient(90deg, rgba(201,168,76,.055) 1px, transparent 1px);
        background-size: 36px 36px;
        pointer-events: none;
        z-index: 0;
    }
    .sidebar-grid > * { position: relative; z-index: 1; }
    .scroll-thin::-webkit-scrollbar       { width: 4px; }
    .scroll-thin::-webkit-scrollbar-track { background: transparent; }
    .scroll-thin::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }
    .bar-fill { transition: width .6s ease; }
    .sales-item { cursor: pointer; transition: background .15s, border-color .15s; }
    .sales-item:hover { background: rgba(255,255,255,.05); }
    .sales-item.selected { background: rgba(201,168,76,.12); border-color: rgba(201,168,76,.4); }
    .right-panel { transition: opacity .2s; }
  </style>
</head>
<body class="bg-[#0f0f23] text-gray-100 flex h-screen overflow-hidden" style="font-family:'DM Sans',sans-serif;">

<%
    com.leadestate.model.User userLogin = (com.leadestate.model.User) session.getAttribute("userLogin");
    if (userLogin == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String namaUser  = userLogin.getName();
    String roleName  = userLogin.getRoleName();
    boolean isAdmin  = "Admin".equalsIgnoreCase(roleName);
    String[] namaParts = namaUser.trim().split("\\s+");
    StringBuilder inisialSB = new StringBuilder();
    for (int i = 0; i < Math.min(2, namaParts.length); i++) {
        if (namaParts[i].length() > 0) inisialSB.append(namaParts[i].charAt(0));
    }
    String inisialUser = inisialSB.toString().toUpperCase();
%>

<%-- SIDEBAR --%>
<aside class="sidebar-grid w-64 flex-shrink-0 flex flex-col relative overflow-hidden"
       style="background:var(--sidebar-bg); border-right:1px solid var(--sidebar-border);">

    <!-- Brand -->
    <div class="flex items-center gap-2.5 px-4 py-5"
         style="border-bottom:1px solid var(--sidebar-border);">
        <div class="w-8 h-8 rounded-lg flex items-center justify-content-center overflow-hidden">
            <svg viewBox="0 0 36 36" class="w-8 h-8" fill="none">
                <rect width="36" height="36" rx="8" fill="#c9a84c"/>
                <path d="M10 26V14l8-6 8 6v12" stroke="#fff" stroke-width="2" stroke-linejoin="round"/>
                <rect x="14" y="18" width="8" height="8" rx="1" fill="#fff"/>
            </svg>
        </div>
        <span class="text-xl font-bold" style="font-family:'Playfair Display',serif; color:#fff;">
            Lead<span style="color:var(--brand-gold);">Estate</span>
        </span>
    </div>

    <!-- Nav -->
    <nav class="flex-1 overflow-y-auto scroll-thin px-3 py-3 flex flex-col gap-1.5">
        <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1"
             style="color:#3a5068; letter-spacing:1.2px;">Menu Utama</div>

        <!-- Dashboard -->
        <a href="${pageContext.request.contextPath}/dashboard"
           class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
           style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
           onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
           onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <rect x="3" y="3" width="7" height="7" rx="1.5"/>
                    <rect x="14" y="3" width="7" height="7" rx="1.5"/>
                    <rect x="3" y="14" width="7" height="7" rx="1.5"/>
                    <rect x="14" y="14" width="7" height="7" rx="1.5"/>
                </svg>
            </span>
            Dashboard
        </a>

        <!-- Reminder & Follow-Up -->
        <a href="${pageContext.request.contextPath}/reminder"
           class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
           style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
           onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
           onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <circle cx="12" cy="12" r="10"/>
                    <polygon points="10,8 16,12 10,16" fill="currentColor"/>
                </svg>
            </span>
            Reminder &amp; Follow-Up
        </a>

        <!-- Data Lead -->
        <a href="${pageContext.request.contextPath}/lead"
           class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
           style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
           onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
           onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                </svg>
            </span>
            Data Lead
        </a>

        <!-- Manajemen Sales (aktif) -->
        <% if (isAdmin) { %>
        <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-bold cursor-default"
             style="background:var(--brand-gold); border:1px solid var(--brand-gold); color:#0b1622;">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#0b1622" stroke-width="1.8">
                    <rect x="2" y="3" width="20" height="18" rx="2"/>
                    <circle cx="12" cy="10" r="3"/>
                    <path d="M7 21v-1a5 5 0 0 1 10 0v1"/>
                </svg>
            </span>
            Manajemen Sales
        </div>

        <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1"
             style="color:#3a5068; letter-spacing:1.2px;">Laporan</div>

        <a href="${pageContext.request.contextPath}/laporan"
           class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
           style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
           onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
           onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <line x1="18" y1="20" x2="18" y2="10"/>
                    <line x1="12" y1="20" x2="12" y2="4"/>
                    <line x1="6"  y1="20" x2="6"  y2="14"/>
                </svg>
            </span>
            Laporan &amp; Statistik
        </a>
        <% } %>

        <!-- Pengaturan -->
        <a href="${pageContext.request.contextPath}/settings.jsp"
           class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
           style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
           onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
           onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <circle cx="12" cy="12" r="3"/>
                    <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
                </svg>
            </span>
            Pengaturan
        </a>
    </nav>

    <!-- Sidebar Footer -->
    <div class="flex flex-col gap-1 px-4 py-3.5 flex-shrink-0"
         style="border-top:1px solid var(--sidebar-border);">
        <div class="flex items-center gap-2.5">
            <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold flex-shrink-0"
                 style="background:linear-gradient(135deg,var(--brand-gold),#e8c97a); color:var(--sidebar-bg); border:2px solid rgba(201,168,76,.4);">
                <%= inisialUser %>
            </div>
            <div>
                <div class="text-sm font-semibold text-white"><%= namaUser %></div>
                <div class="text-[11px] mt-px" style="color:#5a7a94;"><%= roleName %></div>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/AuthController?action=logout"
           class="flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs transition-all mt-1"
           style="color:#5a7a94; border:1px solid rgba(255,255,255,.06); background:rgba(255,255,255,.02);"
           onmouseover="this.style.background='rgba(239,68,68,.15)';this.style.color='#ef4444';this.style.borderColor='rgba(239,68,68,.3)';"
           onmouseout="this.style.background='rgba(255,255,255,.02)';this.style.color='#5a7a94';this.style.borderColor='rgba(255,255,255,.06)';">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            Keluar
        </a>
    </div>
</aside>

<%-- MAIN --%>
<main class="flex-1 flex flex-col overflow-hidden">

 <%-- Topbar — TEMA TERANG --%>
  <div class="flex items-center justify-between px-6 py-3 bg-white border-b border-gray-200 flex-shrink-0">
    <h1 class="text-base font-semibold text-gray-800">Manajemen Sales</h1>
    <div class="flex items-center gap-3">
      <%-- Flash message --%>
      <c:if test="${not empty flashPesan}">
        <span class="text-xs bg-green-500/20 text-green-600 border border-green-500/30 px-3 py-1 rounded-full font-medium">
          ${flashPesan}
        </span>
      </c:if>
      <%-- Badge Tanggal --%>
      <div class="flex items-center gap-1.5 bg-gray-100 px-3 py-1.5 rounded-lg text-xs text-gray-500">
        <span>📅</span><span id="tanggalHariIni"></span>
      </div>
    </div>
  </div>

  <%-- Split body --%>
  <div class="flex-1 flex overflow-hidden">

    <%-- ── LEFT PANEL: Daftar Sales — TEMA TERANG ── --%>
    <div class="w-72 bg-white border-r border-gray-200 flex flex-col flex-shrink-0">

      <%-- Header + tombol tambah --%>
      <div class="px-4 pt-4 pb-3 border-b border-gray-100">
        <div class="flex items-center justify-between mb-3">
          <div class="text-sm font-semibold text-gray-800">
            Tim Sales
            <span class="text-gray-400 font-normal text-xs ml-1">(${fn:length(daftarSalesData)} orang)</span>
          </div>
          <%-- Tombol Tambah (Menggunakan brand-gold agar tetap konsisten & estetik) --%>
          <a href="${pageContext.request.contextPath}/manajemen-sales?aksi=tambah"
             class="text-[11px] bg-[#c9a84c] text-white font-bold px-3 py-1.5 rounded-lg hover:bg-[#b0923e] transition-colors shadow-sm">
            + Tambah
          </a>
        </div>

        <%-- Search (client-side filtering) --%>
        <div class="relative">
          <svg class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
            <path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
          <input id="searchInput" type="text" placeholder="Cari nama atau properti..."
                 class="w-full bg-gray-50 border border-gray-200 rounded-lg pl-8 pr-3 py-1.5 text-xs text-gray-700 placeholder-gray-400 focus:outline-none focus:border-[#c9a84c] focus:bg-white transition-all"
                 oninput="filterSales(this.value)" />
        </div>
      </div>

      <%-- Sales list --%>
<div id="salesList" class="flex-1 overflow-y-auto py-2">
        <c:choose>
          <c:when test="${empty daftarSalesData}">
            <div class="flex flex-col items-center justify-center h-full text-gray-400 text-xs text-center px-4">
              <svg class="w-10 h-10 mb-2 opacity-40" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
              </svg>
              Belum ada Sales terdaftar.<br/>Klik "+ Tambah" untuk menambah.
            </div>
          </c:when>
          <c:otherwise>
            <%
              String[] avatarColors = {"#f59e0b","#6366f1","#10b981","#ef4444","#8b5cf6","#0ea5e9","#ec4899","#f97316"};
              int colorIdx = 0;
            %>
            <c:forEach var="row" items="${daftarSalesData}" varStatus="loop">
              <%-- row = Object[]{User, totalLead, totalClosing} --%>
              <%
                String avatarColor = avatarColors[colorIdx % avatarColors.length];
                colorIdx++;
              %>
              <%
                // Hitung progress bar (target default 10)
                int closingVal = 0; 
              %>
              <%-- Menambahkan bg-gray-50/50 dan hover:bg-gray-100 agar lebih interaktif di tema terang --%>
              <div class="sales-item mx-2 mb-1 p-3 rounded-lg border border-gray-100 bg-gray-50/50 hover:bg-gray-100/80 transition-colors cursor-pointer"
                   data-name="${fn:toLowerCase(row[0].name)}"
                   onclick="selectSales(${row[0].id})">

                <div class="flex items-start gap-3">
                  <%-- Avatar --%>
                  <div class="w-9 h-9 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0"
                       style="background: <%= avatarColor %>">
                    ${fn:substring(row[0].name, 0, 1)}
                  </div>

                  <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between">
                      <%-- Mengubah teks putih menjadi gray-800 --%>
                      <span class="text-xs font-semibold text-gray-800 truncate">${row[0].name}</span>
                      <span class="text-xs font-bold text-gray-800 ml-2 flex-shrink-0">${row[2]}</span>
                    </div>
                    <div class="text-[10px] text-gray-400 mb-1.5">Sales &nbsp;·&nbsp; closing</div>

                    <%-- Progress bar target --%>
                    <div class="flex items-center justify-between text-[10px] text-gray-400 mb-0.5">
                      <span>Target</span>
                      <span class="font-medium text-gray-600">${row[2]} / 10</span>
                    </div>
                    <%-- Mengubah bg-white/10 menjadi bg-gray-200 --%>
                    <div class="w-full bg-gray-200 rounded-full h-1">
                      <%-- Mengubah warna fill ke hex emas #c9a84c agar konsisten dengan tema terang --%>
                      <div class="bar-fill h-1 rounded-full"
                           style="background: #c9a84c; width: min(${row[2] * 10}%, 100%)"></div>
                    </div>
                  </div>
                </div>
              </div>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <%-- RIGHT PANEL: Detail Sales — TEMA TERANG --%>
    <div class="flex-1 overflow-y-auto p-5 bg-gray-50/50">

      <c:choose>

        <%-- tampilkan profil + statistik --%>
        <c:when test="${not empty param.aksi and param.aksi == 'detail' and not empty sales}">
          
          <%-- Profile card --%>
          <div class="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden mb-4">
            <%-- Mengubah gradien header profil menjadi kombinasi pastel yang elegan --%>
            <div class="h-16 bg-gradient-to-r from-slate-100 to-indigo-50/50 border-b border-gray-100"></div>
            <div class="px-5 pb-5">
              <div class="flex items-end justify-between -mt-6 mb-4">
                <div class="w-14 h-14 rounded-full bg-indigo-100 border-4 border-white flex items-center justify-center text-indigo-600 font-bold text-lg shadow-sm">
                  ${fn:substring(sales.name, 0, 1)}
                </div>
                <div class="flex gap-2 mt-2">
                  <a href="${pageContext.request.contextPath}/manajemen-sales?aksi=edit&id=${sales.id}"
                     class="text-[11px] bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium px-3 py-1.5 rounded-lg transition-colors border border-gray-200 shadow-sm">✏️ Edit</a>
                  <form method="post" action="${pageContext.request.contextPath}/manajemen-sales" class="inline"
                        onsubmit="return confirm('Hapus Sales ini?')">
                    <input type="hidden" name="aksi" value="hapus"/>
                    <input type="hidden" name="id" value="${sales.id}"/>
                    <button type="submit" class="text-[11px] bg-red-50 hover:bg-red-100 text-red-600 font-medium px-3 py-1.5 rounded-lg transition-colors border border-red-200 shadow-sm">🗑 Hapus</button>
                  </form>
                </div>
              </div>
              <div class="text-base font-bold text-gray-800">${sales.name}</div>
              <div class="text-xs text-gray-400 mt-0.5">Sales &nbsp;·&nbsp; ${sales.email}</div>
            </div>
          </div>

          <%-- Statistik 4 kotak --%>
          <div class="grid grid-cols-4 gap-3 mb-4">
            <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm text-center">
              <div class="text-xl font-bold text-[#c9a84c]">${totalLead}</div>
              <div class="text-[10px] text-gray-400 mt-0.5 font-medium uppercase tracking-wider">Total Lead</div>
            </div>
            <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm text-center">
              <div class="text-xl font-bold text-blue-500">${totalFollowUp}</div>
              <div class="text-[10px] text-gray-400 mt-0.5 font-medium uppercase tracking-wider">Follow Up</div>
            </div>
            <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm text-center">
              <div class="text-xl font-bold text-green-600">${totalClosing}</div>
              <div class="text-[10px] text-gray-400 mt-0.5 font-medium uppercase tracking-wider">Closing</div>
            </div>
            <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm text-center">
              <div class="text-xl font-bold" style="color: ${pctPencapaian >= 100 ? '#16a34a' : pctPencapaian >= 60 ? '#d97706' : '#dc2626'}">
                <fmt:formatNumber value="${pctPencapaian}" maxFractionDigits="0"/>%
              </div>
              <div class="text-[10px] text-gray-400 mt-0.5 font-medium uppercase tracking-wider">Pencapaian</div>
            </div>
          </div>

          <%-- Performa detail + Lead list berdampingan --%>
          <div class="grid grid-cols-2 gap-4 mb-4">
            <%-- Performa --%>
            <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm">
              <div class="flex items-center justify-between mb-4">
                <div class="text-sm font-semibold text-gray-800">📊 Performa Detail</div>
                <div class="text-[10px] text-gray-400" id="bulanTahun"></div>
              </div>
              <div class="space-y-3">
                <%-- Target Closing --%>
                <div>
                  <div class="flex justify-between text-[11px] mb-1">
                    <span class="text-gray-500">Target Closing</span>
                    <span class="text-gray-700 font-semibold">${totalClosing} / 10</span>
                  </div>
                  <div class="w-full bg-gray-100 rounded-full h-1.5">
                    <div class="bar-fill h-1.5 rounded-full bg-green-500" style="width: min(${totalClosing * 10}%, 100%)"></div>
                  </div>
                </div>
                <%-- Tingkat Respons Lead (estimasi) --%>
                <div>
                  <div class="flex justify-between text-[11px] mb-1">
                    <span class="text-gray-500">Tingkat Respons Lead</span>
                    <span class="text-gray-700 font-semibold">85%</span>
                  </div>
                  <div class="w-full bg-gray-100 rounded-full h-1.5">
                    <div class="bar-fill h-1.5 rounded-full bg-blue-500" style="width: 85%"></div>
                  </div>
                </div>
                <%-- Follow Up Tepat Waktu --%>
                <div>
                  <div class="flex justify-between text-[11px] mb-1">
                    <span class="text-gray-500">Follow Up Tepat Waktu</span>
                    <span class="text-gray-700 font-semibold">78%</span>
                  </div>
                  <div class="w-full bg-gray-100 rounded-full h-1.5">
                    <div class="bar-fill h-1.5 rounded-full bg-purple-500" style="width: 78%"></div>
                  </div>
                </div>
              </div>
            </div>

            <%-- Lead ditangani --%>
            <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm">
              <div class="flex items-center justify-between mb-3">
                <div class="text-sm font-semibold text-gray-800">📋 Lead Ditangani</div>
                <span class="text-[10px] bg-gray-100 text-gray-600 border border-gray-200 px-2 py-0.5 rounded-full font-medium">${fn:length(daftarLead)} lead</span>
              </div>
              <div class="space-y-2 max-h-44 overflow-y-auto">
                <c:choose>
                  <c:when test="${empty daftarLead}">
                    <div class="text-center text-gray-400 text-xs py-6">Belum ada lead</div>
                  </c:when>
                  <c:otherwise>
                    <c:forEach var="lead" items="${daftarLead}" varStatus="ll">
                      <div class="flex items-center gap-2 p-2 bg-gray-50 border border-gray-100/70 rounded-lg">
                        <div class="w-6 h-6 rounded-full bg-amber-500 flex items-center justify-center text-white text-[10px] font-bold flex-shrink-0">
                          ${fn:substring(lead.name, 0, 1)}
                        </div>
                        <div class="flex-1 min-w-0">
                          <div class="text-[11px] font-medium text-gray-800 truncate">${lead.name}</div>
                          <div class="text-[10px] text-gray-400">🏠 Properti #${lead.propertyId}</div>
                        </div>
                        <c:choose>
                          <c:when test="${lead.statusId == 5}">
                            <span class="text-[9px] bg-green-100 text-green-700 border border-green-200 px-1.5 py-0.5 rounded-full font-medium">Closing</span>
                          </c:when>
                          <c:when test="${lead.statusId == 6}">
                            <span class="text-[9px] bg-red-100 text-red-700 border border-red-200 px-1.5 py-0.5 rounded-full font-medium">Lost</span>
                          </c:when>
                          <c:otherwise>
                            <span class="text-[9px] bg-blue-100 text-blue-700 border border-blue-200 px-1.5 py-0.5 rounded-full font-medium">Aktif</span>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>

          <%-- Aktivitas terbaru --%>
          <div class="bg-white rounded-xl p-4 border border-gray-200 shadow-sm">
            <div class="text-sm font-semibold text-gray-800 mb-3">🕐 Aktivitas Terbaru</div>
            <c:choose>
              <c:when test="${empty aktivitasTerbaru}">
                <div class="text-center text-gray-400 text-xs py-4">Belum ada aktivitas</div>
              </c:when>
              <c:otherwise>
                <div class="space-y-2.5">
                  <c:forEach var="fu" items="${aktivitasTerbaru}">
                    <div class="flex items-start gap-2.5">
                      <div class="w-2 h-2 rounded-full bg-[#c9a84c] mt-1.5 flex-shrink-0"></div>
                      <div>
                        <div class="text-[11px] text-gray-700 leading-relaxed">${fu.notes}</div>
                        <div class="text-[10px] text-gray-400 mt-0.5">${fu.followupDate} &nbsp;·&nbsp; <span class="font-medium text-gray-500">${fu.status}</span></div>
                      </div>
                    </div>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </c:when>

        <%-- Default: empty state — pilih Sales dari daftar --%>
        <c:otherwise>
          <div class="flex flex-col items-center justify-center h-full text-gray-400">
            <svg class="w-16 h-16 mb-4 opacity-30" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/>
            </svg>
            <p class="text-sm text-center leading-relaxed">Pilih Sales dari daftar di kiri<br/><span class="text-xs text-gray-400/80">untuk melihat profil &amp; performa</span></p>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</main>

<%-- MODAL: Form Tambah / Edit Sales --%>
<c:if test="${(param.aksi == 'tambah') or (param.aksi == 'edit' and not empty sales)}">
<div id="salesModal"
     class="fixed inset-0 bg-black/60 flex items-center justify-center z-50"
     onclick="if(event.target===this) closeModal()">
  <div class="bg-[#1a1a2e] rounded-2xl w-full max-w-md mx-4 border border-white/10 shadow-2xl">

    <%-- Modal header --%>
    <div class="flex items-center justify-between px-5 py-4 border-b border-white/10">
      <div class="text-sm font-bold text-white">
        <c:choose>
          <c:when test="${param.aksi == 'edit'}">✏️ Edit Data Sales</c:when>
          <c:otherwise>➕ Tambah Sales Baru</c:otherwise>
        </c:choose>
      </div>
      <button onclick="closeModal()" class="text-gray-400 hover:text-white transition-colors">✕</button>
    </div>

    <%-- Modal form --%>
    <form method="post" action="${pageContext.request.contextPath}/manajemen-sales">
      <input type="hidden" name="aksi" value="simpan"/>
      <c:if test="${param.aksi == 'edit'}">
        <input type="hidden" name="id" value="${sales.id}"/>
      </c:if>

      <div class="px-5 py-4 space-y-4">
        <%-- Nama + Email --%>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-[11px] text-gray-400 mb-1">Nama Lengkap *</label>
            <input type="text" name="name" required placeholder="Contoh: Budi Santoso"
                   value="${sales.name}"
                   class="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-white placeholder-gray-600 focus:outline-none focus:border-brand-gold/50"/>
          </div>
          <div>
            <label class="block text-[11px] text-gray-400 mb-1">Email *</label>
            <input type="email" name="email" required placeholder="email@domain.com"
                   value="${sales.email}"
                   class="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-white placeholder-gray-600 focus:outline-none focus:border-brand-gold/50"/>
          </div>
        </div>

        <%-- Password + Role --%>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <label class="block text-[11px] text-gray-400 mb-1">
              Password <c:if test="${param.aksi == 'edit'}"><span class="text-gray-600">(kosongkan jika tidak diubah)</span></c:if>
              <c:if test="${param.aksi != 'edit'}">*</c:if>
            </label>
            <input type="password" name="password"
                   <c:if test="${param.aksi != 'edit'}">required</c:if>
                   placeholder="Min. 6 karakter"
                   class="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-white placeholder-gray-600 focus:outline-none focus:border-brand-gold/50"/>
          </div>
          <div>
            <label class="block text-[11px] text-gray-400 mb-1">Jabatan</label>
            <input type="text" value="Sales" disabled
                   class="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-sm text-gray-500"/>
          </div>
        </div>
      </div>

      <%-- Modal footer --%>
      <div class="flex items-center justify-end gap-3 px-5 py-4 border-t border-white/10">
        <button type="button" onclick="closeModal()"
                class="text-sm text-gray-400 hover:text-white px-4 py-2 rounded-lg hover:bg-white/5 transition-colors">Batal</button>
        <button type="submit"
                class="text-sm bg-brand-gold text-[#1a1a2e] font-bold px-5 py-2 rounded-lg hover:bg-yellow-400 transition-colors">
          💾 Simpan
        </button>
      </div>
    </form>
  </div>
</div>
</c:if>

<script>
  const tgl = document.getElementById('tanggalHariIni');
  if (tgl) tgl.textContent = new Date().toLocaleDateString('id-ID', {
    weekday:'long', day:'numeric', month:'long', year:'numeric'
  });

  const bln = document.getElementById('bulanTahun');
  if (bln) bln.textContent = new Date().toLocaleDateString('id-ID', {month:'long', year:'numeric'});

  function selectSales(salesId) {
    window.location.href = '${pageContext.request.contextPath}/manajemen-sales?aksi=detail&id=' + salesId;
  }

  const currentId = '${param.id}';
  if (currentId) {
    document.querySelectorAll('.sales-item').forEach(el => {
      if (el.getAttribute('onclick') && el.getAttribute('onclick').includes('(' + currentId + ')')) {
        el.classList.add('selected');
      }
    });
  }

  function filterSales(q) {
    const lower = q.toLowerCase().trim();
    document.querySelectorAll('.sales-item').forEach(el => {
      const name = el.getAttribute('data-name') || '';
      el.style.display = (lower === '' || name.includes(lower)) ? '' : 'none';
    });
  }

  function closeModal() {
    window.location.href = '${pageContext.request.contextPath}/manajemen-sales';
  }
</script>
</body>
</html>
