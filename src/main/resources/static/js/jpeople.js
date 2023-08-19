function setForm(onResponse) {
    // Collect data from input fields
    let personId = $(".edit-container-table").data("id");
    console.log("person id: " + personId);
    let dateOfBirth = $(".property-dateOfBirth").val();
    let firstName = $(".property-firstName").val();
    let lastName = $(".property-lastName").val();
    let middleNames = $(".property-middleNames").val();
    let title = $(".property-title").val();
    let nickName = $(".property-nickName").val();
    let maidenName = $(".property-maidenName").val();
    let marriedName = $(".property-marriedName").val(); // You need to have this field in your HTML

    // Create a data object to send in the POST request
    let postData = new Object();
    postData.personId = personId;
    postData.dateOfBirth = dateOfBirth;
    postData.firstName = firstName;
    postData.lastName = lastName;
    postData.middleNames = middleNames;
    postData.title = title;
    postData.nickName = nickName;
    postData.maidenName = maidenName;
    postData.marriedName = marriedName;
    let data = JSON.stringify(postData)
    Object.keys(data).map((key) => (data[key] === null) ? data[key] = '-': data[key]);
    return data;
}

// UPDATE EXISTING PERSON
function updatePerson(onResponse) {
    let data = setForm(onResponse);
    $.post(
        "/updatePerson",
        data,
        function(response) {

        }
    )
}

// ADD PERSON TO DATABASE
function addPerson(onResponse) {
    let data = setForm(onResponse);
    $.post(
        "/addPerson", // Replace with your actual endpoint
        data,
        function(response) {
            getPeople();
            clearForm();
        },
        "json"
    );

}

// LOAD AND DISPLAY PEOPLE IN DIV
function getPeople(onResponse) {
    $.ajax({
        url: "/getPeople",
        type: "post",
        dataType: "json",
        success: onGetPeopleSuccess
    });
}

function onGetPeopleSuccess(response) {
    // let record_list_container = document.getElementById("record-list-container");
    let record_list_container = $("#record-list-container");
    record_list_container.empty();
    let people = response["people"]
    for (let person of people) {
        let p = $(`<p class="record-element" data-id="${person["personId"]}">${person["lastName"]}, ${person["firstName"]}</p>`);
        record_list_container.append(p);
    }
    onGetPeopleResponse(response)
}

function onGetPeopleResponse(response) {
    $(".record-element").on("click", function () {
        // if selected then unselect
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected")
            $(".btn-add").removeAttr("style");
            $(".btn-update").css("display", "none");
            $(".btn-remove").css("display", "none");
            $.post(
                "/getPerson",
                "{recordId: " + $(this).data("id") + "}",
                function (data, status, xhr) {
                    clearForm();
                },
                "json"
            );
        // if not selected, unset previous and select current
        } else {
            // unset previous record first
            let selected = $(".record-element.selected").removeClass("selected");
            $(".edit-container-table").data("id",$(this).data("id"));
            $(".edit-container-table").attr("data-id",$(this).data("id"));
            // $(this).addClass("selected");
            $(".btn-add").css("display", "none");
            $(".btn-update").removeAttr("style");
            $(".btn-remove").removeAttr("style");
            $.post(
                "/getPerson",
                "{recordId: " + $(this).data("id") + "}",
                function (data, status, xhr) {
                    //Set form values
                    $(".property-nickName").val(data.nickName);
                    $(".property-firstName").val(data.firstName);
                    $(".property-middleNames").val(data.middleNames);
                    $(".property-lastName").val(data.lastName);
                    $(".property-maidenName").val(data.maidenName);
                    $(".property-title").val(data.title);
                    $(".property-dateOfBirth").val(data.dateOfBirth);
                },
                "json"
            );
        }

    });
}

function removePerson(personId) {
    const response = confirm("Are you sure you want to delete this record?")
    if (response) {
        $.post(
            "/removePerson",
            "{personId: " + personId + "}",
            function () {
                getPeople();
                clearForm();
            },
            "json"
        )
    }
}

function clearForm() {
    // Clear form
    $(".edit-container-table").data("id",null)
    $(".property-nickName").val("");
    $(".property-firstName").val("");
    $(".property-middleNames").val("");
    $(".property-lastName").val("");
    $(".property-maidenName").val("");
    $(".property-title").val("");
    $(".property-dateOfBirth").val("");
    $(".btn-add").removeAttr("style");
    $(".btn-update").css("display", "none");
    $(".btn-remove").css("display", "none");
}