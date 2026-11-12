
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.net.URLEncoder,com.echovault.model.Capsule,com.echovault.util.Html" %>

<%
    Capsule c = (Capsule) request.getAttribute("capsule");

    boolean locked = Boolean.TRUE.equals(
        request.getAttribute("locked")
    );

    if (c == null) {
        response.sendError(404);
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>
        <%= Html.escape(c.title) %> · EchoVault
    </title>

    <link rel="stylesheet" href="assets/css/style.css">
</head>

<body>

    <%@ include file="partials/nav.jspf" %>

    <main class="wrap">

        <section class="glass capsule-detail reveal">

            <span class="eyebrow">
                ◈ CAPSULE #<%= c.id %>
            </span>

            <div class="locked-art">
                <%= locked ? "🔒" : "✧" %>
            </div>

            <span class="tag <%= locked ? "" : "open" %>">
                <%= locked
                    ? "SEALED UNTIL THE FUTURE"
                    : "MEMORY UNLOCKED"
                %>
            </span>

            <h1>
                <%= Html.escape(c.title) %>
            </h1>

            <% if (!locked) { %>

                <% if (c.description != null
                        && !c.description.isBlank()) { %>

                    <p class="muted" style="line-height:1.8">
                        <%= Html.escape(c.description) %>
                    </p>

                <% } else { %>

                    <p class="muted">
                        No written note was added to this capsule.
                    </p>

                <% } %>

            <% } %>

            <div class="glass"
                 style="padding:17px; margin:22px 0">

                <small class="muted">
                    UNLOCK DATE
                </small>

                <h3 style="margin:7px 0">
                    <%= c.unlockAt %>
                </h3>

                <% if (locked) { %>

                    <p class="muted">
                        This capsule is still travelling through time.
                        Its contents are sealed until the unlock date.
                        Come back when the time arrives.
                    </p>

                <% } else { %>

                    <p style="color:#8ff0c0">
                        The time lock has ended.
                        Your memory is ready.
                    </p>

                <% } %>

            </div>

            <% if (!locked) {

                List<String[]> memories =
                    (List<String[]>) request.getAttribute("memories");

                if (memories != null && !memories.isEmpty()) {

                    for (String[] memory : memories) {
            %>

                        <a class="memory-link"
                           href="memory?capsuleId=<%= c.id %>&amp;file=<%= URLEncoder.encode(memory[1], "UTF-8") %>">

                            ↗ &nbsp;
                            <%= Html.escape(memory[0]) %>
                            · Open attachment

                        </a>

            <%
                    }

                } else {
            %>

                    <p class="muted">
                        No file attachment was added.
                        Your written memory is above.
                    </p>

            <%
                }
            }
            %>

            <a class="btn ghost"
               href="dashboard"
               style="margin-top:20px">

                ← Back to my vault

            </a>

        </section>

    </main>

</body>
</html>
