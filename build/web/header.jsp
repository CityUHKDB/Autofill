<%@page import="Autofill.User"%>
<div id='header'>
    <div class='container'>
        <div id='logo'>
            <h1>FYP Web Site</h1>
        </div>

        <div id='loginBox'>
            <%
                if (session.getAttribute("user") == null) {
            %>
            <form action='?page=login' method='POST'>
                <fieldset>
                    <legend>Login</legend>
                    <input type='text' name='username' placeholder='Username' />
                    <input type='password' name='password' placeholder='Password' />
                    <input type='submit' value='Login' />
                    <button>Sign Up</button>
                </fieldset>
            </form>
            <%
                } else {
            %>
            <fieldset>
                <legend>${user.username}</legend>
                <span>${user.role}</span>
                <span><a href='?page=logout'>Sign Out</a></span>
            </fieldset>
            <%
                }
            %>
        </div>

    </div>
    <div id='navigator'>
        <div class='container'>
            <a href='?page=home'>Home</a>
            <%
                User user = (User)session.getAttribute("user");
                if (user != null) {
                    if (user.getRole().equals("member")) {
            %>
            <a href='?page=record'>Records</a>
            <a href='?page=fill'>Autofill</a>
            <%
                    }
                    if (user.getRole().equals("admin")) {
            %>
            <a href='?page=manage'>Manage</a>
            <a href='?page=statistics'>Statistics</a>
            <%
                    }
                }
            %>
        </div>
    </div>
</div>