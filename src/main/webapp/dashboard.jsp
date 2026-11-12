
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.echovault.model.Capsule,com.echovault.util.Html" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Vault · EchoVault</title>

    <link rel="stylesheet" href="assets/css/style.css">
    <script defer src="assets/js/app.js"></script>
</head>

<body>

    <%@ include file="partials/nav.jspf" %>

    <main class="wrap">

        <%@ include file="partials/flash.jspf" %>

        <div class="dash-head">

            <div>
                <span class="eyebrow">✧ YOUR PRIVATE UNIVERSE</span>

                <h1>
                    Welcome back,
                    <%= Html.escape(session.getAttribute("userName")) %>.
                </h1>

                <p class="muted">
                    Every memory you seal becomes a gift to your future.
                </p>
            </div>

            <a class="btn" href="create-capsule.jsp">
                ＋ Create capsule
            </a>

        </div>

        <div class="stats">

            <div class="stat">
                <span>CAPSULES YOU CREATED</span>
                <strong><%= request.getAttribute("capsuleCount") %></strong>
                <span>Your personal collection</span>
            </div>

            <div class="stat">
                <span>MEMORIES IN YOUR VAULT</span>
                <strong>✧</strong>
                <span>Letters, moments and stories</span>
            </div>

            <div class="stat">
                <span>YOUR NEXT CHAPTER</span>
                <strong>∞</strong>
                <span>Still waiting to unfold</span>
            </div>

        </div>

        <h2 style="font-weight:600; font-size:25px; margin-top:40px;">
            Your time capsules
        </h2>

        <p class="muted">
            A collection of moments travelling toward tomorrow.
        </p>

        <div class="capsule-grid">

            <%
                List<Capsule> list =
                    (List<Capsule>) request.getAttribute("capsules");

                if (list != null && !list.isEmpty()) {

                    for (Capsule c : list) {

                        boolean open = c.isUnlocked();
            %>

                <article class="glass capsule-card">

                    <div class="capsule-top">

                        <span class="tag <%= open ? "open" : "" %>">
                            <%= open ? "READY TO OPEN" : "TIME LOCKED" %>
                        </span>

                        <span>◈</span>

                    </div>

                    <h3><%= Html.escape(c.title) %></h3>

                    <p>
                        <%= Html.escape(
                            c.description == null
                                ? "A moment saved for another day."
                                : c.description
                        ) %>
                    </p>

                    <div class="countdown"
                        <%= open ? "" :
                            "data-countdown=\"" +
                            c.unlockAt.toLocalDateTime()
                                .toString().replace(' ', 'T') +
                            "\""
                        %>>
                        <%= open ? "READY TO OPEN" : "Counting down…" %>
                    </div>

                    <small class="muted">
                        Unlock date · <%= c.unlockAt %>
                    </small>

                    <div style="margin-top:19px;">

                        <a class="btn small ghost"
                           href="capsule?id=<%= c.id %>">

                            <%= open ? "Open memory →" : "View capsule →" %>

                        </a>

                    </div>

                </article>

            <%
                    }

                } else {
            %>

                <div class="empty" style="grid-column:1/-1;">

                    <div style="font-size:42px;">✧</div>

                    <h3>Your universe is waiting.</h3>

                    <p>
                        Create your first capsule and leave something
                        beautiful for the future.
                    </p>

                    <a class="btn" href="create-capsule.jsp">
                        Seal your first memory ↗
                    </a>

                </div>

            <%
                }
            %>

        </div>

    </main>

</body>
</html>
