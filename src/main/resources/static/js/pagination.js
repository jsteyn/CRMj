/**
 * Handler class for managing a paginator.
 * <br>
 * This class is to be paired with the DOM structure defined in includes/common/pagination.vm
 */
class Paginator {
    /**
     * Maximum number of indices (pages) to show at a time for direct selection.
     * @var {number}
     */
    NUM_DISPLAY_INDICES = 5;

    /**
     * Top-most container for the paginator DOM structure.
     * @var {jQuery}
     */
    container;
    /**
     * Button for incrementing the index by 1.
     * @var {jQuery}
     */
    incrementButton;
    /**
     * Button for decrementing the index by 1.
     * @var {jQuery}
     */
    decrementButton;

    /**
     * Callback executed whenever a new page index is set.
     * <br>
     * Will be called once during construction of this class, with both parameters set to 0.
     * @var {function} onPageSelect
     * @param {number} newIndex New page index.
     * @param {number} oldIndex Old page index.
     */
    onPageSelect;

    /**
     * Total number of pages.
     * @var {number}
     */
    numPages;
    /**
     * Index of the currently selected page.
     * @var {number}
     */
    currentIndex = 0;
    /**
     * Button for the currently selected page.
     * @var {jQuery}
     */
    currentButton = null;

    /**
     * If true, setting an index outside the bounds of 0-[numPages]{@link this.numPages} will wrap the value to within
     * the bounds. If false, the value will be clamped instead.
     * @var {boolean}
     */
    wrapMode;

    /**
     * Create new Paginator
     * <br>
     * Will be initialized with the following default state:
     * <ul>
     *     <li>Page 0 will be selected</li>
     * </ul>
     * @param container See [Paginator::container]{@link this.container}.
     * @param onPageSelect See [Paginator::onPageSelect]{@link this.onPageSelect}.
     * @param numPages See [Paginator::numPages]{@link this.numPages}. Will be clamped to a positive non-zero integer.
     * @param wrapMode See [Paginator::wrapMode]{@link this.wrapMode}.
     */
    constructor(container, onPageSelect, numPages, {wrapMode=false}={}) {
        this.container = container;
        this.onPageSelect = onPageSelect;

        this.wrapMode = wrapMode;

        this.incrementButton = this.container.find(".paginator-btn.increment");
        this.decrementButton = this.container.find(".paginator-btn.decrement");

        this.setNumPages(numPages);
        this.setIndex(0);

        this.incrementButton.on("click", function() {
            this.setIndex(this.currentIndex + 1);
        }.bind(this));
        this.decrementButton.on("click", function() {
            this.setIndex(this.currentIndex - 1);
        }.bind(this));
    }

    setNumPages(numPages) {
        this.numPages = numPages < 1 ? 1 : numPages;
        this.updatePageList();
        this.setIndex(this.currentIndex);
    }

    setIndex(newIndex) {
        console.log(newIndex);
        newIndex = this.wrapMode ?
            mod(newIndex, this.numPages) :
            clamp(newIndex, 0, this.numPages - 1);

        if (newIndex === this.currentIndex)
            return;

        this.onPageSelect(newIndex, this.currentIndex);
        this.currentIndex = newIndex;
    }

    updatePageList() {
        this.container.find(".paginator-btn.paginator-index").remove();
        for (let i = 0; i < this.numPages; i++) {
            let newButton = $(`<button class="paginator-btn paginator-index">${i}</button>`);
            newButton.on("click", function(index) {
                this.setIndex(index);
            }.bind(this, i));
            this.incrementButton.before(newButton);
        }
        this.incrementButton.after();
    }
}
