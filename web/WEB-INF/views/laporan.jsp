<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.leadestate.model.User" %>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Laporan &amp; Statistik — LeadEstate</title>

  <!-- Tailwind CSS CDN -->
  <script src="https://cdn.tailwindcss.com"></script>

  <!-- Chart.js CDN -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

  <!-- Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet" />

  <style>
    :root {
        --sidebar-bg:       #0f1923;
        --sidebar-border:   #1e3040;
        --brand-gold:       #c9a84c;
        --brand-gold-light: #e8c97a;
        --content-bg:       #f5f4f0;
        --card-bg:          #ffffff;
        --text-primary:     #1a1a2e;
        --text-secondary:   #6b7280;
        --text-muted:       #9ca3af;
        --border:           #e5e7eb;
        --danger:           #ef4444;
    }
    body { font-family: 'DM Sans', sans-serif; }

    /* ── Sidebar dot-grid overlay ── */
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

    /* ── Scrollbar ── */
    .scroll-thin::-webkit-scrollbar       { width: 4px; }
    .scroll-thin::-webkit-scrollbar-track { background: transparent; }
    .scroll-thin::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }

    /* ── Chart canvas responsif ── */
    .chart-wrapper { position: relative; width: 100%; }

    /* ── Funnel bar transition ── */
    .funnel-fill {
      transition: width 0.8s cubic-bezier(.4,0,.2,1);
      min-width: 2px;
    }

    /* ── Progress bar transition ── */
    .src-bar-fill, .rt-bar-fill {
      transition: width 0.7s cubic-bezier(.4,0,.2,1);
      min-width: 2px;
    }

    /* ── Print styles ── */
    @media print {
      .sidebar-grid, .no-print { display: none !important; }
      .main-content { margin-left: 0 !important; }
    }
  </style>
</head>

<%
    User userLogin = (User) session.getAttribute("userLogin");
    if (userLogin == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String namaUser = userLogin.getName();
    String roleName = userLogin.getRoleName();
    boolean isAdmin = "Admin".equalsIgnoreCase(roleName);

    String[] namaParts = namaUser.trim().split("\\s+");
    StringBuilder inisialSB = new StringBuilder();
    for (int i = 0; i < Math.min(2, namaParts.length); i++) {
        if (namaParts[i].length() > 0) inisialSB.append(namaParts[i].charAt(0));
    }
    String inisialUser = inisialSB.toString().toUpperCase();
%>

<body class="bg-[#f5f4f0] font-sans text-gray-800 antialiased">

<div class="flex min-h-screen">

  <!-- ══════════════════════════════════
       SIDEBAR
       ══════════════════════════════════ -->
  <aside class="sidebar-grid fixed top-0 left-0 h-screen w-64 flex flex-col overflow-hidden z-30"
         style="background:var(--sidebar-bg); border-right:1px solid var(--sidebar-border);">

    <!-- Brand -->
    <div class="flex items-center gap-2.5 px-4 py-5"
         style="border-bottom:1px solid var(--sidebar-border);">
        <div class="w-8 h-8 rounded-lg flex items-center justify-center overflow-hidden">
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

        <!-- Manajemen Sales (Admin only) -->
        <% if (isAdmin) { %>
        <a href="${pageContext.request.contextPath}/manajemen-sales"
           class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
           style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
           onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
           onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <rect x="2" y="3" width="20" height="18" rx="2"/>
                    <circle cx="12" cy="10" r="3"/>
                    <path d="M7 21v-1a5 5 0 0 1 10 0v1"/>
                </svg>
            </span>
            Manajemen Sales
        </a>

        <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1"
             style="color:#3a5068; letter-spacing:1.2px;">Laporan</div>

        <!-- Laporan & Statistik (aktif) -->
        <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-bold cursor-default"
             style="background:var(--brand-gold); border:1px solid var(--brand-gold); color:#0b1622;">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#0b1622" stroke-width="1.8">
                    <line x1="18" y1="20" x2="18" y2="10"/>
                    <line x1="12" y1="20" x2="12" y2="4"/>
                    <line x1="6"  y1="20" x2="6"  y2="14"/>
                </svg>
            </span>
            Laporan &amp; Statistik
        </div>
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

  <!-- ══════════════════════════════════
       MAIN CONTENT
       ══════════════════════════════════ -->
  <main class="ml-64 flex-1 flex flex-col min-h-screen">

    <!-- TOPBAR -->
    <header class="sticky top-0 z-20 bg-white border-b border-gray-100 px-6 py-3.5 flex items-center justify-between shadow-sm no-print">
      <div>
        <h1 class="text-lg font-bold text-gray-900">Laporan &amp; Statistik</h1>
        <p class="text-xs text-gray-400 mt-0.5">Rekap performa penjualan &amp; konversi lead</p>
      </div>
      <div class="flex items-center gap-3">
        <!-- Tanggal hari ini (dirender JS) -->
        <div class="flex items-center gap-1.5 bg-gray-50 border border-gray-200 rounded-lg px-3 py-1.5 text-xs text-gray-600" id="date-chip">
          📅 <span id="date-label">–</span>
        </div>
        <!-- Tombol Print -->
        <button onclick="window.print()"
                class="flex items-center gap-1.5 bg-[#1a1a2e] hover:bg-[#2a2a4e] text-white text-xs font-semibold px-3.5 py-2 rounded-lg transition-colors no-print">
          🖨️ Print
        </button>
      </div>
    </header>

    <!-- PAGE BODY -->
    <div class="flex-1 px-6 py-6 space-y-6">

      <!-- ─────────────────────────────────
           KPI CARDS
           ───────────────────────────────── -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

        <!-- Total Lead -->
        <div class="bg-white rounded-2xl p-5 border border-amber-100 shadow-sm hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-3">
            <div class="w-10 h-10 rounded-xl bg-amber-50 flex items-center justify-center text-xl">📋</div>
            <span class="text-xs font-semibold text-emerald-500 bg-emerald-50 px-2 py-0.5 rounded-full">Live</span>
          </div>
          <div class="text-3xl font-extrabold text-gray-900 mb-1">${totalLead}</div>
          <div class="text-sm font-medium text-gray-500">Total Lead Masuk</div>
          <div class="text-xs text-gray-400 mt-1">Semua lead yang terdaftar</div>
        </div>

        <!-- Total Closing -->
        <div class="bg-white rounded-2xl p-5 border border-emerald-100 shadow-sm hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-3">
            <div class="w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center text-xl">✅</div>
            <span class="text-xs font-semibold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full">Closed Won</span>
          </div>
          <div class="text-3xl font-extrabold text-emerald-600 mb-1">${totalClosing}</div>
          <div class="text-sm font-medium text-gray-500">Total Closing</div>
          <div class="text-xs text-gray-400 mt-1">Lead berstatus Closed Won</div>
        </div>

        <!-- Closing Rate -->
        <div class="bg-white rounded-2xl p-5 border border-blue-100 shadow-sm hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-3">
            <div class="w-10 h-10 rounded-xl bg-blue-50 flex items-center justify-center text-xl">📈</div>
            <c:choose>
              <c:when test="${closingRate >= 25}">
                <span class="text-xs font-semibold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-full">↑ Bagus</span>
              </c:when>
              <c:otherwise>
                <span class="text-xs font-semibold text-amber-600 bg-amber-50 px-2 py-0.5 rounded-full">Perlu Ditingkatkan</span>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="text-3xl font-extrabold text-blue-600 mb-1">
            <fmt:formatNumber value="${closingRate}" maxFractionDigits="1"/>%
          </div>
          <div class="text-sm font-medium text-gray-500">Closing Rate</div>
          <div class="text-xs text-gray-400 mt-1">Rasio closing dari total lead</div>
        </div>

        <!-- Jumlah Status -->
        <div class="bg-white rounded-2xl p-5 border border-purple-100 shadow-sm hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-3">
            <div class="w-10 h-10 rounded-xl bg-purple-50 flex items-center justify-center text-xl">🔵</div>
            <span class="text-xs font-semibold text-purple-600 bg-purple-50 px-2 py-0.5 rounded-full">${fn:length(leadPerStatus)} Status</span>
          </div>
          <div class="text-3xl font-extrabold text-purple-600 mb-1">${fn:length(sumberLead)}</div>
          <div class="text-sm font-medium text-gray-500">Sumber Lead Aktif</div>
          <div class="text-xs text-gray-400 mt-1">Channel akuisisi yang tercatat</div>
        </div>
      </div>

      <!-- ─────────────────────────────────
           ROW 2: BAR CHART + DONUT CHART
           ───────────────────────────────── -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">

        <!-- BAR CHART: Tren Lead & Closing (2/3 width) -->
        <div class="lg:col-span-2 bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="text-sm font-bold text-gray-900">📈 Tren Lead &amp; Closing</h3>
              <p class="text-xs text-gray-400 mt-0.5">6 bulan terakhir</p>
            </div>
            <div class="flex gap-4 text-xs text-gray-500">
              <span class="flex items-center gap-1.5">
                <span class="w-3 h-3 rounded-full bg-amber-400 inline-block"></span> Lead Masuk
              </span>
              <span class="flex items-center gap-1.5">
                <span class="w-3 h-3 rounded-full bg-blue-500 inline-block"></span> Closing
              </span>
            </div>
          </div>
          <div class="chart-wrapper" style="height: 240px;">
            <canvas id="chartTren"></canvas>
          </div>
        </div>

        <!-- DONUT CHART: Status Lead (1/3 width) -->
        <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <div class="mb-4">
            <h3 class="text-sm font-bold text-gray-900">🔵 Status Lead</h3>
            <p class="text-xs text-gray-400 mt-0.5">Distribusi saat ini</p>
          </div>
          <div class="chart-wrapper mb-3" style="height: 180px;">
            <canvas id="chartDonut"></canvas>
          </div>
          <!-- Legend status -->
          <div class="space-y-1.5 mt-2" id="donut-legend">
            <%-- Diisi oleh JS setelah chart render --%>
          </div>
        </div>
      </div>

      <!-- ─────────────────────────────────
           ROW 3: RANKING SALES + SUMBER LEAD + FUNNEL
           ───────────────────────────────── -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">

        <!-- RANKING SALES -->
        <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-sm font-bold text-gray-900">🏆 Ranking Sales</h3>
            <span class="text-xs bg-amber-50 text-amber-600 font-semibold px-2 py-0.5 rounded-full">Closing</span>
          </div>

          <c:set var="maxClosing" value="1"/>
          <c:forEach items="${rankingSales}" var="s">
            <c:if test="${s.closing > maxClosing}">
              <c:set var="maxClosing" value="${s.closing}"/>
            </c:if>
          </c:forEach>

          <%-- Warna untuk masing-masing rank --%>
          <c:set var="salesColors" value="#f59e0b,#6366f1,#10b981,#ef4444,#8b5cf6"/>

          <div class="space-y-3">
            <c:forEach items="${rankingSales}" var="s" varStatus="vs">
              <c:if test="${vs.index < 5}">
                <div class="flex items-center gap-3">

                  <!-- Badge rank -->
                  <c:choose>
                    <c:when test="${vs.index == 0}">
                      <div class="w-6 h-6 rounded-full bg-amber-400 text-white text-xs font-bold flex items-center justify-center flex-shrink-0">1</div>
                    </c:when>
                    <c:when test="${vs.index == 1}">
                      <div class="w-6 h-6 rounded-full bg-gray-300 text-gray-700 text-xs font-bold flex items-center justify-center flex-shrink-0">2</div>
                    </c:when>
                    <c:when test="${vs.index == 2}">
                      <div class="w-6 h-6 rounded-full bg-orange-300 text-white text-xs font-bold flex items-center justify-center flex-shrink-0">3</div>
                    </c:when>
                    <c:otherwise>
                      <div class="w-6 h-6 rounded-full bg-gray-100 text-gray-500 text-xs font-bold flex items-center justify-center flex-shrink-0">${vs.index + 1}</div>
                    </c:otherwise>
                  </c:choose>

                  <!-- Avatar inisial -->
                  <c:set var="namaS" value="${s.nama}"/>
                  <div class="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0"
                       style="background: ${vs.index == 0 ? '#f59e0b' : vs.index == 1 ? '#6366f1' : vs.index == 2 ? '#10b981' : vs.index == 3 ? '#ef4444' : '#8b5cf6'}">
                    ${fn:substring(namaS, 0, 1)}
                  </div>

                  <!-- Info + progress bar -->
                  <div class="flex-1 min-w-0">
                    <div class="text-xs font-semibold text-gray-800 truncate">${s.nama}</div>
                    <div class="mt-1 h-1.5 bg-gray-100 rounded-full overflow-hidden">
                      <div class="h-full rounded-full rt-bar-fill"
                           style="width: ${maxClosing > 0 ? (s.closing * 100 / maxClosing) : 0}%;
                                  background: ${vs.index == 0 ? '#f59e0b' : vs.index == 1 ? '#6366f1' : vs.index == 2 ? '#10b981' : vs.index == 3 ? '#ef4444' : '#8b5cf6'}">
                      </div>
                    </div>
                  </div>

                  <!-- Nilai closing -->
                  <div class="text-sm font-bold text-gray-800 flex-shrink-0">${s.closing}</div>
                </div>
              </c:if>
            </c:forEach>

            <c:if test="${empty rankingSales}">
              <div class="text-center py-6 text-gray-400 text-sm">Belum ada data ranking</div>
            </c:if>
          </div>
        </div>
          
           <!-- SUMBER LEAD -->
       <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-sm font-bold text-gray-900">📣 Sumber Lead</h3>
            <span class="text-xs bg-blue-50 text-blue-600 font-semibold px-2 py-0.5 rounded-full">Semua Waktu</span>
          </div>

          <c:set var="totalSumber" value="0"/>
          <c:forEach items="${sumberLead}" var="src">
            <c:set var="totalSumber" value="${totalSumber + src.value}"/>
          </c:forEach>
          <c:set var="maxSumber" value="1"/>
          <c:forEach items="${sumberLead}" var="src">
            <c:if test="${src.value > maxSumber}">
              <c:set var="maxSumber" value="${src.value}"/>
            </c:if>
          </c:forEach>

          <div class="space-y-3">
            <c:forEach items="${sumberLead}" var="src">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg flex items-center justify-center text-base flex-shrink-0"
                     style="background: ${src.key == 'Instagram' ? '#fce7f3' :
                                          src.key == 'Facebook'  ? '#dbeafe' :
                                          src.key == 'Website'   ? '#dcfce7' :
                                          src.key == 'Referral'  ? '#fef3cd' :
                                          src.key == 'TikTok'    ? '#f3f4f6' :
                                          src.key == 'Walk-in'   ? '#ede9fe' : '#f3f4f6'}">
                  <c:choose>
                    <c:when test="${src.key == 'Instagram'}">📸</c:when>
                    <c:when test="${src.key == 'Facebook'}">👥</c:when>
                    <c:when test="${src.key == 'Website'}">🌐</c:when>
                    <c:when test="${src.key == 'Referral'}">🤝</c:when>
                    <c:when test="${src.key == 'TikTok'}">🎵</c:when>
                    <c:when test="${src.key == 'Walk-in'}">🚶</c:when>
                    <c:otherwise>📌</c:otherwise>
                  </c:choose>
                </div>

                <div class="flex-1 min-w-0">
                  <div class="flex justify-between text-xs mb-1">
                    <span class="font-medium text-gray-700 truncate">${src.key}</span>
                    
                    <%-- Membulatkan tampilan teks persentase kontribusi sumber --%>
                    <span class="text-gray-400 ml-2 flex-shrink-0">
                      <fmt:formatNumber value="${totalSumber > 0 ? (src.value * 100 / totalSumber) : 0}" maxFractionDigits="1"/>%
                    </span>
                  </div>
                  <div class="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                    <div class="h-full rounded-full src-bar-fill transition-all duration-500"
                         style="width: ${maxSumber > 0 ? (src.value * 100 / maxSumber) : 0}%;
                                background: ${src.key == 'Instagram' ? '#e1306c' :
                                              src.key == 'Facebook'  ? '#1877f2' :
                                              src.key == 'Website'   ? '#10b981' :
                                              src.key == 'Referral'  ? '#f59e0b' :
                                              src.key == 'TikTok'    ? '#374151' :
                                              src.key == 'Walk-in'   ? '#8b5cf6' : '#6b7280'}">
                    </div>
                  </div>
                </div>

                <div class="text-sm font-bold text-gray-800 flex-shrink-0">${src.value}</div>
              </div>
            </c:forEach>

            <c:if test="${empty sumberLead}">
              <div class="text-center py-6 text-gray-400 text-sm">Belum ada data sumber lead</div>
            </c:if>
          </div>
        </div>

        <!-- CONVERSION FUNNEL -->
        <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-sm font-bold text-gray-900">🔽 Conversion Funnel</h3>
            <span class="text-xs bg-purple-50 text-purple-600 font-semibold px-2 py-0.5 rounded-full">Per Status</span>
          </div>

          <%-- Hitung total lead untuk base funnel --%>
          <c:set var="funnelTotal" value="${totalLead}"/>
          <c:set var="funnelBase" value="${funnelTotal > 0 ? funnelTotal : 1}"/>

          <div class="space-y-2" id="funnel-container">
            <c:forEach items="${leadPerStatus}" var="entry" varStatus="fvs">
              <%-- 1. Hitung dan simpan nilai mentah ke variabel funnelPct --%>
              <c:set var="funnelPct" value="${funnelBase > 0 ? (entry.value * 100 / funnelBase) : 0}"/>
              
              <div class="funnel-step">
                <div class="flex items-center gap-2 mb-0.5">
                  <span class="text-xs font-medium text-gray-600 w-24 truncate">${entry.key}</span>
                  
                  <%-- 2. Membulatkan tampilan teks persentase di sebelah kanan (gunakan maxFractionDigits="0" atau "1") --%>
                  <span class="text-xs text-gray-400 ml-auto">
                    <fmt:formatNumber value="${funnelPct}" maxFractionDigits="1"/>%
                  </span>
                </div>
                
                <div class="h-6 bg-gray-100 rounded-lg overflow-hidden relative">
                  <%-- Gunakan nilai mentah ${funnelPct} untuk width style agar transisi grafik tetap akurat --%>
                  <div class="h-full rounded-lg funnel-fill flex items-center justify-end pr-2 transition-all duration-500"
                       style="width: ${funnelPct}%;
                              background: ${fvs.index == 0 ? '#3b82f6' :
                                            fvs.index == 1 ? '#f59e0b' :
                                            fvs.index == 2 ? '#8b5cf6' :
                                            fvs.index == 3 ? '#f97316' :
                                            fvs.index == 4 ? '#22c55e' :
                                            fvs.index == 5 ? '#ef4444' : '#9ca3af'}">
                    <span class="text-white text-xs font-bold">${entry.value}</span>
                  </div>
                </div>
              </div>
            </c:forEach>

            <c:if test="${empty leadPerStatus}">
              <div class="text-center py-6 text-gray-400 text-sm">Belum ada data status</div>
            </c:if>
          </div>
        </div>
      </div>

      <!-- ─────────────────────────────────
           SUMMARY TABLE: Rekap Bulanan
           ───────────────────────────────── -->
      <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-sm font-bold text-gray-900">📋 Rekap Bulanan</h3>
            <p class="text-xs text-gray-400 mt-0.5">Perbandingan performa 6 bulan terakhir</p>
          </div>
          <button onclick="downloadCSV()"
                  class="flex items-center gap-1.5 text-xs font-semibold text-gray-600 bg-gray-50 hover:bg-gray-100 border border-gray-200 px-3 py-1.5 rounded-lg transition-colors no-print">
            ⬇ Download CSV
          </button>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-sm" id="summary-table">
            <thead>
              <tr class="border-b border-gray-100">
                <th class="text-left text-xs font-semibold text-gray-400 uppercase tracking-wider py-2 pr-4">Bulan</th>
                <th class="text-right text-xs font-semibold text-gray-400 uppercase tracking-wider py-2 px-3">Lead Masuk</th>
                <th class="text-right text-xs font-semibold text-gray-400 uppercase tracking-wider py-2 px-3">Closing</th>
                <th class="text-right text-xs font-semibold text-gray-400 uppercase tracking-wider py-2 px-3">Closing Rate</th>
              </tr>
            </thead>
            <tbody>
              <%-- Inject data tren dari Map ke tabel --%>
              <%--
                trenLeadBulanan    : Map<String bulan, Integer jumlahLead>
                trenClosingBulanan : Map<String bulan, Integer jumlahClosing>
                Keduanya sudah diurutkan bulan terlama → terbaru oleh Controller.
                Karena JSP/EL tidak bisa lookup Map by key langsung dari dua Map berbeda
                secara paralel, kita gabungkan via JS setelah inject dua array terpisah.
              --%>
              <c:set var="baris" value="0"/>
              <c:forEach items="${trenLeadBulanan}" var="entry" varStatus="rvs">
                <c:set var="baris" value="${baris + 1}"/>
                <%-- Ambil nilai closing untuk bulan yang sama --%>
                <c:set var="closingBulanIni" value="${trenClosingBulanan[entry.key]}"/>
                <c:if test="${empty closingBulanIni}"><c:set var="closingBulanIni" value="0"/></c:if>
                <c:set var="rateBulan" value="${entry.value > 0 ? (closingBulanIni * 100 / entry.value) : 0}"/>
                <tr class="border-b border-gray-50 hover:bg-gray-50 transition-colors">
                  <td class="py-3 pr-4 font-semibold text-gray-800">${entry.key}</td>
                  <td class="py-3 px-3 text-right text-gray-700">${entry.value}</td>
                  <td class="py-3 px-3 text-right font-bold text-emerald-600">${closingBulanIni}</td>
                  <td class="py-3 px-3 text-right">
                    <span class="inline-block px-2 py-0.5 rounded-full text-xs font-bold
                      ${rateBulan >= 25 ? 'bg-emerald-50 text-emerald-700' : 'bg-amber-50 text-amber-700'}">
                      ${rateBulan}%
                    </span>
                  </td>
                </tr>
              </c:forEach>

              <c:if test="${empty trenLeadBulanan}">
                <tr>
                  <td colspan="4" class="py-8 text-center text-gray-400 text-sm">
                    Belum ada data tren bulanan
                  </td>
                </tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>

    </div><%-- end page body --%>
  </main>
</div><%-- end flex wrapper --%>


<!-- ══════════════════════════════════
     JAVASCRIPT
     ══════════════════════════════════ -->
<script>
// ── Tanggal hari ini ──────────────────────────────────────────────────────
(function() {
  const el = document.getElementById('date-label');
  if (el) {
    el.textContent = new Date().toLocaleDateString('id-ID', {
      weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });
  }
})();

// ══════════════════════════════════════════════════════════════════════════
//  INJECT DATA JAVA → JS
//  Menggunakan c:forEach untuk membangun array JS dari Map Java.
//  Pola: iterate entry.key dan entry.value dari setiap Map.
// ══════════════════════════════════════════════════════════════════════════

// ── Tren Lead Bulanan ─────────────────────────────────────────────────────
const trenLabels = [
  <c:forEach items="${trenLeadBulanan}" var="e" varStatus="s">
    '${e.key}'<c:if test="${!s.last}">,</c:if>
  </c:forEach>
];
const trenLeadData = [
  <c:forEach items="${trenLeadBulanan}" var="e" varStatus="s">
    ${e.value}<c:if test="${!s.last}">,</c:if>
  </c:forEach>
];

// ── Tren Closing Bulanan ──────────────────────────────────────────────────
// Gunakan label yang sama dengan trenLeadBulanan agar sumbu X selaras.
// Jika suatu bulan tidak ada di trenClosingBulanan, default = 0.
const trenClosingData = trenLabels.map(function(label) {
  const closingMap = {
    <c:forEach items="${trenClosingBulanan}" var="e" varStatus="s">
      '${e.key}': ${e.value}<c:if test="${!s.last}">,</c:if>
    </c:forEach>
  };
  return closingMap[label] !== undefined ? closingMap[label] : 0;
});

// ── Status Lead (untuk Donut) ─────────────────────────────────────────────
const statusLabels = [
  <c:forEach items="${leadPerStatus}" var="e" varStatus="s">
    '${e.key}'<c:if test="${!s.last}">,</c:if>
  </c:forEach>
];
const statusData = [
  <c:forEach items="${leadPerStatus}" var="e" varStatus="s">
    ${e.value}<c:if test="${!s.last}">,</c:if>
  </c:forEach>
];
const statusColors = (function() {
  const map = {
    'Baru':              '#3b82f6',
    'New Lead':          '#3b82f6',
    'Dihubungi':         '#f59e0b',
    'Contacted':         '#f59e0b',
    'Prospect':          '#8b5cf6',
    'Follow Up':         '#8b5cf6',
    'Negosiasi':         '#f97316',
    'Negotiation':       '#f97316',
    'Closing':           '#22c55e',
    'Closed':            '#22c55e',
    'Closed Won':        '#22c55e',
    'Batal':             '#ef4444',
    'Lost':              '#ef4444',
    'Tidak Merespons':   '#9ca3af',
  };
  const fallbacks = ['#3b82f6','#f59e0b','#8b5cf6','#f97316','#22c55e','#ef4444','#9ca3af'];
  return statusLabels.map(function(label, i) {
    return map[label] || fallbacks[i % fallbacks.length];
  });
})();

// ══════════════════════════════════════════════════════════════════════════
//  CHART.JS — BAR CHART (Tren Lead & Closing)
// ══════════════════════════════════════════════════════════════════════════
(function() {
  const ctx = document.getElementById('chartTren');
  if (!ctx) return;

  // Jika tidak ada data, tampilkan placeholder
  if (trenLabels.length === 0) {
    ctx.parentElement.innerHTML =
      '<div class="flex items-center justify-center h-full text-gray-400 text-sm">Belum ada data tren bulanan</div>';
    return;
  }

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: trenLabels,
      datasets: [
        {
          label: 'Lead Masuk',
          data: trenLeadData,
          backgroundColor: 'rgba(245, 158, 11, 0.85)',
          borderColor:     '#f59e0b',
          borderWidth: 1,
          borderRadius: 6,
          borderSkipped: false,
        },
        {
          label: 'Closing',
          data: trenClosingData,
          backgroundColor: 'rgba(59, 130, 246, 0.85)',
          borderColor:     '#3b82f6',
          borderWidth: 1,
          borderRadius: 6,
          borderSkipped: false,
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1a1a2e',
          titleFont: { size: 12, weight: '600' },
          bodyFont:  { size: 11 },
          padding: 10,
          callbacks: {
            afterBody: function(items) {
              const lead    = items[0]?.parsed?.y || 0;
              const closing = items[1]?.parsed?.y || 0;
              const rate    = lead > 0 ? Math.round((closing / lead) * 100) : 0;
              return ['─────────────', 'Closing Rate: ' + rate + '%'];
            }
          }
        }
      },
      scales: {
        x: {
          grid: { display: false },
          ticks: { font: { size: 11 }, color: '#9ca3af' }
        },
        y: {
          beginAtZero: true,
          grid: { color: '#f3f4f6' },
          ticks: {
            font: { size: 11 }, color: '#9ca3af',
            stepSize: 1,
            callback: function(val) { return Number.isInteger(val) ? val : null; }
          }
        }
      }
    }
  });
})();

// ══════════════════════════════════════════════════════════════════════════
//  CHART.JS — DOUGHNUT CHART (Status Lead)
// ══════════════════════════════════════════════════════════════════════════
(function() {
  const ctx = document.getElementById('chartDonut');
  if (!ctx) return;

  if (statusLabels.length === 0) {
    ctx.parentElement.innerHTML =
      '<div class="flex items-center justify-center h-full text-gray-400 text-sm">Belum ada data status</div>';
    return;
  }

  const total = statusData.reduce(function(a, b) { return a + b; }, 0);

  // Plugin untuk teks tengah donut
  const centerTextPlugin = {
    id: 'centerText',
    afterDraw: function(chart) {
      const { ctx: c, chartArea: { left, right, top, bottom } } = chart;
      const cx = (left + right)  / 2;
      const cy = (top  + bottom) / 2;
      c.save();
      c.font = 'bold 22px Inter, sans-serif';
      c.fillStyle = '#1a1a2e';
      c.textAlign = 'center';
      c.textBaseline = 'middle';
      c.fillText(total, cx, cy - 8);
      c.font = '11px Inter, sans-serif';
      c.fillStyle = '#9ca3af';
      c.fillText('Total', cx, cy + 12);
      c.restore();
    }
  };

  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: statusLabels,
      datasets: [{
        data: statusData,
        backgroundColor: statusColors,
        borderWidth: 2,
        borderColor: '#ffffff',
        hoverOffset: 6,
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '68%',
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1a1a2e',
          callbacks: {
            label: function(item) {
              const pct = total > 0 ? Math.round((item.parsed / total) * 100) : 0;
              return ' ' + item.label + ': ' + item.parsed + ' (' + pct + '%)';
            }
          }
        }
      }
    },
    plugins: [centerTextPlugin]
  });

  // Bangun legend manual di bawah donut
  const legendEl = document.getElementById('donut-legend');
  if (legendEl) {
    legendEl.innerHTML = statusLabels.map(function(label, i) {
      const val = statusData[i] || 0;
      const pct = total > 0 ? Math.round((val / total) * 100) : 0;
      return '<div class="flex items-center gap-2 text-xs">'
        + '<span class="w-2.5 h-2.5 rounded-full flex-shrink-0" style="background:' + statusColors[i] + '"></span>'
        + '<span class="flex-1 text-gray-600 truncate">' + label + '</span>'
        + '<span class="font-semibold text-gray-800">' + val + '</span>'
        + '<span class="text-gray-400 w-8 text-right">' + pct + '%</span>'
        + '</div>';
    }).join('');
  }
})();

// ══════════════════════════════════════════════════════════════════════════
//  DOWNLOAD CSV
// ══════════════════════════════════════════════════════════════════════════
function downloadCSV() {
  const table   = document.getElementById('summary-table');
  if (!table) return;

  const rows    = table.querySelectorAll('tr');
  const csvRows = [];

  rows.forEach(function(row) {
    const cols = row.querySelectorAll('th, td');
    const data = Array.from(cols).map(function(col) {
      return '"' + col.innerText.replace(/"/g, '""').trim() + '"';
    });
    csvRows.push(data.join(','));
  });

  const csv  = '\uFEFF' + csvRows.join('\n'); // BOM untuk Excel
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url  = URL.createObjectURL(blob);
  const a    = document.createElement('a');
  a.href     = url;
  a.download = 'laporan-leadestate.csv';
  a.click();
  URL.revokeObjectURL(url);
}
</script>

</body>
</html>
