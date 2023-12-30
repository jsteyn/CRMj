let recordLimit = 20;
let recordIndex = 0;
let count = 0;
let paginator = new Paginator($("#record-list-paginator"),function (new_index, old_index) {
    recordIndex = new_index * recordLimit;
    getPeople()
}, 5);
getPersonCount();

// BUTTON ACTIONS
// Updated the selected person record
let updateBtn = $(`.btn-update`);
updateBtn.on("click", updatePerson);
// Remove the selected person
// let removeBtn = $(`.btn-remove`);
// removeBtn.on("click", removeLinkedAddress);
// Add a new person
let addBtn = $(`.btn-add`);
addBtn.on("click", addPerson);
// Append an address to the list of addresses for a person
let addAddressBtn = $(`.btn-address-add-selected`);
addAddressBtn.on("click", appendAddress);

function setPersonForm() {
    // Collect data from input fields
    console.log("setPersonForm");
    let personId = $(".edit-container-table").attr("data-person-id");
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
function getPersonCount() {
    console.log("getPersonCount");
    $.post(
        "/getPersonCount",
        null,
        function(response) {
            let recordCount = response["record_count"];
            count = recordCount;
            paginator.setNumPages(recordCount / recordLimit);
        }
    )
}

// UPDATE EXISTING PERSON
function updatePerson(onResponse) {
    console.log("updatePerson");
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
    console.log("addPerson");
    let data = setPersonForm(onResponse);
    $.post(
        "/addPerson",
        data,
        function(response) {
            getPeople();
            clearPersonForm();
            clearAddressForm();
        },
        "json"
    );

}

function removePerson(personId) {
    console.log("removePerson");
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
    console.log("getPeople");
    $.post(
        "/getPeople",
        `{"begin": ${recordIndex}, "amount": ${recordLimit}}`,
        onGetPeopleSuccess,
        "json"
    );
}

function onGetPeopleSuccess(response) {
    console.log("onGetPeopleSuccess");
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
    console.log("onGetPeopleResponse")
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

function resetForAdd() {
    clearAddressForm();
    clearPersonForm();
    // TODO: unselect, name, linked address and address list
}


function clearPersonForm() {
    console.log("clearPersonForm");
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

/**
 * Get a list of addresses linked to a specific person
 * @param personId
 */
function getAddresses(personId) {
    console.log("getAddresses");
    $.post(
        "/getAddresses",
        "{personId: " + personId + "}",
        function(response) {
            clearAddressForm();
            onGetAddressesSuccess(response);
        }
    )
}

function onGetAddressesSuccess(response) {
    console.log("onGetAddressesSuccess");
    // clearAddressForm()
    updateListOfAddresses();
    $(".edit-container-address-list").empty()
    let addresses = response["addresses"];
    for (let address of addresses) {
                let p = $(`<p class="address-element" data-address-id="${address["addressId"]}">${address["addressLine1"]}</p>`);
        $(".edit-container-address-list").append(p);
    }
    onGetAddressesResponse(response);
}

function onGetAddressesResponse(response) {
    console.log("onGetAddressesResponse");
    $(".address-element").on("click", function() {
       if ($(this).hasClass("selected")) { // if address is selected, unselect it
           console.log("onGetAddressResponse - selected")
           $(this).removeClass("selected");
           $(".btn-address-add").removeAttr("style");
           $(".btn-address-update").css("display", "none");
           $(".btn-address-remove").css("display", "none");
           $(".btn-address-add-selected").css("display", "none")
           $(".edit-container-address-table").removeAttr("data-address-id");
           clearAddressForm()
       } else { // if not selected, unset previous and select current
           console.log("onGetAddressResponse - not selected");
           $(".address-element.selected").removeClass("selected");
           $(this).addClass("selected");
           $(".btn-address-add").css("display", "none");
           $(".btn-address-update").removeAttr("style");
           $(".btn-address-remove").removeAttr("style");
           $(".edit-container-address-table").attr("data-address-id",$(this).data("address-id"));
           $(".btn-address-add-selected").css("display", "none")
           listContainerState();
           $.post(
               "/getAddress",
               "{addressId: " + $(this).data("address-id") + ", personId: " + $(this).data("person-id") + "}",
               setAddressForm
           )
       }
    });
}

function setAddressForm(response) {
    console.log("SetAddressForm");
    let personId = $(".edit-container-table").attr("data-person-id")
    let addressId = $(".edit-container-address-table").attr("data-address-id");
    $(".property-addressLine1").val(response.addressLine1);
    $(".property-addressLine2").val(response.addressLine2);
    $(".property-addressLine3").val(response.addressLine3);
    $(".property-city").val(response.city);
    $(".property-county").val(response.county);
    $(".property-country").val(response.country);
    $(".property-postcode").val(response.postcode);
}

function clearAddressForm() {
    console.log("clearAddressForm");
    $(".edit-container-address-table").data("id", null);
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
    $(".btn-address-add-selected").css("display", "none")
}

function updateListOfAddresses() {
    console.log("updateListOfAddresses");
    $.post(
        "/getAddresses",
        "{\"personId\": 0}",
        function(response) {
            getListOfAddresses(response);
        }
    )
}

/**
 * Get a list of all the addresses in the database
 * @param response
 */
function getListOfAddresses(response) {
    console.log("getListOfAddresses")
    $(".list-container").empty()
    let addresses = response["addresses"]
    for (let address of addresses) {
        let p = $(`<p class="list-address-element" data-address-id="${address["addressId"]}">${address["addressLine1"]}</p>`);
        $(".list-container").append(p);
        p.on("click", onClickListElement.bind(p));
    }
}

/**
 * Add an onClick response to each address element
 */
function onClickListElement() {
    console.log("onClickListElement");
    if ($(this).hasClass("selected")) { // if address is selected, unselect it
        console.log("onClickListElement - selected")
        $(".edit-container-address-table").removeAttr("data-address-id");
        $(this).removeClass("selected");
        clearAddressForm();
    } else { // if not selected, unset previous and select current
        console.log("onClickListElement - not selected")
        let addressTable = $(".edit-container-address-table");
        addressTable.attr("data-address-id",$(this).data("address-id"));
        $(".list-address-element").removeClass("selected");
        $(this).addClass("selected");
        let addressId = addressTable.attr("data-address-id");
        console.log(addressId);
        $.post(
            "/getAddress",
            "{addressId: " + addressId + "}",
            setAddressForm
        );
        editContainerAddressListState();
    }
    $(".btn-address-add").css("display", "none");
    $(".btn-address-update").css("display", "none");
    $(".btn-address-remove").css("display", "none");
    $(".btn-address-add-selected").removeAttr("style");

}

/**
 * Fill the form with the data from response
 * @returns {string}
 */
function retrieveAddressFromForm() {
    console.log("retrieveAddressFromForm")
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
function updateAddress() {
    console.log("updateAddress");
    let addressId = $(".edit-container-address-table").attr("data-address-id");
    let data = retrieveAddressFromForm();
    $.post(
        "/updateAddress",
        data,
        function() {
            console.log("Updated");
            getAddresses($(".edit-container-address-table").attr("data-address-id"));
        }
    )
}

function removeAddress() {
    console.log("removeAddress");
}

function addAddress() {
    console.log("addAddress");
    $(".edit-container-address-table").attr("data-address-id",0);
    let data = retrieveAddressFromForm();
    console.log(data);
    $.post(
        "/addAddress",
        data,
        function() {
            getAddresses($(".edit-container-address-table").data("person-id"));
            clearAddressForm();
        }
    )
}

function appendAddress() {
    console.log("appendAddress");
    let addressTable = $(".edit-container-address-table");
    let personId = addressTable.attr("data-person-id");
    let addressId = addressTable.attr("data-address-id");
    console.log("Append address " + addressId + " for " + personId);
    $.post(
        "/addAddress",
        "{\"addressId\": " + addressId + ",\"personId\": " + personId + "}",
        function() {
            console.log("Address " + addressId + " appended to " + personId);
        }
    )
    getAddresses(personId)
}


function editContainerAddressListState() {
    console.log("editContainerAddressListState")
    let container = $(".edit-container-address-list");
    container.children().removeClass("selected");
}

function listContainerState() {
    console.log("listContainerState")
    let container = $(".list-container");
    container.children().removeClass("selected");
}

/**
 * Remove record from person_addresses that links the person and the address
 */
function removeLinkedAddress() {
    let addressTable = $(".edit-container-address-table")
    let personId = addressTable.attr("data-person-id");
    let addressId = addressTable.attr("data-address-id");
    console.log("removeLinkedAddress: Remove address link: " + personId + " to " + addressId);
    $.post(
        "/removeLinkedAddress",
        "{personId: " + personId + ", addressId: " + addressId + "}",
        function() {
            clearAddressForm();
            updateListOfAddresses();
            getAddresses(personId);
        }
    )
}