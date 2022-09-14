function backToTop(el = document.scrollingElement) {
  scrollToVal(250, el, 0);
  document.getElementById('header').focus();
}

/**
 *
 * Explanation:
 * - pi is the length/end point of the cosinus intervall (see below)
 * - newTimestamp indicates the current time when callbacks queued by requestAnimationFrame begin to fire.
 * (for more information see https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame)
 * - newTimestamp - oldTimestamp equals the delta time
 *
 * a * cos (bx + c) + d                        | c translates along the x axis = 0
 * = a * cos (bx) + d                            | d translates along the y axis = 1 -> only positive y values
 * = a * cos (bx) + 1                            | a stretches along the y axis = cosParameter = window.scrollY / 2
 * = cosParameter + cosParameter * (cos bx)  | b stretches along the x axis = scrollCount = Math.PI / (scrollDuration / (newTimestamp - oldTimestamp))
 * = cosParameter + cosParameter * (cos scrollCount * x)
 *
 * @param duration
 * @param el
 * @param valueScroll
 */
function scrollToVal (duration, el, valueScroll) {
  // cancel if already on top
  if (el.scrollTop === 0) return;

  const cosParameter = el.scrollTop / 2;
  let scrollCount = 0, oldTimestamp = null;

  function step (newTimestamp) {
    if (oldTimestamp !== null) {
      // if duration is 0 scrollCount will be Infinity
      scrollCount += Math.PI * (newTimestamp - oldTimestamp) / duration;
      if (scrollCount >= Math.PI) return el.scrollTop = valueScroll;
      el.scrollTop = cosParameter + cosParameter * Math.cos(scrollCount);
    }
    oldTimestamp = newTimestamp;
    window.requestAnimationFrame(step);
  }
  window.requestAnimationFrame(step);
}