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
    let data = setPersonForm(onResponse);
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
    let data = setPersonForm(onResponse);
    $.post(
        "/addPerson", // Replace with your actual endpoint
        data,
        function(response) {
            getPeople();
            clearPersonForm();
        },
        "json"
    );

}

function removePerson(personId) {
    const response = confirm("Are you sure you want to delete this record?")
    if (response) {
        $.post(
            "/removePerson",
            "{personId: " + personId + "}",
            function () {
                getPeople();
                clearPersonForm();
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
                "{personId: " + $(this).data("id") + "}",
                function (data, status, xhr) {
                    clearPersonForm();
                    clearAddressForm();
                },
                "json"
            );
        // if not selected, unset previous and select current
        } else {
            let personId = $(this).data("id")
            $(".record-element.selected").removeClass("selected");
            $(this).addClass("selected")
            $(".edit-container-table").attr("data-person-id", personId);
            $(".edit-container-address-table").attr("data-person-id", personId);
            // $(this).addClass("selected");
            $(".btn-add").css("display", "none");
            $(".btn-update").removeAttr("style");
            $(".btn-remove").removeAttr("style");
            $.post(
                "/getPerson",
                "{\"personId\": " + personId + "}",
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
            getAddresses(personId);
        }
    });
}



function clearPersonForm() {
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

function getAddresses(personId) {
    $.post(
        "/getAddresses",
        "{personId: " + personId + "}",
        onGetAddressesSuccess
    )
}

function onGetAddressesSuccess(response) {
    clearAddressForm()
    $(".edit-container-address-list").empty()
    let addresses = response["addresses"]
    for (let address of addresses) {
        let p = $(`<p class="address-element" data-address-id="${address["addressId"]}">${address["addressLine1"]}</p>`);
        $(".edit-container-address-list").append(p);
    }
    onGetAddressesResponse(response)
}

function onGetAddressesResponse(response) {
    $(".address-element").on("click", function() {
       if ($(this).hasClass("selected")) {
           $(this).removeClass("selected");
           $(".btn-address-add").removeAttr("style");
           $(".btn-address-update").css("display", "none");
           $(".btn-address-remove").css("display", "none");
           clearAddressForm();
       } else {
           $(".address-element").removeClass("selected");
           $(this).addClass("selected");
           $(".btn-address-add").css("display", "none");
           $(".btn-address-update").removeAttr("style");
           $(".btn-address-remove").removeAttr("style");
           $(".edit-container-address-table").attr("data-address-id",$(this).data("address-id"));
           $.post(
               "/getAddress",
               "{addressId: " + $(this).data("address-id") + ", personId: " + $(this).data("person-id") + "}",
               function(response) {
                   $(".property-addressLine1").val(response.addressLine1)
                   $(".property-addressLine2").val(response.addressLine2)
                   $(".property-addressLine3").val(response.addressLine3)
                   $(".property-city").val(response.city)
                   $(".property-county").val(response.county)
                   $(".property-country").val(response.country)
                   $(".property-postcode").val(response.postcode)
               }
           )
       }
    });
}

function clearAddressForm() {
    $(".edit-container-address-table").data("id",null);
    $(".property-addressLine1").val("");
    $(".property-addressLine2").val("");
    $(".property-addressLine3").val("");
    $(".property-city").val("");
    $(".property-county").val("");
    $(".property-country").val("");
    $(".property-postcode").val("");
    $(".btn-address-add").removeAttr("style");
    $(".btn-address-update").css("display", "none");
    $(".btn-address-remove").css("display", "none");
}

function setAddressForm(response) {
    let personId = $(".edit-container-table").attr("data-person-id")
    let addressId = $(".edit-container-address-table").attr("data-address-id");
    let addressLine1 = $(".property-addressLine1").val();
    let addressLine2 = $(".property-addressLine2").val();
    let addressLine3 = $(".property-addressLine3").val();
    let city = $(".property-city").val();
    let county = $(".property-county").val();
    let country = $(".property-country").val();
    let postcode = $(".property-postcode").val();

    let postData = {};
    postData.personId = personId;
    postData.addressId = addressId;
    postData.addressLine1 = addressLine1;
    postData.addressLine2 = addressLine2;
    postData.addressLine3 = addressLine3;
    postData.city = city;
    postData.county = county;
    postData.country = country;
    postData.postcode = postcode;
    let data = JSON.stringify(postData);
    Object.keys(data).map((key) => (data[key] === null) ? data[key] = '-': data[key]);
    return data;
}
function updateAddress(addressId) {

}

function removeAddress(addressId) {

}

function addAddress(personId) {
    let data = setAddressForm(personId);
    $.post(
        "/addAddress",
        data,
        function() {
            getAddresses($(".edit-container-address-table").data("person-id"));
            clearAddressForm();
        }
    )
}