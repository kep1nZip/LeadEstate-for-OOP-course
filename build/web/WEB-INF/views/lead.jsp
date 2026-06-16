<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="id">
    <head>
        <meta charset="UTF-8">
        <title>Data Lead — LeadEstate</title>

        <script src="https://cdn.tailwindcss.com"></script>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=DM+Sans:wght@300;400;500;600&family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">

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
        </style>
    </head>

    <body class="bg-gray-50 font-sans">

<%
    com.leadestate.model.User userLogin = (com.leadestate.model.User) session.getAttribute("userLogin");
    if (userLogin == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String namaUser  = userLogin.getName();
    String roleName  = userLogin.getRoleName();
    boolean isAdmin  = "Admin".equalsIgnoreCase(roleName);
    String[] namaParts = namaUser.trim().split("\s+");
    StringBuilder inisialSB = new StringBuilder();
    for (int i = 0; i < Math.min(2, namaParts.length); i++) {
        if (namaParts[i].length() > 0) inisialSB.append(namaParts[i].charAt(0));
    }
    String inisialUser = inisialSB.toString().toUpperCase();
%>

        <div class="flex min-h-screen">

            <!-- SIDEBAR -->
            <aside class="sidebar-grid fixed w-64 h-screen flex flex-col overflow-hidden"
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
                         style="color:#3a5068; letter-spacing:1.2px;">Menu Utama</div>

                    <!-- Dashboard -->
                    <a href="${pageContext.request.contextPath}/dashboard"
                       class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
                       style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
                       onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
                       onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="flex-shrink-0">
                            <rect x="3" y="3" width="7" height="7" rx="1.5"/>
                            <rect x="14" y="3" width="7" height="7" rx="1.5"/>
                            <rect x="3" y="14" width="7" height="7" rx="1.5"/>
                            <rect x="14" y="14" width="7" height="7" rx="1.5"/>
                        </svg>
                        Dashboard
                    </a>

                    <!-- Reminder -->
                    <a href="${pageContext.request.contextPath}/reminder"
                       class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
                       style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
                       onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
                       onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="flex-shrink-0">
                            <circle cx="12" cy="12" r="10"/>
                            <polygon points="10,8 16,12 10,16" fill="currentColor"/>
                        </svg>
                        Reminder &amp; Follow-Up
                    </a>

                    <!-- Data Lead (aktif) -->
                    <div class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-bold"
                         style="background:var(--brand-gold); border:1px solid var(--brand-gold); color:#0b1622;">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#0b1622" stroke-width="1.8" class="flex-shrink-0">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                        </svg>
                        Data Lead
                    </div>

                    <% if (isAdmin) { %>
                    <!-- Manajemen Sales (admin only) -->
                    <a href="${pageContext.request.contextPath}/manajemen-sales"
                       class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
                       style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
                       onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
                       onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="flex-shrink-0">
                            <rect x="2" y="3" width="20" height="18" rx="2"/>
                            <circle cx="12" cy="10" r="3"/>
                            <path d="M7 21v-1a5 5 0 0 1 10 0v1"/>
                        </svg>
                        Manajemen Sales
                    </a>
                    <% } %>

                    <div class="text-[10px] font-semibold uppercase tracking-widest px-1 py-1.5 mt-1"
                         style="color:#3a5068; letter-spacing:1.2px;">Laporan</div>

                    <!-- Laporan -->
                    <a href="${pageContext.request.contextPath}/laporan"
                       class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
                       style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
                       onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
                       onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="flex-shrink-0">
                            <line x1="18" y1="20" x2="18" y2="10"/>
                            <line x1="12" y1="20" x2="12" y2="4"/>
                            <line x1="6"  y1="20" x2="6"  y2="14"/>
                        </svg>
                        Laporan &amp; Statistik
                    </a>

                    <!-- Pengaturan -->
                    <a href="${pageContext.request.contextPath}/settings.jsp"
                       class="flex items-center gap-2.5 px-3.5 py-2.5 rounded-[10px] text-[13.5px] font-medium no-underline transition-all"
                       style="border:1px solid rgba(255,255,255,.08); background:rgba(255,255,255,.03); color:#7a99b2;"
                       onmouseover="this.style.background='rgba(255,255,255,.07)';this.style.color='#fff';"
                       onmouseout="this.style.background='rgba(255,255,255,.03)';this.style.color='#7a99b2';">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" class="flex-shrink-0">
                            <circle cx="12" cy="12" r="3"/>
                            <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
                        </svg>
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
                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                            <polyline points="16 17 21 12 16 7"/>
                            <line x1="21" y1="12" x2="9" y2="12"/>
                        </svg>
                        Keluar
                    </a>
                </div>
            </aside>


            <!-- MAIN -->
            <main class="ml-64 flex-1">

                <!-- TOPBAR -->
                <header class="bg-white border-b px-6 py-3 flex justify-between items-center">
                    <div>
                        <h1 class="font-bold text-lg">Data Lead</h1>
                        <p class="text-xs text-gray-400">Kelola semua data calon customer</p>
                    </div>

                    <div class="text-xs bg-gray-100 px-3 py-1 rounded-lg" id="tanggalHariIni">
                    </div>
                </header>

                <!-- CONTENT -->
                <div class="p-6 space-y-6">

                    <!-- FILTER -->
                    <div class="flex gap-3 items-center">

                        <input type="text" placeholder="Cari nama, nomor, atau properti..."
                               class="flex-1 px-4 py-2 border rounded-lg text-sm">

                        <select class="px-3 py-2 border rounded-lg text-sm">
                            <option>Semua Status</option>
                        </select>

                        <select class="px-3 py-2 border rounded-lg text-sm">
                            <option>Semua Sumber</option>
                        </select>

                        <button class="px-4 py-2 bg-gray-100 rounded-lg text-sm">Export</button>

                        <button onclick="openModal()"
                                class="px-4 py-2 bg-amber-400 text-white rounded-lg text-sm font-semibold">
                            + Tambah Lead
                        </button>

                    </div>


                    <!-- KPI -->
                    <div class="grid grid-cols-4 gap-4">

                        <div class="bg-white p-4 rounded-xl border">
                            <div class="text-xl font-bold">${totalLead}</div>
                            <div class="text-sm text-gray-400">Total Lead</div>
                        </div>

                        <div class="bg-white p-4 rounded-xl border">
                            <div class="text-xl font-bold">${newLead}</div>
                            <div class="text-sm text-gray-400">New Lead</div>
                        </div>

                        <div class="bg-white p-4 rounded-xl border">
                            <div class="text-xl font-bold">${followUp}</div>
                            <div class="text-sm text-gray-400">Follow Up</div>
                        </div>

                        <div class="bg-white p-4 rounded-xl border">
                            <div class="text-xl font-bold">${closing}</div>
                            <div class="text-sm text-gray-400">Closing</div>
                        </div>

                    </div>


                    <!-- TABLE -->
                    <div class="bg-white rounded-xl border overflow-hidden">

                        <div class="px-5 py-3 border-b text-sm font-semibold">
                            Daftar Semua Lead
                        </div>

                        <table class="w-full text-sm">

                            <thead class="bg-gray-50 text-gray-400 text-xs">
                                <tr>
                                    <th class="p-3 text-center">ID</th>
                                    <th class="p-3 text-center">Nama</th>
                                    <th class="p-3 text-center">Properti</th>
                                    <th class="p-3 text-center">Status</th>
                                    <th class="p-3 text-center">Sales</th>
                                    <th class="p-3 text-center">Sumber</th>
                                    <th class="p-3 text-center">Tanggal</th>
                                    <th class="p-3 text-center">Aksi</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach items="${daftarLead}" var="l">
                                    <tr class="border-t hover:bg-gray-50">

                                        <td class="p-3 text-center">#${l.id}</td>
                                        <td class="p-3 text-center">${l.name}</td>
                                        <td class="p-3 text-center">
                                            <c:forEach items="${daftarProperty}" var="p">
                                                <c:if test="${p.id == l.propertyId}">
                                                    ${p.name}
                                                </c:if>
                                            </c:forEach>
                                        </td>

                                        <td class="p-3 text-center">
                                            <span class="px-2 py-1 text-xs rounded-full bg-blue-100 text-blue-600">
                                                <c:forEach items="${daftarStatus}" var="s">
                                                    <c:if test="${s.id == l.statusId}">
                                                        ${s.statusName}
                                                    </c:if>
                                                </c:forEach>
                                            </span>
                                        </td>

                                        <td class="p-3 text-center">
                                            <c:forEach items="${daftarSales}" var="sales">
                                                <c:if test="${sales.id == l.salesId}">
                                                    ${sales.name}
                                                </c:if>
                                            </c:forEach>
                                        </td>

                                        <td class="p-3 text-center">${l.source}</td>
                                        <td class="p-3 text-center">
                                            <fmt:formatDate value="${l.createdAt}" pattern="dd-MM-yyyy"/>
                                        </td>
                                        <td class="p-3 text-center">
                                            <div class="flex justify-center gap-2">
                                                <a href="${pageContext.request.contextPath}/reminder?leadId=${l.id}"
                                                   class="w-9 h-9 flex items-center justify-center rounded-lg bg-green-100 text-green-700"
                                                   title="Lihat Reminder">
                                                    ⏰
                                                </a>
                                                <button
                                                    onclick="editStatus(${l.id}, ${l.statusId})"
                                                    class="w-9 h-9 flex items-center justify-center rounded-lg bg-yellow-100 text-yellow-700"
                                                    title="Ubah Status">
                                                    ✎
                                                </button>
                                                <a href="${pageContext.request.contextPath}/lead?action=detail&id=${l.id}"
                                                   class="w-9 h-9 flex items-center justify-center rounded-lg bg-blue-100 text-blue-700"
                                                   title="Detail Lead">
                                                    👁
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>

                        </table>

                    </div>

                </div>
            </main>
        </div>


        <!-- MODAL -->
        <div id="modal" class="fixed inset-0 bg-black/40 hidden items-center justify-center">

            <div class="bg-white rounded-xl w-[420px] p-6">

                <h2 class="font-semibold mb-4">Tambah Lead Baru</h2>

                <form action="${pageContext.request.contextPath}/lead?action=add"
                      method="post"
                      class="space-y-3">

                    <input name="name" placeholder="Nama Lengkap"
                           class="w-full border px-3 py-2 rounded-lg text-sm">

                    <input name="phone" placeholder="Nomor HP"
                           class="w-full border px-3 py-2 rounded-lg text-sm">

                    <input name="email" placeholder="Email"
                           class="w-full border px-3 py-2 rounded-lg text-sm">

                    <select name="source" class="w-full border px-3 py-2 rounded-lg text-sm">
                        <option>Instagram</option>
                        <option>Facebook</option>
                    </select>

                    <select name="statusId"
                            class="w-full border px-3 py-2 rounded-lg text-sm">
                        <c:forEach items="${daftarStatus}" var="s">
                            <option value="${s.id}">
                                ${s.statusName}
                            </option>
                        </c:forEach>
                    </select>

                    <select name="propertyId"
                            class="w-full border px-3 py-2 rounded-lg text-sm">
                        <option value="">Pilih Properti</option>

                        <c:forEach items="${daftarProperty}" var="p">
                            <option value="${p.id}">
                                ${p.id} - ${p.name}
                            </option>
                        </c:forEach>
                    </select>

                    <select name="salesId"
                            class="w-full border px-3 py-2 rounded-lg text-sm">

                        <c:forEach items="${daftarSales}" var="sales">
                            <option value="${sales.id}">
                                ${sales.id} - ${sales.name}
                            </option>
                        </c:forEach>
                    </select>

                    <div class="flex justify-end gap-2 pt-3">
                        <button type="button" onclick="closeModal()"
                                class="px-3 py-2 text-sm bg-gray-100 rounded-lg">
                            Batal
                        </button>

                        <button class="px-3 py-2 text-sm bg-amber-400 text-white rounded-lg">
                            Simpan
                        </button>
                    </div>

                </form>

            </div>
        </div>

        <!-- MODAL EDIT STATUS -->
        <div id="statusModal"
             class="fixed inset-0 bg-black/40 hidden items-center justify-center">

            <div class="bg-white rounded-xl w-[400px] p-6">

                <h2 class="font-semibold mb-4">
                    Edit Status Lead
                </h2>

                <form action="${pageContext.request.contextPath}/lead"
                      method="post">

                    <input type="hidden"
                           name="action"
                           value="changeStatus">

                    <input type="hidden"
                           name="id"
                           id="leadIdInput">

                    <select name="statusId"
                            id="statusSelect"
                            class="w-full border px-3 py-2 rounded-lg text-sm">

                        <c:forEach items="${daftarStatus}" var="s">

                            <option value="${s.id}">
                                ${s.statusName}
                            </option>

                        </c:forEach>

                    </select>

                    <div class="flex justify-end gap-2 mt-4">

                        <button type="button"
                                onclick="closeStatusModal()"
                                class="px-3 py-2 bg-gray-100 rounded-lg">

                            Batal

                        </button>

                        <button type="submit"
                                class="px-3 py-2 bg-amber-400 text-white rounded-lg">

                            Simpan

                        </button>

                    </div>

                </form>

            </div>

        </div>

        <script>
            document.getElementById('tanggalHariIni').textContent =
                new Date().toLocaleDateString('id-ID', {weekday:'long', day:'numeric', month:'long', year:'numeric'});

            function openModal() {
                document.getElementById("modal").classList.remove("hidden")
                document.getElementById("modal").classList.add("flex")
            }

            function closeModal() {
                document.getElementById("modal").classList.add("hidden")
            }

            function editStatus(leadId, statusId) {
                document.getElementById("leadIdInput").value = leadId;
                document.getElementById("statusSelect").value = statusId;
                document.getElementById("statusModal")
                        .classList.remove("hidden");
                document.getElementById("statusModal")
                        .classList.add("flex");
            }

            function closeStatusModal() {
                document.getElementById("statusModal")
                        .classList.add("hidden");
            }
        </script>

    </body>
</html>