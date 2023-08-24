let recordLimit = 20;
let recordIndex = 0;

let paginator = new Paginator($("#record-list-paginator"),function (new_index, old_index) {
    recordIndex = new_index * recordLimit;
    getPeople()
}, 5);
getPersonCount();

// BUTTON ACTIONS
let updateBtn = $(`.btn-update`);
updateBtn.on("click", updatePerson);
let removeBtn = $(`.btn-remove`);
// removeBtn.on("click", removePerson.bind(null, $(".edit-container-table").attr(`data-id`)));
let addBtn = $(`.btn-add`);
addBtn.on("click", addPerson);

function setPersonForm(onResponse) {
    // Collect data from input fields
    console.log($(".property-dateOfBirth").val());
    let personId = $(".edit-container-table").attr("data-id");
    let dateOfBirth = $(".property-dateOfBirth").val();
    let firstName = $(".property-firstName").val();
    let lastName = $(".property-lastName").val();
    let middleNames = $(".property-middleNames").val();
    let title = $(".property-title").val();
    let nickName = $(".property-nickName").val();
    let maidenName = $(".property-maidenName").val();
    let marriedName = $(".property-marriedName").val(); // You need to have this field in your HTML

    // Create a data object to send in the POST request
    let postData = {};
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

// GET NUMBER OF PERSON RECORDS
function getPersonCount(onResponse) {
    $.post(
        "/getPersonCount",
        null,
        function(response) {
            let recordCount = response["record_count"];
            paginator.setNumPages(recordCount / recordLimit);
        }
    )
}

// UPDATE EXISTING PERSON
function updatePerson(onResponse) {
    console.log("Update person")
    let data = setPersonForm(onResponse);
    console.log(data);
    $.post(
        "/updatePerson",
        data,
        function(response) {
            $("#message").text("Record updated.")
            setTimeout(function () {$("#message").html("&nbsp;")}, 3000, undefined);
            getPeople()
        }
    )
}


// ADD PERSON TO DATABASE
function addPerson(onResponse) {
    console.log("Add person")
    let data = setPersonForm(onResponse);
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

function removePerson(personId) {
    console.log("Delete person: " + personId);
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
// LOAD AND DISPLAY PEOPLE IN DIV
function getPeople(onResponse) {
    $.post(
        "/getPeople",
        `{"begin": ${recordIndex}, "amount": ${recordLimit}}`,
        onGetPeopleSuccess,
        "json",
    );
}

function onGetPeopleSuccess(response) {
    // let record_list_container = document.getElementById("record-list-container");
    $("#record-list-container").empty();
    let people = response["people"]
    for (let person of people) {
        let nickname ="";
        if (person.nickName === undefined || person.nickName === "") {
            nickname = "";
        } else nickname = "(" + person.nickName + ")";
        let p = $(`<p class="record-element" data-id="${person["personId"]}">${person["lastName"]}, ${person["firstName"]} ${nickname}</p>`);
        $("#record-list-container").append(p);
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
            $(".record-element.selected").removeClass("selected");
            //$(".edit-container-table").data("id",$(this).data("id"));
            $(this).addClass("selected")
            $(".edit-container-table").attr("data-id",$(this).data("id"));
            $(".edit-container-buttons").attr("data-id",$(this).data("id"));
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
            getAddresses($(this).data("id"))
        }
    });
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

function getAddresses(recordId) {
    $.post(
        "/getAddresses",
        "{recordId: " + recordId + "}",
        onGetAddressesSuccess
    )
}

function onGetAddressesSuccess(response) {
    console.log(response)
    $(".edit-container-address-list").empty()
    let addresses = response["addresses"]
    for (let address of addresses) {
        console.log(address)
        let p = $(`<p class="address-element" data-id="${address["addressId"]}">${address["addressLine1"]}</p>`);
        $(".edit-container-address-list").append(p);
    }
    onGetAddressesResponse(response)
}

function onGetAddressesResponse(response) {

}

function updateAddress(recordId) {

}

function removeAddress(recordId) {

}

function addAddress(recordId) {

}