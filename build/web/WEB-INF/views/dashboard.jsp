<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"
    uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fn"
    uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Dashboard — LeadEstate</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

    /* ── SIDEBAR CSS ── */
    :root {
        --sidebar-bg:       #0f1923;
        --sidebar-border:   #1e3040;
        --brand-gold:       #c9a84c;
        --brand-gold-light: #e8c97a;
        --gold: #c9a84c;
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

    .chart-bar { transition: height .5s ease; min-height: 4px; border-radius: 4px 4px 0 0; }
  </style>
</head>
<body class="bg-gray-50 text-gray-800 flex h-screen overflow-hidden" style="font-family:'DM Sans',sans-serif;">

<%
    /* Ambil user dari session */
    com.leadestate.model.User userLogin = (com.leadestate.model.User) session.getAttribute("userLogin");
    if (userLogin == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String namaUser  = userLogin.getName();
    String roleName  = userLogin.getRoleName();
    boolean isAdmin  = "Admin".equalsIgnoreCase(roleName);

    /* Inisial avatar */
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
        <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-bold cursor-default"
             style="background:var(--brand-gold); border:1px solid var(--brand-gold); color:#0b1622;">
            <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#0b1622" stroke-width="1.8">
                    <rect x="3" y="3" width="7" height="7" rx="1.5"/>
                    <rect x="14" y="3" width="7" height="7" rx="1.5"/>
                    <rect x="3" y="14" width="7" height="7" rx="1.5"/>
                    <rect x="14" y="14" width="7" height="7" rx="1.5"/>
                </svg>
            </span>
            Dashboard
        </div>

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
            <c:if test="${followUpPending > 0}">
                <span class="ml-auto text-[10px] font-bold min-w-[18px] h-[18px] px-1.5 rounded-full flex items-center justify-center"
                      style="background:#ef4444; color:#fff;">${followUpPending}</span>
            </c:if>
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

<%-- MAIN CONTENT --%>
<main class="flex-1 flex flex-col overflow-hidden">

  <%-- Topbar --%>
  <div class="flex items-center justify-between px-6 py-3 bg-white border-b border-gray-200 flex-shrink-0">
    <h1 class="text-base font-semibold text-gray-800">Dashboard Overview</h1>
    <div class="flex items-center gap-3">
      <%-- Flash message --%>
      <c:if test="${not empty flashPesan}">
        <span class="text-xs bg-green-500/20 text-green-400 border border-green-500/30 px-3 py-1 rounded-full">
          ${flashPesan}
        </span>
      </c:if>
      <div class="flex items-center gap-1.5 bg-gray-100 px-3 py-1.5 rounded-lg text-xs text-gray-500">
        <span>📅</span>
        <span id="tanggalHariIni"></span>
      </div>
    </div>
  </div>

  <%-- Scrollable content area --%>
  <div class="flex-1 overflow-y-auto px-6 py-5 space-y-5">

    <%-- KPI CARDS --%>
    <div class="grid grid-cols-4 gap-4">

      <%-- Card 1: Total Lead Aktif --%>
      <div class="bg-white rounded-xl p-4 border border-gray-200 hover:border-yellow-400 transition-colors shadow-sm">
        <div class="flex items-center justify-between mb-3">
          <div class="w-9 h-9 rounded-lg bg-yellow-500/15 flex items-center justify-center text-lg">📋</div>
          <span class="text-xs text-green-500 font-medium">↑ Aktif</span>
        </div>
        <div class="text-2xl font-bold text-gray-800 mb-0.5">${totalLeadAktif}</div>
        <div class="text-xs font-medium text-gray-600">Total Lead Aktif</div>
        <div class="text-[11px] text-gray-400 mt-0.5">Selain Closed Lost</div>
      </div>

      <%-- Card 2: Follow Up Pending --%>
      <div class="bg-white rounded-xl p-4 border border-gray-200 hover:border-yellow-400 transition-colors shadow-sm">
        <div class="flex items-center justify-between mb-3">
          <div class="w-9 h-9 rounded-lg bg-blue-500/15 flex items-center justify-center text-lg">🔔</div>
          <span class="text-xs text-blue-400 font-medium">Pending</span>
        </div>
        <div class="text-2xl font-bold text-gray-800 mb-0.5">${followUpPending}</div>
        <div class="text-xs font-medium text-gray-600">Follow Up Hari Ini</div>
        <div class="text-[11px] text-gray-500 mt-0.5">Cek reminder</div>
      </div>

      <%-- Card 3: Lead Tertunda --%>
      <div class="bg-white rounded-xl p-4 border border-gray-200 hover:border-yellow-400 transition-colors shadow-sm">
        <div class="flex items-center justify-between mb-3">
          <div class="w-9 h-9 rounded-lg bg-red-500/15 flex items-center justify-center text-lg">⏳</div>
          <span class="text-xs text-red-400 font-medium">↑ Perlu aksi</span>
        </div>
        <div class="text-2xl font-bold text-gray-800 mb-0.5">${leadTertunda}</div>
        <div class="text-xs font-medium text-gray-600">Lead Tertunda</div>
        <div class="text-[11px] text-gray-500 mt-0.5">Status Follow Up</div>
      </div>

      <%-- Card 4: Closing Bulan Ini --%>
      <div class="bg-white rounded-xl p-4 border border-gray-200 hover:border-yellow-400 transition-colors shadow-sm">
        <div class="flex items-center justify-between mb-3">
          <div class="w-9 h-9 rounded-lg bg-green-500/15 flex items-center justify-center text-lg">✅</div>
          <span class="text-xs text-green-400 font-medium">Closed Won</span>
        </div>
        <div class="text-2xl font-bold text-gray-800 mb-0.5">${closingBulanIni}</div>
        <div class="text-xs font-medium text-gray-600">Closing Bulan Ini</div>
        <div class="text-[11px] text-gray-500 mt-0.5">Target: 15</div>
      </div>
    </div>

<%-- GRAFIK + TOP SALES --%>
    <div class="grid grid-cols-5 gap-4">

      <%-- Grafik Closing Rate --%>
      <div class="col-span-3 bg-white rounded-xl p-5 border border-gray-200 shadow-sm">
        <div class="flex items-start justify-between mb-4">
          <div>
            <div class="text-sm font-semibold text-gray-800">Grafik Closing Rate</div>
            <div class="text-[11px] text-gray-400 mt-0.5">Perbandingan lead masuk vs closing</div>
          </div>
          <div class="flex gap-1.5">
            <span class="text-[11px] text-[#c9a84c] bg-yellow-500/10 px-2.5 py-1 rounded-md font-medium cursor-pointer">6 Bulan</span>
          </div>
        </div>

        <%-- Bar chart data dari Controller --%>
        <div class="flex items-end gap-3 h-28">
          <%
            String[] bulanLabel = {"Jan","Feb","Mar","Apr","Mei","Jun"};
            int[] fakeLead = {8, 12, 7, 15, 10, 14};
            int[] fakeClosing = {3, 5, 2, 8, 4, 7};
            int maxVal = 15;
            for (int bi = 0; bi < bulanLabel.length; bi++) {
              int h1 = Math.round(((float)fakeLead[bi] / maxVal) * 96);
              int h2 = Math.round(((float)fakeClosing[bi] / maxVal) * 96);
          %>
          <div class="flex flex-col items-center gap-1 flex-1">
            <div class="flex items-end gap-0.5 w-full justify-center" style="height:96px">
              <div class="chart-bar w-3" style="background:#c9a84c; height:<%= h1 %>px" title="Lead: <%= fakeLead[bi] %>"></div>
              <div class="chart-bar bg-blue-500 w-3" style="height:<%= h2 %>px" title="Closing: <%= fakeClosing[bi] %>"></div>
            </div>
            <span class="text-[10px] text-gray-400"><%= bulanLabel[bi] %></span>
          </div>
          <% } %>
        </div>

        <div class="flex gap-4 mt-3">
          <div class="flex items-center gap-1.5">
            <div class="w-2.5 h-2.5 rounded-sm" style="background:#c9a84c;"></div>
            <span class="text-[11px] text-gray-500">Lead Masuk</span>
          </div>
          <div class="flex items-center gap-1.5">
            <div class="w-2.5 h-2.5 rounded-sm bg-blue-500"></div>
            <span class="text-[11px] text-gray-500">Closing</span>
          </div>
        </div>
      </div>

      <%-- <div class="col-span-3 bg-white rounded-xl p-5 border border-gray-200 shadow-sm"> --%>

     <%-- Top Sales Leaderboard --%>
      <div class="col-span-2 bg-white rounded-xl p-5 border border-gray-200 shadow-sm">
        <div class="mb-4">
          <div class="text-sm font-semibold text-gray-800">🏆 Top Sales</div>
          <div class="text-[11px] text-gray-400 mt-0.5">Berdasarkan jumlah closing</div>
        </div>

        <div class="space-y-3">
          <c:choose>
            <c:when test="${empty topSales}">
              <div class="text-center text-gray-400 text-xs py-6">Belum ada data sales</div>
            </c:when>
            <c:otherwise>
              <c:forEach var="row" items="${topSales}" varStatus="loop">
                <%-- row = Object[]{User, totalLead, totalClosing} --%>
                <div class="flex items-center gap-3">
                  
                  <%-- Rank badge --%>
                  <c:choose>
                    <c:when test="${loop.index == 0}">
                      <div class="w-5 h-5 rounded-full flex items-center justify-center text-[10px] font-bold flex-shrink-0" style="background:#c9a84c; color:#fff;">1</div>
                    </c:when>
                    <c:when test="${loop.index == 1}">
                      <div class="w-5 h-5 rounded-full bg-gray-200 flex items-center justify-center text-gray-700 text-[10px] font-bold flex-shrink-0">2</div>
                    </c:when>
                    <c:when test="${loop.index == 2}">
                      <div class="w-5 h-5 rounded-full bg-orange-100 flex items-center justify-center text-orange-700 text-[10px] font-bold flex-shrink-0">3</div>
                    </c:when>
                    <c:otherwise>
                      <div class="w-5 h-5 rounded-full bg-gray-100 flex items-center justify-center text-gray-400 text-[10px] font-bold flex-shrink-0">${loop.index + 1}</div>
                    </c:otherwise>
                  </c:choose>

                  <%-- Avatar --%>
                  <div class="w-7 h-7 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 text-[10px] font-bold flex-shrink-0">
                    ${fn:substring(row[0].name, 0, 1)}
                  </div>

                  <%-- Info --%>
                  <div class="flex-1 min-w-0">
                    <div class="text-xs font-medium text-gray-800 truncate">${row[0].name}</div>
                    <div class="text-[10px] text-gray-400">${row[1]} lead</div>
                  </div>

                  <%-- Closing count --%>
                  <div class="text-xs font-bold text-green-600 flex-shrink-0">${row[2]} ✅</div>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
       </div>

   <%-- ── REMINDER HARI INI ── DISAMAKAN KE TEMA TERANG (PUTIH) --%>
    <div class="bg-white rounded-xl p-5 border border-gray-200 shadow-sm">
      <div class="flex items-center justify-between mb-4">
        <div>
          <div class="text-sm font-semibold text-gray-800">🔔 Reminder Hari Ini</div>
          <div class="text-[11px] text-gray-400 mt-0.5">Lead yang perlu di-follow up</div>
        </div>
        <%-- Merapikan double double quote pada style --%>
        <a href="${pageContext.request.contextPath}/followup"
           class="text-[11px] hover:underline font-medium" style="color:#c9a84c;">Lihat semua →</a>
      </div>

      <c:choose>
        <c:when test="${empty reminderHariIni}">
          <div class="text-center text-gray-400 text-xs py-8">
            <div class="text-2xl mb-2">🎉</div>
            Tidak ada reminder hari ini
          </div>
        </c:when>
        <c:otherwise>
          <div class="space-y-3">
            <c:forEach var="item" items="${reminderHariIni}" varStatus="loop">
              <%-- item = Object[]{FollowUp, Lead, namasSales} --%>
              <%-- Mengubah list baris menjadi abu-abu terang (bg-gray-50) yang soft --%>
              <div class="flex items-center gap-4 p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors border border-gray-100">
                <%-- Avatar lead --%>
                <div class="w-9 h-9 rounded-full bg-amber-500 flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                  <c:choose>
                    <c:when test="${not empty item[1]}">
                      ${fn:substring(item[1].name, 0, 1)}
                    </c:when>
                    <c:otherwise>?</c:otherwise>
                  </c:choose>
                </div>

                <%-- Info lead --%>
                <div class="flex-1 min-w-0">
                  <div class="text-xs font-medium text-gray-800">
                    <c:choose>
                      <c:when test="${not empty item[1]}">${item[1].name}</c:when>
                      <c:otherwise>Lead #${item[0].leadId}</c:otherwise>
                    </c:choose>
                  </div>
                  <div class="text-[11px] text-gray-500 mt-0.5">
                    Sales: <span class="font-medium text-gray-700">${item[2]}</span> &nbsp;·&nbsp; ${item[0].notes}
                  </div>
                </div>

                <%-- Status followup (Diseragamkan menggunakan warna pastel khas Light Mode) --%>
                <c:choose>
                  <c:when test="${item[0].status == 'Pending'}">
                    <span class="text-[10px] bg-yellow-100 text-yellow-700 border border-yellow-200 px-2.5 py-0.5 rounded-full flex-shrink-0 font-medium">Pending</span>
                  </c:when>
                  <c:otherwise>
                    <span class="text-[10px] bg-gray-100 text-gray-600 border border-gray-200 px-2.5 py-0.5 rounded-full flex-shrink-0 font-medium">${item[0].status}</span>
                  </c:otherwise>
                </c:choose>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div><%-- end scrollable content --%>
</main>

<script>
  document.getElementById('tanggalHariIni').textContent =
    new Date().toLocaleDateString('id-ID', {
      weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });
</script>
</body>
</html>
