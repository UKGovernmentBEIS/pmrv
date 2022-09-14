<#ftl output_format="plainText">
<#list event.details as item>
	<#if item.key == "redirect_uri">
		<#assign url>${item.value}</#assign>
	</#if>
</#list>
${msg("eventLoginErrorBodyText", event.date?string["dd/MM/yyyy 'at' HH:mm"])} ${url}.
${msg("eventLoginErrorBodyContact")}