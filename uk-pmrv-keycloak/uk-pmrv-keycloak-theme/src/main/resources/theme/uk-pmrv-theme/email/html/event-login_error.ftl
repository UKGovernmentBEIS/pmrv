<#list event.details as item>
	<#if item.key == "redirect_uri">
		<#assign url>${item.value}</#assign>
	</#if>
</#list>
<html>
<body>
<p>${kcSanitize(msg("eventLoginErrorBodyText", event.date?string["dd/MM/yyyy 'at' HH:mm"]))?no_esc} <a href="${url}">${url}</a>.</p>
<p>${msg("eventLoginErrorBodyContact")}</p>
</body>
</html>