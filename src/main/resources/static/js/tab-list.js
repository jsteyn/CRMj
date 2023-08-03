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
     */
    constructor(name, container) {
        this.name = name;
        this.container = container;

        this.container.find(".tab-list button").on("click", {tabList: this}, function(event) {
            event.data.tabList.selectTab($(this));
        });
        this.container.find(".tab-content-container .tab-content").hide();

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
