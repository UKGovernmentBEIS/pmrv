package uk.gov.pmrv.api.notification.template.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MarkdownUtilsTest {

	@Test
	void parseToHtml() {
		String markdownText = "Hello *PMRV* in bold";
		String htmlText = MarkdownUtils.parseToHtml(markdownText);
		String expectedHtmlText = "<p>Hello <em>PMRV</em> in bold</p>\n";
		assertThat(htmlText).isEqualTo(expectedHtmlText);
	}

	@Test
	void parseToHtml_escape_html_tags() {
		String markdownText = "<div>text</div>";
		String htmlText = MarkdownUtils.parseToHtml(markdownText);
		String expectedHtmlText = "<p>&lt;div&gt;text&lt;/div&gt;</p>\n";
		assertThat(htmlText).isEqualTo(expectedHtmlText);
	}
}
