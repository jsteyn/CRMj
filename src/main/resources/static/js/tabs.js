
let currentContent = null;
let currentContentButton = null;
if (localStorage["currentContentButton"] && $("#" + localStorage["currentContentButton"])) {
    onTabSelect($("#" + localStorage["currentContentButton"]));
}

$(".tab button").on("click", function () {
    onTabSelect($(this));
});

function onTabSelect(newContentButton) {
    let newContent = $("#" + newContentButton.data("target"));

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
    localStorage["currentContentButton"] = currentContentButton ? currentContentButton.attr("id") : null;
}
