/**
 * @typedef {Object} Tab
 * @property {string} display Display name for this tab.
 * @property {jQuery} content Content element to be displayed when this tab is selected. Will be moved under the
 * tab-content-container subcomponent.
 */

/**
 * Handler class for managing a tab-list.
 * <br>
 * This class is to be paired with the DOM structure defined in includes/common/tab-list.vm
 */
class TabList {
    /**
     * Unique identifier for this tab-list.
     * @var {string} name
     */
    name;

    /**
     * Top-most container for the tab-list DOM structure.
     * @var {jQuery} container
     */
    container;

    /**
     * Currently selected tab, or null if none is selected.
     * @var {?jQuery} selectedTab
     */
    selectedTab = null;
    /**
     * Content of the currently selected tab, or null if none is selected;
     * @var {?jQuery} selectedTabContent
     */
    selectedTabContent = null;

    /**
     * localStorage id for saving which tab is currently selected.
     * @var {string} selectedTabStorageId
     */
    selectedTabStorageId;

    /**
     * Create new TabList.
     * <br>
     * Will initialize to the following default state:
     * <ul>
     *     <li>All tab-contents will be disabled.</li>
     *     <li>The [selected tab storage data]{@link this.selectedTabStorageId} will be read and the stored tab will
     *     be selected (if no tab is found none will be selected).</li>
     * </ul>
     * @param {string} name See [TabList::name]{@link this.name}
     * @param {jQuery} container See [TabList::container]{@link this.container}
     * @param {Array[Tab]} tabs List of all tabs for this tab-list.
     */
    constructor(name, container, tabs) {
        this.name = name;
        this.container = container;

        this.container.addClass("tab-list-container");

        let containerId = this.container.attr("id");

        this.container.append(`
            <div class="tab-list"></div>
            <div class="tab-content-container"></div>
        `);

        let tabList = this.container.find(".tab-list");
        let tabContent = this.container.find(".tab-content-container");
        for (let i = 0; i < tabs.length; i++) {
            let tab = tabs[i];

            let cont = tab["content"];
            tabContent.append(cont);
            cont.hide();

            let tabId = cont.attr("id");

            let btn = $(`<button id="${tabId}-btn" data-target="${tabId}">${tab["display"]}</button>`)
            btn.on("click", function(btn) {
                this.selectTab(btn);
            }.bind(this, btn));
            tabList.append(btn);
        }

        this.selectedTabStorageId = `${name}-selected-tab`;

        if (localStorage[this.selectedTabStorageId]) {
            let tab = $(`#${localStorage[this.selectedTabStorageId]}`)
            if (tab)
                this.selectTab(tab);
        }
    }

    /**
     * Select the tab corresponding to the provided button, or deselect if null is provided. If tab is already selected,
     * it will be deselected instead.
     * <br>
     * On selection, the currently selected tab (if applicable) will be deselected and its contents hidden. The newly
     * selected tab (if applicable) will then be marked as selected and its contents will be shown.
     * @param {?jQuery} tab Button corresponding to the tab being selected.
     */
    selectTab(tab) {
        console.log(tab);
        if (this.selectedTab !== null) {
            this.selectedTab.removeClass("selected");
            this.selectedTabContent.hide();
        }

        if (tab === null || tab.is(this.selectedTab)) {
            this.selectedTab = null;
            this.selectedTabContent = null;
        } else {
            let content = $(`#${tab.data("target")}`);
            this.selectedTab = tab;
            this.selectedTabContent = content;
            this.selectedTab.addClass("selected");
            this.selectedTabContent.show();
        }

        localStorage[this.selectedTabStorageId] = this.selectedTab ? this.selectedTab.attr("id") : null;
    }
}
