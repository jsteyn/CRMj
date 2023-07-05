
let currentContent = null;
let currentContentButton = null;

$(".tab button").on("click", function () {
    let newContentButton = $(this);
    let newContent = $("#" + $(this).data("target"));

    if (currentContentButton != null) {
        currentContent.removeClass("active");
        currentContentButton.removeClass("active");
    }

    if (newContentButton.is(currentContentButton)) {
        currentContent = null;
        currentContentButton = null;
    } else {
        currentContent = newContent;
        currentContentButton = newContentButton;
        currentContent.addClass("active");
        currentContentButton.addClass("active");
    }
});
