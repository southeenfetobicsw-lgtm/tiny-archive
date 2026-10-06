package com.tinyarchive.app.data.sample

import com.tinyarchive.app.domain.model.ArchiveItem
import com.tinyarchive.app.domain.model.Category

object SampleData {

    const val CATEGORY_BOOKS = "cat_books"
    const val CATEGORY_VINYL = "cat_vinyl"
    const val CATEGORY_COINS = "cat_coins"
    const val CATEGORY_POSTCARDS = "cat_postcards"
    const val CATEGORY_BOTANICALS = "cat_botanicals"

    private const val BASE_TIME = 1759000000000L
    private const val STEP = 3600000L

    val categories: List<Category> = listOf(
        Category(CATEGORY_BOOKS, "BOOKS", "#5B5F4A", "book", true),
        Category(CATEGORY_VINYL, "VINYL", "#262626", "vinyl", true),
        Category(CATEGORY_COINS, "COINS", "#C58B57", "coin", true),
        Category(CATEGORY_POSTCARDS, "POSTCARDS", "#BFC0A8", "postcard", true),
        Category(CATEGORY_BOTANICALS, "BOTANICALS", "#7FA46B", "botanical", true),
    )

    val items: List<ArchiveItem> = listOf(
        row(1, "Moby-Dick, Penguin Classics", CATEGORY_BOOKS, "1972", "SHELF B", "Spine sun-faded, pencil note on page 212"),
        row(2, "The Sea, The Sea", CATEGORY_BOOKS, "1978", "SHELF B", "First UK printing, dust jacket intact"),
        row(3, "Blue Train", CATEGORY_VINYL, "1957", "CRATE 1", "Blue Note reissue, light surface noise on side A"),
        row(4, "Pastel Blues", CATEGORY_VINYL, "1965", "CRATE 1", "Bought at the Sunday market, sleeve repaired"),
        row(5, "1943 Steel Cent", CATEGORY_COINS, "1943", "TIN A", "Magnetic, mild pitting on the reverse"),
        row(6, "Silver Florin", CATEGORY_COINS, "1936", "TIN A", "Kept in the felt pouch with the brass key"),
        row(7, "Lisbon Tram 28", CATEGORY_POSTCARDS, "1988", "FOLDER C", "Never posted, one corner crease"),
        row(8, "Night Ferry, Dover", CATEGORY_POSTCARDS, "1964", "FOLDER C", "Written in green ink, no address"),
        row(9, "Pressed Fern", CATEGORY_BOTANICALS, "2019", "FOLDER D", "Collected on the north ridge, pressed four weeks"),
        row(10, "Dried Cornflower", CATEGORY_BOTANICALS, "2021", "FOLDER D", "Colour holding well in the dark drawer"),
        row(11, "Field Guide to Lichens", CATEGORY_BOOKS, "1991", "SHELF A", "Annotated in the margins by a previous owner"),
        row(12, "Brass Drawer Key", CATEGORY_COINS, "NO REF", "TIN A", "Opens the lower catalog drawer"),
    )

    private fun row(
        index: Int,
        title: String,
        categoryId: String,
        reference: String,
        location: String,
        note: String,
    ): ArchiveItem {
        val stamp = BASE_TIME + index * STEP
        return ArchiveItem(
            id = "seed_" + index.toString(),
            title = title,
            categoryId = categoryId,
            reference = reference,
            location = location,
            note = note,
            createdAt = stamp,
            updatedAt = stamp,
        )
    }
}
