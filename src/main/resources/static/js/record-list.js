
class RecordList {
    ajaxId;

    container;
    recordListContainer;
    recordEditContainer;

    selectedRecordElement = null;

    listBegin = 0;
    listAmount = 10;

    constructor(ajaxId, container) {
        this.ajaxId = ajaxId;

        this.container = container;
        this.recordListContainer = this.container.find(".record-list-container");
        this.recordEditContainer = this.container.find(".record-edit-container");

        this.setAddMode();
        this.retrieveRecordList();
    }

    getRecordFromContainer() {
        let record = {};
        for (let property of this.recordEditContainer.find(`input[class*="property-"]`)) {
            let element = $(property);
            let propertyName = element.attr("class").match(/property-(.+)/)[1];
            record[propertyName] = element.val();
        }
        return record;
    }

    deselectRecord() {
        this.selectRecordFromList(null);
    }

    selectRecordFromId(recordId) {
        let element = this.recordListContainer.find(`.record-element[data-id="${recordId}"]`);
        element = element.length === 0 ? null : element;
        this.selectRecordFromList(element);
    }

    selectRecordFromList(recordElement) {
        if (this.selectedRecordElement !== null) {
            this.selectedRecordElement.removeClass("selected");
            if (recordElement !== null && recordElement.data("id") === this.selectedRecordElement.data("id"))
                recordElement = null;
        }

        this.selectedRecordElement = recordElement;
        if (this.selectedRecordElement === null) {
            this.setAddMode();
        } else {
            this.selectedRecordElement.addClass("selected");
            let recordId = this.selectedRecordElement.data("id");
            console.log(this.selectedRecordElement, recordId);
            this.retrieveRecord(recordId);
        }
    }

    setAddMode() {
        this.recordEditContainer.data("id", "null");

        this.recordEditContainer.find("input").val("");

        this.recordEditContainer.find(".btn-update").hide();
        this.recordEditContainer.find(".btn-add").show();
        this.recordEditContainer.find(".btn-remove").hide();
    }

    setEditMode(record, recordId) {
        this.recordEditContainer.data("id", recordId);

        for (const [key, value] of Object.entries(record)) {
            this.recordEditContainer.find(`input.property-${key}`).val(value);
        }

        this.recordEditContainer.find(".btn-update").show();
        this.recordEditContainer.find(".btn-add").hide();
        this.recordEditContainer.find(".btn-remove").show();
    }

    retrieveRecord(recordId) {
        runAjax(
            "post",
            `/get_${this.ajaxId}`,
            {"recordId": recordId},
            this.onRetrieveRecord.bind(this)
        );
    }

    onRetrieveRecord(response) {
        this.setEditMode(response["record"], response["recordId"]);
    }

    retrieveRecordList(onResponse) {
        runAjax(
            "post",
            `/get_${this.ajaxId}_list_ranged`,
            {"begin": this.listBegin, "amount": this.listAmount},
            this.onRetrieveRecordList.bind(this, onResponse)
        );
    }

    onRetrieveRecordList(onResponse, response) {
        this.recordListContainer.empty();

        for (let record of response["records"]) {
            let element = $(`<p class="record-element" data-id="${record["recordId"]}">${record["display"]}</p>`);
            this.recordListContainer.append(element);
            console.log(element);
            element.on("click", this.selectRecordFromList.bind(this, element));
        }
        if (onResponse)
            onResponse(response);
    }

    sendAddRecord(onResponse) {
        let record = this.getRecordFromContainer();

        // if (!this.validateRecord(record))
        //     return;

        runAjax(
            "post",
            `/add_${this.ajaxId}`,
            {"record": record},
            this.onSendAddPerson.bind(this, onResponse)
        );
    }

    onSendAddPerson(onResponse, response) {
        this.deselectRecord();
        this.retrieveRecordList(onResponse);
    }

    sendUpdateRecord(onResponse) {
        let record = this.getRecordFromContainer();

        // if (!this.validateRecord(record))
        //     return;

        runAjax(
            "post",
            `/update_${this.ajaxId}`,
            {"record": record, "recordId": this.recordEditContainer.data("id")},
            this.onSendUpdateRecord.bind(this, onResponse)
        );
    }

    onSendUpdateRecord(onResponse, response) {
        let recordId = this.selectedRecordElement.data("id");
        this.deselectRecord();
        this.retrieveRecordList(function (recordId, onResponse) {
            this.selectRecordFromId(recordId);
            if (onResponse)
                onResponse();
        }.bind(this, recordId, onResponse));
    }

    sendRemoveRecord(onResponse) {
        if (!confirm("Warning! This action cannot be undone. Continue anyway?"))
            return;
        let recordId = this.selectedRecordElement.data("id");
        runAjax(
            "post",
            `/remove_${this.ajaxId}`,
            {"recordId": recordId},
            this.onSendRemoveRecord.bind(this, onResponse)
        )
    }

    onSendRemoveRecord(onResponse, response) {
        this.deselectRecord();
        this.retrieveRecordList(onResponse);
    }
}
