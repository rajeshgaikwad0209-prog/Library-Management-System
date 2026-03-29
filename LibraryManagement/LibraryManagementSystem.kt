class Book(bookId: String, bookTitle: String, bookAuthor: String, bookGenre: String, totalQty: Int) {
    var id: String = bookId
    var title: String = bookTitle
    var author: String = bookAuthor
    var genre: String = bookGenre
    var quantity: Int = totalQty
    var available: Int = totalQty
    var totalBorrowCount: Int = 0

    fun displayInfo() {
        println("ID: $id | Title: $title | Author: $author | Genre: $genre | Available: $available/$quantity | Times Borrowed: $totalBorrowCount")
    }
}

class LibraryMember(memId: String, memName: String, memEmail: String) {
    var memberId: String = memId
    var name: String = memName
    var email: String = memEmail
    var borrowedCount: Int = 0
    var totalFinesPaid: Int = 0

    fun displayInfo() {
        println("ID: $memberId | Name: $name | Email: $email | Books Held: $borrowedCount | Total Fines Paid: Rs.$totalFinesPaid")
    }
}

class LoanRecord(bId: String, mId: String, day: Int) {
    var bookId: String = bId
    var memberId: String = mId
    var dayBorrowed: Int = day
    var isReturned: Boolean = false
    var dayReturned: Int = -1
    var fine: Int = 0

    fun displayInfo() {
        val status = if (isReturned) "Returned on Day $dayReturned" else "Active"
        val fineInfo = if (fine > 0) " | Fine: Rs.$fine" else ""
        println("Book: $bookId | Member: $memberId | Borrowed: Day $dayBorrowed | Status: $status$fineInfo")
    }
}

class FineTracker {
    val DAILY_FINE_RATE: Int = 10
    val LOAN_PERIOD_DAYS: Int = 7

    fun calculateFine(dayBorrowed: Int, dayReturned: Int): Int {
        val daysKept = dayReturned - dayBorrowed
        return if (daysKept > LOAN_PERIOD_DAYS) (daysKept - LOAN_PERIOD_DAYS) * DAILY_FINE_RATE else 0
    }

    fun daysRemaining(dayBorrowed: Int, currentDay: Int): Int {
        return LOAN_PERIOD_DAYS - (currentDay - dayBorrowed)
    }
}

class LibraryManagement {
    var books = arrayOfNulls<Book>(15)
    var members = arrayOfNulls<LibraryMember>(15)
    var loans = arrayOfNulls<LoanRecord>(30)
    var currentDay: Int = 1
    val fineTracker = FineTracker()

    fun advanceDay() {
        currentDay++
        println("\n>>> Day advanced. It is now Day $currentDay <<<")
        checkOverdueLoans()
    }

    fun checkOverdueLoans() {
        var overdueCount = 0
        for (i in 0 until 30) {
            val l = loans[i]
            if (l != null && !l.isReturned) {
                val daysKept = currentDay - l.dayBorrowed
                if (daysKept > fineTracker.LOAN_PERIOD_DAYS) {
                    val projected = fineTracker.calculateFine(l.dayBorrowed, currentDay)
                    println("  [OVERDUE] Book ${l.bookId} held by Member ${l.memberId} — ${daysKept - fineTracker.LOAN_PERIOD_DAYS} day(s) overdue! Fine so far: Rs.$projected")
                    overdueCount++
                }
            }
        }
        if (overdueCount == 0) println("  No overdue loans today.")
    }

    fun addBook(id: String, title: String, author: String, genre: String, quantity: Int) {
        for (i in 0 until 15) {
            if (books[i] != null && books[i]?.id == id) {
                books[i]?.quantity = (books[i]?.quantity ?: 0) + quantity
                books[i]?.available = (books[i]?.available ?: 0) + quantity
                println("Library: Added $quantity more copies of '$title'. Total now: ${books[i]?.quantity}")
                return
            }
        }
        for (i in 0 until 15) {
            if (books[i] == null) {
                books[i] = Book(id, title, author, genre, quantity)
                println("Library: New book added — '$title' ($quantity copies, Genre: $genre)")
                return
            }
        }
        println("Library: Shelf is full! Cannot add more distinct books.")
    }

    fun registerMember(id: String, name: String, email: String) {
        for (i in 0 until 15) {
            if (members[i] != null && members[i]?.memberId == id) {
                println("Library: Member ID '$id' already exists.")
                return
            }
        }
        for (i in 0 until 15) {
            if (members[i] == null) {
                members[i] = LibraryMember(id, name, email)
                println("Library: Registered member '$name' (Email: $email)")
                return
            }
        }
        println("Library: Member list is full.")
    }

    fun displayBooks() {
        println("\n--- Book Catalogue ---")
        var count = 0
        for (i in 0 until 15) {
            val b = books[i]
            if (b != null) {
                b.displayInfo()
                count++
            }
        }
        if (count == 0) println("No books in the library.")
        else println("Total distinct titles: $count")
    }

    fun displayMembers() {
        println("\n--- Registered Members ---")
        var count = 0
        for (i in 0 until 15) {
            val m = members[i]
            if (m != null) {
                m.displayInfo()
                count++
            }
        }
        if (count == 0) println("No members registered.")
        else println("Total members: $count")
    }

    fun displayLoanHistory() {
        println("\n--- Loan History ---")
        var count = 0
        for (i in 0 until 30) {
            val l = loans[i]
            if (l != null) {
                l.displayInfo()
                count++
            }
        }
        if (count == 0) println("No loan records found.")
        else println("Total loan records: $count")
    }

    fun searchBook(keyword: String) {
        println("\n--- Search Results for '$keyword' ---")
        var count = 0
        for (i in 0 until 15) {
            val b = books[i]
            if (b != null && (b.title.contains(keyword, ignoreCase = true) || b.author.contains(keyword, ignoreCase = true) || b.genre.contains(keyword, ignoreCase = true))) {
                b.displayInfo()
                count++
            }
        }
        if (count == 0) println("No matching books found.")
    }

    fun deleteBook(bookId: String) {
        for (i in 0 until 15) {
            val b = books[i]
            if (b != null && b.id == bookId) {
                if (b.available < b.quantity) {
                    println("Error: Cannot delete '${b.title}' — ${b.quantity - b.available} copy/copies still on loan.")
                    return
                }
                println("Library: Deleted book '${b.title}' by ${b.author}.")
                books[i] = null
                return
            }
        }
        println("Library: Book ID '$bookId' not found.")
    }

    fun deleteMember(memberId: String) {
        for (i in 0 until 15) {
            val m = members[i]
            if (m != null && m.memberId == memberId) {
                if (m.borrowedCount > 0) {
                    println("Error: Cannot delete '${m.name}' — they still have ${m.borrowedCount} book(s) to return.")
                    return
                }
                println("Library: Member '${m.name}' has been removed.")
                members[i] = null
                return
            }
        }
        println("Library: Member ID '$memberId' not found.")
    }

    fun borrowBook(bookId: String, memberId: String) {
        var foundBook: Book? = null
        var foundMember: LibraryMember? = null

        for (i in 0 until 15) {
            if (books[i]?.id == bookId) foundBook = books[i]
            if (members[i]?.memberId == memberId) foundMember = members[i]
        }

        if (foundBook == null || foundMember == null) {
            println("Library: Invalid Book ID or Member ID.")
            return
        }

        if (foundMember.borrowedCount >= 3) {
            println("Error: '${foundMember.name}' has reached the borrow limit (3 books max).")
            return
        }

        if (foundBook.available <= 0) {
            println("Library: All copies of '${foundBook.title}' are currently on loan.")
            return
        }

        for (i in 0 until 30) {
            if (loans[i] == null) {
                loans[i] = LoanRecord(bookId, memberId, currentDay)
                foundBook.available--
                foundBook.totalBorrowCount++
                foundMember.borrowedCount++
                println("Library: '${foundBook.title}' borrowed by ${foundMember.name} on Day $currentDay.")
                println("         Please return by Day ${currentDay + fineTracker.LOAN_PERIOD_DAYS} to avoid a fine.")
                return
            }
        }
        println("Library: Loan records are full.")
    }

    fun returnBook(bookId: String, memberId: String) {
        var foundLoan: LoanRecord? = null

        for (i in 0 until 30) {
            val l = loans[i]
            if (l != null && l.bookId == bookId && l.memberId == memberId && !l.isReturned) {
                foundLoan = l
                break
            }
        }

        if (foundLoan == null) {
            println("Library: No active loan found for Book '$bookId' and Member '$memberId'.")
            return
        }

        foundLoan.isReturned = true
        foundLoan.dayReturned = currentDay

        val fine = fineTracker.calculateFine(foundLoan.dayBorrowed, currentDay)
        foundLoan.fine = fine

        if (fine > 0) {
            println("Warning: This book is overdue! Fine due: Rs.$fine")
        } else {
            val remaining = fineTracker.daysRemaining(foundLoan.dayBorrowed, currentDay)
            println("Returned on time. You had $remaining day(s) remaining.")
        }

        for (i in 0 until 15) {
            if (books[i]?.id == bookId) {
                books[i]?.available = (books[i]?.available ?: 0) + 1
            }
            if (members[i]?.memberId == memberId) {
                members[i]?.borrowedCount = (members[i]?.borrowedCount ?: 1) - 1
                if (fine > 0) members[i]?.totalFinesPaid = (members[i]?.totalFinesPaid ?: 0) + fine
            }
        }
        println("Library: Book returned successfully on Day $currentDay.")
    }
}

class LibraryApp {
    val library = LibraryManagement()

    fun run() {
        var running = true
        println("╔══════════════════════════════════════╗")
        println("║   Advanced Library Management System  ║")
        println("╚══════════════════════════════════════╝")

        while (running) {
            println("\n--- Menu (Today: Day ${library.currentDay}) ---")
            println(" 1. Display Books          2. Display Members")
            println(" 3. Add Book               4. Add Member")
            println(" 5. Delete Book            6. Delete Member")
            println(" 7. Borrow Book            8. Return Book")
            println(" 9. Loan History          10. Search Book")
            println("11. Advance Day           12. Exit")
            print("Enter choice: ")

            when (readln().trim()) {
                "1" -> library.displayBooks()
                "2" -> library.displayMembers()
                "3" -> {
                    print("Book ID: "); val id = readln()
                    print("Title: "); val title = readln()
                    print("Author: "); val author = readln()
                    print("Genre: "); val genre = readln()
                    print("Quantity: "); val qty = readln().toIntOrNull() ?: 0
                    library.addBook(id, title, author, genre, qty)
                }
                "4" -> {
                    print("Member ID: "); val id = readln()
                    print("Name: "); val name = readln()
                    print("Email: "); val email = readln()
                    library.registerMember(id, name, email)
                }
                "5" -> {
                    library.displayBooks()
                    print("Book ID to delete: ")
                    library.deleteBook(readln())
                }
                "6" -> {
                    library.displayMembers()
                    print("Member ID to delete: ")
                    library.deleteMember(readln())
                }
                "7" -> {
                    library.displayBooks(); library.displayMembers()
                    print("Book ID: "); val bId = readln()
                    print("Member ID: "); val mId = readln()
                    library.borrowBook(bId, mId)
                }
                "8" -> {
                    print("Book ID: "); val bId = readln()
                    print("Member ID: "); val mId = readln()
                    library.returnBook(bId, mId)
                }
                "9"  -> library.displayLoanHistory()
                "10" -> {
                    print("Search (title/author/genre): ")
                    library.searchBook(readln())
                }
                "11" -> library.advanceDay()
                "12" -> { running = false; println("Goodbye! 📚") }
                else -> println("Invalid choice. Please enter 1–12.")
            }
        }
    }
}

fun main() {
    LibraryApp().run()
}