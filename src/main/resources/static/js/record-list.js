/**
 * @typedef {Object} ValidationResult
 * @property {boolean} success true if record is valid, otherwise false.
 * @property {?string} reason   Why the record is invalid, if applicable. Optional if success is true.
 */

/**
 * @typedef {Object} Property
 * @property {string} display Display label of the property.
 * @property {string} label Unique identifier for the property (must match the json-format name of the property in
 * relation to its' formal class definition).
 * @property {string} type HTML &lt;input&gt; tag type.
 */

/**
 * Handler class for managing lists of records.
 * <br>
 * This class is to be paired with the DOM structure defined in includes/common/record-list.vm
 */
class RecordList {
    /**
     * Paginator used to handle large sets of records.
     * @var {Paginator}
     */
    paginator;

    /**
     * Identifier for the record type for use in ajax requests.
     * @var {string} ajaxId
     */
    ajaxId;

    /**
     * Top-most container for the record-list DOM structure.
     * @var {jQuery} container
     */
    container;
    /**
     * Container for the list of records.
     * @var {jQuery} recordListContainer
     */
    recordListContainer;
    /**
     * Container for edit/add functionality.
     * <br>
     * In [edit mode]{@link this.setEditMode}, this container will represent an existing record and allow for
     * modification and deletion.
     * <br>
     * In [add mode]{@link this.setAddMode}, this container will represent a new record to be added.
     * @var {jQuery} recordEditContainer
     */
    recordEditContainer;

    recordCountElement;

    /**
     * Record-list element currently selected (if null, no element is selected/edit container is in add mode).
     * @var {jQuery} selectedRecordElement
     */
    selectedRecordElement = null;

    /**
     * Number of records. See [RecordList::retrieveRecordCount]{@link this.retrieveRecordCount}
     * @var {number} recordCount
     */
    recordCount = 0;
    /**
     * Index from which to begin the record-list, for when there are a large number of records.
     * @var {number} listBegin
     */
    listBegin = 0;
    /**
     * Number of records to be retrieved and displayed.
     * @var {number} listAmount
     */
    listAmount = 20;

    /**
     * Callback for validating the contents of the edit container.
     * @var {function} validateRecordCallback
     * @param {Object} record Record to be validated.
     * @returns {ValidationResult} Validation status.
     */
    validateRecordCallback;
    retrieveRecordCountCallback;

    /**
     * Create new RecordList.
     * <br>
     * Will initialise to the following default state:
     * <ul>
     *     <li>[Edit container]{@link this.recordEditContainer} will be set to [add mode]{@link this.setAddMode} (no
     *     record selected).</li>
     *     <li>[Record list]{@link this.recordListContainer} will be updated with the default index and amount.</li>
     * </ul>
     *
     * @param {string} ajaxId See [RecordList::ajaxId]{@link this#ajaxId}
     * @param {jQuery} container See [RecordList::container]{@link this#container}
     * @param {function} validateRecordCallback See [RecordList::validateRecordCallback]{@link this#validateRecordCallback}
     * @param {Array[Property]} properties List of all properties of each record.
     * @param {function} retrieveRecordCountCallback See [RecordList::retrieveRecordCountCallback]{@link this#retrieveRecordCountCallback}
     */
    constructor(ajaxId, container, validateRecordCallback, properties, {retrieveRecordCountCallback = null}) {
        this.ajaxId = ajaxId;
        this.container = container;
        this.validateRecordCallback = validateRecordCallback;

        this.retrieveRecordCountCallback = retrieveRecordCountCallback;

        this.container.addClass("record-container");

        let containerId = this.container.attr("id");

        this.container.append(`
            <div class="record-list-manager">
                <div class="record-list-container"></div>
                <div class="record-list-footer">
                    <div id="${containerId}-paginator"></div>
                </div>
            </div>
            <div class="record-edit-container">
                <div class="edit-container-table"></div>
            </div>
        `);

        this.recordListContainer = this.container.find(".record-list-container");
        this.recordEditContainer = this.container.find(".record-edit-container");

        let recordEditTable = this.recordEditContainer.find(".edit-container-table");
        console.log(properties);
        for (let prop of properties) {
            recordEditTable.append(`<label>${prop["display"]}</label>`);
            recordEditTable.append(`<input class="property-${prop["label"]}" type="${prop.type}" />`);
        }
        let updateBtn = $(`<button class="btn-update">Update</button>`);
        updateBtn.on("click", function() {
            this.sendUpdateRecord();
        }.bind(this));
        recordEditTable.append(updateBtn);
        let removeBtn = $(`<button class="btn-remove">Remove</button>`);
        removeBtn.on("click", function() {
            this.sendRemoveRecord();
        }.bind(this));
        recordEditTable.append(removeBtn);
        let addBtn    = $(`<button class="btn-add">Add</button>`);
        addBtn.on("click", function() {
            this.sendAddRecord();
        }.bind(this));
        recordEditTable.append(addBtn);

        this.recordCountElement = this.container.find(".record-count");

        this.paginator = new Paginator(this.container.find(`#${containerId}-paginator`), function(newIndex, oldIndex) {
            this.listBegin = newIndex * this.listAmount;
            this.retrieveRecordList();
        }.bind(this), 5);

        this.deselectRecord();
        this.retrieveRecordList(this.retrieveRecordCount.bind(this));
    }

    /**
     * @returns {Object} Record object formed from the names and values of all properties within the
     * [edit container]{@link this.recordEditContainer}.
     */
    getRecordFromContainer() {
        let record = {};
        for (let property of this.recordEditContainer.find(`input[class*="property-"]`)) {
            let element = $(property);
            let propertyName = element.attr("class").match(/property-(.+)/)[1];
            record[propertyName] = element.val() === "" ? null : element.val();
        }
        return record;
    }

    /**
     * Deselect the current record and set the [edit container]{@link this.recordEditContainer} to
     * [add mode]{@link this.setAddMode}.
     * <br>
     * See [RecordList::selectRecordFromList]{@link this.selectRecordFromList} for more details.
     */
    deselectRecord() {
        this.selectRecordFromList(null);
    }

    /**
     * Select the record in the [record list]{@link this.recordListContainer} with an id matching recordId, or deselect
     * if no match is found.
     * <br>
     * See [RecordList::selectRecordFromList]{@link this.selectRecordFromList} for more details.
     * @param {number|string} recordId Id of the record to search for.
     */
    selectRecordFromId(recordId) {
        let element = this.recordListContainer.find(`.record-element[data-id="${recordId}"]`);
        element = element.length === 0 ? null : element;
        this.selectRecordFromList(element);
    }

    /**
     * Select the provided record, or deselect if null is provided. If recordElement is already selected, it will be
     * deselected instead.
     * <br>
     * On selection, the [edit container]{@link this.recordEditContainer} will be set to
     * [edit mode]{@link this.setEditMode}.
     * <br>
     * On deselection, the [edit container]{@link this.recordEditContainer} will be set to
     * [add mode]{@link this.setAddMode}.
     * @param {?jQuery} recordElement Record list element to select.
     */
    selectRecordFromList(recordElement = null) {
        if (this.selectedRecordElement !== null) {
            this.selectedRecordElement.removeClass("selected");
            // Toggle the element if already selected
            if (recordElement !== null && recordElement.data("id") === this.selectedRecordElement.data("id"))
                recordElement = null;
        }

        this.selectedRecordElement = recordElement;
        if (this.selectedRecordElement === null) {
            this.setAddMode();
        } else {
            this.selectedRecordElement.addClass("selected");
            let recordId = this.selectedRecordElement.data("id");
            this.retrieveRecord(recordId);
        }
    }

    /**
     * Set the [edit container]{@link this.recordEditContainer} to add mode, clearing all property and data attributes,
     * and showing only the add button.
     */
    setAddMode() {
        this.recordEditContainer.data("id", "null");

        this.recordEditContainer.find("input").val("");

        this.recordEditContainer.find(".btn-update").hide();
        this.recordEditContainer.find(".btn-add").show();
        this.recordEditContainer.find(".btn-remove").hide();
    }

    /**
     * Set the [edit container]{@link this.recordEditContainer} to edit mode, setting all properties and data attributes
     * to values matching the given record.
     * @param {Object} record Record containing data to populate the inputs with.
     * @param {number|string} recordId Unique id of the record.
     */
    setEditMode(record, recordId) {
        this.recordEditContainer.data("id", recordId);

        for (const [key, value] of Object.entries(record)) {
            this.recordEditContainer.find(`input.property-${key}`).val(value);
        }

        this.recordEditContainer.find(".btn-update").show();
        this.recordEditContainer.find(".btn-add").hide();
        this.recordEditContainer.find(".btn-remove").show();
    }

    /**
     * Retrieve the record matching recordId through ajax request.
     * <br>
     * The [edit container]{@link this.recordEditContainer} will be set to [edit mode]{@link this.setEditMode} for the
     * retrieved record on success. Does nothing on failure.
     * @param {number|string} recordId Unique id of the record to be queried for.
     * @param {?function} onResponse Callback to be executed after a successful response is received and the container is
     * updated.
     */
    retrieveRecord(recordId, onResponse = null) {
        runAjax(
            "post",
            `/get_${this.ajaxId}`,
            {"recordId": recordId},
            this.onRetrieveRecord.bind(this, onResponse)
        );
    }

    /**
     * Extension of [RecordList::retrieveRecord]{@link this.retrieveRecord}.
     */
    onRetrieveRecord(onResponse, response) {
        this.setEditMode(response["record"], response["recordId"]);
        if (onResponse)
            onResponse();
    }

    /**
     * Retrieve a list of records within bounds of [RecordList::listBegin]{@link this.listBegin} and
     * [RecordList::listAmount]{@link this.listAmount} through ajax request and repopulate the
     * [record list]{@link this.recordListContainer} with the results on success. Does nothing on failure.
     * @param {?function} onResponse Callback to be executed after a successful response is received and the container is
     * updated.
     */
    retrieveRecordList(onResponse = null) {
        runAjax(
            "post",
            `/get_${this.ajaxId}_list_ranged`,
            {"begin": this.listBegin, "amount": this.listAmount},
            this.onRetrieveRecordList.bind(this, onResponse)
        );
    }

    /**
     * Extension of [RecordList::retrieveRecordList]{@link this.retrieveRecordList}.
     */
    onRetrieveRecordList(onResponse, response) {
        this.recordListContainer.find("p").remove(".record-element");

        for (let record of response["records"]) {
            let element = $(`<p class="record-element" data-id="${record["recordId"]}">${record["display"]}</p>`);
            this.recordListContainer.append(element);
            element.on("click", this.selectRecordFromList.bind(this, element));
        }
        if (onResponse)
            onResponse(response);
    }

    /**
     * Retrieve the number of records in the database
     * @param onResponse
     */
    retrieveRecordCount(onResponse) {
        runAjax(
            "post",
            `/get_${this.ajaxId}_count`,
            null,
            this.onRetrieveRecordCount.bind(this, onResponse)
        );
    }

    /**
     * On receiving record count ... TODO add proper description
     * @param onResponse
     * @param response
     */
    onRetrieveRecordCount(onResponse, response) {
        this.recordCount = response["count"];
        this.paginator.setNumPages(Math.ceil(this.recordCount / this.listAmount));
        this.recordCountElement.html(`${this.recordCount} records.`)

        if (this.retrieveRecordCountCallback)
            this.retrieveRecordCountCallback(response["count"]);
    }

    /**
     * Send the current contents of the [edit container]{@link this.recordEditContainer} to be added to the database
     * through ajax request. Will set the [edit container]{@link this.recordEditContainer} to
     * [add mode]{@link this.setAddMode} on success. Does nothing on failure.
     * @param {?function} onResponse Callback to be executed after a successful response is received and the container is
     * updated.
     */
    sendAddRecord(onResponse = null) {
        let record = this.getRecordFromContainer();

        let validate = this.validateRecordCallback(record);
        if (!validate.success) {
            alert(`Record Invalid. Reason:\n${validate.reason}`);
            return;
        }

        runAjax(
            "post",
            `/add_${this.ajaxId}`,
            {"record": record},
            this.onSendAddPerson.bind(this, function(onResponse) {
                this.retrieveRecordCount(onResponse);
            }.bind(this, onResponse))
        );
    }

    /**
     * Extension of [RecordList::sendAddRecord]{@link this.sendAddRecord}.
     */
    onSendAddPerson(onResponse, response) {
        this.deselectRecord();
        this.retrieveRecordList(onResponse);
    }

    /**
     * Send the current contents of the [edit container]{@link this.recordEditContainer} to update the currently
     * selected record in the database through ajax request. Will update the
     * [record list]{@link this.recordListContainer} and reselect the record on success. Does nothing on failure.
     * @param {?function} onResponse Callback to be executed after a successful response is received and the container is
     * updated.
     */
    sendUpdateRecord(onResponse = null) {
        let record = this.getRecordFromContainer();

        let validate = this.validateRecordCallback(record);
        if (!validate.success) {
            alert(`Record Invalid. Reason:\n${validate.reason}`);
            return;
        }

        runAjax(
            "post",
            `/update_${this.ajaxId}`,
            {"record": record, "recordId": this.recordEditContainer.data("id")},
            this.onSendUpdateRecord.bind(this, onResponse)
        );
    }

    /**
     * Extension of [RecordList::sendUpdateRecord]{@link this.sendUpdateRecord}.
     */
    onSendUpdateRecord(onResponse, response) {
        let recordId = this.selectedRecordElement.data("id");
        this.deselectRecord();
        this.retrieveRecordList(function (recordId, onResponse) {
            this.selectRecordFromId(recordId);
            if (onResponse)
                onResponse();
        }.bind(this, recordId, onResponse));
    }

    /**
     * Delete the database record matching the currently selected record through ajax request and set the
     * [edit container]{@link this.recordEditContainer} to [add mode]{@link this.setAddMode} on success. Does nothing
     * on failure.
     * @param {?function} onResponse Callback to be executed after a successful response is received and the container is
     * updated.
     */
    sendRemoveRecord(onResponse = null) {
        if (!confirm("Warning! This action cannot be undone. Continue anyway?"))
            return;
        let recordId = this.selectedRecordElement.data("id");
        runAjax(
            "post",
            `/remove_${this.ajaxId}`,
            {"recordId": recordId},
            this.onSendRemoveRecord.bind(this, function(onResponse) {
                this.retrieveRecordCount(onResponse);
            }.bind(this, onResponse))
        )
    }

    /**
     * Extension of [RecordList::sendRemoveRecord]{@link this.sendRemoveRecord}.
     */
    onSendRemoveRecord(onResponse, response) {
        this.deselectRecord();
        this.retrieveRecordList(onResponse);
    }
}
