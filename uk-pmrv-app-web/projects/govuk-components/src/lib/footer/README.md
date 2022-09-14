The footer components are designed to add footer content to a full screen app.
To setup a govuk footer we use three components: `<govuk-footer>` which acts as
a structural container for our content including the styling and icons from the
GOV.UK design standards and navigation, `<govuk-footer-nav-list>` which represents
the secondary navigation of the application, and `<govuk-footer-meta-info>` which
can include links to meta information about a site, like cookies and contact details in the footer.

### Specifying the footer, navigation and meta information

Both the navigation and the meta information should be placed inside of the `<govuk-footer>` tag.

The navigation should be wrapped in a `<govuk-footer-nav-list>`. The `title` property can be used
to add title to the navigation section of the footer. The `twoColumnList` property can be used to
define if the navigation should be in a two columns layout or in a single column.

The `govukLink` directive is required with input `footer` for every link inside the `<govuk-footer-nav-list>` container
in order to apply the required styling.

The meta information should be wrapped in a `<govuk-footer-meta-info>`.

The `govukLink` directive is required with input `meta` for every link inside the `<govuk-footer-meta-info>` container
in order to apply the required styling.

The following is an example of a valid footer container:

```html
<govuk-footer>
  <govuk-footer-nav-list title="Single column list" [columns]="1">
    <a govukLink="footer" href="#1"> Navigation item 1 </a>
  </govuk-footer-nav-list>
  <govuk-footer-meta-info>
    <a govukLink="meta" href="#3"> Item 1 </a>
  </govuk-footer-meta-info>
</govuk-footer>
```
