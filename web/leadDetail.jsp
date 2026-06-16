<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.leadestate.model.Lead"%>
<%@page import="com.leadestate.model.Property"%>
<%@page import="com.leadestate.model.LeadStatus"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>[DEBUG] Detail Lead</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 30px; }
            table { border-collapse: collapse; }
            th, td { border: 1px solid #999; padding: 6px 12px; text-align: left; }
            .error { color: red; font-weight: bold; }
        </style>
    </head>
    <body>
        <%
            Lead lead = (Lead) request.getAttribute("lead");
            Property property = (Property) request.getAttribute("property");
            LeadStatus status = (LeadStatus) request.getAttribute("status");
        %>

        <h1>[DEBUG] Detail Lead</h1>
        <p><em>Halaman ini sementara, hanya untuk testing LeadController.</em></p>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="error"><%= request.getAttribute("errorMessage") %></p>
        <% } %>

        <% if (lead != null) { %>
        <table>
            <tr><th>ID</th><td><%= lead.getId() %></td></tr>
            <tr><th>Nama</th><td><%= lead.getName() %></td></tr>
            <tr><th>Telepon</th><td><%= lead.getPhone() %></td></tr>
            <tr><th>Email</th><td><%= lead.getEmail() %></td></tr>
            <tr><th>Sales Id</th><td><%= lead.getSalesId() %></td></tr>
            <tr><th>Source</th><td><%= lead.getSource() %></td></tr>
            <tr>
                <th>Property</th>
                <td><%= property != null ? property.getName() + " - " + property.getLocation() + " (Rp " + property.getPrice() + ")" : "(tidak ada)" %></td>
            </tr>
            <tr>
                <th>Status</th>
                <td><%= status != null ? status.getStatusName() + " (id=" + status.getId() + ")" : "(tidak ada)" %></td>
            </tr>
            <tr><th>Jumlah FollowUp</th><td><%= lead.getDaftarFollowUp().size() %> (belum diisi DAO)</td></tr>
        </table>

        <h3>Ubah Status</h3>
        <form method="post" action="lead">
            <input type="hidden" name="action" value="changeStatus">
            <input type="hidden" name="id" value="<%= lead.getId() %>">
            <input type="number" name="statusId" placeholder="ID status baru" required>
            <button type="submit">Ubah Status</button>
        </form>

        <p>
            <a href="lead?action=form&id=<%= lead.getId() %>">Edit Lead</a> |
            <a href="lead">Kembali ke Daftar Lead</a>
        </p>
        <% } %>
    </body>
</html>
