<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<html>
<head>
<title>eXo IDE Login Page</title>
<body bgcolor="white">
<table width="100%" height="100%">
	<tr align="center" valign="bottom">
		<td><img alt="ide" src="/IDE/eXo-IDE-Logo.png"></td>
	</tr>
	<tr align="center" valign="top">
		<td>
		<form method="POST"
			action='<%=response.encodeURL("j_security_check")%>'>
		<table border="0" cellspacing="5">
			<tr>
				<th align="right">Username:</th>
				<td align="left"><input type="text" name="j_username"></td>
			</tr>
			<tr>
				<th align="right">Password:</th>
				<td align="left"><input type="password" name="j_password"></td>
			</tr>
			<tr>
				<td align="right"><input type="submit" value="Log In"></td>
				<td align="left"><input type="reset"></td>
			</tr>
			<tr>
				<td colspan="2"><font color="red">Invalid username and/or password, please try</font></td>
			</tr>
			<tr>
				<td colspan="2">
				<table border="0">
					<tr bgcolor="#DDDDDD">
						<td style="padding: 2" align="center">User id/password</td>
						<td style="padding: 2" align="center">User role</td>
					</tr>
					<tr>
						<td style="padding: 2" align="left">exo/exo</td>
						<td style="padding: 2" align="left">developers</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</form>
		</td>
	</tr>
</table>
</body>
</html>