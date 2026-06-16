<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.leadestate.model.User, com.leadestate.model.Admin, com.leadestate.dao.UserDAO, java.util.List, java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pengaturan — LeadEstate</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <style>
        /* ── VARIABEL WARNA ── */
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

        .scroll-thin::-webkit-scrollbar       { width: 4px; }
        .scroll-thin::-webkit-scrollbar-track { background: transparent; }
        .scroll-thin::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 10px; }

        .finp:focus {
            outline: none;
            border-color: var(--brand-gold) !important;
            background: #fff !important;
            box-shadow: 0 0 0 3px rgba(201,168,76,.12);
        }

        .save-bar {
            position: fixed; bottom: 0; left: 256px; right: 0;
            background: #fff; border-top: 2px solid var(--brand-gold);
            padding: 14px 28px; display: flex; align-items: center; gap: 12px;
            transform: translateY(100%); transition: transform .25s;
            z-index: 100; box-shadow: 0 -4px 20px rgba(0,0,0,.08);
        }
        .save-bar.visible { transform: translateY(0); }

        .toast {
            position: fixed; bottom: 24px; right: 24px;
            border-radius: 12px; padding: 13px 18px;
            display: flex; align-items: center; gap: 9px;
            font-size: 13.5px; font-weight: 600;
            opacity: 0; transform: translateY(12px);
            transition: opacity .25s, transform .25s;
            pointer-events: none; z-index: 200;
            box-shadow: 0 4px 20px rgba(0,0,0,.12);
        }
        .toast.show        { opacity: 1; transform: translateY(0); }
        .toast.ok          { background: #dcfce7; color: #16a34a; border: 1px solid #bbf7d0; }
        .toast.err         { background: #fee2e2; color: #dc2626; border: 1px solid #fecaca; }

        .modal-overlay {
            position: fixed; inset: 0; background: rgba(0,0,0,.55);
            display: flex; align-items: center; justify-content: center;
            z-index: 300; opacity: 0; pointer-events: none; transition: opacity .2s;
        }
        .modal-overlay.open { opacity: 1; pointer-events: all; }
        .modal-box {
            background: #fff; border-radius: 16px; padding: 28px;
            width: 400px; max-width: 90vw;
            box-shadow: 0 20px 60px rgba(0,0,0,.15);
        }

        .sn-item {
            display: flex; align-items: center; gap: 9px;
            padding: 10px 12px; border-radius: 10px;
            font-size: 13.5px; color: var(--text-secondary);
            cursor: pointer; transition: background .15s, color .15s;
            margin-bottom: 2px; border: 1px solid transparent;
        }
        .sn-item:hover           { background: #f9fafb; color: var(--text-primary); }
        .sn-item.active          { background: #fef3cd; color: #92400e; font-weight: 600; border-color: #fde68a; }
        .sn-item.sn-danger       { color: var(--danger); }
        .sn-item.sn-danger:hover { background: #fee2e2; }
        .sn-item.sn-danger.active{ background: #fee2e2; font-weight: 600; border-color: #fecaca; }

        .set-card {
            background: var(--card-bg); border: 1px solid var(--border);
            border-radius: 16px; overflow: hidden; margin-bottom: 20px;
        }
        .set-card-head {
            display: flex; align-items: center; justify-content: space-between;
            padding: 18px 22px; border-bottom: 1px solid var(--border);
            flex-wrap: wrap; gap: 12px;
        }
        .set-card-body { padding: 22px; }
        .danger-card   { border-color: #fecaca; }
        .danger-head   { border-bottom-color: #fee2e2; }

        .flbl {
            display: block; font-size: 11.5px; font-weight: 600;
            color: var(--text-secondary); letter-spacing: .03em;
            text-transform: uppercase; margin-bottom: 6px;
        }
        .finp {
            width: 100%; padding: 10px 13px;
            background: var(--content-bg); border: 1.5px solid var(--border);
            border-radius: 10px; color: var(--text-primary); font-size: 13.5px;
            font-family: 'DM Sans', sans-serif; transition: border-color .2s, background .2s;
        }
        .finp:disabled { opacity: .5; cursor: not-allowed; }
        .finp::placeholder { color: var(--text-muted); }

        .tim-row {
            display: flex; align-items: center; gap: 13px;
            padding: 14px 22px; border-bottom: 1px solid #f3f4f6;
        }
        .tim-row:last-child { border-bottom: none; }
        .team-av {
            width: 40px; height: 40px; border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            font-size: 13px; font-weight: 700; color: #fff; flex-shrink: 0;
            position: relative;
        }
        .status-dot {
            position: absolute; bottom: 0; right: 0;
            width: 11px; height: 11px; border-radius: 50%;
            background: #9ca3af; border: 2px solid #fff;
        }
        .role-badge {
            display: inline-block; font-size: 10.5px; font-weight: 700;
            border-radius: 5px; padding: 2px 8px;
        }
        .role-badge.admin { background: #ede9fe; color: #7c3aed; }
        .role-badge.sales { background: #d1fae5; color: #059669; }
        .me-badge {
            font-size: 10px; font-weight: 700; color: #92400e;
            background: #fef3cd; border: 1px solid #fde68a;
            border-radius: 4px; padding: 1px 6px;
        }

        .danger-item {
            display: flex; align-items: flex-start; justify-content: space-between;
            gap: 16px; padding: 18px 22px;
            border-bottom: 1px solid #fee2e2;
        }
        .danger-item:last-child { border-bottom: none; }

        .btn-outline {
            padding: 8px 16px; border-radius: 9px; font-size: 12.5px; font-weight: 600;
            border: 1.5px solid var(--border); background: transparent; color: var(--text-secondary);
            cursor: pointer; font-family: 'DM Sans', sans-serif; transition: all .15s;
        }
        .btn-outline:hover          { border-color: var(--text-secondary); color: var(--text-primary); }
        .btn-outline.danger         { border-color: #fecaca; color: var(--danger); }
        .btn-outline.danger:hover   { background: #fee2e2; border-color: var(--danger); }
        .btn-outline.danger.solid   { background: var(--danger); color: #fff; border-color: var(--danger); }
        .btn-outline.danger.solid:hover { background: #dc2626; }
        .btn-gold {
            padding: 9px 20px; border-radius: 9px; font-size: 13px; font-weight: 700;
            background: var(--brand-gold); border: none; color: #0b1622;
            cursor: pointer; font-family: 'DM Sans', sans-serif; transition: background .15s;
        }
        .btn-gold:hover { background: #b8963d; }
    </style>
</head>

<%
    /* ── Proteksi session ── */
    User userLogin = (User) session.getAttribute("user");
    if (userLogin == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    /* ── Data user ── */
    String namaUser  = userLogin.getName()  != null ? userLogin.getName()  : "";
    String emailUser = userLogin.getEmail() != null ? userLogin.getEmail() : "";
    int    roleId    = userLogin.getRoleId();
    boolean isAdmin  = (roleId == 1);

    String[] namaParts    = namaUser.split(" ", 2);
    String   namaDepan    = namaParts.length > 0 ? namaParts[0] : "";
    String   namaBelakang = namaParts.length > 1 ? namaParts[1] : "";

    StringBuilder inisialSb = new StringBuilder();
    for (String part : namaUser.split(" ")) {
        if (!part.isEmpty()) inisialSb.append(part.charAt(0));
        if (inisialSb.length() >= 2) break;
    }
    String inisial = inisialSb.toString().toUpperCase();
    if (inisial.isEmpty()) inisial = "U";

    String namaRole = userLogin.getRoleName();

    /* ── Tab aktif ── */
    String activeTabReq = (String) request.getAttribute("activeTab");
    String activeTab    = (activeTabReq != null) ? activeTabReq : "profil";

    /* ── Flash messages ── */
    String successMsg = (String) request.getAttribute("successMessage");
    String errorMsg   = (String) request.getAttribute("errorMessage");
    if (successMsg == null && session.getAttribute("flashPesan") != null) {
        successMsg = (String) session.getAttribute("flashPesan");
        session.removeAttribute("flashPesan");
    }

    /* ── Data tim (Admin only) ── */
    List<User> daftarTim = new ArrayList<>();
    if (isAdmin && "tim".equals(activeTab)) {
        UserDAO userDAO = new UserDAO();
        daftarTim = userDAO.findAll();
    }

    /* ── Tanggal ── */
    java.util.Locale locale = new java.util.Locale("id", "ID");
    java.text.SimpleDateFormat sdfHari = new java.text.SimpleDateFormat("EEEE, d MMMM yyyy", locale);
    String tanggalHariIni = sdfHari.format(new java.util.Date());
%>

<body class="bg-[#f5f4f0] text-[#1a1a2e] overflow-hidden">
<div class="flex h-screen overflow-hidden">

    <!-- SIDEBAR — sama persis dengan reminder.jsp -->
    <aside class="sidebar-grid w-64 flex-shrink-0 flex flex-col relative overflow-hidden"
           style="background:var(--sidebar-bg); border-right:1px solid var(--sidebar-border);">

        <!-- Brand -->
        <div class="flex items-center gap-2.5 px-4 py-5"
             style="border-bottom:1px solid var(--sidebar-border);">
            <div class="w-8 h-8 rounded-lg overflow-hidden flex-shrink-0">
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
                 style="color:#3a5068;">Menu Utama</div>

            <!-- Dashboard -->
            <a href="${pageContext.request.contextPath}/dashboard"
               class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
               style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
               onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
               onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <rect x="3" y="3" width="7" height="7" rx="1.5"/>
                    <rect x="14" y="3" width="7" height="7" rx="1.5"/>
                    <rect x="3" y="14" width="7" height="7" rx="1.5"/>
                    <rect x="14" y="14" width="7" height="7" rx="1.5"/>
                </svg>
                Dashboard
            </a>

            <!-- Reminder & Follow-Up -->
            <a href="${pageContext.request.contextPath}/reminder"
               class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
               style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
               onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
               onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                    <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
                </svg>
                Reminder &amp; Follow-Up
            </a>

            <!-- Data Lead -->
            <a href="${pageContext.request.contextPath}/lead"
               class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
               style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
               onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
               onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                </svg>
                Data Lead
            </a>

            <% if (isAdmin) { %>
            <!-- Manajemen Sales -->
            <a href="${pageContext.request.contextPath}/manajemen-sales"
               class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
               style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
               onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
               onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <rect x="2" y="3" width="20" height="18" rx="2"/>
                    <circle cx="12" cy="10" r="3"/>
                    <path d="M7 21v-1a5 5 0 0 1 10 0v1"/>
                </svg>
                Manajemen Sales
            </a>

            <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1"
                 style="color:#3a5068;">Laporan</div>

            <a href="${pageContext.request.contextPath}/laporan"
               class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
               style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
               onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
               onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <line x1="18" y1="20" x2="18" y2="10"/>
                    <line x1="12" y1="20" x2="12" y2="4"/>
                    <line x1="6"  y1="20" x2="6"  y2="14"/>
                </svg>
                Laporan &amp; Statistik
            </a>
            <% } %>

            <!-- Pengaturan (AKTIF) -->
            <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-bold cursor-default"
                 style="background:var(--brand-gold); border:1px solid var(--brand-gold); color:#0b1622;">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="#0b1622" stroke-width="1.8">
                    <circle cx="12" cy="12" r="3"/>
                    <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
                </svg>
                Pengaturan
            </div>
        </nav>

        <!-- Sidebar Footer -->
        <div class="flex items-center gap-2.5 px-4 py-3.5 flex-shrink-0"
             style="border-top:1px solid var(--sidebar-border);">
            <div class="w-9 h-9 rounded-full flex items-center justify-center text-sm font-bold flex-shrink-0"
                 id="sfAvatar"
                 style="background:linear-gradient(135deg,var(--brand-gold),#e8c97a); color:var(--sidebar-bg); border:2px solid rgba(201,168,76,.4);">
                <%= inisial %>
            </div>
            <div>
                <div class="text-sm font-semibold text-white"><%= namaUser %></div>
                <div class="text-[11px] mt-px" style="color:#5a7a94;"><%= namaRole %></div>
            </div>
        </div>
    </aside>

    <!-- MAIN CONTENT -->
    <main class="flex-1 flex flex-col overflow-hidden">

        <!-- TOPBAR — sama dengan reminder.jsp -->
        <div class="flex items-center justify-between flex-shrink-0 px-7"
             style="height:60px; background:#fff; border-bottom:1px solid var(--border); z-index:50;">
            <div class="text-xl font-semibold" style="font-family:'Playfair Display',serif; color:var(--text-primary);">
                Pengaturan
            </div>
            <div class="flex items-center gap-3">
                <div class="flex items-center gap-1.5 rounded-full px-3.5 py-1.5 text-xs font-medium"
                     style="background:var(--content-bg); border:1px solid var(--border); color:var(--text-secondary);">
                    📅 <%= tanggalHariIni %>
                </div>
            </div>
        </div>

        <!-- BODY (settings nav + content) -->
        <div class="flex flex-1 overflow-hidden">

            <!-- ─── Settings Nav Kiri ─── -->
            <div class="w-52 flex-shrink-0 overflow-y-auto scroll-thin p-3"
                 style="background:#fff; border-right:1px solid var(--border);">

                <div class="text-[10px] font-semibold uppercase tracking-widest px-2 py-2 mt-1"
                     style="color:var(--text-muted);">Akun</div>
                <div class="sn-item <%= "profil".equals(activeTab) ? "active" : "" %>"
                     onclick="switchTab('profil')">
                    <span>👤</span> Profil Saya
                </div>

               
                <div class="text-[10px] font-semibold uppercase tracking-widest px-2 py-2 mt-2"
                     style="color:var(--text-muted);">Lainnya</div>
                <div class="sn-item sn-danger <%= "bahaya".equals(activeTab) ? "active" : "" %>"
                     onclick="switchTab('bahaya')">
                    <span>⚠️</span> Zona Bahaya
                </div>
            </div>

            <!-- ─── Settings Content Kanan ─── -->
            <div class="flex-1 overflow-y-auto scroll-thin p-6" style="background:var(--content-bg);">

                <%-- Flash messages --%>
                <% if (successMsg != null) { %>
                <div class="flex items-center gap-2.5 px-4 py-3 rounded-xl mb-5 text-sm"
                     style="background:#dcfce7; border:1px solid #bbf7d0; color:#16a34a;">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                    </svg>
                    <%= successMsg %>
                </div>
                <% } %>
                <% if (errorMsg != null) { %>
                <div class="flex items-center gap-2.5 px-4 py-3 rounded-xl mb-5 text-sm"
                     style="background:#fee2e2; border:1px solid #fecaca; color:#dc2626;">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                    </svg>
                    <%= errorMsg %>
                </div>
                <% } %>

                <!-- TAB: PROFIL SAYA -->
                <div id="tab-profil" class="tab-panel" style="display:<%= "profil".equals(activeTab) ? "block" : "none" %>">
                    <div class="set-card">

                        <!-- Avatar header -->
                        <div class="flex items-center gap-4 p-5" style="border-bottom:1px solid var(--border);">
                            <div class="w-16 h-16 rounded-full flex items-center justify-center text-2xl font-bold text-white flex-shrink-0"
                                 id="avatarBig"
                                 style="background:linear-gradient(135deg,var(--brand-gold),#e8c97a); color:#0b1622;">
                                <%= inisial %>
                            </div>
                            <div>
                                <div class="text-lg font-bold" id="avDisplayName"><%= namaUser %></div>
                                <div class="text-sm mt-0.5" style="color:var(--text-muted);">
                                    <%= namaRole %> &middot; <%= emailUser %>
                                </div>
                            </div>
                        </div>

                        <!-- Form update profil -->
                        <form method="post" action="<%= request.getContextPath() %>/AuthController"
                              id="formProfil" onsubmit="return validateProfil()">
                            <input type="hidden" name="action" value="updateProfile">
                            <div class="set-card-body">
                                <div class="grid grid-cols-2 gap-4 mb-5">
                                    <div>
                                        <label class="flbl" for="inpNamaDepan">Nama Depan</label>
                                        <input id="inpNamaDepan" name="namaDepan" type="text"
                                               class="finp" value="<%= namaDepan %>"
                                               oninput="markDirty()">
                                    </div>
                                    <div>
                                        <label class="flbl" for="inpNamaBelakang">Nama Belakang</label>
                                        <input id="inpNamaBelakang" name="namaBelakang" type="text"
                                               class="finp" value="<%= namaBelakang %>"
                                               oninput="markDirty()">
                                    </div>
                                </div>
                                <div class="grid grid-cols-2 gap-4">
                                    <div>
                                        <label class="flbl">Email</label>
                                        <input type="email" class="finp" value="<%= emailUser %>" disabled>
                                    </div>
                                    <div>
                                        <label class="flbl">Jabatan</label>
                                        <input type="text" class="finp" value="<%= namaRole %>" disabled>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- TAB: MANAJEMEN TIM (Admin only) -->
                <% if (isAdmin) { %>
                <div id="tab-tim" class="tab-panel" style="display:<%= "tim".equals(activeTab) ? "block" : "none" %>">
                    <div class="set-card">
                        <div class="set-card-head">
                            <div class="flex items-center gap-3">
                                <div class="w-10 h-10 rounded-xl flex items-center justify-center text-xl"
                                     style="background:#ede9fe;">👥</div>
                                <div>
                                    <div class="font-bold text-[15px]">Anggota Tim</div>
                                    <div class="text-[12.5px]" id="jumlahAnggota" style="color:var(--text-muted);">
                                        <%= daftarTim.size() %> anggota
                                    </div>
                                </div>
                            </div>
                            <div class="flex gap-2">
                                <input type="text" id="searchTim" class="finp" style="width:180px;"
                                       placeholder="Cari nama / email..." oninput="filterTim()">
                                <select id="filterRole" class="finp" style="width:110px;" onchange="filterTim()">
                                    <option value="all">Semua</option>
                                    <option value="admin">Admin</option>
                                    <option value="sales">Sales</option>
                                </select>
                            </div>
                        </div>

                        <div id="timList">
                            <%
                                String[] avatarColors = {"#6366f1","#10b981","#ef4444","#8b5cf6","#f59e0b"};
                                int colorIdx = 0;
                                int loggedInId = userLogin.getId();
                                for (User member : daftarTim) {
                                    boolean isMe = (member.getId() == loggedInId);
                                    boolean memberAdmin = (member.getRoleId() == 1);
                                    String memberRole = memberAdmin ? "Admin" : "Sales";
                                    String roleClass  = memberAdmin ? "admin" : "sales";
                                    String color = avatarColors[colorIdx % avatarColors.length];
                                    colorIdx++;

                                    StringBuilder mInisialSb = new StringBuilder();
                                    for (String p : member.getName().split(" ")) {
                                        if (!p.isEmpty()) mInisialSb.append(p.charAt(0));
                                        if (mInisialSb.length() >= 2) break;
                                    }
                                    String mInisial = mInisialSb.toString().toUpperCase();
                            %>
                            <div class="tim-row"
                                 data-name="<%= member.getName().toLowerCase() %>"
                                 data-email="<%= member.getEmail().toLowerCase() %>"
                                 data-role="<%= memberRole.toLowerCase() %>">

                                <div class="team-av" style="background:<%= color %>;">
                                    <%= mInisial %>
                                    <div class="status-dot"></div>
                                </div>

                                <div class="flex-1 min-w-0">
                                    <div class="flex items-center gap-1.5 text-[13.5px] font-semibold">
                                        <%= member.getName() %>
                                        <% if (isMe) { %>
                                        <span class="me-badge">Saya</span>
                                        <% } %>
                                    </div>
                                    <div class="text-[12px] mt-0.5" style="color:var(--text-muted);">
                                        <span class="role-badge <%= roleClass %>"><%= memberRole %></span>
                                        &middot; <%= member.getEmail() %>
                                    </div>
                                </div>

                                <% if (isMe) { %>
                                <span class="text-xs font-semibold px-2.5 py-1 rounded-full"
                                      style="background:#dcfce7; color:#16a34a;">Owner</span>
                                <% } else { %>
                                <form method="post" action="<%= request.getContextPath() %>/manajemen-sales"
                                      style="display:inline;" onchange="this.submit()">
                                    <input type="hidden" name="aksi"  value="simpan">
                                    <input type="hidden" name="id"    value="<%= member.getId() %>">
                                    <input type="hidden" name="name"  value="<%= member.getName() %>">
                                    <input type="hidden" name="email" value="<%= member.getEmail() %>">
                                    <select name="roleId" class="finp" style="width:110px;">
                                        <option value="1" <%= memberAdmin  ? "selected" : "" %>>Admin</option>
                                        <option value="2" <%= !memberAdmin ? "selected" : "" %>>Sales</option>
                                    </select>
                                </form>
                                <button type="button" class="btn-outline danger"
                                        onclick="konfirmasiHapus(<%= member.getId() %>, '<%= member.getName().replace("'", "\\'") %>')">
                                    Hapus
                                </button>
                                <% } %>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                <% } %>

                <!-- TAB: ZONA BAHAYA -->
                <div id="tab-bahaya" class="tab-panel" style="display:<%= "bahaya".equals(activeTab) ? "block" : "none" %>">
                    <div class="set-card danger-card">
                        <div class="set-card-head danger-head">
                            <div class="flex items-center gap-3">
                                <div class="w-10 h-10 rounded-xl flex items-center justify-center text-xl"
                                     style="background:#fee2e2;">⚠️</div>
                                <div>
                                    <div class="font-bold text-[15px]" style="color:var(--danger);">Zona Bahaya</div>
                                    <div class="text-[12.5px]" style="color:var(--text-muted);">
                                        Tindakan berikut bersifat permanen dan tidak bisa dibatalkan
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Logout -->
                        <div class="danger-item">
                            <div>
                                <div class="font-semibold text-[13.5px] mb-1">Logout dari Akun</div>
                                <div class="text-[12.5px]" style="color:var(--text-muted);">
                                    Keluar dari sesi saat ini dan kembali ke halaman login.
                                </div>
                            </div>
                            <form method="post" action="<%= request.getContextPath() %>/AuthController">
                                <input type="hidden" name="action" value="logout">
                                <button type="submit" class="btn-outline danger">🚪 Logout</button>
                            </form>
                        </div>

                       
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </main>
</div>

<!-- ── SAVE BAR ── -->
<div class="save-bar" id="saveBar">
    <span class="flex-1 text-sm" style="color:var(--text-secondary);">Ada perubahan yang belum disimpan</span>
    <button type="button" class="btn-outline" onclick="discardChanges()">Batal</button>
    <button type="button" class="btn-gold"    onclick="saveChanges()">Simpan Perubahan</button>
</div>

<!-- ── TOAST ── -->
<div class="toast" id="toast"></div>

<!-- ── MODAL HAPUS ── -->
<div class="modal-overlay" id="modalHapus">
    <div class="modal-box">
        <div class="text-lg font-bold mb-2">Hapus Anggota Tim?</div>
        <div class="text-[13.5px] mb-5" id="modalHapusDesc" style="color:var(--text-secondary); line-height:1.6;">
            Aksi ini tidak bisa dibatalkan.
        </div>
        <form method="post" action="<%= request.getContextPath() %>/manajemen-sales" id="formHapus">
            <input type="hidden" name="aksi" value="hapus">
            <input type="hidden" name="id"   id="hapusId"  value="">
            <div class="flex gap-2 justify-end">
                <button type="button" class="btn-outline" onclick="tutupModal()">Batal</button>
                <button type="submit" class="btn-outline danger solid">Ya, Hapus</button>
            </div>
        </form>
    </div>
</div>

<script>
/* ── TAB SWITCHING ── */
function switchTab(tabId) {
    document.querySelectorAll('.tab-panel').forEach(p => p.style.display = 'none');
    document.querySelectorAll('.sn-item').forEach(el => el.classList.remove('active'));
    var target = document.getElementById('tab-' + tabId);
    if (target) target.style.display = 'block';
    document.querySelectorAll('.sn-item').forEach(el => {
        if (el.getAttribute('onclick') === "switchTab('" + tabId + "')") el.classList.add('active');
    });
    if (tabId !== 'profil') hideSaveBar();
}

/* ── SAVE BAR ── */
var isDirty = false;
var origNamaDepan    = document.getElementById('inpNamaDepan')    ? document.getElementById('inpNamaDepan').value    : '';
var origNamaBelakang = document.getElementById('inpNamaBelakang') ? document.getElementById('inpNamaBelakang').value : '';

function markDirty() {
    if (!isDirty) {
        isDirty = true;
        document.getElementById('saveBar').classList.add('visible');
    }
    updateAvatarPreview();
}
function hideSaveBar() {
    isDirty = false;
    document.getElementById('saveBar').classList.remove('visible');
}
function discardChanges() {
    var dep = document.getElementById('inpNamaDepan');
    var bel = document.getElementById('inpNamaBelakang');
    if (dep) dep.value = origNamaDepan;
    if (bel) bel.value = origNamaBelakang;
    updateAvatarPreview();
    hideSaveBar();
}
function saveChanges() {
    if (!validateProfil()) return;
    document.getElementById('formProfil').submit();
}
function validateProfil() {
    var dep = document.getElementById('inpNamaDepan');
    if (!dep || dep.value.trim() === '') {
        showToast('Nama depan tidak boleh kosong.', true);
        return false;
    }
    return true;
}
function updateAvatarPreview() {
    var dep = document.getElementById('inpNamaDepan');
    var bel = document.getElementById('inpNamaBelakang');
    if (!dep) return;
    var fullName = (dep.value + ' ' + (bel ? bel.value : '')).trim();
    var parts    = fullName.split(' ').filter(Boolean);
    var ini      = '';
    for (var i = 0; i < Math.min(2, parts.length); i++) ini += parts[i][0].toUpperCase();
    if (!ini) ini = 'U';
    ['avatarBig','sfAvatar'].forEach(id => {
        var el = document.getElementById(id);
        if (el) el.textContent = ini;
    });
    var dn = document.getElementById('avDisplayName');
    if (dn) dn.textContent = fullName || 'User';
}

/* ── FILTER TIM ── */
function filterTim() {
    var search = (document.getElementById('searchTim') || {}).value || '';
    var role   = (document.getElementById('filterRole') || {}).value || 'all';
    search = search.toLowerCase();
    var rows = document.querySelectorAll('.tim-row'), count = 0;
    rows.forEach(row => {
        var matchSearch = (row.dataset.name || '').includes(search) || (row.dataset.email || '').includes(search);
        var matchRole   = role === 'all' || row.dataset.role === role;
        row.style.display = (matchSearch && matchRole) ? '' : 'none';
        if (matchSearch && matchRole) count++;
    });
    var jml = document.getElementById('jumlahAnggota');
    if (jml) jml.textContent = count + ' anggota';
}

/* ── MODAL ── */
function konfirmasiHapus(id, nama) {
    document.getElementById('hapusId').value = id;
    document.getElementById('modalHapusDesc').textContent = 'Hapus "' + nama + '" dari sistem? Aksi ini tidak bisa dibatalkan.';
    document.getElementById('modalHapus').classList.add('open');
}
function tutupModal() { document.getElementById('modalHapus').classList.remove('open'); }
document.getElementById('modalHapus').addEventListener('click', e => { if (e.target === e.currentTarget) tutupModal(); });

/* ── TOAST ── */
var toastTimer = null;
function showToast(msg, isErr) {
    var el = document.getElementById('toast');
    el.textContent = msg;
    el.className = 'toast show ' + (isErr ? 'err' : 'ok');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => el.className = 'toast', 2800);
}

/* ── AUTO TOAST dari server ── */
<% if (successMsg != null) { %>
window.addEventListener('DOMContentLoaded', () => showToast('<%= successMsg.replace("'", "\\'") %>', false));
<% } %>
<% if (errorMsg != null) { %>
window.addEventListener('DOMContentLoaded', () => showToast('<%= errorMsg.replace("'", "\\'") %>', true));
<% } %>
</script>
</body>
</html>
