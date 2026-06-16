<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.leadestate.model.User" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reminder &amp; Follow Up — LeadEstate</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
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
            --success:          #22c55e;
            --warning:          #f59e0b;
            --danger:           #ef4444;
            --info:             #3b82f6;
        }
        body { font-family: 'DM Sans', sans-serif; }

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

        .lead-card-item.selected {
            background: #fef3cd;
            border-left: 3px solid var(--brand-gold);
        }
        .lead-card-item:hover:not(.selected) { background: #fffbf0; }

        .tl-seg-done    { background: #e5e7eb; }
        .tl-seg-current { background: var(--brand-gold); }

        .tag-today  { background: #dcfce7; color: #16a34a; }
        .tag-soon   { background: #fef3cd; color: #b45309; }
        .tag-late   { background: #fee2e2; color: #dc2626; }
        .tag-done   { background: #e0e7ff; color: #4338ca; }

        .sp-belum   { background: #fee2e2; color: #dc2626; }
        .sp-proses  { background: #fef3cd; color: #b45309; }
        .sp-selesai { background: #dcfce7; color: #16a34a; }

        .wa-chip { transition: all .15s; }
        .wa-chip.sel, .wa-chip:hover {
            background: #16a34a; color: #fff; border-color: #16a34a;
        }

        .scroll-thin::-webkit-scrollbar       { width: 4px; }
        .scroll-thin::-webkit-scrollbar-track { background: transparent; }
        .scroll-thin::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }

        #searchInput:focus { border-color: var(--brand-gold); background: #fff; }
        #notesArea:focus { border-color: var(--brand-gold); background: #fff; }
    </style>
</head>
<body class="bg-[#f5f4f0] text-[#1a1a2e] overflow-hidden">

<%
    User userLogin = (User) session.getAttribute("userLogin");
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

    String tabAktif = (String) request.getAttribute("tabAktif");
    if (tabAktif == null) tabAktif = "hariIni";

    Integer jumlahNotif = (Integer) request.getAttribute("jumlahNotif");
    if (jumlahNotif == null) jumlahNotif = 0;

    Integer leadIdDipilih = (Integer) request.getAttribute("leadIdDipilih");

    java.util.Locale locale = new java.util.Locale("id", "ID");
    java.text.SimpleDateFormat sdfHari = new java.text.SimpleDateFormat("EEEE, d MMMM yyyy", locale);
    String tanggalSekarang = sdfHari.format(new java.util.Date());
%>

<div class="flex h-screen overflow-hidden">

    <aside class="sidebar-grid w-64 flex-shrink-0 flex flex-col relative overflow-hidden"
           style="background:var(--sidebar-bg); border-right:1px solid var(--sidebar-border);">
        <div class="flex items-center gap-2.5 px-4 py-5" style="border-bottom:1px solid var(--sidebar-border);">
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

        <nav class="flex-1 overflow-y-auto scroll-thin px-3 py-3 flex flex-col gap-1.5">
            <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1" style="color:#3a5068; letter-spacing:1.2px;">Menu Utama</div>

            <a href="${pageContext.request.contextPath}/dashboard" class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all" style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;" onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';" onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                        <rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>
                    </svg>
                </span>
                Dashboard
            </a>

            <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-bold cursor-default" style="background:var(--brand-gold); border:1px solid var(--brand-gold); color:#0b1622;">
                <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#0b1622" stroke-width="1.8">
                        <circle cx="12" cy="12" r="10"/><polygon points="10,8 16,12 10,16" fill="#0b1622"/>
                    </svg>
                </span>
                Reminder &amp; Follow-Up
                <c:if test="${jumlahNotif > 0}">
                    <span class="ml-auto text-[10px] font-bold min-w-[18px] h-[18px] px-1.5 rounded-full flex items-center justify-center" style="background:var(--danger); color:#fff;">${jumlahNotif}</span>
                </c:if>
            </div>

            <a href="${pageContext.request.contextPath}/lead" class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all" style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;" onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';" onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
                    </svg>
                </span>
                Data Lead
            </a>

            <% if (isAdmin) { %>
            <a href="${pageContext.request.contextPath}/manajemen-sales" class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all" style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;" onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';" onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                        <rect x="2" y="3" width="20" height="18" rx="2"/><circle cx="12" cy="10" r="3"/><path d="M7 21v-1a5 5 0 0 1 10 0v1"/>
                    </svg>
                </span>
                Manajemen Sales
            </a>

            <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1" style="color:#3a5068; letter-spacing:1.2px;">Laporan</div>
            <a href="${pageContext.request.contextPath}/laporan" class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all" style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;" onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';" onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                        <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6"  y1="20" x2="6"  y2="14"/>
                    </svg>
                </span>
                Laporan &amp; Statistik
            </a>
            <% } %>

            <a href="${pageContext.request.contextPath}/settings.jsp" class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all" style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;" onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';" onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <span class="w-4.5 h-4.5 flex items-center justify-center flex-shrink-0">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                        <circle cx="12" cy="12" r="3"/><path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
                    </svg>
                </span>
                Pengaturan
            </a>
        </nav>

        <div class="flex flex-col gap-1 px-4 py-3.5 flex-shrink-0" style="border-top:1px solid var(--sidebar-border);">
            <div class="flex items-center gap-2.5">
                <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold flex-shrink-0" style="background:linear-gradient(135deg,var(--brand-gold),#e8c97a); color:var(--sidebar-bg); border:2px solid rgba(201,168,76,.4);">
                    <%= inisialUser %>
                </div>
                <div>
                    <div class="text-sm font-semibold text-white"><%= namaUser %></div>
                    <div class="text-[11px] mt-px" style="color:#5a7a94;"><%= roleName %></div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/AuthController?action=logout" class="flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs transition-all mt-1" style="color:#5a7a94; border:1px solid rgba(255,255,255,.06); background:rgba(255,255,255,.02);" onmouseover="this.style.background='rgba(239,68,68,.15)';this.style.color='#ef4444';this.style.borderColor='rgba(239,68,68,.3)';" onmouseout="this.style.background='rgba(255,255,255,.02)';this.style.color='#5a7a94';this.style.borderColor='rgba(255,255,255,.06)';">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                Keluar
            </a>
        </div>
    </aside>

    <main class="flex-1 flex flex-col overflow-hidden">
        <div class="flex items-center justify-between flex-shrink-0 px-7" style="height:60px; background:#fff; border-bottom:1px solid var(--border); z-index:50;">
            <div class="text-xl font-semibold" style="font-family:'Playfair Display',serif; color:var(--text-primary);">
                Reminder &amp; Follow Up
            </div>
            <div class="flex items-center gap-3.5">
                <div class="flex items-center gap-1.5 rounded-full px-3.5 py-1.5 text-xs font-medium" style="background:var(--content-bg); border:1px solid var(--border); color:var(--text-secondary);">
                    📅 <%= tanggalSekarang %>
                </div>
                <a href="${pageContext.request.contextPath}/reminder?action=cekOverdue" title="Cek & update reminder overdue" class="w-9 h-9 rounded-full flex items-center justify-center transition-all" style="background:var(--content-bg); border:1px solid var(--border); color:var(--text-secondary);" onmouseover="this.style.borderColor='var(--brand-gold)';" onmouseout="this.style.borderColor='var(--border)';">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/>
                    </svg>
                </a>
            </div>
        </div>

        <div class="flex flex-1 overflow-hidden">
            <div class="w-[380px] flex-shrink-0 flex flex-col overflow-hidden" style="background:#fff; border-right:1px solid var(--border);">
                <div class="flex gap-1.5 p-3.5 flex-shrink-0" style="border-bottom:1px solid var(--border);">
                    <%
                        String[] tabKeys   = {"semua", "hariIni", "tertunda", "selesai"};
                        String[] tabLabels = {"Semua", "Hari Ini", "Tertunda", "Selesai"};
                        for (int i = 0; i < tabKeys.length; i++) {
                            boolean isActive = tabKeys[i].equals(tabAktif);
                    %>
                    <a href="${pageContext.request.contextPath}/reminder?tab=<%= tabKeys[i] %>" class="flex-1 text-center py-2 rounded-lg text-xs font-medium no-underline transition-all" style="<%= isActive ? "background:var(--brand-gold); color:#0b1622; font-weight:700; border:1px solid var(--brand-gold);" : "color:var(--text-secondary); border:1px solid transparent;" %>" onmouseover="<%= !isActive ? "this.style.background='var(--content-bg)';" : "" %>" onmouseout="<%= !isActive ? "this.style.background='transparent';" : "" %>">
                        <%= tabLabels[i] %>
                    </a>
                    <% } %>
                </div>

                <div class="px-4 py-2.5 flex-shrink-0" style="border-bottom:1px solid var(--border);">
                    <div class="relative">
                        <svg class="absolute left-2.5 top-1/2 -translate-y-1/2" width="14" height="14" viewBox="0 0 24 24" fill="currentColor" style="color:var(--text-muted);">
                            <path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                        </svg>
                        <input id="searchInput" type="text" placeholder="Cari nama atau properti..." class="w-full rounded-lg pl-8 pr-3 py-2 text-[13px] outline-none transition-all" style="border:1px solid var(--border); background:var(--content-bg); color:var(--text-primary); font-family:'DM Sans',sans-serif;">
                    </div>
                </div>

                <div class="flex justify-between items-center px-4 pt-2.5 pb-1.5 flex-shrink-0">
                    <span class="text-[13px] font-semibold" style="color:var(--text-primary);">Daftar Reminder</span>
                    <span id="leadCount" class="text-[11px]" style="color:var(--text-muted);">memuat...</span>
                </div>

                <div id="leadList" class="overflow-y-auto flex-1 scroll-thin">

                    <%-- TAB: SEMUA --%>
                    <c:if test="${tabAktif == 'semua' || tabAktif == null}">
                        <c:choose>
                            <c:when test="${empty followUpSemua}">
                                <div class="flex flex-col items-center justify-center gap-2.5 p-10" style="color:var(--text-muted);">
                                    <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor" style="opacity:.3;">
                                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                                    </svg>
                                    <p class="text-sm text-center">Belum ada follow-up sama sekali</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="fu" items="${followUpSemua}">
                                    <div class="lead-card-item flex items-center gap-3 px-4 py-3 cursor-pointer transition-all ${leadIdDipilih == fu.leadId ? 'selected' : ''}"
                                         style="border-bottom:1px solid #f3f4f6; ${leadIdDipilih == fu.leadId ? 'background:#fef3cd; border-left:3px solid var(--brand-gold);' : ''}"
                                         onclick="pilihLead(${fu.leadId}, ${fu.id})">
                                        <div class="w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-white flex-shrink-0"
                                             style="background:${fu.status == 'Selesai' ? '#6366f1' : '#10b981'};">
                                            ${not empty fu.leadName ? fn:substring(fu.leadName, 0, 1) : 'FU'}
                                        </div>
                                        <div class="flex-1 min-w-0">
                                            <div class="text-[13px] font-semibold truncate">${not empty fu.leadName ? fu.leadName : 'Lead #'}${empty fu.leadName ? fu.leadId : ''}</div>
                                            <div class="text-[11px] mt-px truncate" style="color:var(--text-muted);">
                                                <fmt:formatDate value="${fu.followupDate}" pattern="d MMM yyyy"/>
                                            </div>
                                        </div>
                                        <div class="text-right flex-shrink-0">
                                            <span class="inline-flex items-center px-1.5 py-0.5 rounded-full text-[10px] font-bold ${fu.status == 'Selesai' ? 'tag-done' : 'tag-today'}">
                                                ${fu.status}
                                            </span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <%-- TAB: HARI INI --%>
                    <c:if test="${tabAktif == 'hariIni'}">
                        <c:choose>
                            <c:when test="${empty followUpHariIni}">
                                <div class="flex flex-col items-center justify-center gap-2.5 p-10" style="color:var(--text-muted);">
                                    <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor" style="opacity:.3;">
                                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                                    </svg>
                                    <p class="text-sm text-center">Tidak ada follow-up untuk hari ini</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="fu" items="${followUpHariIni}">
                                    <div class="lead-card-item flex items-center gap-3 px-4 py-3 cursor-pointer transition-all ${leadIdDipilih == fu.leadId ? 'selected' : ''}"
                                         style="border-bottom:1px solid #f3f4f6; ${leadIdDipilih == fu.leadId ? 'background:#fef3cd; border-left:3px solid var(--brand-gold);' : ''}"
                                         onclick="pilihLead(${fu.leadId}, ${fu.id})">
                                        <div class="w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-white flex-shrink-0"
                                             style="background:#f59e0b;">
                                            ${not empty fu.leadName ? fn:substring(fu.leadName, 0, 1) : 'FU'}
                                        </div>
                                        <div class="flex-1 min-w-0">
                                            <div class="text-[13px] font-semibold truncate">${not empty fu.leadName ? fu.leadName : 'Lead #'}${empty fu.leadName ? fu.leadId : ''}</div>
                                            <div class="text-[11px] mt-px truncate" style="color:var(--text-muted);">
                                                <fmt:formatDate value="${fu.followupDate}" pattern="d MMM yyyy"/>
                                            </div>
                                            <div class="text-[11px] mt-0.5 truncate" style="color:var(--text-secondary);">
                                                📝 <c:out value="${fn:substring(fu.notes, 0, 30)}"/>...
                                            </div>
                                        </div>
                                        <div class="text-right flex-shrink-0">
                                            <div class="text-xs font-semibold" style="color:var(--text-secondary);">
                                                <fmt:formatDate value="${fu.followupDate}" pattern="d MMM yyyy"/>
                                            </div>
                                            <span class="inline-flex items-center px-1.5 py-0.5 rounded-full text-[10px] font-bold mt-1 tag-today">Hari ini</span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <%-- TAB: TERTUNDA --%>
                    <c:if test="${tabAktif == 'tertunda'}">
                        <c:choose>
                            <c:when test="${empty followUpTertunda}">
                                <div class="flex flex-col items-center justify-center gap-2.5 p-10" style="color:var(--text-muted);">
                                    <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor" style="opacity:.3;">
                                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                                    </svg>
                                    <p class="text-sm text-center">Tidak ada follow-up tertunda 🎉</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="fu" items="${followUpTertunda}">
                                    <div class="lead-card-item flex items-center gap-3 px-4 py-3 cursor-pointer transition-all ${leadIdDipilih == fu.leadId ? 'selected' : ''}"
                                         style="border-bottom:1px solid #f3f4f6;" onclick="pilihLead(${fu.leadId}, ${fu.id})">
                                        <div class="w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-white flex-shrink-0"
                                             style="background:#ef4444;">
                                            ${not empty fu.leadName ? fn:substring(fu.leadName, 0, 1) : 'FU'}
                                        </div>
                                        <div class="flex-1 min-w-0">
                                            <div class="text-[13px] font-semibold truncate">${not empty fu.leadName ? fu.leadName : 'Lead #'}${empty fu.leadName ? fu.leadId : ''}</div>
                                            <div class="text-[11px] mt-px truncate" style="color:var(--text-muted);">
                                                <fmt:formatDate value="${fu.followupDate}" pattern="d MMM yyyy"/>
                                            </div>
                                        </div>
                                        <div class="text-right flex-shrink-0">
                                            <span class="inline-flex items-center px-1.5 py-0.5 rounded-full text-[10px] font-bold tag-late">Tertunda</span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <%-- TAB: SELESAI --%>
                    <c:if test="${tabAktif == 'selesai'}">
                        <c:choose>
                            <c:when test="${empty followUpSelesai}">
                                <div class="flex flex-col items-center justify-center gap-2.5 p-10" style="color:var(--text-muted);">
                                    <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor" style="opacity:.3;">
                                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/>
                                    </svg>
                                    <p class="text-sm text-center">Belum ada follow-up yang selesai</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="fu" items="${followUpSelesai}">
                                    <div class="lead-card-item flex items-center gap-3 px-4 py-3 cursor-pointer transition-all ${leadIdDipilih == fu.leadId ? 'selected' : ''}"
                                         style="border-bottom:1px solid #f3f4f6; opacity:.7;" onclick="pilihLead(${fu.leadId}, ${fu.id})">
                                        <div class="w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-white flex-shrink-0"
                                             style="background:#6366f1;">
                                            ${not empty fu.leadName ? fn:substring(fu.leadName, 0, 1) : 'FU'}
                                        </div>
                                        <div class="flex-1 min-w-0">
                                            <div class="text-[13px] font-semibold truncate">${not empty fu.leadName ? fu.leadName : 'Lead #'}${empty fu.leadName ? fu.leadId : ''}</div>
                                            <div class="text-[11px] mt-px truncate" style="color:var(--text-muted);">
                                                <fmt:formatDate value="${fu.followupDate}" pattern="d MMM yyyy"/>
                                            </div>
                                        </div>
                                        <div class="text-right flex-shrink-0">
                                            <span class="inline-flex items-center px-1.5 py-0.5 rounded-full text-[10px] font-bold tag-done">Selesai</span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </div>

            <div class="flex-1 flex flex-col overflow-hidden" style="background:var(--content-bg);">
                <div id="rightPanel" class="flex-1 overflow-y-auto scroll-thin p-5 flex flex-col gap-4">
                    <c:choose>
                        <c:when test="${empty riwayatFollowUp && leadIdDipilih == null}">
                            <div class="flex-1 flex flex-col items-center justify-center gap-2.5 p-10 h-full" style="color:var(--text-muted);">
                                <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor" style="opacity:.2;">
                                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z"/>
                                </svg>
                                <p class="text-sm text-center leading-relaxed">
                                    Pilih lead dari daftar<br>untuk melihat detail follow-up
                                </p>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <div class="rounded-2xl p-5" style="background:var(--card-bg); border:1px solid var(--border);">
                                <div class="flex items-center gap-3.5 mb-4">
                                    <div class="w-14 h-14 rounded-full flex items-center justify-center text-xl font-bold text-white flex-shrink-0"
                                         style="background:linear-gradient(135deg,var(--brand-gold),#e8c97a);">
                                        ${not empty namaLeadDipilih ? fn:substring(namaLeadDipilih, 0, 1) : 'L'}
                                    </div>
                                    <div class="flex-1">
                                        <div class="text-lg font-bold">${not empty namaLeadDipilih ? namaLeadDipilih : 'Lead #'}${empty namaLeadDipilih ? leadIdDipilih : ''}</div>
                                        <div class="text-[13px] mt-0.5" style="color:var(--text-muted);">
                                            Klik "WhatsApp" untuk menghubungi langsung
                                        </div>
                                        <div class="flex gap-2 mt-2">
                                            <button onclick="bukaWhatsApp()" class="flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[11px] font-semibold cursor-pointer transition-all" style="background:#dcfce7; color:#16a34a; border:none;" onmouseover="this.style.background='#bbf7d0';" onmouseout="this.style.background='#dcfce7';">
                                                💬 WhatsApp
                                            </button>
                                        </div>
                                    </div>
                                        <c:set var="statusBadge" value="Pending" />
                                        <c:forEach var="fu" items="${riwayatFollowUp}">
                                            <c:if test="${param.followupId == fu.id}">
                                                <c:set var="statusBadge" value="${fu.status}" />
                                            </c:if>
                                        </c:forEach>

                                        <div class="text-right">
                                            <c:choose>
                                                <c:when test="${statusBadge == 'Selesai'}">
                                                    <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold" style="background:#dcfce7; color:#16a34a;">Selesai</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold" style="background:#fef3cd; color:#b45309;">Sedang Diproses</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="text-[11px] mt-1.5" style="color:var(--text-muted);">Sales: <%= namaUser%></div>
                                        </div>
                                </div>

                                <div class="grid grid-cols-2 gap-3">
                                    <div>
                                        <div class="text-[11px] font-semibold uppercase tracking-wide mb-1" style="color:var(--text-muted);">Follow-up Terakhir</div>
                                        <div class="text-[13px] font-medium" style="color:var(--text-primary);">
                                            <c:choose>
                                                <c:when test="${not empty riwayatFollowUp}">
                                                    <fmt:formatDate value="${riwayatFollowUp[0].followupDate}" pattern="d MMM yyyy"/>
                                                </c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div>
                                        <div class="text-[11px] font-semibold uppercase tracking-wide mb-1" style="color:var(--text-muted);">Total Follow-up</div>
                                        <div class="text-[13px] font-medium" style="color:var(--text-primary);">
                                            <c:out value="${fn:length(riwayatFollowUp)}"/> kali
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="rounded-2xl p-5" style="background:var(--card-bg); border:1px solid var(--border);">
                                <div class="text-[13px] font-bold mb-3 flex items-center gap-1.5">🕐 Riwayat Follow Up</div>
                                <c:choose>
                                    <c:when test="${empty riwayatFollowUp}">
                                        <p class="text-[13px]" style="color:var(--text-muted);">Belum ada riwayat follow-up.</p>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="flex flex-col">
                                            <c:forEach var="fu" items="${riwayatFollowUp}" varStatus="loop">
                                                <div class="flex gap-3">
                                                    <div class="flex flex-col items-center">
                                                        <div class="w-2.5 h-2.5 rounded-full flex-shrink-0 mt-1" style="background:${fu.status == 'Selesai' ? 'var(--success)' : 'var(--brand-gold)'};"></div>
                                                        <c:if test="${!loop.last}">
                                                            <div class="w-0.5 flex-1 min-h-6 my-0.5 ${fu.status == 'Selesai' ? 'tl-seg-done' : 'tl-seg-current'}"></div>
                                                        </c:if>
                                                    </div>
                                                    <div class="pb-4 flex-1">
                                                        <div class="text-[11px]" style="color:var(--text-muted);">
                                                            <fmt:formatDate value="${fu.followupDate}" pattern="d MMM yyyy"/>
                                                            — <span class="font-semibold ${fu.status == 'Selesai' ? 'text-green-600' : 'text-yellow-600'}">${fu.status}</span>
                                                        </div>
                                                        <div class="text-[12px] mt-0.5" style="color:var(--text-secondary);">
                                                            <c:out value="${fu.notes}"/>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="rounded-2xl p-5" style="background:var(--card-bg); border:1px solid var(--border);">
                                <div class="text-[13px] font-bold mb-3.5">⚡ Aksi Follow Up</div>
                                <div class="rounded-xl p-3.5 mb-3.5" style="background:#f0fdf4; border:1px solid #bbf7d0;">
                                    <div class="text-[11px] font-semibold mb-2" style="color:#16a34a;">💬 Kirim via WhatsApp</div>
                                    <div class="flex flex-col gap-1.5">
                                        <div class="wa-chip px-3 py-1.5 rounded-lg text-xs cursor-pointer" style="border:1px solid #bbf7d0; background:#fff; color:#15803d; font-family:'DM Sans',sans-serif;" onclick="pilihTemplate('reminder', this)">📅 Reminder Jadwal</div>
                                        <div class="wa-chip px-3 py-1.5 rounded-lg text-xs cursor-pointer" style="border:1px solid #bbf7d0; background:#fff; color:#15803d; font-family:'DM Sans',sans-serif;" onclick="pilihTemplate('info', this)">🏠 Info Properti</div>
                                        <div class="wa-chip px-3 py-1.5 rounded-lg text-xs cursor-pointer" style="border:1px solid #bbf7d0; background:#fff; color:#15803d; font-family:'DM Sans',sans-serif;" onclick="pilihTemplate('harga', this)">💰 Penawaran Harga</div>
                                    </div>
                                    <div id="waPreview" class="rounded-lg px-3 py-2.5 mt-2.5 text-xs leading-relaxed min-h-[60px]" style="background:var(--content-bg); border:1px dashed var(--border); color:var(--text-secondary);">Pilih template pesan di atas...</div>
                                    <button onclick="kirimWhatsApp()" class="w-full mt-2.5 py-2.5 rounded-lg text-[13px] font-bold flex items-center justify-center gap-2 transition-all" style="background:#16a34a; color:#fff; border:none; cursor:pointer; font-family:'DM Sans',sans-serif;" onmouseover="this.style.background='#15803d';" onmouseout="this.style.background='#16a34a';">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                                            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                                        </svg> Kirim WhatsApp
                                    </button>
                                </div>
                                <div class="flex items-center gap-2.5 my-3 text-[11px]" style="color:var(--text-muted);">
                                    <div class="flex-1 h-px" style="background:var(--border);"></div>
                                    atau catat aktivitas
                                    <div class="flex-1 h-px" style="background:var(--border);"></div>
                                </div>
                                <form method="post" action="${pageContext.request.contextPath}/followup">
                                    <input type="hidden" name="action"    value="catat">
                                    <input type="hidden" name="leadId"    value="${leadIdDipilih}">
                                    <input type="hidden" name="salesId"   value="<%= userLogin.getId() %>">
                                    <input type="hidden" name="followupDate" id="followupDateInput" value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date()) %>">
                                    <textarea id="notesArea" name="notes" rows="3" placeholder="Tulis catatan hasil follow up... (misal: Lead sudah dihubungi, respon positif, minta brosur)" class="w-full rounded-lg px-3 py-2.5 text-[13px] outline-none resize-none transition-all" style="border:1px solid var(--border); background:var(--content-bg); color:var(--text-primary); font-family:'DM Sans',sans-serif; min-height:70px;"></textarea>
                                    <div class="flex gap-2 mt-2.5">
                                        <button type="button" onclick="tandaiSelesai()" class="flex-1 py-2.5 rounded-lg text-xs font-bold flex items-center justify-center gap-1.5 transition-all" style="background:var(--brand-gold); color:#0b1622; border:none; cursor:pointer; font-family:'DM Sans',sans-serif;" onmouseover="this.style.background='#b8963d';" onmouseout="this.style.background='var(--brand-gold)';">
                                            ✅ Tandai Selesai
                                        </button>
                                        <button type="submit" class="flex-1 py-2.5 rounded-lg text-xs font-bold flex items-center justify-center gap-1.5 transition-all" style="background:var(--content-bg); color:var(--text-secondary); border:1px solid var(--border); cursor:pointer; font-family:'DM Sans',sans-serif;" onmouseover="this.style.background='#e5e7eb';" onmouseout="this.style.background='var(--content-bg)';">
                                            📝 Catat Aktivitas
                                        </button>
                                    </div>
                                </form>
                                <form id="formSelesai" method="post" action="${pageContext.request.contextPath}/followup" style="display:none;">
                                    <input type="hidden" name="action" value="selesai">
                                    <input type="hidden" name="followupId" id="followupIdSelesai" value="">
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </main>
</div>

<c:if test="${param.sukses != null}">
    <div id="toastSukses" class="fixed bottom-5 right-5 z-50 px-5 py-3 rounded-xl text-sm font-semibold shadow-lg" style="background:#16a34a; color:#fff;">
        ✅ <c:choose><c:when test="${param.sukses == 'catat'}">Aktivitas berhasil dicatat!</c:when><c:when test="${param.sukses == 'selesai'}">Follow-up ditandai selesai!</c:when><c:when test="${param.sukses == 'batalkan'}">Reminder berhasil dibatalkan.</c:when><c:otherwise>Berhasil!</c:otherwise></c:choose>
    </div>
    <script>setTimeout(() => document.getElementById('toastSukses')?.remove(), 3500);</script>
</c:if>
<c:if test="${param.gagal != null}">
    <div id="toastGagal" class="fixed bottom-5 right-5 z-50 px-5 py-3 rounded-xl text-sm font-semibold shadow-lg" style="background:#ef4444; color:#fff;">
        ❌ Gagal melakukan aksi. Silakan coba lagi.
    </div>
    <script>setTimeout(() => document.getElementById('toastGagal')?.remove(), 3500);</script>
</c:if>

<script>
    const CTX       = "${pageContext.request.contextPath}";
    const LEAD_ID   = ${leadIdDipilih != null ? leadIdDipilih : 'null'};
    const SALES_ID  = <%= userLogin.getId() %>;

    document.addEventListener('DOMContentLoaded', function () {
        const items = document.querySelectorAll('.lead-card-item');
        const countEl = document.getElementById('leadCount');
        if (countEl) countEl.textContent = items.length + ' lead';
    });

    document.getElementById('searchInput').addEventListener('input', function () {
        const q = this.value.toLowerCase();
        document.querySelectorAll('.lead-card-item').forEach(card => {
            const txt = card.textContent.toLowerCase();
            card.style.display = txt.includes(q) ? '' : 'none';
        });
        const visible = [...document.querySelectorAll('.lead-card-item')].filter(c => c.style.display !== 'none').length;
        document.getElementById('leadCount').textContent = visible + ' lead';
    });

    function pilihLead(leadId, followupId) {
        const tab = "${tabAktif}";
        window.location.href = CTX + '/reminder?tab=' + tab + '&leadId=' + leadId + '&followupId=' + followupId;
    }

    const TEMPLATES = {
        reminder: 'Halo, saya dari LeadEstate. Ingin mengingatkan bahwa hari ini kita ada jadwal diskusi. Apakah Bapak/Ibu masih bisa? 🙏',
        info:     'Halo, ada informasi terbaru mengenai properti yang mungkin menarik bagi Bapak/Ibu. Boleh saya share detailnya? 😊',
        harga:    'Halo, kami memiliki penawaran spesial untuk properti pilihan Anda. Tertarik untuk kita diskusikan? 🎉'
    };
    let templateDipilih = null;

    function pilihTemplate(key, element) {
        templateDipilih = key;
        document.getElementById('waPreview').textContent = TEMPLATES[key] || '';
    
        // Hapus warna hijau dari semua tombol
        document.querySelectorAll('.wa-chip').forEach(c => c.classList.remove('sel'));
    
        // Tambahkan warna hijau HANYA pada tombol yang diklik
        if (element) {
        element.classList.add('sel');
        }
    }

    function kirimWhatsApp() {
        if (!templateDipilih) {
            alert('Pilih template terlebih dahulu');
            return;
        }
        const phone = ''; 
        const msg   = encodeURIComponent(TEMPLATES[templateDipilih]);
        window.open('https://wa.me/' + phone.replace(/\D/g,'') + '?text=' + msg, '_blank');
    }

    function bukaWhatsApp() {
        window.open('https://wa.me/', '_blank');
    }

    function tandaiSelesai() {
        const followupId = new URLSearchParams(window.location.search).get('followupId');
        if (!followupId) {
            alert('Pilih follow-up dari daftar terlebih dahulu');
            return;
        }
        if (!confirm('Tandai follow-up ini sebagai selesai?')) return;
        document.getElementById('followupIdSelesai').value = followupId;
        document.getElementById('formSelesai').submit();
    }

    document.querySelector('form')?.addEventListener('submit', function () {
        const now = new Date();
        const pad = n => String(n).padStart(2, '0');
        const dt  = now.getFullYear() + '-' + pad(now.getMonth()+1) + '-' + pad(now.getDate())
                  + 'T' + pad(now.getHours()) + ':' + pad(now.getMinutes());
        const el  = document.getElementById('followupDateInput');
        if (el) el.value = dt;
    });
</script>
</body>
</html>