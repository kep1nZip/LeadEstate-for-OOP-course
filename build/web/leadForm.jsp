<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.leadestate.model.Lead"%>
<%@page import="com.leadestate.model.Property"%>
<%@page import="com.leadestate.model.LeadStatus"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>[DEBUG] Form Lead</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 30px; }
            label { display: block; margin-top: 10px; font-weight: bold; }
            input, select { padding: 4px; width: 250px; }
            .error { color: red; font-weight: bold; }
        </style>
    </head>
    <body>
        <%
            Lead lead = (Lead) request.getAttribute("lead");
            List<Property> daftarProperty = (List<Property>) request.getAttribute("daftarProperty");
            List<LeadStatus> daftarStatus = (List<LeadStatus>) request.getAttribute("daftarStatus");
            boolean isEdit = (lead != null);
        %>

        <h1>[DEBUG] <%= isEdit ? "Edit Lead #" + lead.getId() : "Tambah Lead Baru" %></h1>
        <p><em>Halaman ini sementara, hanya untuk testing LeadController.</em></p>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>

        <form method="post" action="lead">
            <input type="hidden" name="action" value="<%= isEdit ? "edit" : "add" %>">
            <% if (isEdit) { %>
                <input type="hidden" name="id" value="<%= lead.getId() %>">
            <% } %>

            <label>Nama</label>
            <input type="text" name="name" value="<%= isEdit ? lead.getName() : "" %>" required>

            <label>Email</label>
            <input type="email" name="email" value="<%= isEdit ? lead.getEmail() : "" %>" required>

            <label>Telepon</label>
            <input type="text" name="phone" value="<%= isEdit ? lead.getPhone() : "" %>" required>

            <label>Property</label>
            <select name="propertyId">
                <% if (daftarProperty != null) { for (Property p : daftarProperty) { %>
                    <option value="<%= p.getId() %>"
                        <%= (isEdit && lead.getPropertyId() == p.getId()) ? "selected" : "" %>>
                        <%= p.getName() %> (<%= p.getLocation() %>)
                    </option>
                <% } } %>
            </select>

            <label>Sales Id</label>
            <input type="number" name="salesId" value="<%= isEdit ? lead.getSalesId() : "" %>" required>

            <% if (!isEdit) { %>
            <label>Status Awal</label>
            <select name="statusId">
                <% if (daftarStatus != null) { for (LeadStatus s : daftarStatus) { %>
                    <option value="<%= s.getId() %>"><%= s.getStatusName() %></option>
                <% } } %>
            </select>
            <% } %>

            <label>Source</label>
            <input type="text" name="source" value="<%= isEdit ? lead.getSource() : "" %>">

            <p><button type="submit"><%= isEdit ? "Simpan Perubahan" : "Tambah Lead" %></button></p>
        </form>

        <p><a href="lead">&laquo; Kembali ke Daftar Lead</a></p>
    </body>
</html>
