const guidanceLinks = [
  document.querySelector("#topic-accessing-the-registry-link"),
  document.querySelector("#topic-downloading-the-app-link"),
  document.querySelector("#topic-for-apple-ios-link"),
  document.querySelector("#topic-for-android-link"),
];
const guidanceArticlesEls = [
  document.querySelectorAll(".topic-accessing-the-registry")[0],
  document.querySelectorAll(".topic-downloading-the-app")[0],
  document.querySelectorAll(".topic-for-apple-ios")[0],
  document.querySelectorAll(".topic-for-android")[0],
];
const openGuidanceModalLink = document.querySelector("#guidance-link");
const modalGuidanceCloseButtons = document.querySelectorAll(
  ".gem-c-modal-dialogue__close-button"
);
const mask = document.querySelector('#mask');
const helpSystem = document.querySelector('#help-system');

guidanceLinks.forEach((guidanceLink) => {
  addEvent("click", guidanceLink, () => {
    const articleClassName = guidanceLink.id.substring(
      0,
      guidanceLink.id.length - 5
    );
    guidanceLinks.forEach((guidanceLink2) => {
      if (
        guidanceLink2.classList.contains("active") &&
        guidanceLink2 !== guidanceLink
      ) {
        guidanceLink2.className = guidanceLink2.className.replace(
          /\bactive\b/g,
          "non-active"
        );
        guidanceLink2.setAttribute('aria-current', false);
      }
      if (guidanceLink2 === guidanceLink) {
        guidanceLink2.className = guidanceLink2.className.replace(
          /\bnon-active\b/g,
          "active"
        );
        guidanceLink2.setAttribute('aria-current', true);
      }
    });
    guidanceArticlesEls.forEach((guidanceArticlesEl) => {
      if (guidanceArticlesEl.classList.contains(articleClassName)) {
        if (
          !guidanceArticlesEl.classList.contains(articleClassName + "-target")
        ) {
          guidanceArticlesEl.classList.add(articleClassName + "-target");
        }
      } else {
        if (guidanceArticlesEl.classList.length > 1) {
          guidanceArticlesEl.classList.remove(
            guidanceArticlesEl.classList[
              guidanceArticlesEl.classList.length - 1
            ]
          );
        }
      }
    });
    scrollToVal(250, mask, 0);
  });
});

addEvent("click", openGuidanceModalLink, () => {
  getDocumentBody().classList.add("govuk-noselect"); 
  setTimeout(function() {helpSystem.focus()}, 100);
});

modalGuidanceCloseButtons.forEach((guidanceCloseButton) =>
  addEvent("click", guidanceCloseButton, () => {
    getDocumentBody().classList.remove("govuk-noselect");
  })
);

function getDocumentBody() {
  return document.body ? document.body : document.documentElement;
}

function addEvent(evnt, elem, func) {
  if (elem) {
    if (elem.addEventListener)
      // W3C DOM
      elem.addEventListener(evnt, func, false);
    else if (elem.attachEvent) {
      // IE DOM
      elem.attachEvent("on" + evnt, func);
    } else {
      // No much to do
      elem["on" + evnt] = func;
    }
  }
}
