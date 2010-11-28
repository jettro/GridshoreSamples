<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%-- Can be used to present info in nice formatted structure when using the WurflDevice --%>
<p>
    <a href="?mobile=true">Mobile site</a>&nbsp;<a href="?mobile=false">Normal site</a>
</p>

<h2>Basic Device info</h2>
<table>
    <%--<tr>--%>
    <%--<td>id</td>--%>
    <%--<td><c:out value="${device.id}"/></td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
    <%--<td>UserAgent</td>--%>
    <%--<td><c:out value="${device.userAgent}"/></td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
    <%--<td>MarkUp</td>--%>
    <%--<td><c:out value="${device.markUp}"/></td>--%>
    <%--</tr>--%>
    <tr>
        <td>Is mobile</td>
        <td><c:out value="${device.mobile}"/></td>
    </tr>
    <tr>
        <td>Description</td>
        <td><c:out value="${device}"/></td>
    </tr>
</table>
<%--<h2>Capabilities</h2>--%>
<%--<table>--%>
<%--<tr>--%>
<%--<th>key</th>--%>
<%--<th>value</th>--%>
<%--</tr>--%>
<%--<c:forEach var="item" items="${device.capabilities}">--%>
<%--<tr>--%>
<%--<td><c:out value="${item.key}"/></td>--%>
<%--<td><c:out value="${item.value}"/></td>--%>
<%--</tr>--%>
<%--</c:forEach>--%>
<%--</table>--%>