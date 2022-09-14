package uk.gov.pmrv.api.notification.template.utils;

import lombok.experimental.UtilityClass;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@UtilityClass
public final class MarkdownUtils {

	/**
	 * Parse the provided markdown template and return it in html format
	 * @param markdownTemplate
	 * @return the markdown template to HTML 
	 */
	public static String parseToHtml(String markdownTemplate) {
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder()
			.sanitizeUrls(true)
			.escapeHtml(true)
			.build();
		
		Node document = parser.parse(markdownTemplate);
		return renderer.render(document);
	}
}
