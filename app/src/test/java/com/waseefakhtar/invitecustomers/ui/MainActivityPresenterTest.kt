package com.waseefakhtar.invitecustomers.ui

import android.net.Uri
import com.nhaarman.mockito_kotlin.mock
import com.waseefakhtar.invitecustomers.data.Customer
import com.waseefakhtar.invitecustomers.util.DistanceUtil
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.concurrent.ThreadLocalRandom

private const val DISTANCE_CAP = 100.0

class MainActivityPresenterTest {

    private var view: MainInterface.View = mock()
    private lateinit var presenter: MainActivityPresenter

    private val allCustomerList = createCustomerList(10)
    private val fileUri: Uri = mock()

    @Nested
    inner class OnPostExecute {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)

            Mockito.reset(view)
        }

        @Test
        fun `Should update view when the list is not empty`() {
            presenter.onPostExecute(allCustomerList)

            Verify on view that view.updateViewWith(allCustomerList)
            `Verify no further interactions` on view
        }

        @Test
        fun `Should not update view when the list is empty`() {
            presenter.onPostExecute(null)

            `Verify no further interactions` on view
        }
    }

    @Nested
    inner class UpdateListWithAll {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)
            presenter.onPostExecute(allCustomerList)

            Mockito.reset(view)
        }

        @Test
        fun `Should update view with all customers`() {
            presenter.updateListWithAll()

            Verify on view that view.updateViewWith(allCustomerList) was called
            `Verify no further interactions` on view
        }
    }

    @Nested
    inner class UpdateListWithInvited {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)
            presenter.onPostExecute(allCustomerList)

            Mockito.reset(view)
        }

        @Test
        fun `Should update view invited customers`() {
            val customerList = getInvitedCustomerList(allCustomerList)

            presenter.updateListWithInvited()

            Verify on view that view.updateViewWith(customerList) was called
            `Verify no further interactions` on view
        }
    }

    @Nested
    inner class GenerateInvitedCustomerMap {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)
            presenter.onPostExecute(allCustomerList)

            Mockito.reset(view)
        }

        @Test
        fun `Should save file to disk with generated invited customer map`() {
            val invitedCustomerList = getInvitedCustomerList(allCustomerList)
            val invitedCustomerMap = getInvitedCustomerMap(invitedCustomerList)

            presenter.generateInvitedCustomerMap()

            Verify on view that view.saveFileToDisk(invitedCustomerMap) was called
            `Verify no further interactions` on view
        }
    }

    @Nested
    inner class UpdateFileUri {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)
            presenter.onPostExecute(allCustomerList)

            Mockito.reset(view)
        }

        @Test
        fun `Should invalidate menu items with updated file uri`() {
            presenter.updateFileUri(fileUri)

            Verify on view that view.invalidateMenuItems() was called
            `Verify no further interactions` on view
        }
    }

    @Nested
    inner class GenerateFileUri {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)
            presenter.onPostExecute(allCustomerList)
            presenter.updateFileUri(fileUri)

            Mockito.reset(view)
        }

        @Test
        fun `Should open file in folder when uri is not null`() {
            presenter.generateFileUri()

            Verify on view that view.openFileInFolder(fileUri) was called
            `Verify no further interactions` on view
        }


    }

    @Nested
    inner class GenerateNullFileUri {

        @BeforeEach
        fun setup() {
            presenter = MainActivityPresenter(view)
            presenter.onPostExecute(allCustomerList)

            Mockito.reset(view)
        }

        @Test
        fun `Should not open file in folder when uri is null`() {
            presenter.generateFileUri()

            `Verify no further interactions` on view
        }
    }

    private fun getInvitedCustomerList(customerList: ArrayList<Customer>): ArrayList<Customer> {
        val invitedCustomerList = arrayListOf<Customer>()
        for (customer in customerList) {
            val distance = DistanceUtil.getDistance(
                customer.longitude,
                customer.latitude
            )

            if (distance <= DISTANCE_CAP) {
                invitedCustomerList.add(customer)
            }
        }

        return invitedCustomerList
    }

    private fun getInvitedCustomerMap(customerList: ArrayList<Customer>): MutableMap<Int, String> {
        val invitedCustomers = mutableMapOf<Int, String>()

        for (customer in customerList) {
            invitedCustomers[customer.userId] = customer.name
        }

        return invitedCustomers
    }
}


private fun createCustomer(
    userId: Int = positiveRandomInt(),
    name: String = randomString(),
    latitude: String = randomDouble().toString(),
    longitude: String = randomDouble().toString()): Customer
    = Customer(
    userId,
    name,
    latitude,
    longitude)

private fun createCustomerList(size: Int): ArrayList<Customer> =
    ArrayList((1..size).map { createCustomer() })

fun positiveRandomInt(maxInt: Int = Int.MAX_VALUE-1): Int = random.nextInt(maxInt+1).takeIf { it > 0 } ?: positiveRandomInt(maxInt)
fun randomDouble() = random.nextDouble()
fun randomString(size: Int = 20): String = (0..size)
    .map { charPool[random.nextInt(0, charPool.size)] }
    .joinToString()

val random
    get() = ThreadLocalRandom.current()

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')


